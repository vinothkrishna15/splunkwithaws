package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserAccessPrivilegesT;


/**
 * 
 * Repository for working with {@link UserAccessPrivilegesT} domain objects
 */
@Repository
public interface UserAccessPrivilegesRepository extends JpaRepository<UserAccessPrivilegesT, String> {

	@Query(value = "select * from user_access_privileges_t where user_id=?1 and parent_privilege_id is null", nativeQuery = true)
	List<UserAccessPrivilegesT> findByUserId(String userId);
}
