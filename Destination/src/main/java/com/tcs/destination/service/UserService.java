package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryClusterT;
import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.NotificationTypeEventMappingT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserAccessPrivilegeDTO;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserGoalsT;
import com.tcs.destination.bean.UserGroupMappingT;
import com.tcs.destination.bean.UserModule;
import com.tcs.destination.bean.UserModuleAccess;
import com.tcs.destination.bean.UserModuleAccessT;
import com.tcs.destination.bean.UserProfile;
import com.tcs.destination.bean.UserRoleMappingT;
import com.tcs.destination.bean.UserSubscriptions;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryClusterRepository;
import com.tcs.destination.data.repository.GoalGroupMappingRepository;
import com.tcs.destination.data.repository.GoalMappingRepository;
import com.tcs.destination.data.repository.LoginHistoryRepository;
import com.tcs.destination.data.repository.NotificationTypeEventMappingRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.data.repository.UserGoalsRepository;
import com.tcs.destination.data.repository.UserGroupMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.UserRoleMappingRepository;
import com.tcs.destination.data.repository.UserSubscriptionsRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.DestinationUserDefaultObjectsHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;

/**
 * 
 * This service handles functionalities related to user such as find, last login, login history
 * privileges and forgot password
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
	UserSubscriptionsRepository userSubscriptionsRepository;

	@Autowired
	UserUploadService userUploadService;

	@Autowired
	GoalGroupMappingRepository goalGroupMappingRepository;

	@Autowired
	GoalMappingRepository goalMappingRepository;

	@Autowired
	UserGoalsRepository userGoalsRepository;
	
	@Autowired
	UserGeneralSettingsRepository userGenSettingsRepository;

	@Autowired
	DestinationMailUtils mailUtils;
	
	@Autowired
	UserGroupMappingRepository userGroupMappingRepository;
	
	@Autowired
	UserRoleMappingRepository userRoleMappingRepository;
	
	
	@Autowired
	DeliveryClusterRepository deliveryClusterRepository;
	
	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;

	@Value("${forgotPassword}")
	private String forgotPasswordSubject;
	
	@Value("${user_default_password.length}")
	private int defaultPasswordLength;

	@Autowired
	CustomerService customerService;
	
	@Autowired
	ThreadPoolTaskExecutor mailTaskExecutor;

	@Autowired
	NotificationTypeEventMappingRepository notificationTypeEventMappingRepository ;

	/**
	 * This method retrieves user details based on user name
	 * @param nameWith
	 * @return
	 * @throws Exception
	 */
	public List<UserT> findByUserName(String nameWith) throws Exception {
		logger.debug("Begin:Inside findByUserName UserService");
		List<UserT> users = (List<UserT>) userRepository
				//inactive indicator - filter only active users - done
				.findByActiveTrueAndUserNameIgnoreCaseLike("%" + nameWith + "%");

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}
		logger.debug("End:Inside findByUserName Service");
		return users;
	}
	
	


	/**
	 * This method is used to retrieve user details based on user role
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<String> findByUserRole(String role) throws Exception {
		logger.debug("Begin:Inside findByUserRole Service");
		List<String> users = (List<String>) userRepository
				.findUserIdByUserRole(role);

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}
		logger.debug("End:Inside findByUserRole Service");
		return users;
	}

	/**
	 * This method is used to retrieve user details based on user name
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public UserT findUserByName(String userName) throws Exception {
		logger.debug("Begin:Inside findUserByName UserService");
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
		logger.debug("End:Inside findUserByName UserService");
		return user;
	}

	/**
	 * This method is used to find the last login details of a specific user
	 * @param userId
	 * @return
	 */
	public Timestamp getUserLastLogin(String userId) {
		logger.debug("Begin:Inside getUserLastLogin of UserService");
		Timestamp lastLogin = null;
		LoginHistoryT loginHistory = loginHistoryRepository
				.findLastLoginByUserId(userId);
		if (loginHistory != null)
			lastLogin = loginHistory.getLoginDatetime();
		logger.debug("End:Inside getUserLastLogin of UserService");
		return lastLogin;
	}

	/**
	 * This method is used to save user details to database
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public boolean adduser(UserT user) throws Exception {
		logger.debug("Begin:Inside adduser() of Userservice");
		return userRepository.save(user) != null;
	}


	/**
	 * This method is used to add login history
	 * @param loginHistory
	 * @return
	 */

	public boolean addLoginHistory(LoginHistoryT loginHistory) {
		logger.debug("Begin:Inside addLoginHistory() of Userservice");
		LoginHistoryT managedLoginHistory = loginHistoryRepository
				.save(loginHistory);
		if (managedLoginHistory == null){
			logger.debug("End:Inside addLoginHistory() of Userservice");
			return false;
		}
		else{
			logger.debug("End:Inside addLoginHistory() of Userservice");
			return true;
		}
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
		logger.debug("Begin:Inside findByUserIdAndSessionId() of Userservice");
		LoginHistoryT loginHistory = null;
		if (userId != null && sessionId != null) {
			loginHistory = loginHistoryRepository.findByUserIdAndSessionId(
					userId, sessionId);
		}
		logger.debug("End:Inside findByUserIdAndSessionId() of Userservice");
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
	public void updateUser(UserT userUpdateList) {
		logger.debug("Begin:Inside updateUser() service");
		userRepository.save(userUpdateList);
		logger.debug("End:Inside updateUser() service");
	}
	
	
	/**
	 * This method is used to update user details
	 * @param user
	 */
	public void updateUser(List<UserT> userUpdateList) {
		logger.debug("Begin:Inside updateUser() service");
		userRepository.save(userUpdateList);
		logger.debug("End:Inside updateUser() service");
	}
	
	/**
	 * This service deletes user details from user_t
	 * 
	 * @param userList
	 * @param keyword
	 * @throws Exception
	 */
	public void deleteUser(List<UserT> userDeleteList) {
		logger.debug("Begin:Inside deleteUser method of UserService");
	    userRepository.save(userDeleteList);
		logger.debug("End:Inside deleteUser method of UserService");
	}

	/**
	 * This method is used to retrieve user details based on userId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UserT findByUserId(String userId) throws Exception {
		logger.debug("Begin:Inside findByUserId() service");
		UserT dbUser = userRepository.findOne(userId);
		populateUser(dbUser);
		logger.debug("End:Inside findByUserId() service");
		return dbUser;
	}

	/**
	 * This method is used to populate user module access
	 * 
	 * @param dbUser
	 */
	private void populateUser(UserT dbUser) {
		logger.debug("Begin:Inside populateUser() service");
		List<UserModuleAccessT> userModuleAccessTList = dbUser.getUserModuleAccessTs();
		UserModuleAccess userModuleAccess = new UserModuleAccess();
		for(UserModuleAccessT userModuleAccessT : userModuleAccessTList){
			String moduleName = userModuleAccessT.getModuleSubModuleT().getModuleT().getModuleName();
			String subModuleName = userModuleAccessT.getModuleSubModuleT().getSubModuleName();
			UserModule userModule = userModuleAccess.getModule(moduleName) != null ? userModuleAccess.getModule(moduleName) : new UserModule(moduleName);
			Set<String> submodules = CollectionUtils.isEmpty(userModule.getSubModuleName()) ?  new HashSet<String>() : userModule.getSubModuleName();
			submodules.add(subModuleName);
			userModule.setSubModuleName(submodules);
			userModuleAccess.addModule(userModule);
		 }
		 dbUser.setUserModuleAccess(userModuleAccess);
		 logger.debug("End:Inside populateUser() service");
	}

	/**
	 * This method is used to retrieve user details based on role of user
	 * @param userRole
	 * @return
	 * @throws Exception
	 */
	public List<UserT> getUsersByRole(String userRole) throws Exception {
		logger.debug("Inside getUsersByRole() service");
		return userRepository.findByUserRole(userRole);
	}

	/**
	 * This method is used to check if a given user is System Admin 
	 * @param userId
	 * @return
	 */
	public boolean isSystemAdmin(String userId) {
		logger.debug("Inside isSystemAdmin() service");
		return isUserWithRole(userId, UserRole.SYSTEM_ADMIN.getValue());

	}

	/**
	 * This method is used to validate if a given user has the specified role
	 * @param userId
	 * @param userRole
	 * @return
	 */
	private boolean isUserWithRole(String userId, String userRole) {
		logger.debug("Inside isUserWithRole() service");
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
		logger.debug("Begin:Inside forgotPassword() of Userservice");
		//get active user by id
		UserT user = userRepository.findByActiveTrueAndUserId(userId);
		if (user != null) {
			String retrievedMailId = user.getUserEmailId();
			if (retrievedMailId.equalsIgnoreCase(userEmailId)) {
				mailUtils.sendPasswordAutomatedEmail(forgotPasswordSubject,
						user, new Date());
			} else {
				logger.error("UserId and E-Mail address do not match");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"UserId and E-Mail address do not match");
			}
		} else {
			logger.error("NOT_FOUND: User not found or inactive: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND, "User not found or inactive: " + userId);
		}
		logger.debug("End:Inside forgotPassword() of Userservice");
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
		logger.debug("Inside getAllChildPrivilegesByUserIdAndParentPrivilegeId() of Userservice");
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
		logger.debug("Begin:inside insertUser() of UserService");
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			logger.debug("End:inside insertUser() of UserService: user Saved : " + user.getUserId());
			return true;
		}
		logger.debug("End:inside insertUser() of UserService: user not Saved");
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
		logger.debug("Begin:inside validateUser() of UserService");
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
		logger.debug("End:inside validateUser() of UserService");
	}

	/**
	 * This method is used to retrieve users with a specific role 
	 * @param roles
	 * @return
	 */
	public List<UserT> getByUserRoles(List<String> roles) {

		logger.debug("Begin:Inside findByUserRole() of UserService");

		List<UserT> users = (List<UserT>) userRepository.findByUserRoles(roles);

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}
		logger.debug("End:Inside findByUserRole() of UserService");
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
		Set<String> userIds = new HashSet<String>();
		List<UserGeneralSettingsT> settingsList = new ArrayList<UserGeneralSettingsT>();
		for(UserGeneralSettingsT settingsT : insertList){
			if(!userIds.contains(settingsT.getUserId())){
				userIds.add(settingsT.getUserId());
				settingsList.add(settingsT);
			}
		}
		
		userGeneralSettingsRepository.save(settingsList);
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
		List<UserSubscriptions> userNotificationSettingsList=new ArrayList<UserSubscriptions>();
		
		Map<String, NotificationTypeEventMappingT> notifyTypeEventMap = getNotifyTypeEventMappings();
		Set<String> userIds = new HashSet<String>();
		
		for(UserT user:userList)
		{
			if(!userIds.contains(user.getUserId())){
			  userIds.add(user.getUserId());
			  userNotificationSettingsList.addAll(DestinationUserDefaultObjectsHelper.getUserNotificationSettingsList(user, notifyTypeEventMap));
			} 
		}
		userSubscriptionsRepository.save(userNotificationSettingsList);
		logger.debug("User Notification Settings : saved");

	}

	public Map<String, NotificationTypeEventMappingT> getNotifyTypeEventMappings() {
		List<NotificationTypeEventMappingT> notifyTypeEventMappings = (List<NotificationTypeEventMappingT>) notificationTypeEventMappingRepository.findAll();
		Map<String, NotificationTypeEventMappingT> notifyTypeEventMap = mapNotifyTypeEventMapping(notifyTypeEventMappings);
		return notifyTypeEventMap;
	}

	private static Map<String, NotificationTypeEventMappingT> mapNotifyTypeEventMapping(
			List<NotificationTypeEventMappingT> notifyTypeEventMappings) {
		Map<String, NotificationTypeEventMappingT> map = Maps.newHashMap();
		for (NotificationTypeEventMappingT item : notifyTypeEventMappings) {
			map.put(String.format("%d%d", item.getEventId(),item.getModeId()) , item);
		}
		return map;
	}

	/**
	 * This service saves user details into user_access_priviledges_t
	 * @param userAccessPrivilegeDTOList
	 * @throws Exception
	 */
	public void saveAccessPriviledgeSettings(List<UserAccessPrivilegeDTO> userAccessPrivilegeDTOList,List<UserT> usersList) throws Exception 
	{
		logger.debug("Inside save access privileges method");

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

		for(UserGoalsT userGoalTToBeUpdated:goalList)
		{
			String userIdGoalSheet=userGoalTToBeUpdated.getUserId();
			BigDecimal targetValueInExcel=userGoalTToBeUpdated.getTargetValue();
			String goalName=userGoalTToBeUpdated.getGoalMappingT().getGoalName();
			String financialYear=userGoalTToBeUpdated.getFinancialYear();
			String goalId=goalMappingRepository.findGoalIdByGoalNameAndFinancialYear(goalName,financialYear);
			
			if(!StringUtils.isEmpty(goalId)){
			List<UserGoalsT> userGoalsList = userGoalsRepository.getUserGoals(userIdGoalSheet, goalId, financialYear);
			if(CollectionUtils.isNotEmpty(userGoalsList)){
			if(!goalId.equals("G5")){
				UserGoalsT userGoalT = userGoalsList.get(0);
				userGoalT.setTargetValue(targetValueInExcel);
				userGoalsRepository.save(userGoalT);
			}
			}
			if(goalId.equals("G4"))
			{
				List<UserGoalsT> goalG5List=userGoalsRepository.getUserGoals(userIdGoalSheet, "G5", financialYear);//(userIdGoalSheet,financialYear);
				if(CollectionUtils.isNotEmpty(goalG5List)){
				UserGoalsT goalG5 = goalG5List.get(0);
				goalG5.setTargetValue(targetValueInExcel.multiply(new BigDecimal(5)));
				UserGoalsT savedGoal = userGoalsRepository.save(goalG5);
				logger.info("{}  - g5 - multiplied value :  {}",userIdGoalSheet , savedGoal.getTargetValue().toString());
				}
			}
		} else {
			logger.info("Unable to fetch Goal : {} for {}",goalName,financialYear);
		}
		}
		logger.info("** user goals saved **");
   }
	/**
	 * This service saves default user goal details into user_goals_t
	 * @param usersList
	 * @param createdModifiedBy
	 */

	public void insertDefaultGoals(List<UserT> usersList,String createdModifiedBy){
		if(usersList!=null){
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

	/**
	 * This method is used to search user details for the given userId/userName or supervisorId/Name or userGroup or base location
	 * 
	 * @param userNameOrId
	 * @param supervisorNameOrId
	 * @param userGroup
	 * @param baseLocation 
	 * @param count 
	 * @param page 
	 * @return
	 */
	public PaginatedResponse searchUserDetails(String userNameOrId, String supervisorNameOrId, String userGroup, String baseLocation, int page, int count) {
		logger.info("Begin:Inside searchUserDetails UserService");
		List<UserT> userTs = null;
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		if(!userNameOrId.equals("")){
			userTs = userRepository.findByUserNameOrUserId("%" + userNameOrId.toUpperCase() + "%");
		} else if(!supervisorNameOrId.equals("")){
			userTs = userRepository.findBySupervisorNameOrId("%" + supervisorNameOrId.toUpperCase() + "%");
		} else if(!userGroup.equals("")){
			userTs = userRepository.findByUserGroup(userGroup);
		} else if(!baseLocation.equals("")){
			userTs = userRepository.findByBaseLocationIgnoreCaseContainingOrderByUserNameAsc(baseLocation);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Please Enter Search Details");
		}
		if (userTs.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No matching user found");
		}
		paginatedResponse.setTotalCount(userTs.size());

		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count, userTs.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count, userTs.size());
			int toIndex = PaginationUtils.getEndIndex(page, count, userTs.size()) + 1;
			userTs = userTs.subList(fromIndex, toIndex);
			paginatedResponse.setUserTs(userTs);
			logger.debug("users after pagination size is " + userTs.size());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "No users available for the specified page");
		}
		return paginatedResponse;
	}

	/**
	 * This method is used to insert new user details into database
	 * 
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Transactional
	public boolean insertUserDetails(UserT user) throws Exception {
		logger.info("Begin:inside insertUserDetails() method");
		checkIfUserAlreadyExist(user);
	//	checkIfClusterHeadAlreadyExist(user);
	//	checkIfCentreHeadAlreadyExist(user);
		user.setTempPassword(getTempPassword());
		user.setStatus(0);
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			saveOrUpdateUserGeneralSettings(user);//save user general settings
			saveUserPrivileges(user);//save user access privileges
			saveOrUpdateUserGoals(user);//save user goals
			saveDefaultNotificationSettings(user);//save default notification settings
			
			updateDeliveryHead(user); //update the delivery head
			
			logger.info("End:inside insertUserDetails() of UserService: user Saved : " + user.getUserId());
			return true;
		} else {
			logger.info("End:inside insertUserDetails() of UserService: user not Saved");
			return false;
		}
	}
	
	/**
	 * this method is used to update Delivery Head
	 * @param user
	 */
	
	
	private void updateDeliveryHead(UserT user) {
		logger.info("Inside updateDeliveryHead() method");
		
		if(user.getDeliveryClusterId() != null)
		{
			DeliveryClusterT deliveryClusterT = deliveryClusterRepository.findByDeliveryClusterId(user.getDeliveryClusterId());
			deliveryClusterT.setDeliveryClusterHead(user.getUserId());
			deliveryClusterRepository.save(deliveryClusterT);
			logger.info("End:inside updateDeliveryClusterHead() of UserService: cluster head saved : " + user.getDeliveryClusterId());
		}
		else if(CollectionUtils.isNotEmpty(user.getDeliveryCentreId()))
		 {
			 DeliveryCentreT deliveryCentreT = deliveryCentreRepository.findByDeliveryCentreId(user.getDeliveryCentreId().get(0));
			 deliveryCentreT.setDeliveryCentreHead(user.getUserId());
			 deliveryCentreRepository.save(deliveryCentreT);
			 logger.info("End:inside updateDeliveryCentreHead() of UserService: centre head saved : " + user.getDeliveryCentreId());
		 }
		 logger.info("End of updateDeliveryHead() method");
	}
	

	



		
			
	/**
	 * This method is used to verify whether userId is already present in database
	 * 
	 * @param user
	 */
	private void checkIfUserAlreadyExist(UserT user) {
		logger.info("Begin:inside checkIfAlreadyExist() method");
		UserT usert = userRepository.findByUserId(user.getUserId());
		if(usert!=null){
			logger.info("BAD_REQUEST, UserId Already Exist");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "UserId Already Exist");
		}
	}

	/**
	 * This method is used to get temporary password based on environment
	 * @return
	 */
	private String getTempPassword() {
		logger.info("Inside getTempPassword() method");
		String tempPassword=com.tcs.destination.utils.StringUtils.generateRandomString(defaultPasswordLength);
		
		return tempPassword;
	}

	/**
	 * This Method is used to save the default user general settings and notifications
	 *  
	 * @param user
	 */
	private void saveDefaultNotificationSettings(UserT user) {
		logger.info("Inside saveDefaultNotificationSettings() method");
		//saving user notification settings for the user
		List<UserSubscriptions> userNotificationSettingsList = DestinationUserDefaultObjectsHelper
				.getUserNotificationSettingsList(user, getNotifyTypeEventMappings());
		userSubscriptionsRepository.save(userNotificationSettingsList);
		logger.info("User Notification Settings : saved");
	}

	/**
	 * This method is used to save or update user general settings 
	 * 
	 * @param user
	 */
	private void saveOrUpdateUserGeneralSettings(UserT user) {
		logger.info("Inside saveOrUpdateUserGeneralSettings() method");
		//saving user general settings for the user
		UserGeneralSettingsT userGenSettings = DestinationUserDefaultObjectsHelper
				.getDefaultSettings(user.getUserId(),user.getUserGeneralSettingsT().getTimeZoneDesc());
		userGenSettingsRepository.save(userGenSettings);
		logger.info("User General Settings : saved");
	}

	/**
	 * This method is used to save user privileges
	 * 
	 * @param user
	 */
	private void saveUserPrivileges(UserT user) {
		logger.info("Inside saveUserPrivileges() method");
		//save parent privileges
		for (UserAccessPrivilegesT parentAccessPrivilege : user.getUserAccessPrivilegesTs()) {
			 saveParentChildPrivileges(user, parentAccessPrivilege);
		}
	}



	/**
	 * This method is used to save parent child privileges
	 * 
	 * @param user
	 * @param parentAccessPrivilege
	 */
	private void saveParentChildPrivileges(UserT user, UserAccessPrivilegesT parentAccessPrivilege) {
		parentAccessPrivilege.setPrivilegeType(parentAccessPrivilege.getPrivilegeType());
		parentAccessPrivilege.setPrivilegeValue(parentAccessPrivilege.getPrivilegeValue());
		parentAccessPrivilege.setUserId(user.getUserId());
		parentAccessPrivilege.setIsactive(Constants.Y);
		parentAccessPrivilege = userAccessPrivilegesRepository.save(parentAccessPrivilege);
		logger.info("Parent Privilege saved");
		Integer parentAccessPrivilegeId = parentAccessPrivilege.getPrivilegeId();
		//save child privilege
		for (UserAccessPrivilegesT childAccessPrivilege : parentAccessPrivilege.getUserAccessPrivilegesTs()) {
			childAccessPrivilege.setPrivilegeType(childAccessPrivilege.getPrivilegeType());
			childAccessPrivilege.setPrivilegeValue(childAccessPrivilege.getPrivilegeValue());
			childAccessPrivilege.setParentPrivilegeId(parentAccessPrivilegeId);
			childAccessPrivilege.setUserId(user.getUserId());
			childAccessPrivilege.setIsactive(Constants.Y);
			childAccessPrivilege = userAccessPrivilegesRepository.save(childAccessPrivilege);
			logger.info("Child Privilege saved");
		}
	}

	/**
	 * This method is used to update user details
	 * 
	 * @param user
	 * @return
	 * @throws Exception 
	 */
	@Transactional
	public boolean updateUserDetails(UserT user) throws Exception {
		logger.info("Begin:inside updateUserDetails() of UserService");
		UserT userT= userRepository.findByUserId(user.getUserId());
		user.setTempPassword(userT.getTempPassword());
		user.setStatus(userT.getStatus());
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			if(!StringUtils.equals(userT.getUserGroup(), user.getUserGroup())) {
				//modify settings, if usergroup changed
				saveOrUpdateUserGeneralSettings(user);//update user general settings
			}
			updateUserPrivileges(user);//update user access privileges
			saveOrUpdateUserGoals(user);//update user goals
			logger.info("End:inside updateUserDetails() of UserService: user Saved : " + user.getUserId());
			return true;
		} else {
			logger.info("End:inside updateUserDetails() of UserService: user not Saved");
			return false;
		}
	}

	
	/**
	 * This method is used to save/update user goals to data base
	 * 
	 * @param user
	 */
	private void saveOrUpdateUserGoals(UserT user) {
		logger.info("Inside saveOrUpdateUserGoals() methos");
		//saving user targets
		for (UserGoalsT userGoal : user.getUserGoalsTs1()) {
			userGoal.setUserId(user.getUserId());
			userGoal.setCreatedModifiedBy(DestinationUtils.getCurrentUserDetails().getUserId());
			userGoal.setFinancialYear(DateUtils.getCurrentFinancialYear());
			userGoalsRepository.save(userGoal);
			logger.info("user Goals saved/updated: " + userGoal.getGoalId());
		}
	}

	/**
	 * This method is used to update user privileges
	 * 
	 * @param user
	 */
	private void updateUserPrivileges(UserT user) {
		logger.info("Inside updateUserPrivileges() method");
		//To delete the user privileges
		deleteUserPrivileges(user);

		for (UserAccessPrivilegesT parentAccessPrivilege : user.getUserAccessPrivilegesTs()) {
			if(parentAccessPrivilege.getPrivilegeId()==null){
				saveParentChildPrivileges(user,parentAccessPrivilege);
			} else {
				parentAccessPrivilege.setPrivilegeId(parentAccessPrivilege.getPrivilegeId());
				parentAccessPrivilege.setPrivilegeType(parentAccessPrivilege.getPrivilegeType());
				parentAccessPrivilege.setPrivilegeValue(parentAccessPrivilege.getPrivilegeValue());
				parentAccessPrivilege.setUserId(user.getUserId());
				parentAccessPrivilege.setIsactive(Constants.Y);
				
				for (UserAccessPrivilegesT childAccessPrivilege : parentAccessPrivilege.getUserAccessPrivilegesTs()) {
					if(childAccessPrivilege.getPrivilegeId()==null){
						saveChildPrivilege(user, childAccessPrivilege);
					} else {
						childAccessPrivilege.setPrivilegeId(childAccessPrivilege.getPrivilegeId());
						saveChildPrivilege(user, childAccessPrivilege);
					}
					childAccessPrivilege = userAccessPrivilegesRepository.save(childAccessPrivilege);
					logger.info("Child Privileges Updated");
				}
			parentAccessPrivilege = userAccessPrivilegesRepository.save(parentAccessPrivilege);
			logger.info("Parent Privileges Updated");
			}
		}
		logger.info("End of updateUserPrivileges method");
	}



	/**
	 * This method is used to set child privileges to 
	 * 
	 * @param user
	 * @param childAccessPrivilege
	 */
	private void saveChildPrivilege(UserT user,
			UserAccessPrivilegesT childAccessPrivilege) {
		childAccessPrivilege.setPrivilegeType(childAccessPrivilege.getPrivilegeType());
		childAccessPrivilege.setPrivilegeValue(childAccessPrivilege.getPrivilegeValue());
		childAccessPrivilege.setParentPrivilegeId(childAccessPrivilege.getParentPrivilegeId());
		childAccessPrivilege.setUserId(user.getUserId());
		childAccessPrivilege.setIsactive(Constants.Y);
	}

	/**
	 * This method is used to delete user privileges
	 * 
	 * @param user
	 */
	private void deleteUserPrivileges(UserT user) {
		logger.info("Inside deleteUserPrivileges() method");
		if (user.getDeleteUserAccessPrivilegesTs()!=null) {
			for (UserAccessPrivilegesT userAccessPrivilegesT : user.getDeleteUserAccessPrivilegesTs()) {
				if(userAccessPrivilegesT.getPrivilegeId()!=null){
					UserAccessPrivilegesT accessPrivilegesT = userAccessPrivilegesRepository.findByPrivilegeId(userAccessPrivilegesT.getPrivilegeId());
					if(!accessPrivilegesT.getUserAccessPrivilegesTs().isEmpty()){
						userAccessPrivilegesRepository.delete(accessPrivilegesT.getUserAccessPrivilegesTs());
						logger.info("Child Privileges Deleted");
					}
					userAccessPrivilegesRepository.delete(accessPrivilegesT);
					logger.info("Parent Privileges Deleted");
				}
			}
		}
	}

	/**
	 * This method is used to retrieve all user groups
	 * 
	 * @return
	 */
	public ArrayList<UserGroupMappingT> findUserGroups() {
		logger.info("Inside findUserGroups() method");
		return (ArrayList<UserGroupMappingT>) userGroupMappingRepository.findAll();
	}

	/**
	 * This method is used to retrieve all user roles
	 * 
	 * @return
	 */
	public ArrayList<UserRoleMappingT> findUserRoles() {
		logger.info("Inside findUserRoles() method");
		return (ArrayList<UserRoleMappingT>) userRoleMappingRepository.findAll();
	}

	/**
	 * This method is used to retrieve privilege types
	 * 
	 * @return
	 */
	public String[] getPrivilegeType() {
		logger.info("Inside getPrivilegeType() method");
		String[] privilegeType = { PrivilegeType.GEOGRAPHY.getValue(),
				PrivilegeType.IOU.getValue(),PrivilegeType.SUBSP.getValue(),
				PrivilegeType.CUSTOMER.getValue(),PrivilegeType.GROUP_CUSTOMER.getValue(),
				PrivilegeType.DELIVERY_CENTRE.getValue(),PrivilegeType.DELIVERY_CLUSTER.getValue()};
		return privilegeType;
	}




	public PageDTO<SearchResultDTO<UserT>> smartSearch(SmartSearchType smartSearchType,
			String term, boolean getAll, int page, int count) {
		logger.info("UserService::smartSearch type {}",smartSearchType);
		PageDTO<SearchResultDTO<UserT>> res = new PageDTO<SearchResultDTO<UserT>>();
		List<SearchResultDTO<UserT>> resList = Lists.newArrayList();
		SearchResultDTO<UserT> searchResultDTO = new SearchResultDTO<UserT>();
		if(smartSearchType != null) {
			
			switch(smartSearchType) {
			case ALL:
				resList.add(getUsersByNumber(term, getAll));
				resList.add(getUserByName(term, getAll));
				resList.add(getUserBySupervisor(term, getAll));
				resList.add(getUserByLocation(term, getAll));
				break;
			case EMPNO:
				searchResultDTO = getUsersByNumber(term, getAll);
				break;
			case EMPNAME:
				searchResultDTO = getUserByName(term, getAll);
				break;
			case SUPERVISOR:
				searchResultDTO = getUserBySupervisor(term, getAll);
				break;
			case LOCATION:
				searchResultDTO = getUserByLocation(term, getAll);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid search type");

			}
			
			if(smartSearchType != SmartSearchType.ALL) {//paginate the result if it is fetching entire record(ie. getAll=true)
				if(getAll) {
					List<UserT> values = searchResultDTO.getValues();
					searchResultDTO.setValues(PaginationUtils.paginateList(page, count, values));
					res.setTotalCount(values.size());
				}
				resList.add(searchResultDTO);
			}
		}
		res.setContent(resList);
		return res;
	}

	private SearchResultDTO<UserT> getUserByLocation(String term, boolean getAll) {
		List<UserT> records = userRepository.searchByLocation("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.LOCATION);
	}

	private SearchResultDTO<UserT> getUserBySupervisor(String term,
			boolean getAll) {
		List<UserT> records = userRepository.searchBySupervisor("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.SUPERVISOR);
	}

	private SearchResultDTO<UserT> getUserByName(String term, boolean getAll) {
		List<UserT> records = userRepository.searchByUserName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.EMPNAME);
	}

	private SearchResultDTO<UserT> getUsersByNumber(String term, boolean getAll) {
		List<UserT> records = userRepository.searchByUserId("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.EMPNO);
	}
	
	private SearchResultDTO<UserT> createSearchResultFrom(
			List<UserT> records, SmartSearchType type) {
		SearchResultDTO<UserT> conRes = new SearchResultDTO<UserT>();
		conRes.setSearchType(type);
		conRes.setValues(records);
		return conRes;
	}
	
	
	/**
	 * This method returns the Last Login Date for the user
	 * 
	 * @return
	 */
	public LoginHistoryT getLastLoginDate() throws Exception {
		
		logger.debug("Begin : getLastLogin service");
		
		LoginHistoryT loginHistoryT = null;
		
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		
		// Get the Last Login Date
		loginHistoryT = loginHistoryRepository.findLastLoginDateByUserId(userId);
		
		logger.debug("End : getLastLogin service");
		
		return loginHistoryT;
		
	}

	/**
	 * This method retrieves the User Profile Details of the user
	 * 
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UserProfile getProfile(String userId) throws Exception {

		logger.debug("Begin : getProfile service");

		UserProfile userProfile = new UserProfile();
		UserT userT = null;

		UserT user = userRepository.findOne(userId);

		if ((user != null) && (userId.equals(user.getUserId()))) {

			// Get the User's Details
			userT = userRepository.findOne(userId);
			userProfile.setUserT(userT);

			// Get the User's Reportees
			List<UserT> reporteesList = userRepository
					.findSubordinatesBySupervisorId(userId);
			userProfile.setReportees(reporteesList);

			// Get the User's Hierarchy
			List<UserT> hierarchyList = userRepository
					.findUserHierarchy(userId);
			userProfile.setReportingHierarchy(hierarchyList);
		} else {
			logger.error("NOT FOUND: user NOT present : {} ",userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,"user NOT present : "+userId);
		}

		logger.debug("End : getProfile service");

		return userProfile;
	}
	
	/**
	 * This service helps in updating the user contacts of the user
	 * 
	 * @param userId
	 * @param userEmailId
	 * @param userTelephone
	 * @throws Exception
	 */
	public Status editContact(UserT userJSON) throws Exception{
		
		logger.debug("Start : Edit Contact And Base Location service");
		
		Status status = new Status();
		boolean flagUserLocation = false;
		boolean flagUserTelephone = false;
		boolean flagUserMobile = false;
		
		if(StringUtils.isEmpty(userJSON.getUserId())){
			logger.error("BAD_REQUEST: userId cannot be Empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"userId cannot be Empty");
		} else {
			
			// Get the details of exisiting users
			UserT userT = userRepository.findOne(DestinationUtils.getCurrentUserDetails().getUserId());	
			
			if((userT!=null) && (userJSON.getUserId().equals(userT.getUserId()))){ // If users are the same
				if(!StringUtils.isEmpty(userJSON.getBaseLocation())){ // If Base Location is empty
					if(!userJSON.getBaseLocation().equals(userT.getBaseLocation())){ // If Base Location is different
						userT.setBaseLocation(userJSON.getBaseLocation());
						flagUserLocation = true;
					}
				} else {
					logger.error("BAD REQUEST : Base Location cannot be empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,"Base Location cannot be empty");
				}
				if(userJSON.getUserTelephone()!=null){ // If User Telephone is null
					if(!userJSON.getUserTelephone().equals(userT.getUserTelephone())){ // If User Telephone is different
						userT.setUserTelephone(userJSON.getUserTelephone());
						flagUserTelephone = true;
					}
				} 
				if(userJSON.getUserMobile()!=null){ // If User Mobile is null
					if(!userJSON.getUserMobile().equals(userT.getUserMobile())){ // If User Mobile is different
						userT.setUserMobile(userJSON.getUserMobile());
						flagUserMobile = true;
					}
				} 
				
				if(flagUserLocation || flagUserTelephone || flagUserMobile){ // If any of the 3 flags are true
					userRepository.save(userT);
					status.setStatus(Status.SUCCESS, "Contact Information and Base Location Saved");
					logger.info("SUCCESS : Contact Details Saved");
				} else {
					status.setStatus(Status.FAILED, "No Contact Information or Base Location Saved");
					logger.info("FAILED : No Contact Details or Base Location Saved");
				}
				
				
			} else {
				logger.error("FORBIDDEN: user forbidden to make this request");
				throw new DestinationException(HttpStatus.FORBIDDEN,"user forbidden to make this request");
			}
		}
		
		logger.debug("End : Edit Contact And Base Location service");
		
		return status;
	}

	/**
	 * This service helps in escalate the supervisor Id and name changes to the 
	 * admin for veriification and acting upon the mentioned change
	 * 
	 * @param userJSON
	 * @return
	 * @throws Exception
	 */
	public Status escalateUserDetails(UserT userJSON) throws Exception{

		logger.debug("Start : escalate User Details service");
		
		Status status = new Status();

		if (StringUtils.isEmpty(userJSON.getUserId())) {
			logger.error("BAD_REQUEST: userId cannot be Empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"userId cannot be Empty");
		} else {
			
			UserT userT = userRepository.findOne(DestinationUtils.getCurrentUserDetails().getUserId());
			
			if (userT.getUserId().equals(userJSON.getUserId())) { // If logged in user and user specified in JSON are true

				if(StringUtils.isEmpty(userJSON.getSupervisorUserId()) || StringUtils.isEmpty(userJSON.getSupervisorUserName())){
					logger.error("BAD_REQUEST: Supervisor UserId or Supervisor Username is Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Supervisor UserId or Supervisor Username is Empty");
				} else {
					StringBuilder existingValue = new StringBuilder();
					existingValue.append("Supervisor UserId : ");
					existingValue.append(userT.getSupervisorUserId());
					existingValue.append(", ");
					existingValue.append("Supervisor Username : ");
					existingValue.append(userT.getSupervisorUserName());
					existingValue.append("<br/>");
					
					StringBuilder newValue = new StringBuilder();
					newValue.append("Supervisor UserId : ");
					newValue.append(userJSON.getSupervisorUserId());
					newValue.append(", ");
					newValue.append("Supervisor Username : ");
					newValue.append(userJSON.getSupervisorUserName());
					newValue.append("<br/>");
	
					// send email
					sendEmailNotification(existingValue.toString(),newValue.toString(), userT);
					status.setStatus(Status.SUCCESS, "Email sent to Admin");
				}
				
			} else {
				logger.error("FORBIDDEN: userId forbidden to make this request");
				throw new DestinationException(HttpStatus.FORBIDDEN,"userId forbidden to make this request");
			}
		}

		logger.debug("End : escalate User Details service");
		return status;
	}

	/**
	 * method to send Email Notification for an request id
	 * 
	 * @param requestId
	 * @param date
	 * @throws Exception
	 */
	private void sendEmailNotification(String existingSupervisorDetails, String newSupervisorDetails, UserT userT) throws Exception {
		logger.debug("Begin:Inside sendEmailNotification for Escalate User Details to Admin");
		@Transactional
		class EscalateUserDetailsRunnable implements Runnable {
			String existingSupervisorDetails;
			String newSupervisorDetails;
			UserT userT;

			EscalateUserDetailsRunnable(String existingSupervisorDetails, String newSupervisorDetails, UserT userT) {
				this.existingSupervisorDetails = existingSupervisorDetails;
				this.newSupervisorDetails = newSupervisorDetails;
				this.userT = userT;
			}

			@Override
			public void run() {
				try {
					mailUtils.sendEscalateUserDetailsAutomatedEmail(existingSupervisorDetails, newSupervisorDetails, userT);
				} catch (Exception e) {
					logger.error("Error sending email for Escalate User Details to Admin"+e.getMessage());
				}
			}

		}
		EscalateUserDetailsRunnable escalateUserDetailsRunnable = new EscalateUserDetailsRunnable(existingSupervisorDetails, newSupervisorDetails, userT);
		mailTaskExecutor.execute(escalateUserDetailsRunnable);
		logger.debug("End:Inside sendEmailNotification for Escalate User Details to Admin");
	}

	/**
	 * Update Photo service in User Profile
	 * 
	 * @param userJSON
	 * @return
	 * @throws Exception
	 */
	public Status updatePhoto(UserT userJSON) throws Exception{
		
		logger.debug("Start : Update Photo service");
		
		Status status = new Status();
		
		if(StringUtils.isEmpty(userJSON.getUserId())){
			logger.error("BAD_REQUEST: userId cannot be Empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"userId cannot be Empty");
		} else {
			
			UserT userT = userRepository.findOne(DestinationUtils.getCurrentUserDetails().getUserId());	
			
			if((userT!=null) && (userJSON.getUserId().equals(userT.getUserId()))){
				
				if(userJSON.getUserPhoto()!=null) { // to save photo if exists
					userT.setUserPhoto(userJSON.getUserPhoto());
					userRepository.save(userT);
					status.setStatus(Status.SUCCESS, "Photo Uploaded Successfully");
					logger.info("SUCCESS : Photo Uploaded Successfully");
				} else { // No photo found to upload
					status.setStatus(Status.FAILED, "No Photo to upload");
					logger.info("FAILED : No Photo to upload");
				}
				
			} else {
				logger.error("FORBIDDEN: userId forbidden to make this request");
				throw new DestinationException(HttpStatus.FORBIDDEN,"userId forbidden to make this request");
			}
		}
		
		logger.debug("End : Update Photo service");
		
		return status;
	}


	/**
	 * This service retrieves the user details based on the fields provided
	 * 
	 * @param userId
	 * @return
	 */
	
	public UserT getUserDetailsById(String userId) {
		
		UserT userT = userRepository.findByUserId(userId);
	
		if(StringUtils.isEmpty(userId)){ // If userId is empty
			logger.error("BAD_REQUEST: userId cannot be Empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,"userId cannot be Empty");
		} else {
			// Hiding the password
			if(userT!=null){
				userT.setTempPassword("");
			} else { // If NO user matches the userId
				logger.error("NOT FOUND: user details NOT found : {} ",userId);
				throw new DestinationException(HttpStatus.NOT_FOUND,"user NOT found : "+userId);
			}
		}
		
		return userT;
		
	}

}