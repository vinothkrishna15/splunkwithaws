package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.LoginHistoryRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserGeneralSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
//import com.tcs.destination.helper.DestinationUserDefaultObjectsHelper;
import com.tcs.destination.utils.Constants;
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
	LoginHistoryRepository loginHistoryRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepository;

	UserGeneralSettingsRepository userGeneralSettingsRepository;

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
		logger.info("Begin:Inside findByUserName UserService");
		List<UserT> users = (List<UserT>) userRepository
				.findByUserNameIgnoreCaseLike("%" + nameWith + "%");

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}
		logger.info("End:Inside findByUserName Service");
		return users;
	}

	/**
	 * This method is used to retrieve user details based on user role
	 * @param role
	 * @return
	 * @throws Exception
	 */
	public List<String> findByUserRole(String role) throws Exception {
		logger.info("Begin:Inside findByUserRole Service");
		List<String> users = (List<String>) userRepository
				.findUserIdByUserRole(role);

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}
		logger.info("End:Inside findByUserRole Service");
		return users;
	}

	/**
	 * This method is used to retrieve user details based on user name
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public UserT findUserByName(String userName) throws Exception {
		logger.info("Begin:Inside findUserByName UserService");
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
		logger.info("End:Inside findUserByName UserService");
		return user;
	}

	/**
	 * This method is used to find the last login details of a specific user
	 * @param userId
	 * @return
	 */
	public Timestamp getUserLastLogin(String userId) {
		logger.info("Inside getUserLastLogin of UserService");
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

	/**
	 * This method is used to add login history
	 * @param loginHistory
	 * @return
	 */
	public boolean addLoginHistory(LoginHistoryT loginHistory) {
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
		logger.info("Begin:Inside findByUserIdAndSessionId() of Userservice");
		LoginHistoryT loginHistory = null;
		if (userId != null && sessionId != null) {
			loginHistory = loginHistoryRepository.findByUserIdAndSessionId(
					userId, sessionId);
		}
		logger.info("End:Inside findByUserIdAndSessionId() of Userservice");
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
		logger.info("Inside findByUserIdAndPassword() service");
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
		logger.info("Begin:Inside forgotPassword() of Userservice");
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
		logger.info("End:Inside forgotPassword() of Userservice");
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
		logger.info("Inside getAllPrivilegesByUserId() service");
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
		logger.info("Inside getAllChildPrivilegesByUserIdAndParentPrivilegeId() of Userservice");
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
		logger.info("Begin:inside insertUser() of UserService");
		// validate user
		validateUser(user, true);
		if (userRepository.save(user) != null) {
			logger.info("End:inside insertUser() of UserService: user Saved : " + user.getUserId());
			return true;
		}
		logger.info("End:inside insertUser() of UserService: user not Saved");
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
		logger.info("Begin:inside validateUser() of UserService");
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
		logger.info("End:inside validateUser() of UserService");
	}

	/**
	 * This method is used to retrieve users with a specific role 
	 * @param roles
	 * @return
	 */
	public List<UserT> getByUserRoles(List<String> roles) {

		logger.info("Begin:Inside findByUserRole() of UserService");

		List<UserT> users = (List<UserT>) userRepository.findByUserRoles(roles);

		if (users.isEmpty()) {
			logger.error("NOT_FOUND: No matching user found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No matching user found");
		}
		logger.info("End:Inside findByUserRole() of UserService");
		return users;
	}
}
