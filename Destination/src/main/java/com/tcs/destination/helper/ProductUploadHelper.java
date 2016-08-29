/**
 * 
 * ProductUploadHelper.java 
 *
 * @author TCS
 * 
 */
package com.tcs.destination.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.utils.StringUtils;

/**
 * This ProductUploadHelper class holds the functionality to aid product upload
 * 
 */
@Component("productUploadHelper")
public class ProductUploadHelper {

	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CommonHelper commonHelper;
	
	/**
	 * validate product details before insert
	 * @param data
	 * @param userId
	 * @param productT
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateProductData(String[] data,
			String userId, ProductMasterT productT) throws Exception {

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		int rowNumber = Integer.parseInt(data[0]) + 1;
		String productName = data[3];
		String productDescription = data[4];
		if (StringUtils.isEmpty(productName)) {
			error.setRowNumber(rowNumber);
			error.setMessage("product Name Is Mandatory; ");
		} else {
			ProductMasterT dbProduct = productRepository.findByProductName(productName);
			if(dbProduct!=null){
				error.setRowNumber(rowNumber);
				error.setMessage("product Name already exists; ");
			}
			productT.setProductName(productName);
			productT.setProductDescription(productDescription);
			productT.setCreatedBy(userId);
			productT.setModifiedBy(userId);
		}
		
		return error;
	}

	
	/**
	 * validate product details before update
	 * @param data
	 * @param userId
	 * @param productT
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateProductDataUpdate(
			String[] data, String userId, ProductMasterT productT) {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		int rowNumber = Integer.parseInt(data[0]) + 1;
		String productName = data[3];
		String productDescription = data[4];
		String productId = data[2];
		ProductMasterT product = productRepository.findOne(productId);
		if (product != null && product.isActive()) {
			if (!StringUtils.isEmpty(productName)) {

				productT.setProductName(productName);
				productT.setProductDescription(productDescription);
				productT.setModifiedBy(userId);

			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("product Name Is Mandatory ");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("invalid product Id ");
		}
		return error;
	}

	
	
}

	




	







	

	
	

