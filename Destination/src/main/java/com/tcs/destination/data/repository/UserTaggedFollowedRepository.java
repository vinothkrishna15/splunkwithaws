package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserTaggedFollowedT;

@Repository
public interface UserTaggedFollowedRepository extends CrudRepository<UserTaggedFollowedT, String> {

	List<UserTaggedFollowedT> findByConnectId(String connectId);

	List<UserTaggedFollowedT> findByTaskId(String taskId);

}
