package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.MiscTypeValueT;
import com.tcs.destination.bean.PrivilegeGroup;
import com.tcs.destination.bean.QueryBufferDTO;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.MiscTypeValueRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.Constants;

@Component
public class UserAccessPrivilegeQueryBuilder {

	private static final Logger logger = LoggerFactory
			.getLogger(UserAccessPrivilegeQueryBuilder.class);

	@Autowired
	MiscTypeValueRepository miscTypeRepo;

	@Autowired
	CustomerRepository customerRepo;

	@Autowired
	UserService userService;
	
	HashMap<Integer,String> parameterMap=new HashMap<Integer,String>();
	
 
	/**
	 * This method initializes the buffers for each of the condition required
	 * (SubSp, Iou, Geography, Customer) in the PrivilegeGroup.
	 * 
	 * @param userId
	 *            , queryPrefix
	 * @return String
	 * @throws Exception
	 */
	public String getUserAccessPrivilegeWhereConditionClause(String userId,
			HashMap<String, String> queryPrefix) throws Exception {
		logger.debug("Inside getUserAccessPrivilegeWhereConditionClause() method");
		// Get user access privileges
		List<UserAccessPrivilegesT> userPrivilegesList = userService
				.getAllPrivilegesByUserId(userId);

		// Create a Privilege group for each Parent Privileges
		// and initialize group conditions for each parent
		List<PrivilegeGroup> privilegeGroups = new ArrayList<PrivilegeGroup>();

		if (userPrivilegesList != null && !userPrivilegesList.isEmpty()) {
			for (UserAccessPrivilegesT parentPrivilege : userPrivilegesList) {
				PrivilegeGroup privilegeGroup = new PrivilegeGroup();
				initGroupConditions(privilegeGroup, queryPrefix);
				populateConditionGroup(parentPrivilege, privilegeGroup);
				replaceLastCommaWithParenthesisInQueryCondition(privilegeGroup);
				privilegeGroups.add(privilegeGroup);
			}
			String where = getWhereClauseString(privilegeGroups);
			if (!where.equalsIgnoreCase("(())"))
				return where;
			else
				return null;
		} else {
			logger.info("User does not have any specific access privileges defined");
			return null;
		}
	}
	
	/**
	 * This method initializes the buffers for each of the condition required
	 * (SubSp, Iou, Geography, Customer) in the PrivilegeGroup.
	 * 
	 * @param userId
	 *            , queryPrefix
	 * @return String
	 * @throws Exception
	 */
	public QueryBufferDTO getUserAccessPrivilegeWhereCondition(String userId,
			HashMap<String, String> queryPrefix) throws Exception {
		logger.debug("Inside getUserAccessPrivilegeWhereConditionClause() method");
		int startcount=1;
		
		QueryBufferDTO queryBuffer=new QueryBufferDTO();
		
		// Get user access privileges
		List<UserAccessPrivilegesT> userPrivilegesList = userService
				.getAllPrivilegesByUserId(userId);

		// Create a Privilege group for each Parent Privileges
		// and initialize group conditions for each parent
		List<PrivilegeGroup> privilegeGroups = new ArrayList<PrivilegeGroup>();

		if (userPrivilegesList != null && !userPrivilegesList.isEmpty()) {
			for (UserAccessPrivilegesT parentPrivilege : userPrivilegesList) {
				
				PrivilegeGroup privilegeGroup = new PrivilegeGroup();
				initGroupConditions(privilegeGroup, queryPrefix);
				populateCondGroup(parentPrivilege, privilegeGroup,startcount);
				replaceLastCommaWithParenthesisInQueryCondition(privilegeGroup);
				startcount++;
				privilegeGroups.add(privilegeGroup);
			}
			String where = getWhereClauseString(privilegeGroups);
			if (!where.equalsIgnoreCase("(())"))
			{
				queryBuffer.setQuery(where);
			    queryBuffer.setParameterMap(parameterMap);
			    return queryBuffer;
			}
			else
			{
				return null;
			}
			
			
		} else {
			logger.info("User does not have any specific access privileges defined");
			return null;
		}
	
	}

