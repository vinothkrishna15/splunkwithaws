package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface OpportunityCompetitorLinkTRepository extends CrudRepository<OpportunityCompetitorLinkT, String> {

}
