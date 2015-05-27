package com.tcs.destination.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.FeedbackT;
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
		FeedbackT feedback = feedbackRepository.findByFeedbackId(feedbackId);
		if (feedback != null) {
			// Add Search Keywords
			return feedback;
		} else {
			logger.error("NOT_FOUND: feedback record not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"feedback not found");
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

	private void validateRequest(FeedbackT feedback, boolean isInsert)
			throws Exception {
		if (isInsert) {
			if (feedback.getUserId() == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Request - Missing UserId");
			}
			if(feedback.getResolutionComments()!=null){
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Request - Cannot set comments while creating feedback");
			}
		} else {
			FeedbackT checkRecord = findFeedbackById(feedback.getFeedbackId());
			boolean isUserIdNull = feedback.getUserId() == null;
			boolean isCreatedDateNull = feedback.getCreatedDatetime() == null;
			if (isUserIdNull || isCreatedDateNull) {
				StringBuffer message = new StringBuffer("");
				if (isUserIdNull) {
					message.append("userId,");
				}
				if (isCreatedDateNull) {
					message.append("createdDatetime");
				}
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Request - Missing Param : " + message);
			}

		}
	}

	public boolean editFeedback(FeedbackT feedback) throws Exception {
		logger.debug("Inside editFeedback Service");
		validateRequest(feedback, false);
		if (feedbackRepository.save(feedback) != null) {
			logger.debug("Feedback Record edited");
			return true;
		}
		return false;
	}

}
