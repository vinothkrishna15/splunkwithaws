package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ApplicationSettingsT;
import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ApplicationSettingsRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ApplicationSettingsService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.service.UserUploadService;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

@RestController
@RequestMapping("/user")
public class UserDetailsController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserDetailsController.class);

	@Autowired
	UserService userService;
	
	@Autowired
	UserUploadService userUploadService;

	@Autowired
	ApplicationSettingsService applicationSettingsService;

	@Autowired
	ApplicationSettingsRepository applicationSettingsRepository;
	
	@Autowired
	UploadErrorReport uploadErrorReport;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith)
			throws Exception {
		logger.debug("Inside UserDetailsController /user GET");
		
		if (nameWith.equals("")) {
			logger.debug("nameWith is EMPTY");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, DestinationUtils.getCurrentUserDetails());
		} else {
			List<UserT> user = userService.findByUserName(nameWith);
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, user);
		}
		
	}

	/**
	 * This method is used to validate User Login
	 * Also saves Login SessionId, Date Time, Device, Browser details
	 * @param userName is the login user name.
	 * @return Login response.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> userLogin(
			HttpServletRequest httpServletRequest,
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "appVersion", defaultValue = "") String appVersion)
			throws Exception {

		logger.debug("Inside UserDetailsController /user/login POST");
		UserT user = userService.findUserByName(userName);
		if (user != null) {
			// Log Username for debugging
			logger.info("Username: {}", userName);
			HttpSession session = httpServletRequest.getSession(false);
			if (session == null) {
				logger.info("Session is null, creating new session");
				session = httpServletRequest.getSession();

			}
			logger.info("sessionId: {}", session.getId());

			// Check if the User has already logged in with the same session
			if (userService.findByUserIdAndSessionId(user.getUserId(),
					session.getId()) != null) {
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User has already logged in with the same session");
			}

			// Set the custom TimeOut
			ApplicationSettingsT appSettings = applicationSettingsRepository
					.findOne(Constants.TIME_OUT);
			logger.debug("Time_Out Interval: {}", appSettings.getValue());
			session.setMaxInactiveInterval(Integer.parseInt(appSettings
					.getValue()) * 60);
			logger.debug("Session Timeout: {}", session.getMaxInactiveInterval());

			// Get Last Login Time
			Timestamp lastLogin = userService
					.getUserLastLogin(user.getUserId());
			if (lastLogin != null)
				user.setLastLogin(lastLogin);

			// Get Browser, Device details from request header
			logger.info("UserAgent : "
					+ httpServletRequest.getHeader("User-Agent"));
			UserAgent userAgent = UserAgent
					.parseUserAgentString(httpServletRequest
							.getHeader("User-Agent"));
			Browser browser = null;
			String browserName = null;
			String browserVersion = null;
			if (userAgent.getBrowser() != null) {
				browser = userAgent.getBrowser();
				if (browser != null && browser.getName() != null)
					browserName = browser.getName();
			}
			if (userAgent.getBrowserVersion() != null)	
				browserVersion = userAgent.getBrowserVersion().getVersion();
			logger.info("Browser: {}, Version: {}", browserName, browserVersion);
			
			// Get OS details
			OperatingSystem os = null;
			String osName = null;
			short osVersion = 0; 
			if (userAgent.getOperatingSystem() != null) {
				os = userAgent.getOperatingSystem();
				if (os != null) { 
					osVersion = os.getId();
					if (os.getName() != null)
						osName = os.getName();
				}
			}

			logger.info("OS: {}, Version: {}", os, (byte) osVersion);

			// Get Device details
			String device = null;
			if (os.getDeviceType() != null) {
				if (os.getDeviceType().getName() != null)
					device = os.getDeviceType().getName();
			}
			logger.info("Device: {}", device);

			// Save current login session
			LoginHistoryT loginHistory = new LoginHistoryT();
			loginHistory.setUserId(user.getUserId());
			loginHistory.setSessionId(session.getId());
			loginHistory.setBrowser(browserName);
			loginHistory.setBrowserVersion(browserVersion);
			loginHistory.setOs(osName);
			loginHistory.setOsVersion(Integer.toString((byte) osVersion));
			loginHistory.setDevice(device);
			if (appVersion != null && !appVersion.isEmpty()) {
				logger.info("App Version: {}", appVersion);
				loginHistory.setAppVersion(appVersion);
			}
				
			if (!userService.addLoginHistory(loginHistory)) {
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR,
						"Could not save Login History");
			}
		}

		// Setting Application Settings in Response Header
		List<ApplicationSettingsT> applicationSettingsTs = applicationSettingsService
				.findAll();

		HttpHeaders headers = new HttpHeaders();
		if (applicationSettingsTs != null && !applicationSettingsTs.isEmpty()) {
			for (ApplicationSettingsT applicationSettingsT : applicationSettingsTs) {
				headers.add(applicationSettingsT.getKey(),
						applicationSettingsT.getValue());
			}
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						user), headers, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> userLogout(HttpServletRequest httpServletRequest)
		throws Exception {
		logger.debug("Inside UserDetailsController /user/logout GET");
		Status status = new Status();
		HttpSession session = httpServletRequest.getSession(false);
		if (session != null) {
			logger.info("Logging out User Session: {}", session.getId());
			session.invalidate();
			status.setStatus(Status.SUCCESS, "Session logged out");
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,"No valid session to log out");
		}
		
		return new ResponseEntity<String>
			(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	}

	@RequestMapping(value = "/changepwd", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> changePassword(HttpServletRequest httpServletRequest,@RequestBody UserT user)
	throws Exception {
		logger.debug("Inside UserDetailsController /user/changepwd PUT");
		Status status = new Status();
		String userId = user.getUserId();
		
		String currentlyLoggedInUser = DestinationUtils.getCurrentUserDetails().getUserId();
		if(currentlyLoggedInUser.equals(userId)){
		String currentPassword = user.getTempPassword();
		String newPassword = user.getNewPassword();
		//getting session object, if exist 
		HttpSession session = httpServletRequest.getSession(false);
		if (session != null) {
			//valid session
			UserT dbUser = userService.findByUserIdAndPassword(userId,currentPassword);
			if(dbUser!=null){
				dbUser.setTempPassword(newPassword);
				userService.updateUser(dbUser);
				status.setStatus(Status.SUCCESS, "Password has been updated successfully");
				//invalidate session to force user to re-authenticate with updated password
				session.invalidate();
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND,"User not found");
			}
		} else {
			throw new DestinationException(HttpStatus.UNAUTHORIZED,"User not in a valid session");
		}
		
		return new ResponseEntity<String>
		(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	}
	else {
		throw new DestinationException(HttpStatus.UNAUTHORIZED,"Not authorized to make changes");
	}
	}
	
	@RequestMapping(value = "/forgotpwd", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> forgotPassword(@RequestBody UserT user) throws Exception{
		logger.info("Inside UserDetailsController /user/forgotpwd POST");
		Status status = new Status();
		
		String userId = user.getUserId();
		String userEmailId = user.getUserEmailId();
		logger.info("userId : " + userId + ", userEmailId : " + userEmailId);
		userService.forgotPassword(userId,userEmailId);
		status.setStatus(Status.SUCCESS, "Password has been sent to the email address");
		
		return new ResponseEntity<String>
		(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	}
	
	@RequestMapping(value="/privileges",method=RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getPrivileges(
			HttpServletRequest httpServletRequest,
			@RequestParam(value = "userId") String userId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view) throws Exception{
		logger.debug("Inside UserDetailsController /user/privileges GET");
		Status status = new Status();
	    	
		List<UserAccessPrivilegesT> userPrivilegesList = userService.getAllPrivilegesByUserId(userId);
	    if(userPrivilegesList!=null && userPrivilegesList.isEmpty()){
	    	status.setStatus(Status.FAILED, "Invalid userId");
	    	return new ResponseEntity<String>
	    	(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	    } else {
	    	return new ResponseEntity<String>
	    	(ResponseConstructors.filterJsonForFieldAndViews(fields, view, userPrivilegesList), HttpStatus.OK);
	    }
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertUser(@RequestBody UserT user) throws Exception{
		logger.info("Inside UserDetailsController /user POST");
		Status status = new Status();
		
		if(userService.insertUser(user, false)){
			status.setStatus(Status.SUCCESS, user.getUserId());
			logger.debug("USER CREATED SUCCESS" + user.getUserId());
		}
		
		return new ResponseEntity<String>
		(ResponseConstructors.filterJsonForFieldAndViews("all", "", status), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadUser(
			@RequestParam("userId") String userId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Upload request Received : docName - ");
		UploadStatusDTO status = null;
	
			List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			status = userUploadService.saveDocument(file, userId);
			if (status != null && !status.isStatusFlag()) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO up : status.getListOfErrors()) {
					logger.error(up.getRowNumber() + "   " + up.getMessage());
				}
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		InputStreamResource excelFile = uploadErrorReport
				.getErrorSheet(errorDetailsDTOs);
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders
				.setContentType(MediaType
						.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		respHeaders.setContentDispositionFormData("attachment",
				"upload_error.xlsx");
		return new ResponseEntity<InputStreamResource>(excelFile, respHeaders,
				HttpStatus.OK);
	}
	


}
