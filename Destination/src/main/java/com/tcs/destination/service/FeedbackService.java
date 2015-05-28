package com.tcs.destination.service;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.FeedbackT;
import com.tcs.destination.enums.FeedbackIssueType;
import com.tcs.destination.enums.FeedbackPriority;
import com.tcs.destination.enums.FeedbackStatus;
import com.tcs.destination.data.repository.FeedbackRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class FeedbackService {

	private static final Logger logger = LoggerFactory
			.getLogger(FeedbackService.class);

	@Autowired
	FeedbackRepository feedbackRepository;

	public FeedbackT findFeedbackById(String feedbackId) throws Exception {
		logger.debug("Inside searchforfeedbacksById service");
		FeedbackT feedback = feedbackRepository.findOne(feedbackId);
		if (feedback != null) {
			return feedback;
		} else {
			logger.error("NOT_FOUND: feedback record not found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Feedback not found");
		}
	}

	@Transactional
	public boolean insertFeedback(FeedbackT feedback) throws Exception {
		logger.debug("Inside insertFeedback Service");
		validateRequest(feedback, true);
		if (feedbackRepository.save(feedback) != null) {
			logger.debug("Feedback Record Inserted");
			return true;
		}
		return false;
	}

	@Transactional
	public boolean editFeedback(FeedbackT feedback) throws Exception {
		logger.debug("Inside editFeedback Service");
		validateRequest(feedback, false);
		if (feedbackRepository.save(feedback) != null) {
			logger.debug("Feedback Record edited");
			return true;
		}
		return false;
	}

	private void validateRequest(FeedbackT feedback, boolean isInsert)
			throws Exception {

		//Validate Issue Type
		if (!FeedbackIssueType.contains(feedback.getIssueType())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Issue Type");
		}

		//Validate Priority
		if (!FeedbackPriority.contains(feedback.getPriority())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Feedback Priority");
		}

		//Validate Status
		if (!FeedbackStatus.contains(feedback.getStatus())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Feedback Status");
		}

		if (isInsert) {
			if (feedback.getUserId() == null || feedback.getUserId().isEmpty()) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"UserId is required");
			}

			if(feedback.getResolutionComments() != null && !(feedback.getResolutionComments().isEmpty())) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Resolution comments should not be passed while creating Feedback");
			}
		} else {
			if (feedback.getFeedbackId() == null || feedback.getFeedbackId().isEmpty()) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"FeedbackId is required");
			}
			
			FeedbackT dbFeedback = feedbackRepository.findOne(feedback.getFeedbackId());
			if (dbFeedback == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Feedback not found");
			} else {
				//CreatedDateTime && CreatedUser should not be updated on update
				feedback.setCreatedDatetime(dbFeedback.getCreatedDatetime());
				feedback.setUserId(dbFeedback.getUserId());

				//Validate UpdatedUserId
				if (feedback.getUpdatedUserId() == null || feedback.getUpdatedUserId().isEmpty()) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Feedback Updated UserId is required");
				}

			}
		}
	}
}