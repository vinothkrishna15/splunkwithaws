package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.enums.CommentType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.enums.EntityType;

@Service
public class CollaborationCommentsService {
	
	private static final Logger logger = LoggerFactory.getLogger(CollaborationCommentsService.class);

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	public boolean insertComments(CollaborationCommentT comments)
			throws Exception {
		if (isValidComment(comments)) {
			logger.debug("Inside insertComments Service");
			try {
				
				return commentsRepository.save(comments) != null;
			} catch (Exception e) {
				logger.error("INTERNAL_SERVER_ERROR "+e.getMessage());
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		return false;
	}

	private boolean isValidComment(CollaborationCommentT comments)
			throws Exception {
		logger.debug("Inside isValidComment Service");
		if (!CommentType.contains(comments.getCommentType())) {
			logger.error("BAD_REQUEST:Comment Type must be USER or AUTO");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Comment Type must be USER or AUTO");
		}
		if (comments.getComments() == null) {
			logger.error("BAD_REQUEST:Comment must not be empty");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Comment must not be empty");
		}
		if (EntityType.contains(comments.getEntityType())) {
			logger.debug("Entity Type is present");
			switch (EntityType.valueOf(comments.getEntityType())) {
			case CONNECT:
				logger.debug("Connect Found");
				if (comments.getConnectId() != null)
				{
					comments.setEntityId(comments.getConnectId());
					logger.debug("Customer Id Available");
					return true;
				}					
				else
				{
					logger.error("BAD_REQUEST: Connect ID cannot be Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Connect ID cannot be Empty");
				}
			case OPPORTUNITY:
				logger.debug("Opportunity Found");
				if (comments.getOpportunityId() != null)
				{
					comments.setEntityId(comments.getOpportunityId());
					logger.debug("Opportunity Id Available");
					return true;
				}
					
				else
				{
					logger.error("BAD_REQUEST: Opportunity ID cannot be Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Opportunity ID cannot be Empty");
				}
			case TASK:
				logger.debug("Task Found");
				if (comments.getTaskId() != null)
				{
					comments.setEntityId(comments.getTaskId());
					logger.debug("Task Id Available");
					return true;
				}	
				else
				{
					logger.error("BAD_REQUEST: Task ID cannot be Empty");
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Task ID cannot be Empty");
				}	
			default:
			{
				logger.error("BAD_REQUEST: Invalid Entity Type");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Entity Type");
			}
			}
		} else {
			logger.error("BAD_REQUEST:Invalid Entity Type");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Entity Type");
		}
	}
}
