package com.tcs.destination.data.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotificationTypeEventMappingT;

@Repository
public interface NotificationTypeEventMappingRepository extends
		CrudRepository<NotificationTypeEventMappingT, Integer> {

	@Query(value = "select distinct(ntemt.event_id) from notification_type_event_mapping_t ntemt "
			+ "join user_subscriptions us on "
			+ "ntemt.notification_type_event_mapping_id = us.notification_type_event_mapping_id "
			+ "where us.user_id = (:userId) ", nativeQuery = true)
	List<Integer> getNotificationEventIdsForUser(
			@Param("userId") String userId);
}
