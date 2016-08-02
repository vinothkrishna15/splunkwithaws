package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ProductContactLinkT;


@Repository
public interface ProductContactLinkTRepository extends
		CrudRepository<ProductContactLinkT, String> {}
