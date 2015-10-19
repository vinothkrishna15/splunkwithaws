package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.UserTaggedFollowedT;

public interface UserTaggedFollowedRepository extends CrudRepository<UserTaggedFollowedT, String> {

	List<UserTaggedFollowedT> findByConnectId(String connectId);

	List<UserTaggedFollowedT> findByTaskId(String taskId);

}
