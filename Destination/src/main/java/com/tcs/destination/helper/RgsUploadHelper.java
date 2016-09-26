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
	
	

	/**
	 * @param data
	 * @param userId
	 * @param rgst
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateRgsData(String[] data,
			String userId, DeliveryRgsT rgst, DeliveryRequirementT deliveryRequirementT) {
		

			UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
			int rowNumber = Integer.parseInt(data[0]) + 1;
			StringBuffer errorMsg = new StringBuffer("");
			
			
			String rgsId = data[1];
			if(StringUtils.isEmpty(rgsId)){
				error.setRowNumber(rowNumber);
				errorMsg.append("RGS Id Is Mandatory; ");
			}else{
				rgsId=rgsId.replace(".0", "");
				if(rgsRepository.exists(rgsId.trim())){
					error.setRowNumber(rowNumber);
					errorMsg.append("RGS Id Already exists; ");
				} else {
					rgsId = rgsId.trim();
				}
			}
			
			String requirementId = data[2];
			if(StringUtils.isEmpty(requirementId)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Requirement Id Is Mandatory; ");
			}else{
				requirementId=requirementId.replace(".0", "");
				if(requirementRepository.exists(requirementId.trim())){
					error.setRowNumber(rowNumber);
					errorMsg.append("Requirement Id Already exists; ");
				}else{
					requirementId = requirementId.trim();
				}
			}
			
			String location = data[8];
			
			if(StringUtils.isEmpty(location)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Location Is Mandatory; ");
			}else{
				location = location.trim();
			}
			
			String competencyArea = data[12];
			
			if(!StringUtils.isEmpty(competencyArea)){
				competencyArea = competencyArea.trim();
			}
			
			String subCompetencyArea = data[14];
			
			if(!StringUtils.isEmpty(subCompetencyArea)){
				subCompetencyArea = subCompetencyArea.trim();
			}
			
			String experience = data[18];
			
			if(StringUtils.isEmpty(experience)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Experience Is Mandatory; ");
			}else{
				experience = experience.trim();
			}
			
			String role = data[21];
			
			if(StringUtils.isEmpty(role)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Role Is Mandatory; ");
			}else{
				role = role.trim();
			}
			
			String status = data[64];
			
			if(StringUtils.isEmpty(status)){
				error.setRowNumber(rowNumber);
				errorMsg.append("Status Is Mandatory; ");
			}else{
				status = status.trim();
			}
			
			if(StringUtils.isEmpty(errorMsg.toString())){
				rgst.setDeliveryRgsId(rgsId);
				deliveryRequirementT.setDeliveryRgsId(rgsId);
				deliveryRequirementT.setRequirementId(requirementId);
				deliveryRequirementT.setLocation(location);
				deliveryRequirementT.setCompetencyArea(competencyArea);
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
