package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

import com.tcs.destination.bean.ConnectT;

/**
 * 
 * Repository for working with {@link ConnectT} domain objects
 */
@Repository
public interface ConnectRepository extends CrudRepository<ConnectT, String> {

	/**
	 * Finds the connection details for the given connection name.
	 * 
	 * @param name
	 *            is the connection name.
	 * @return connection details.
	 */
	List<ConnectT> findByConnectNameIgnoreCaseLike(String name);

	/**
	 * Finds the connection details for the given connection id.
	 * 
	 * @param connectid
	 *            is the connection id.
	 * @return connection details.
	 */
	ConnectT findByConnectId(String connectid);
	
	
	List<ConnectT> findByConnectNameIgnoreCaseLikeAndCustomerId(String name,String customerId);

	@Query(value = "select c from ConnectT c where (primaryOwner=(:primaryOwner) OR (:primaryOwner)='')and startDatetimeOfConnect between (:fromDate) and (:toDate) and (customer_id=(:customerId) OR (:customerId)='') and (partner_id=(:partnerId) OR (:partnerId)='')")
	List<ConnectT> findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
			@Param("primaryOwner") String primaryOwner,
			@Param("fromDate") Timestamp fromDate,
			@Param("toDate") Timestamp toDate,
			@Param("customerId") String customerId,
			@Param("partnerId") String partnerId);
	
	
	@Query(value= "select distinct (CON.*)  "
			   +" from connect_t CON "
				+"   JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"  
				+"   JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
				+"   JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
				+"   JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography"
				+"   JOIN connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id"   
				+"   JOIN sub_sp_mapping_t SSM on CSL.sub_sp=SSM.sub_sp" 
				+" where "
				+" CON.start_datetime_of_connect between (:startDate) and (:endDate) and "
				+" (ICMT.display_iou in (:iou) OR ('') in (:iou)) and"
				+" (GMT.display_geography in (:geography) OR ('') in (:geography)) and"
				+" (GCM.country in (:country) OR ('') in (:country)) and"
				+" (SSM.display_sub_sp in (:serviceLines) OR ('') in (:serviceLines))",nativeQuery=true)
	List<ConnectT> findByConnectReport(
				@Param("startDate") Timestamp startDate,
				@Param("endDate") Timestamp endDate,
				@Param("iou") List<String> iou,
				@Param("geography")List<String> geography,
				@Param("country") List<String> country,
				@Param("serviceLines") List<String> serviceLines);
		
			
	@Query(value= "select count(distinct(CON.connect_id)),display_geography from connect_t CON "
				+"   JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"  
				+"   JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
				+"   JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
				+"   JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography"
				+"   JOIN connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id"   
				+"   JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp" 
				+" where CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
				+" (ICMT.display_iou IN (:iou) OR ('') IN (:iou)) AND"
				+" (GMT.display_geography IN (:geography) OR ('') IN (:geography)) AND"
				+" (GCM.country IN (:country) OR ('') IN (:country)) and"
				+" (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))"
				+ "group by display_geography",nativeQuery=true)
	List<Object[]> findByGeographyConnectSummaryReport(
				@Param("startDate") Timestamp startDate,
				@Param("endDate") Timestamp endDate,
				@Param("iou") List<String> iou,
				@Param("geography")List<String> geography,
				@Param("country") List<String> country,
				@Param("serviceLines") List<String> serviceLines);
		
	@Query(value= "select count(distinct(CON.connect_id)),display_sub_sp  from connect_t CON "
				+"   JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"  
				+"   JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
				+"   JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
				+"   JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography"
				+"   JOIN connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id"   
				+"   JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp" 
				+" where CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
				+" (ICMT.display_iou in (:iou) OR ('') IN (:iou)) AND"
				+" (GMT.display_geography in (:geography) OR ('') IN (:geography)) AND"
				+" (GCM.country IN (:country) OR ('') IN (:country)) AND"
				+" (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))"
				+ "group by display_sub_sp",nativeQuery=true)
	List<Object[]> findBySubSpConnectSummaryReport(
						@Param("startDate") Timestamp startDate,
						@Param("endDate") Timestamp endDate,
						@Param("iou") List<String> iou,
						@Param("geography")List<String> geography,
						@Param("country") List<String> country,
						@Param("serviceLines") List<String> serviceLines);
				
				
	@Query(value= "select count(distinct(CON.connect_id)),display_iou from connect_t CON "
				+"   JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"  
				+"   JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
				+"   JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
				+"   JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography"
				+"   JOIN connect_sub_sp_link_t CSL on CON.connect_id=CSL.connect_id"   
				+"   JOIN sub_sp_mapping_t SSM on CSL.sub_sp=SSM.sub_sp "
				+" where CON.start_datetime_of_connect between (:startDate) AND (:endDate) AND "
				+" (ICMT.display_iou IN (:iou) OR ('') IN (:iou)) AND"
				+" (GMT.display_geography IN (:geography) OR ('') IN (:geography)) AND"
				+" (GCM.country IN (:country) OR ('') IN (:country)) AND"
				+" (SSM.display_sub_sp IN (:serviceLines) OR ('') IN (:serviceLines))"
				+ "group by display_iou",nativeQuery=true)
	List<Object[]> findByIouConnectSummaryReport(
						@Param("startDate") Timestamp startDate,
						@Param("endDate") Timestamp endDate,
						@Param("iou") List<String> iou,
						@Param("geography")List<String> geography,
						@Param("country") List<String> country,
						@Param("serviceLines") List<String> serviceLines);


}
