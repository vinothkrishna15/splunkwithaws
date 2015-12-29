package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.GoalGroupMappingT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserAccessPrivilegeDTO;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserGoalsT;
import com.tcs.destination.bean.UserGroupMappingT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.GoalGroupMappingRepository;
import com.tcs.destination.data.repository.GoalMappingRepository;
import com.tcs.destination.data.repository.LoginHistoryRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.data.repository.UserGoalsRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.DestinationUserDefaultObjectsHelper;
//import com.tcs.destination.helper.DestinationUserDefaultObjectsHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.StringUtils;

/**
 * 
 * This service handles functionalities related to user
 *
 */
@Service
public class UserService {

	private static final Logger logger = LoggerFactory
			.getLogger(UserService.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserGeneralSettingsRepository userGeneralSettingsRepository;

	@Autowired
	LoginHistoryRepository loginHistoryRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;

	@Autowired
	UserUploadService userUploadService;

	@Autowired
	GoalGroupMappingRepository goalGroupMappingRepository;

	@Autowired
	GoalMappingRepository goalMappingRepository;

	@Autowired
	UserGoalsRepository userGoalsRepository;


	@Autowired
	DestinationMailUtils mailUtils;

	@Value("${forgotPassword}")
	private String forgotPasswordSubject;

	@Autowired
	CustomerService customerService;

	/**
	 * This method retrieves user details based on user name
	 * @param nameWith
	 * @return
	 * @throws Exception
	 */
	public List<UserT> findByUserName(String nameWith) throws Exception {
		logger.debug("Inside findByUserName Service");
		List<UserT> users = (List<UserT>) userRepository
				.findByUserNameIgnoreCaseLike("%" + nameWith + "%");

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}

		return users;
	}

	/**
	 * This method is used to retrieve user details based on user role
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<String> findByUserRole(String role) throws Exception {
		logger.debug("Inside findByUserRole Service");
		List<String> users = (List<String>) userRepository
				.findUserIdByUserRole(role);

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}

		return users;
	}

	/**
	 * This method is used to retrieve user details based on user name
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public UserT findUserByName(String userName) throws Exception {
		logger.debug("Inside findUserByName Service");
		UserT user = null;
		try {
			user = userRepository.findByUserName(userName);
		} catch (Exception e) {
			logger.error("Error occured while retrieving user details");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

		if (user == null) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found");
		}
		return user;
	}

	/**
	 * This method is used to find the last login details of a specific user
	 * @param userId
	 * @return
	 */
	public Timestamp getUserLastLogin(String userId) {
		logger.debug("Inside getUserNotification Service");
		Timestamp lastLogin = null;
		LoginHistoryT loginHistory = loginHistoryRepository
				.findLastLoginByUserId(userId);
		if (loginHistory != null)
			lastLogin = loginHistory.getLoginDatetime();
		return lastLogin;
	}

	/**
	 * This method is used to save user details to database
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean adduser(UserT user) throws Exception {
		return userRepository.save(user) != null;
	}


	public boolean addLoginHistory(LoginHistoryT loginHistory) {
		logger.debug("Inside addLoginHistory Service");
		LoginHistoryT managedLoginHistory = loginHistoryRepository
				.save(loginHistory);
		if (managedLoginHistory == null)
			return false;
		else
			return true;
	}

	/**
	 * This method is used to find user login details for the given session id.
	 * 
	 * @param userId
	 *            , sessionId.
	 * @return user login details.
	 */
	public LoginHistoryT findByUserIdAndSessionId(String userId,
			String sessionId) throws Exception {
		logger.debug("Inside findByUserIdAndSessionId() service");
		LoginHistoryT loginHistory = null;
		if (userId != null && sessionId != null) {
			loginHistory = loginHistoryRepository.findByUserIdAndSessionId(
					userId, sessionId);
		}
		return loginHistory;
	}

	/**
	 * This method is used to search and retrieve user details based on userId an password
	 * @param userId
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public UserT findByUserIdAndPassword(String userId, String password)
			throws Exception {
		logger.debug("Inside findByUserIdAndPassword() service");
		return (userRepository.findByUserIdAndTempPassword(userId, password));
	}

	/**
	 * This method is used to update user details
	 * @param user
	 */
	public void updateUser(UserT user) {
		userRepository.save(user);
	}

