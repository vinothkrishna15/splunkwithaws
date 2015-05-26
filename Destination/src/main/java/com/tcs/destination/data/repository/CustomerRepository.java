package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CustomerMasterT;

@Repository
public interface CustomerRepository extends
		CrudRepository<CustomerMasterT, String> {

	List<CustomerMasterT> findByCustomerName(String customerName);

	List<CustomerMasterT> findByCustomerId(String customerid);

	List<CustomerMasterT> findByCustomerNameIgnoreCaseContainingOrderByCustomerNameAsc(String name);

	List<CustomerMasterT> findByCustomerNameIgnoreCaseStartingWithOrderByCustomerNameAsc(String name);

	@Query(value = "select * from customer_Master_T c ORDER BY c.created_Modified_Datetime desc Limit ?1", nativeQuery = true)
	List<CustomerMasterT> findRecent(int count);

	@Query(value = "select * from customer_master_t where customer_name IN(select a.CUSTOMER_NAME from REVENUE_CUSTOMER_MAPPING_T a join (select FINANCE_CUSTOMER_NAME,SUM(REVENUE) AS rev from ACTUAL_REVENUES_DATA_T where FINANCIAL_YEAR=?2 Group By FINANCE_CUSTOMER_NAME  Order By rev Desc Limit ?1) b on b.FINANCE_CUSTOMER_NAME = a.FINANCE_CUSTOMER_NAME) ", nativeQuery = true)
	List<CustomerMasterT> findTopRevenue(int count, String financialYear);

	@Query(value = "select 	ART.quarter,sum(ART.revenue) as actual from ACTUAL_REVENUES_DATA_T ART,REVENUE_CUSTOMER_MAPPING_T RCM where RCM.customer_name =?1 and ART.financial_year = ?2 and ART.finance_customer_name = RCM.finance_customer_name group by 	ART.QUARTER", nativeQuery = true)
	List<Object[]> findActual(String customerName, String financialYear);

	@Query(value = "SELECT B.Quarter,B.target FROM BEACON_DATA_T B,BEACON_CUSTOMER_MAPPING_T CM WHERE  B.beacon_customer_name =CM.beacon_customer_name AND B.FINANCIAL_YEAR=?2  AND CM.customer_name=?1", nativeQuery = true)
	List<Object[]> findTarget(String customerName, String financialYear);

	List<CustomerMasterT> findByGroupCustomerNameIgnoreCaseContainingOrderByGroupCustomerNameAsc(String groupCustName);

//	@Query(value = "update customer_master_t set logo = ?1  where customer_id=?2", nativeQuery = true)
//	void addImage(byte[] imageBytes, String id);
	
	@Query(value="select ART.quarter,sum(ART.revenue) as actual from ACTUAL_REVENUES_DATA_T ART,REVENUE_CUSTOMER_MAPPING_T RCM where ART.financial_year like (?1) "
			+ "and ART.finance_customer_name = RCM.finance_customer_name and (ART.QUARTER= (?2) or (?2)  = '') "
			+ "and RCM.customer_name in (select CMT.customer_name from customer_master_t CMT "
			+ "where ( CMT.geography in (select geography from geography_mapping_t where (display_geography= (?3) OR (?3) =''))) "
			+ "and CMT.iou in (select iou from iou_customer_mapping_t where (display_iou= (?5) OR (?5) ='')) "
			+ "and CMT.customer_id in (select customer_id from opportunity_t OPP "
			+ "where OPP.opportunity_id in (select opportunity_id from opportunity_sub_sp_link_t "
			+ "where sub_sp in (select sub_sp from sub_sp_mapping_t where (display_sub_sp= (?4) OR (?4) ='')))) "
			+ "and (CMT.customer_id = (?6) OR (?6) ='')) group by ART.QUARTER", nativeQuery=true)
	List<Object[]> findActualRevenue(
			String financialYear,
			String quarter,
			String geography,
			String serviceLine, 
			String iou,
			String customerId);

}
