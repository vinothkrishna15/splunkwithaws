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
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationMailUtils;
import com.tcs.destination.data.repository.LoginHistoryRepository;

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
	DestinationMailUtils mailUtils;

	@Value("${forgotPassword}")
	private String forgotPasswordSubject;

	@Autowired
	CustomerService customerService;

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
//		user.setPreviledgedCustomerNameList(customerService
//				.getPreviledgedCustomerName(user.getUserId(), null, false));
		return user;
	}

	public Timestamp getUserLastLogin(String userId) {
		logger.debug("Inside getUserNotification Service");
		Timestamp lastLogin = null;
		LoginHistoryT loginHistory = loginHistoryRepository
				.findLastLoginByUserId(userId);
		if (loginHistory != null)
			lastLogin = loginHistory.getLoginDatetime();
		return lastLogin;
	}

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

	public UserT findByUserIdAndPassword(String userId, String password)
			throws Exception {
		logger.debug("Inside findByUserIdAndPassword() service");
		return (userRepository.findByUserIdAndTempPassword(userId, password));
	}

	public void updateUser(UserT user) {
		userRepository.save(user);
	}

	public UserT findByUserId(String userId) throws Exception {
		UserT dbUser = userRepository.findOne(userId);
		return dbUser;
	}

	public boolean isSystemAdmin(String userId) {
		return isUserWithRole(userId, UserRole.SYSTEM_ADMIN.getValue());

	}

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
	 * This service is used to get all the access privileges for a user
	 * 
	 * @param userId
	 * @return List of user access privileges
	 * @throws Exception
	 */
	public List<UserAccessPrivilegesT> getAllPrivilegesByUserId(String userId)
			throws Exception {
		logger.debug("Inside getAllPrivilegesByUserId() service");
		return (userAccessPrivilegesRepository
				.findByUserIdAndParentPrivilegeIdIsNull(userId));
	}

}
