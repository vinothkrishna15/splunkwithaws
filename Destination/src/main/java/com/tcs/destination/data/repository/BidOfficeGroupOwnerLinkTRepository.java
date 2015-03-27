package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.OpportunityT;

@Repository
public interface BidOfficeGroupOwnerLinkTRepository extends
		CrudRepository<BidOfficeGroupOwnerLinkT, String> {

	@Query("select bdt.opportunityT from BidDetailsT bdt,BidOfficeGroupOwnerLinkT bogot where bdt.bidId=bogot.bidId and bogot.bidOfficeGroupOwner=(:userId)")
	List<OpportunityT> findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(@Param("userId")String userId);

}