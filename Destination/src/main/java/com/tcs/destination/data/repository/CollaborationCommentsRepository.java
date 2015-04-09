package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.CollaborationCommentT;

public interface CollaborationCommentsRepository extends
		CrudRepository<CollaborationCommentT, String> {
	
	@Query(value = "select * from collaboration_comment_t where user_id!=?1 order by updated_datetime Desc limit 100",nativeQuery = true)
	List<CollaborationCommentT> getComments(String userId);
			
	@Query(value = "SELECT * FROM (SELECT DISTINCT ON (entity_id) entity_id, * FROM collaboration_comment_t where user_id!=?1 ORDER BY entity_id DESC ) as t1 ORDER BY updated_datetime DESC",nativeQuery=true)
	List<CollaborationCommentT> getDistinctComments(String userId);
	
}
