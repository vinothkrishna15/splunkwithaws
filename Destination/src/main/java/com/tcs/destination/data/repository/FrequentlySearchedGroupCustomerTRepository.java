package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.FrequentlySearchedGroupCustomersT;

@Repository
public interface FrequentlySearchedGroupCustomerTRepository extends
	CrudRepository<FrequentlySearchedGroupCustomersT, String> {

	/**
	 * This Method used to retrieve the frequently searched group customer details for the given userId
	 * @param userId
	 * @return
	 */
	@Query(value = "select * from frequently_searched_group_customers_t  where user_id = ?1 order by created_datetime desc limit 5 ", nativeQuery = true)
	List<FrequentlySearchedGroupCustomersT> findByUserId(String userId);

	/**
	 * This Method used to find whether group customer name is present or not
	 * @param groupCustomerName
	 * @return
	 */
	@Query(value = "select group_customer_name from frequently_searched_group_customers_t where group_customer_name = ?1 ", nativeQuery = true)
	String findByGroupCustomerName(String groupCustomerName);

	/**
	 * This Method delete the old frequently searched group customer record if present
	 * @param userId
	 */
	@Modifying
	@Query(value = "delete from frequently_searched_group_customers_t where created_datetime not in "
			+ " (select created_datetime from frequently_searched_group_customers_t  where user_id = ?1 order by created_datetime desc limit 5 "
			+ ") and user_id = ?1" , nativeQuery = true)
	void deleteOldFrequentlySearchedGroupCustomer(String userId);

}
