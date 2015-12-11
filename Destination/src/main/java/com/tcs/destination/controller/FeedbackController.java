package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.FeedbackT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.FeedbackService;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * This controller handles the feedback module
 * 
 * @author TCS
 *
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

	private static final Logger logger = LoggerFactory
			.getLogger(FeedbackController.class);

	@Autowired
	FeedbackService feedbackService;

	/**
	 * This method is used to get the feedback for the given feedback id
	 * 
	 * @param feedbackId
	 * @param fields
	 * @param view
	 * @return feedback
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String getFeedback(
			@PathVariable("id") String feedbackId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "feedback") String view)
			throws DestinationException {
		logger.info("Inside feedback controller : Start of retrieving the feedback");
		try {
			FeedbackT feedback = feedbackService.findFeedbackById(feedbackId);
			logger.info("Inside feedback controller : End of retrieving the feedback");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, feedback);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the feedback for id :"
							+ feedbackId);
		}
	}

	/**
	 * This method is used to insert a new feedback
	 * 
	 * @param feedback
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToFeedback(
			@RequestBody FeedbackT feedback) throws DestinationException {
		logger.info("Inside feedback controller : Start of insert feedback");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (feedbackService.insertFeedback(feedback)) {
				status.setStatus(Status.SUCCESS, feedback.getFeedbackId());
				logger.debug("Feedback created successfully"
						+ feedback.getFeedbackId());
			}
			logger.info("Inside feedback controller : End of insert feedback");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting the feedback");
		}

	}

	/**
	 * This method is used to update the existing feedback
	 * 
	 * @param feedback
	 * @return status
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editFeedback(
			@RequestBody FeedbackT feedback) throws DestinationException {
		logger.info("Inside feedback controller : Start of edit feedback");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (feedbackService.editFeedback(feedback)) {
				status.setStatus(Status.SUCCESS, feedback.getFeedbackId());
				logger.debug("Feedback updated successfully"
						+ feedback.getFeedbackId());
			}
			logger.info("Inside feedback controller : End of edit feedback");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while editing the feedback");
		}

	}

	/**
	 * This method retrieves the feedback based on the various filter options
	 * 
	 * @param titleWith
	 * @param descriptionWith
	 * @param issueType
	 * @param priority
	 * @param status
	 * @param module
	 * @param updatedUserId
	 * @param subModule
	 * @param fields
	 * @param view
	 * @return feedbackList
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getFilteredFeedbacks(
			@RequestParam(value = "titleWith", defaultValue = "") String titleWith,
			@RequestParam(value = "descriptionWith", defaultValue = "") String descriptionWith,
			@RequestParam(value = "issueType", defaultValue = "") String issueType,
			@RequestParam(value = "priority", defaultValue = "") String priority,
			@RequestParam(value = "status", defaultValue = "") String status,
			@RequestParam(value = "module", defaultValue = "") String module,
			@RequestParam(value = "updatedUserId", defaultValue = "") String updatedUserId,
			@RequestParam(value = "subModule", defaultValue = "") String subModule,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "feedback") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside feedback controller : Start of retrieving the filtered feedback");
		try {
			List<FeedbackT> feedbackList = feedbackService.findFeedbacksWith(
					titleWith, descriptionWith, issueType, priority, status,
					userId, module, updatedUserId, subModule);
			logger.info("Inside feedback controller : End of retrieving the filtered feedback");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, feedbackList);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the filtered feedback");
		}
	}

}
