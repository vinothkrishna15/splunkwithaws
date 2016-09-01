package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserAccessPrivilegeDTO;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserGoalsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.GoalMappingRepository;
import com.tcs.destination.data.repository.IouRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserGoalsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.StringUtils;

@Component("userUploadHelper")
public class UserUploadHelper {
	
	
	@Value("${user_default_password.length}")
	private int defaultPasswordLength;
	
	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private PartnerRepository partnerRepository;
	
	@Autowired
	private SubSpRepository subSpRepository;
	
	@Autowired
	private OfferingRepository offeringRepository;
	
	@Autowired
	private TimezoneMappingRepository timeZoneMappingRepository;
	
	@Autowired
	private  UserRepository userRepository;
	
	@Autowired
	private  ContactRepository contactRepository;
	
	@Autowired
	private  ConnectTypeRepository connectTypeRepository;
	
	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	SubSpRepository subspRepository;

	@Autowired
	IouRepository iouRepository;

	@Autowired
	CustomerRepository custRepository;
	
	@Autowired
	UserGoalsRepository userGoalsRepository;
	
	@Autowired
	GoalMappingRepository goalMappingRepository;
	
	private static final Logger logger = LoggerFactory
			.getLogger(UserUploadHelper.class);
	
	/**
	 * This method validates User Data to be inserted 
	 */
	public  UploadServiceErrorDetailsDTO validateUserData(String[] data, String userId, UserT userT) throws Exception 
	{
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer("");
		
		 // USER_ID 
		if(data[2]!=null){
		String usrId = validateAndRectifyValue(data[2].trim());
	    if(!StringUtils.isEmpty(usrId))
	    {
	    	if (isUserExists(usrId)) 
	    	{
	    		error.setRowNumber(Integer.parseInt(data[0]) + 1);
			    errorMsg.append(" User Id Already Exists! ");
			}
	    	else
	    	{      
	    	    userT.setUserId(usrId);
	    	}
		}
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id Is Mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id Is Mandatory; ");
		}
		
		        // USER_NAME 
	            if(data[3]!=null){
				String userName = data[3].trim();
				if(!StringUtils.isEmpty(userName))
				{
					if (!isUserNameExists(userName)) {
					    userT.setUserName(userName);
					} else {
						error.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorMsg.append(" User Name already exists ");
					}
				 }
				else
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Name Is Mandatory; ");
				}
	            } else {
	            	error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Name Is Mandatory; ");
	            }
				
				// PASSWORD 
				String password = StringUtils.generateRandomString(defaultPasswordLength);
				if(!StringUtils.isEmpty(password))
				{
					userT.setTempPassword(password);
					userT.setStatus(0);
				}
				else{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Password Is Mandatory; ");
				}
				
				//USER GROUP 
				if(data[5]!=null){
				String userGroup = data[5].trim();
				if(!StringUtils.isEmpty(userGroup))
				{
                   // check if valid user group
				    boolean validGroup = false;
					for (UserGroup group : UserGroup.values()) {
						if (userGroup.equalsIgnoreCase(group.getValue())) {
							validGroup = true;
						}
					}
					if (!validGroup) {
		                error.setRowNumber(Integer.parseInt(data[0]) + 1);
    					errorMsg.append(" User Group Is Invalid; ");
					}
					else
					{
					  userT.setUserGroup(userGroup);
					}
				}
				else{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Group Is Mandatory; ");
				}
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Group Is Mandatory; ");
				}
				
				//USER ROLE 
				if(data[6]!=null){
				String userRole = data[6].trim();
				if(!StringUtils.isEmpty(userRole))
				{
                    // check if valid user role
					boolean validRole = false;
					for (UserRole role : UserRole.values()) {
						if (userRole.equalsIgnoreCase(role.getValue())) {
							validRole = true;
						}
					}
                    if (!validRole) 
                    {
                    	error.setRowNumber(Integer.parseInt(data[0]) + 1);
    					errorMsg.append(" User Role Is Invalid; ");
					}
                    else
                    {
				        userT.setUserRole(userRole);
                    }
				}
				else{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Role Is Mandatory; ");
				}
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Role Is Mandatory; ");
				}
				
