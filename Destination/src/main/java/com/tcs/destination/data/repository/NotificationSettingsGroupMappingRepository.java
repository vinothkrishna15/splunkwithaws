package com.tcs.destination.data.repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotificationSettingsGroupMappingT;

@Repository
public interface NotificationSettingsGroupMappingRepository extends CrudRepository<NotificationSettingsGroupMappingT, String>{
}
