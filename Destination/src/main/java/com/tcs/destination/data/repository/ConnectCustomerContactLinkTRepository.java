package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;

@Repository
public interface ConnectCustomerContactLinkTRepository extends
	CrudRepository<ConnectCustomerContactLinkT, String> {
	
	@Query(value ="select * from connect_customer_contact_link_t where contact_id=?1",nativeQuery = true)
	List<ConnectCustomerContactLinkT> findByContactId(String contactId);

}
