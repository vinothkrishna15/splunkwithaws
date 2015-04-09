package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UserTaggedFollowedT;
import com.tcs.destination.service.FollowedService;
import com.tcs.destination.utils.ResponseConstructors;


@RestController
@RequestMapping("/follow")
public class FollowedController {

	private static final Logger logger = LoggerFactory.getLogger(FollowedController.class);
	
	@Autowired
	FollowedService followedService;

	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFavorite(
			@RequestParam("userId") String userId,
			@RequestParam("entityType") String entityType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
			List<UserTaggedFollowedT> userFollowed = followedService.findFollowedFor(userId, entityType);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				userFollowed);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addFollowed(
			@RequestBody UserTaggedFollowedT followed,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside FollowedController /follow POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		System.out.println(followed.getEntityType());
		if (followedService.addFollow(followed)) {
			logger.debug("User FollowedId" + followed.getUserTaggedFollowedId()
					+ "Inserted Successfully");
			status.setStatus(Status.SUCCESS, followed.getUserTaggedFollowedId());
		}
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews(
				"all", "", status), HttpStatus.OK);
	}
}