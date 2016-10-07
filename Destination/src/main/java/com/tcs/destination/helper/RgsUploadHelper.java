/**
 * 
 * RgsUploadHelper.java 
 *
 * @author TCS
 * 
 */
package com.tcs.destination.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.DeliveryRgsT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DeliveryRequirementRepository;
import com.tcs.destination.data.repository.RgsRepository;
import com.tcs.destination.data.repository.UserRepository;
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
	
	private static final int RGS_ID_COL_INDEX = 1;
	private static final int REQ_ID_COL_INDEX = 2;
	private static final int CUSTOMER_COL_INDEX = 9;
	private static final int BRANCH_COL_INDEX = 10;
	private static final int LOCATION_COL_INDEX = 7;
	private static final int COMPETENCY_COL_INDEX = 4;
	private static final int SUB_COMP_COL_INDEX = 5;
	private static final int EXPERIENCE_COL_INDEX = 6;
	private static final int ROLE_COL_INDEX = 3;
	private static final int STATUS_COL_INDEX = 8;
	private static final int IOU_COL_INDEX = 11;

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
			
			
			String rgsId = data[RGS_ID_COL_INDEX];
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
			
			String requirementId = data[REQ_ID_COL_INDEX];
			if(StringUtils.isEmpty(requirementId)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Requirement Id Is Mandatory; ");
			}else{
				requirementId=requirementId.replace(".0", "");
				requirementId = requirementId.trim();
			}
			
			String customerName = data[CUSTOMER_COL_INDEX];
			
			if(!StringUtils.isEmpty(customerName)){
				customerName = customerName.trim();
			}
			
			String branchName = data[BRANCH_COL_INDEX];
			
			if(!StringUtils.isEmpty(branchName)){
				branchName = branchName.trim();
			}
			
			String location = data[LOCATION_COL_INDEX];
			
			if(StringUtils.isEmpty(location)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Location Is Mandatory; ");
			}else{
				location = location.trim();
			}
			
			String competencyArea = data[COMPETENCY_COL_INDEX];
			
			if(!StringUtils.isEmpty(competencyArea)){
				competencyArea = competencyArea.trim();
			}
			
			String subCompetencyArea = data[SUB_COMP_COL_INDEX];
			
			if(!StringUtils.isEmpty(subCompetencyArea)){
				subCompetencyArea = subCompetencyArea.trim();
			}
			
			String experience = data[EXPERIENCE_COL_INDEX];
			
			if(StringUtils.isEmpty(experience)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Experience Is Mandatory; ");
			}else{
				experience = experience.trim();
			}
			
			String role = data[ROLE_COL_INDEX];
			
			if(StringUtils.isEmpty(role)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Role Is Mandatory; ");
			}else{
				role = role.trim();
			}
			
			String status = data[STATUS_COL_INDEX];
			
			if(StringUtils.isEmpty(status)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Status Is Mandatory; ");
			}else{
				DeliveryRequirementT deliveryRequirementDB = requirementRepository.findOne(requirementId);
				status = status.trim();
				if(deliveryRequirementDB!=null){
				String statusDb = deliveryRequirementDB.getStatus(); 
				if(status.equalsIgnoreCase(statusDb)){
					error.setRowNumber(rowNumber);
					errorMsg.append("Given Status already exists for the Requirement Id; ");
				}
				}
			}
			
			String iouName = data[IOU_COL_INDEX];
			if(!StringUtils.isEmpty(iouName)){
				iouName = iouName.trim();
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
				deliveryRequirementT.setCreatedBy(userId);
				deliveryRequirementT.setModifiedBy(userId);
			} else {
				error.setMessage(errorMsg.toString());
			}
			
							
			
			return error;
		}
	

	
	
}
