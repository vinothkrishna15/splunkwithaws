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

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

	private static final Logger logger = LoggerFactory
			.getLogger(FeedbackController.class);

	@Autowired
	FeedbackService feedbackService;

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String getFeedback(
			@PathVariable("id") String feedbackId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "feedback") String view)
			throws DestinationException {
		logger.info("Start of retrieving the feedback");
		logger.debug("Inside searchforfeedbacksById service");
		try {
			FeedbackT feedback = feedbackService.findFeedbackById(feedbackId);
			logger.info("End of retrieving the feedback");
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

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToFeedback(
			@RequestBody FeedbackT feedback) throws DestinationException {
		logger.info("Start of insert feedback");
		logger.debug("Feedback Insert Request Received /feedback POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (feedbackService.insertFeedback(feedback)) {
				status.setStatus(Status.SUCCESS, feedback.getFeedbackId());
				logger.debug("Feedback created successfully"
						+ feedback.getFeedbackId());
			}
			logger.info("End of insert feedback");
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

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editFeedback(
			@RequestBody FeedbackT feedback) throws DestinationException {
		logger.info("Start of edit feedback");
		logger.debug("Feedback Edit Request Received /feedback PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (feedbackService.editFeedback(feedback)) {
				status.setStatus(Status.SUCCESS, feedback.getFeedbackId());
				logger.debug("Feedback updated successfully"
						+ feedback.getFeedbackId());
			}
			logger.info("End of edit feedback");
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
		logger.info("Start of retrieving the filtered feedback");
		logger.debug("Inside getFilteredFeedbacks service");
		try {
			List<FeedbackT> feedbackList = feedbackService.findFeedbacksWith(
					titleWith, descriptionWith, issueType, priority, status,
					userId, module, updatedUserId, subModule);
			logger.info("End of retrieving the filtered feedback");
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
