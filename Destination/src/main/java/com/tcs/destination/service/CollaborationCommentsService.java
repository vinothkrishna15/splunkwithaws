package com.tcs.destination.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.CollaborationCommentT;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;

@Component
public class CollaborationCommentsService {

	@Autowired
	CollaborationCommentsRepository commentsRepository;

	public boolean insertComments(CollaborationCommentT comments) {
		return commentsRepository.save(comments) != null ? true : false;
	}
}
