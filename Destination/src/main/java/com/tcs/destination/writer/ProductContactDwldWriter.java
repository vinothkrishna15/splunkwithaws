package com.tcs.destination.writer;

import static com.tcs.destination.utils.Constants.DOWNLOAD;
import static com.tcs.destination.utils.Constants.DOWNLOADCONSTANT;
import static com.tcs.destination.utils.Constants.FILE_DIR_SEPERATOR;
import static com.tcs.destination.utils.Constants.REQUEST;
import static com.tcs.destination.utils.Constants.XLS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.PartnerContactLinkT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.ProductRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;
/**
 * This ProductsDwldWriter class contains the functionality to write Product 
 * data into the workbook
 * 
 */
public class ProductContactDwldWriter implements ItemWriter<ContactT>,
		StepExecutionListener {
	
	private static final Logger logger = LoggerFactory.getLogger(ProductContactDwldWriter.class);

	private int rowCount=1;
	private String template;
	private DataProcessingRequestRepository dataProcessingRequestRepository;
	private DataProcessingService dataProcessingService;
	private Sheet sheet;
	private Workbook workbook;
	private StepExecution stepExecution;
	private String fileServerPath;
	private String filePath;
	private FileInputStream fileInputStream;
    private ProductRepository productRepository;
    private ContactRepository contactRepository;
    
    private Map<String,List<ProductContactLinkT>> productMap;
   
	
	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
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

	public void setDataProcessingService(DataProcessingService dataProcessingService) {
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

	public StepExecution getStepExecution() {
		return stepExecution;
	}

	public void setStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public String getFileServerPath() {
		return fileServerPath;
	}

	public void setFileServerPath(String fileServerPath) {
		this.fileServerPath = fileServerPath;
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
	
	 public ContactRepository getContactRepository() {
			return contactRepository;
		}

		public void setContactRepository(ContactRepository contactRepository) {
			this.contactRepository = contactRepository;
		}
		
		 public ProductRepository getProductRepository() {
				return productRepository;
			}

			public void setProductRepository(ProductRepository productRepository) {
				this.productRepository = productRepository;
			}

		

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
try {
			
			fileInputStream.close();
			FileOutputStream outputStream = new FileOutputStream(new File(
					filePath));
			workbook.write(outputStream); // write changes
			outputStream.close(); // close the stream
			
			ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			 jobContext.remove("productMap");
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}

		return stepExecution.getExitStatus();
	
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		logger.debug("Inside before method of products download writer");

		try {
			this.stepExecution = stepExecution;

			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);
			
			productMap = (Map<String,List<ProductContactLinkT>>) jobContext.get("productMap");
			String entity = dataProcessingService.getEntity(request
					.getRequestType());
			String environmentName=PropertyUtil.getProperty("environment.name");
			
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
			.append(DateUtils.getCurrentDateForFile()).append(XLS);
		
			FileManager.copyFile(filePath.toString(), template,
					fileName.toString());

			request.setFilePath(filePath.toString());
			request.setFileName(fileName.toString());
			request.setStatus(RequestStatus.INPROGRESS.getStatus());
			dataProcessingRequestRepository.save(request);

			jobContext.put(REQUEST, request);
			fileInputStream = new FileInputStream(new File(
					request.getFilePath() + request.getFileName()));
			
			String fileExtension = fileName.substring(
					fileName.lastIndexOf(".") + 1, fileName.length());
			if (fileExtension.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(fileInputStream);
			} else if (fileExtension.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(fileInputStream);
			} else if (fileExtension.equalsIgnoreCase("xlsm")) {
				workbook = new XSSFWorkbook(fileInputStream);
			}

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
	public void write(List<? extends ContactT> items) throws Exception {
		logger.debug("Inside write method of products download writer");

		if (rowCount == 1) {
			ExecutionContext jobContext = stepExecution.getJobExecution()
					.getExecutionContext();
			DataProcessingRequestT request = (DataProcessingRequestT) jobContext
					.get(REQUEST);

			filePath = request.getFilePath() + request.getFileName();
		
			
			sheet = workbook.getSheet(Constants.PRODUCT_CONTACT_SHEET);
		}

		if (items != null) {
			for (ContactT contactT : items) {
				
				if ((contactT.getContactCategory().equals(EntityType.PRODUCT.toString()) && 
						(contactT.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For Product Contact
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);
				
				// Create new Cell and set cell value
				//Contact Id
				Cell cellContactId = row.createCell(1);
				cellContactId.setCellValue(contactT.getContactId());
				
				//Product Name
				String productName = getProductName(contactT.getContactId());
				Cell cellProductName = row.createCell(2);
				cellProductName.setCellValue(productName);
				
				Cell cellContactName = row.createCell(3);
				if(contactT.getContactName()!=null)
				cellContactName.setCellValue(contactT.getContactName());
				
				Cell cellContactRole = row.createCell(4);
				if(contactT.getContactRole()!=null)
				{
					if(!contactT.getContactRole().equalsIgnoreCase("Other"))
					{
				     cellContactRole.setCellValue(contactT.getContactRole());
					}
					else
					{
					 cellContactRole.setCellValue(contactT.getOtherRole());

					}
				}
				
				Cell cellContactEmailId = row.createCell(5);
				if(contactT.getContactEmailId()!=null)
				cellContactEmailId.setCellValue(contactT.getContactEmailId());
				
				Cell cellContactTelephone = row.createCell(6);
				if(contactT.getContactTelephone()!=null)
				cellContactTelephone.setCellValue(contactT.getContactTelephone());
				
				Cell cellContactLinkedIn = row.createCell(7);
				if(contactT.getContactLinkedinProfile()!=null)
				cellContactLinkedIn.setCellValue(contactT.getContactLinkedinProfile());
				
				Cell cellContactActive = row.createCell(8);
				cellContactActive.setCellValue(contactT.isActive());
			

			// Increment row counter for products sheet
			rowCount++;

			}
			}
		}
	}
	
	private String getProductName(String contactId) {
		StringBuffer productBuffer = new StringBuffer("");
		List<ProductContactLinkT> productContactList = productMap.get(contactId);	
		if(productContactList!=null){
			for(ProductContactLinkT productContactLinkT: productContactList){
				if(StringUtils.isEmpty(productBuffer.toString())){
					productBuffer.append(productContactLinkT.getProductMasterT().getProductName());
				} else {
					productBuffer.append("," + productContactLinkT.getProductMasterT().getProductName());
				}
			}
		}
		return productBuffer.toString();
	}
		
	}


