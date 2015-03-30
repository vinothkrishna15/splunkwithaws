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

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.service.CollaborationCommentsService;
import com.tcs.destination.utils.Constants;

@RestController
@RequestMapping("/comments")
public class CollaborationCommentsController {

	@Autowired
	CollaborationCommentsService commentsService;

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> insertComments(
			@RequestBody CollaborationCommentT comments) throws Exception {
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
			if (commentsService.insertComments(comments)) {
				status.setStatus(Status.SUCCESS,comments.getCommentId());
			}
		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);
	}

}
