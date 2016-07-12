package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerMasterT;

@Repository
public interface PartnerRepository extends
CrudRepository<PartnerMasterT, String> {

	/**
	 * Finds the partner details for the given partner name.
	 * 
	 * @param partnername
	 *            is the partner name.
	 * @return partner details.
	 */
	List<PartnerMasterT> findByPartnerName(String partnername);
	

	@Query(value = "select partner_id from partner_master_t p where p.partner_name=?1", nativeQuery = true)
	String findPartnerIdByName(String partnername);
	
	
	Page<PartnerMasterT> findByPartnerNameIgnoreCaseContainingAndActiveOrderByPartnerNameAsc(
			String partnername, Pageable page, boolean active);

	Page<PartnerMasterT> findByPartnerNameIgnoreCaseStartingWithAndActiveOrderByGroupPartnerNameAsc(
			String startsWith, Pageable pageable,boolean active);

	@Query(value = "select * from partner_master_t p where p.active=TRUE ORDER BY p.created_modified_datetime desc LIMIT ?1", nativeQuery = true)
	List<PartnerMasterT> findRecent(int count);
	@Query(value = "select * from partner_master_t where partner_id=? and active=TRUE", nativeQuery = true)
	PartnerMasterT findactivepartner(String partner_id);//TODO inactive indicator - remove this

	@Query(value = "update partner_master_t set logo = ?1  where partner_id=?2", nativeQuery = true)
	void addImage(byte[] imageBytes, String id);

	@Query(value = "select partner_name, partner_id from partner_master_t", nativeQuery = true)
	List<Object[]> getPartnerNameAndId();

	@Query(value = "select partner_name, geography from partner_master_t", nativeQuery = true)
	List<Object[]> getPartnerNameAndGeography();

	@Query(value = "select * from partner_master_t where "
			+ "(upper(partner_name) like ?1) "
			+ "and (geography in (?2) or ('') in (?2))"
			+ "and (active='true' or active=(?3))", nativeQuery = true)
	List<PartnerMasterT> findByPartnerNameAndGeographyNonMandatory(String name,
			List<String> geography, boolean active);

	@Query(value = "select partner_id from partner_master_t where partner_name=?1 and geography=?2", nativeQuery = true)
	String findByPartnerNameAndGeography(String name, String geography);

	@Query(value = "select p.partnerId from PartnerMasterT p")
	List<String> findPartnerIdFromPartnerMasterT();

	/**
	 * Finds the partner details for the given partner id.
	 * 
	 * @param partnerid
	 *            is the partner id.
	 * @return partner details.
	 */
	PartnerMasterT findByPartnerId(String partnerid);

	@Query(value = "select * from partner_master_t where partner_name in (:names)", nativeQuery = true)
	List<PartnerMasterT> findByPartnerNames(@Param("names") String[] Name);

	@Query(value="select partner_id,partner_name from partner_master_t",nativeQuery=true)
	List<Object[]> findPartnerIdName();

	@Query(value ="select partner_name from partner_master_t where partner_id in ("
			+ "select partner_id from opportunity_partner_link_t  where opportunity_id=?1)",nativeQuery = true)
	List<String> findPartnerNameByOpportunityId(String opportunityId);


	@Query(value ="select partner_name from partner_master_t where partner_name = (:partnerName)",nativeQuery=true)
	String findPartnerName(@Param("partnerName") String partnerName);

	@Query(value = "select geography from partner_master_t where partner_id = ?1", nativeQuery = true)			
	String findGeographyByPartnerId(String partnerId);

	PartnerMasterT findByActiveTrueAndPartnerId(String partnerId);	

	// partner smart search

	@Query(value = "SELECT * FROM partner_master_t WHERE UPPER(geography) LIKE UPPER(:term) ORDER BY partner_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<PartnerMasterT> searchByGeography(@Param("term") String term, @Param("getAll") boolean getAll);

	@Query(value = "SELECT * FROM partner_master_t WHERE UPPER(partner_name) LIKE UPPER(:term) ORDER BY partner_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<PartnerMasterT> searchByPartnerName(@Param("term") String term, @Param("getAll") boolean getAll);

	@Query(value = "SELECT * FROM partner_master_t WHERE UPPER(group_partner_name) LIKE UPPER(:term) ORDER BY partner_name LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<PartnerMasterT> searchByGroupPartnerName(@Param("term") String term, @Param("getAll") boolean getAll);	
}
