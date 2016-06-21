package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotificationEventGroupMappingT;

@Repository
public interface NotificationEventGroupMappingTRepository extends
		CrudRepository<NotificationEventGroupMappingT, Long> {

	List<NotificationEventGroupMappingT> findByEventId(int eventId);

	@Query(value="select message_template from notification_event_group_mapping_t where event_id = ?1",nativeQuery=true)
	String getMessageTemplateByEventId(Integer eventId);
	
}
