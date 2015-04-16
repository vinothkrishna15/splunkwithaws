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

//	@Query(value = "SELECT comment_id,entity_type,entity_id,comments,updated_datetime FROM (SELECT DISTINCT on (entity_id) entity_id,comment_id,entity_type,comments,updated_datetime FROM collaboration_comment_t where user_id!=?1 and entity_id!='' ORDER BY entity_id DESC ) as t1 where updated_datetime < to_timestamp(?2,'YYYY-MM-DD HH24:MI:SS.FF') ORDER BY updated_datetime DESC LIMIT ?3", nativeQuery = true)
//	List<Object[]> getDistinctComments(String userId, Timestamp token,int count);
	
	@Query(value =  "SELECT comment_id,entity_type,entity_id,comments,updated_datetime FROM " +
			" (SELECT DISTINCT on (entity_id) entity_id,comment_id,entity_type,comments,updated_datetime FROM collaboration_comment_t where user_id!=?1 and entity_id!='' " +
			 " ORDER BY entity_id DESC) as T1 " +
			"WHERE T1.updated_datetime <= to_timestamp(?2,'YYYY-MM-DD HH24:MI:SS.FF') AND "+
			     " (  (T1.entity_type = 'TASK' and T1.entity_id in "+
			           "(select task.task_id from task_t as task where "+
			               "(task.task_owner=?1 OR task.collaboration_preference='PUBLIC' OR " +
			                    " (task.collaboration_preference = 'RESTRICTED' AND " +
			                       " ?1 in (select tagged.bdms_tagged from task_bdms_tagged_link_t tagged where tagged.task_id=task.task_id)"+
			                     ")"+  
			                ")"+
			            ")"+
			      ") OR T1.entity_type != 'TASK')"+

			"ORDER BY T1.updated_datetime DESC LIMIT ?3",nativeQuery = true)
	List<Object[]> getDistinctComments(String userId, Timestamp token,int count);

}
