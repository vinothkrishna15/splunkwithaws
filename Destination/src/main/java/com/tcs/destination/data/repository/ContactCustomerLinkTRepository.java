package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ProductContactLinkT;

/**
 * @author
 *
 */
public interface ContactCustomerLinkTRepository extends
		CrudRepository<ContactCustomerLinkT, String> {
	
	List<ContactCustomerLinkT> findByContactId(String contactId);
	
	ContactCustomerLinkT findByContactCustomerLinkId(String contactCustomerLinkId);
	
	List<ContactCustomerLinkT> findByCustomerIdAndContactId(String customerId,
			String contactId);

}
