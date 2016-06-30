package com.tcs.destination.data.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.BidDetailsT;

@Repository
public interface BidDetailsTRepository extends
		CrudRepository<BidDetailsT, String> {

	@Query(value = " select distinct (BID.*) from bid_details_t BID "
			+ "	 left outer JOIN bid_office_group_owner_link_t BIDGO ON BIDGO.bid_id=BID.bid_id"
			+ "  JOIN opportunity_t OPP ON BID.opportunity_id=OPP.opportunity_id"
			+ "  JOIN customer_master_t CMT ON  CMT.customer_id=OPP.customer_id"
			+ "  JOIN iou_customer_mapping_t ICMT on  CMT.iou=ICMT.iou"
			+ "  JOIN geography_mapping_t GMT on CMT.geography=GMT.geography"
			+ "  JOIN geography_country_mapping_t GCMT ON GMT.geography=GCMT.geography"
			+ "  left outer JOIN opportunity_sub_sp_link_t OPSUBL ON OPP.opportunity_id=OPSUBL.opportunity_id"
			+ "  left outer JOIN sub_sp_mapping_t SSM ON OPSUBL.sub_sp=SSM.sub_sp where"
			+ " (BIDGO.bid_office_group_owner IN (:bidOwner) OR ('') IN (:bidOwner)) AND"
			+ " (BID.bid_request_receive_date between (:startDate) AND (:endDate)) AND"
			+ " (GMT.geography IN (:geography) OR ('') IN (:geography)) AND"
			+ " (GCMT.country IN (:country) OR ('') IN (:country)) AND"
			+ " (SSM.display_sub_sp IN (:serviceLine) OR ('') IN (:serviceLine)) AND"
			+ " (ICMT.display_iou IN (:iou) OR ('') IN (:iou))", nativeQuery = true)
	List<BidDetailsT> findByBidDetailsReport(
			@Param("startDate") Date startDate, @Param("endDate") Date endDate,
			@Param("bidOwner") List<String> bidOwner,
			@Param("iou") List<String> iou,
			@Param("geography") List<String> geography,
			@Param("country") List<String> country,
			@Param("serviceLine") List<String> serviceLine);

	@Query(value = "select distinct (BID.*) from bid_details_t BID where BID.bid_id in (:bidId) order by BID.bid_id", nativeQuery = true)
	List<BidDetailsT> findByBidId(
			@Param("bidId")List<String> bidId);

	@Query(value = "select bid_request_type from bid_request_type_mapping_t", nativeQuery = true)
	List<String> getBidRequestType();

	List<BidDetailsT> findByOpportunityId(String opportunityId);
	
	@Query(value = "select (BID.*) from bid_details_t BID where opportunity_id=(:opportunityId) order by modified_datetime desc limit 1", nativeQuery = true)
	BidDetailsT findLatestBidByOpportunityId(@Param("opportunityId") String opportunityId);
	
}