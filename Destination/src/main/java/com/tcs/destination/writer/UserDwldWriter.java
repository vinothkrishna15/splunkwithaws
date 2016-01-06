package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.DOWNLOAD;
import static com.tcs.destination.utils.Constants.DOWNLOADCONSTANT;
import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;
import static com.tcs.destination.utils.Constants.XLSM;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;

import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;

public class UserDwldWriter implements ItemWriter<String[]>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(UserDwldWriter.class);

	private StepExecution stepExecution;

	private String template;

	private String fileServerPath;

	private DataProcessingRequestRepository dataProcessingRequestRepository;

	private DataProcessingService dataProcessingService;

	private Sheet sheet;

	private Workbook workbook;

	private int rowCount = 1;

	private String filePath;

	private FileInputStream fileInputStream;

	Map<String, UserT> userIdUserMap;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			jobContext.put("userDetails", userIdUserMap);

			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}

		return stepExecution.getExitStatus();

	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		userIdUserMap = new HashMap<String, UserT>();
		logger.debug("Inside before step:");

		try {
			this.stepExecution = stepExecution;

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			String environmentName=PropertyUtil.getProperty("environment.name");
			String entity = dataProcessingService.getEntity(request
					.getRequestType());
			StringBuffer filePath = new StringBuffer(fileServerPath)
					.append(entity).append(FILE_DIR_SEPERATOR).append(DOWNLOAD)
					.append(FILE_DIR_SEPERATOR)
					.append(DateUtils.getCurrentDate())
					.append(FILE_DIR_SEPERATOR)
					.append(request.getUserT().getUserId())
					.append(FILE_DIR_SEPERATOR);
			StringBuffer fileName = new StringBuffer(environmentName)
			.append(entity)
			.append(DOWNLOADCONSTANT)
			.append(DateUtils.getCurrentDateForFile()).append(XLSM);
			
			FileManager.copyFile(filePath.toString(), template,
					fileName.toString());
            request.setFilePath(filePath.toString());
			request.setFileName(fileName.toString());
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);

			jobContext.put(REQUEST, request);

		} catch (Exception e) {
			logger.error("Error in before step process: {}", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends String[]> items) throws Exception {

		logger.debug("Inside write method:");

		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			filePath = request.getFilePath() + request.getFileName();
			fileInputStream = new FileInputStream(new File(
					request.getFilePath() + request.getFileName()));
			String fileName = request.getFileName();

			String fileExtension = fileName.substring(
					fileName.lastIndexOf(".") + 1, fileName.length());
			if (fileExtension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(fileInputStream);
			} else if (fileExtension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(fileInputStream);
			} else if (fileExtension.equalsIgnoreCase("xlsm")) {
				workbook = new XSSFWorkbook(fileInputStream);
			}

			sheet = workbook.getSheet(Constants.USER_TEMPLATE_USER_MASTER);
		}

		if (items != null) {

			for (Object[] userRecord : items) {
				Row row = sheet.createRow(rowCount);

				String userId = (String) userRecord[0];
				ExcelUtils.createCell(userId, row, 1);

				String userName = (String) userRecord[1];
				ExcelUtils.createCell(userName, row, 2);

				String userPassword = (String) userRecord[2];
				ExcelUtils.createCell(userPassword, row, 3);

				String userGroup = (String) userRecord[3];
				ExcelUtils.createCell(userGroup, row, 4);

				String userRole = (String) userRecord[4];
				ExcelUtils.createCell(userRole, row, 5);

				String userLocation = (String) userRecord[5];
				ExcelUtils.createCell(userLocation, row, 6);

				String timeZone = (String) userRecord[6];
				ExcelUtils.createCell(timeZone, row, 7);

				String telephone = (String) userRecord[7];
				ExcelUtils.createCell(telephone, row, 8);

				String emailId = (String) userRecord[8];
				ExcelUtils.createCell(emailId, row, 9);

				String supervisorId = (String) userRecord[9];
				ExcelUtils.createCell(supervisorId, row, 10);

				String supervisorName = (String) userRecord[10];
				ExcelUtils.createCell(supervisorName, row, 11);
				UserT user = new UserT();
				user.setUserId(userId);
				user.setUserName(userName);
				user.setUserGroup(userGroup);
				userIdUserMap.put(userId, user);
				rowCount++;
			}

			// for (CustomerMasterT cmt : items) {
			// // Create row with rowCount
			// Row row = sheet.createRow(rowCount);
			//
			// // Create new Cell and set cell value
			// Cell cellGrpClient = row.createCell(1);
			// cellGrpClient.setCellValue(cmt.getGroupCustomerName().trim());
			//
			// Cell cellCustName = row.createCell(2);
			// cellCustName.setCellValue(cmt.getCustomerName().trim());
			//
			// Cell cellIou = row.createCell(3);
			// cellIou.setCellValue(cmt.getIouCustomerMappingT().getIou().trim());
			//
			// Cell cellGeo = row.createCell(4);
			// cellGeo.setCellValue(cmt.getGeographyMappingT().getGeography()
			// .trim());
			//
			// // Increment row counter
			// rowCount++;
			// }
		}
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
	}

	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
	}

	public DataProcessingService getDataProcessingService() {
		return dataProcessingService;
	}

	public void setDataProcessingService(
			DataProcessingService dataProcessingService) {
		this.dataProcessingService = dataProcessingService;
	}

	public Sheet getSheet() {
		return sheet;
	}

	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

}
