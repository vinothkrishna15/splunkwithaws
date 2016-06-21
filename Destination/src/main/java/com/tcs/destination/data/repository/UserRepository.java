package com.tcs.destination.data.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserT;

@Repository
public interface UserRepository extends CrudRepository<UserT, String> {
	
	List<UserT> findByUserNameIgnoreCaseLike(String nameWith);
	
	@Query(value = "select * from user_t  where user_id like ?1 or upper(user_name) like ?1 order by user_name", nativeQuery = true)
	List<UserT> findByUserNameOrUserId(String userNameOrId);
	
	@Query(value = "select * from user_t  where supervisor_user_id like ?1 or upper(supervisor_user_name) like ?1 order by user_name", nativeQuery = true)
	List<UserT> findBySupervisorNameOrId(String userNameOrId);
	
	List<UserT> findByUserGroup(String userGroup);
	
	List<UserT> findByActiveTrueAndUserNameIgnoreCaseLike(String nameWith);

	UserT findByUserName(String userName);

	UserT findByUserId(String userId);

	UserT findByUserIdAndTempPassword(String userName, String tempPassword);

	@Query(value = "WITH RECURSIVE U1 AS (SELECT * FROM user_t"
			+ " WHERE supervisor_user_id = ?1 UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id"
			+ " ) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc", nativeQuery = true)
	List<String> getAllSubordinatesIdBySupervisorId(String supervisorId);

	@Query(value = "WITH RECURSIVE U1 AS (SELECT * FROM user_t"
			+ " WHERE supervisor_user_id = ?1 UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id"
			+ " ) SELECT U1.* FROM U1 WHERE UPPER(user_name) like UPPER(?2) ORDER BY U1.user_id asc ", nativeQuery = true)
	List<UserT> getSubordinatesIdBySupervisorId(String supervisorId, String userName);
	
	@Query(value = "select user_id from user_t where user_role=?1", nativeQuery = true)
	List<String> findUserIdByUserRole(String userRole);

	List<UserT> findByUserIdAndUserRole(String userId, String userRole);

	UserT findByUserEmailId(String emailId);

	UserT findByUserIdAndUserEmailId(String userId, String emailId);

	@Query(value = "update user_t set user_photo = ?1  where user_id=?2", nativeQuery = true)
	void addImage(byte[] imageBytes, String id);

	@Query(value = "select user_name, user_id from user_t", nativeQuery = true)
	List<Object[]> getNameAndId();

	@Query(value = "select distinct(supervisor_user_id) from user_t where user_id in (:userIds) and supervisor_user_id <> ''", nativeQuery = true)
	List<String> getSupervisorUserId(@Param("userIds") List<String> userIds);

	@Query(value = "select user_id from user_t where user_group=?1", nativeQuery = true)
	List<String> findUserIdByUserGroup(String userGroup);
	
	@Query(value = "select user_name from user_t where user_id in (:userIds)", nativeQuery = true)
	List<String> findUserNamesByUserIds(@Param("userIds")List<String> userIds);
	
	@Query(value = "select user_id from user_t ", nativeQuery = true)
	List<String> findAllUserIds();
	
	@Query(value = "select user_id from user_t where user_group in (:userGroup)", nativeQuery = true)
	List<String> findUserIdByuserGroup(@Param("userGroup") List<String> userGroup);
	
	List<UserT> findByUserRole(String userRole);
	
	@Query(value = "select u.user_id,u.user_name,u.temp_password,u.user_group,u.user_role,u.base_location,ug.time_zone_desc,u.user_telephone,"
			+ "u.user_email_id,u.supervisor_user_id,u.supervisor_user_name,u.active from user_t u join user_general_settings_t ug on u.user_id=ug.user_id", nativeQuery = true)
	List<Object[]> findUserWithTimeZone();
	
	@Query( value = "select * from user_t where user_role in (:userRoles)", nativeQuery = true)
	List<UserT> findByUserRoles(@Param("userRoles") List<String> roles);
	
	@Query(value=" select distinct user_name from user_t U join opportunity_sales_support_link_t OSSL on U.user_id=OSSL.sales_support_owner "
			+ "where opportunity_id=?1", nativeQuery=true)
	List<String> findOpportunitySalesSupportOwnersNameByOpportunityId(String opportunityId);
	
