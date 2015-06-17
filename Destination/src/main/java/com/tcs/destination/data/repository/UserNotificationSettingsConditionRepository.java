package com.tcs.destination.data.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserNotificationSettingsConditionsT;

@Repository
public interface UserNotificationSettingsConditionRepository extends CrudRepository<UserNotificationSettingsConditionsT, String>{

}
