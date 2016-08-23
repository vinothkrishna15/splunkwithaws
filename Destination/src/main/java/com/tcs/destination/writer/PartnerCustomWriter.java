package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.StringUtils;

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
    
	private Map<String, String> mapOfPartnerAndHqLink = new HashMap<String, String>();
	
	List<PartnerMasterT> childList = new ArrayList<PartnerMasterT>();
	List<PartnerMasterT> parentList = new ArrayList<PartnerMasterT>();
    
    
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		
		logger.debug("Inside write:");
		List<PartnerMasterT> insertList = new ArrayList<PartnerMasterT>();
		List<PartnerMasterT> updateList = new ArrayList<PartnerMasterT>();
		List<PartnerMasterT> deleteList = new ArrayList<PartnerMasterT>();
		
		
		String operation = null; 
		
		for (String[] data: items) {
			operation = (String) data[1];
			if(operation!=null)
			{
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {
				
				logger.debug("***PARTNER ADD***");
				PartnerMasterT partner =  new PartnerMasterT();
				UploadServiceErrorDetailsDTO errorDTO = helper.validatePartnerData(data, request.getUserT().getUserId() ,partner,childList,parentList,mapOfPartnerAndHqLink);
				if (errorDTO.getMessage() != null) {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorList.add(errorDTO);
				} else if (errorDTO.getMessage() == null) {
					insertList.add(partner);
				}
				
			}
			else if (operation.equalsIgnoreCase(Operation.UPDATE.name()))
			{
				logger.debug("***PARTNER UPDATE***");
				String partnerId =data[2];
                UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
				if ((!StringUtils.isEmpty(partnerId))&&(partnerId!=null)) {
					    PartnerMasterT partner= partnerRepository.findByPartnerId(partnerId);
					    if (partner != null) {
						errorDTO = helper.validatePartnerDataUpdate(data, request.getUserT().getUserId() ,partner,childList,parentList);
						if (errorDTO.getMessage() != null) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						} 
						else if (errorDTO.getMessage() == null) {
							updateList.add(partner);
						}
					} else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Partner Id is invalid");
						errorList.add(errorDTO);
					}
				}
				else {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorDTO.setMessage("Partner Id is mandatory");
					errorList.add(errorDTO);
				}
				
				}
			else if (operation.equalsIgnoreCase(Operation.DELETE.name())){
				logger.debug("***PARTNER DELETE***");
				String partnerId =data[2];
				UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
				if ((!StringUtils.isEmpty(partnerId))&&(partnerId!=null)) {
					PartnerMasterT partner =  new PartnerMasterT();
					partner = partnerRepository.findByPartnerId(partnerId);
					if(partner!=null)
					{
					 errorDTO = helper.validatePartnerId(data, partner);
					if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						deleteList.add(partner);
				      }
					}
					else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Partner Id is invalid");
						errorList.add(errorDTO);
					}
			  }
			  else 
			  {
					errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
					errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorDTO.setMessage("Partner Id is mandatory");
					errorList.add(errorDTO);
				}
			}
		
			//to add partner details to db
			if (CollectionUtils.isNotEmpty(insertList)) {
				
				if(CollectionUtils.isNotEmpty(parentList))
				{
				 partnerService.save(parentList);
				}
				
			} //to update the partner details in db
			else if (CollectionUtils.isNotEmpty(updateList)){ 
				if(CollectionUtils.isNotEmpty(parentList))
				{
				 partnerService.updatePartner(parentList);
				}
				else if(CollectionUtils.isNotEmpty(childList))
				{
				 partnerService.updatePartner(childList);
				}
			}// to delete the partner details from db
			else if (CollectionUtils.isNotEmpty(deleteList)){ 
				partnerService.deletePartner(deleteList);
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
			
			if (CollectionUtils.isNotEmpty(childList)) {
				if (mapOfPartnerAndHqLink != null) {
					partnerService.saveChild(childList, mapOfPartnerAndHqLink);
				}

			}
			
			if ( errorList != null) {
				Workbook workbook = uploadErrorReport.writeErrorToWorkbook(errorList,Constants.PARTNER_TEMPLATE_PARTNER_SHEET_NAME);
				
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
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);
			mapOfPartnerAndHqLink.clear();
			parentList.clear();
			childList.clear();
			} 
		    catch (Exception e) {
			logger.error("Error while writing the error report: {}", e);
		}
		
		return stepExecution.getExitStatus();
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
