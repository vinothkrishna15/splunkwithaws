package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserT;

import java.lang.String;

@Repository
public interface UserRepository extends CrudRepository<UserT, String> {
	List<UserT> findByUserNameIgnoreCaseLike(String nameWith);
	UserT findByUserName(String userName);
	
	UserT findByUserIdAndTempPassword(String userName, String tempPassword);
	
	@Query (value="WITH RECURSIVE U1 AS (SELECT * FROM user_t"
			+ " WHERE supervisor_user_id = ?1 UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id"
			+ " ) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc", nativeQuery=true)
	List<String> getAllSubordinatesIdBySupervisorId(String supervisorId);
}
