package com.tcs.destination.data.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.UserNotificationsT;

@Repository
public interface UserNotificationsRepository extends
		CrudRepository<UserNotificationsT, String> {


	@Query(value="select distinct(UNT.*) from user_notifications_t UNT JOIN user_notification_settings_t UNST on UNT.recipient=UNST.user_id "
			+ "where UNT.recipient=(:userId) and  UNT.updated_datetime between (:from) and (:to) "
			+ "and UNST.mode_id=1 and UNST.isactive='Y' order by UNT.updated_datetime desc", nativeQuery=true)
	List<UserNotificationsT> getOptedPortalNotifications(@Param("userId") String userId,
			@Param("from") Timestamp from, @Param("to") Timestamp to);
	
	@Query(value="select distinct(UNT.*) from user_notifications_t UNT JOIN user_notification_settings_t UNST on UNT.recipient=UNST.user_id "
			+ "where UNT.recipient=(:userId) and  UNT.updated_datetime between (:from) and (:to) "
			+ "and UNST.mode_id=1 and UNST.isactive='Y' and UNT.read=(:read) order by UNT.updated_datetime desc", nativeQuery=true)
	List<UserNotificationsT> getOptedPortalNotificationsWithRead(@Param("userId") String userId,
			@Param("from") Timestamp from, @Param("to") Timestamp to,@Param("read") String read);
	
	/**
	 * This query updates the read status in user_notifications_t
	 * @param userNotificationIds
	 * @param status
	 */
	@Transactional
	@Modifying
	@Query(value="update user_notifications_t set read=(:status) where user_notification_id IN (:userNotificationIds)", nativeQuery=true)
	int updateReadStatus(@Param("userNotificationIds") List<String> userNotificationIds, @Param("status") String status);
}
