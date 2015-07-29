package com.tcs.destination.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.MiscTypeValueT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.MiscTypeValueRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;

@Component
public class TopRevenueQueryBuilder {

	private static final Logger logger = LoggerFactory.getLogger(TopRevenueQueryBuilder.class);

	private static final String TOP_REVENUE_GEO_COND_PREFIX = "RCMT.customer_geography in (";
	private static final String TOP_REVENUE_SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String TOP_REVENUE_IOU_COND_PREFIX = "ICMT.display_iou in (";
	private static final String TOP_REVENUE_CUSTOMER_COND_PREFIX = "RCMT.customer_name in (";
	private static final String AND_CLAUSE = " and ";
	private static final String OR_CLAUSE = " or ";
	
	@Autowired
	MiscTypeValueRepository miscTypeRepo;
	
	@Autowired
	CustomerRepository customerRepo;
	
	/**
	 * This method returns the user access privilege clause for the top revenue query
	 * @param userPrivilegesList
	 * @return String
	 * @throws DestinationException
	 */
	public String getTopRevenueQueryClause(List<UserAccessPrivilegesT> userPrivilegesList) throws Exception {
		logger.debug("Inside getQueryString() method" );
		
		// Create a Privilege group for each Parent Privileges 
		// and initialize group conditions for each parent 
		List<PrivilegeGroup> privilegeGroups = new ArrayList<PrivilegeGroup>();
		for (UserAccessPrivilegesT parentPrivilege : userPrivilegesList) {
			PrivilegeGroup privilegeGroup = new PrivilegeGroup();
			initGroupConditions(privilegeGroup);
			populateConditionGroup(parentPrivilege, privilegeGroup);
			replaceLastCommaWithParenthesisInQueryCondition(privilegeGroup);
			privilegeGroups.add(privilegeGroup);
		}
		
		// WHERE Clause parenthesis - start 
		StringBuffer clauseBuffer = new StringBuffer(Constants.LEFT_PARANTHESIS);
		for (PrivilegeGroup group : privilegeGroups) {
			// Sub condition group parenthesis - start
			clauseBuffer.append(Constants.LEFT_PARANTHESIS);
			concatenateBuffer(clauseBuffer, group.getGeographyBuffer(), AND_CLAUSE);
			concatenateBuffer(clauseBuffer , group.getSubspBuffer(), AND_CLAUSE);
			concatenateBuffer(clauseBuffer, group.getIouBuffer(), AND_CLAUSE);
			concatenateBuffer(clauseBuffer, group.getCustomerBuffer(), AND_CLAUSE);
			// Trimming the unwanted last " and "
			clauseBuffer.setLength(clauseBuffer.length() - 5);
			// Sub condition group parenthesis - end
			clauseBuffer.append(Constants.RIGHT_PARANTHESIS);
			// Logical OR prefix for next condition group 
			clauseBuffer.append(OR_CLAUSE);
		}
		// Trimming the unwanted last " or "
		clauseBuffer.setLength(clauseBuffer.length()-4);
		// Clause parenthesis - end 
		clauseBuffer.append(Constants.RIGHT_PARANTHESIS);
		return clauseBuffer.toString();
	}

	/**
	 * This method initializes the buffers for each of the condition required 
	 * (SubSp, Iou, Geography, Customer) in the PrivilegeGroup.
	 * @param privilegeGroup
	 */
	private void initGroupConditions(PrivilegeGroup privilegeGroup) {
		logger.debug("Inside initGroupConditions() method" );
		StringBuffer geoBuffer = new StringBuffer();
		StringBuffer subspBuffer = new StringBuffer();
		StringBuffer iouBuffer = new StringBuffer();
		StringBuffer custBuffer = new StringBuffer();

		privilegeGroup.setGeographyBuffer(geoBuffer.append(TOP_REVENUE_GEO_COND_PREFIX));
		privilegeGroup.setSubspBuffer(subspBuffer.append(TOP_REVENUE_SUBSP_COND_PREFIX));
		privilegeGroup.setIouBuffer(iouBuffer.append(TOP_REVENUE_IOU_COND_PREFIX));
		privilegeGroup.setCustomerBuffer(custBuffer.append(TOP_REVENUE_CUSTOMER_COND_PREFIX));
	}

