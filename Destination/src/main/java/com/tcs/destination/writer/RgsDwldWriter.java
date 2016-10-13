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
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;
/**
 * Writes the RGS Data to the sheet
 * @author TCS
 *
 */
public class RgsDwldWriter implements ItemWriter<DeliveryRequirementT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(RgsDwldWriter.class);

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

	private CellStyle cellDateStyle;

	@Override
	public void beforeStep(StepExecution stepExecution) {

		logger.debug("Inside before step:");

		try {
			this.stepExecution = stepExecution;

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			String environmentName = PropertyUtil
					.getProperty("environment.name");
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
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
					.append(entity).append(DOWNLOADCONSTANT)
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

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			request.setStatus(RequestStatus.PROCESSED.getStatus());
			dataProcessingRequestRepository.save(request);

			jobContext.remove(REQUEST);
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}

		return stepExecution.getExitStatus();

	}

	@Override
	public void write(List<? extends DeliveryRequirementT> items)
			throws Exception {

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

			sheet = workbook.getSheet(Constants.RGS_DETAILS_SHEET_NAME);
			cellDateStyle = DateUtils.getCellStyleDate(sheet, Constants.DATE_FORMAT);

		}

		if (items != null) {
			for (DeliveryRequirementT deliveryRequirement : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);
				// Rgs Id
				Cell cellRgsId = row.createCell(0);
				cellRgsId.setCellValue(deliveryRequirement.getDeliveryRgsId());
				// Requirement Id
				Cell cellReqId = row.createCell(1);
				cellReqId.setCellValue(deliveryRequirement.getRequirementId());
				// Cell Role
				Cell cellRole = row.createCell(2);
				cellRole.setCellValue(deliveryRequirement.getRole());
				// Competency Area
				Cell cellCompetencyArea = row.createCell(3);
				cellCompetencyArea.setCellValue(deliveryRequirement
						.getCompetencyArea());
				// Competency Sub Area
				Cell cellCompetencySubArea = row.createCell(4);
				cellCompetencySubArea.setCellValue(deliveryRequirement
						.getSubCompetencyArea());
				// Experience
				Cell cellExperience = row.createCell(5);
				cellExperience
						.setCellValue(deliveryRequirement.getExperience());
				// Project Site
				Cell cellProjectSite = row.createCell(6);
				cellProjectSite.setCellValue(deliveryRequirement.getLocation());
				// Status
				Cell cellStatus = row.createCell(7);
				cellStatus.setCellValue(deliveryRequirement.getStatus());
				// Customer Name
				Cell cellCustomerName = row.createCell(8);
				cellCustomerName.setCellValue(deliveryRequirement
						.getCustomerName());
				// Branch
				Cell cellBranchCentre = row.createCell(9);
				cellBranchCentre.setCellValue(deliveryRequirement.getBranch());
				// IOU
				Cell cellIOUName = row.createCell(10);
				cellIOUName.setCellValue(deliveryRequirement.getIouName());
				// Employee Name
				Cell cellEmployeeName = row.createCell(11);
				cellEmployeeName.setCellValue(deliveryRequirement
						.getEmployeeName());
				// Employee Number
				Cell cellEmployeeNumber = row.createCell(12);
				cellEmployeeNumber.setCellValue(deliveryRequirement
						.getEmployeeId());
				// Fulfillment Date
				if (deliveryRequirement.getFulfillmentDate() != null) {
					Cell cellFulfillmentDate = row.createCell(13);
					cellFulfillmentDate.setCellValue(deliveryRequirement
							.getFulfillmentDate());
					cellFulfillmentDate.setCellStyle(cellDateStyle);
				}

				// Requirement Start Date
				if (deliveryRequirement.getRequirementStartDate() != null) {
					Cell cellRequirementStartDate = row.createCell(14);
					cellRequirementStartDate.setCellValue(deliveryRequirement
							.getRequirementStartDate());
					cellRequirementStartDate.setCellStyle(cellDateStyle);
				}
				// Requirement End Date

				if (deliveryRequirement.getRequirementEndDate() != null) {
					Cell cellRequirementEndDate = row.createCell(15);
					cellRequirementEndDate.setCellValue(deliveryRequirement
							.getRequirementEndDate());
					cellRequirementEndDate.setCellStyle(cellDateStyle);
				}
				// Increment row counter
				rowCount++;
			}
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

	public Workbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public FileInputStream getFileInputStream() {
		return fileInputStream;
	}

	public void setFileInputStream(FileInputStream fileInputStream) {
		this.fileInputStream = fileInputStream;
	}

}
