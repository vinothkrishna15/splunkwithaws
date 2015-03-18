package com.tcs.destination.data.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface ConnectSecondaryOwnerRepository extends
		CrudRepository<ConnectSecondaryOwnerLinkT, String> {

	@Query(value = "select c from ConnectT c,ConnectSecondaryOwnerLinkT cs where cs.secondaryOwner=(:secondaryOwner) and c.connectId=cs.connectId and c.startDatetimeOfConnect between (:startTime) and (:endTime)")
	List<ConnectT> findConnectTWithDateWithRangeForSecondaryOwner(
			@Param("secondaryOwner") String secondaryOwner,
			@Param("startTime") Timestamp startTime,
			@Param("endTime") Timestamp endTime);
}
