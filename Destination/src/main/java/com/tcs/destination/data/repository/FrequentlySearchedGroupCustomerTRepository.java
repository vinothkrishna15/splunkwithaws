package com.tcs.destination.data.repository;

import java.util.List;

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
//	List<FrequentlySearchedGroupCustomersT> findByUserId(String userId);
	
}
