package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.PartnerMasterT;

@Repository
public interface CustomerRepository extends
		CrudRepository<CustomerMasterT, String> {

	CustomerMasterT findByCustomerName(String customerName);

	List<CustomerMasterT> findByCustomerId(String customerid);

	List<CustomerMasterT> findByCustomerNameIgnoreCaseContainingOrderByCustomerNameAsc(
			String customerName);

	Page<CustomerMasterT> findByCustomerNameIgnoreCaseContainingAndCustomerNameIgnoreCaseNotLikeAndActiveOrderByCustomerNameAsc(
			String name, String nameNot,boolean active,Pageable page);

	Page<CustomerMasterT> findByCustomerNameIgnoreCaseStartingWithAndCustomerNameIgnoreCaseNotLikeAndActiveOrderByCustomerNameAsc(
			String name, String nameNot,boolean active,Pageable page);
	
	
	@Query(value = "select * from customer_master_t where customer_id=? and active=TRUE", nativeQuery=true)
	CustomerMasterT findactivecust(String cust_id);

	@Query(value = "select * from customer_Master_T c where c.active=TRUE ORDER BY c.created_Modified_Datetime desc Limit ?1", nativeQuery = true)
	List<CustomerMasterT> findRecent(int count);

	@Query(value = "select CMT.* from customer_master_t CMT,(select RCMT.customer_name,sum(ART.revenue) from actual_revenues_data_t ART,revenue_customer_mapping_t RCMT where financial_year=?2 and ART.finance_customer_name=RCMT.finance_customer_name and ART.finance_geography=RCMT.customer_geography group by RCMT.customer_name order by sum desc limit ?1) as RV where RV.customer_name=CMT.customer_name order by RV.sum desc", nativeQuery = true)
	List<CustomerMasterT> findTopRevenue(int count, String financialYear);

	@Query(value = "select 	ART.quarter,sum(ART.revenue) as actual from ACTUAL_REVENUES_DATA_T ART,REVENUE_CUSTOMER_MAPPING_T RCM where RCM.customer_name =?1 and ART.financial_year = ?2 and ART.finance_customer_name = RCM.finance_customer_name group by 	ART.QUARTER", nativeQuery = true)
	List<Object[]> findActual(String customerName, String financialYear);

	@Query(value = "SELECT B.Quarter,B.target FROM BEACON_DATA_T B,BEACON_CUSTOMER_MAPPING_T CM WHERE  B.beacon_customer_name =CM.beacon_customer_name AND B.FINANCIAL_YEAR=?2  AND CM.customer_name=?1", nativeQuery = true)
	List<Object[]> findTarget(String customerName, String financialYear);

	
	List<CustomerMasterT> findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeAndActiveOrderByGroupCustomerNameAsc(
			String groupCustName, String groupCustNameNot,boolean active);
	
	List<CustomerMasterT> findByGroupCustomerNameIgnoreCaseContainingAndGroupCustomerNameIgnoreCaseNotLikeOrderByGroupCustomerNameAsc(
			String groupCustName, String groupCustNameNot);


	// @Query(value =
	// "update customer_master_t set logo = ?1  where customer_id=?2",
	// nativeQuery = true)
	// void addImage(byte[] imageBytes, String id);

	@Query(value = "select ART.quarter,sum(ART.revenue) as actual from ACTUAL_REVENUES_DATA_T ART,REVENUE_CUSTOMER_MAPPING_T RCM where ART.financial_year like (?1) "
			+ "and ART.finance_customer_name = RCM.finance_customer_name and (ART.QUARTER= (?2) or (?2)  = '') "
			+ "and RCM.customer_name in (select CMT.customer_name from customer_master_t CMT "
			+ "where ( CMT.geography in (select geography from geography_mapping_t where (display_geography= (?3) OR (?3) =''))) "
			+ "and CMT.iou in (select iou from iou_customer_mapping_t where (display_iou= (?5) OR (?5) ='')) "
			+ "and CMT.customer_id in (select customer_id from opportunity_t OPP "
			+ "where OPP.opportunity_id in (select opportunity_id from opportunity_sub_sp_link_t "
			+ "where sub_sp in (select sub_sp from sub_sp_mapping_t where (display_sub_sp= (?4) OR (?4) ='')))) "
			+ "and (CMT.customer_id = (?6) OR (?6) ='')) group by ART.QUARTER", nativeQuery = true)
	List<Object[]> findActualRevenue(String financialYear, String quarter,
			String geography, String serviceLine, String iou, String customerId);

	@Query(value = "select * from customer_master_t where customer_id in (:custIds)", nativeQuery = true)
	List<CustomerMasterT> getCustomersByIds(
			@Param("custIds") List<String> customerIds);

	@Query(value = "update customer_master_t set logo = ?1  where customer_id=?2", nativeQuery = true)
	void addImage(byte[] imageBytes, String id);

	@Query(value = "select * from customer_master_t", nativeQuery = true)
	List<CustomerMasterT> getNameAndId();
	
	@Query(value = "select distinct group_customer_name, customer_name, iou, geography, customer_id from customer_master_t", nativeQuery = true)
	List<Object[]> getCustomerNameAndIouAndGeography();

	@Query(value = "select customer_id from customer_master_t", nativeQuery = true)
	List<String> getCustomerIds();

	@Query(value = "select customer_name from customer_master_t where group_customer_name = ?1", nativeQuery = true)
	List<String> findByGroupCustomerName(String groupCustName);
	
	@Query(value = "select customer_id from customer_master_t where "
			+ "(group_customer_name = (:groupCustomerName)"
			+ "and customer_name =(:customerName)"
			+ "and iou=(:displayIOU)"
			+ "and geography =(:geography))", nativeQuery = true)
	String findCustomerIdForDeleteOrUpdate(
			@Param("groupCustomerName") String groupCustomerName,
			@Param("customerName") String name,
			@Param("displayIOU") String displayIOU,
			@Param("geography") String geography);

	@Query(value = "select * from customer_master_t where "
			+ "(active='true' or active=(:active))"
			+ "and (upper(customer_name) like (:customerName)) "
			+ "and (upper(group_customer_name) like (:groupCustomerName)) "
			+ "and (geography in (:geography) or ('') in (:geography)) "
			+ "and iou in (select iou from iou_customer_mapping_t where (display_iou in (:displayIOU) or ('') in (:displayIOU)))", nativeQuery = true)
	List<CustomerMasterT> advancedSearch(
			@Param("groupCustomerName") String groupCustomerNameWith,
			@Param("customerName") String nameWith,
			@Param("geography") List<String> geography,
			@Param("displayIOU") List<String> displayIOU,
			@Param("active") boolean active);
	
	
	@Query(value = "select customer_id,customer_name from customer_master_t", nativeQuery=true)
    List<Object[]> findAllCustomerIdName();
    
    @Query(value = "select customer_name from customer_master_t where customer_name = (:customerName)", nativeQuery = true)
    String findCustomerName(@Param("customerName") String customerName);
    
    @Query(value = "select geography from customer_master_t where customer_id = ?1", nativeQuery = true)
	String findGeographyByCustomerId(String customerId);
    
    @Query(value = "select iou from customer_master_t where customer_id = ?1", nativeQuery = true)
	String findIouByCustomerId(String customerId);
    
    CustomerMasterT findByActiveTrueAndCustomerId(String customerId);

	
	/* ---------- repository methods for smart search --------- */

    @Query(value = "SELECT * FROM customer_master_t "
			+ "WHERE active = 'true' AND UPPER(group_customer_name) LIKE UPPER(:term) "
			+ "ORDER BY created_modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
    List<CustomerMasterT> getCustomersByGrpCustName(@Param("term") String term, @Param("getAll") boolean getAll);

    @Query(value = "SELECT * FROM customer_master_t "
			+ "WHERE active = 'true' AND UPPER(customer_name) LIKE UPPER(:term) "
			+ "ORDER BY created_modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<CustomerMasterT> getCustomersByName(@Param("term") String term, @Param("getAll") boolean getAll);

    @Query(value = "SELECT * FROM customer_master_t "
			+ "WHERE active = 'true' AND UPPER(geography) LIKE UPPER(:term) "
			+ "ORDER BY created_modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<CustomerMasterT> getCustomersByGeography(@Param("term") String term, @Param("getAll") boolean getAll);

    @Query(value = "SELECT * FROM customer_master_t "
			+ "WHERE active = 'true' AND UPPER(iou) LIKE UPPER(:term) "
			+ "ORDER BY created_modified_datetime DESC "
			+ "LIMIT CASE WHEN :getAll THEN null ELSE 3 END", nativeQuery = true)
	List<CustomerMasterT> getCustomersByIou(@Param("term") String term, @Param("getAll") boolean getAll);
    
    List<CustomerMasterT> findByActiveTrue();

	/* ---------- ends - repository methods for smart search --------- */
}
