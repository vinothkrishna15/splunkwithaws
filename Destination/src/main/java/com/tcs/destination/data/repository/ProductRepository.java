package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.ProductMasterT;

/**
 * 
 * Repository for working with {@link ProductMasterT} domain objects
 */
@Repository
public interface ProductRepository extends CrudRepository<ProductMasterT, String> {
	
	    @Query(value="select product_name from product_master_t", nativeQuery=true)
	    List<String> getProductName();
	    
	    ProductMasterT findByProductName(String product);
	    
	    ProductMasterT findByProductId(String productId);

}
