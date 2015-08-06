package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

import scala.Array;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchCurrencyException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;

@Service
public class CustomerService {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerService.class);
	
	private static final String TOP_REVENUE_QUERY_PREFIX = 
			"select CMT.customer_id from customer_master_t CMT,(select RCMT.customer_name, sum(ART.revenue) from "+
			"actual_revenues_data_t ART, revenue_customer_mapping_t RCMT, "+
			"iou_customer_mapping_t ICMT, sub_sp_mapping_t SSMT "+
			"where ART.finance_customer_name = RCMT.finance_customer_name and "+
			"ART.finance_geography = RCMT.customer_geography and "+
			"ART.finance_iou = ICMT.iou and "+
			"ART.sub_sp = SSMT.actual_sub_sp";
	
	private static final String TOP_REVENUE_QUERY_SUFFIX = ") as RV where RV.customer_name=CMT.customer_name order by RV.sum desc";
	private static final String TOP_REVENUE_QUERY_YEAR = " and ART.financial_year = ";
	private static final String TOP_REVENUE_QUERY_GROUP_BY = " group by RCMT.customer_name order by sum desc limit ";
	private static final String TOP_REVENUE_GEO_COND_PREFIX = "RCMT.customer_geography in (";
	private static final String TOP_REVENUE_SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String TOP_REVENUE_IOU_COND_PREFIX = "ICMT.display_iou in (";
	private static final String TOP_REVENUE_CUSTOMER_COND_PREFIX = "RCMT.customer_name in (";

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	public CustomerMasterT findById(String customerId) throws Exception {
		logger.debug("Inside findById() service");
		CustomerMasterT customerMasterT = customerRepository
				.findOne(customerId);
		if (customerMasterT == null) {
			logger.error("NOT_FOUND: Customer not found: {}", customerId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found: " + customerId);
		}
		removeCyclicForLinkedContactTs(customerMasterT);
		return customerMasterT;
	}

	/**
	 * This service is used to find Top revenue customers based on user's access privileges.
	 * 
	 * @param userId, year, count.
	 * @return Top revenue customers.
	 */
	public List<CustomerMasterT> findTopRevenue(String userId, String financialYear, int count)
			throws Exception {
		logger.debug("Inside findTopRevenue() service");
		UserT user = userService.findByUserId(userId);
		if (user == null) {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
		} else {
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (UserGroup.contains(userGroup)) {
			    // Validate user group, BDM's & BDM supervisor's are not authorized for this service 
				switch(UserGroup.valueOf(UserGroup.getName(userGroup))) {
					case BDM: 
					case BDM_SUPERVISOR:
						logger.error("User is not authorized to access this service");
						throw new DestinationException(HttpStatus.FORBIDDEN, "User is not authorised to access this service");
					default: 	
						// Validate financial year and set default value
						if (financialYear.isEmpty()) {
							logger.debug("Financial year is empty");
							financialYear = DateUtils.getCurrentFinancialYear();
						}
						List<CustomerMasterT> resultCustomerList = getTopRevenuesBasedOnUserPrivileges(userId, financialYear, count);
						return resultCustomerList;
				}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
			}
        }
	}

	/**
	 * This method forms and executes the query to find Top revenue customers based on user access privileges
	 * 
	 * @param userId
	 * @param financialYear
	 * @param count
	 * @return Top revenue customers
	 * @throws Exception
	 */
	private List<CustomerMasterT> getTopRevenuesBasedOnUserPrivileges(String userId, 
			String financialYear, int count) throws Exception {
		logger.debug("Inside getTopRevenuesBasedOnUserPrivileges() method");
		// Form the native top revenue query string
		String queryString = getRevenueQueryString(userId, count, financialYear);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query topRevenueQuery = entityManager.createNativeQuery(queryString);
		List<Object> resultList = topRevenueQuery.getResultList();

		// Get the list of customer ids
		List<String> customerIdList = new ArrayList<String>();
		if (resultList != null && !resultList.isEmpty()) {
			for (Object custObj : resultList) {
				customerIdList.add((String) custObj);
			}
		}
		
		// Retrieve customer details
		List<CustomerMasterT> customerDetailsList = customerRepository.getCustomersByIds(customerIdList);
		if (customerDetailsList == null || customerDetailsList.isEmpty()) {
			logger.error("NOT_FOUND: Top revenue customers not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,"Top revenue customers not found");
		} 

		removeCyclicForLinkedContactTs(customerDetailsList);
		return customerDetailsList;
	}

	/**
	 * This method returns the query string with user access privilege restrictions added
	 * @param count
	 * @param financialYear
	 * @param privileges
	 * @return
	 * @throws DestinationException
	 */
	private String getRevenueQueryString(String userId, int count, String financialYear) throws Exception {
		logger.debug("Inside getRevenueQueryString() method" );
		StringBuffer queryBuffer = new StringBuffer(TOP_REVENUE_QUERY_PREFIX);
		// Get user access privilege groups 
		HashMap<String, String> queryPrefixMap = 
				userAccessPrivilegeQueryBuilder.getQueryPrefixMap(TOP_REVENUE_GEO_COND_PREFIX, TOP_REVENUE_SUBSP_COND_PREFIX, 
						TOP_REVENUE_IOU_COND_PREFIX, TOP_REVENUE_CUSTOMER_COND_PREFIX);
		// Get WHERE clause string
		String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) { 
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		financialYear = financialYear.replace(Constants.SINGLE_QUOTE, Constants.DOUBLE_SINGLE_QUOTE);
		queryBuffer.append(TOP_REVENUE_QUERY_YEAR + Constants.SINGLE_QUOTE 
				+ financialYear + Constants.SINGLE_QUOTE);
		queryBuffer.append(TOP_REVENUE_QUERY_GROUP_BY + count);
		queryBuffer.append(TOP_REVENUE_QUERY_SUFFIX);

		return queryBuffer.toString();
	}

	public List<TargetVsActualResponse> findTargetVsActual(String name,
			String currency, String financialYear) throws Exception {
		logger.debug("Inside findTargetVsActual() service");
		BeaconConvertorMappingT beacon = beaconRepository
				.findByCurrencyName(currency);
		if (beacon == null) {
			logger.error("No Such Currency Exception");
			throw new NoSuchCurrencyException();
		}
		List<TargetVsActualResponse> tarActResponseList = new ArrayList<TargetVsActualResponse>();
		if (financialYear.equals("")) {
			financialYear = DateUtils.getCurrentFinancialYear();
		}
		List<Object[]> actualList = customerRepository.findActual(name,
				financialYear);
		List<Object[]> targetList = customerRepository.findTarget(name,
				financialYear);
		for (Object[] actual : actualList) {
			TargetVsActualResponse response = new TargetVsActualResponse();
			response.setQuarter(actual[0].toString());
			response.setActual(new BigDecimal(actual[1].toString()).divide(
					beacon.getConversionRate(), 2, RoundingMode.HALF_UP));
			for (Object[] target : targetList) {
				if (target[0].toString().equals(response.getQuarter())) {
					logger.debug("Target Equals Quarter");
					response.setTarget(new BigDecimal(target[1].toString())
							.divide(beacon.getConversionRate(), 2,
									RoundingMode.HALF_UP));
				}
			}
			tarActResponseList.add(response);
		}
		if (actualList.isEmpty()) {
			for (Object[] target : targetList) {
				TargetVsActualResponse response = new TargetVsActualResponse();
				response.setQuarter(target[0].toString());
				response.setTarget(new BigDecimal(target[1].toString()).divide(
						beacon.getConversionRate(), 2, RoundingMode.HALF_UP));
				tarActResponseList.add(response);
			}
		}
		if (tarActResponseList.isEmpty()) {
			logger.error("NOT_FOUND: TargetVsActual data not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"TargetVsActual data not found");
		}
		return tarActResponseList;
	}

	public List<CustomerMasterT> findByNameContaining(String nameWith)
			throws Exception {
		logger.debug("Inside findByNameContaining() service");
		List<CustomerMasterT> custList = customerRepository
				.findByCustomerNameIgnoreCaseContainingOrderByCustomerNameAsc(nameWith);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: Customer not found with given name: {}", nameWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given name: " + nameWith);
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName)
			throws Exception {
		logger.debug("Inside findByGroupCustomerName() service");
		List<CustomerMasterT> custList = customerRepository
				.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(groupCustName);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: Customer not found with given group customer name: {}", groupCustName);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given group customer name: " + groupCustName);
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	public List<CustomerMasterT> findByNameStarting(String startsWith)
			throws Exception {
		logger.error("Starts With" + startsWith);
		List<CustomerMasterT> custList = new ArrayList<CustomerMasterT>();
		if (!startsWith.equals("@"))
			custList.addAll(customerRepository
					.findByCustomerNameIgnoreCaseStartingWithOrderByCustomerNameAsc(startsWith));
		else
			for (int i = 0; i <= 9; i++) {
				List<CustomerMasterT> customerMasterTs = customerRepository
						.findByCustomerNameIgnoreCaseStartingWithOrderByCustomerNameAsc(i
								+ "");
				custList.addAll(customerMasterTs);
			}

		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: Customer not found with given customer name: {}", startsWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Customer not found with given customer name: " + startsWith);
		}
		removeCyclicForLinkedContactTs(custList);
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

}