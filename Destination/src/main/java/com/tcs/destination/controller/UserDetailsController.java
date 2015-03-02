package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;

@RestController
@RequestMapping("/user")
public class UserDetailsController {

	@Autowired
	ApplicationContext appContext;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody UserDetails findOne() {
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
	    UserDetails currentUserDetails = (UserDetails) a.getPrincipal();
	    return currentUserDetails;
	}
}