	/**
	 * This method initializes the buffers for each of the condition required
	 * (SubSp, Iou, Geography, Customer) in the PrivilegeGroup.
	 * 
	 * @param privilegeGroup
	 *            , queryPrefix
	 */
	private void initGroupConditions(PrivilegeGroup privilegeGroup,
			HashMap<String, String> queryPrefix) throws Exception {
		logger.debug("Inside initGroupConditions() method");
		if (privilegeGroup != null && queryPrefix != null) {
			if (queryPrefix.get(PrivilegeType.GEOGRAPHY.name()) != null) {
				StringBuffer geoBuffer = new StringBuffer();
				privilegeGroup.setGeographyBuffer(geoBuffer.append(queryPrefix
						.get(PrivilegeType.GEOGRAPHY.name())));
			}
			if (queryPrefix.get(PrivilegeType.SUBSP.name()) != null) {
				StringBuffer subspBuffer = new StringBuffer();
				privilegeGroup.setSubspBuffer(subspBuffer.append(queryPrefix
						.get(PrivilegeType.SUBSP.name())));
			}
			if (queryPrefix.get(PrivilegeType.IOU.name()) != null) {
				StringBuffer iouBuffer = new StringBuffer();
				privilegeGroup.setIouBuffer(iouBuffer.append(queryPrefix
						.get(PrivilegeType.IOU.name())));
			}
			if (queryPrefix.get(PrivilegeType.CUSTOMER.name()) != null) {
				StringBuffer custBuffer = new StringBuffer();
				privilegeGroup.setCustomerBuffer(custBuffer.append(queryPrefix
						.get(PrivilegeType.CUSTOMER.name())));
			}
		}
	}

	/**
	 * This method populates the required condition group object for a parent &
	 * its child privilege groups
	 * 
	 * @param privilege
	 *            , privilegeGroup
	 * @throws Exception
	 */
	private void populateConditionGroup(UserAccessPrivilegesT privilege,
			PrivilegeGroup privilegeGroup) throws Exception {
		logger.debug("Inside populateConditionGroup() method");
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
				if (geoBuffer != null && geoBuffer.length() > 0) {
					geoBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setGeographyBuffer(geoBuffer);
				}
				break;
			case SUBSP:
				if (subspBuffer != null && subspBuffer.length() > 0) {
					subspBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setSubspBuffer(subspBuffer);
				}
				break;
			case IOU:
				if (iouBuffer != null && iouBuffer.length() > 0) {
					iouBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setIouBuffer(iouBuffer);
				}
				break;
			case CUSTOMER:
				if (custBuffer != null && custBuffer.length() > 0) {
					handleCustomer(privilegeGroup, privilegeValue, custBuffer);
				}
				break;
			case GROUP_CUSTOMER: {
				// Get the master customer list using group customer name
				List<CustomerMasterT> customerList = customerRepo
						.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(privilegeValue, Constants.UNKNOWN_CUSTOMER);
				if (customerList == null && customerList.isEmpty()) {
					logger.error(
							"Customers not found for the group customer name: {}",
							privilegeValue);
					throw new DestinationException(
							HttpStatus.INTERNAL_SERVER_ERROR,
							"Customers not found for the group customer name: "
									+ privilegeValue);
				} else {
					if (custBuffer != null && custBuffer.length() > 0) {
						for (CustomerMasterT customer : customerList) {
							custBuffer.append(Constants.SINGLE_QUOTE
									+ customer.getCustomerName()
									+ Constants.SINGLE_QUOTE + Constants.COMMA);
						}
					}
					privilegeGroup.setCustomerBuffer(custBuffer);
				}
				break;
			}
			default: {
				logger.error("Invalid Privilege Type: {}", privilegeType);
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Invalid Privilege Type: " + privilegeType);
			}
			}

			// Forming conditions for child privileges if any
			//List<UserAccessPrivilegesT> childPrivileges = privilege.getUserAccessPrivilegesTs();
			List<UserAccessPrivilegesT> childPrivileges = 
					userService.getAllChildPrivilegesByUserIdAndParentPrivilegeId(privilege.getUserId(), privilege.getPrivilegeId());

