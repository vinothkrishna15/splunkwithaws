package com.tcs.destination.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

@Component
public class DestinationQueryBuilder {

	@Autowired
	MiscTypeValueRepository miscTypeRepo;
	
	@Autowired
	CustomerRepository custRepo;
	
	/**
	 * This method returns the user access privilege clause for the query
	 * @param userPrivilegesList
	 * @return
	 * @throws DestinationException
	 */
	public String getQueryClause(List<UserAccessPrivilegesT> userPrivilegesList) throws DestinationException{
		List<PrivilegeGroup> pGroups = new ArrayList<PrivilegeGroup>();
		for(UserAccessPrivilegesT parentPrivilege : userPrivilegesList){
			PrivilegeGroup pGroup = new PrivilegeGroup();
			initGroupMembers(pGroup);
			generateConditionGroup(parentPrivilege,pGroup);
			handleParenthesis(pGroup);
			pGroups.add(pGroup);
		}
		StringBuffer clauseBuffer = new StringBuffer("");
		//clause parenthesis - start 
		clauseBuffer.append("(");
		for(PrivilegeGroup group : pGroups){
			//condition group parenthesis - start
			clauseBuffer.append("(");
			StringBuffer geoBuffer = group.getGeographyBuffer();
			concatenateBuffer(clauseBuffer,geoBuffer," and ");
			
			StringBuffer subspBuffer = group.getSubspBuffer();
			concatenateBuffer(clauseBuffer,subspBuffer," and ");
			
			StringBuffer iouBuffer = group.getIouBuffer();
			concatenateBuffer(clauseBuffer,iouBuffer," and ");
			
			StringBuffer custBuffer = group.getCustomerBuffer();
			concatenateBuffer(clauseBuffer,custBuffer," and ");
			//trimming the unwanted suffixed " and "
			clauseBuffer.setLength(clauseBuffer.length()-5);
			//condition group parenthesis - end
			clauseBuffer.append(")");
			//logical prefix for next condition group 
			clauseBuffer.append(" or ");
		}
		//trimming the unwanted suffixed " or "
		clauseBuffer.setLength(clauseBuffer.length()-4);
		//clause parenthesis - end 
		clauseBuffer.append(")");
		return clauseBuffer.toString();
	}

	/**
	 * This method concatenates the parent clause and the other clause with the required condition("and")
	 * @param clauseBuffer
	 * @param condBuffer
	 * @param joinString
	 */
	private void concatenateBuffer(StringBuffer clauseBuffer,
			StringBuffer condBuffer, String joinString) {
		if(condBuffer!=null){
			String condStr = condBuffer.toString();
			if(condStr.contains(")")){
				clauseBuffer.append(condBuffer);
				clauseBuffer.append(joinString);
			}
		}
	}

	/**
	 * This method initializes the buffers for each of the condition required (subsp,iou,geography,customer) in 
	 * the PrivilegeGroup.
	 * PrivilegeGroup consists of the needed buffers for a condition group on the whole(subsp,iou,geography,customer)
	 * @param pGroup
	 */
	private void initGroupMembers(PrivilegeGroup pGroup) {
		StringBuffer geoBuffer = new StringBuffer();
		geoBuffer.append("RCMT.customer_geography in (");
		pGroup.setGeographyBuffer(geoBuffer);
		
		StringBuffer subspBuffer = new StringBuffer();
		subspBuffer.append("SSMT.display_sub_sp in (");
		pGroup.setSubspBuffer(subspBuffer);
		
		StringBuffer iouBuffer = new StringBuffer();
		iouBuffer.append("ICMT.display_iou in (");
		pGroup.setIouBuffer(iouBuffer);
		
		StringBuffer custBuffer = new StringBuffer();
		custBuffer.append("RCMT.customer_name in (");
		pGroup.setCustomerBuffer(custBuffer);
	}
	
