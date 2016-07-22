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
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.PartnerSubSpMappingT;
import com.tcs.destination.bean.PartnerSubspProductMappingT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;

public class PartnerSubSpProductDwldWriter implements ItemWriter<PartnerSubspProductMappingT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerSubSpProductDwldWriter.class);

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
	
	private SubSpRepository subSpRepository;
	
	private PartnerSubSpMappingTRepository partnerSubSpMappingTRepository;
	
	private ProductRepository productRepository;

   

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {
		    if(fileInputStream!=null)
		    {
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream
		    }

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
	public void beforeStep(StepExecution stepExecution) {

		try {
			this.stepExecution = stepExecution;
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
	public void write(List<? extends PartnerSubspProductMappingT> items) throws Exception {

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

			sheet = workbook.getSheet(Constants.PARTNER_SUBSP_PRODUCT_TEMPLATE_SHEET_NAME);
		}

		if (items != null) {
			for (PartnerSubspProductMappingT partnerSubspProductMappingT : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellPartnerSubSpProductId = row.createCell(1);
				cellPartnerSubSpProductId.setCellValue(partnerSubspProductMappingT.getPartnerSubspProductMappingId().trim());

				Cell cellPartnerName = row.createCell(2);
				cellPartnerName.setCellValue(partnerSubspProductMappingT.getPartnerSubSpMappingT().getPartnerMasterT().getPartnerName().trim());

				Cell cellSubSp = row.createCell(3);
				String subSpMappingId=partnerSubspProductMappingT.getPartnerSubspMappingId();
				PartnerSubSpMappingT partnerSubSpMappingT=partnerSubSpMappingTRepository.findByPartnerSubspMappingId(subSpMappingId);
				SubSpMappingT  subSpMappingT=subSpRepository.findBySubSpId(partnerSubSpMappingT.getSubSpId());
				cellSubSp.setCellValue(subSpMappingT.getSubSp());
				
				Cell cellProduct = row.createCell(4);
                ProductMasterT productMasterT=productRepository.findByProductId(partnerSubspProductMappingT.getProductId());
                cellProduct.setCellValue(productMasterT.getProductName());
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
	

	public SubSpRepository getSubSpRepository() {
		return subSpRepository;
	}

	public void setSubSpRepository(SubSpRepository subSpRepository) {
		this.subSpRepository = subSpRepository;
	}
	
	public PartnerSubSpMappingTRepository getPartnerSubSpMappingTRepository() {
		return partnerSubSpMappingTRepository;
	}

	public void setPartnerSubSpMappingTRepository(
			PartnerSubSpMappingTRepository partnerSubSpMappingTRepository) {
		this.partnerSubSpMappingTRepository = partnerSubSpMappingTRepository;
	}
	
	public ProductRepository getProductRepository() {
			return productRepository;
	}

    public void setProductRepository(ProductRepository productRepository) {
			this.productRepository = productRepository;
	}

}
