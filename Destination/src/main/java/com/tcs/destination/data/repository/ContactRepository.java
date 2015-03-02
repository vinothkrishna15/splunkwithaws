package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ContactT;

/**
 * @author bnpp
 *
 */
public interface ContactRepository extends CrudRepository<ContactT, String> {
	
//	List<ContactT> findByCustomerId(String customerId);
//	List<ContactT> findByPartnerId(String partnerId);

}
