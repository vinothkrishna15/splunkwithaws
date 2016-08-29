package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.REQUEST;

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
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.utils.Constants;

/*
 * This class deals with writing the Partner Master table data 
 * from database into an excel 
 * using batch processing
 */
public class PartnerMasterRefDwldWriter implements ItemWriter<PartnerMasterT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerDwldWriter.class);

	private StepExecution stepExecution;

	private Sheet sheet;

	private Workbook workbook;

	private int rowCount = 1;

	private String filePath;

	private FileInputStream fileInputStream;

	private DataProcessingRequestRepository dataProcessingRequestRepository;
	
	private PartnerRepository partnerRepository;

	

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.info("Start:Inside afterstep() of PartnerMasterRefDwldWriter:");
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
			logger.info("End:Inside afterstep() of PartnerMasterRefDwldWriter:");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Error in after step process: {}", e);
		}

		return stepExecution.getExitStatus();

	}

	@Override
	public void beforeStep(StepExecution stepExecution) {

		logger.info("start:Inside beforeStep() of PartnerMasterRefDwldWriter:");

		try {
			this.stepExecution = stepExecution;
			logger.info("End:Inside beforeStep() of PartnerMasterRefDwldWriter:");

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
	public void write(List<? extends PartnerMasterT> items) throws Exception {

		logger.info("start:Inside write() of PartnerMasterRefDwldWriter:");

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

			sheet = workbook.getSheet(Constants.PARTNER_MASTER_REF_PARTNER_SHEET_NAME);
		}
		if (items != null) {
			for (PartnerMasterT partnerMaster : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellPartnerId = row.createCell(1);
				cellPartnerId.setCellValue(partnerMaster.getPartnerId().trim());

				Cell cellPartnerName = row.createCell(2);
				cellPartnerName.setCellValue(partnerMaster.getPartnerName().trim());
 
				Cell cellGroupPartnerName = row.createCell(3);
				if(partnerMaster.getGroupPartnerName()!=null)
				{
				cellGroupPartnerName.setCellValue(partnerMaster.getGroupPartnerName().trim());
				}
				
				Cell cellGeography = row.createCell(4);
				cellGeography.setCellValue(partnerMaster.getGeography().trim());
				
				Cell cellCountry = row.createCell(5);
				if(partnerMaster.getCountry()!=null)
				{
				cellCountry.setCellValue(partnerMaster.getCountry().trim());
				}
				
				Cell cellCity= row.createCell(6);
				if(partnerMaster.getCity()!=null)
				{
				cellCity.setCellValue(partnerMaster.getCity().trim());
				}
				
				Cell cellHqPartnerLinkName = row.createCell(7);
				if(partnerMaster.getHqPartnerLinkId()!=null)
				{
				PartnerMasterT partner=partnerRepository.findByPartnerId(partnerMaster.getHqPartnerLinkId());
				 if(partner!=null)
				 {
				  cellHqPartnerLinkName.setCellValue(partner.getPartnerName().trim());
				 }
				}
				
				Cell cellCorporateHqAddress = row.createCell(8);
				if(partnerMaster.getCorporateHqAddress()!=null)
				{
					cellCorporateHqAddress.setCellValue(partnerMaster.getCorporateHqAddress().trim());
				}
				
				Cell cellWebsite = row.createCell(9);
				if(partnerMaster.getWebsite()!=null)
				{
					cellWebsite.setCellValue(partnerMaster.getWebsite().trim());
				}
				
				Cell cellFacebook = row.createCell(10);
				if(partnerMaster.getFacebook()!=null)
				{
					cellFacebook.setCellValue(partnerMaster.getFacebook().trim());
				}
				
				Cell cellText1 = row.createCell(11);
				if(partnerMaster.getText1()!=null)
				{
					cellText1.setCellValue(partnerMaster.getText1().trim());
				}
				
				Cell cellText2 = row.createCell(12);
				if(partnerMaster.getText2()!=null)
				{
					cellText2.setCellValue(partnerMaster.getText2().trim());
				}
				
				Cell cellText3 = row.createCell(13);
				if(partnerMaster.getText3()!=null)
				{
					cellText3.setCellValue(partnerMaster.getText3().trim());
				}
				
				Cell cellNotes = row.createCell(14);
				if(partnerMaster.getNotes()!=null)
				{
					cellNotes.setCellValue(partnerMaster.getNotes().trim());
				}
				
				Cell active = row.createCell(15);
				active.setCellValue(partnerMaster.isActive());//TODO inactive indicator - adding extra column for active -done
				
				// Increment row counter
				rowCount++;
			}
		}
		logger.info("Exit: Inside write() of PartnerMasterRefDwldWriter:");
	}
	
	public PartnerRepository getPartnerRepository() {
		return partnerRepository;
	}

	public void setPartnerRepository(PartnerRepository partnerRepository) {
		this.partnerRepository = partnerRepository;
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}


	public DataProcessingRequestRepository getDataProcessingRequestRepository() {
		return dataProcessingRequestRepository;
	}

	public void setDataProcessingRequestRepository(
			DataProcessingRequestRepository dataProcessingRequestRepository) {
		this.dataProcessingRequestRepository = dataProcessingRequestRepository;
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
