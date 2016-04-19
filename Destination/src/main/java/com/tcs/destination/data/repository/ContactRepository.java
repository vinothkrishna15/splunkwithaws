package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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
	
	List<ContactT> findByContactName(String contactName);
	
	@Query(value ="select * from contact_t where contact_name in (:names)",
			 nativeQuery = true)
	List<ContactT> findByContactNameList(@Param("names") List<String> name);

	@Query(value ="select * from contact_t where contact_name in (:names)",
			 nativeQuery = true)
	List<ContactT> findByContactNames(@Param("names") String[] name);
	
	@Query(value ="select * from contact_t where partner_id=?1 and contact_category='PARTNER'",nativeQuery = true)
	List<ContactT> findByPartnerId(String partnerId);
	
	@Query(value ="select * from contact_t where contact_category='CUSTOMER'",nativeQuery = true)
	List<ContactT> getAllCustomerContacts();
	
	@Query(value ="select * from contact_t where contact_category='PARTNER'",nativeQuery = true)
	List<ContactT> getAllPartnerContacts();
	
	/**
	 * This Method is used to get tcs account contact names for the given opportunityId
	 * @param opportunityId
	 * @return
	 */
	@Query(value = "select contact_name from contact_t CONT "
			+ "join opportunity_tcs_account_contact_link_t OPPTACL on CONT.contact_id=OPPTACL.contact_id where OPPTACL.opportunity_id=?1" , nativeQuery = true)
	List<String> findTcsAccountContactNamesByOpportinityId(String opportunityId);
	
	/**
	 * This Method is used to get customer contact names for the given opportunityId
	 * @param opportunityId
	 * @return
	 */
	@Query(value = "select contact_name from contact_t CONT "
			+ "join opportunity_customer_contact_link_t OCCLT on CONT.contact_id=OCCLT.contact_id where opportunity_id=?1" , nativeQuery = true)
	List<String> findCustomerContactNamesByOpportinityId(String opportunityId);

	/**
	 * This Method is used to get tcs account contact names for the given connectId
	 * @param connectId
	 * @return
	 */
	@Query(value = "select contact_name,contact_role from contact_t CONT "
			+ "join connect_tcs_account_contact_link_t CTACL on CONT.contact_id=CTACL.contact_id where CTACL.connect_id=?1" , nativeQuery = true)
	List<Object[]> findTcsAccountContactNamesByConnectId(String connectId);
	
	/**
	 * This Method is used to get customer contact names for the given connectId
	 * @param opportunityId
	 * @return
	 */
	@Query(value = "select contact_name,contact_role from contact_t CONT "
			+ "join connect_customer_contact_link_t CCACL on CONT.contact_id=CCACL.contact_id where CCACL.connect_id=?1" , nativeQuery = true)
	List<Object[]> findCustomerContactNamesByConnectId(String connectId);
	
	/**
	 * This method to find the duplicate contacts for a customer
	 * @param customerId
	 * @param contactType
	 * @param contactCategory
	 * @param conatctName
	 * @param contactRole
	 * @return
	 */
	@Query(value = "select * from contact_t where contact_id in (select contact_id from contact_customer_link_t where customer_id = ?1) and contact_type = ?2 and contact_category = ?3 and contact_name = ?4 and contact_role = ?5",nativeQuery = true)
	List<ContactT> findDuplicateContacts(String customerId, String contactType, String contactCategory, String conatctName, String contactRole);

}
