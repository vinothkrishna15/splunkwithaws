package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.FeedbackT;
import com.tcs.destination.data.repository.FeedbackRepository;
import com.tcs.destination.enums.FeedbackIssueType;
import com.tcs.destination.enums.FeedbackPriority;
import com.tcs.destination.enums.FeedbackStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * This service validates the feedback related requests,
 * to insert or to edit a feedback 
 * or to find a feedback by Id or String
 */
@Service
public class FeedbackService {

	private static final Logger logger = LoggerFactory
			.getLogger(FeedbackService.class);

	@Autowired
	FeedbackRepository feedbackRepository;

	public FeedbackT findFeedbackById(String feedbackId) throws Exception {
		logger.info("starting searchforfeedbacksById service");
		FeedbackT feedback = feedbackRepository.findOne(feedbackId);
		if (feedback != null) {
			logger.info("Ending searchforfeedbacksById service");
			return feedback;
		} else {
			logger.error("NOT_FOUND: feedback record not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Feedback not found");
		}
	}

	@Transactional
	public boolean insertFeedback(FeedbackT feedback) throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		feedback.setUserId(userId);
		logger.info("starting insertFeedback Service");
		validateRequest(feedback, true);
		if (feedbackRepository.save(feedback) != null) {
			logger.info("Ending insertFeedback Service");
			return true;
		}
		return false;
	}

	@Transactional
	public boolean editFeedback(FeedbackT feedback) throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		feedback.setUserId(userId);
		logger.info("starting editFeedback Service");
		validateRequest(feedback, false);
		if (feedbackRepository.save(feedback) != null) {
			logger.info("Ending editFeedback Service");
			return true;
		}
		return false;
	}

	private void validateRequest(FeedbackT feedback, boolean isInsert)
			throws Exception {
		logger.info("starting validateRequest for Feedback Service");
		// Validate Issue Type
		if (!FeedbackIssueType.contains(feedback.getIssueType())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Issue Type");
		}

		// Validate Priority
		if (!FeedbackPriority.contains(feedback.getPriority())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Feedback Priority");
		}

		// Validate Status
		if (!FeedbackStatus.contains(feedback.getStatus())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Feedback Status");
		}

		if (isInsert) {
			if (feedback.getUserId() == null || feedback.getUserId().isEmpty()) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"UserId is required");
			}

			if (feedback.getResolutionComments() != null
					&& !(feedback.getResolutionComments().isEmpty())) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Resolution comments should not be passed while creating Feedback");
			}
		} else {
			if (feedback.getFeedbackId() == null
					|| feedback.getFeedbackId().isEmpty()) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"FeedbackId is required");
			}

			FeedbackT dbFeedback = feedbackRepository.findOne(feedback
					.getFeedbackId());
			if (dbFeedback == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Feedback not found");
			} else {
				// CreatedDateTime && CreatedUser should not be updated on
				// update
				feedback.setCreatedDatetime(dbFeedback.getCreatedDatetime());
				feedback.setUserId(dbFeedback.getUserId());

				// Validate UpdatedUserId
				if (feedback.getUpdatedUserId() == null
						|| feedback.getUpdatedUserId().isEmpty()) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Feedback Updated UserId is required");
				}

			}
		}
		logger.info("Ending validateRequest for Feedback Service");
	}

	public List<FeedbackT> findFeedbacksWith(String titleWith,
			String descriptionWith, String issueType, String priority,
			String status, String userId, String module, String updatedUserId,
			String subModule) throws Exception {
		logger.info("Starting findFeedbacksWith for Feedback Service");

		titleWith = (titleWith.length() == 0) ? titleWith.trim() : "%"
				+ titleWith + "%";

		descriptionWith = (descriptionWith.length() == 0) ? descriptionWith
				.trim() : "%" + descriptionWith + "%";
		List<FeedbackT> feedbackTs = feedbackRepository
				.findByOptionalIssueTypeAndPriorityAndStatusAndUserIdAndEntityTypeAndUpdatedUserId(
						titleWith, descriptionWith, issueType, priority,
						status, userId, module, updatedUserId, subModule);
		if (feedbackTs == null || feedbackTs.isEmpty()) {
			String message = "No feedback available for the given criteria";
			logger.error(message);
			throw new DestinationException(HttpStatus.NOT_FOUND, message);
		}
		logger.info("Ending findFeedbacksWith for Feedback Service");
		return feedbackTs;
	}
}