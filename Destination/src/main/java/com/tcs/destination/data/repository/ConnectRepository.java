package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import com.tcs.destination.bean.ConnectT;

/**
 * 
 *  Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface ConnectRepository extends CrudRepository<ConnectT, String> {
	
	/**
	 * Finds the connection details for the given connection name.
	 * @param name is the connection name.
	 * @return connection details.
	 */
	List<ConnectT> findByConnectNameIgnoreCaseLike(String name);
	
	/**
	 * Finds the connection details for the given connection id.
	 * @param connectid is the connection id.
	 * @return connection details.
	 */
	ConnectT findByConnectId(String connectid);
	
	List<ConnectT> findByStartDatetimeOfConnectBetween(Timestamp fromDate,Timestamp toDate);
}

   