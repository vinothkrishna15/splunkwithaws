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

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdIsNullAndIsactive(String userId, String isActive);
	
	List<UserAccessPrivilegesT> findByUserIdAndIsactive(String userId, String isActive);

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdAndIsactive(String userId, Integer parentPrivilegeId, String isActive);
	
	@Query(value = "select u.user_id,u.user_name,u.user_group,uap.privilege_type,uap.privilege_value from user_t u join user_access_privileges_t uap on u.user_id=uap.user_id", nativeQuery = true)
	List<Object[]> findPrivilegesWithUserId();
	
	List<UserAccessPrivilegesT> findByParentPrivilegeIdIsNull();

}
