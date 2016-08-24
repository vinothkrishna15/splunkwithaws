/**
 * 
 * ProductCustomWriter.java 
 *
 * @author TCS
 */
package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.FILE_PATH;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.WriteListener;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.ProductUploadHelper;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.StringUtils;
/**
 * This ProductCustomWriter class provide the functionality for writing product details to db, and having listener functionality for steps
 * 
 */
public class ProductCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ProductCustomWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private ProductUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	private ProductRepository productRepository;
	
	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}


	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}


	public ProductUploadHelper getHelper() {
		return helper;
	}


	public void setHelper(ProductUploadHelper helper) {
		this.helper = helper;
	}


	public DataProcessingRequestT getRequest() {
		return request;
	}


	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}


	public List<UploadServiceErrorDetailsDTO> getErrorList() {
		return errorList;
	}


	public void setErrorList(List<UploadServiceErrorDetailsDTO> errorList) {
		this.errorList = errorList;
	}


	public UploadErrorReport getUploadErrorReport() {
		return uploadErrorReport;
	}


	public void setUploadErrorReport(UploadErrorReport uploadErrorReport) {
		this.uploadErrorReport = uploadErrorReport;
	}


	public ProductRepository getProductRepository() {
		return productRepository;
	}


	public void setProductRepository(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}


	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		List<ProductMasterT> insertList = new ArrayList<ProductMasterT>();
		List<ProductMasterT> updateList = new ArrayList<ProductMasterT>();
		List<ProductMasterT> deleteList = new ArrayList<ProductMasterT>();
		
		
		String operation = null; 
		for (String[] data : items) {

			operation = (String) data[1];
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {

				ProductMasterT product = new ProductMasterT();
				UploadServiceErrorDetailsDTO errorDTO = helper
						.validateProductData(data, request.getUserT()
								.getUserId(), product);
				if (errorDTO.getMessage() != null) {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
							: errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					insertList.add(product);
				}

			} else {
				int rowNumber = Integer.parseInt(data[0]) + 1;
				if (operation.equalsIgnoreCase(Operation.UPDATE.name())) {

					String productId = data[2];
					UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();

					if (!StringUtils.isEmpty(productId)) {
						ProductMasterT productT = productRepository
								.findOne(productId);
						if (productT != null) {
							errorDTO = helper.validateProductDataUpdate(data,
									request.getUserT().getUserId(), productT);
							if (errorDTO.getMessage() != null) {
								errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
										: errorList;
								errorList.add(errorDTO);
							} else if (errorDTO.getMessage() == null) {
								updateList.add(productT);
							}
						} else {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
									: errorList;
							errorDTO.setRowNumber(rowNumber);
							errorDTO.setMessage("product Id is invalid; ");
							errorList.add(errorDTO);
						}
					} else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorDTO.setRowNumber(rowNumber);
						errorDTO.setMessage("product Id is mandatory for update ");
						errorList.add(errorDTO);
					}

				} else if (operation.equalsIgnoreCase(Operation.DELETE.name())) {
					String productId = data[2];
					UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
					if (!StringUtils.isEmpty(productId)) {
						ProductMasterT productT = productRepository
								.findOne(productId);
						if (productT != null) {
							if (errorDTO.getMessage() != null) {
								errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
										: errorList;
								errorList.add(errorDTO);
							} else if (errorDTO.getMessage() == null) {
								deleteList.add(productT);
							}
						} else {
							errorDTO.setRowNumber(rowNumber);
							errorDTO.setMessage("Invalid product Id ");
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
									: errorList;
							errorList.add(errorDTO);
						}
					} else {
						errorDTO.setRowNumber(rowNumber);
						errorDTO.setMessage("product Id is mandatory for delete");
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorList.add(errorDTO);
					}
				}
			}
		}
		
		if (CollectionUtils.isNotEmpty(insertList)) {
			productRepository.save(insertList);
		} 
		if (CollectionUtils.isNotEmpty(updateList)){ 
			productRepository.save(updateList);
		} 
		if (CollectionUtils.isNotEmpty(deleteList)){ 
			for(ProductMasterT item:deleteList){
				item.setActive(false);
				productRepository.save(item);
			}
		}
		
		
	}


	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		try {
			
			DataProcessingRequestT request = (DataProcessingRequestT) stepExecution.getJobExecution().getExecutionContext().get(REQUEST);
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);
			
		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}
			
			
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
		try {
			
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			if (request != null && errorList != null) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList);
				
				String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
				String errorFileName = "productUpload_error.xlsx";
				
				File file = FileManager.createFile(errorPath, errorFileName);
				FileOutputStream outputStream = new FileOutputStream(file);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();
				
				request.setErrorFileName(errorFileName);	
				request.setErrorFilePath(errorPath);
				
			}
			
			request.setStatus(RequestStatus.PROCESSED.getStatus());
			dataProcessingRequestRepository.save(request);
			
			jobContext.remove(REQUEST);
			jobContext.remove(FILE_PATH);
		} catch (Exception e) {
			logger.error("Error while writing the error report: {}", e);
		}
		
		return ExitStatus.COMPLETED;
	}


	@Override
	public void onWritePossible() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(Throwable throwable) {
		logger.error("Error while writing the error report: {}", throwable);
		
	}

}
