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
import org.apache.commons.lang.StringUtils;
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
import com.tcs.destination.helper.CustomerContactUploadHelper;
import com.tcs.destination.service.ContactService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;

/**
 * This writer class is used to write the values which are read from the
 * spreadsheet reader
 * 
 * @author bnpp
 *
 */
public class CustomerContactCustomWriter implements ItemWriter<String[]>,
		StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerContactCustomWriter.class);

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private DataProcessingRequestT request;

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private UploadErrorReport uploadErrorReport;

	private ContactService contactService;

	private CustomerContactUploadHelper helper;
	
	private ContactRepository contactRepository;

	

	@Override
	public void onWritePossible() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable throwable) {
		logger.error("Error while writing the error report: {}", throwable);

	}

	@Override
	public void beforeStep(StepExecution stepExecution) {

		try {

			DataProcessingRequestT request = (DataProcessingRequestT) stepExecution
					.getJobExecution().getExecutionContext().get(REQUEST);
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);

		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();

			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			if (request != null && errorList != null) {
				Workbook workbook = uploadErrorReport
						.writeErrorToWorkbook(errorList);

				String errorPath = request.getFilePath() + "ERROR"
						+ FILE_DIR_SEPERATOR;
				String errorFileName = "customerContactUpload_error.xlsx";

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
	public void write(List<? extends String[]> items) throws Exception {
		// TODO Auto-generated method stub
		List<ContactT> contactList = new ArrayList<ContactT>();
		List<ContactT> updateList = new ArrayList<ContactT>();
		List<ContactT> deleteList = new ArrayList<ContactT>();
		String operation = null; 
		
		
		for (String[] data : items) {
			operation = (String) data[1];
			if(operation!=null)
			{
			if (operation.equalsIgnoreCase(Operation.ADD.name())) {
				logger.debug("***CUSTOMER CONTACT ADD***");
			ContactT contact = new ContactT();
			UploadServiceErrorDetailsDTO errorDTO = helper
					.validateCustomerContactData(data, request.getUserT()
							.getUserId(), contact);
			if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
				errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
						: errorList;
				errorList.add(errorDTO);
			} else {
				contactList.add(contact);
			}
			}
			else if (operation.equalsIgnoreCase(Operation.UPDATE.name()))
			{

				logger.debug("***CUSTOMER CONTACT UPDATE***");
				String contactId =data[2];
				ContactT contact = new ContactT();
                UploadServiceErrorDetailsDTO errorDTO = new UploadServiceErrorDetailsDTO();
				if (!contactId.isEmpty()) {
				
						
						contact= contactRepository.findByContactId(contactId);
						if (contact != null) {
						errorDTO = helper.validateContactDataUpdate(data, request.getUserT().getUserId() ,contact);
						if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
							errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
							errorList.add(errorDTO);
						} 
						else {
							updateList.add(contact);
						}
					} else {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorDTO.setRowNumber(Integer.parseInt(data[0]) + 1);
						errorDTO.setMessage("Contact Id is invalid");
						errorList.add(errorDTO);
					}
				
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

				logger.debug("***CUSTOMER CONTACT DELETE***");
				ContactT contactT =  new ContactT();
				contactT = contactRepository.findByContactId(data[2]);
				 UploadServiceErrorDetailsDTO errorDTO = helper.validateContactId(data, contactT);
				 
					if (StringUtils.isNotEmpty(errorDTO.getMessage())) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>(): errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						deleteList.add(contactT);
				}
			
			
			
			}
			
			}
		}
		   //for saving customer contact details
			if (CollectionUtils.isNotEmpty(contactList)) {
				contactService.saveContacts(contactList);
			} //for updating customer contact details
			else if (CollectionUtils.isNotEmpty(updateList)){ 
				contactService.updateContact(updateList);
			}//for deleting customer contact details
			else if (CollectionUtils.isNotEmpty(deleteList)){ 
				contactService.deleteContact(deleteList);
			}
		

	}

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
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

	public ContactService getContactService() {
		return contactService;
	}

	public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}

	public CustomerContactUploadHelper getHelper() {
		return helper;
	}

	public void setHelper(CustomerContactUploadHelper helper) {
		this.helper = helper;
	}
	
	public ContactRepository getContactRepository() {
		return contactRepository;
	}

	public void setContactRepository(ContactRepository contactRepository) {
		this.contactRepository = contactRepository;
	}

}