	/**
	 * This method trims the unwanted comma and closes the condition set with parenthesis
	 * @param pGroup
	 */
	private void handleParenthesis(PrivilegeGroup pGroup) {
		StringBuffer geoBuffer = pGroup.getGeographyBuffer();
		if(geoBuffer.indexOf(",")!=-1){
			geoBuffer.replace(geoBuffer.length()-1, geoBuffer.length(), ")");
			pGroup.setGeographyBuffer(geoBuffer);
		}
		StringBuffer subspBuffer = pGroup.getSubspBuffer();
		if(subspBuffer.indexOf(",")!=-1){
			subspBuffer.replace(subspBuffer.length()-1, subspBuffer.length(), ")");
			pGroup.setSubspBuffer(subspBuffer);
		}
		
		StringBuffer iouBuffer = pGroup.getIouBuffer();
		if(iouBuffer.indexOf(",")!=-1){
			iouBuffer.replace(iouBuffer.length()-1, iouBuffer.length(), ")");
			pGroup.setSubspBuffer(iouBuffer);
		}
		
		StringBuffer custBuffer = pGroup.getCustomerBuffer();
		if(custBuffer.indexOf(",")!=-1){
			custBuffer.replace(custBuffer.length()-1, custBuffer.length(), ")");
			pGroup.setSubspBuffer(custBuffer);
		}
	}

	/**
	 * This method populates the required condition group object
	 * @param privilege
	 * @param pGroup
	 * @throws DestinationException
	 */
	private void generateConditionGroup(UserAccessPrivilegesT privilege,
			PrivilegeGroup pGroup) throws DestinationException{
		String privilegeType = privilege.getPrivilegeType();
		String privilegeValue = privilege.getPrivilegeValue();
		
		StringBuffer geoBuffer = pGroup.getGeographyBuffer();
		StringBuffer subspBuffer = pGroup.getSubspBuffer();
		StringBuffer iouBuffer = pGroup.getIouBuffer();
		StringBuffer custBuffer = pGroup.getCustomerBuffer();
		
		//forming parent condition
		if(PrivilegeType.contains(privilegeType)){
			switch(PrivilegeType.valueOf(privilegeType)){
			case GEOGRAPHY		:
				geoBuffer.append("'"+privilegeValue+"',");
				pGroup.setGeographyBuffer(geoBuffer);
				break;
			case SUBSP 			: 
				subspBuffer.append("'"+privilegeValue+"',");
				pGroup.setSubspBuffer(subspBuffer);
				break;
			case IOU 			: 
				iouBuffer.append("'"+privilegeValue+"',");
				pGroup.setIouBuffer(iouBuffer);
				break;
			case CUSTOMER 		: 
				handleCustomerOrGlobal(pGroup, privilegeValue, custBuffer);
				break;   
			case GROUP_CUSTOMER : 
				List<CustomerMasterT> custList = custRepo.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(privilegeValue);
				for(CustomerMasterT customer : custList){
					custBuffer.append("'"+customer.getCustomerName()+"',");
				}
				pGroup.setCustomerBuffer(custBuffer);
				break; 
			default 			:
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
						"Invalid Privilege Type");
			}

			//forming child conditions
			List<UserAccessPrivilegesT> childPrivileges = privilege.getUserAccessPrivilegesTs();
			if(childPrivileges != null && !childPrivileges.isEmpty()){
				for(UserAccessPrivilegesT childPrivilege : childPrivileges){
					generateConditionGroup(childPrivilege, pGroup);
				}
			} else {
				return;
			}
		} else {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Invalid Privilege Type");
		}
	}

	/**
	 * This method populates the customer condition (global customer, if required)
	 * @param pGroup
	 * @param privilegeValue
	 * @param custBuffer
	 */
	private void handleCustomerOrGlobal(PrivilegeGroup pGroup,
			String privilegeValue, StringBuffer custBuffer) {
		if(!privilegeValue.equals(Constants.GLOBAL)){
			//for other customers
			custBuffer.append("'"+privilegeValue+"',");
			pGroup.setCustomerBuffer(custBuffer);
		} else {
			//for GLOBAL_CUSTOMERS
			List<MiscTypeValueT> miscList = miscTypeRepo.findByType(Constants.GLOBAL);
			for(MiscTypeValueT miscItem : miscList){
				List<CustomerMasterT> custList = custRepo.findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(miscItem.getValue());
				if(custList!=null && !custList.isEmpty()){
					//groupname is specified
					for(CustomerMasterT customer : custList){
						custBuffer.append("'"+customer.getCustomerName()+"',");
					}
				} else {
					//customername is specified
					custBuffer.append("'" + miscItem.getValue()+"',");
				}
			}
			
			pGroup.setCustomerBuffer(custBuffer);
		}
	}
	
}
