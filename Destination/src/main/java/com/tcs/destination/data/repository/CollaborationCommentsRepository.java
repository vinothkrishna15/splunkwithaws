package com.tcs.destination.data.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.CollaborationCommentT;

public interface CollaborationCommentsRepository extends
		CrudRepository<CollaborationCommentT, String> {

	@Query(value = "select * from collaboration_comment_t where user_id!=?1 order by updated_datetime Desc limit 100", nativeQuery = true)
	List<CollaborationCommentT> getComments(String userId);

	@Query(value = "SELECT comment_id,entity_type,entity_id,comments,updated_datetime FROM (SELECT DISTINCT on (entity_id) entity_id,comment_id,entity_type,comments,updated_datetime FROM collaboration_comment_t where user_id!=?1 and entity_id!='' and entity_type=?2 ORDER BY entity_id DESC ) as t1 ORDER BY updated_datetime DESC", nativeQuery = true)
	List<String[]> getDistinctCommentsForEntity(String userId, String entityType);

	@Query(value = "SELECT comment_id,entity_type,entity_id,comments,updated_datetime FROM (SELECT DISTINCT on (entity_id) entity_id,comment_id,entity_type,comments,updated_datetime FROM collaboration_comment_t where user_id!=?1 and entity_id!='' ORDER BY entity_id DESC ) as t1 where updated_datetime < to_timestamp(?2,'YYYY-MM-DD HH24:MI:SS.FF') ORDER BY updated_datetime DESC LIMIT ?3", nativeQuery = true)
	List<Object[]> getDistinctComments(String userId, Timestamp token,int count);

}
