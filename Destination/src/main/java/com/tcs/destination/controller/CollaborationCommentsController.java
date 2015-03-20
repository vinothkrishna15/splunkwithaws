package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
	public @ResponseBody String insertComments(
			@RequestBody CollaborationCommentT comments) {
		Status status = new Status();
		status.setStatus(Status.FAILED);
		if (commentsService.insertComments(comments)) {
			status.setStatus(Status.SUCCESS);
		}
		return Constants.filterJsonForFieldAndViews("all", "", status);
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String showComments() {
		return Constants.filterJsonForFieldAndViews("all", "",
				commentsRepository.findOne("CMT01"));
	}

}
