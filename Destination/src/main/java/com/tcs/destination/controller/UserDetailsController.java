package com.tcs.destination.controller;

import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.LoginHistoryT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.SubSpService;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/user")
public class UserDetailsController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserDetailsController.class);

	@Autowired
	UserService userService;

	@Autowired
	ApplicationContext appContext;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith) throws Exception {
		logger.debug("Inside UserDetailsController /user GET");
		if (nameWith.equals("")) {
			logger.debug("nameWith is EMPTY");
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					DestinationUtils.getCurrentUserDetails());
		} else {
			List<UserT> user = userService.findByUserName(nameWith);
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view, user);
		}
	}
	
	@RequestMapping(value="/login", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> getUserNotifications(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		
		    logger.debug("Inside UserDetailsController /User/login GET");
		    Status status = new Status();
		    status.setStatus(Status.FAILED, "");
		    UserT user=DestinationUtils.getCurrentUserDetails();
		    Timestamp lastLogin=userService.getUserNotification(user.getUserId());
		    if(lastLogin == null)
			{
				throw new DestinationException(HttpStatus.NOT_FOUND,"No Previous Login History found for this User");
			}
		    user.setLastLogin(lastLogin);
			LoginHistoryT loginHistory=new LoginHistoryT();
			loginHistory.setUserId(user.getUserId());
			if(!(userService.addLoginHistory(loginHistory)))
			{
				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"Internal Server Error");
			}
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							user), HttpStatus.OK);
	   }

}
