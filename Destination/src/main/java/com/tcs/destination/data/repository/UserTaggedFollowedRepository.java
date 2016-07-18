package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserTaggedFollowedT;

@Repository
public interface UserTaggedFollowedRepository extends CrudRepository<UserTaggedFollowedT, String> {

	List<UserTaggedFollowedT> findByConnectId(String connectId);

	List<UserTaggedFollowedT> findByTaskId(String taskId);
	
	@Query(value = "select distinct(user_id) from user_tagged_followed_t where opportunity_id = (:opportunityId)", nativeQuery = true)
	List<String> getOpportunityFollowers(@Param("opportunityId") String opportunityId);
	
	@Query(value = "select distinct(user_id) from user_tagged_followed_t where connect_id = (:connectId)", nativeQuery = true)
	List<String> getConnectFollowers(@Param("connectId") String connectId);


}
