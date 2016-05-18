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

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.PartnerContactUploadHelper;
import com.tcs.destination.service.ContactService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;

/**
 * This Custom writer class writes the validated excel data
 * into the database
 */
public class PartnerContactCustomWriter implements ItemWriter<String[]>, StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerContactCustomWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private PartnerContactUploadHelper helper;

	private DataProcessingRequestT request; 

	private StepExecution stepExecution;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private ContactService contactService;

	private ContactRepository contactRepository;

	/**
	 * This method performs insertion if the operation is ADD
	 * Firstly it validates the PartnerContact sheet for constraints
	 * and then saves the content into the repository
	 */
	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.info("Begin Inside write of PartnerContactCustomWriter:");

		List<ContactT> insertList = new ArrayList<ContactT>();
		List<ContactT> updateList = new ArrayList<ContactT>();
		List<ContactT> deleteList = new ArrayList<ContactT>();

		String operation = null; 

		for (String[] data: items) {
			operation = (String) data[1];
			if(operation!=null)
			{
				if (operation.equalsIgnoreCase(Operation.ADD.name())) {

					logger.debug("***PARTNER CONTACT ADD***");
					ContactT partnerContact =  new ContactT();
					UploadServiceErrorDetailsDTO errorDTO = helper.validatePartnerContactData(data, request.getUserT().getUserId() ,partnerContact);
					if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						insertList.add(partnerContact);
						logger.info("End: Inside write of PartnerContactCustomWriter:");
					}

				}
				else if (operation.equalsIgnoreCase(Operation.UPDATE.name()))
				{

					logger.debug("***PARTNER CONTACT UPDATE***");
					String contactId =data[9];
	                UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
					if (!contactId.isEmpty()) {
						try{
							
							ContactT contact= contactRepository.findByContactId(contactId);
						    if (contact != null) {
							errorDTO = helper.validatePartnerContactUpdate(data, request.getUserT().getUserId() ,contact);
							if (errorDTO.getMessage() != null) {
								errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
								errorList.add(errorDTO);
							} 
							else if (errorDTO.getMessage() == null) {
								updateList.add(contact);
							}
						} else {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
							errorDTO.setMessage("Contact Id is invalid");
							errorList.add(errorDTO);
						}
					}catch(InvocationTargetException e){System.out.println("Exception Cause:"+e.getCause());}
						}
					else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Contact Id is mandatory");
						errorList.add(errorDTO);
					}
				}
				
				else if (operation.equalsIgnoreCase(Operation.DELETE.name()))
				{

					logger.debug("***PARTNER CONTACT DELETE***");
					ContactT contactT =  new ContactT();
					contactT = contactRepository.findByContactId(data[9]);
					 UploadServiceErrorDetailsDTO errorDTO = helper.validateContactId(data, contactT);
					 
					 if (errorDTO.getMessage() != null) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						} else if (errorDTO.getMessage() == null) {
							deleteList.add(contactT);
					}
				
				
				
				}
				
				if ((CollectionUtils.isNotEmpty(insertList)) || (CollectionUtils.isNotEmpty(updateList)) || (CollectionUtils.isNotEmpty(deleteList))) 
				{
                    if (operation.equalsIgnoreCase(Operation.ADD.name())){
						contactService.save(insertList);
						logger.info("contact is saved");
					} 
					else if (operation.equalsIgnoreCase(Operation.UPDATE.name())){ 
						contactService.updateContact(updateList);
					}
					else if (operation.equalsIgnoreCase(Operation.DELETE.name())){ 
						contactService.deleteContact(deleteList);
					}
				}
			}
		}
	}


	public PartnerContactUploadHelper getHelper() {
		return helper;
	}


	public void setHelper(PartnerContactUploadHelper helper) {
		this.helper = helper;
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

	public ContactRepository getContactRepository() {
		return contactRepository;
	}

	public void setContactRepository(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}
	
	/**
	 * This afterStep method deals with the operations after the write()
	 * method is completed.
	 * It writes the error report in case of any errors
	 * and then closes all the opened connections. 
	 */
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

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
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
