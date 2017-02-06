package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerAssociateT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.GroupCustomerT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.QueryBufferDTO;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.dto.CustomerListDTO;
import com.tcs.destination.bean.dto.GroupCustomerDTO;
import com.tcs.destination.data.repository.BeaconCustomerMappingRepository;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerDao;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.GroupCustomerPagingRepository;
import com.tcs.destination.data.repository.GroupCustomerRepository;
import com.tcs.destination.data.repository.IouRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.SalesStageCode;
import com.tcs.destination.enums.SmartSearchType;
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
	
	@Autowired @Lazy
	private CustomerDao customerDao;
	
	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	GroupCustomerRepository groupCustomerRepository;
	
	@Autowired
	GroupCustomerPagingRepository groupCustomerPagingRepository;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouCustomerMappingT = null;
	Map<String, IouBeaconMappingT> mapOfIouBeaconMappingT = null;

	@Autowired
	private ConnectRepository connectRepository;

	@Autowired
	private ConnectService connectService;
	
	@Autowired
	DozerBeanMapper beanMapper;


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
		UserT userT= DestinationUtils.getCurrentUserDetails();
		String userGroup = userT.getUserGroup();
		CustomerMasterT customerMasterT = customerRepository
				.findOne(customerId);
		if (customerMasterT == null) {
			logger.error("NOT_FOUND: Customer not found: {}", customerId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found: " + customerId);
		}
		UserT supervisorUser = userRepository
				.findByUserId(userT
						.getSupervisorUserId());
		boolean pmoDelivery = opportunityService.isPMODelivery(userT,supervisorUser);
		if(userGroup.contains(UserGroup.DELIVERY_CLUSTER_HEAD.getValue()) 
				|| userGroup.contains(UserGroup.DELIVERY_CENTRE_HEAD.getValue()) 
				|| userGroup.contains(UserGroup.DELIVERY_MANAGER.getValue())
				|| pmoDelivery){
			
			prepareDeliveryCustomerDetails(customerMasterT, pmoDelivery ? supervisorUser : userT);
		} else {
			prepareCustomerDetails(customerMasterT, null);
		}
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
			customerNameList = customerDao.getPreviledgedCustomerName(userId, customerNameList,
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
			case CONSULTING_HEAD:
			case CONSULTING_USER:
			case DELIVERY_CLUSTER_HEAD:
			case DELIVERY_CENTRE_HEAD:
			case DELIVERY_MANAGER:	
				logger.error("User is not authorized to access this service");
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User is not authorised to access this service");
			default:
				UserT supervisorUser = userRepository
				.findByUserId(user
						.getSupervisorUserId());
				boolean pmoDelivery = opportunityService.isPMODelivery(user,supervisorUser);
				if(pmoDelivery) {
					logger.error("User is not authorized to access this service");
					throw new DestinationException(HttpStatus.FORBIDDEN,
							"User is not authorised to access this service");
				} else {
					// Validate financial year and set default value
					if (financialYear.isEmpty()) {
						logger.debug("Financial year is empty");
						financialYear = DateUtils.getCurrentFinancialYear();
					}
					List<CustomerMasterT> resultCustomerList = getTopRevenuesBasedOnUserPrivileges(
							user.getUserId(), financialYear, count);
					return resultCustomerList;
				}
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
		List<CustomerMasterT> resultList = (ArrayList<CustomerMasterT>) topRevenueQuery.getResultList();
		if (resultList == null || resultList.isEmpty()) {
			logger.error("NOT_FOUND: Top revenue customers not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Top revenue customers not found");
		}
		removeCyclicForLinkedContactTs(resultList);
		return resultList;
	}

	/**
	 * This method is used to fetch the customer details using group customer name based on priviledge
	 * @param nameWith
	 * @return
	 * @throws Exception
	 */
	
	public List<String> findByGroupCustomerNameBasedOnPrivilege(String nameWith)
			throws Exception {
		List<String> resultList=customerDao.findByGroupCustomerNameBasedOnPrivilege(nameWith);
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
				 customerDao.TOP_REVENUE_PROJECTED_PREFIX);
		queryBuffer.append(reportsService.getTopRevenueCustomersForDashboard(userId, count));
		queryBuffer.append( customerDao.TOP_REVENUE_PROJECTED_SUFFIX);
		return queryBuffer.toString();
	}

	public List<TargetVsActualResponse> findTargetVsActual(
			String financialYear, String quarter, String customerName,
			String currency) throws Exception {
		logger.debug("Inside findTargetVsActual() service");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		ArrayList<String> customerNameList = new ArrayList<String>();
		customerNameList.add(customerName);
		customerNameList =  customerDao.getPreviledgedCustomerName(userId, customerNameList,
				true);

		if (customerNameList == null || customerNameList.isEmpty())
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User does not have access to view this information");

		return performanceReportService.getTargetVsActualRevenueSummary(
				financialYear, quarter, "", "", "", "", customerName, currency,
				"", false, userId, false, Constants.CATEGORY_REVENUE);
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

	private void prepareCustomerDetails(List<CustomerMasterT> customerMasterList)
			throws Exception {
		logger.debug("Inside prepareCustomerDetails() method");
		UserT userT= DestinationUtils.getCurrentUserDetails();
		String userGroup = userT.getUserGroup();
		if (customerMasterList != null && !customerMasterList.isEmpty()) {
			ArrayList<String> customerNameList = new ArrayList<String>();
			for (CustomerMasterT customerMasterT : customerMasterList) {
				customerNameList.add(customerMasterT.getCustomerName());
			}
			UserT supervisorUser = userRepository
					.findByUserId(userT
							.getSupervisorUserId());
			boolean pmoDelivery = opportunityService.isPMODelivery(userT,supervisorUser);
			if(userGroup.contains(UserGroup.DELIVERY_CLUSTER_HEAD.getValue()) 
					|| userGroup.contains(UserGroup.DELIVERY_CENTRE_HEAD.getValue()) 
					|| userGroup.contains(UserGroup.DELIVERY_MANAGER.getValue())
					|| pmoDelivery){
				
				for (CustomerMasterT customerMasterT : customerMasterList) {
					prepareDeliveryCustomerDetails(customerMasterT, pmoDelivery ? supervisorUser : userT);
				}
			} else {
				customerNameList =  customerDao.getPreviledgedCustomerName(userT.getUserId(), 
						customerNameList, true);
				for (CustomerMasterT customerMasterT : customerMasterList) {
					prepareCustomerDetails(customerMasterT, customerNameList);
				}
			}
		}
	}

	/**
	 * This method is used to prepare delivery customer for his and his subordinates
	 * 
	 * @param customerMasterT
	 * @param userT
	 */
	public void prepareDeliveryCustomerDetails(CustomerMasterT customerMasterT, UserT userT) {
		removeCyclicForLinkedContactTs(customerMasterT);
		List<String> userIds = userRepository.getAllSubordinatesIdBySupervisorId(userT.getUserId());
		userIds.add(userT.getUserId());
		for (ContactCustomerLinkT contactCustomerLinkT : customerMasterT.getContactCustomerLinkTs()) {
			if(!userIds.contains(contactCustomerLinkT.getContactT().getCreatedByUser().getUserId())){
				contactService.preventSensitiveInfoForDelivery(contactCustomerLinkT.getContactT());
			}
		}
		List<ConnectT> connectTs = connectRepository.getConnectByOwnersAndCustomer(userIds, customerMasterT.getCustomerId());
		customerMasterT.setConnectTs(connectTs);
		
		List<OpportunityT> opportunityts = opportunityRepository.findAllDeliveryOpportunitiesByOwnersAndCustomer(customerMasterT.getCustomerId(), userIds);
		
		customerMasterT.setOpportunityTs(opportunityts); 
	}

	private void prepareCustomerDetails(CustomerMasterT customerMasterT,
			ArrayList<String> customerNameList) throws DestinationException {
		logger.debug("Inside prepareCustomerDetails() method");

		removeCyclicForLinkedContactTs(customerMasterT);
		try {
			if (customerNameList == null) {
				customerNameList = new ArrayList<String>();
				customerNameList.add(customerMasterT.getCustomerName());
				customerNameList =  customerDao.getPreviledgedCustomerName(DestinationUtils
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
	 * @param userGroup 
	 */
	private void hideSensitiveInfo(CustomerMasterT customerMasterT) {
		logger.debug("Inside hideSensitiveInfo() method");
		opportunityService.preventSensitiveInfo(customerMasterT
				.getOpportunityTs());
			for (ContactCustomerLinkT contactCustomerLinkT : customerMasterT
					.getContactCustomerLinkTs()) {
				contactService.preventSensitiveInfo(contactCustomerLinkT
						.getContactT());
			}
	}

	

	/**
	 * This method is used to fetch the group customer name with access priviledge restrictions applied
	 * @param userId
	 * @param nameWith
	 * @return
	 * @throws Exception
	 */

	public QueryBufferDTO getGroupCustomerPrivilegeQueryString(String userId,
			String nameWith) throws Exception {
		logger.debug("Inside getGroupCustomerPrivilegeQueryString() method");
		QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge
        StringBuffer queryBuffer = new StringBuffer(
				 customerDao.GROUP_CUSTOMER_NAME_QUERY_PREFIX);

		HashMap<String, String> queryPrefixMap;

		queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
				 customerDao.CUSTOMER_GEO_COND_SUFFIX, null,  customerDao.CUSTOMER_IOU_COND_SUFFIX,
				 customerDao.CUSTOMER_NAME_CUSTOMER_COND_SUFFIX);

		// Get WHERE clause string
		queryBufferDTO = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereCondition(userId,
						queryPrefixMap);

		if(queryBufferDTO!=null)
        {
		 if ((queryBufferDTO.getQuery() != null && !queryBufferDTO.getQuery().isEmpty())
				|| (nameWith != null && !nameWith.isEmpty())) {
			queryBuffer.append(" and "
					+ customerDao.GROUP_CUSTOMER_NAME_CUSTOMER_COND_SUFFIX + nameWith
					+ " and " + queryBufferDTO.getQuery() +customerDao.ORDERBY_SUFFIX);
		 }
		 queryBufferDTO.setQuery(queryBuffer.toString());
        }
        else
		   {
			queryBufferDTO=new QueryBufferDTO();
			queryBufferDTO.setQuery(queryBuffer.toString());
			queryBufferDTO.setParameterMap(null);
		   }
        
		logger.info("queryString = " +queryBufferDTO.getQuery());
		return queryBufferDTO;
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
		UserT userT= DestinationUtils.getCurrentUserDetails();
		String userGroup = userT.getUserGroup();
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
		UserT supervisorUser = userRepository
				.findByUserId(userT
						.getSupervisorUserId());
		boolean pmoDelivery = opportunityService.isPMODelivery(userT,supervisorUser);
		if(userGroup.contains(UserGroup.DELIVERY_CLUSTER_HEAD.getValue()) 
				|| userGroup.contains(UserGroup.DELIVERY_CENTRE_HEAD.getValue()) 
				|| userGroup.contains(UserGroup.DELIVERY_MANAGER.getValue())
				|| pmoDelivery) {
			
			for (CustomerMasterT customer : customerMasterTs) {
				prepareDeliveryCustomerDetails(
						customer,
						pmoDelivery ? supervisorUser : userT);
			}
			
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
			/*case STRATEGIC_GROUP_ADMIN:
				customerEdited = validateCustomerAndOtherDetails(customerMaster);
				savedCustomer = editCustomerAndOtherDetails(customerEdited,isBdmWithAccess);
				if (savedCustomer != null) {
					isValid = true;
					logger.info("Customer has been updated successfully: " + savedCustomer.getCustomerName());
					return isValid;
				}
				break;*/
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
							throw new DestinationException(HttpStatus.FORBIDDEN, "user is not authorised to update the customer" );
						}
						break;

					default:
						break;

					}
				}

			default: 
				logger.error("NOT_AUTHORISED: user is not authorised to update the customer");
				throw new DestinationException(HttpStatus.FORBIDDEN, "user is not authorised to update the customer" );
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
						isBeaconCustomerModifiedFlag =true;
					}
					if (!bcmtNew.getBeaconIou().equals(bcmtOld.getBeaconIou())) {
						isBeaconCustomerModifiedFlag =true;
					}
					if (!bcmtNew.getCustomerGeography().equals(bcmtOld.getCustomerGeography())) {
						isBeaconCustomerModifiedFlag =true;
					}
					String beaconCustomerName = isBeaconCustomerModifiedFlag?bcmtNew.getBeaconCustomerName():bcmtOld.getBeaconCustomerName();
					String beaconIou = isBeaconCustomerModifiedFlag?bcmtNew.getBeaconIou():bcmtOld.getBeaconIou();
					String beaconGeo = isBeaconCustomerModifiedFlag?bcmtNew.getCustomerGeography():bcmtOld.getCustomerGeography();

					beaconCustomers = beaconCustomerMappingRepository.checkBeaconMappingPK(beaconCustomerName,beaconGeo,beaconIou);
					if(!beaconCustomers.isEmpty() && isBeaconCustomerModifiedFlag == true){
						logger.error("This Beacon details already exists.."+bcmtNew.getBeaconCustomerName() +" " +bcmtNew.getCustomerGeography() + " " + bcmtNew.getBeaconIou());
						throw new DestinationException(
								HttpStatus.BAD_REQUEST,
								"This Beacon details already exists.."+bcmtNew.getBeaconCustomerName() +" " +bcmtNew.getCustomerGeography() + " " + bcmtNew.getBeaconIou());
					}
					if(isBeaconCustomerModifiedFlag == true){
						beaconRepository.save(bcmtNew);
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
		boolean isRevenueCustomergeographyModifiedFlag = false;
		boolean isRevenueCustomeriouModifiedFlag = false;
		for(RevenueCustomerMappingT rcmtOld : oldRevenueObj){
			for(RevenueCustomerMappingT rcmtNew : revenueCustomerMappingTs){
				if((rcmtNew.getRevenueCustomerMapId() != null) && (rcmtNew.getRevenueCustomerMapId().equals(rcmtOld.getRevenueCustomerMapId()))){
					isRevenueCustomerModifiedFlag = false;
					if (!rcmtNew.getFinanceCustomerName().equals(rcmtOld.getFinanceCustomerName())) {
						isRevenueCustomerModifiedFlag =true;
					}
					if (!rcmtNew.getFinanceIou().equals(rcmtOld.getFinanceIou())) {
						isRevenueCustomeriouModifiedFlag =true;
					}
					if (!rcmtNew.getCustomerGeography().equals(rcmtOld.getCustomerGeography())) {
						isRevenueCustomergeographyModifiedFlag =true;
					}

					String financeCustomername = isRevenueCustomerModifiedFlag?rcmtNew.getFinanceCustomerName():rcmtOld.getFinanceCustomerName();
					String financeiou = isRevenueCustomeriouModifiedFlag?rcmtNew.getFinanceIou():rcmtOld.getFinanceIou();
					String financegeo = isRevenueCustomergeographyModifiedFlag?rcmtNew.getCustomerGeography():rcmtOld.getCustomerGeography();
					financeCustomers = revenueRepository.findByFinanceCustomerNameAndCustomerGeographyAndFinanceIou(financeCustomername,financegeo,financeiou);
					
					if(!financeCustomers.isEmpty() && isRevenueCustomerModifiedFlag == true){
						logger.error("This Revenue details already exists.."+rcmtNew.getFinanceCustomerName() +" " +rcmtNew.getCustomerGeography() + " " + rcmtNew.getFinanceIou());
						throw new DestinationException(
								HttpStatus.BAD_REQUEST,
								"This Revenue details already exists.."+rcmtNew.getFinanceCustomerName() +" " +rcmtNew.getCustomerGeography() + " " + rcmtNew.getFinanceIou());
					}

					if(isRevenueCustomerModifiedFlag == true ){
						revenueRepository.save(rcmtNew);
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
				throw new DestinationException(HttpStatus.FORBIDDEN, "user is not authorised to update the customer name" );
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
				throw new DestinationException(HttpStatus.FORBIDDEN, "user is not authorised to update the geography" );
			}

		}
		//group customer name 
		String groupCustomerName = customerMaster.getGroupCustomerName();
		if (!groupCustomerName.equals(oldCustomerObj.getGroupCustomerName())) {
			workflowService.saveGroupCustomer(groupCustomerName);
			oldCustomerObj.setGroupCustomerName(groupCustomerName);
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
				throw new DestinationException(HttpStatus.FORBIDDEN, "user is not authorised to update the iou" );
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
		String userRole = userRepository.findByUserId(DestinationUtils.getCurrentUserDetails().getUserId()).getUserRole();
		CustomerMasterT copiedObject = (CustomerMasterT) DestinationUtils.copy(requestedCustomerT);
		// true in case of admin: to validate the iou field for not empty check
		customerToBeSaved = validateCustomerMasterDetails(requestedCustomerT);
		// Edit access for Finance and Beacon details to Strategic group admin 
		if (UserRole.valueOf(UserRole.getName(userRole)).equals(UserRole.STRATEGIC_GROUP_ADMIN)){
		validateFinanceAndBeaconDetails(copiedObject,customerToBeSaved);
		}
		return customerToBeSaved;
	}

	private CustomerMasterT validateFinanceAndBeaconDetails(
			CustomerMasterT copiedRequestedCustomerT, CustomerMasterT customerToBeSaved) {
		List<RevenueCustomerMappingT> revenueCustomerMappingTs = new ArrayList<RevenueCustomerMappingT>();
		List<BeaconCustomerMappingT> beaconCustomerMappingTs = new ArrayList<BeaconCustomerMappingT>();
		revenueCustomerMappingTs = copiedRequestedCustomerT.getRevenueCustomerMappingTs();

		if (CollectionUtils.isNotEmpty(revenueCustomerMappingTs)) {
			revenueCustomerMappingTs = validateRevenueCustomerDetails(revenueCustomerMappingTs, copiedRequestedCustomerT.getCustomerId());
		} 
		customerToBeSaved.setRevenueCustomerMappingTs(revenueCustomerMappingTs);
		beaconCustomerMappingTs = copiedRequestedCustomerT
				.getBeaconCustomerMappingTs();
		if (CollectionUtils.isNotEmpty(beaconCustomerMappingTs)) {
			beaconCustomerMappingTs = validateBeaconCustomerDetails(beaconCustomerMappingTs,copiedRequestedCustomerT.getCustomerId());

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
		String userRole = userRepository.findByUserId(DestinationUtils.getCurrentUserDetails().getUserId()).getUserRole();
		CustomerMasterT oldCustomerObj = customerRepository.findOne(customerId);
		List<RevenueCustomerMappingT> oldRevenueObj =  revenueRepository.findByCustomerId(customerId);
		List<BeaconCustomerMappingT> oldBeaconObj = beaconCustomerMappingRepository.findByCustomerId(customerId);
		// updated customer object is saved to the database
		if(isCustomerMasterModified(oldCustomerObj, customerMaster,isBdmFlag)){
			customerRepository.save(oldCustomerObj);
		}
		if (UserRole.valueOf(UserRole.getName(userRole)).equals(UserRole.STRATEGIC_GROUP_ADMIN)){

		if(customerMaster.getRevenueCustomerMappingTs()!=null)
		{
			isRevenueModified(oldRevenueObj, customerMaster.getRevenueCustomerMappingTs());
		}
		if(customerMaster.getBeaconCustomerMappingTs()!=null)
		{
			isBeaconModied(oldBeaconObj, customerMaster.getBeaconCustomerMappingTs());
		}
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

	/**
	 * This method is used to find the user details related to customer
	 * 
	 * @param customerId
	 * @param page
	 * @param count
	 * @return
	 */
	public PaginatedResponse searchUserDetailsForCustomer(String customerId, int page, int count) {
		logger.info("Begin:Inside searchUserDetailsForCustomer CustomerService");
		List<UserT> userTs = new ArrayList<UserT>();
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		
		userTs = userRepository.findUsersByCustomerId(Constants.CUSTOMER, customerId);
		
		paginatedResponse.setTotalCount(userTs.size());

		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count, userTs.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count, userTs.size());
			int toIndex = PaginationUtils.getEndIndex(page, count, userTs.size()) + 1;
			userTs = userTs.subList(fromIndex, toIndex);
			paginatedResponse.setUserTs(userTs);
			logger.debug("users after pagination size is " + userTs.size());
		} else {
			logger.info("No users available for the specified page");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No users available for the specified page");
		}
		return paginatedResponse;
	}

	/**
	 * Service to fetch the customer related information based on search type and the search keyword
	 * @param smartSearchType
	 * @param term
	 * @param getAll
	 * @param page
	 * @param count
	 * @return
	 */
	public PageDTO<SearchResultDTO<CustomerMasterT>> smartSearch(SmartSearchType smartSearchType,
			String term, boolean getAll, int page, int count) {
		logger.info("CustomerService::smartSearch type {}",smartSearchType);
		PageDTO<SearchResultDTO<CustomerMasterT>> res = new PageDTO<SearchResultDTO<CustomerMasterT>>();
		List<SearchResultDTO<CustomerMasterT>> resList = Lists.newArrayList();
		SearchResultDTO<CustomerMasterT> searchResultDTO = new SearchResultDTO<CustomerMasterT>();
		if(smartSearchType != null) {
			
			switch(smartSearchType) {
			case ALL:
				resList.add(getCustomersByGrpCustName(term, getAll));
				resList.add(getCustomersByName(term, getAll));
				resList.add(getCustomersByGeography(term, getAll));
				resList.add(getCustomersByIou(term, getAll));
				break;
			case GROUP_CUSTOMER_NAME:
				searchResultDTO = getCustomersByGrpCustName(term, getAll);
				break;
			case NAME:
				searchResultDTO = getCustomersByName(term, getAll);
				break;
			case GEOGRAPHY:
				searchResultDTO = getCustomersByGeography(term, getAll);
				break;
			case IOU:
				searchResultDTO = getCustomersByIou(term, getAll);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid search type");

			}
			
			if(smartSearchType != SmartSearchType.ALL) {//paginate the result if it is fetching entire record(ie. getAll=true)
				if(getAll) {
					List<CustomerMasterT> values = searchResultDTO.getValues();
					List<CustomerMasterT> records = PaginationUtils.paginateList(page, count, values);
					if(CollectionUtils.isNotEmpty(records)) {
						try {
							prepareCustomerDetails(records);
						} catch (Exception e) {
							logger.error("error::smartSearch::prepareCustomerDetails",e);
						}
					}
					searchResultDTO.setValues(records);
					res.setTotalCount(values.size());
				}
				resList.add(searchResultDTO);
			}
		}
		res.setContent(resList);
		return res;
	}

	private SearchResultDTO<CustomerMasterT> getCustomersByGrpCustName(
			String term, boolean getAll) {
		List<CustomerMasterT> records = customerRepository.getCustomersByGrpCustName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.GROUP_CUSTOMER_NAME, getAll);
	}

	private SearchResultDTO<CustomerMasterT> getCustomersByName(String term,
			boolean getAll) {
		List<CustomerMasterT> records = customerRepository.getCustomersByName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.NAME, getAll);
	}

	private SearchResultDTO<CustomerMasterT> getCustomersByGeography(
			String term, boolean getAll) {
		List<CustomerMasterT> records = customerRepository.getCustomersByGeography("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.GEOGRAPHY, getAll);
	}

	private SearchResultDTO<CustomerMasterT> getCustomersByIou(String term,
			boolean getAll) {
		List<CustomerMasterT> records = customerRepository.getCustomersByIou("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.IOU, getAll);
	}
	
	/**
	 * creates {@link SearchResultDTO} from the list of customers
	 * @param records
	 * @param type
	 * @param getAll
	 * @return
	 */
	private SearchResultDTO<CustomerMasterT> createSearchResultFrom(
			List<CustomerMasterT> records, SmartSearchType type, boolean getAll) {
		SearchResultDTO<CustomerMasterT> conRes = new SearchResultDTO<CustomerMasterT>();
		conRes.setSearchType(type);
		conRes.setValues(records);
		return conRes;
	}
	
	private void setCountForGrpCustomer(GroupCustomerT groupCustomerT,
			Date startDate, Date endDate, String type,
			List<String> privilegedCustomerNames, boolean isStrategicInitiatives) {
		int connectCount = 0, cxoCount = 0, othersCount = 0, associates = 0, deWons = 0, nonDeWons = 0, oppWins = 0, oppLoss = 0, prospecting = 0, pipeline = 0, opportunitiesCount = 0;
		BigDecimal revenue = new BigDecimal(0);
		BigDecimal consultingRevenue = new BigDecimal(0);
		BigDecimal grossMargin = new BigDecimal(0);
		BigDecimal cost = new BigDecimal(0);
		BigDecimal winValue = new BigDecimal(0);
		BigDecimal lossValue = new BigDecimal(0);
		BigDecimal winRatio = new BigDecimal(0);
		String financialYear = DateUtils.getCurrentFinancialYear();
		List<CustomerMasterT> customerMasterTs = groupCustomerT
				.getCustomerMasterTs();
		for (CustomerMasterT customerMasterT : customerMasterTs) {
			//Checking if it is a privileged customer
			boolean privilegedCustomer = isPrivilegedCustomer(
					privilegedCustomerNames, customerMasterT.getCustomerName(),
					isStrategicInitiatives);
			// Connects
			if (type.equals(Constants.CUSTOMER_TYPE_CONNECTS) || type.equals("ALL")) {
				List<ConnectT> connectTs = customerMasterT.getConnectTs();
				for (ConnectT connectT : connectTs) {
					Timestamp startDatetimeOfConnect = connectT
							.getStartDatetimeOfConnect();
					if (checkIfDateBetween(startDate, endDate,
							startDatetimeOfConnect)) {
						// if Cxo flag is true, then it is a cXo Connect
						if (connectT.isCxoFlag()) {
							cxoCount++;
						} else {
							othersCount++;
						}
					}
				}
			}

			List<RevenueCustomerMappingT> revenueMapping = customerMasterT
					.getRevenueCustomerMappingTs();
			if (CollectionUtils.isNotEmpty(revenueMapping)) {
				for (RevenueCustomerMappingT revenueCustomerMappingT : revenueMapping) {
					// Associates
					if (type.equals(Constants.CUSTOMER_TYPE_ASSOCIATES) || type.equals("ALL")) {
						List<CustomerAssociateT> customerAssociates = revenueCustomerMappingT
								.getCustomerAssociateTs();
						associates = associates + customerAssociates.size();
						for (CustomerAssociateT associate : customerAssociates) {
							if (checkIfDateBetween(startDate, endDate,
									associate.getCreatedDate())) {
								if (Constants.ALLOCATION_TYPE_WON.equals(associate.getAllocationCategory())&&Constants.SP_DESS.equals(associate.getSp())) {
									deWons++;
								} else {
									nonDeWons++;
								}
							}
						}
					}
					// Consulting
					if ((type.equals(Constants.CUSTOMER_TYPE_CONSULTING) || type.equals("ALL"))
							&& (privilegedCustomer)) {
						List<ActualRevenuesDataT> revenueData = revenueCustomerMappingT
								.getActualRevenuesDataTs();
						for (ActualRevenuesDataT actualRevenue : revenueData) {
							//getting revenue data only for cuurent financial year
							if (StringUtils.equals(
									actualRevenue.getFinancialYear(),
									financialYear)) {
								BigDecimal revenueInUSD = beaconConverterService
										.convert("INR", "USD",
												actualRevenue.getRevenue());
								if (actualRevenue.getCategory().equals(
										Constants.CATEGORY_REVENUE)) {
									revenue = revenue.add(revenueInUSD);
									if (StringUtils.contains(
											actualRevenue.getSubSp(), "Consulting")) {
										consultingRevenue = consultingRevenue
												.add(revenueInUSD);
									}
								} else if (actualRevenue.getCategory().equals(
										Constants.CATEGORY_COST)) {
									cost = cost.add(revenueInUSD);
								}
							}
						}
					}
				}
			}
			//Opportunities
			if (type.equals(Constants.CUSTOMER_TYPE_OPPORTUNITIES) || type.equals("ALL")) {
				List<OpportunityT> opportunities = customerMasterT
						.getOpportunityTs();
				for (OpportunityT opportunityT : opportunities) {
					switch (SalesStageCode.valueOf(opportunityT
							.getSalesStageCode())) {
					case WIN:
						//Checking deal closure date till YTD
						if (checkIfDateBetween(startDate, endDate,
								opportunityT.getDealClosureDate())) {
							if(opportunityT.getDigitalDealValue()!=null) {
								BigDecimal dealValueInUSD = beaconConverterService
										.convert(opportunityT.getDealCurrency(),
												"USD",
												opportunityT.getDigitalDealValue());
								winValue = winValue.add(dealValueInUSD);
								oppWins++;
							}
						}
						break;
					case LOST:
						if (checkIfDateBetween(startDate, endDate,
								opportunityT.getDealClosureDate())) {
							if(opportunityT.getDigitalDealValue()!=null) {
								BigDecimal dealValueInUSD = beaconConverterService
										.convert(opportunityT.getDealCurrency(),
												"USD",
												opportunityT.getDigitalDealValue());
								lossValue = lossValue.add(dealValueInUSD);
								oppLoss++;
							}
						}
						break;
					case CLOSED_AND_DISQUALIFIED:
					case CLOSED_AND_SCRAPPED:	
					case CLOSED_AND_SHELVED:
						break;
					case PROSPECTING:
					case SUSPECTING:
					case RFI_SUBMITTED:
					case RFI_IN_RESPONSE:
						prospecting++;
						break;
					default:
						pipeline++;
						break;
					}
				}
			}
		}
		//Calculating win ratio for a grp customer
		winRatio = DestinationUtils.getWinRatio(oppWins, oppLoss);
		//Calculating Gross Margin for a grp customer
		grossMargin = getGrossMargin(revenue, cost);
		//Total Connects count
		connectCount = cxoCount + othersCount;
		//Total Opportunities Count
		opportunitiesCount = pipeline + prospecting;
		groupCustomerT.setTotalConnects(connectCount);
		groupCustomerT.setCxoConnects(cxoCount);
		groupCustomerT.setOtherConnects(othersCount);
		groupCustomerT.setAssociates(associates);
		groupCustomerT.setAssociatesDE(deWons);
		groupCustomerT.setAssociatesNonDE(nonDeWons);
		groupCustomerT.setConsultingRevenue(consultingRevenue);
		groupCustomerT.setCost(cost);
		groupCustomerT.setGrossMargin(grossMargin);
		groupCustomerT.setOpportunities(opportunitiesCount);
		groupCustomerT.setPipelineOpportunities(pipeline);
		groupCustomerT.setProspectingOpportunities(prospecting);
		groupCustomerT.setTotalLoss(oppLoss);
		groupCustomerT.setTotalRevenue(revenue);
		groupCustomerT.setTotalWins(oppWins);
		groupCustomerT.setWinRatio(winRatio);
		groupCustomerT.setWinValue(winValue);
		groupCustomerT.setLossValue(lossValue);
	}

	/**
	 * checks if the given customer name is present in the privileged customer names
	 * @param privilegedCustomerNames
	 * @param customerName
	 * @param isStrategicInitiatives
	 * @return
	 */
	private boolean isPrivilegedCustomer(List<String> privilegedCustomerNames,
			String customerName, boolean isStrategicInitiatives) {
		if ((isStrategicInitiatives)
				|| (CollectionUtils.isNotEmpty(privilegedCustomerNames) && privilegedCustomerNames
						.contains(customerName))
				|| (CollectionUtils.isEmpty(privilegedCustomerNames))) {
			return true;
		}
		return false;
	}

	private BigDecimal getGrossMargin(BigDecimal revenue, BigDecimal cost) {
		BigDecimal grossMarginPercent = new BigDecimal(0);
		if(cost.compareTo(new BigDecimal(0))==0) {
			return grossMarginPercent;
		} else {
			BigDecimal grossMarginVal = revenue.subtract(cost).divide(cost, 4, BigDecimal.ROUND_HALF_UP);
			grossMarginPercent = grossMarginVal.multiply(new BigDecimal(100));
		}
		return grossMarginPercent;
	}
	
	/**
	 * Checks if the given date is in between the two dates provided.
	 * @param startDate
	 * @param endDate
	 * @param dateToCheck
	 * @return
	 */
	private boolean checkIfDateBetween(Date startDate, Date endDate,
			Date dateToCheck) {
		
		if (dateToCheck != null && dateToCheck.after(startDate)
				&& dateToCheck
						.before(endDate)) {
			return true;
		}
		return false;
	}

	public PageDTO<GroupCustomerDTO> getGrpCustomersByType(
			CustomerListDTO customerListDTO) throws Exception {
		logger.info("Inside getGrpCustomersByType method");
		PageDTO<GroupCustomerDTO> grpCustomerDto = new PageDTO<GroupCustomerDTO>();
		
		List<String> grpCustomerNames = customerListDTO.getGroupCustomerNames();
		if(CollectionUtils.isEmpty(grpCustomerNames)) {
			grpCustomerNames = Lists.newArrayList();
			grpCustomerNames.add("");
		}
		int page = customerListDTO.getPage();
		int count = 15;
		if(customerListDTO.getCount() == -1) {
			count = (int) groupCustomerRepository.count();
		} else {
			count = customerListDTO.getCount()==0 ? 15 : customerListDTO.getCount();
		}
		
		//getting year to date (YTD) if date is not available
		Date startDate = customerListDTO.getFromDate() !=null ? customerListDTO.getFromDate() : DateUtils
				.getFinancialYrStartDate();
		Date endDate = customerListDTO.getToDate() !=null ? customerListDTO.getToDate() : new Date();
		String type = StringUtils.isEmpty(customerListDTO.getType()) ? "ALL" : customerListDTO.getType();
		String mapId = StringUtils.isEmpty(customerListDTO.getMapId()) ? "" : customerListDTO.getMapId();
		Pageable pageable = new PageRequest(page, count);
		String userId = DestinationUtils.getCurrentUserId();
		String nameWith = customerListDTO.getNameWith();
		nameWith = StringUtils.isEmpty(nameWith) ? "%%":"%"+nameWith+"%"; 
		boolean strategicInitiatives = false;
		String userGroup = DestinationUtils.getCurrentUserDetails()
				.getUserGroup();
		List<String> privilegedCustomerNames = null;
		//Not getting privileges if the user group is Strategic Intiatives
		if (userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {
			strategicInitiatives = true;
		} else {
			privilegedCustomerNames = customerDao
					.getPrivilegedCustomers(userId);
		}
		Page<GroupCustomerT> grpCustomersPage = null;
		grpCustomersPage = groupCustomerRepository.getGrpCustomersByNameWith(grpCustomerNames,nameWith,pageable);
		if (grpCustomersPage == null) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer Details not found");
		} else {
			List<GroupCustomerT> grpCustomersList = grpCustomersPage
					.getContent();

			List<GroupCustomerDTO> grpCustDTOs = Lists.newArrayList();
			if(StringUtils.isEmpty(mapId)) {
				mapId = "group-customer-count";
			}
			if (CollectionUtils.isNotEmpty(grpCustomersList)) {
				for (GroupCustomerT groupCustomerT : grpCustomersList) {
					setCountForGrpCustomer(groupCustomerT, startDate, endDate,
							type, privilegedCustomerNames, strategicInitiatives);
					GroupCustomerDTO grpCustDTO = beanMapper.map(
							groupCustomerT, GroupCustomerDTO.class,
							mapId);
					grpCustDTOs.add(grpCustDTO);
				}
			}
			grpCustomerDto.setContent(grpCustDTOs);
			grpCustomerDto.setTotalCount(grpCustomersPage.getTotalElements());
		}
		return grpCustomerDto;
	}
}