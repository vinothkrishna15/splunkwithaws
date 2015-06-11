package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ApplicationSettingsT;
import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ApplicationSettingsService;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/user")
public class UserDetailsController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserDetailsController.class);

	@Autowired
	UserService userService;

	@Autowired
	ApplicationSettingsService applicationSettingsService;

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

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getUserNotifications(
			@RequestParam(value = "userName") String userName,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {

		logger.debug("Inside UserDetailsController /User/login GET");
		UserT user = userService.findUserByName(userName);
		if (user != null) {
			// Get Last Login Time
			Timestamp lastLogin = userService
					.getUserLastLogin(user.getUserId());
			if (lastLogin != null)
				user.setLastLogin(lastLogin);

			// Save current login session
			LoginHistoryT loginHistory = new LoginHistoryT();
			loginHistory.setUserId(user.getUserId());
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

}
