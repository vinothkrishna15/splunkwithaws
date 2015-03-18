package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CustomerMasterT;

@Repository
public interface CustomerRepository extends
		CrudRepository<CustomerMasterT, String> {

	List<CustomerMasterT> findByCustomerName(String customerName);

	List<CustomerMasterT> findByCustomerId(String customerid);

	List<CustomerMasterT> findByCustomerNameIgnoreCaseLike(String name);

	@Query(value="select * from customer_Master_T c ORDER BY c.created_Modified_Datetime desc Limit ?1",nativeQuery=true)
	List<CustomerMasterT> findRecent(int count);

	@Query(value = "select * from customer_master_t where customer_name IN(select a.CUSTOMER_NAME from REVENUE_CUSTOMER_MAPPING_T a join (select FINANCE_CUSTOMER_NAME,SUM(REVENUE) AS rev from ACTUAL_REVENUES_DATA_T where FINANCIAL_YEAR=?2 Group By FINANCE_CUSTOMER_NAME  Order By rev Desc Limit ?1) b on b.FINANCE_CUSTOMER_NAME = a.FINANCE_CUSTOMER_NAME) ", nativeQuery = true)
	List<CustomerMasterT> findTopRevenue(int count,String financialYear);

	@Query(value = "select 	ART.quarter,sum(ART.revenue) as actual from ACTUAL_REVENUES_DATA_T ART,REVENUE_CUSTOMER_MAPPING_T RCM where RCM.customer_name =?1 and ART.financial_year = ?2 and ART.finance_customer_name = RCM.finance_customer_name group by 	ART.QUARTER", nativeQuery = true)
	List<Object[]> findActual(String customerName, String financialYear);

	@Query(value = "SELECT B.Quarter,B.target FROM BEACON_DATA_T B,BEACON_CUSTOMER_MAPPING_T CM WHERE  B.beacon_customer_name =CM.beacon_customer_name AND B.FINANCIAL_YEAR=?2  AND CM.customer_name=?1", nativeQuery = true)
	List<Object[]> findTarget(String customerName, String financialYear);

}