			if (childPrivileges != null && !childPrivileges.isEmpty()) {
				logger.debug("Child Privileges Size: {}",
						childPrivileges.size());
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
	 * This method populates the required condition group object for a parent &
	 * its child privilege groups
	 * 
	 * @param privilege
	 *            , privilegeGroup
	 * @throws Exception
	 */
	private void populateCondGroup(UserAccessPrivilegesT privilege,
			PrivilegeGroup privilegeGroup,int count) throws Exception {
		logger.debug("Inside populateConditionGroup() method");
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
				if (geoBuffer != null && geoBuffer.length() > 0) {
					geoBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setGeographyBuffer(geoBuffer);
				}
				break;
			case SUBSP:
				if (subspBuffer != null && subspBuffer.length() > 0) {
					subspBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setSubspBuffer(subspBuffer);
				}
				break;
			case IOU:
				if (iouBuffer != null && iouBuffer.length() > 0) {
					iouBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
							+ Constants.SINGLE_QUOTE + Constants.COMMA);
					privilegeGroup.setIouBuffer(iouBuffer);
				}
				break;
			case CUSTOMER:
				if (custBuffer != null && custBuffer.length() > 0) {
					manageCustomer(privilegeGroup, privilegeValue, custBuffer,count);
				}
				break;
			case GROUP_CUSTOMER: {
				// Get the master customer list using group customer name
				List<CustomerMasterT> customerList = customerRepo
						.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(privilegeValue, Constants.UNKNOWN_CUSTOMER);
				if (customerList == null && customerList.isEmpty()) {
					logger.error(
							"Customers not found for the group customer name: {}",
							privilegeValue);
					throw new DestinationException(
							HttpStatus.INTERNAL_SERVER_ERROR,
							"Customers not found for the group customer name: "
									+ privilegeValue);
				} else {
					if (custBuffer != null && custBuffer.length() > 0) {
						for (CustomerMasterT customer : customerList) {
							custBuffer.append(Constants.SINGLE_QUOTE
									+ customer.getCustomerName()
									+ Constants.SINGLE_QUOTE + Constants.COMMA);
						}
					}
					privilegeGroup.setCustomerBuffer(custBuffer);
				}
				break;
			}
			default: {
				logger.error("Invalid Privilege Type: {}", privilegeType);
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Invalid Privilege Type: " + privilegeType);
			}
			}

			// Forming conditions for child privileges if any
			//List<UserAccessPrivilegesT> childPrivileges = privilege.getUserAccessPrivilegesTs();
			List<UserAccessPrivilegesT> childPrivileges = 
					userService.getAllChildPrivilegesByUserIdAndParentPrivilegeId(privilege.getUserId(), privilege.getPrivilegeId());

