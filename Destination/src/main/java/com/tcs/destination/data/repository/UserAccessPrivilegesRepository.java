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
	
	@Query(value = "select privilege_id from user_access_privileges_t where user_id=?1 and privilege_type=?2 and privilege_value=?3", nativeQuery = true)
	Integer  getParentPrivilegeId(String userId,String privilegeType,String privilegeValue);

	@Query(value = "select user_id from user_access_privileges_t where user_id in ?1 and privilege_value = ?2", nativeQuery = true)
	List<String> findUserIdsForPrivilegeValue(List<String> geoHeadsAndPMOUsers,
			String geography);
    
	@Query(value = "select distinct(uat.user_id) from user_access_privileges_t uat "
			+ "join user_t ut on ut.user_id = uat.user_id "
			+ "join workflow_customer_t wct on wct.geography = uat.privilege_value "
            + "where uat.privilege_value = ?1 and (ut.user_group = ?2 or ut.user_id like ?3)", nativeQuery = true)
	List<String> findUserIdsForWorkflowUserGroupWithPMO(String geography, String userGroup, String pmoValue);

}
