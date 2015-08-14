package com.tcs.destination.data.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserAccessPrivilegesT;

/**
 * 
 * Repository for working with {@link UserAccessPrivilegesT} domain objects
 */
@Repository
public interface UserAccessPrivilegesRepository extends JpaRepository<UserAccessPrivilegesT, String> {

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdIsNull(String userId);
	
	List<UserAccessPrivilegesT> findByUserId(String userId);
}
