package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.CustomerMasterT;

@Repository
public interface CustomerRepository extends
		CrudRepository<CustomerMasterT, String> {

	List<CustomerMasterT> findByCustomerName(String customerName);

	List<CustomerMasterT> findByCustomerId(String customerid);

	List<CustomerMasterT> findByCustomerNameIgnoreCaseLike(String name);

//	List<CustomerMasterT> findTop5CustomerMasterTsOrPartnerMasterTsOrderByCreatedModifiedDatetime();

}
