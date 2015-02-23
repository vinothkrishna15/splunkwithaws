package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerMasterT;

@Repository
public interface PartnerRepository extends
		CrudRepository<PartnerMasterT, String> {

	List<PartnerMasterT> findByPartnerName(String partnername);

	List<PartnerMasterT> findByPartnerNameIgnoreCaseLike(String partnername);

}