	/**
	 * This method populates the required condition group object for a parent & its child privilege groups
	 * @param privilege
	 * @param privilegeGroup
	 * @throws DestinationException
	 */
	private void populateConditionGroup(UserAccessPrivilegesT privilege,
			PrivilegeGroup privilegeGroup) throws Exception {
		logger.debug("Inside populateConditionGroup() method" );
		String privilegeType = privilege.getPrivilegeType();
		String privilegeValue = privilege.getPrivilegeValue();
		StringBuffer geoBuffer = privilegeGroup.getGeographyBuffer();
		StringBuffer subspBuffer = privilegeGroup.getSubspBuffer();
		StringBuffer iouBuffer = privilegeGroup.getIouBuffer();
		StringBuffer custBuffer = privilegeGroup.getCustomerBuffer();
		
		logger.info("Privilege Type: {}", privilegeType);
		logger.info("Privilege Value: {}", privilegeValue);
		
		// Forming condition for privilege 
		if (PrivilegeType.contains(privilegeType)) {
			switch (PrivilegeType.valueOf(privilegeType)) {
				case GEOGRAPHY:
					geoBuffer.append(Constants.SINGLE_QUOTE + privilegeValue 
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setGeographyBuffer(geoBuffer);
					break;
				case SUBSP : 
					subspBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setSubspBuffer(subspBuffer);
					break;
				case IOU: 
					iouBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
						+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setIouBuffer(iouBuffer);
					break;
				case CUSTOMER: 
					handleCustomer(privilegeGroup, privilegeValue, custBuffer);
					break;   
				case GROUP_CUSTOMER: { 
					// Get the master customer list using group customer name
					List<CustomerMasterT> customerList = 
						customerRepo.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(privilegeValue);
					if (customerList == null && customerList.isEmpty()) {
						logger.error("Customers not found for the group customer name: {}", privilegeValue);
						throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
								"Customers not found for the group customer name: " + privilegeValue);
					} else {
						for (CustomerMasterT customer : customerList) {
							custBuffer.append(Constants.SINGLE_QUOTE + customer.getCustomerName() 
									+ Constants.SINGLE_QUOTE + Constants.COMMA);
						}
						privilegeGroup.setCustomerBuffer(custBuffer);
					}
					break; 
				}
				default: {
					logger.error("Invalid Privilege Type: {}", privilegeType);
					throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
							"Invalid Privilege Type: " + privilegeType);
				}
			}

			// Forming conditions for child privileges if any
			List<UserAccessPrivilegesT> childPrivileges = privilege.getUserAccessPrivilegesTs();
			if (childPrivileges != null && !childPrivileges.isEmpty()) {
				logger.debug("Child Privileges Size: {}", childPrivileges.size());
				for (UserAccessPrivilegesT childPrivilege : childPrivileges) {
					populateConditionGroup(childPrivilege, privilegeGroup);
				}
			} else {
				return;
			}
		} else {
			logger.error("Invalid Privilege Type: {}", privilegeType);
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Invalid Privilege Type: " + privilegeType);
		}
	}

	/**
	 * This method populates the customer condition (global customer, if required)
	 * @param privilegeGroup
	 * @param privilegeValue
	 * @param customerBuffer
	 */
	private void handleCustomer(PrivilegeGroup privilegeGroup,
			String privilegeValue, StringBuffer customerBuffer) {
		logger.debug("Inside handleCustomer() method");
		if (!privilegeValue.equals(Constants.GLOBAL)) {
			// Master customers
			customerBuffer.append(Constants.SINGLE_QUOTE + privilegeValue + Constants.SINGLE_QUOTE + Constants.COMMA);
			privilegeGroup.setCustomerBuffer(customerBuffer);
		} else {
			// GLOBAL_CUSTOMERS - Get the list from Misc_Type_Value_T
			List<MiscTypeValueT> miscList = miscTypeRepo.findByType(Constants.GLOBAL);
			for (MiscTypeValueT miscItem : miscList) {
				List<CustomerMasterT> custList = 
						customerRepo.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(miscItem.getValue());
				// Value is Group customer name
				if (custList != null && !custList.isEmpty()) {
					for (CustomerMasterT customer : custList) {
						customerBuffer.append(Constants.SINGLE_QUOTE + customer.getCustomerName() 
								+ Constants.SINGLE_QUOTE + Constants.COMMA);
					}
				} else {
					// Value is Master customer name
					customerBuffer.append(Constants.SINGLE_QUOTE + miscItem.getValue()
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
				}
			}
			privilegeGroup.setCustomerBuffer(customerBuffer);
		}
	}

	/**
	 * This method concatenates the parent clause and the other clauses with the required condition("and")
	 * @param clauseBuffer
	 * @param condBuffer
	 * @param joinString
	 */
	private void concatenateBuffer(StringBuffer clauseBuffer,
			StringBuffer condBuffer, String joinString) {
		logger.debug("Inside concatenateBuffer() method");
		// Need not check for length as it is always initialized with condition prefix
		if (condBuffer != null) {
			String condStr = condBuffer.toString();
			// Append condition string and join string after last paranthesis  
			if (condStr.contains(Constants.RIGHT_PARANTHESIS)) {
				clauseBuffer.append(condBuffer);
				clauseBuffer.append(joinString);
			}
		}
		logger.debug("Clause Buffer: " + clauseBuffer.toString());
	}

	/**
	 * This method trims the unwanted comma and closes the condition set with parenthesis
	 * @param privilegeGroup
	 */
	private void replaceLastCommaWithParenthesisInQueryCondition(PrivilegeGroup privilegeGroup) {
		logger.debug("Inside handleParenthesis() method");
		StringBuffer geoBuffer = privilegeGroup.getGeographyBuffer();
		if (geoBuffer.indexOf(Constants.COMMA) != -1) {
			geoBuffer.replace(geoBuffer.length()-1, geoBuffer.length(), Constants.RIGHT_PARANTHESIS);
			privilegeGroup.setGeographyBuffer(geoBuffer);
		}
		StringBuffer subspBuffer = privilegeGroup.getSubspBuffer();
		if (subspBuffer.indexOf(Constants.COMMA) != -1) {
			subspBuffer.replace(subspBuffer.length()-1, subspBuffer.length(), Constants.RIGHT_PARANTHESIS);
			privilegeGroup.setSubspBuffer(subspBuffer);
		}
		
		StringBuffer iouBuffer = privilegeGroup.getIouBuffer();
		if (iouBuffer.indexOf(Constants.COMMA) != -1) {
			iouBuffer.replace(iouBuffer.length()-1, iouBuffer.length(), Constants.RIGHT_PARANTHESIS);
			privilegeGroup.setIouBuffer(iouBuffer);
		}
		
		StringBuffer custBuffer = privilegeGroup.getCustomerBuffer();
		if (custBuffer.indexOf(Constants.COMMA) != -1) {
			custBuffer.replace(custBuffer.length()-1, custBuffer.length(), Constants.RIGHT_PARANTHESIS);
			privilegeGroup.setCustomerBuffer(custBuffer);
		}
	}
}