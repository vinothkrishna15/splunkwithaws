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

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdIsNullAndIsactive(String userId, String isActive);
	
	List<UserAccessPrivilegesT> findByUserIdAndIsactive(String userId, String isActive);

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdAndIsactive(String userId, Integer parentPrivilegeId, String isActive);

}
