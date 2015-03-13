package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tcs.destination.bean.CustomerMasterT;

/**
 * 
 * Repository for working with {@link CustomerMasterT} domain objects
 *
 */

@Repository
public interface CustomerRepository extends
		CrudRepository<CustomerMasterT, String> {

	/**
	 * Finds the customer details for the given customer name.
	 * @param customerName is customer name.
	 * @return customer details.
	 */
	List<CustomerMasterT> findByCustomerName(String customerName);
	
	/**
	 * Finds the customer details for the given customer id.
	 * @param customerid is customer id.
	 * @return customer details.
	 */
	List<CustomerMasterT> findByCustomerId(String customerid);
	
	/**
	 * Finds the customer details for the given customer name with case insensitive.
	 * @param name is customer name.
	 * @return customer details.
	 */
	List<CustomerMasterT> findByCustomerNameIgnoreCaseLike(String name);
	
	/**
	 * Finds the Recent top 5 customer details.
	 * @return customer details.
	 */
	@Query("select c from CustomerMasterT c ORDER BY c.createdModifiedDatetime desc LIMIT 5")
	List<CustomerMasterT> findRecent5();
	
	/**
	 * Finds the Recent top 10 Revenue customer details.
	 * @return customer details.
	 */
	@Query(value = "select * from customer_master_t where customer_name IN(select a.CUSTOMER_NAME from REVENUE_CUSTOMER_MAPPING_T a join (select FINANCE_CUSTOMER_NAME,SUM(REVENUE) AS rev from ACTUAL_REVENUES_DATA_T where FINANCIAL_YEAR=?1 Group By FINANCE_CUSTOMER_NAME  Order By rev Desc Limit 10) b on b.FINANCE_CUSTOMER_NAME = a.FINANCE_CUSTOMER_NAME) ", nativeQuery = true)
	List<CustomerMasterT> findTop10RevenueCustomers(String financialYear);
	
	/**
	 * Finds the Actual Revenue of the customer for the given customer name and financial year.
	 * @param customerName is the customer name.
	 * @param financialYear is the financial year. 
	 * @return actual revenue.
	 */
	@Query(value = "select 	ART.quarter,sum(ART.revenue) as actual from ACTUAL_REVENUES_DATA_T ART,REVENUE_CUSTOMER_MAPPING_T RCM where RCM.customer_name =?1 and ART.financial_year = ?2 and ART.finance_customer_name = RCM.finance_customer_name group by 	ART.QUARTER", nativeQuery = true)
	List<Object[]> findActual(String customerName, String financialYear);
	
	/**
	 * Finds the Target Revenue of the customer for the given customer name and financial year.
	 * @param customerName is the customer name.
	 * @param financialYear is the financial year.
	 * @return target revenue.
	 */
	@Query(value = "SELECT B.Quarter,B.target FROM BEACON_DATA_T B,BEACON_CUSTOMER_MAPPING_T CM WHERE  B.beacon_customer_name =CM.beacon_customer_name AND B.FINANCIAL_YEAR=?2  AND CM.customer_name=?1", nativeQuery = true)
	List<Object[]> findTarget(String customerName, String financialYear);

}
