package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserNotificationSettingsT;

import java.lang.String;

@Repository
public interface UserNotificationSettingsRepository extends CrudRepository<UserNotificationSettingsT, Long> {
	
	List<UserNotificationSettingsT> findByUserIdAndIsactive(String userId, String isActive);

	UserNotificationSettingsT findByUserNotificationSettingsId(
			String userNotificationSettingsId);
	
	List<UserNotificationSettingsT> findByUserIdAndEventId(String userId,Integer eventId);

	
}
