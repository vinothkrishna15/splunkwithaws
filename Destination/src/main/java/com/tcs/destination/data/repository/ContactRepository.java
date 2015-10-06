package com.tcs.destination.data.repository;

import static com.tcs.destination.utils.QueryConstants.CONNECT_REMINDER;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ContactT;

/**
 * @author
 *
 */
@Repository
public interface ContactRepository extends CrudRepository<ContactT, String> {

	@Query(value = "select distinct(CON.*) from contact_t CON,contact_customer_link_t CCLT where UPPER(CON.contact_name) like UPPER(?1) and ((CON.contact_id=CCLT.contact_id and CCLT.customer_id = ?2) or ?2='') and (CON.partner_id = ?3 or ?3='') and (CON.contact_type=?4 or ?4 ='') order by contact_name asc", nativeQuery = true)
	List<ContactT> findByContactName(String contactName, String customerId, String partnerId, String contactType);

	List<ContactT> findByContactNameIgnoreCaseStartingWithOrderByContactNameAsc(String startsWith);
	
	@Query(value = "select CON.* from contact_t CON, contact_customer_link_t CCLT where ((CON.contact_id=CCLT.contact_id and CCLT.customer_id = ?1) or ?1='') and (CON.partner_id = ?2 or ?2='') and (CON.contact_type=?3 or ?3 ='') order by contact_type asc", nativeQuery = true)
	List<ContactT> findByContactType(String customerId, String partnerId, String contactType);
	
	@Query(value ="update contact_t set contact_photo = ?1  where contact_id=?2",
			 nativeQuery = true)
	void addImage(byte[] imageBytes, String id);
	
	@Query(value ="select c.contactId from ContactT c")
	List<String> findContactIdFromContactT();
	
	@Query(value = CONNECT_REMINDER, nativeQuery = true)
	List<Object[]> getConnectReminders();
}
