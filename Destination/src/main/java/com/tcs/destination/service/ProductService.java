package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

/**
 * This service deals with city requests
 *
 */
@Service
public class ProductService {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
	
	@Autowired
	ProductRepository productRepository;

	/**
	 * The Service is Used to get the products by ajaxSearch
	 * @param nameWith
	 * @return 
	 * @throws Exception
	 */
	public List<ProductMasterT>findProductsAjaxSearch(String nameWith) throws Exception{
		logger.info("Inside findProductsAjaxSearch() method"); 
		List<ProductMasterT> products=null;

		nameWith = "%" + nameWith + "%";
		if(nameWith!=null)
		{
		 products=productRepository.getProductsByProductNameKeyword(nameWith);
		}
       if((products == null)||(products.isEmpty())){
			logger.error("NOT_FOUND : No product found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No product found");
		}
		logger.info("End of findProductsAjaxSearch() method");
		return products;

	}
}
