package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BeaconCustomerMappingRepository;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.IouRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;

@Service
public class CustomerService {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerService.class);

	private static final String CUSTOMER_NAME_QUERY_PREFIX = "select CMT.customer_name from customer_master_t CMT "
			+ "JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou";

	private static final String TOP_REVENUE_PROJECTED_PREFIX = "select CMT.* from customer_master_t CMT, (";
	private static final String TOP_REVENUE_PROJECTED_SUFFIX = ") as TRC where CMT.customer_name = TRC.customer_name and CMT.active =TRUE order by TRC.revenue desc";

	private static final String CUSTOMER_IOU_COND_SUFFIX = "ICMT.display_iou in (";
	private static final String CUSTOMER_GEO_COND_SUFFIX = "CMT.geography in (";
	private static final String CUSTOMER_NAME_CUSTOMER_COND_SUFFIX = "CMT.customer_name in (";
	private static final String GROUP_CUSTOMER_NAME_QUERY_PREFIX = "select distinct CMT.group_customer_name from customer_master_t CMT "
			+ "JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou";

	private static final String GROUP_CUSTOMER_NAME_CUSTOMER_COND_SUFFIX = "CMT.group_customer_name like ";

	private static final String ORDERBY_SUFFIX = " order by CMT.group_customer_name";

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	CustomerIOUMappingRepository customerIouMappingTRepository;

	@Autowired
	OpportunityService opportunityService;

	@Autowired
	RevenueCustomerMappingTRepository revenueRepository;

	@Autowired
	BeaconCustomerMappingRepository beaconCustomerMappingRepository;

	@Autowired
	CustomerUploadService customerUploadService;

	@Autowired
	ContactService contactService;

	@Autowired
	BeaconRepository beaconRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UserService userService;

	@Autowired
	ReportsService reportsService;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	@Autowired
	PerformanceReportService performanceReportService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	private IouRepository iouRepository;
	
	@Autowired
	private GeographyRepository geoRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouCustomerMappingT = null;
	Map<String, IouBeaconMappingT> mapOfIouBeaconMappingT = null;

	/**
	 * This method is used to fetch customer details using customer id
	 * @param customerId
	 * @param toCurrency
	 * @return
	 * @throws Exception
	 */
	public CustomerMasterT findById(String customerId, List<String> toCurrency)
			throws Exception {
		logger.debug("Inside findById() service");
		CustomerMasterT customerMasterT = customerRepository
				.findactivecust(customerId);
		if (customerMasterT == null) {
			logger.error("NOT_FOUND: Customer not found: {}", customerId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found: " + customerId);
		}
		prepareCustomerDetails(customerMasterT, null);
		beaconConverterService.convertOpportunityCurrency(
				customerMasterT.getOpportunityTs(), toCurrency);
		return customerMasterT;
	}
	
	/**
	 * This method is used to get count of opportunities for a customerID,to avoid the duplicate opportunities
	 * 
	 * @param customerId
	 * @return
	 * @throws Exception
	 */
	public int getOpportunityCountByCustomerId(String customerId)
			throws Exception {
		logger.debug("Inside getOpportunityCountByCustomerId() service");
		boolean isValid=false;
		int opportunityCount=0;
		ArrayList<String> customerNameList=new ArrayList<String>();
		//Validating privilege for user and fetching opportunity count accordingly
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		CustomerMasterT customer= customerRepository.findOne(customerId);
		if(customer==null)
		{
			logger.info("BAD_REQUEST,Invalid Customer Id");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Invalid Customer Id");
		}
		else
        {
        customerNameList.add(customer.getCustomerName());
		customerNameList = getPreviledgedCustomerName(userId, customerNameList,
				true);
		for(String customerName:customerNameList)
		{
			if(customerName.equalsIgnoreCase(customer.getCustomerName()))
			{
				 opportunityCount = opportunityRepository.getOpportunityCountByCustomerId(customer.getCustomerId());
				 isValid=true;
			}
		}
		
        }
       
		if(isValid==false)
		{
			logger.info("User doesn't have privilege to this customer");
		    throw new DestinationException(HttpStatus.FORBIDDEN,"User doesn't have privilege to this customer");
		}

		return opportunityCount;
	}

	/**
	 * This method deletes the Customer from the database
	 * 
	 * @param customerT
	 * @throws Exception
	 */
	@Transactional
	public void removeCustomer(CustomerMasterT customerT) throws Exception {

		if (customerT != null) {
			logger.info("inside remove customer" + customerT.getCustomerId()
					+ " ");
			customerRepository.delete(customerT);
			logger.info("customer deleted for customerid:"
					+ customerT.getCustomerId());
		}
	}
    
	/**
	 * This method is used to save the customer details 
	 * @param insertList
	 */
    public void save(List<CustomerMasterT> insertList) {

		logger.debug("Inside save method of customer service");
		customerRepository.save(insertList);

	}

	/**
	 * This method is used to delete the customer details
	 * @param deleteList
	 */
    public void delete(List<CustomerMasterT> deleteList) {

		logger.debug("Inside save method of customer service");
		customerRepository.delete(deleteList);

	}

	/**
	 * This service is used to find Top revenue customers based on user's access
	 * privileges.
	 * 
	 * @param userId
	 *            , year, count.
	 * @return Top revenue customers.
	 */
	public List<CustomerMasterT> findTopRevenue(String financialYear, int count)
			throws Exception {
		logger.debug("Inside findTopRevenue() service");
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		UserT user = userRepository.findOne(userId);

		String userGroup = user.getUserGroupMappingT().getUserGroup();
		if (UserGroup.contains(userGroup)) {
			// Validate user group, BDM's & BDM supervisor's are not
			// authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
			case PRACTICE_OWNER:	
				logger.error("User is not authorized to access this service");
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User is not authorised to access this service");
			default:
				// Validate financial year and set default value
				if (financialYear.isEmpty()) {
					logger.debug("Financial year is empty");
					financialYear = DateUtils.getCurrentFinancialYear();
				}
				List<CustomerMasterT> resultCustomerList = getTopRevenuesBasedOnUserPrivileges(
						user.getUserId(), financialYear, count);
				return resultCustomerList;
			}
		} else {
			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid User Group");
		}
	}

	/**
	 * This method forms and executes the query to find Top revenue customers
	 * based on user access privileges
	 * 
	 * @param userId
	 * @param financialYear
	 * @param count
	 * @return Top revenue customers
	 * @throws Exception
	 */
	private List<CustomerMasterT> getTopRevenuesBasedOnUserPrivileges(
			String userId, String financialYear, int count) throws Exception {
		logger.debug("Inside getTopRevenuesBasedOnUserPrivileges() method");
		List<String> months = DateUtils.getMonthsFromYear(financialYear);

		// Form the native top revenue query string
		String queryString = getRevenueQueryString(userId, count, financialYear);
		logger.info("Query string: {}", queryString);

		// Execute the native revenue query string
		Query topRevenueQuery = entityManager.createNativeQuery(queryString, CustomerMasterT.class);
		topRevenueQuery.setParameter("months",months);
		List<CustomerMasterT> resultList = (ArrayList<CustomerMasterT>)topRevenueQuery.getResultList();
		if (resultList == null || resultList.isEmpty()) {
			logger.error("NOT_FOUND: Top revenue customers not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Top revenue customers not found");
		}
		removeCyclicForLinkedContactTs(resultList);
		return resultList;
	}

	/**
	 * This method returns the query string with user access privilege
	 * restrictions added
	 * 
	 * @param count
	 * @param financialYear
	 * @param privileges
	 * @return
	 * @throws DestinationException
	 */
	private String getRevenueQueryString(String userId, int count,
			String financialYear) throws Exception {
		logger.debug("Inside getRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TOP_REVENUE_PROJECTED_PREFIX);
		queryBuffer.append(reportsService.getTopRevenueCustomersForDashboard(userId, count));
		queryBuffer.append(TOP_REVENUE_PROJECTED_SUFFIX);
		return queryBuffer.toString();
	}

	public List<TargetVsActualResponse> findTargetVsActual(
			String financialYear, String quarter, String customerName,
			String currency) throws Exception {
		logger.debug("Inside findTargetVsActual() service");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		ArrayList<String> customerNameList = new ArrayList<String>();
		customerNameList.add(customerName);
		customerNameList = getPreviledgedCustomerName(userId, customerNameList,
				true);

		if (customerNameList == null || customerNameList.isEmpty())
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User does not have access to view this information");

		return performanceReportService.getTargetVsActualRevenueSummary(
				financialYear, quarter, "", "", "", "", customerName, currency,
				"", false, userId, false);
	}

	/**
	 * This method is used to fetch the customer details based upon customer name
	 * @param nameWith
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findByNameContaining(String nameWith, int page,
			int count) throws Exception {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		logger.debug("Inside findByNameContaining() service");
		Page<CustomerMasterT> customerPage = customerRepository
				.findByCustomerNameIgnoreCaseContainingAndCustomerNameIgnoreCaseNotLikeAndActiveOrderByCustomerNameAsc(
						nameWith, Constants.UNKNOWN_CUSTOMER,true, pageable);
		paginatedResponse.setTotalCount(customerPage.getTotalElements());
		List<CustomerMasterT> custList = customerPage.getContent();
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: Customer not found with given name: {}",
					nameWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given name: " + nameWith);
		}
		prepareCustomerDetails(custList);
		paginatedResponse.setCustomerMasterTs(custList);
		return paginatedResponse;
	}

	/**
	 * This method is used to fetch customer details based on group customer name
	 * @param groupCustName
	 * @return
	 * @throws Exception
	 */
	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName)
			throws Exception {
		logger.debug("Inside findByGroupCustomerName() service");
		List<CustomerMasterT> custList = customerRepository
				.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeAndActiveOrderByGroupCustomerNameAsc(
						groupCustName, Constants.UNKNOWN_CUSTOMER,true);
		if (custList.isEmpty()) {
			logger.error(
					"NOT_FOUND: Customer not found with given group customer name: {}",
					groupCustName);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given group customer name: "
							+ groupCustName);
		}
		prepareCustomerDetails(custList);
		return custList;
	}

	/**
	 * This method is used to fetch the customer details whose name starts with using "startsWith" parameter
	 * @param startsWith
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findByNameStarting(String startsWith, int page,
			int count) throws Exception {
		
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		logger.debug("Starts With" + startsWith);
		List<CustomerMasterT> custList = new ArrayList<CustomerMasterT>();
		if (!startsWith.equals("@")) {
			Page<CustomerMasterT> customerPage = customerRepository
					.findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeAndActiveOrderByCustomerNameAsc(
							startsWith, Constants.UNKNOWN_CUSTOMER,true, pageable);
			custList.addAll(customerPage.getContent());
			paginatedResponse.setTotalCount(paginatedResponse.getTotalCount()
					+ customerPage.getTotalElements());
		} else
			for (int i = 0; i <= 9; i++) {
				Page<CustomerMasterT> customerPage = customerRepository
						.findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeAndActiveOrderByCustomerNameAsc(
								i + "", Constants.UNKNOWN_CUSTOMER,true, pageable);
				custList.addAll(customerPage.getContent());
				paginatedResponse.setTotalCount(paginatedResponse
						.getTotalCount() + customerPage.getTotalElements());
			}

		if (custList.isEmpty()) {
			logger.error(
					"NOT_FOUND: Customer not found with given customer name: {}",
					startsWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given customer name: "
							+ startsWith);
		}
		prepareCustomerDetails(custList);
		paginatedResponse.setCustomerMasterTs(custList);
		return paginatedResponse;
	}

	private void removeCyclicForLinkedContactTs(
			List<CustomerMasterT> customerMasterTs) {
		if (customerMasterTs != null) {
			for (CustomerMasterT customerMasterT : customerMasterTs) {
				removeCyclicForLinkedContactTs(customerMasterT);
			}
		}
	}

	private void removeCyclicForLinkedContactTs(CustomerMasterT customerMasterT) {
		if (customerMasterT != null) {
			if (customerMasterT.getContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : customerMasterT
						.getContactCustomerLinkTs()) {
					contactCustomerLinkT.getContactT()
					.setContactCustomerLinkTs(null);
				}
			}
		}
	}

	/**
	 * This method returns the query string with user access privilege
	 * restrictions added
	 * 
	 * @param count
	 * @param financialYear
	 * @param privileges
	 * @return
	 * @throws DestinationException
	 */
	private String getCustomerPrevilegeQueryString(String userId,
			List<String> customerNameList, boolean considerGeoIou)
					throws Exception {
		logger.debug("Inside getRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(CUSTOMER_NAME_QUERY_PREFIX);

		HashMap<String, String> queryPrefixMap;

		if (considerGeoIou) {
			queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
					CUSTOMER_GEO_COND_SUFFIX, null, CUSTOMER_IOU_COND_SUFFIX,
					CUSTOMER_NAME_CUSTOMER_COND_SUFFIX);
		} else {
			queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
					null, null, null, CUSTOMER_NAME_CUSTOMER_COND_SUFFIX);
		}

		// Get WHERE clause string
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);

		if ((whereClause != null && !whereClause.isEmpty())
				|| (customerNameList != null && customerNameList.size() > 0)) {
			queryBuffer.append(" where ");
		}

		if (customerNameList != null && customerNameList.size() > 0) {
			String customerNameQueryList = "(";
			{
				for (String customerName : customerNameList)
					customerNameQueryList += "'"
							+ customerName.replace("\'", "\'\'") + "',";
			}
			customerNameQueryList = customerNameQueryList.substring(0,
					customerNameQueryList.length() - 1);
			customerNameQueryList += ")";

			queryBuffer
			.append(" CMT.customer_name in " + customerNameQueryList);
		}

		if ((whereClause != null && !whereClause.isEmpty())
				&& (customerNameList != null && customerNameList.size() > 0)) {
			queryBuffer.append(Constants.AND_CLAUSE);
		}

		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(whereClause);
		}

		logger.info("queryString = " + queryBuffer.toString());
		return queryBuffer.toString();
	}

	/**
	 * This method is used to fetch the customer name based upon priviledge
	 * @param userId
	 * @param customerNameList
	 * @param considerGeoIou
	 * @return
	 * @throws Exception
	 */
	public ArrayList<String> getPreviledgedCustomerName(String userId,
			ArrayList<String> customerNameList, boolean considerGeoIou)
					throws Exception {
		logger.debug("Inside getPreviledgedCustomerName() method");
		String queryString = getCustomerPrevilegeQueryString(userId,
				customerNameList, considerGeoIou);
		logger.info("Query string: {}", queryString);
		Query opportunityQuery = entityManager.createNativeQuery(queryString);
		return (ArrayList<String>) opportunityQuery.getResultList();
	}

	private void prepareCustomerDetails(List<CustomerMasterT> customerMasterList)
			throws Exception {
		logger.debug("Inside prepareCustomerDetails() method");

		if (customerMasterList != null && !customerMasterList.isEmpty()) {
			ArrayList<String> customerNameList = new ArrayList<String>();
			for (CustomerMasterT customerMasterT : customerMasterList) {
				customerNameList.add(customerMasterT.getCustomerName());
			}
			customerNameList = getPreviledgedCustomerName(DestinationUtils
					.getCurrentUserDetails().getUserId(), customerNameList,
					true);

			for (CustomerMasterT customerMasterT : customerMasterList) {
				prepareCustomerDetails(customerMasterT, customerNameList);
			}
		}

	}

	private void prepareCustomerDetails(CustomerMasterT customerMasterT,
			ArrayList<String> customerNameList) throws DestinationException {
		logger.debug("Inside prepareCustomerDetails() method");

		removeCyclicForLinkedContactTs(customerMasterT);
		try {
			if (customerNameList == null) {
				customerNameList = new ArrayList<String>();
				customerNameList.add(customerMasterT.getCustomerName());
				customerNameList = getPreviledgedCustomerName(DestinationUtils
						.getCurrentUserDetails().getUserId(), customerNameList,
						true);
			}
			if (customerNameList == null
					|| customerNameList.isEmpty()
					|| (!customerNameList.contains(customerMasterT
							.getCustomerName()))) {
				hideSensitiveInfo(customerMasterT);
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

	}
    
	/**
     * This method is used to hide the sensitive information of customer contact
     * @param customerMasterT
     */
	private void hideSensitiveInfo(CustomerMasterT customerMasterT) {
		logger.debug("Inside hideSensitiveInfo() method");

		opportunityService.preventSensitiveInfo(customerMasterT
				.getOpportunityTs());
		for (ContactCustomerLinkT contactCustomerLinkT : customerMasterT
				.getContactCustomerLinkTs())
			contactService.preventSensitiveInfo(contactCustomerLinkT
					.getContactT());

	}

	/**
	 * This method is used to find based upon group customer name based on privilege 
	 * @param nameWith
	 *            - string to be searched
	 * @param userId
	 *            - userId for which the privilege restrictions are to be
	 *            applied
	 * @return - List of distinct group customer names based on privileges
	 * @throws Exception
	 */
	public List<String> findByGroupCustomerNameBasedOnPrivilege(String nameWith)
			throws Exception {
		String queryString = null;
		List<String> resultList = null;
		queryString = getGroupCustomerPrivilegeQueryString(DestinationUtils
				.getCurrentUserDetails().getUserId(), "'%" + nameWith + "%'");
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query groupCustomerPrivilegeQuery = entityManager
				.createNativeQuery(queryString);

		resultList = (ArrayList<String>) groupCustomerPrivilegeQuery.getResultList();

		return resultList;
	}
	
	/**
	 * This method is used to fetch the group customer name with access priviledge restrictions applied
	 * @param userId
	 * @param nameWith
	 * @return
	 * @throws Exception
	 */

	private String getGroupCustomerPrivilegeQueryString(String userId,
			String nameWith) throws Exception {
		logger.debug("Inside getGroupCustomerPrivilegeQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				GROUP_CUSTOMER_NAME_QUERY_PREFIX);

		HashMap<String, String> queryPrefixMap;

		queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
				CUSTOMER_GEO_COND_SUFFIX, null, CUSTOMER_IOU_COND_SUFFIX,
				CUSTOMER_NAME_CUSTOMER_COND_SUFFIX);

		// Get WHERE clause string
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);

		if ((whereClause != null && !whereClause.isEmpty())
				|| (nameWith != null && !nameWith.isEmpty())) {
			queryBuffer.append(" and "
					+ GROUP_CUSTOMER_NAME_CUSTOMER_COND_SUFFIX + nameWith
					+ " and " + whereClause + ORDERBY_SUFFIX);
		}

		logger.info("queryString = " + queryBuffer.toString());
		return queryBuffer.toString();
	}

	/**
	 * This method inserts customer to the database
	 * 
	 * @param customerToInsert
	 * @return CustomerMasterT
	 * @throws Exception
	 */
	@Transactional
	public CustomerMasterT addCustomer(CustomerMasterT customerToInsert)
			throws Exception {
		CustomerMasterT customerT = null;
		logger.info("inside addCustomer");
		if (customerToInsert != null) {
			customerT = new CustomerMasterT();
			logger.info("customer not null");
			// primary key check for customerid
			logger.info("where critrtia:grp_cus_name: "
					+ customerToInsert.getGroupCustomerName() + "cusname: "
					+ customerToInsert.getCustomerName() + "iou: "
					+ customerToInsert.getIou() + "geo: "
					+ customerToInsert.getGeography());
			String customerId = customerRepository
					.findCustomerIdForDeleteOrUpdate(
							customerToInsert.getGroupCustomerName(),
							customerToInsert.getCustomerName(),
							customerToInsert.getIou(),
							customerToInsert.getGeography());
			logger.info("customer id for add from repo " + customerId);
			if (customerId == null) {
				logger.info("customer id is not empty");
				customerT.setCustomerName(customerToInsert.getCustomerName());

				customerT.setDocumentsAttached("NO");
				customerT.setCorporateHqAddress(customerToInsert
						.getCorporateHqAddress());
				customerT.setCreatedModifiedBy(customerToInsert
						.getCreatedModifiedBy());
				customerT.setGroupCustomerName(customerToInsert
						.getGroupCustomerName());
				customerT.setFacebook(customerToInsert.getFacebook());
				customerT.setWebsite(customerToInsert.getWebsite());
				customerT.setLogo(customerToInsert.getLogo());
				customerT.setGeography(customerToInsert.getGeography());
				customerT.setIou(customerToInsert.getIou());
			} else {
				logger.error("EXISTS: customer Already Exist!");
				throw new DestinationException(HttpStatus.CONFLICT,
						"customer Already Exist!");
			}
			validateInactiveIndicators(customerT);
			
			logger.info("before save");
			customerT = customerRepository.save(customerT);
			logger.info("Customer Saved .... " + customerT.getCustomerId());
		}
		return customerT;
	}

	/**
	 * validates all the fields of customer which has any inactive fields 
	 * @param customer
	 * @throws {@link DestinationException} if any inactive records founds
	 */
	public void validateInactiveIndicators(CustomerMasterT customer) {

		//iou,
		String iou = customer.getIou();
		if(StringUtils.isNotBlank(iou) && iouRepository.findByActiveTrueAndIou(iou) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The iou is inactive");
		}

		// createdModifiedBy, 
		String createdBy = customer.getCreatedModifiedBy();
		if(StringUtils.isNotBlank(createdBy) && userRepository.findByActiveTrueAndUserId(createdBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The user createdBy is inactive");
		}

		// geography, 
		String geography = customer.getGeography();
		if(StringUtils.isNotBlank(geography) && geoRepository.findByActiveTrueAndGeography(geography) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The geography is inactive");
		}

		// beaconCustomerMappingTs,
		List<BeaconCustomerMappingT> beaconCustomerMapping = customer.getBeaconCustomerMappingTs();
		if(CollectionUtils.isNotEmpty(beaconCustomerMapping)) {
			for (BeaconCustomerMappingT beaconCustomer : beaconCustomerMapping) {
				String beaconIou = beaconCustomer.getBeaconIou();
				if(StringUtils.isNotBlank(beaconIou) && iouRepository.findByActiveTrueAndIou(beaconIou) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The beacon iou is inactive");
				}

				String geo = beaconCustomer.getCustomerGeography();
				if(StringUtils.isNotBlank(geo) && geoRepository.findByActiveTrueAndGeography(geo) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The beacon geography is inactive");
				}
			}
		}

		// revenueCustomerMappingTs,
		List<RevenueCustomerMappingT> revenueCustMapping = customer.getRevenueCustomerMappingTs();
		if(CollectionUtils.isNotEmpty(revenueCustMapping)) {
			for (RevenueCustomerMappingT revenueCust : revenueCustMapping) {
				String financeIou = revenueCust.getFinanceIou();
				if(StringUtils.isNotBlank(financeIou) && iouRepository.findByActiveTrueAndIou(financeIou) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The revenue iou is inactive");
				}

				String geo = revenueCust.getCustomerGeography();
				if(StringUtils.isNotBlank(geo) && geoRepository.findByActiveTrueAndGeography(geo) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The revenue geography is inactive");
				}
			}
		}

		// contactCustomerLinkTs,
		List<ContactCustomerLinkT> contactCustomerLink = customer.getContactCustomerLinkTs();
		if(CollectionUtils.isNotEmpty(contactCustomerLink)) {
			for (ContactCustomerLinkT revenueCust : contactCustomerLink) {
				String contact = revenueCust.getContactId();
				if(StringUtils.isNotBlank(contact) && contactRepository.findByActiveTrueAndContactId(contact) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The contact is inactive");
				}
			}
		}
		
	}

	/**
	 * This method inserts Beacon customers to the database
	 * 
	 * @param beaconCustomerToInsert
	 * @return BeaconCustomerMappingT
	 * @throws Exception
	 */
	@Transactional
	public BeaconCustomerMappingT addBeaconCustomer(
			BeaconCustomerMappingT beaconCustomerToInsert) throws Exception {
		BeaconCustomerMappingT beaconT = null;
		//	BeaconCustomerMappingTPK beaconTPK = null;
		List<BeaconCustomerMappingT> beaconCustomers = null;
		if (beaconCustomerToInsert != null) {
			beaconT = new BeaconCustomerMappingT();
			//beaconTPK = new BeaconCustomerMappingTPK();

			// to find the uniqueness of the primary key (here composite key)
			beaconCustomers = beaconRepository.findbeaconDuplicates(
					beaconCustomerToInsert.getBeaconCustomerName(),
					beaconCustomerToInsert.getBeaconIou(),
					beaconCustomerToInsert.getCustomerGeography());
			if (beaconCustomers.isEmpty()) {
				// CustomerMasterT customerMasterT=beaconCustomers.get(0).getCustomerMasterT();
				beaconT.setCustomerId(beaconCustomerToInsert.getCustomerId());
				beaconT.setBeaconCustomerName(beaconCustomerToInsert
						.getBeaconCustomerName());
				beaconT.setBeaconIou(beaconCustomerToInsert.getBeaconIou());
				beaconT.setCustomerGeography(beaconCustomerToInsert
						.getCustomerGeography());
			} else {
				logger.error("EXISTS: Beacon Already Exist!");
				throw new DestinationException(HttpStatus.CONFLICT,
						"Beacon Already Exist!");
			}
			if(beaconT!=null)
			{
				beaconT = beaconRepository.save(beaconT);
			}
			logger.info("Beacon Saved .... ");
		}
		return beaconT;
	}

	/**
	 * This methos is used to perform a search 
	 * @param groupCustomerNameWith
	 * @param nameWith
	 * @param geography
	 * @param displayIOU
	 * @param inactive
	 * @param page
	 * @param count
	 * @return
	 * @throws DestinationException
	 */
	public PaginatedResponse search(String groupCustomerNameWith,
			String nameWith, List<String> geography, List<String> displayIOU,
			boolean inactive, int page, int count) throws DestinationException {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		if (geography.isEmpty())
			geography.add("");
		if (displayIOU.isEmpty())
			displayIOU.add("");
		List<CustomerMasterT> customerMasterTs = customerRepository
				.advancedSearch(
						"%" + groupCustomerNameWith.toUpperCase() + "%", "%"
								+ nameWith.toUpperCase() + "%", geography,
								displayIOU, !inactive);

		if (customerMasterTs.isEmpty()) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer available");
		}

		paginatedResponse.setTotalCount(customerMasterTs.size());

		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count,
				customerMasterTs.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					customerMasterTs.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					customerMasterTs.size()) + 1;
			customerMasterTs = customerMasterTs.subList(fromIndex, toIndex);
			paginatedResponse.setCustomerMasterTs(customerMasterTs);
			logger.debug("Partners after pagination size is "
					+ customerMasterTs.size());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Partner available for the specified page");
		}
		return paginatedResponse;
	}
    
	/**
	 * This method is used to update the customer details
	 * @param customerMaster
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean updateCustomer(CustomerMasterT customerMaster) throws Exception {

		boolean isValid = false;
		boolean isBdmWithAccess=false;
		CustomerMasterT  customerEdited = null;
		CustomerMasterT savedCustomer=null;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		logger.debug("Inside updateCustomer() of CustomerService");
		UserT userT = userRepository.findByUserId(userId);

		String userRole = userT.getUserRole();
		String userGroup=userT.getUserGroup();
		if(UserRole.contains(userRole)){
			switch (UserRole.valueOf(UserRole.getName(userRole))){

			case SYSTEM_ADMIN: 
			case STRATEGIC_GROUP_ADMIN:

				customerEdited = validateCustomerDetails(customerMaster);
				savedCustomer = editCustomer(customerEdited,isBdmWithAccess);
				if (savedCustomer != null) {
					isValid = true;
					logger.info("Customer has been updated successfully: " + savedCustomer.getCustomerName());
					return isValid;
				}
				break;

			case USER:	
				if (UserGroup.contains(userGroup))
				{
					switch(UserGroup.valueOf(UserGroup.getName(userGroup)))
					{
					case BDM:
						List<String> privilegeValueList = null; 
						List<UserAccessPrivilegesT> userAccessPrevilegeList = userAccessPrivilegesRepository.findByUserIdAndIsactive(userId, "Y");
						for(UserAccessPrivilegesT userPrivege:userAccessPrevilegeList)
						{
							switch (PrivilegeType.valueOf(userPrivege.getPrivilegeType())){
							case CUSTOMER: 
								privilegeValueList=userAccessPrivilegesRepository.getPrivilegeValueForUser(userId,PrivilegeType.CUSTOMER.toString());
								privilegeValueList.contains(customerMaster.getCustomerName());
								isBdmWithAccess=true;
								break;
							case GEOGRAPHY:
								privilegeValueList=userAccessPrivilegesRepository.getPrivilegeValueForUser(userId,PrivilegeType.GEOGRAPHY.toString());
								privilegeValueList.contains(customerMaster.getGeography());
								isBdmWithAccess=true;
								break;
							case IOU:
								privilegeValueList=userAccessPrivilegesRepository.getPrivilegeValueForUser(userId,PrivilegeType.IOU.toString());
								privilegeValueList.contains(customerMaster.getIou());
								isBdmWithAccess=true;
								break;
							default:
									break;
							}
						}

						if(isBdmWithAccess)
						{
							customerEdited = validateCustomerDetails(customerMaster);  
							savedCustomer = editCustomer(customerEdited,isBdmWithAccess);
							if (savedCustomer != null) {
								isValid = true;
								logger.info("Customer has been updated successfully: " + savedCustomer.getCustomerName());
								return isValid;
							}
						}
						else
						{
							logger.error("NOT_AUTHORISED: user is not authorised to update the customer");
							throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the customer" );
						}
						break;

					default:
						break;

					}
				}

			default: 
				logger.error("NOT_AUTHORISED: user is not authorised to update the customer");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the customer" );
			}
		}


		return isValid;
	}


	private boolean isBeaconModied(List<BeaconCustomerMappingT> oldBeaconObj,
			List<BeaconCustomerMappingT> beaconCustomerMappingTs) {
		List<BeaconCustomerMappingT> beaconCustomers = null;
		boolean isBeaconCustomerModifiedFlag = false;
		for(BeaconCustomerMappingT bcmtOld : oldBeaconObj){
			for(BeaconCustomerMappingT bcmtNew : beaconCustomerMappingTs){
				if((bcmtNew.getBeaconCustomerMapId() != null) && (bcmtNew.getBeaconCustomerMapId().equals(bcmtOld.getBeaconCustomerMapId()))){
					isBeaconCustomerModifiedFlag = false;
					if (!bcmtNew.getBeaconCustomerName().equals(bcmtOld.getBeaconCustomerName())) {
						bcmtOld.setBeaconCustomerName(bcmtNew.getBeaconCustomerName());
						isBeaconCustomerModifiedFlag =true;
					}
					if (!bcmtNew.getBeaconIou().equals(bcmtOld.getBeaconIou())) {
						bcmtOld.setBeaconIou(bcmtNew.getBeaconIou());
						isBeaconCustomerModifiedFlag =true;
					}
					if (!bcmtNew.getCustomerGeography().equals(bcmtOld.getCustomerGeography())) {
						bcmtOld.setCustomerGeography(bcmtNew.getCustomerGeography());
						isBeaconCustomerModifiedFlag =true;
					}
					if(isBeaconCustomerModifiedFlag == false && bcmtNew.equals(bcmtOld)){
						logger.error("BAD_REQUEST: This Beacon details already exists..");
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"This Beacon details already exists..");
					}
					beaconCustomers = beaconCustomerMappingRepository.checkBeaconMappingPK(bcmtNew.getBeaconCustomerName(),bcmtNew.getCustomerGeography(),bcmtNew.getBeaconIou());
					if(!beaconCustomers.isEmpty() && isBeaconCustomerModifiedFlag == true){
						logger.error("This Beacon details already exists.."+bcmtNew.getBeaconCustomerName() +" " +bcmtNew.getCustomerGeography() + " " + bcmtNew.getBeaconIou());
						throw new DestinationException(
								HttpStatus.BAD_REQUEST,
								"This Beacon details already exists.."+bcmtNew.getBeaconCustomerName() +" " +bcmtNew.getCustomerGeography() + " " + bcmtNew.getBeaconIou());
					}
					if(isBeaconCustomerModifiedFlag == true){
						beaconRepository.save(bcmtOld);
					}
				}
			}
		}
		// for adding a new beacon data
		BeaconCustomerMappingT bcmtNewEntry = new BeaconCustomerMappingT();
		for(BeaconCustomerMappingT rcmtNew : beaconCustomerMappingTs){
			if(rcmtNew.getBeaconCustomerMapId() == null){
				bcmtNewEntry.setBeaconCustomerName(rcmtNew.getBeaconCustomerName());
				bcmtNewEntry.setBeaconIou(rcmtNew.getBeaconIou());
				bcmtNewEntry.setCustomerGeography(rcmtNew.getCustomerGeography());
				bcmtNewEntry.setCustomerId(rcmtNew.getCustomerId());
				oldBeaconObj.add(bcmtNewEntry);
				isBeaconCustomerModifiedFlag =true;
				beaconRepository.save(bcmtNewEntry);
			}
		}
		return isBeaconCustomerModifiedFlag;
	}

	private boolean isRevenueModified(List<RevenueCustomerMappingT> oldRevenueObj,
			List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		List<RevenueCustomerMappingT> financeCustomers = null;
		boolean isRevenueCustomerModifiedFlag = false;
		for(RevenueCustomerMappingT rcmtOld : oldRevenueObj){
			for(RevenueCustomerMappingT rcmtNew : revenueCustomerMappingTs){
				if((rcmtNew.getRevenueCustomerMapId() != null) && (rcmtNew.getRevenueCustomerMapId().equals(rcmtOld.getRevenueCustomerMapId()))){
					isRevenueCustomerModifiedFlag = false;
					if (!rcmtNew.getFinanceCustomerName().equals(rcmtOld.getFinanceCustomerName())) {
						rcmtOld.setFinanceCustomerName(rcmtNew.getFinanceCustomerName());
						isRevenueCustomerModifiedFlag =true;
					}
					if (!rcmtNew.getFinanceIou().equals(rcmtOld.getFinanceIou())) {
						rcmtOld.setFinanceIou(rcmtNew.getFinanceIou());
						isRevenueCustomerModifiedFlag =true;
					}
					if (!rcmtNew.getCustomerGeography().equals(rcmtOld.getCustomerGeography())) {
						rcmtOld.setCustomerGeography(rcmtNew.getCustomerGeography());
						isRevenueCustomerModifiedFlag =true;
					}

					financeCustomers = revenueRepository.checkRevenueMappingPK(rcmtNew.getFinanceCustomerName(),rcmtNew.getCustomerGeography(),rcmtNew.getFinanceIou());
					if(!financeCustomers.isEmpty() && isRevenueCustomerModifiedFlag == true){
						logger.error("This Revenue details already exists.."+rcmtNew.getFinanceCustomerName() +" " +rcmtNew.getCustomerGeography() + " " + rcmtNew.getFinanceIou());
						throw new DestinationException(
								HttpStatus.BAD_REQUEST,
								"This Revenue details already exists.."+rcmtNew.getFinanceCustomerName() +" " +rcmtNew.getCustomerGeography() + " " + rcmtNew.getFinanceIou());
					}

					if(isRevenueCustomerModifiedFlag == true ){
						revenueRepository.save(rcmtOld);
					}
				}
			}
		}
		// for adding a new revenue data
		RevenueCustomerMappingT rcmtNewEntry = new RevenueCustomerMappingT();
		for(RevenueCustomerMappingT rcmtNew : revenueCustomerMappingTs){
			if(rcmtNew.getRevenueCustomerMapId() == null){
				rcmtNewEntry.setFinanceCustomerName(rcmtNew.getFinanceCustomerName());
				rcmtNewEntry.setFinanceIou(rcmtNew.getFinanceIou());
				rcmtNewEntry.setCustomerGeography(rcmtNew.getCustomerGeography());
				rcmtNewEntry.setCustomerId(rcmtNew.getCustomerId());
				oldRevenueObj.add(rcmtNewEntry);
				isRevenueCustomerModifiedFlag =true;
				revenueRepository.save(rcmtNewEntry);
			}
		}
		return isRevenueCustomerModifiedFlag;
	}

	/**
	 * to check if the customer master fields are edited before save
	 * @param oldCustomerObj
	 * @param customerMaster
	 * @return
	 */
	private boolean isCustomerMasterModified(CustomerMasterT oldCustomerObj,
			CustomerMasterT customerMaster,boolean isBdmFlag) {
		boolean isCustomerModifiedFlag = false;
		String corporateHqAdress = "";
		String website = "";
		String notes = "";
		String facebook = "";
		byte[] logo=null;
		//customer name
		if (!customerMaster.getCustomerName().equals(oldCustomerObj.getCustomerName())) {
			if(!isBdmFlag)
			{
				oldCustomerObj.setCustomerName(customerMaster.getCustomerName());
				isCustomerModifiedFlag =true;
			}
			else
			{
				logger.error("NOT_AUTHORISED: user is not authorised to update the customer name");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the customer name" );
			}


		}
		//corpoarate address
		if(!StringUtils.isEmpty(oldCustomerObj.getCorporateHqAddress())){
			corporateHqAdress = oldCustomerObj.getCorporateHqAddress();
		}
		if (!customerMaster.getCorporateHqAddress().equals(corporateHqAdress)) {
			oldCustomerObj.setCorporateHqAddress(customerMaster.getCorporateHqAddress());
			isCustomerModifiedFlag = true;
		}

		//logo
		if ((oldCustomerObj.getLogo() == null && customerMaster.getLogo() != null)
				|| (!oldCustomerObj.getLogo().equals(customerMaster.getLogo()))) {
			logo = oldCustomerObj.getLogo();
			if (!customerMaster.getLogo().equals(logo)) {
				oldCustomerObj.setLogo(customerMaster.getLogo());
				isCustomerModifiedFlag = true;
			}
		}

		//notes edited
		if(!StringUtils.isEmpty(oldCustomerObj.getNotes())){
			notes = oldCustomerObj.getNotes();
		}
		if (!customerMaster.getNotes().equals(notes)) {
			oldCustomerObj.setNotes(customerMaster.getNotes());
			isCustomerModifiedFlag = true;
		}
		//facebook
		if(!StringUtils.isEmpty(oldCustomerObj.getFacebook())){
			facebook = oldCustomerObj.getFacebook();
		}
		if (!customerMaster.getFacebook().equals(facebook)) {
			oldCustomerObj.setFacebook(customerMaster.getFacebook());
			isCustomerModifiedFlag = true;
		}
		//website
		if(!StringUtils.isEmpty(oldCustomerObj.getWebsite())){
			website = oldCustomerObj.getWebsite();
		}
		if (!customerMaster.getWebsite().equals(website)) {
			oldCustomerObj.setWebsite(customerMaster.getWebsite());
			isCustomerModifiedFlag = true;
		}
		//geography
		if (!customerMaster.getGeography().equals(oldCustomerObj.getGeography())) {
			if(!isBdmFlag)
			{
				oldCustomerObj.setGeography(customerMaster.getGeography());
				isCustomerModifiedFlag = true;
			}
			else
			{
				logger.error("NOT_AUTHORISED: user is not authorised to update the geography");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the geography" );
			}

		}
		//group customer name 
		if (!customerMaster.getGroupCustomerName().equals(oldCustomerObj.getGroupCustomerName())) {
			oldCustomerObj.setGroupCustomerName(customerMaster.getGroupCustomerName());
			isCustomerModifiedFlag =true;
		}
		//iou
		if (!customerMaster.getIou().equals(oldCustomerObj.getIou())) {
			if(!isBdmFlag)
			{
				oldCustomerObj.setIou(customerMaster.getIou());
				isCustomerModifiedFlag =true;
			}
			else
			{
				logger.error("NOT_AUTHORISED: user is not authorised to update the iou");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "user is not authorised to update the iou" );
			}

		}


		return isCustomerModifiedFlag;
	}
   
	/**
    * To validate the customer details
    * @param requestedCustomerT
    * @return
    * @throws Exception
    */
	private CustomerMasterT validateCustomerDetails(CustomerMasterT requestedCustomerT) throws Exception {

		CustomerMasterT customerToBeSaved = null;
		logger.debug("Inside updateCustomer() of CustomerService");

		mapOfGeographyMappingT = customerUploadService.getGeographyMappingT();
		mapOfIouCustomerMappingT = customerUploadService.getIouMappingT();
		mapOfIouBeaconMappingT = customerUploadService.getBeaconIouMappingT();
		String customerId = requestedCustomerT.getCustomerId();
		CustomerMasterT copiedObject = (CustomerMasterT) DestinationUtils.copy(requestedCustomerT);
		// true in case of admin: to validate the iou field for not empty check
		customerToBeSaved = validateCustomerMasterDetails(requestedCustomerT);

		List<RevenueCustomerMappingT> revenueCustomerMappingTs = new ArrayList<RevenueCustomerMappingT>();
		List<BeaconCustomerMappingT> beaconCustomerMappingTs = new ArrayList<BeaconCustomerMappingT>();
		revenueCustomerMappingTs = copiedObject.getRevenueCustomerMappingTs();

		if (CollectionUtils.isNotEmpty(revenueCustomerMappingTs)) {
			revenueCustomerMappingTs = validateRevenueCustomerDetails(revenueCustomerMappingTs, customerId);
		} 
		customerToBeSaved.setRevenueCustomerMappingTs(revenueCustomerMappingTs);
		beaconCustomerMappingTs = copiedObject
				.getBeaconCustomerMappingTs();
		if (CollectionUtils.isNotEmpty(beaconCustomerMappingTs)) {
			beaconCustomerMappingTs = validateBeaconCustomerDetails(beaconCustomerMappingTs,customerId);

		}
		customerToBeSaved.setBeaconCustomerMappingTs(beaconCustomerMappingTs);
		return customerToBeSaved;
	}

	/*
	 * to validate the customer master details
	 */
	private CustomerMasterT validateCustomerMasterDetails(CustomerMasterT customerMaster) throws Exception {
		

		CustomerMasterT customerToBeSaved = new CustomerMasterT();
		CustomerMasterT duplicateCustomer = new CustomerMasterT();
		CustomerMasterT customerCopy = new CustomerMasterT();

		if (customerMaster.getCustomerId()== null) {
			logger.error("BAD_REQUEST: customerId is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"customerId is required for update");
		}

		// Check if the customer exists
		if (customerRepository.exists(customerMaster.getCustomerId())) {
			customerToBeSaved = customerRepository.findOne(customerMaster.getCustomerId());
			customerCopy = (CustomerMasterT) DestinationUtils.copy(customerToBeSaved);
		}
		else{
			logger.error("NOT_FOUND: Customer not found for update: {}",customerMaster.getCustomerId());
			throw new DestinationException(HttpStatus.NOT_FOUND, "Customer not found for update: " + customerMaster.getCustomerId());
		}

		// Check if the customer name already exists
		if (!StringUtils.isEmpty((customerMaster.getCustomerName()))) {
			CustomerMasterT oldObject = new CustomerMasterT();
			oldObject = customerRepository.findOne(customerMaster.getCustomerId());
			if (!customerMaster.getCustomerName().equals(
					oldObject.getCustomerName())) {
				duplicateCustomer = customerRepository.findByCustomerName(customerMaster.getCustomerName());
				if(duplicateCustomer == null){
					customerCopy.setCustomerName(customerMaster.getCustomerName());
				}
				else{
					logger.error("BAD_REQUEST: This Customer name already exists: {}",customerMaster.getCustomerName());
					throw new DestinationException(HttpStatus.BAD_REQUEST, "This Customer name already exists: " + customerMaster.getCustomerName());
				}
			}
		}
		else{
			logger.error("NOT_VALID: Customer Name is empty for update: {}",customerMaster.getCustomerName());
			throw new DestinationException(HttpStatus.NOT_FOUND, "Customer name is Empty" + customerMaster.getCustomerName());
		}
		customerCopy.setCreatedModifiedBy(DestinationUtils.getCurrentUserDetails().getUserId());

		// IOU 
		if(customerMaster.getIou().length()>0){
			if(mapOfIouCustomerMappingT.containsKey(customerMaster.getIou())){
				customerCopy.setIou(customerMaster.getIou());
			} else {
				logger.error("NOT_VALID: IOU is not valid for update: {}",customerMaster.getIou());
				throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid IOU");
			}
		}

		customerCopy.setDocumentsAttached("NO");

		// MASTER_GEOGRAPHY
		if(customerMaster.getGeography().length()>0){
			if(mapOfGeographyMappingT.containsKey(customerMaster.getGeography())){
				customerCopy.setGeography(customerMaster.getGeography());
			} else {
				logger.error("NOT_VALID: Geography is not valid for update: {}",customerMaster.getGeography());
				throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid geography");
			}
		}

		// GROUP_CUSTOMER_NAME	
		if(!StringUtils.isEmpty(customerMaster.getGroupCustomerName())){
			customerCopy.setGroupCustomerName(customerMaster.getGroupCustomerName());
		}
		else{
			logger.error("NOT_VALID: group Customer Name is empty for update: {}",customerMaster.getGroupCustomerName());
			throw new DestinationException(HttpStatus.NOT_FOUND, "Group Customer name is Empty" + customerMaster.getGroupCustomerName());
		}

		//LOGO
		if(customerMaster.getLogo()!=null)
		{
			customerCopy.setLogo(customerMaster.getLogo());
		}
		/*else
		{
			customerCopy.setLogo(null);
		}*/

		if(customerMaster.getNotes()!=null)
		{
			customerCopy.setNotes(customerMaster.getNotes());
		}
		else
		{
			customerCopy.setNotes("");
		}

		if(customerMaster.getCorporateHqAddress()!=null)
		{
			customerCopy.setCorporateHqAddress(customerMaster.getCorporateHqAddress());
		}
		else
		{
			customerCopy.setCorporateHqAddress("");

		}
		if(customerMaster.getFacebook()!=null)
		{
			customerCopy.setFacebook(customerMaster.getFacebook());
		}
		else{
			customerCopy.setFacebook("");
		}
		if(customerMaster.getWebsite()!=null)
		{
			customerCopy.setWebsite(customerMaster.getWebsite());
		}
		else
		{
			customerCopy.setWebsite("");
		}
		return customerCopy;
	}

	/**
	 * validate beacon details for the requested customer
	 * 
	 * @param beaconCustomerMappingTs
	 */
	private List<BeaconCustomerMappingT> validateBeaconCustomerDetails(List<BeaconCustomerMappingT> beaconCustomerMappingTs, String customerId) {
		for (BeaconCustomerMappingT bcmt : beaconCustomerMappingTs) {
			if (StringUtils.isEmpty(bcmt.getBeaconCustomerName())) {
				logger.error("Beacon Customer name should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Beacon Customer name should not be empty");
			}
			if (!StringUtils.isEmpty(bcmt.getCustomerGeography())) {
				if (!mapOfGeographyMappingT.containsKey(bcmt
						.getCustomerGeography())) {
					logger.error("Invalid Geography");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid Geography" + bcmt.getCustomerGeography());
				}
			} else {
				logger.error("Geography Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Geography Should not be empty");
			}
			if (!StringUtils.isEmpty(bcmt.getBeaconIou())) {
				if (!mapOfIouBeaconMappingT.containsKey(bcmt.getBeaconIou())) {
					logger.error("Invalid IOU");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid IOU" + bcmt.getBeaconIou());
				}
			} else {
				logger.error("IOU Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"IOU Should not be empty");
			}
		}
		return beaconCustomerMappingTs;
	}

	/**
	 * validate revenue details for the requested customer
	 * 
	 * @param revenueCustomerMappingTs
	 */
	private List<RevenueCustomerMappingT> validateRevenueCustomerDetails(List<RevenueCustomerMappingT> revenueCustomerMappingTs, String customerId) {
		for (RevenueCustomerMappingT rcmt : revenueCustomerMappingTs) {
			if (StringUtils.isEmpty(rcmt.getFinanceCustomerName())) {
				logger.error("Finance Customer name should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Finance Customer name should not be empty");
			}

			if (!StringUtils.isEmpty(rcmt.getCustomerGeography())) {
				if (!mapOfGeographyMappingT.containsKey(rcmt
						.getCustomerGeography())) {
					logger.error("Invalid Geography");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid Geography" + rcmt.getCustomerGeography());
				}
			} else {
				logger.error("Geography Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Geography Should not be empty");
			}
			if (!StringUtils.isEmpty(rcmt.getFinanceIou())) {
				if (!mapOfIouCustomerMappingT.containsKey(rcmt.getFinanceIou())) {
					logger.error("Invalid IOU");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"Invalid IOU" + rcmt.getFinanceIou());
				}
			} else {
				logger.error("IOU Should not be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"IOU Should not be empty");
			}
		}
		return revenueCustomerMappingTs;
	}


	// Customer object is updated into the repository
	@Transactional
	private CustomerMasterT editCustomer(CustomerMasterT customerMaster,boolean isBdmFlag) throws Exception 
	{

		String customerId = customerMaster.getCustomerId();
		CustomerMasterT oldCustomerObj = customerRepository.findOne(customerId);
		List<RevenueCustomerMappingT> oldRevenueObj =  revenueRepository.findByCustomerId(customerId);
		List<BeaconCustomerMappingT> oldBeaconObj = beaconCustomerMappingRepository.findByCustomerId(customerId);
		// updated customer object is saved to the database
		if(isCustomerMasterModified(oldCustomerObj, customerMaster,isBdmFlag)){
			 customerRepository.save(oldCustomerObj);
		}
		if(customerMaster.getRevenueCustomerMappingTs()!=null)
		{
			isRevenueModified(oldRevenueObj, customerMaster.getRevenueCustomerMappingTs());
		}
		if(customerMaster.getBeaconCustomerMappingTs()!=null)
		{
			isBeaconModied(oldBeaconObj, customerMaster.getBeaconCustomerMappingTs());
		}
		return oldCustomerObj;
	}
	/*
	 * soft delete - status updated as inactive for the given customer list
	 */
	public void makeInactive(List<CustomerMasterT> deleteList) {
		for(CustomerMasterT customer : deleteList){
			customer.setActive(false);
			customerRepository.save(customer);
		}
	}
}