package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.FILE_PATH;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.BeaconUploadHelper;
import com.tcs.destination.service.BeaconDataService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;

public class BeaconCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(BeaconCustomWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private BeaconUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private StepExecution stepExecution;
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	private BeaconDataService beaconDataService;
	
    private BeaconDataTRepository beaconDataTRepository;
	
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		List<BeaconDataT> insertList = new ArrayList<BeaconDataT>();
		for (String[] data: items) {
			logger.debug("****BEACON ADD****");	
			BeaconDataT beacon =  new BeaconDataT();
			UploadServiceErrorDetailsDTO errorDTO = helper.validateBeaconData(data, request.getUserT().getUserId() ,beacon);
			if (errorDTO.getMessage() != null) {
				errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
				errorList.add(errorDTO);
			} else if (errorDTO.getMessage() == null) {
				insertList.add(beacon);
			}
		}
	    if (CollectionUtils.isNotEmpty(insertList)) {
	        beaconDataService.save(insertList);
		}
	  }


	public BeaconUploadHelper getHelper() {
		return helper;
	}


	public void setHelper(BeaconUploadHelper helper) {
		this.helper = helper;
	}

	public BeaconDataService getBeaconDataService() {
		return beaconDataService;
	}


	public void setBeaconDataService(BeaconDataService beaconDataService) {
		this.beaconDataService = beaconDataService;
	}
	
	public BeaconDataTRepository getBeaconDataTRepository() {
			return beaconDataTRepository;
		}


	public void setBeaconDataTRepository(BeaconDataTRepository beaconDataTRepository) {
			this.beaconDataTRepository = beaconDataTRepository;
		}


	public DataProcessingRequestT getRequest() {
		return request;
	}

	

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}


	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}


	public UploadErrorReport getUploadErrorReport() {
		return uploadErrorReport;
	}


	public void setUploadErrorReport(UploadErrorReport uploadErrorReport) {
		this.uploadErrorReport = uploadErrorReport;
	}




	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		
		try {
			
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			
			if ( errorList != null) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList);
				String errorPath = request.getFilePath() + "ERROR" +FILE_DIR_SEPERATOR;
				String errorFileName = "beaconUpload_error.xlsx";
				
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
	public void onWritePossible() throws IOException 
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onError(Throwable throwable) {
		logger.error("Error while writing the error report: {}", throwable);
		
	}


}
