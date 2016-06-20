package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ConnectSubSpLinkT;


public interface ConnectSubSpLinkRepository extends CrudRepository<ConnectSubSpLinkT, String> {
	
	/**
	 * This method is used to find displaySubSps for the given connectId
	 * 
	 * @param connectId
	 * @return
	 */
	@Query(value = "select distinct display_sub_sp from sub_sp_mapping_t SMT join connect_sub_sp_link_t CSLT on SMT.sub_sp=CSLT.sub_sp "
			+ " where connect_id =?1", nativeQuery = true)
	List<String> findDisplaySubSpByConnectId(String connectId);

	/**
	 * This method is used to find subSps for the given connectId
	 * 
	 * @param connectId
	 * @return
	 */
	@Query(value = "select distinct sub_sp from connect_sub_sp_link_t where connect_id = ?1", nativeQuery = true)
	List<String> findSubSpByConnectId(String connectId);

}
