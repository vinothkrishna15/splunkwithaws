package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

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
import com.tcs.destination.bean.BeaconCustomerMappingTPK;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.enums.UserGroup;
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
	private static final String TOP_REVENUE_PROJECTED_SUFFIX = ") as TRC where CMT.customer_name = TRC.customer_name order by TRC.revenue desc";

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
	OpportunityService opportunityService;

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

	public CustomerMasterT findById(String customerId, String userId,
			List<String> toCurrency) throws Exception {
		logger.debug("Inside findById() service");
		CustomerMasterT customerMasterT = customerRepository
				.findOne(customerId);
		if (!userId
				.equals(DestinationUtils.getCurrentUserDetails().getUserId()))
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User Id and Login User Detail does not match");
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

	// for batch save
	public void save(List<CustomerMasterT> insertList) {

		logger.debug("Inside save method of customer service");
		customerRepository.save(insertList);

	}

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
	public List<CustomerMasterT> findTopRevenue(String userId,
			String financialYear, int count) throws Exception {
		logger.debug("Inside findTopRevenue() service");
		UserT user = userService.findByUserId(userId);
		if (user == null) {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found: " + userId);
		} else {
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (UserGroup.contains(userGroup)) {
				// Validate user group, BDM's & BDM supervisor's are not
				// authorized for this service
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
				case BDM_SUPERVISOR:
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
							userId, financialYear, count);
					return resultCustomerList;
				}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid User Group");
			}
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
		// Form the native top revenue query string
		String queryString = getRevenueQueryString(userId, count, financialYear);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query topRevenueQuery = entityManager.createNativeQuery(queryString,
				CustomerMasterT.class);
		List<CustomerMasterT> resultList = topRevenueQuery.getResultList();
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
		queryBuffer.append(reportsService.getTopRevenueCustomersForDashboard(
				userId, financialYear, count));
		queryBuffer.append(TOP_REVENUE_PROJECTED_SUFFIX);
		return queryBuffer.toString();
	}

	public List<TargetVsActualResponse> findTargetVsActual(
			String financialYear, String quarter, String customerName,
			String currency, String userId) throws Exception {
		logger.debug("Inside findTargetVsActual() service");
		if (!userId
				.equals(DestinationUtils.getCurrentUserDetails().getUserId()))
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User Id and Login User Detail doesnot match");

		ArrayList<String> customerNameList = new ArrayList<String>();
		customerNameList.add(customerName);
		customerNameList = getPreviledgedCustomerName(userId, customerNameList,
				true);

		if (customerNameList == null || customerNameList.isEmpty())
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User does not have access to view this information");
		try {
			List<TargetVsActualResponse> targetVsActualResponse = performanceReportService
					.getTargetVsActualRevenueSummary(financialYear, quarter,
							"", "", "", "", customerName, currency, "", false,
							userId);
			return targetVsActualResponse;
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User does not have access to view this information");
		}
	}

	public PaginatedResponse findByNameContaining(String nameWith, int page,
			int count) throws Exception {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		logger.debug("Inside findByNameContaining() service");
		Page<CustomerMasterT> customerPage = customerRepository
				.findByCustomerNameIgnoreCaseContainingAndCustomerNameIgnoreCaseNotLikeOrderByCustomerNameAsc(
						nameWith, Constants.UNKNOWN_CUSTOMER, pageable);
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

	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName)
			throws Exception {
		logger.debug("Inside findByGroupCustomerName() service");
		List<CustomerMasterT> custList = customerRepository
				.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(
						groupCustName, Constants.UNKNOWN_CUSTOMER);
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

	public PaginatedResponse findByNameStarting(String startsWith, int page,
			int count) throws Exception {
		// TODO: Paginated Response
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		logger.debug("Starts With" + startsWith);
		List<CustomerMasterT> custList = new ArrayList<CustomerMasterT>();
		if (!startsWith.equals("@")) {
			Page<CustomerMasterT> customerPage = customerRepository
					.findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeOrderByCustomerNameAsc(
							startsWith, Constants.UNKNOWN_CUSTOMER, pageable);
			custList.addAll(customerPage.getContent());
			paginatedResponse.setTotalCount(paginatedResponse.getTotalCount()
					+ customerPage.getTotalElements());
		} else
			for (int i = 0; i <= 9; i++) {
				Page<CustomerMasterT> customerPage = customerRepository
						.findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeOrderByCustomerNameAsc(
								i + "", Constants.UNKNOWN_CUSTOMER, pageable);
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
	 * @param nameWith
	 *            - string to be searched
	 * @param userId
	 *            - userId for which the privilege restrictions are to be
	 *            applied
	 * @return - List of distinct group customer names based on privileges
	 * @throws Exception
	 */
	public List<String> findByGroupCustomerNameBasedOnPrivilege(
			String nameWith, String userId) throws Exception {
		String queryString = null;
		List<String> resultList = null;

		if (!DestinationUtils.getCurrentUserDetails().getUserId()
				.equalsIgnoreCase(userId)) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Invalid user Id");
		} else {

			queryString = getGroupCustomerPrivilegeQueryString(userId, "'%"
					+ nameWith + "%'");
			logger.info("Query string: {}", queryString);
			// Execute the native revenue query string
			Query groupCustomerPrivilegeQuery = entityManager
					.createNativeQuery(queryString);

			resultList = groupCustomerPrivilegeQuery.getResultList();
		}

		return resultList;
	}

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
			logger.info("before save");
			customerT = customerRepository.save(customerT);
			logger.info("Customer Saved .... " + customerT.getCustomerId());
		}
		return customerT;
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
		BeaconCustomerMappingTPK beaconTPK = null;
		List<BeaconCustomerMappingT> beaconCustomers = null;
		if (beaconCustomerToInsert != null) {
			beaconT = new BeaconCustomerMappingT();
			beaconTPK = new BeaconCustomerMappingTPK();

			// to find the uniqueness of the primary key (here composite key)
			beaconCustomers = beaconRepository.findbeaconDuplicates(
					beaconCustomerToInsert.getBeaconCustomerName(),
					beaconCustomerToInsert.getBeaconIou(),
					beaconCustomerToInsert.getCustomerGeography());
			if (beaconCustomers.isEmpty()) {
				beaconT.setCustomerName(beaconCustomerToInsert
						.getCustomerName());
				beaconTPK.setBeaconCustomerName(beaconCustomerToInsert
						.getBeaconCustomerName());
				beaconTPK.setBeaconIou(beaconCustomerToInsert.getBeaconIou());
				beaconTPK.setCustomerGeography(beaconCustomerToInsert
						.getCustomerGeography());
			} else {
				logger.error("EXISTS: Beacon Already Exist!");
				throw new DestinationException(HttpStatus.CONFLICT,
						"Beacon Already Exist!");
			}
			beaconT.setId(beaconTPK);
			beaconT = beaconRepository.save(beaconT);
			logger.info("Beacon Saved .... " + "beacon primary key"
					+ beaconT.getId());
		}
		return beaconT;
	}

	public PaginatedResponse search(String groupCustomerNameWith,
			String nameWith, List<String> geography, List<String> displayIOU,
			int page, int count) throws DestinationException {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		if (geography.isEmpty())
			geography.add("");
		if (displayIOU.isEmpty())
			displayIOU.add("");
		List<CustomerMasterT> customerMasterTs = customerRepository
				.advancedSearch(
						"%" + groupCustomerNameWith.toUpperCase() + "%", "%"
								+ nameWith.toUpperCase() + "%", geography,
						displayIOU);

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

}