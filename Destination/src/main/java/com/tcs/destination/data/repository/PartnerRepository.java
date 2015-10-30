package com.tcs.destination.data.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.tcs.destination.bean.PartnerMasterT;

@Repository
public interface PartnerRepository extends
		CrudRepository<PartnerMasterT, String> 
{

	
	/**
	 * Finds the partner details for the given partner name.
	 * 
	 * @param partnername
	 *            is the partner name.
	 * @return partner details.
	 */
	List<PartnerMasterT> findByPartnerName(String partnername);
	
	
    Page<PartnerMasterT> findByPartnerNameIgnoreCaseContainingOrderByPartnerNameAsc(
			String partnername,Pageable page);

	Page<PartnerMasterT> findByPartnerNameIgnoreCaseStartingWithOrderByPartnerNameAsc(
			String startsWith,Pageable pageable);

	@Query(value = "select * from partner_master_t p ORDER BY p.created_modified_datetime desc LIMIT ?1", nativeQuery = true)
	List<PartnerMasterT> findRecent(int count);

	@Query(value = "update partner_master_t set logo = ?1  where partner_id=?2", nativeQuery = true)
	void addImage(byte[] imageBytes, String id);

	@Query(value = "select partner_name, partner_id from partner_master_t", nativeQuery = true)
	List<Object[]> getPartnerNameAndId();

	@Query(value = "select partner_name, geography from partner_master_t", nativeQuery = true)
	List<Object[]> getPartnerNameAndGeography();

	@Query(value = "select * from partner_master_t where (partner_name=?1 or ?1='') and (geography=?2 or ?2='')", nativeQuery = true)
	List<PartnerMasterT> findByPartnerNameAndGeographyNonMandatory(String name,
			String geography);
	
	@Query(value = "select partner_id from partner_master_t where partner_name=?1 and geography=?2", nativeQuery = true)
	String findByPartnerNameAndGeography(String name,
			String geography);
	
	@Query(value ="select p.partnerId from PartnerMasterT p")
	List<String> findPartnerIdFromPartnerMasterT();
	
	
	/**
	 * Finds the partner details for the given partner id.
	 * 
	 * @param partnerid
	 *            is the partner id.
	 * @return partner details.
	 */
	PartnerMasterT findByPartnerId(String partnerid);

}
