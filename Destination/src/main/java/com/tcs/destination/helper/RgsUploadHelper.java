/**
 * 
 * RgsUploadHelper.java 
 *
 * @author TCS
 * 
 */
package com.tcs.destination.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.DeliveryRgsT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DeliveryRequirementRepository;
import com.tcs.destination.data.repository.RgsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.StringUtils;

/**
 * This RgsUploadHelper class holds the functionality to aid rgs upload
 * 
 */
@Component("rgsUploadHelper")
public class RgsUploadHelper {

	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommonHelper commonHelper;
	
	@Autowired
	private RgsRepository rgsRepository;
	
	@Autowired
	private DeliveryRequirementRepository requirementRepository;
	
	private static final DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yy");
	

	/**
	 * @param data
	 * @param userId
	 * @param rgst
	 * @param isRgsIdExists 
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateRgsData(String[] data,
			String userId, DeliveryRgsT rgst, DeliveryRequirementT deliveryRequirementT, Boolean isRgsIdExists) {
		

			UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
			int rowNumber = Integer.parseInt(data[0]) + 1;
			StringBuffer errorMsg = new StringBuffer("");
			
			
			String rgsId = data[Constants.RGS_ID_COL_INDEX];
			if(StringUtils.isEmpty(rgsId)){
				error.setRowNumber(rowNumber);
				errorMsg.append("RGS Id Is Mandatory; ");
			}else{
				rgsId=rgsId.replace(".0", "");
				if(rgsRepository.exists(rgsId.trim())){
					isRgsIdExists = true;
				} 
					rgsId = rgsId.trim();
			}
			
			String requirementId = data[Constants.REQ_ID_COL_INDEX];
			if(StringUtils.isEmpty(requirementId)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Requirement Id Is Mandatory; ");
			}else{
				requirementId=requirementId.replace(".0", "");
				requirementId = requirementId.trim();
			}
			
			String customerName = data[Constants.CUSTOMER_COL_INDEX];
			
			if(!StringUtils.isEmpty(customerName)){
				customerName = customerName.trim();
			}
			
			String branchName = data[Constants.BRANCH_COL_INDEX];
			
			if(!StringUtils.isEmpty(branchName)){
				branchName = branchName.trim();
			}
			
			String location = data[Constants.LOCATION_COL_INDEX];
			
			if(StringUtils.isEmpty(location)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Location Is Mandatory; ");
			}else{
				location = location.trim();
			}
			
			String competencyArea = data[Constants.COMPETENCY_COL_INDEX];
			
			if(!StringUtils.isEmpty(competencyArea)){
				competencyArea = competencyArea.trim();
			}
			
			String subCompetencyArea = data[Constants.SUB_COMP_COL_INDEX];
			
			if(!StringUtils.isEmpty(subCompetencyArea)){
				subCompetencyArea = subCompetencyArea.trim();
			}
			
			String experience = data[Constants.EXPERIENCE_COL_INDEX];
			
			if(StringUtils.isEmpty(experience)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Experience Is Mandatory; ");
			}else{
				experience = experience.trim();
			}
			
			String role = data[Constants.ROLE_COL_INDEX];
			
			if(StringUtils.isEmpty(role)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Role Is Mandatory; ");
			}else{
				role = role.trim();
			}
			
			String status = data[Constants.STATUS_COL_INDEX];
			if(StringUtils.isEmpty(status)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Status Is Mandatory; ");
			}
			
			String iouName = data[Constants.IOU_COL_INDEX];
			if(!StringUtils.isEmpty(iouName)){
				iouName = iouName.trim();
			}
			
			String employeeId = data[Constants.EMP_ID_COL_INDEX];
			if(!StringUtils.isEmpty(employeeId)){
				employeeId = employeeId.trim();
			}
			
			String employeeName = data[Constants.EMP_NAME_COL_INDEX];
			if(!StringUtils.isEmpty(employeeName)){
				employeeName = employeeName.trim();
			}
			
			Date fulfillmentDate = null;
			String requirementFulfillmentDate = data[Constants.FULFILL_DATE_COL_INDEX];
			if (!StringUtils.isEmpty(requirementFulfillmentDate)) {
				try{
					fulfillmentDate = dateFormat.parse(requirementFulfillmentDate);
				}catch (Exception e){
					error.setRowNumber(rowNumber);
					errorMsg.append(" Invalid Fulfillment date Format ");
				}
			}
			
			Date reqStartDate = null;
			String requirementStartDate = data[Constants.REQ_START_DATE_COL_INDEX];
			if (!StringUtils.isEmpty(requirementStartDate)) {
				try{
					reqStartDate = dateFormat.parse(requirementStartDate);
				}catch (Exception e){
					error.setRowNumber(rowNumber);
					errorMsg.append(" Invalid Requirement Start date Format ");
				}
			} else {
				error.setRowNumber(rowNumber);
				errorMsg.append("requirementStartDate Is Mandatory; ");
			}
			
			Date reqEndDate = null;
			String requirementEndDate = data[Constants.REQ_END_DATE_COL_INDEX];
			if (!StringUtils.isEmpty(requirementEndDate)) {
				try{
					reqEndDate = dateFormat.parse(requirementEndDate);
				}catch (Exception e){
					error.setRowNumber(rowNumber);
					errorMsg.append(" Invalid Requirement End date Format ");
				}
			} else {
				error.setRowNumber(rowNumber);
				errorMsg.append("requirementEndDate Is Mandatory; ");
			}
			
			if(StringUtils.isEmpty(errorMsg.toString())){
				rgst.setDeliveryRgsId(rgsId);
				deliveryRequirementT.setDeliveryRgsId(rgsId);
				deliveryRequirementT.setRequirementId(requirementId);
				deliveryRequirementT.setLocation(location);
				deliveryRequirementT.setCompetencyArea(competencyArea);
				deliveryRequirementT.setCustomerName(customerName);
				deliveryRequirementT.setBranch(branchName);
				deliveryRequirementT.setIouName(iouName);
				deliveryRequirementT.setSubCompetencyArea(subCompetencyArea);
				deliveryRequirementT.setExperience(experience);
				deliveryRequirementT.setRole(role);
				deliveryRequirementT.setStatus(status);
				deliveryRequirementT.setEmployeeId(employeeId);
				deliveryRequirementT.setEmployeeName(employeeName);
				deliveryRequirementT.setFulfillmentDate(fulfillmentDate);
				deliveryRequirementT.setRequirementStartDate(reqStartDate);
				deliveryRequirementT.setRequirementEndDate(reqEndDate);
				deliveryRequirementT.setCreatedBy(userId);
				deliveryRequirementT.setModifiedBy(userId);
			} else {
				error.setMessage(errorMsg.toString());
			}
			
							
			
			return error;
		}
	

	
	
}
