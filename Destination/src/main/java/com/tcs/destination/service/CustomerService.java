package com.tcs.destination.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.BeaconConvertorMappingT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BeaconConvertorRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoSuchCurrencyException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationQueryBuilder;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class CustomerService {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerService.class);
	
	private static final String TOP10_QUERY_PREFIX="select CMT.customer_id from customer_master_t CMT,(select RCMT.customer_name, sum(ART.revenue) from "+
			"actual_revenues_data_t ART, revenue_customer_mapping_t RCMT, "+
			"iou_customer_mapping_t ICMT, sub_sp_mapping_t SSMT "+
			"where ART.finance_customer_name = RCMT.finance_customer_name and "+
			"ART.finance_geography = RCMT.customer_geography and "+
			"ART.finance_iou = ICMT.iou and "+
			"ART.sub_sp = SSMT.actual_sub_sp and ";
	
	private static final String TOP10_QUERY_SUFFIX=") as RV where RV.customer_name=CMT.customer_name order by RV.sum desc";
	
	private static final String TOP10_QUERY_FINANCIALYEAR = " and financial_year = ";
	
	private static final String TOP10_GROUPBY = " group by RCMT.customer_name order by sum desc limit ";
	
	private static final String SINGLE_QUOTE = "'";

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeaconConvertorRepository beaconRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	@Autowired
	UserService userService;
	
	@Autowired
	DestinationQueryBuilder queryBuilder;

	public CustomerMasterT findById(String customerid) throws Exception {
		logger.debug("Inside findById Service");
		CustomerMasterT customerMasterT = customerRepository
				.findOne(customerid);
		if (customerMasterT == null) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No such Customer");
		}
		removeCyclicForLinkedContactTs(customerMasterT);
		return customerMasterT;
	}

	public List<CustomerMasterT> findTopRevenue(int count, String financialYear,String userId)
			throws Exception {
		logger.debug("Inside findTopRevenue Service");
		
		UserT user = userService.findByUserId(userId);
		String userGroup = user.getUserGroupMappingT().getUserGroup();
		if(UserGroup.contains(userGroup)){
		    //authorise the request based on user group
			switch(UserGroup.valueOf(UserGroup.getEnum(userGroup))){
			case BDM			: 
				logger.error("User is not authorised to access this service");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "User is not authorised to access this service");
			case BDM_SUPERVISOR :
				logger.error("User is not authorised to access this service");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "User is not authorised to access this service");
			default 			: 	
				//for other user groups
				List<CustomerMasterT> resultCustomerList = handleOtherUserGroups(count, financialYear, userId);
				return resultCustomerList;
			}
		} else {
			logger.error("Invalid User Group");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
		}
        }

	
	/**
	 * This method executes the query with access privileges for other user groups
	 * (apart from BDM and BDM Supervisor) and returns the list of top customers
	 * @param count
	 * @param financialYear
	 * @param userId
	 * @return
	 * @throws DestinationException
	 */
	private List<CustomerMasterT> handleOtherUserGroups(int count,
			String financialYear, String userId) throws DestinationException {
		//validate financial year and set default value
		if (financialYear.equals("")) {
			logger.debug("Financial Year is Empty");
			financialYear = DateUtils.getCurrentFinancialYear();
		}

		List<UserAccessPrivilegesT> privileges = userService.getAllPrivilegesByUserId(userId);
		String queryString = getQueryString(count,financialYear,privileges);
		Query top10QueryWithPrivilegeFilter = entityManager.createNativeQuery(queryString);
		List<Object> resultList = top10QueryWithPrivilegeFilter.getResultList();

		List<String> customerIdList = new ArrayList<String>();
		if(resultList!=null && !resultList.isEmpty()){
			for(Object custObj : resultList){
				customerIdList.add((String) custObj);
			}
		}
		
		List<CustomerMasterT> resultCustomerList = customerRepository.getCustomersByIds(customerIdList);
		if (resultCustomerList.isEmpty()) {
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,"No Relevent Data Found in the database");
		} 

		removeCyclicForLinkedContactTs(resultCustomerList);
		return resultCustomerList;
	}

	/**
	 * This method returns the query string with user access privilege restrictions added
	 * @param count
	 * @param financialYear
	 * @param privileges
	 * @return
	 * @throws DestinationException
	 */
	private String getQueryString(int count, String financialYear,
			List<UserAccessPrivilegesT> privileges) throws DestinationException {
		logger.debug("inside CustomerService - getQueryString" );
		StringBuffer queryBuffer = new StringBuffer(TOP10_QUERY_PREFIX);
		String queryClause = queryBuilder.getQueryClause(privileges);
		logger.info(" Condition clause formed : " + queryClause);
		queryBuffer.append(queryClause);
		financialYear = financialYear.replace("'", "''");
		queryBuffer.append(TOP10_QUERY_FINANCIALYEAR + SINGLE_QUOTE + financialYear + SINGLE_QUOTE);
		queryBuffer.append(TOP10_GROUPBY + count);
		queryBuffer.append(TOP10_QUERY_SUFFIX);
		return queryBuffer.toString();
	}

	public List<TargetVsActualResponse> findTargetVsActual(String name,
			String currency, String financialYear) throws Exception {
		logger.debug("Inside findTargetVsActual Service");
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
			logger.error("NOT_FOUND: No Relevent Data Found in the database");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Relevent Data Found in the database");
		}
		return tarActResponseList;
	}

	public List<CustomerMasterT> findByNameContaining(String nameWith)
			throws Exception {
		List<CustomerMasterT> custList = customerRepository
				.findByCustomerNameIgnoreCaseContainingOrderByCustomerNameAsc(nameWith);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer found");
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	public List<CustomerMasterT> findByGroupCustomerName(String groupCustName)
			throws Exception {
		logger.debug("Inside findByGroupCustomerName Service");
		List<CustomerMasterT> custList = customerRepository
				.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(groupCustName);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer found");
		}
		removeCyclicForLinkedContactTs(custList);
		return custList;
	}

	public List<CustomerMasterT> findByNameStarting(String startsWith)
			throws Exception {
		List<CustomerMasterT> custList = customerRepository
				.findByCustomerNameIgnoreCaseStartingWithOrderByCustomerNameAsc(startsWith);
		if (custList.isEmpty()) {
			logger.error("NOT_FOUND: No such Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Customer found");
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