	/**
	 * This method is used to retrieve user details based on userId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UserT findByUserId(String userId) throws Exception {
		UserT dbUser = userRepository.findOne(userId);
		return dbUser;
	}

	/**
	 * This method is used to retrieve user details based on role of user
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public List<UserT> getUsersByRole(String userRole) throws Exception {
		return userRepository.findByUserRole(userRole);
	}

	/**
	 * This method is used to check if a given user is System Admin 
	 * @param userId
	 * @return
	 */
	public boolean isSystemAdmin(String userId) {
		return isUserWithRole(userId, UserRole.SYSTEM_ADMIN.getValue());

	}

	/**
	 * This method is used to validate if a given user has the specified role
	 * @param userId
	 * @param userRole
	 * @return
	 */
	private boolean isUserWithRole(String userId, String userRole) {
		return !(userRepository.findByUserIdAndUserRole(userId, userRole)
				.isEmpty());
	}

	/**
	 * This is the service method for forgot password service
	 * 
	 * @param userId
	 * @param userEmailId
	 * @throws Exception
	 */
	public void forgotPassword(String userId, String userEmailId)
			throws Exception {
		logger.debug("Inside forgotPassword() service");
		UserT user = userRepository.findOne(userId);
		if (user != null) {
			String retrievedMailId = user.getUserEmailId();
			if (retrievedMailId.equals(userEmailId)) {
				mailUtils.sendPasswordAutomatedEmail(forgotPasswordSubject,
						user, new Date());
			} else {
				logger.error("UserId and E-Mail address do not match");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"UserId and E-Mail address do not match");
			}
		} else {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found: " + userId);
		}
	}

	/**
	 * This service is used to get all the active access privileges for a user
	 * 
	 * @param userId
	 * @return List of user access privileges
	 * @throws Exception
	 */
	public List<UserAccessPrivilegesT> getAllPrivilegesByUserId(String userId)
			throws Exception {
		logger.debug("Inside getAllPrivilegesByUserId() service");
		return (userAccessPrivilegesRepository
				.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId,
						Constants.Y));
	}

	/**
	 * This service is used to get all the active access privileges for a user
	 * and parent privilege id
	 * 
	 * @param userId
	 * @param privilegeId
	 * @return List of user access privileges
	 * @throws Exception
	 */
	public List<UserAccessPrivilegesT> getAllChildPrivilegesByUserIdAndParentPrivilegeId(
			String userId, Integer parentPrivilegeId) throws Exception {
		logger.debug("Inside getAllChildPrivilegesByUserIdAndParentPrivilegeId() service");
		return (userAccessPrivilegesRepository
				.findByUserIdAndParentPrivilegeIdAndIsactive(userId,
						parentPrivilegeId, Constants.Y));
	}

	/**
	 * This method is used to insert given user details into the database 
	 * @param user
	 * @param isBulkUpload
	 * @return
	 * @throws Exception
	 */
	public boolean insertUser(UserT user, boolean isBulkUpload)
			throws Exception {
		logger.debug("inside insertUser method");
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			logger.debug("user Saved : " + user.getUserId());
			return true;
		}
		logger.debug("user not Saved");
		return false;
	}

	/**
	 * This method is used to check if a given user details are valid 
	 * @param user
	 * @param isInsert
	 * @throws Exception
	 */
	public void validateUser(UserT user, boolean isInsert) throws Exception {
		// check for not null fields
		if (StringUtils.isEmpty(user.getUserId())) {
			logger.error("user id is null");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"User Id cannot be empty");
		}

		if (StringUtils.isEmpty(user.getUserName())) {
			logger.error("user name is null");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"User Name cannot be empty");
		}

		if (StringUtils.isEmpty(user.getTempPassword())) {
			logger.error("password is null");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Password cannot be empty");
		}

		if (StringUtils.isEmpty(user.getBaseLocation())) {
			logger.error("base_location is null");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Base location cannot be empty");
		}

		if (StringUtils.isEmpty(user.getUserRole())) {
			logger.error("user role is null");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"User Role cannot be empty");
		} else {
			// check if valid entry
			String userRole = user.getUserRole();
			boolean validRole = false;
			for (UserRole role : UserRole.values()) {
				if (userRole.equalsIgnoreCase(role.getValue())) {
					validRole = true;
				}
			}

			if (!validRole) {
				logger.error("user role invalid");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"User Role is invalid");
			}
		}

		if (StringUtils.isEmpty(user.getUserGroup())) {
			logger.error("user group is null");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"User Group cannot be empty");
		} else {
			// check if entry present in the table
			String userGroup = user.getUserGroup();
			boolean validGroup = false;
			for (UserGroup group : UserGroup.values()) {
				if (userGroup.equalsIgnoreCase(group.getValue())) {
					validGroup = true;
				}
			}
			if (!validGroup) {
				logger.error("user group invalid");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"User Group is invalid");
			}
		}
	}

	/**
	 * This method is used to retrieve users with a specific role 
	 * @param roles
	 * @return
	 */
	public List<UserT> getByUserRoles(List<String> roles) {

		logger.debug("Inside findByUserRole Service");

		List<UserT> users = (List<UserT>) userRepository.findByUserRoles(roles);

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}

		return users;
	}

	/**
	 * This service saves user details into user_t
	 * 
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void save(List<UserT> insertList) throws Exception {
		logger.debug("Inside save method");
		userRepository.save(insertList);
	}

	/**
	 * This service saves user details into user_general_settings_t
	 * 
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void saveGeneralSettings(List<UserGeneralSettingsT> insertList) throws Exception {
		logger.debug("Inside save method");
		userGeneralSettingsRepository.save(insertList);
	}

	/**
	 * This service saves user details into user_notification_settings_t
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void saveNotificationSettings(List<UserT> userList) throws Exception {
		logger.debug("Inside save notifications method");
		// saving user notification settings for the user
		List<UserNotificationSettingsT> userNotificationSettingsList=new ArrayList<UserNotificationSettingsT>();
		for(UserT user:userList)
		{
			userNotificationSettingsList = DestinationUserDefaultObjectsHelper.getUserNotificationSettingsList(user);
		}
		userNotificationSettingsRepository.save(userNotificationSettingsList);
		logger.debug("User Notification Settings : saved");

	}

	/**
	 * This service saves user details into user_access_priviledges_t
	 * @param userAccessPrivilegeDTOList
	 * @throws Exception
	 */
	public void saveAccessPriviledgeSettings(List<UserAccessPrivilegeDTO> userAccessPrivilegeDTOList,List<UserT> usersList) throws Exception 
	{
		logger.debug("Inside save access privileges method");

		List<UserAccessPrivilegesT> userAccessPrivilegeList=new ArrayList<UserAccessPrivilegesT>();


		for(UserAccessPrivilegeDTO accessPrivilegeDTO:userAccessPrivilegeDTOList)
		{

			// Primary Privilege Value List
			List<String> primaryPrivilegeValueList=accessPrivilegeDTO.getPrimaryPrivilegeValues();

			for(String primaryPrivilegeValue: primaryPrivilegeValueList)
			{
				UserAccessPrivilegesT userAccessPrivilegeT=new UserAccessPrivilegesT();
				//Setting Primary Privilege Type
				userAccessPrivilegeT.setPrivilegeType(accessPrivilegeDTO.getPrimaryPrivilegeType());

				//Setting Primary Privilege Value
				userAccessPrivilegeT.setPrivilegeValue(primaryPrivilegeValue);

				//Setting UserID
				userAccessPrivilegeT.setUserId(accessPrivilegeDTO.getUserId());

				//Setting IsActive Flag
				userAccessPrivilegeT.setIsactive("Y");

				userAccessPrivilegesRepository.save(userAccessPrivilegeT);

				if (!StringUtils.isEmpty(accessPrivilegeDTO.getSecondaryPrivilegeType()))
				{

					Integer parentPrivilegeId=userAccessPrivilegesRepository.getParentPrivilegeId(accessPrivilegeDTO.getUserId(),accessPrivilegeDTO.getPrimaryPrivilegeType(),primaryPrivilegeValue);

					// Secondary Privilege Value List
					List<String> secondaryPrivilegeValueList=accessPrivilegeDTO.getSecondaryPrivilegeValues();
					for(String secondaryPrivilegeValue: secondaryPrivilegeValueList)
					{
						UserAccessPrivilegesT childUserAccessPrivilegeT=new UserAccessPrivilegesT();
						//Setting ParentPrivilege Id
						childUserAccessPrivilegeT.setParentPrivilegeId(parentPrivilegeId);

						//Setting UserID
						childUserAccessPrivilegeT.setUserId(accessPrivilegeDTO.getUserId()); 

						//Setting Primary Privilege Type
						childUserAccessPrivilegeT.setPrivilegeType(accessPrivilegeDTO.getSecondaryPrivilegeType());

						//Setting Primary Privilege Value
						childUserAccessPrivilegeT.setPrivilegeValue(secondaryPrivilegeValue);

						//Setting IsActive Flag
						childUserAccessPrivilegeT.setIsactive("Y");

						userAccessPrivilegesRepository.save(childUserAccessPrivilegeT);
					}
				}
			}
		}
	}

	/**
	 * This service saves goal details from excel into user_goals_t
	 * @param goalList
	 * @param usersList
	 * @throws Exception
	 */
	public void	saveUserGoalsData( List<UserGoalsT> goalList,List<UserT> usersList,String createdModifiedBy,List<UploadServiceErrorDetailsDTO> errorList) throws Exception 
	{
		logger.debug("******Inside save user goals method*******");

		UploadServiceErrorDetailsDTO errorDTO=new UploadServiceErrorDetailsDTO();

		for(UserGoalsT userGoalTToBeUpdated:goalList)
		{
			String userIdGoalSheet=userGoalTToBeUpdated.getUserId();
			BigDecimal targetValueInExcel=userGoalTToBeUpdated.getTargetValue();
			String goalName=userGoalTToBeUpdated.getGoalMappingT().getGoalName();
			String goalId=goalMappingRepository.findGoalId(goalName);
			String financialYear=userGoalTToBeUpdated.getFinancialYear();
			List<UserGoalsT> userGoalsList = userGoalsRepository.getUserGoals(userIdGoalSheet, goalId, financialYear);
			UserGoalsT userGoalT = userGoalsList.get(0);
			userGoalT.setTargetValue(targetValueInExcel);
			if(!goalId.equals("G5")){
				userGoalsRepository.save(userGoalT);
			} else {
				errorDTO.setMessage("Pipeline value provided is ignored for " + userIdGoalSheet);
				errorList.add(errorDTO);
			}
			if(goalId.equals("G4"))
			{
				List<UserGoalsT> goalG5List=userGoalsRepository.getUserGoals(userIdGoalSheet, "G5", financialYear);//(userIdGoalSheet,financialYear);
				UserGoalsT goalG5 = goalG5List.get(0);
				goalG5.setTargetValue(targetValueInExcel.multiply(new BigDecimal(5)));
				userGoalsRepository.save(goalG5);
			}
		}
   }
	/**
	 * This service saves default user goal details into user_goals_t
	 * @param usersList
	 * @param createdModifiedBy
	 */

	public void insertDefaultGoals(List<UserT> usersList,String createdModifiedBy){
		for(UserT userT:usersList)
		{
			String userId=userT.getUserId();
			String userGroup=userT.getUserGroup();
			String currentFinancialYear=DateUtils.getCurrentFinancialYear();
			StringBuffer financialyear = new StringBuffer("");
			financialyear.append(currentFinancialYear.substring(0, 3));
			financialyear.append("'");
			financialyear.append(currentFinancialYear.substring(3,currentFinancialYear.length()));
			List<Object[]> goalGroupMappingList= goalGroupMappingRepository.findByUserGroupFinancialyear(userGroup,currentFinancialYear);
			List<GoalMappingT> goalMappingT=goalMappingRepository.findByFinancialyear(currentFinancialYear);	
			for(Object[] goalGroupMappingT:goalGroupMappingList)
			{
				UserGoalsT userGoalT=new UserGoalsT();
				userGoalT.setUserId(userId);
				userGoalT.setFinancialYear(currentFinancialYear);  
				userGoalT.setGoalId((String)goalGroupMappingT[0]);
				userGoalT.setTargetValue((BigDecimal)goalGroupMappingT[1]);
				userGoalT.setCreatedModifiedBy(createdModifiedBy);
				userGoalsRepository.save(userGoalT);
			}

		}
	}

}