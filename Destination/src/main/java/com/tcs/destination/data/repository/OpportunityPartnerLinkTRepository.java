package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityPartnerLinkT;

/**
 * @author bnpp
 *
 */
@Repository
public interface OpportunityPartnerLinkTRepository extends
		CrudRepository<OpportunityPartnerLinkT, String> {



}
