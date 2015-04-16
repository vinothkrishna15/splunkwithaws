package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.OpportunityCustomerContactLinkT;

/**
 * @author bnpp
 *
 */
@Repository
public interface OpportunityCustomerContactLinkTRepository extends
		CrudRepository<OpportunityCustomerContactLinkT, String> {



}
