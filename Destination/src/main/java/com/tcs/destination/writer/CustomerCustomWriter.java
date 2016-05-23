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

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.Operation;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.helper.CustomerUploadHelper;
import com.tcs.destination.service.CustomerService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.StringUtils;

public class CustomerCustomWriter implements ItemWriter<String[]>,
		StepExecutionListener, WriteListener {

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerCustomWriter.class);

	private List<UploadServiceErrorDetailsDTO> errorList = null;

	private CustomerUploadHelper helper;

	private DataProcessingRequestT request;

	private CustomerService customerService;

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private StepExecution stepExecution;

	private UploadErrorReport uploadErrorReport;

	@Override
	public void onWritePossible() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Throwable throwable) {
		// TODO Auto-generated method stub

	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {
			logger.info("inside exit status");
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();

			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			if (errorList != null) {
				logger.info("inside exit status if loop");
				Workbook workbook = uploadErrorReport
						.writeErrorToWorkbook(errorList);

				String errorPath = request.getFilePath() + "ERROR"
						+ FILE_DIR_SEPERATOR;
				String errorFileName = "connectUpload_error.xlsx";

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
	public void beforeStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub

	}

	public List<UploadServiceErrorDetailsDTO> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<UploadServiceErrorDetailsDTO> errorList) {
		this.errorList = errorList;
	}

	public CustomerUploadHelper getHelper() {
		return helper;
	}

	public void setHelper(CustomerUploadHelper helper) {
		this.helper = helper;
	}

	public DataProcessingRequestT getRequest() {
		return request;
	}

	public void setRequest(DataProcessingRequestT request) {
		this.request = request;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
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

	@Override
	public void write(List<? extends String[]> items) throws Exception {
		logger.info("Inside write:");

		List<CustomerMasterT> insertList = new ArrayList<CustomerMasterT>();
		List<CustomerMasterT> deleteList = new ArrayList<CustomerMasterT>();
		List<CustomerMasterT> updateList = new ArrayList<CustomerMasterT>();
		for (String[] data : items) {
			String operation = (String) data[1];
			
			if ((!StringUtils.isEmpty(operation))) {
				if (operation.equalsIgnoreCase(Operation.ADD.name())) {
					logger.info("executing " + operation + " operation");
					CustomerMasterT customer = new CustomerMasterT();
					for (String a : data)//for testing
						logger.info(a);
					UploadServiceErrorDetailsDTO errorDTO = helper
							.validateCustomerAdd(data, request.getUserT()
									.getUserId(), customer);
					if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						insertList.add(customer);
					}

				} else if (operation.equalsIgnoreCase(Operation.DELETE.name())) {
					logger.info("executing " + operation + " operation");
					CustomerMasterT customer = new CustomerMasterT();
					UploadServiceErrorDetailsDTO errorDTO = helper
							.validateCustomerDelete(data, request.getUserT()
									.getUserId(), customer);
					if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						deleteList.add(customer);
					}

				} else if (operation.equalsIgnoreCase(Operation.UPDATE.name())) {
					logger.info("executing " + operation + " operation");
					CustomerMasterT customer = new CustomerMasterT();
					UploadServiceErrorDetailsDTO errorDTO = helper
							.validateCustomerUpdate(data, request.getUserT()
									.getUserId(), customer);
					if (errorDTO.getMessage() != null) {
						errorList = (errorList == null) ? new ArrayList<UploadServiceErrorDetailsDTO>()
								: errorList;
						errorList.add(errorDTO);
					} else if (errorDTO.getMessage() == null) {
						updateList.add(customer);
					}

				}

			}
		}
		// for saving the rows which are valid
		if (CollectionUtils.isNotEmpty(insertList)) {
			customerService.save(insertList);
		}
		// for deleting the rows which are valid
		if (CollectionUtils.isNotEmpty(deleteList)) {
			customerService.makeInactive(deleteList);
		}
		// for updating the rows which are valid
				if (CollectionUtils.isNotEmpty(updateList)) {
					customerService.save(updateList);
				}
	}

}
