package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.NotificationEventGroupMappingT;

@Repository
public interface NotificationEventGroupMappingTRepository extends
		CrudRepository<NotificationEventGroupMappingT, Long> {

	List<NotificationEventGroupMappingT> findByEventId(int eventId);

}
