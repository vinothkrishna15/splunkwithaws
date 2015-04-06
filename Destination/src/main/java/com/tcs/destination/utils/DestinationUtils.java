package com.tcs.destination.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.UserRepositoryUserDetailsService.UserRepositoryUserDetails;

public class DestinationUtils {

	
	public static UserT getCurrentUserDetails() {
		Authentication a = SecurityContextHolder.getContext()
				.getAuthentication();
		return ((UserRepositoryUserDetails) a.getPrincipal());
	}
	
}
