package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerMasterT;

@Repository
public interface PartnerRepository extends
		CrudRepository<PartnerMasterT, String> {

	List<PartnerMasterT> findByPartnerName(String partnername);

	List<PartnerMasterT> findByPartnerNameIgnoreCaseContainingOrderByPartnerNameAsc(String partnername);

	List<PartnerMasterT> findByPartnerNameIgnoreCaseStartingWithOrderByPartnerNameAsc(String startsWith);

	@Query(value = "select * from partner_master_t p ORDER BY p.created_modified_datetime desc LIMIT ?1", nativeQuery = true)
	List<PartnerMasterT> findRecent(int count);
	
	@Query(value ="update partner_master_t set logo = ?1  where partner_id=?2",
			 nativeQuery = true)
	void addImage(byte[] imageBytes, String id);

}
