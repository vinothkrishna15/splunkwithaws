package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerSubSpMappingT;



@Repository
public interface PartnerSubSpMappingTRepository extends
		CrudRepository<PartnerSubSpMappingT, String> {
	
	PartnerSubSpMappingT findByPartnerSubspMappingId(String partnerSubspMappingId);
	
	List<PartnerSubSpMappingT> findByPartnerId(String partnerId);
	
	List<PartnerSubSpMappingT> findBySubSpId(Integer subSpId);
	
	List<PartnerSubSpMappingT> findByPartnerIdAndSubSpId(String partnerId,Integer subSpId);
}
