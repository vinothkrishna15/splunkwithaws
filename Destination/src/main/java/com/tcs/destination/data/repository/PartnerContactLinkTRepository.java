package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.PartnerContactLinkT;


@Repository
public interface PartnerContactLinkTRepository extends
		CrudRepository<PartnerContactLinkT, String> {

	/**
	 * @param partnerId
	 * @param contactId
	 * @return
	 */
	List<PartnerContactLinkT> findByPartnerIdAndContactId(String partnerId,
			String contactId);

	List<PartnerContactLinkT> findByPartnerId(String partnerId);
	
}
