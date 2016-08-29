package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerSubspProductMappingT;


@Repository
public interface PartnerSubSpProductMappingTRepository extends
		CrudRepository<PartnerSubspProductMappingT, String> {
	
	@Query(value="select * from partner_subsp_product_mapping_t where partner_subsp_product_mapping_id =?1",nativeQuery=true)
	PartnerSubspProductMappingT findByPartnerSubspProductMappingId(String partnerSubspProductMappingId);
	
	List<PartnerSubspProductMappingT> findByPartnerSubspMappingId(String partnerSubspMappingId);
}
