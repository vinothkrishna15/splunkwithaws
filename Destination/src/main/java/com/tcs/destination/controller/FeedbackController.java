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
			throws Exception {
		logger.debug("Inside searchforfeedbacksById service");
		FeedbackT feedback = feedbackService.findFeedbackById(feedbackId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				feedback);
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertToFeedback(
			@RequestBody FeedbackT feedback) throws Exception {
		logger.debug("Feedback Insert Request Received /feedback POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (feedbackService.insertFeedback(feedback)) {
				status.setStatus(Status.SUCCESS, feedback.getFeedbackId());
				logger.debug("Feedback created successfully"
						+ feedback.getFeedbackId());
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editFeedback(
			@RequestBody FeedbackT feedback) throws Exception {
		logger.debug("Feedback Edit Request Received /feedback PUT");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (feedbackService.editFeedback(feedback)) {
				status.setStatus(Status.SUCCESS, feedback.getFeedbackId());
				logger.debug("Feedback updated successfully"
						+ feedback.getFeedbackId());
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String getFilteredFeedbacks(
			@RequestParam(value = "titleWith", defaultValue = "") String titleWith,
			@RequestParam(value = "descriptionWith", defaultValue = "") String descriptionWith,
			@RequestParam(value = "issueType", defaultValue = "") String issueType,
			@RequestParam(value = "priority", defaultValue = "") String priority,
			@RequestParam(value = "status", defaultValue = "") String status,
			@RequestParam(value = "userId", defaultValue = "") String userId,
			@RequestParam(value = "module", defaultValue = "") String module,
			@RequestParam(value = "updatedUserId", defaultValue = "") String updatedUserId,
			@RequestParam(value = "subModule", defaultValue = "") String subModule,
			@RequestParam(value = "fields", defaultValue = "") String fields,
			@RequestParam(value = "view", defaultValue = "feedback") String view)
			throws Exception {
		logger.debug("Inside getFilteredFeedbacks service");
		List<FeedbackT> feedbackList = feedbackService.findFeedbacksWith(titleWith,descriptionWith,
				issueType, priority, status, userId, module, updatedUserId,
				subModule);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				feedbackList);
	}

}