			if (childPrivileges != null && !childPrivileges.isEmpty()) {
				logger.debug("Child Privileges Size: {}",
						childPrivileges.size());
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
	 * This method populates the customer condition (global customer, if
	 * required)
	 * 
	 * @param privilegeGroup
	 *            , privilegeValue, customerBuffer
	 * @throws Exception
	 */
	private void manageCustomer(PrivilegeGroup privilegeGroup,
			String privilegeValue, StringBuffer customerBuffer,int count)
			throws Exception {
		logger.debug("Inside handleCustomer() method");
		if (!privilegeValue.equals(Constants.GLOBAL)) {
			
			parameterMap.put(count, privilegeValue);
			 privilegeValue="?"+count;
			customerBuffer.append( privilegeValue + Constants.COMMA);
			privilegeGroup.setCustomerBuffer(customerBuffer);
		} else {
			// GLOBAL_CUSTOMERS - Get the list from Misc_Type_Value_T
			List<MiscTypeValueT> miscList = miscTypeRepo
					.findByType(Constants.GLOBAL);
			for (MiscTypeValueT miscItem : miscList) {
				List<CustomerMasterT> custList = customerRepo
						.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(miscItem
								.getValue(), Constants.UNKNOWN_CUSTOMER);
				// Value is Group customer name
				if (custList != null && !custList.isEmpty()) {
					for (CustomerMasterT customer : custList) {
						String customerName = customer.getCustomerName();
						parameterMap.put(count, customerName);
						customerName="?"+count;
						customerBuffer.append(customerName + Constants.COMMA);
					   }
				} else {
					// Value is Master customer name
					String customerName = miscItem.getValue() ;
					parameterMap.put(count, customerName);
					customerName="?"+count;
					customerBuffer.append(customerName + Constants.COMMA);
					}
			}
			privilegeGroup.setCustomerBuffer(customerBuffer);
		}
    }


	/**
	 * This method populates the customer condition (global customer, if
	 * required)
	 * 
	 * @param privilegeGroup
	 *            , privilegeValue, customerBuffer
	 * @throws Exception
	 */
	private void handleCustomer(PrivilegeGroup privilegeGroup,
			String privilegeValue, StringBuffer customerBuffer)
			throws Exception {
		logger.debug("Inside handleCustomer() method");
		
		if (!privilegeValue.equals(Constants.GLOBAL)) {
			// Master customers
		    privilegeValue = privilegeValue.replace("'", "''");
			customerBuffer.append(Constants.SINGLE_QUOTE + privilegeValue
					+ Constants.SINGLE_QUOTE + Constants.COMMA);
			privilegeGroup.setCustomerBuffer(customerBuffer);
		} else {
			// GLOBAL_CUSTOMERS - Get the list from Misc_Type_Value_T
			List<MiscTypeValueT> miscList = miscTypeRepo
					.findByType(Constants.GLOBAL);
			for (MiscTypeValueT miscItem : miscList) {
				List<CustomerMasterT> custList = customerRepo
						.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(miscItem
								.getValue(), Constants.UNKNOWN_CUSTOMER);
				// Value is Group customer name
				if (custList != null && !custList.isEmpty()) {
					for (CustomerMasterT customer : custList) {
						String customerName = customer.getCustomerName();
						customerName=	customerName.replace("'", "''");
						customerBuffer.append(Constants.SINGLE_QUOTE
								+ customerName
								+ Constants.SINGLE_QUOTE + Constants.COMMA);
					}
				} else {
					// Value is Master customer name
					String customerName = miscItem.getValue() ;
					customerName=	customerName.replace("'", "''");
					customerBuffer.append(Constants.SINGLE_QUOTE
							+ customerName + Constants.SINGLE_QUOTE
							+ Constants.COMMA);
				}
			}
			privilegeGroup.setCustomerBuffer(customerBuffer);
		}
	}

	/**
	 * This method trims the unwanted comma and closes the condition set with
	 * parenthesis
	 * 
	 * @param privilegeGroup
	 */
	private void replaceLastCommaWithParenthesisInQueryCondition(
			PrivilegeGroup privilegeGroup) throws Exception {
		logger.debug("Inside handleParenthesis() method");
		StringBuffer geoBuffer = privilegeGroup.getGeographyBuffer();
		if (geoBuffer != null && geoBuffer.length() > 0) {
			if (geoBuffer.indexOf(Constants.COMMA) != -1) {
				geoBuffer.replace(geoBuffer.length() - 1, geoBuffer.length(),
						Constants.RIGHT_PARANTHESIS);
				privilegeGroup.setGeographyBuffer(geoBuffer);
			}
		}

		StringBuffer subspBuffer = privilegeGroup.getSubspBuffer();
		if (subspBuffer != null && subspBuffer.length() > 0) {
			if (subspBuffer.indexOf(Constants.COMMA) != -1) {
				subspBuffer.replace(subspBuffer.length() - 1,
						subspBuffer.length(), Constants.RIGHT_PARANTHESIS);
				privilegeGroup.setSubspBuffer(subspBuffer);
			}
		}

		StringBuffer iouBuffer = privilegeGroup.getIouBuffer();
		if (iouBuffer != null && iouBuffer.length() > 0) {
			if (iouBuffer.indexOf(Constants.COMMA) != -1) {
				iouBuffer.replace(iouBuffer.length() - 1, iouBuffer.length(),
						Constants.RIGHT_PARANTHESIS);
				privilegeGroup.setIouBuffer(iouBuffer);
			}
		}

		StringBuffer custBuffer = privilegeGroup.getCustomerBuffer();
		if (custBuffer != null && custBuffer.length() > 0) {
			if (custBuffer.indexOf(Constants.COMMA) != -1) {
				custBuffer.replace(custBuffer.length() - 1,
						custBuffer.length(), Constants.RIGHT_PARANTHESIS);
				privilegeGroup.setCustomerBuffer(custBuffer);
			}
		}
	}

	/**
	 * This method concatenates the parent clause and the other clauses with the
	 * required condition("and/or")
	 * 
	 * @param clauseBuffer
	 *            , condBuffer, joinString
	 */
	private void concatenateBuffer(StringBuffer clauseBuffer,
			StringBuffer condBuffer, String joinString) {
		logger.debug("Inside concatenateBuffer() method");
		// Need not check for length as it is always initialized with condition
		// prefix
		if (condBuffer != null) {
			String condStr = condBuffer.toString();
			// Append condition string and join string after last paranthesis
			if (condStr.contains(Constants.RIGHT_PARANTHESIS) && !condStr.equalsIgnoreCase("('') in (") ) {
				clauseBuffer.append(condBuffer);
				clauseBuffer.append(joinString);
			}
		}
		logger.debug("Clause Buffer: " + clauseBuffer.toString());
	}

	/**
	 * This method populates WHERE condition
	 * 
	 * @param privilegeGroups
	 * @return String
	 * @throws Exception
	 */
	private String getWhereClauseString(List<PrivilegeGroup> privilegeGroups)
			throws Exception {
		logger.debug("Inside getWhereClauseString() method");
		if (privilegeGroups != null && !privilegeGroups.isEmpty()) {
			// WHERE Clause parenthesis - start
			StringBuffer clauseBuffer = new StringBuffer(Constants.LEFT_PARANTHESIS);
			for (PrivilegeGroup privilegeGroup : privilegeGroups) {
				StringBuffer geoBuffer = privilegeGroup.getGeographyBuffer();
				StringBuffer subspBuffer = privilegeGroup.getSubspBuffer();
				StringBuffer iouBuffer = privilegeGroup.getIouBuffer();
				StringBuffer custBuffer = privilegeGroup.getCustomerBuffer();
				
				if ((geoBuffer != null && geoBuffer.length() > 0)
						|| (subspBuffer != null && subspBuffer.length() > 0)
						|| (iouBuffer != null && iouBuffer.length() > 0)
						|| (custBuffer != null && custBuffer.length() > 0)) 
				{
					// Sub condition group parenthesis - start
					clauseBuffer.append(Constants.LEFT_PARANTHESIS);
					if (geoBuffer != null && geoBuffer.length() > 0) {
						concatenateBuffer(clauseBuffer, geoBuffer, Constants.AND_CLAUSE);
					}
					if (subspBuffer != null && subspBuffer.length() > 0) {
						concatenateBuffer(clauseBuffer, subspBuffer, Constants.AND_CLAUSE);
					}
					if (iouBuffer != null && iouBuffer.length() > 0) {
						concatenateBuffer(clauseBuffer, iouBuffer, Constants.AND_CLAUSE);
					}
					if (custBuffer != null && custBuffer.length() > 0) {
						concatenateBuffer(clauseBuffer, custBuffer, Constants.AND_CLAUSE);
					}
					// Trimming the unwanted last " and "
					if (clauseBuffer.toString().contains(Constants.AND_CLAUSE))
						clauseBuffer.setLength(clauseBuffer.length() - 5);
					// Sub condition group parenthesis - end
					clauseBuffer.append(Constants.RIGHT_PARANTHESIS);
					// Remove inner empty parenthesis  (e.g. (() or ())
					if (clauseBuffer.toString().contains(Constants.EMPTY_PARANTHESIS))  {
						clauseBuffer.setLength(clauseBuffer.length() - 2);
					} else { 
						// Logical OR prefix for next condition group
						clauseBuffer.append(Constants.OR_CLAUSE);
					}
				}
			}
			// Trimming the unwanted last " or "
			if (clauseBuffer.toString().contains(Constants.OR_CLAUSE))
				clauseBuffer.setLength(clauseBuffer.length() - 4);
			// Clause parenthesis - end
			clauseBuffer.append(Constants.RIGHT_PARANTHESIS);
			// Remove outer empty parenthesis after removing inner empty parenthesis
			if (clauseBuffer.toString().contains(Constants.EMPTY_PARANTHESIS)) 
				clauseBuffer.setLength(clauseBuffer.length() - 2);
			logger.info("Condition clause formed: " + clauseBuffer.toString());
			return clauseBuffer.toString();
		} else {
			logger.debug("User does not have any specific access privileges defined");
			return null;
		}
	}

	/**
	 * This method populates a HashMap with query where condition prefix (i.e
	 * tableName.columnName)
	 * 
	 * @param geoPrefix
	 *            , subspPrefix, iouPrefix, custPrefix
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> getQueryPrefixMap(String geoPrefix,
			String subspPrefix, String iouPrefix, String custPrefix)
			throws Exception {
		logger.debug("Inside getQueryPrefixMap() method");
		HashMap<String, String> queryPrefixMap = new HashMap<String, String>();
		if (geoPrefix != null && !geoPrefix.isEmpty())
			queryPrefixMap.put(PrivilegeType.GEOGRAPHY.name(), geoPrefix);
		if (subspPrefix != null && !subspPrefix.isEmpty())
			queryPrefixMap.put(PrivilegeType.SUBSP.name(), subspPrefix);
		if (iouPrefix != null && !iouPrefix.isEmpty())
			queryPrefixMap.put(PrivilegeType.IOU.name(), iouPrefix);
		if (custPrefix != null && !custPrefix.isEmpty())
			queryPrefixMap.put(PrivilegeType.CUSTOMER.name(), custPrefix);

		return queryPrefixMap;
	}
}