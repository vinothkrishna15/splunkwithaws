package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;

@Repository
public interface ConnectTcsAccountContactLinkTRepository extends
	CrudRepository<ConnectTcsAccountContactLinkT, String>{

}
