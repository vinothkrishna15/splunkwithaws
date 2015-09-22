package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotificationEventFieldsT;

@Repository
public interface NotificationsEventFieldsTRepository extends CrudRepository<NotificationEventFieldsT, Long> {

	List<NotificationEventFieldsT> findByParentFieldIdAndFieldTypeAndIsactive(
			Integer fieldId, String fieldType, String isActive);

	List<NotificationEventFieldsT> findByEntityTypeAndFieldTypeAndIsactiveAndParentFieldIdIsNull(
			String entityType, String fieldType, String isActive);

	List<NotificationEventFieldsT> findByParentFieldIdAndIsactive(
			Integer fieldId, String isActive);

	List<NotificationEventFieldsT> findByEntityTypeAndIsactiveAndParentFieldIdIsNull(
			String entityType, String isActive);
	
	List<NotificationEventFieldsT> findByNotificationEventIdAndIsactive(int eventId,String isActive);
	
	List<NotificationEventFieldsT> findByNotificationEventIdAndEntityTypeAndIsactive(int eventId, String entityType, String isActive);
	
}
