package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;

/**
 * This service downloads product data from database into an excel
 */
@Service
public class ProductDownloadService 
{
	@Autowired
	ProductRepository productRepository;

	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheets;

	private static final Logger logger = LoggerFactory.getLogger(ProductDownloadService .class);

	/**
	 * this method downloads the sheet Product Master
	 * @param oppFlag
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getProducts(boolean oppFlag) throws Exception 
	{
		logger.debug("Begin:Inside getProducts() method of ProductDownloadService"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook =(HSSFWorkbook) ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.PRODUCT_TEMPLATE_LOCATION_PROPERTY_NAME)));

			// Populate Product Master Sheet
			if(oppFlag)
				populateProductMasterSheet(workbook.getSheet(Constants.PRODUCT_MASTER_SHEET));

			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"An Internal Exception has occured");
		}
		logger.debug("End:Inside getProducts() method of ProductDownloadService"); 
		return inputStreamResource;
	}

	
	/**
	 * This Method Writes product details into the workbook
	 * @param productSheet
	 */
	private void  populateProductMasterSheet(Sheet productSheet) 
	{
		//Get the Product Master Sheet From Workbook
		logger.debug("Begin:Inside populateProductMasterSheet() method of ProductDownloadService"); 
		int currentRow = 1; // Excluding the header, header starts with index 0

		List<ProductMasterT> productsList=(List<ProductMasterT>) productRepository.findAll();
		for(ProductMasterT product:productsList){

			Row row = productSheet.createRow(currentRow);

			// Get Cell and set cell value
			String productId = product.getProductId();
			if(!StringUtils.isEmpty(productId)){
			row.createCell(1).setCellValue(productId.toString());
			}
			
			String productName = product.getProductName();
			if(!StringUtils.isEmpty(productName)){
			row.createCell(2).setCellValue(productName.toString());
			}
			
			String productDescription = product.getProductDescription();
			if(!StringUtils.isEmpty(productDescription)){
			row.createCell(3).setCellValue(productDescription.toString());
			}
			
			String active = new Boolean(product.isActive()).toString();
			if(!StringUtils.isEmpty(active)){
			row.createCell(4).setCellValue(active.toString());
			}
			// Increment row counter
			currentRow++;
		}
		logger.debug("End:Inside populateProductMasterSheet() method of ProductDownloadService"); 
	}

}
