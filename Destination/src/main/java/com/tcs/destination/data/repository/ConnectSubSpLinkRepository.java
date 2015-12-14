package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ConnectSubSpLinkT;


public interface ConnectSubSpLinkRepository extends CrudRepository<ConnectSubSpLinkT, String> {
	
	@Query(value = "select distinct sub_sp from connect_sub_sp_link_t where connect_id = ?1", nativeQuery = true)
	List<String> findSubSpByConnectId(String connectId);

}