				//USER LOCATION 
				if(data[7]!=null){
				String userLocation = data[7].trim();
				if(!StringUtils.isEmpty(userLocation))
				{
					userT.setBaseLocation(userLocation);
				}
				else{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Location Is Mandatory; ");
				}
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Location Is Mandatory; ");
				}
				
				//USER TELEPHONE
				if(data[9]!=null){
				String userPhone = data[9].trim();
				if(!StringUtils.isEmpty(userPhone))
				{
					userT.setUserTelephone(userPhone);
				} 
				}
				
				//USER EMAIL ID
				if(data[10]!=null){
				String emailId = data[10].trim();
				if(!StringUtils.isEmpty(emailId))
				{
					userT.setUserEmailId(emailId);
				}
				}
				
				//USER SUPERVISOR ID
				if(data[11]!=null){
				String supervisorId = validateAndRectifyValue(data[11].trim());
				if(!StringUtils.isEmpty(supervisorId))
				{
					userT.setSupervisorUserId(supervisorId);
				}
				}
				
				//USER SUPERVISOR NAME
				if(data[12]!=null){
				String supervisorName = data[12].trim();
				if(!StringUtils.isEmpty(supervisorName))
				{
					userT.setSupervisorUserName(supervisorName);
				}
				}
				
				if(!StringUtils.isEmpty(errorMsg.toString())){
					error.setMessage(errorMsg.toString());
				}
				
