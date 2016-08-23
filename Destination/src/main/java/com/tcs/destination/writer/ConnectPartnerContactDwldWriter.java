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
import org.springframework.http.HttpStatus;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;



public class ConnectPartnerContactDwldWriter implements ItemWriter<ContactT>,
StepExecutionListener {


	private static final Logger logger = LoggerFactory
			.getLogger(ConnectPartnerContactDwldWriter.class);

	private StepExecution stepExecution;

	private Sheet sheet;

	private Workbook workbook;

	private int rowCount = 1;

	private String filePath;

	private FileInputStream fileInputStream;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {

		try {
			logger.info("Start:inside after step() PartnerContactDwldWriter:");
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			logger.info("End:inside after step() PartnerContactDwldWriter:");
			outputStream.close(); // close the stream
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


	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends ContactT> items) throws Exception {
		logger.info("Begin: Inside write method  of PartnerContactDwldWriter:");

		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
			filePath = request.getFilePath() + request.getFileName();
			fileInputStream = new FileInputStream(new File(filePath));
			String fileName  = request.getFileName();

			String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
			if(fileExtension.equalsIgnoreCase("xls")){
				workbook = new HSSFWorkbook(fileInputStream);
			} else if(fileExtension.equalsIgnoreCase("xlsx")){
				workbook = new XSSFWorkbook(fileInputStream);
			} else if(fileExtension.equalsIgnoreCase("xlsm")){
				workbook = new XSSFWorkbook(fileInputStream);
			}

			sheet = workbook.getSheet(Constants.OPPORTUNITY_TEMPLATE_PARTNER_CONTACT_SHEET_NAME);
		}

		if(items!=null) {
			int rowCountPartnerSheet = 1; // Excluding the header, header starts with index 0
			for (ContactT ct : items) {

				if ((ct.getContactCategory().equals(EntityType.PARTNER.toString()) && 
						(ct.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For Partner Contact

					// Create row with rowCount
					Row row = sheet.createRow(rowCountPartnerSheet);

					// Create new Cell and set cell value
					Cell cellPartnerName = row.createCell(0);
					try {
						cellPartnerName.setCellValue(ct.getPartnerContactLinkTs().get(0).getPartnerMasterT().getPartnerName().trim());

					} catch(NullPointerException npe){
						throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Partner Contact cannot exist without Partner");
					}

					Cell cellPartnerContactName = row.createCell(1);
					cellPartnerContactName.setCellValue(ct.getContactName());

					Cell cellPartnerContactRole = row.createCell(2);
					cellPartnerContactRole.setCellValue(ct.getContactRole());

					Cell cellPartnerContactEmailId = row.createCell(3);
					if(ct.getContactEmailId()!=null) {
						cellPartnerContactEmailId.setCellValue(ct.getContactEmailId());
					}
					// Increment row counter for partner contact sheet
					rowCountPartnerSheet++;
				}
				else{
					logger.info("partner conatcts doesnot exists for this user");
				}
			}
		}
		logger.info("Exit: Inside write method  of PartnerContactDwldWriter:");
	}

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
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
