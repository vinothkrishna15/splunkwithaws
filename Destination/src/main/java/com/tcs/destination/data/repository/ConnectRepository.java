package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import com.tcs.destination.bean.ConnectT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface ConnectRepository extends CrudRepository<ConnectT, String> {

	/**
	 * Finds the connection details for the given connection name.
	 * 
	 * @param name
	 *            is the connection name.
	 * @return connection details.
	 */
	List<ConnectT> findByConnectNameIgnoreCaseLike(String name);

	/**
	 * Finds the connection details for the given connection id.
	 * 
	 * @param connectid
	 *            is the connection id.
	 * @return connection details.
	 */
	ConnectT findByConnectId(String connectid);

	@Query(value = "select c from ConnectT c where (primaryOwner=(:primaryOwner) OR (:primaryOwner)='')and startDatetimeOfConnect between (:fromDate) and (:toDate) and (customer_id=(:customerId) OR (:customerId)='') and (partner_id=(:partnerId) OR (:partnerId)='')")
	List<ConnectT> findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
			@Param("primaryOwner") String primaryOwner,
			@Param("fromDate") Timestamp fromDate,
			@Param("toDate") Timestamp toDate,
			@Param("customerId") String customerId,
			@Param("partnerId") String partnerId);
}
