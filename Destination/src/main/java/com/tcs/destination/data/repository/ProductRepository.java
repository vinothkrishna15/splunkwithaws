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
	    
	    @Query(value = "SELECT * FROM product_master_t WHERE UPPER(product_name) like UPPER(?1) ORDER BY product_id asc", nativeQuery = true)
		List<ProductMasterT> getProductsByProductNameKeyword(String productName);
	    
	    @Query(value = "select * from product_master_t where product_name like (?1) AND product_id IN (select distinct product_id from partner_subsp_product_mapping_t where partner_subsp_mapping_id IN  (select partner_subsp_mapping_id from partner_sub_sp_mapping_t where partner_id = (?2) and sub_sp_id IN (?3)) ORDER BY product_id asc)", nativeQuery = true)
	    List<ProductMasterT> findProductsForPartnerAndSubsp(String nameWith, String partnerId, List<Integer> subspList );
}
