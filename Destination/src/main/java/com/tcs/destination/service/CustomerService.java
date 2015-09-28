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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;

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
	BeaconConvertorRepository beaconRepository;

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

	public CustomerMasterT findById(String customerId,String userId, List<String> toCurrency) throws Exception {
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
		beaconConverterService.convertOpportunityCurrency(customerMasterT.getOpportunityTs(), toCurrency);
		return customerMasterT;
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
		return performanceReportService.getTargetVsActualRevenueSummary(
				financialYear, quarter, "", "","", "", customerName, currency, "",false);
	}

	public List<CustomerMasterT> findByNameContaining(String nameWith)
			throws Exception {
		logger.debug("Inside findByNameContaining() service");
		List<CustomerMasterT> custList = customerRepository
				.findByCustomerNameIgnoreCaseContainingAndCustomerNameIgnoreCaseNotLikeOrderByCustomerNameAsc(nameWith, Constants.UNKNOWN_CUSTOMER);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: Customer not found with given name: {}",
					nameWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given name: " + nameWith);
		}
		prepareCustomerDetails(custList);
		return custList;
	}

	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName)
			throws Exception {
		logger.debug("Inside findByGroupCustomerName() service");
		List<CustomerMasterT> custList = customerRepository.
				findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(groupCustName, Constants.UNKNOWN_CUSTOMER);
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

	public List<CustomerMasterT> findByNameStarting(String startsWith)
			throws Exception {
		logger.debug("Starts With" + startsWith);
		List<CustomerMasterT> custList = new ArrayList<CustomerMasterT>();
		if (!startsWith.equals("@"))
			custList.addAll(customerRepository
					.findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeOrderByCustomerNameAsc(startsWith, Constants.UNKNOWN_CUSTOMER));
		else
			for (int i = 0; i <= 9; i++) {
				List<CustomerMasterT> customerMasterTs = customerRepository
						.findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeOrderByCustomerNameAsc(i
								+ "", Constants.UNKNOWN_CUSTOMER);
				custList.addAll(customerMasterTs);
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
		return custList;
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
					customerNameQueryList += "'" + customerName.replace("\'", "\'\'") + "',";
			}
			customerNameQueryList = customerNameQueryList.substring(0,
					customerNameQueryList.length() - 1);
			customerNameQueryList += ")";

			queryBuffer.append(" CMT.customer_name in " + customerNameQueryList);
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
	 * @param nameWith - string to be searched
	 * @param userId - userId for which the privilege restrictions are to be applied 
	 * @return - List of distinct group customer names based on privileges
	 * @throws Exception
	 */
	public List<String> findByGroupCustomerNameBasedOnPrivilege(
			String nameWith, String userId) throws Exception {
		// TODO Auto-generated method stub
		String queryString = null;
		List<String> resultList = null;
		
		if(!DestinationUtils.getCurrentUserDetails().getUserId().equalsIgnoreCase(userId))
		{
			throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid user Id"); 
		}
		else{
			
		queryString = getGroupCustomerPrivilegeQueryString(userId, "'%"+nameWith+"%'");
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query groupCustomerPrivilegeQuery = entityManager.createNativeQuery(queryString);
		
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
			queryBuffer.append(" and "+GROUP_CUSTOMER_NAME_CUSTOMER_COND_SUFFIX + 
					 nameWith + " and " + whereClause + ORDERBY_SUFFIX);
		}

		logger.info("queryString = " + queryBuffer.toString());
		return queryBuffer.toString();
	}

}