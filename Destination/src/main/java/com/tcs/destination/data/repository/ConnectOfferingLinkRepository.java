package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;


public interface ConnectOfferingLinkRepository extends CrudRepository<ConnectOfferingLinkT, String> {
	
	@Query(value = "select distinct offering from connect_offering_link_t  where connect_id = ?1", nativeQuery = true)
	List<String> findOfferingByConnectId(String connectId);

}
