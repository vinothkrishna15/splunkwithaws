package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.service.SubSpService;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.Constants;

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
			return Constants.filterJsonForFieldAndViews(fields, view,
					Constants.getCurrentUserDetails());
		} else {
			List<UserT> user = userService.findByUserName(nameWith);
			return Constants.filterJsonForFieldAndViews(fields, view, user);
		}
	}

}
