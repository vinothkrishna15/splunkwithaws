package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.ConnectT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface ConnectOpportunityLinkTRepository extends CrudRepository<ConnectOpportunityLinkIdT, String> {

}
