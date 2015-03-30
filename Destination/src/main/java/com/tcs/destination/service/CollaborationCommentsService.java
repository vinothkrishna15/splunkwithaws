package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.enums.CommentType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants.EntityType;

@Component
public class CollaborationCommentsService {

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	public boolean insertComments(CollaborationCommentT comments)
			throws Exception {
		if (isValidComment(comments)) {
			try {
				return commentsRepository.save(comments) != null;
			} catch (Exception e) {
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		return false;
	}

	private boolean isValidComment(CollaborationCommentT comments)
			throws Exception {
		if (!CommentType.contains(comments.getCommentType())) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Comment Type must be USER or AUTO");
		}
		if (comments.getComments() == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Comment must not be empty");
		}
		if (EntityType.contains(comments.getEntityType())) {
			switch (EntityType.valueOf(comments.getEntityType())) {
			case CONNECT:
				if (comments.getConnectId() != null)
					return true;
				else
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Connect ID cannot be Empty");
			case OPPORTUNITY:
				if (comments.getOpportunityId() != null)
					return true;
				else
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Opportunity ID cannot be Empty");
			case TASK:
				if (comments.getTaskId() != null)
					return true;
				else
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Task ID cannot be Empty");
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Entity Type");
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Entity Type");
		}
	}
}