	@Query(value = "select distinct user_name from user_t U join bid_office_group_owner_link_t BOGL on U.user_id=BOGL.bid_office_group_owner "
			+ "where bid_id=?1", nativeQuery=true)
	List<String> findBidOfficeGroupOwnersNameByBidId(String bidId);
	
	@Query(value = "select distinct user_name from user_t U join connect_secondary_owner_link_t CSW on U.user_id=CSW.secondary_owner"
			+ " where CSW.connect_id=?1", nativeQuery = true)
	List<String> getSecondaryOwnerNamesByConnectId(String connectId);
	
	@Query(value = "select user_name from user_t where user_id = :userId", nativeQuery = true)
	String findUserNameByUserId(@Param("userId")String userId);

	@Query(value = "select user_id from user_t where user_id = ?1", nativeQuery = true)
	List<String> findPmoUserIds(String pmoValue);

	List<UserT> findByUserIdLikeOrUserGroup(String pmoValue, String value);
    
	@Query(value = "select user_id from user_t where user_id like ?1 or user_group = ?2", nativeQuery = true)
	List<String> findGeoHeadsAndPMOUserIds(String pmoValue, String value);
    
	List<UserT> findByBaseLocationIgnoreCaseContainingOrderByUserNameAsc(String baseLocation);
	
	@Query(value = "select distinct(user_group) from user_t where user_id in (:userIds) and active = (:active)", nativeQuery = true)
	List<String> findUserGroupByUserIds(@Param("userIds") Set<String> userIds, @Param("active") boolean active);

	@Query(value = "select distinct(user_email_id) from user_t where active='true' and user_email_id in (:userMails)", nativeQuery = true)
	List<String> findActiveUserMailIds(@Param("userMails") List<String> userMails);

	@Query(value = "select distinct(user_email_id) from user_t where user_id in (:userIds)", nativeQuery = true)
	List<String> findUserMailIdsFromUserId(@Param("userIds") List<String> userIds);
	
	UserT findByActiveTrueAndUserName(String userName);
	UserT findByActiveTrueAndUserId(String userId);
	
	List<UserT> findUsersByStatusAndActive(int status,boolean active);

	
	/* ------- user smart search repository methods ------- */
	@Query(value = "SELECT * FROM user_t WHERE UPPER(user_id) LIKE UPPER(:term) ORDER BY user_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<UserT> searchByUserId(@Param("term") String term, @Param("getAll") boolean getAll);
	
	@Query(value = "SELECT * FROM user_t WHERE UPPER(user_name) LIKE UPPER(:term) ORDER BY user_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<UserT> searchByUserName(@Param("term") String term, @Param("getAll") boolean getAll);

	@Query(value = "SELECT * FROM user_t WHERE UPPER(supervisor_user_name) LIKE UPPER(:term) ORDER BY user_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<UserT> searchBySupervisor(@Param("term") String term, @Param("getAll") boolean getAll);

	@Query(value = "SELECT * FROM user_t WHERE UPPER(base_location) LIKE UPPER(:term) ORDER BY user_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<UserT> searchByLocation(@Param("term") String term, @Param("getAll") boolean getAll);
	
	/* ------- END  - user smart search repository methods ------- */
	
	@Query(value = "SELECT * FROM user_t WHERE UPPER(user_name) like UPPER(?1) ORDER BY user_id asc", nativeQuery = true)
	List<UserT> getUsersByUserNameKeyword(String userName);
	
	
	/**
	 * Find the userId and username of subordinates of the user
	 * 
	 * @param userId
	 * @return
	 */
	@Query(value = "WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = ?1 UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id ) SELECT U1.* FROM U1 ORDER BY U1.user_id asc", nativeQuery = true)
	List<UserT> findSubordinatesBySupervisorId(String userId);
	 
	/**
	 * Find the reporting hierarchy of the user
	 * 
	 * @param userId
	 * @return
	 */
	@Query(value = "WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE user_id = ?1 UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U1.supervisor_user_id = U2.user_id ) SELECT U1.* FROM U1", nativeQuery = true)
	List<UserT> findUserHierarchy(String userId);
	
	@Query(value = "select supervisor_user_id from user_t where user_id = (:userId)", nativeQuery = true)
    String getSupervisorUserIdForUser(@Param("userId") String userId);
	
}
