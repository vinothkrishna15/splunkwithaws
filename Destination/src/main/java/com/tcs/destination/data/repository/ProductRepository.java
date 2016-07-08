package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ProductMasterT;

/**
 * 
 * Repository for working with {@link ProductMasterT} domain objects
 */
@Repository
public interface ProductRepository extends CrudRepository<ProductMasterT, String> {

}
