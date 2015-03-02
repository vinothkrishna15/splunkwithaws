package com.tcs.destination.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.exception.UnAuthorizedException;

@RestController
@RequestMapping("/l")
public class UnAuthorizedController {

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String unAuthorized() {
		throw new UnAuthorizedException();
	}
}
