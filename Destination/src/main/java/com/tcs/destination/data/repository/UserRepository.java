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
	UserT findByUserId(String userId);
	
	UserT findByUserIdAndTempPassword(String userName, String tempPassword);
	
	@Query (value="WITH RECURSIVE U1 AS (SELECT * FROM user_t"
			+ " WHERE supervisor_user_id = ?1 UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id"
			+ " ) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc", nativeQuery=true)
	List<String> getAllSubordinatesIdBySupervisorId(String supervisorId);
	
	@Query (value="select user_id from user_t where user_role=?1",nativeQuery=true)
	List<String> findUserIdByUserRole(String userRole);
	
	List<UserT> findByUserIdAndUserRole(String userId, String userRole);
	
	UserT findByUserEmailId(String emailId);

	UserT findByUserIdAndUserEmailId(String userId, String emailId);
	
	@Query(value ="update user_t set user_photo = ?1  where user_id=?2",
			 nativeQuery = true)
	void addImage(byte[] imageBytes, String id);
	
	@Query(value ="select user_name, user_id from user_t", nativeQuery = true)
	List<Object[]> getNameAndId();

}
