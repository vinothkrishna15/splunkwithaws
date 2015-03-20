package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
			@RequestBody CollaborationCommentT comments) {
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (commentsService.insertComments(comments)) {
				status.setStatus(Status.SUCCESS);
			}
		} catch (Exception e) {
			status.setStatus(Status.FAILED,e.getMessage());
			return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(Constants.filterJsonForFieldAndViews("all", "", status),HttpStatus.OK);;
	}

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String showComments() {
		return Constants.filterJsonForFieldAndViews("all", "",
				commentsRepository.findOne("CMT01"));
	}

}
