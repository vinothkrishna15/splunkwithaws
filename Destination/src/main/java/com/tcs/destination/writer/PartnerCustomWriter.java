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

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.PartnerUploadHelper;
import com.tcs.destination.service.PartnerService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;

public class PartnerCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {
	
	private static final Logger logger = LoggerFactory
			.getLogger(PartnerCustomWriter.class);
	
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private PartnerUploadHelper helper;
	
	private DataProcessingRequestT request; 
	
	private StepExecution stepExecution;
	
	private List<UploadServiceErrorDetailsDTO> errorList = null;
	
	private UploadErrorReport uploadErrorReport;
	
	private PartnerService partnerService;
	
  


	private PartnerRepository partnerRepository;
	
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.debug("Inside write:");
		
		List<PartnerMasterT> insertList = new ArrayList<PartnerMasterT>();
		String operation = null; 
		
		for (String[] data: items) {
			operation = (String) data[1];
			if(operation!=null)
			{
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {
				
				PartnerMasterT partner =  new PartnerMasterT();
				UploadServiceErrorDetailsDTO errorDTO = helper.validatePartnerData(data, request.getUserT().getUserId() ,partner);
				if (errorDTO.getMessage() != null) {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					insertList.add(partner);
				}
				
			}
			else if (operation.equalsIgnoreCase(Operation.UPDATE.name()))
			{
                String partnerId =data[2];
                UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
				
				
				if (!partnerId.isEmpty()) {
					try{
						
						 PartnerMasterT partner= partnerRepository.findByPartnerId(partnerId);
					
					
					if (partner != null) {
						errorDTO = helper.validatePartnerDataUpdate(data, request.getUserT().getUserId() ,partner);
						if (errorDTO.getMessage() != null) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						} 
						else if (errorDTO.getMessage() == null) {
							insertList.add(partner);
						}
					} else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Partner Id is invalid");
						errorList.add(errorDTO);
					}
				}catch(InvocationTargetException e){System.out.println("Exception Cause:"+e.getCause());}
					}
				else {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorDTO.setMessage("Partner Id is mandatory");
					errorList.add(errorDTO);
				}
				
				}
			else if (operation.equalsIgnoreCase(Operation.DELETE.name())){
				PartnerMasterT partner =  new PartnerMasterT();
				partner = partnerRepository.findByPartnerId(data[2]);
				 UploadServiceErrorDetailsDTO errorDTO = helper.validatePartnerId(data, partner);
				 
				 if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						insertList.add(partner);
				}
			
			}
				
			
			
			
		
		if (CollectionUtils.isNotEmpty(insertList)) {
			
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {
				partnerService.save(insertList);
			} 
			else if (operation.equalsIgnoreCase(Operation.UPDATE.name())){ 
				partnerService.updatePartner(insertList);
			}
			else if (operation.equalsIgnoreCase(Operation.DELETE.name())){ 
				partnerService.deletePartner(insertList);
			}
		}
			}
		}
	}


	public PartnerUploadHelper getHelper() {
		return helper;
	}


	public void setHelper(PartnerUploadHelper helper) {
		this.helper = helper;
	}

	  public PartnerRepository getPartnerRepository() {
			return partnerRepository;
		}


		public void setPartnerRepository(PartnerRepository partnerRepository) {
			this.partnerRepository = partnerRepository;
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

	
	public PartnerService getPartnerService() {
		return partnerService;
	}


	public void setPartnerService(PartnerService partnerService) {
		this.partnerService = partnerService;
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
				String errorFileName = "partnerUpload_error.xlsx";
				
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
