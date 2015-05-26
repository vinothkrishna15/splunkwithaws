package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ContactT;

/**
 * @author
 *
 */
public interface ContactRepository extends CrudRepository<ContactT, String> {

	@Query(value = "select * from contact_t where UPPER(contact_name) like UPPER(?1) and (customer_id = ?2 or ?2='') and (partner_id = ?3 or ?3='') order by contact_name", nativeQuery = true)
	List<ContactT> findByContactName(String contactName, String customerId, String partnerId);

	List<ContactT> findByContactNameIgnoreCaseStartingWithOrderByContactNameAsc(String startsWith);
}
