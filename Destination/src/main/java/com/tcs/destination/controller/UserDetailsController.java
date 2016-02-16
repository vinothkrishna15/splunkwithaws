package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ApplicationSettingsService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.service.UserDownloadService;
import com.tcs.destination.service.UserService;
import com.tcs.destination.service.UserUploadService;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.ResponseConstructors;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;

/**
 * This class deals with user details and other functionalities like login,
 * logout, change password, forget password, upload and download of User Details
 * 
 * @author tcs
 *
 */
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
	UserDownloadService userDownloadService;

	@Autowired
	ApplicationSettingsService applicationSettingsService;

	@Autowired
	UploadErrorReport uploadErrorReport;
	
	@Autowired
	private SessionRegistry sessionRegistry;
	
	@Value("${maximum.concurrent.user}")
	private int maxActiveSession;

	/**
	 * @param fields
	 * @param view
	 * @param nameWith
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith)
			throws DestinationException {
		logger.info("starting UserDetailsController findone method");
		try {
			if (nameWith.equals("")) {
				logger.info("Ending UserDetailsController findone method");
				return ResponseConstructors.filterJsonForFieldAndViews(fields,
						view, DestinationUtils.getCurrentUserDetails());
			} else {
				List<UserT> user = userService.findByUserName(nameWith);
				logger.info("Ending UserDetailsController findone method");
				return ResponseConstructors.filterJsonForFieldAndViews(fields,
						view, user);
			}

		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving user details");
		}
	}

	/**
	 * This method is used to validate User Login Also saves Login SessionId,
	 * Date Time, Device, Browser details
	 * 
	 * @param userName
	 *            is the login user name.
	 * @return Login response.
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> userLogin(
			HttpServletRequest httpServletRequest,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "appVersion", defaultValue = "") String appVersion)
			throws DestinationException {

		logger.info("starting UserDetailsController /user/login POST");
		try {
			List<SessionInformation> activeSessions = null;
			logger.info("httpServletRequest sessionId:"
					+ httpServletRequest.getSession(false).getId());
			activeSessions = new ArrayList<>();
			
			for (Object principal : sessionRegistry.getAllPrincipals()) {
				activeSessions.addAll(sessionRegistry.getAllSessions(principal,
						false));
			}
			logger.info("active session size:" + activeSessions.size());
			 /*
			 Max active session is commented as the sessions are not expired when timeout occurs
			i.e when the user leaves the application without logout.
			 */
			 
			if (maxActiveSession >= activeSessions.size()) {
				UserT user = userService.findByUserId(DestinationUtils
						.getCurrentUserDetails().getUserId());
				if (user != null) {
					// Log Username for debugging
					HttpSession session = httpServletRequest.getSession(false);
					if (session == null) {
						session = httpServletRequest.getSession();
					}

					// Check if the User has already logged in with the same
					// session
					if (userService.findByUserIdAndSessionId(user.getUserId(),
							session.getId()) != null) {
						throw new DestinationException(HttpStatus.FORBIDDEN,
								"User has already logged in with the same session");
					}

					logger.info("Session Timeout: {}",
							session.getMaxInactiveInterval());

					// Get Last Login Time
					Timestamp lastLogin = userService.getUserLastLogin(user
							.getUserId());
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
						browserVersion = userAgent.getBrowserVersion()
								.getVersion();
					logger.info("Browser: {}, Version: {}", browserName,
							browserVersion);

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
					loginHistory.setOsVersion(Integer
							.toString((byte) osVersion));
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
				if (applicationSettingsTs != null
						&& !applicationSettingsTs.isEmpty()) {
					for (ApplicationSettingsT applicationSettingsT : applicationSettingsTs) {
						headers.add(applicationSettingsT.getKey(),
								applicationSettingsT.getValue());
					}
				}
				logger.info("Ending UserDetailsController /user/login POST");
				return new ResponseEntity<String>(
						ResponseConstructors.filterJsonForFieldAndViews(fields,
								view, user), headers, HttpStatus.OK);
			} else {
				HttpSession maxreq_session = httpServletRequest
						.getSession(false);
				if (maxreq_session != null) {

					sessionRegistry.removeSessionInformation(httpServletRequest
							.getSession(false).getId());
					maxreq_session.invalidate();
				}
				logger.info("Maximum number of users reached.Please try after sometime");
				throw new DestinationException(
						HttpStatus.SERVICE_UNAVAILABLE,
						"Maximum number of users reached.Please try after sometime");
			}
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Backend Error while login process");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while login process");
		}
	}

	/**
	 * @param httpServletRequest
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> userLogout(
			HttpServletRequest httpServletRequest) throws DestinationException {
		try {
			logger.info("Starting UserDetailsController /user/logout GET");
			Status status = new Status();
			HttpSession session = httpServletRequest.getSession(false);
			if (session != null) {
				logger.info("Logging out User Session: {}", session.getId());
				session.invalidate();
				sessionRegistry.removeSessionInformation(session.getId());
				status.setStatus(Status.SUCCESS, "Session logged out");
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No valid session to log out");
			}
			logger.info("Ending UserDetailsController /user/logout GET");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (IllegalStateException e) {
			logger.error("Invalid session.");
			throw new DestinationException(HttpStatus.UNAUTHORIZED,
					"Invalid session");
		} catch (Exception e) {
			logger.error("Backend Error while logout process");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while logout process");
		}
	}

	/**
	 * @param httpServletRequest
	 * @param user
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/changepwd", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> changePassword(
			HttpServletRequest httpServletRequest, @RequestBody UserT user)
			throws DestinationException {
		logger.info("starting UserDetailsController /user/changepwd PUT");
		Status status = new Status();
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();

			String currentPassword = user.getTempPassword();
			String newPassword = user.getNewPassword();
			// getting session object, if exist
			HttpSession session = httpServletRequest.getSession(false);
			if (session != null) {
				// valid session
				UserT dbUser = userService.findByUserIdAndPassword(userId,
						currentPassword);
				if (dbUser != null) {
					dbUser.setTempPassword(newPassword);
					userService.updateUser(dbUser);
					status.setStatus(Status.SUCCESS,
							"Password has been updated successfully");
					// invalidate session to force user to re-authenticate with
					// updated password
					session.invalidate();
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"User not found");
				}
			} else {
				throw new DestinationException(HttpStatus.UNAUTHORIZED,
						"User not in a valid session");
			}
			logger.info("Ending UserDetailsController /user/changepwd PUT");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Backend Error while updating password");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while updating password");
		}
	}

	/**
	 * @param user
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/forgotpwd", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> forgotPassword(
			@RequestBody UserT user) throws DestinationException {
		logger.info("Starting UserDetailsController /user/forgotpwd POST");
		Status status = new Status();

		try {
			String userId = user.getUserId();
			String userEmailId = user.getUserEmailId();
			userService.forgotPassword(userId, userEmailId);
			status.setStatus(Status.SUCCESS,
					"Password has been sent to the email address");
			logger.info("Ending UserDetailsController /user/changepwd PUT");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Backend Error while processing forgot password");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while processing forgot password");
		}
	}

	/**
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/privileges", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getPrivileges(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		try {
			logger.info("Starting UserDetailsController /user/privileges GET");
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			Status status = new Status();

			List<UserAccessPrivilegesT> userPrivilegesList = userService
					.getAllPrivilegesByUserId(userId);
			if (userPrivilegesList != null && userPrivilegesList.isEmpty()) {
				status.setStatus(Status.FAILED, "Invalid userId");
				logger.info("Ending UserDetailsController /user/privileges GET: Invalid User");
				return new ResponseEntity<String>(
						ResponseConstructors.filterJsonForFieldAndViews("all",
								"", status), HttpStatus.OK);
			} else {
				logger.info("Ending UserDetailsController /user/privileges GET");
				return new ResponseEntity<String>(
						ResponseConstructors.filterJsonForFieldAndViews(fields,
								view, userPrivilegesList), HttpStatus.OK);
			}
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Backend Error while retrieving privileges");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while retrieving privileges");
		}
	}

	/**
	 * @param user
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertUser(
			@RequestBody UserT user) throws DestinationException {
		logger.info("Starting UserDetailsController /user POST");
		Status status = new Status();
		try {
			if (userService.insertUser(user, false)) {
				status.setStatus(Status.SUCCESS, user.getUserId());
			}
			logger.info("Ending UserDetailsController /user POST");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Backend Error while inserting user");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while inserting user");
		}
	}

	/**
	 * @param file
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadUser(
			@RequestParam("file") MultipartFile file,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			logger.info("Starting UserDetailsController/upload ");
			UploadStatusDTO status = null;

			List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;

			status = userUploadService.saveDocument(file, userId);
			if (status != null && !status.isStatusFlag()) {
				errorDetailsDTOs = status.getListOfErrors();
				for (UploadServiceErrorDetailsDTO up : status.getListOfErrors()) {
					logger.error(up.getRowNumber() + "   " + up.getMessage());
				}
			}

			InputStreamResource excelFile = uploadErrorReport
					.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
					.setContentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment",
					"upload_error.xlsx");
			logger.info("Starting UserDetailsController/upload ");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Backend Error while uploading users");
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend Error while uploading users");
		}
	}

	/**
	 * @param oppFlag
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadCustomerMaster(
			@RequestParam("downloadUsers") boolean oppFlag,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of Customer Details download");
		HttpHeaders respHeaders = null;
		InputStreamResource customerDownloadExcel = null;
		try {
			customerDownloadExcel = userDownloadService.getUsers(oppFlag);
			respHeaders = new HttpHeaders();
			String todaysDate_formatted = DateUtils
					.getCurrentDateInDesiredFormat();
			String environmentName=PropertyUtil.getProperty("environment.name");
			String repName =environmentName+"_UserDownload_" + todaysDate_formatted + ".xlsm";
			respHeaders.add("reportName", repName);
			respHeaders.setContentDispositionFormData("attachment",repName);
			respHeaders.setContentType(MediaType
					.parseMediaType("application/octet-stream"));
			logger.info("Start of Customer Details download: Success");
			return new ResponseEntity<InputStreamResource>(
					customerDownloadExcel, respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in downloading the customer details in excel");
		}
	}

}
