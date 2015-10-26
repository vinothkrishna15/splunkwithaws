package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserTaggedFollowedT;

@Repository
public interface FollowedRepository extends
		CrudRepository<UserTaggedFollowedT, String> {

	List<UserTaggedFollowedT> findByUserIdAndEntityTypeOrderByCreatedModifiedDatetimeDesc(String userId,
			String entityType);

	@Query(value = "select distinct(user_id) from user_tagged_followed_t  where entity_type = 'OPPORTUNITY' and opportunity_id=(:opportunityId)", nativeQuery = true)
	List<String> getOpportunityTaggedFollowedUsers(
			@Param("opportunityId") String opportunityId);

	@Query(value = "select distinct(user_id) from user_tagged_followed_t  where entity_type = 'CONNECT' and connect_id=(:connectId)", nativeQuery = true)
	List<String> getConnectTaggedFollowedUsers(
			@Param("connectId") String connectId);
	
	@Query(value = "select distinct(user_id) from user_tagged_followed_t  where entity_type = 'TASK' and task_id=(:taskId)", nativeQuery = true)
	List<String> getTasksTaggedFollowedUsers(
			@Param("taskId") String taskId);
}