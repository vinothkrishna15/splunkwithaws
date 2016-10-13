package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
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


	@Query(value ="select * from opportunity_partner_link_t where partner_id=?1",nativeQuery = true)
	List<OpportunityPartnerLinkT> findByPartnerId(String partnerId);
	

	
	@Query(value ="select o.opportunityPartnerLinkId from OpportunityPartnerLinkT o")
	List<String> findOpportunityPartnerLinkIdFromOpportunityPartnerLinkT();



	List<OpportunityPartnerLinkT> findByOpportunityId(String opportunityId);
	
	@Query(value ="select * from opportunity_partner_link_t where opportunity_partner_link_id in ((select distinct OPPL.opportunity_partner_link_id from opportunity_partner_link_t OPPL JOIN opportunity_t OPPT on OPPT.opportunity_id=OPPL.opportunity_id "
			+ " where OPPT.delivery_team_flag = true AND OPPL.partner_id = ?1 ) UNION (select distinct OPPLT.opportunity_partner_link_id from opportunity_partner_link_t OPPLT "
			+ " JOIN opportunity_t OPP on OPP.opportunity_id=OPPLT.opportunity_id Join opportunity_delivery_centre_mapping_t OPPDCM on (OPP.opportunity_id=OPPDCM.opportunity_id) "
			+ " Join delivery_centre_t DC on OPPDCM.delivery_centre_id=DC.delivery_centre_id Join delivery_cluster_t DCL on DC.delivery_cluster_id=DCL.delivery_cluster_id where "
			+ " (delivery_centre_head in (?2) OR (delivery_cluster_head in (?2))) AND OPPLT.partner_id = ?1 )) ORDER BY modified_datetime DESC ", nativeQuery=true)
	List<OpportunityPartnerLinkT> findAllDeliveryOpportunitiesByOwnersAndPartner(String customerId, List<String> userIds);

}
