/**
 * 
 * RgsCustomWriter.java 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.commons.lang.StringUtils;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.DeliveryRgsT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.data.repository.RgsRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.ProductUploadHelper;
import com.tcs.destination.helper.RgsUploadHelper;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;
/**
 * This ProductCustomWriter class provide the functionality for writing product details to db, and having listener functionality for steps
 * 
 */
public class RgsCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(RgsCustomWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private RgsUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	private RgsRepository rgsDetailsRepository;
	
	private StepExecution stepExecution;
	
	Map<String,DeliveryRequirementT> rgsIdRequirementMap;
	
	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}


	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}


	public RgsUploadHelper getHelper() {
		return helper;
	}


	public void setHelper(RgsUploadHelper helper) {
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


	public RgsRepository getRgsDetailsRepository() {
		return rgsDetailsRepository;
	}


	public void setRgsDetailsRepository(RgsRepository rgsDetailsRepository) {
		this.rgsDetailsRepository = rgsDetailsRepository;
	}


	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		List<DeliveryRgsT> insertList = new ArrayList<DeliveryRgsT>();
		
		
		
		String operation = null; 
		for (String[] data : items) {
			int rowNumber = Integer.parseInt(data[0]) + 1;
			//operation = (String) data[1];
			//if (operation.equalsIgnoreCase(Operation.ADD.name())) {

				DeliveryRgsT rgst = new DeliveryRgsT();
				DeliveryRequirementT deliveryRequirementT = new DeliveryRequirementT();
				UploadServiceErrorDetailsDTO errorDTO = helper
						.validateRgsData(data, request.getUserT()
								.getUserId(), rgst,deliveryRequirementT);
				if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
							: errorList;
					errorList.add(errorDTO);
				} else if (StringUtils.isEmpty(errorDTO.getMessage())) {
					insertList.add(rgst);
					rgsIdRequirementMap.put(rgst.getDeliveryRgsId(), deliveryRequirementT);
				}

			//} 
		}
		if (CollectionUtils.isNotEmpty(insertList)) {
			rgsDetailsRepository.save(insertList);
		} 
		
		
		
	}


	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		try {
			this.stepExecution = stepExecution;
			rgsIdRequirementMap = new HashMap<String,DeliveryRequirementT>();
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
			jobContext.put("rgsRequirementMap", rgsIdRequirementMap);
			
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			if (request != null && errorList != null) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList);
				
				String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
				String errorFileName = "rgsUpload_error.xlsx";
				
				File file = FileManager.createFile(errorPath, errorFileName);
				FileOutputStream outputStream = new FileOutputStream(file);
				workbook.write(outputStream);
				outputStream.flush();
				outputStream.close();
				
				request.setErrorFileName(errorFileName);	
				request.setErrorFilePath(errorPath);
				
			}
			
			
			dataProcessingRequestRepository.save(request);
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
