package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.FeedbackT;

/**
 * 
 * Repository for working with {@link FeedbackT} domain objects
 */
@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackT, String> {

	@Query(value = "select * from feedback_t where (issue_type=(:issueType) or (:issueType)='') "
			+ "and (priority=(:priority) or (:priority)='') and (status=(:status) or (:status)='') "
			+ "and (user_id=(:userId) or (:userId)='') "
			+ "and (module=(:module) or (:module)='') "
			+ "and (updated_user_id=(:updatedUserId) or (:updatedUserId)='')"
			+ "and (sub_module=(:subModule) or (:subModule)='')"
			+ "and (title like (:titleWith) or (:titleWith) = '')"
			+ "and (description like (:descriptionWith) or (:descriptionWith) = '') order by created_datetime desc", nativeQuery = true)
	List<FeedbackT> findByOptionalIssueTypeAndPriorityAndStatusAndUserIdAndEntityTypeAndUpdatedUserId(
			@Param("titleWith") String titleWith,
			@Param("descriptionWith") String descriptionWith,
			@Param("issueType") String issueType,
			@Param("priority") String priority, @Param("status") String status,
			@Param("userId") String userId, @Param("module") String module,
			@Param("updatedUserId") String updatedUserId,
			@Param("subModule") String subModule);
}
