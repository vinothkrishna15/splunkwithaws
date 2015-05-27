package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;

@Repository
public interface ConnectCustomerContactLinkTRepository extends
	CrudRepository<ConnectCustomerContactLinkT, String> {

}
