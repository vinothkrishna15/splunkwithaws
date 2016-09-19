package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ProductContactLinkT;


@Repository
public interface ProductContactLinkTRepository extends
		CrudRepository<ProductContactLinkT, String> {

	List<ProductContactLinkT> findByProductId(String productId);

	List<ProductContactLinkT> findByProductIdAndContactId(String productId,
			String contactId);
	
}
