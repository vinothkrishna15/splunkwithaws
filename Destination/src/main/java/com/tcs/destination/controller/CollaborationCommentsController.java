package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.service.CollaborationCommentsService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/comments")
public class CollaborationCommentsController {

	private static final Logger logger = LoggerFactory.getLogger(CollaborationCommentsController.class);
	
	@Autowired
	CollaborationCommentsService commentsService;

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertComments(
			@RequestBody CollaborationCommentT comments) throws Exception {
		logger.debug("Inside CollaborationCommentsController /comments POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
			if (commentsService.insertComments(comments)) {
				logger.debug("Comments Inserted Successfully");
				status.setStatus(Status.SUCCESS,comments.getCommentId());
			}
		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);
	}

}
