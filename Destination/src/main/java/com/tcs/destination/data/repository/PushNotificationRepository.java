package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PushNotificationRegistrationT;

@Repository
public interface PushNotificationRepository extends CrudRepository<PushNotificationRegistrationT, Long> {

	PushNotificationRegistrationT findByUserId(String userId);
	
}