				return error;
	}
	
	private boolean isUserNameExists(String userName) {
		return userRepository.findByUserName(userName) != null;
	}

	/**
	 * This method validates User Data to be update
	 */
	public  UploadServiceErrorDetailsDTO validateUserDataUpdate(String[] data, String userId, UserT userT) throws Exception 
	{
		
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		StringBuffer errorMsg = new StringBuffer("");
		// USER_ID
		if(data[2]!=null){
		String usrId = validateAndRectifyValue(data[2].trim());
	    if(!StringUtils.isEmpty(usrId))
	    {
			if (!isUserExists(usrId)) 
			 {
			   error.setRowNumber(Integer.parseInt(data[0]) + 1);
			   errorMsg.append(" Invalid User Id ! ");
			 }
			 else
			 {      
			    	    userT.setUserId(usrId);
			 }
		}
		else
		{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Id is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id is mandatory; ");
		}
	 // USER_NAME 
		if(data[3]!=null){
		String userName = data[3].trim();
		if(!StringUtils.isEmpty(userName))
		{
			userT.setUserName(userName);
		 }
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Name is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Name is mandatory; ");
		}
		
		// PASSWORD 
		if(data[4]!=null){
		String password = data[4].trim();
		if(!StringUtils.isEmpty(password))
		{
			userT.setTempPassword(password);
		}
		else{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Password is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Password is mandatory; ");
		}
		
		//USER GROUP 
		if(data[5]!=null){
		String userGroup = data[5].trim();
		if(!StringUtils.isEmpty(userGroup))
		{
           // check if valid user group
		    boolean validGroup = false;
			for (UserGroup group : UserGroup.values()) {
				if (userGroup.equalsIgnoreCase(group.getValue())) {
					validGroup = true;
				}
			}
			if (!validGroup) {
                error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" User Group is invalid; ");
			}
			else
			{
			  userT.setUserGroup(userGroup);
			}
		}
		else{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Group is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Group is mandatory; ");
		}
		
		
		//USER ROLE 
		if(data[6]!=null){
		String userRole = data[6].trim();
		if(!StringUtils.isEmpty(userRole))
		{
            // check if valid user role
			boolean validRole = false;
			for (UserRole role : UserRole.values()) {
				if (userRole.equalsIgnoreCase(role.getValue())) {
					validRole = true;
				}
			}
            if (!validRole) 
            {
            	error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" User Role is invalid; ");
			}
            else
            {
		        userT.setUserRole(userRole);
            }
		}
		else{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Role is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Role is mandatory; ");
		}
		
		//USER LOCATION 
		if(data[7]!=null){
		String userLocation = data[7].trim();
		if(!StringUtils.isEmpty(userLocation))
		{
			userT.setBaseLocation(userLocation);
		}
		else{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Location is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Location is mandatory; ");
		}
		
		//USER TELEPHONE
		if(data[9]!=null){
		String userPhone = data[9].trim();
		if(!StringUtils.isEmpty(userPhone)){
			userT.setUserTelephone(userPhone);
		}
		}
		
		//USER EMAIL ID
		if(data[10]!=null){
		String emailId = data[10].trim();
		if(!StringUtils.isEmpty(emailId))
		{
			userT.setUserEmailId(emailId);
		}
		}
		
		//USER SUPERVISOR ID
		if(data[11]!=null){
		String supervisorId = validateAndRectifyValue(data[11].trim());
		if(!StringUtils.isEmpty(supervisorId))
		{
			userT.setSupervisorUserId(supervisorId);
		}
		}
		
		//USER SUPERVISOR NAME
		if(data[12]!=null){
		String supervisorName = data[12].trim();
		if(!StringUtils.isEmpty(supervisorName))
		{
			userT.setSupervisorUserName(supervisorName);
		}
		}
		
		if(!StringUtils.isEmpty(errorMsg.toString())){
			error.setMessage(errorMsg.toString());
		}
		
		return error;
		
	}
	
	

	/**
	 * This method validates User Data to be delete
	 */
	public  UploadServiceErrorDetailsDTO validateUserId(String[] data,
			UserT user)
	{

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
	
		String userId = data[2];

		if (StringUtils.isEmpty(userId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("User Id is mandatory ");
		} else {
			userId = userId.indexOf(".") < 0 ? userId : userId.replaceAll("0*$", "").replaceAll("\\.$", "");
			user = userRepository.findByUserId(userId);
			if (user == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid User Id ");
			}
			else
			{
				//ACTIVE
				user.setActive(false);
				
			}
		}
		

		return error;
	
	}
	
	/**
	 * This method validates User Notification Settings Data to be inserted 
	 */
	public  UploadServiceErrorDetailsDTO validateUserNotificationSettingsData(String[] data, String userId, UserT userT) throws Exception 
	{
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		error.setDuplicateFlag(0);
		
		StringBuffer errorMsg = new StringBuffer("");
		
		 // USER_ID 
		if(data[2]!=null){
		String usrId = validateAndRectifyValue(data[2].trim());
	    if(!StringUtils.isEmpty(usrId))
	    {
	    	if (isUserExists(usrId)) 
	    	{
	    		userT.setUserId(usrId);
	    		
			}
	    	else
	    	{
	    		error.setRowNumber(Integer.parseInt(data[0]) + 1);
			    errorMsg.append(" User Id does not exist! ");
			    error.setDuplicateFlag(1);
	    	}
		}
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id is mandatory; ");
			error.setDuplicateFlag(1);
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id is mandatory; ");
			error.setDuplicateFlag(1);
		}
				
				//USER GROUP 
		        if(data[5]!=null){
				String userGrpData = data[5].trim();
				if(!StringUtils.isEmpty(userGrpData))
				{
                   // check if valid user group
					UserGroup userGroup = UserGroup.getUserGroup(userGrpData);
					if (userGroup == null) {
		                error.setRowNumber(Integer.parseInt(data[0]) + 1);
    					errorMsg.append(" User Group is invalid; ");
    					error.setDuplicateFlag(1);
					} else {
					  userT.setUserGroup(userGrpData);
					}
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Group is mandatory; ");
					error.setDuplicateFlag(1);
				}
		        } else {
		        	error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" User Group is mandatory; ");
					error.setDuplicateFlag(1);
		        }
				
		        if(!StringUtils.isEmpty(errorMsg.toString())){
		        	error.setMessage(errorMsg.toString());
		        }
		        
				return error;
	}
	
	/**
	 * This method validates User General Settings Data to be inserted 
	 */
 
	public UploadServiceErrorDetailsDTO validateUserGeneralSettingsData(
			String[] data, String userId,
			UserGeneralSettingsT userGeneralSettingsT) {
    
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		 error.setDuplicateFlag(0);
		StringBuffer errorMsg = new StringBuffer("");
		
		//WIDGET_ORDER
		userGeneralSettingsT.setWidgetOrder("1,2,3,4");
		
		//EMAIL_DIGEST
		userGeneralSettingsT.setEmailDigest("7");
		
		//THEME
		userGeneralSettingsT.setTheme("Default");
		
		//TIME ZONE DESCRIPTION
		if(data[8]!=null){
		String timeZoneDescription = data[8].trim();
		if(!StringUtils.isEmpty(timeZoneDescription))
		{
			if (!isValidTimeZone(timeZoneDescription)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" Invalid Time Zone Description; ");
			}
			else
			{
			userGeneralSettingsT.setTimeZoneDesc(timeZoneDescription);
			}
		}
		else{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Time Zone Description is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Time Zone Description is mandatory; ");
		}
		
		 // USER_ID 
		if(data[2]!=null){
		String usrId = validateAndRectifyValue(data[2].trim());
	    if(!StringUtils.isEmpty(usrId))
	    {
	    	if (isUserExists(usrId)) 
	    	   {
	    		userGeneralSettingsT.setUserId(usrId);
	    		
			    }
	    	else
	    	{
	    		error.setRowNumber(Integer.parseInt(data[0]) + 1);
			    errorMsg.append(" Invalid User Id; ");
			    error.setDuplicateFlag(1);
	    	}
		}
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id is mandatory; ");
			error.setDuplicateFlag(1);
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User id is mandatory; ");
			error.setDuplicateFlag(1);
		}
		
	    
	     //EVENT_REMINDER
	  	 userGeneralSettingsT.setEventReminder("2");
	  	 
	  	//MISSED_UPDATE_REMINDER
	  	 userGeneralSettingsT.setMissedUpdateReminder("1");
	  	 
	  	//PUSH_SUBSCRIBED
	  	 userGeneralSettingsT.setPushSubscribed("N");
	  	 
	  	if(!StringUtils.isEmpty(errorMsg.toString())){
			error.setMessage(errorMsg.toString());
		}
	  	 
	  	 return error;
	}
	
	
	
	/**
	 * This method validates User Access Privileges Data to be inserted 
	 */
 
	public UploadServiceErrorDetailsDTO validateUserAccessPrivilegesData(
			String[] data, String userId,
			UserAccessPrivilegeDTO userAccessPrivilegeDTO) {
		 
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		StringBuffer errorMsg = new StringBuffer();
		 
		        // USER_ID 
		if(data[2]!=null){
				String usrId = validateAndRectifyValue(data[2].trim());
			    if(!StringUtils.isEmpty(usrId))
			    {
			    	if (isUserExists(usrId)) 
			    	   {
			    		userAccessPrivilegeDTO.setUserId(usrId);
			    		
					    }
			    	else
			    	{
			    		error.setRowNumber(Integer.parseInt(data[0]) + 1);
					    errorMsg.append(" User Id does not exist! ");
			    	}
				}
				else
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					 errorMsg.append(" User Id is mandatory ");
				}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			 errorMsg.append(" User Id is mandatory ");
		}
		    
 		   //PRIMARY_PRIVILEGE_TYPE 
			    boolean isPrimaryTypeInvalid = false;
		if (data[5] != null) {
			String primaryPrivilegeType = data[5].trim();
			if (!StringUtils.isEmpty(primaryPrivilegeType)) {
				if (!PrivilegeType.contains(primaryPrivilegeType)) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid Primary Privilege Type");
					isPrimaryTypeInvalid = true;
				} else {
					userAccessPrivilegeDTO
							.setPrimaryPrivilegeType(primaryPrivilegeType);
				}

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" Primary privilege type is mandatory ");
				isPrimaryTypeInvalid = true;
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Primary privilege type is mandatory ");
			isPrimaryTypeInvalid = true;
		}
		    // PRIMARY_PRIVILEGE_VALUE 
		    List<String> primaryPrivilegeValues = getValuesList(data[6]);
		    if(!primaryPrivilegeValues.isEmpty() && isValidPrivilegeValue(userAccessPrivilegeDTO,primaryPrivilegeValues,true))
		    {
		    	if(isPrimaryTypeInvalid){
		    		error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" primary privilege value cannot exist without primary privilege type ");
		    	}else{
		    	 userAccessPrivilegeDTO.setPrimaryPrivilegeValues(primaryPrivilegeValues); 
		    	}
		    }
		    else
		    {
		        error.setRowNumber(Integer.parseInt(data[0]) + 1);
			    errorMsg.append(" Primary privilege values is invalid ");
		    }
		    
		   // SECONDARY_PRIVILEGE_TYPE 
		boolean isSecondaryTypeInvalid = false;
		if (data[7] != null) {
			String secondaryPrivilegeType = data[7].trim();
			if (!StringUtils.isEmpty(secondaryPrivilegeType)) {
				if (!PrivilegeType.contains(secondaryPrivilegeType)) {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Invalid Secondary Privilege Type ");
					isSecondaryTypeInvalid = true;
				} else {
					userAccessPrivilegeDTO
							.setSecondaryPrivilegeType(secondaryPrivilegeType);
				}
			} else {
				isSecondaryTypeInvalid = true;
			}
		} else {
			isSecondaryTypeInvalid = true;
		}

		// SECONDARY_PRIVILEGE_VALUE
		List<String> secondaryPrivilegeValues = getValuesList(data[8]);
		if (!secondaryPrivilegeValues.isEmpty()) {
			if (!isSecondaryTypeInvalid) {
				if (isValidPrivilegeValue(userAccessPrivilegeDTO,
						secondaryPrivilegeValues, false)) {
					userAccessPrivilegeDTO
							.setSecondaryPrivilegeValues(secondaryPrivilegeValues);
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append(" Secondary Privilege Values is invalid ");
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" secondary privilege value cannot exist without secondary privilege type ");
			}
		}
		    if(!StringUtils.isEmpty(errorMsg.toString())){
		    	error.setMessage(errorMsg.toString());
		    }
		    return error;
		
	}
	private boolean isValidPrivilegeValue(UserAccessPrivilegeDTO userAccessPrivilegeDTO,
			List<String> privilegeValues, boolean isPrimary) {
		String privilegeType = "";
		boolean isValid = false;
		if(isPrimary){
		    privilegeType = userAccessPrivilegeDTO.getPrimaryPrivilegeType();
		} else {
			privilegeType = userAccessPrivilegeDTO.getSecondaryPrivilegeType();
		}
		for (String privilegeValue : privilegeValues) {
		switch (PrivilegeType.valueOf(privilegeType)) {
		case GEOGRAPHY:
			GeographyMappingT geography = geographyRepository
					.findOne(privilegeValue);
			if (geography == null) {
				logger.error("Privilege Value :Geography is invalid");
				isValid = false;
				break;
			} else {
				isValid = true;
			}
			break;

		case SUBSP:
			List<SubSpMappingT> subSp = subspRepository
					.findByDisplaySubSp(privilegeValue);
			if (subSp == null || subSp.isEmpty()) {
				logger.error("Privilege Value :displaysubsp is invalid");
				isValid = false;
				break;
			} else {
				isValid = true;
			}
			break;

		case IOU:
			List<IouCustomerMappingT> iou = iouRepository
					.findByDisplayIou(privilegeValue);
			if (iou == null || iou.isEmpty()) {
				logger.error("Privilege Value :displayiou is invalid");
				isValid = false;
				break;
			} else {
				isValid = true;
			}
			break;

		case CUSTOMER:
			if (!privilegeValue.equals(Constants.GLOBAL)) {
				CustomerMasterT customer = custRepository
						.findByCustomerName(privilegeValue);
				if (customer == null) {
					logger.error(
							"Customers not found for the customer name: {}",
							privilegeValue);
					isValid = false;
					break;
				} else {
					isValid = true;
				}
			}
			break;

		case GROUP_CUSTOMER:
			List<CustomerMasterT> customerList = custRepository
					.findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(
							privilegeValue, Constants.UNKNOWN_CUSTOMER);

			if (customerList == null || customerList.isEmpty()) {
				logger.error(
						"Customers not found for the group customer name: {}",
						privilegeValue);
				isValid = false;
				break;
			} else {
				isValid = true;
			}
			break;
		}
		}
		return isValid;
	}


	/**
	 * This method validates User Goals Data to be inserted
	 * @param data
	 * @param userId
	 * @param userAccessPrivilegeDTO
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateUserGoalsData(
			String[] data, String userId,
			UserGoalsT userGoalsT) 
	{
		
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		StringBuffer errorMsg = new StringBuffer("");
		
		GoalMappingT goalMappingT=new GoalMappingT();
		
		  // USER_ID 
		if(data[2]!=null){
		String 	usrId = validateAndRectifyValue(data[2].trim());
	    if(!StringUtils.isEmpty(usrId))
	    {
	    	if (isUserExists(usrId)) 
	    	   {
	    		userGoalsT.setUserId(usrId);
	    		
			    }
	    	else
	    	{
	    		error.setRowNumber(Integer.parseInt(data[0]) + 1);
			    errorMsg.append(" User Id does not exist! ");
	    	}
		}
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id is mandatory; ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User Id is mandatory; ");
		}

		// USER_NAME 
		if(data[3]!=null){
		String userName = data[3].trim();
		if(StringUtils.isEmpty(userName))
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User name is empty; ");
		} 
		}
		
        // USER_GROUP 
		if(data[4]!=null){
		String usrGroup = data[4].trim();
		if(StringUtils.isEmpty(usrGroup))
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User group is empty; ");
			
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" User group is empty; ");
		}
		
		//GOAL NAME
		String validGoalName = "";
		if(data[5]!=null){
		String goalName=data[5].trim();
		if(StringUtils.isEmpty(goalName))
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Goal name is empty ");
		}
		else
		{
			List<GoalMappingT> goalMappingList = goalMappingRepository.findByGoalName(goalName);
			if(isValidGoalName(goalName,goalMappingList)){
			goalMappingT.setGoalName(goalName);
			validGoalName = goalName;
			userGoalsT.setGoalMappingT(goalMappingT);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" Invalid Goal Name ");
			}
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" Goal Name is empty ");
		}
		
		//FINANCIAL YEAR
		if(data[6]!=null){
		String financialYear=data[6].trim();
		if(!StringUtils.isEmpty(financialYear))
		{
			userGoalsT.setFinancialYear(financialYear);
			if(!StringUtils.isEmpty(validGoalName)){
			String validGoalId=goalMappingRepository.findGoalIdByGoalNameAndFinancialYear(validGoalName,financialYear);
			if(StringUtils.isEmpty(validGoalId)){
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append(" Invalid Goal Name for the given year ");
			}
		    }
		}
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" financial year is empty ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" financial year is empty ");
			
		}
		
		
		
		//TARGET_VALUE
		if(data[7]!=null){
		try{	
		BigDecimal targetValue=new BigDecimal(data[7]);
		if(targetValue!=null)
		{
			userGoalsT.setTargetValue(targetValue);		
		}
		} catch(Exception e){
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" invalid target value ");
		}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append(" target value is empty ");
		}
		
		if(!StringUtils.isEmpty(errorMsg.toString())){
			error.setMessage(errorMsg.toString());
		}
		
		return error;
	
	}
	
	
	private boolean isValidGoalName(String goalName,
			List<GoalMappingT> goalMappingList) {
		boolean isValidGoal = false;
		
		for(GoalMappingT goalMapping : goalMappingList){
			if(goalMapping.getGoalName().equalsIgnoreCase(goalName)){
				isValidGoal = true;
				break;
			}
		}
		
		return isValidGoal;
	}

	/**
	 * This method is used to retrieve the values as a list
	 * @param value
	 * @return
	 */
	private List<String> getValuesList(String value) {
		List<String> values = new ArrayList<String>();
		if (!StringUtils.isEmpty(value)) {
			values = Arrays.asList(value.split(", "));
			for (String pValue : values) {
				pValue.trim();
			}
		}
		return values;
	}
	
	private String validateAndRectifyValue(String value) {
		String val = value;
		System.out.println(value.substring(value.length() - 2, value.length()));
		if (value != null) {
			if (value.substring(value.length() - 2, value.length()).equals(".0")) {
				val = value.substring(0, value.length() - 2);
			}
		}
		return val;
	}
	
	/**
	 * This method validates if a given user is valid 
	 * @param userIdExcelStr
	 * @return
	 */
	private boolean isUserExists(String userId) {
		return userRepository.findOne(userId) != null;
	}
	
	/**
	 * This method validates if a given Time Zone is valid 
	 * @param excelStr
	 * @return
	 */
	private boolean isValidTimeZone(String excelStr) {
		return timeZoneMappingRepository.findOne(excelStr) != null;
	}
	}


