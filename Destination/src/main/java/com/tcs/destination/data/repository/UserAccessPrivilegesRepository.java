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
public interface UserAccessPrivilegesRepository extends
		JpaRepository<UserAccessPrivilegesT, String> {

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdIsNullAndIsactive(
			String userId, String isActive);

	List<UserAccessPrivilegesT> findByUserIdAndIsactive(String userId,
			String isActive);

	List<UserAccessPrivilegesT> findByUserIdAndParentPrivilegeIdAndIsactive(
			String userId, Integer parentPrivilegeId, String isActive);

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
		+ "where uat.privilege_value = ?1 and uat.isactive = ?2 and ut.user_group = ?3", nativeQuery = true)
 	List<String> findUserIdsForWorkflowUserGroup(String geography,
			String isactive, String userGroup);
 	
 	@Query(value ="select privilege_value from user_access_privileges_t where user_id = ?1 and privilege_type = ?2", nativeQuery = true)
    List<String> getPrivilegeValueForUser(String userId,String privilegeType);
 	
 	@Query(value = "select icmt.iou from iou_customer_mapping_t icmt join user_access_privileges_t uat on"
 			+ " uat.privilege_value = icmt.display_iou where uat.user_id = ?1"
 			+ " and uat.privilege_type = ?2", nativeQuery = true)
 	List<String> getIouPrivilegeValue(String userId,String privilegeType);
 	
	@Query(value = "select user_id from user_access_privileges_t where privilege_value in ?1", nativeQuery = true)
	List<String> findByGeography(List<String> geographies);
	
	List<UserAccessPrivilegesT> getPrivilegeTypeAndValueByUserId(String userId);
}
