package com.tcs.destination.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.service.UserService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/user")
public class UserDetailsController {

	@Autowired
	UserService userService;

	@Autowired
	ApplicationContext appContext;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view,
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith) throws Exception {
		if (nameWith.equals("")) {
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
					DestinationUtils.getCurrentUserDetails());
		} else {
			List<UserT> user = userService.findByUserName(nameWith);
			return ResponseConstructors.filterJsonForFieldAndViews(fields, view, user);
		}
	}

}
