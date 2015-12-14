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
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.FollowedService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This class handles the user tagged followed module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/follow")
public class FollowedController {

	private static final Logger logger = LoggerFactory
			.getLogger(FollowedController.class);

	@Autowired
	FollowedService followedService;

	/**
	 * This method retrieves the user tagged followed details for entity type
	 * given
	 * 
	 * @param entityType
	 * @param fields
	 * @param view
	 * @return userFollowed
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String findFavorite(
			@RequestParam("entityType") String entityType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of getting the user tagged followed details");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		try {
			List<UserTaggedFollowedT> userFollowed = followedService
					.findFollowedFor(userId, entityType);
			logger.info("End of getting the user tagged followed details");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, userFollowed);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the user tagged followed details for :"
							+ entityType);
		}
	}

	/**
	 * This method is used to add a user tagged followed for an entity
	 * 
	 * @param followed
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addFollowed(
			@RequestBody UserTaggedFollowedT followed,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of adding the user tagged followed details");
		followed.setCreatedModifiedBy(DestinationUtils.getCurrentUserDetails()
				.getUserId());
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (followedService.addFollow(followed)) {
				logger.debug("User FollowedId"
						+ followed.getUserTaggedFollowedId()
						+ "Inserted Successfully");
				status.setStatus(Status.SUCCESS,
						followed.getUserTaggedFollowedId());
			}
			logger.info("End of adding the user tagged followed details");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting the user tagged followed");
		}
	}

	/**
	 * This method gives the functionality to unfollow based on the
	 * userTaggedFollowedId
	 * 
	 * @param userTaggedFollowedId
	 * @param fields
	 * @param view
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	public @ResponseBody String unFollow(
			@RequestParam(value = "userTaggedFollowedId") String userTaggedFollowedId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Start of Deleting the user tagged followed details");
		Status status = new Status();
		try {
			followedService.unFollow(userTaggedFollowedId);
			status.setStatus(Status.SUCCESS, userTaggedFollowedId);
			logger.info("End of Deleting the user tagged followed details");
			return ResponseConstructors.filterJsonForFieldAndViews("all", "",
					status);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(
					HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while deleting the user tagged followed details for userTaggedFollowedId :"
							+ userTaggedFollowedId);
		}

	}

}