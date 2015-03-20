package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.CollaborationCommentT;

public interface CollaborationCommentsRepository extends
		CrudRepository<CollaborationCommentT, String> {

}
