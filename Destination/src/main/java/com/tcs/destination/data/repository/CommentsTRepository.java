package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.CommentsT;


public interface CommentsTRepository extends CrudRepository<CommentsT, String> {

	List<CommentsT> findByConnectId(String connectId);

}
