package com.tcs.destination.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserAccessPrivilegeDTO;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserGeneralSettingsT;
import com.tcs.destination.bean.UserGoalsT;
import com.tcs.destination.bean.UserGroupMappingT;
import com.tcs.destination.bean.UserModule;
import com.tcs.destination.bean.UserModuleAccess;
import com.tcs.destination.bean.UserModuleAccessT;
import com.tcs.destination.bean.UserNotificationSettingsT;
import com.tcs.destination.bean.UserRoleMappingT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.GoalGroupMappingRepository;
import com.tcs.destination.data.repository.GoalMappingRepository;
import com.tcs.destination.data.repository.LoginHistoryRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.data.repository.UserGoalsRepository;
import com.tcs.destination.data.repository.UserGroupMappingRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.UserRoleMappingRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.DestinationUserDefaultObjectsHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.StringUtils;
import com.tcs.destination.utils.PropertyUtil;

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
	UserGeneralSettingsRepository userGenSettingsRepository;

	@Autowired
	DestinationMailUtils mailUtils;
	
	@Autowired
	UserGroupMappingRepository userGroupMappingRepository;
	
	@Autowired
	UserRoleMappingRepository userRoleMappingRepository;

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
		UserT user = userRepository.findOne(userId);
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
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found: " + userId);
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
	public boolean insertUserDetails(UserT user) throws Exception {
		logger.info("Begin:inside insertUserDetails() method");
		checkIfUserAlreadyExist(user);
		user.setTempPassword(getTempPassword());
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			saveOrUpdateUserGeneralSettings(user);//save user general settings
			saveUserPrivileges(user);//save user access privileges
			saveOrUpdateUserGoals(user);//save user goals
			saveDefaultNotificationSettings(user);//save default notification settings
			logger.info("End:inside insertUserDetails() of UserService: user Saved : " + user.getUserId());
			return true;
		} else {
			logger.info("End:inside insertUserDetails() of UserService: user not Saved");
			return false;
		}
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
		String tempPassword=null;
		if(PropertyUtil.getProperty(Constants.ENVIRONMENT_NAME).equals(Constants.UAT)){
			tempPassword=Constants.TCS_UAT;
		} else if(PropertyUtil.getProperty(Constants.ENVIRONMENT_NAME).equals(Constants.PROD)){
			tempPassword=Constants.TCS_PROD;
		}else if(PropertyUtil.getProperty(Constants.ENVIRONMENT_NAME).equals(Constants.SIT)){
			tempPassword=Constants.TCS_SIT;
		} else {
			tempPassword=Constants.TCS_DEV;
		}
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
		List<UserNotificationSettingsT> userNotificationSettingsList = DestinationUserDefaultObjectsHelper
				.getUserNotificationSettingsList(user);
		userNotificationSettingsRepository.save(userNotificationSettingsList);
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
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			saveOrUpdateUserGeneralSettings(user);//update user general settings
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
				PrivilegeType.CUSTOMER.getValue(),PrivilegeType.GROUP_CUSTOMER.getValue()};
		return privilegeType;
	}
}