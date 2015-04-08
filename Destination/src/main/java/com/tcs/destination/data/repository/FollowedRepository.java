package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserTaggedFollowedT;

@Repository
public interface FollowedRepository extends
		CrudRepository<UserTaggedFollowedT, String> {

	UserTaggedFollowedT findByUserIdAndEntityType(String userId,String entityType);
	
}