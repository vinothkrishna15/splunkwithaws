package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.service.CollaborationCommentsService;
import com.tcs.destination.utils.ResponseConstructors;

@RestController
@RequestMapping("/comments")
public class CollaborationCommentsController {

	private static final Logger logger = LoggerFactory.getLogger(CollaborationCommentsController.class);
	
	@Autowired
	CollaborationCommentsService commentsService;

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
		return new ResponseEntity<String>(ResponseConstructors.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);
	}

}
