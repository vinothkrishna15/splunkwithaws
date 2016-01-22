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
import java.sql.Timestamp;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.DataProcessingRequestT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.DataProcessingRequestRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.RequestStatus;
import com.tcs.destination.service.DataProcessingService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FileManager;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;

public class ConnectDwldWriter implements ItemWriter<ConnectT>,
		StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectDwldWriter.class);

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
	
	@Autowired
	UserRepository userRepository;
	
	
	Map<String,String> customerIdCustomerMap;
	Map<String,String> partnerIdPartnerMap;
	Map<String,List<ConnectSubSpLinkT>> connectSubSpMap;
	Map<String,List<ConnectOfferingLinkT>> connectOfferingMap;
	Map<String,List<ConnectSecondaryOwnerLinkT>> connectSecondaryOwnerMap;
	Map<String,List<ConnectTcsAccountContactLinkT>> connectTcsAccountContactMap;
	Map<String,List<ConnectCustomerContactLinkT>> connectCustomerContactMap;
	Map<String,List<NotesT>> connectNotesMap;
	Map<String,UserT> userIdUserMap;
	Map<String,ContactT> contactIdContactMap;

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		logger.info("Connect Dwld : Inside afterStep method:");
         try {
        	 fileInputStream.close();
        	 FileOutputStream outputStream = new FileOutputStream(new File(filePath));
             workbook.write(outputStream); //write changes
			 outputStream.close();  //close the stream
			 ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
			 jobContext.remove("connectSubSpMap");
			 jobContext.remove("connectOfferingMap");
			 jobContext.remove("connectSecondaryOwnerMap");
			 jobContext.remove("connectTcsAccountContactMap");
			 jobContext.remove("connectCustomerContactMap");
			 jobContext.remove("connectNotesMap");
			 jobContext.remove("contactIdContactMap");
		} catch (IOException e) {
			logger.error("Error in after step process: {}", e);
		}
		
		return stepExecution.getExitStatus();
	}

	@Override
	public void beforeStep(StepExecution stepExecution) {
		
		logger.info("Connect Dwld : Inside beforeStep method:");
		
		try {
			    this.stepExecution = stepExecution;
			    
				ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
				customerIdCustomerMap = (Map<String, String>) jobContext.get("customerIdCustomerMap");
				partnerIdPartnerMap = (Map<String, String>) jobContext.get("partnerIdPartnerMap");
				connectSubSpMap = (Map<String, List<ConnectSubSpLinkT>>) jobContext.get("connectSubSpMap");
				connectOfferingMap = (Map<String, List<ConnectOfferingLinkT>>) jobContext.get("connectOfferingMap");
				connectSecondaryOwnerMap = (Map<String, List<ConnectSecondaryOwnerLinkT>>) jobContext.get("connectSecondaryOwnerMap");
				connectTcsAccountContactMap = (Map<String, List<ConnectTcsAccountContactLinkT>>) jobContext.get("connectTcsAccountContactMap");
				connectCustomerContactMap = (Map<String, List<ConnectCustomerContactLinkT>>) jobContext.get("connectCustomerContactMap");
				connectNotesMap = (Map<String, List<NotesT>>) jobContext.get("connectNotesMap");
				userIdUserMap = (Map<String, UserT>) jobContext.get("userIdUserMap");
				contactIdContactMap = (Map<String, ContactT>) jobContext.get("contactIdContactMap");
				
				DataProcessingRequestT request = (DataProcessingRequestT) jobContext.get(REQUEST);
				
//				String path = fileServerPath + dataProcessingService.getEntityName(request.getRequestType()) + FILE_DIR_SEPERATOR + DateUtils.getCurrentDate() + FILE_DIR_SEPERATOR + request.getUserT().getUserId() + FILE_DIR_SEPERATOR;
//				String fileName  = FileManager.copyFile(path, template);
//				filePath.toString(), template, fileName.toString()
//				filePath =  path + fileName;
//				
//				request.setFilePath(path);
//				request.setFileName(fileName);
//				request.setStatus(RequestStatus.INPROGRESS.getStatus());
//				String entity = dataProcessingService.getEntityName(request.getRequestType());
//				
//				StringBuffer filePath = new StringBuffer(fileServerPath).append(entity).append(FILE_DIR_SEPERATOR)
//						.append(DateUtils.getCurrentDate()).append(FILE_DIR_SEPERATOR).append(request.getUserT().getUserId()).append(FILE_DIR_SEPERATOR);
//				StringBuffer fileName = new StringBuffer(entity).append(DOWNLOADCONSTANT).append(DateUtils.getCurrentDateForFile());
//				FileManager.copyFile(filePath.toString(), template, fileName.toString());
//				
//				request.setFilePath(filePath.toString());
//				request.setFileName(fileName.toString());
//				request.setStatus(RequestStatus.INPROGRESS.getStatus());
				
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
				.append(DateUtils.getCurrentDateForFile()).append(XLSM);
				
				FileManager.copyFile(filePath.toString(), template,
						fileName.toString());

				request.setFilePath(filePath.toString());
				request.setFileName(fileName.toString());
				request.setStatus(RequestStatus.INPROGRESS.getStatus());
				
				
				dataProcessingRequestRepository.save(request);
				
				jobContext.put(REQUEST,request);
				
			} catch (Exception e) {
				logger.error("Error in before step process: {}", e);
			}

	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.item.ItemWriter#write(java.util.List)
	 */
	@Override
	public void write(List<? extends ConnectT> items) throws Exception {

		logger.info("Connect Dwld : Inside write method:");
		
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
	            
			sheet = workbook.getSheet(Constants.CONNECT_TEMPLATE_CONNECT_SHEET_NAME);
			workbook.setActiveSheet(2);
		}
		
		
		if(items!=null) {
			for (ConnectT connect : items) {
				// Create row with rowCount
				Row row = sheet.createRow(rowCount);

				String connectId = connect.getConnectId();
				ExcelUtils.createCell(connectId, row, 1);
				
				String connectFor = connect.getConnectCategory();
				ExcelUtils.createCell(connectFor, row, 2);
				
				String customerPartnerName = getCategoryName(connect);
				ExcelUtils.createCell(customerPartnerName, row, 3);
				
				String country = connect.getCountry();
				ExcelUtils.createCell(country, row, 4);
				
				String connectName = connect.getConnectName();
				ExcelUtils.createCell(connectName, row, 5);

				String subSpStr = getSubSpStr(connect.getConnectId());
				ExcelUtils.createCell(subSpStr, row, 6);
				
				String offeringStr = getOfferingStr(connect.getConnectId());
				ExcelUtils.createCell(offeringStr, row, 7);
				
				Timestamp startDateTimeStamp = connect.getStartDatetimeOfConnect();
				java.util.Date startDate = DateUtils.toDate(startDateTimeStamp);
				String dateOfConnect = DateUtils.convertDateToString(startDate);
				ExcelUtils.createCell(dateOfConnect, row, 8);
				
				String startTime = DateUtils.convertDateToHourMinute(startDate);
				ExcelUtils.createCell(startTime, row, 9);
				
				Timestamp endDateTimeStamp = connect.getEndDatetimeOfConnect();
				java.util.Date endDate = DateUtils.toDate(endDateTimeStamp);
				String endTime = DateUtils.convertDateToHourMinute(endDate);
				ExcelUtils.createCell(endTime, row, 10);
				
				String timeZone = connect.getTimeZone();
				ExcelUtils.createCell(timeZone, row, 11);
				
				String location = connect.getLocation();
				ExcelUtils.createCell(location, row, 12);
				
				String connectType = connect.getType();
				ExcelUtils.createCell(connectType, row, 13);
				
				String connectOwner = connect.getPrimaryOwner();
				UserT primaryOwner = userIdUserMap.get(connectOwner);
				ExcelUtils.createCell(primaryOwner.getUserName(), row, 14);
				
				String secondaryOwnerStr = getSecondaryOwnerStr(connect.getConnectId());
				ExcelUtils.createCell(secondaryOwnerStr, row, 15);
				
				String tcsAccountContactStr = getTcsAccountContactStr(connect.getConnectId());
				ExcelUtils.createCell(tcsAccountContactStr, row, 16);
				
				String customerContactStr = getCustomerContactStr(connect.getConnectId());
				ExcelUtils.createCell(customerContactStr, row, 17);
				
				String notesStr = getNotesStr(connect.getConnectId());
				ExcelUtils.createCell(notesStr, row, 18);
				
				Timestamp createdDate =connect.getCreatedDatetime();
				java.util.Date createdDt = DateUtils.toDate(createdDate);
				String dateCreated = DateUtils.convertDateToString(createdDt);
				ExcelUtils.createCell(dateCreated, row, 19);
				
				String createdBy = connect.getCreatedByUser().getUserName();
				ExcelUtils.createCell(createdBy, row, 20);
				
				Timestamp modifiedDate = connect.getModifiedDatetime();
				java.util.Date modifiedDt = DateUtils.toDate(modifiedDate);
				String dateModified = DateUtils.convertDateToString(modifiedDt);
				ExcelUtils.createCell(dateModified, row, 21);
				
				String modifiedBy = connect.getModifiedByUser().getUserName();
				ExcelUtils.createCell(modifiedBy, row, 22);
				
				
				
				// Increment row counter
				rowCount++;
			}
		} 
	}
	
	private String getNotesStr(String connectId) {
		StringBuffer notesBuffer = new StringBuffer("");
		List<NotesT> connectNotesList = connectNotesMap.get(connectId);
		if(connectNotesList!=null){
			for(NotesT note: connectNotesList){
				if(StringUtils.isEmpty(notesBuffer.toString())){
					notesBuffer.append(note.getNotesUpdated());
				} else {
					notesBuffer.append("," + note.getNotesUpdated());
				}
			}
		}
		
		return notesBuffer.toString();
	}
	

    private String getCustomerContactStr(
			String connectId) {
		StringBuffer customerContactBuffer = new StringBuffer("");
		List<ConnectCustomerContactLinkT> connectCustomerContactList = connectCustomerContactMap.get(connectId);
		if(connectCustomerContactList!=null){
			for(ConnectCustomerContactLinkT connectCustomerContactLinkT: connectCustomerContactList){
				ContactT contact = contactIdContactMap.get(connectCustomerContactLinkT.getContactId());
				if(StringUtils.isEmpty(customerContactBuffer.toString())){
					customerContactBuffer.append(contact.getContactName());
				} else {
					customerContactBuffer.append("," + contact.getContactName());
				}
			}
		}
		return customerContactBuffer.toString();
	}

	private String getTcsAccountContactStr(
			String connectId) {
		StringBuffer tcsAccountContactBuffer = new StringBuffer("");
		List<ConnectTcsAccountContactLinkT> connectTcsAccountContactList = connectTcsAccountContactMap.get(connectId);
		if(connectTcsAccountContactList!=null){
			for(ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT: connectTcsAccountContactList){
				ContactT contact = contactIdContactMap.get(connectTcsAccountContactLinkT.getContactId());
				if(StringUtils.isEmpty(tcsAccountContactBuffer.toString())){
					tcsAccountContactBuffer.append(contact.getContactName());
				} else {
					tcsAccountContactBuffer.append("," + contact.getContactName());
				}
			}
		}
		return tcsAccountContactBuffer.toString();
	}

	private String getSecondaryOwnerStr(
			String connectId) {
		StringBuffer secondaryOwnerBuffer = new StringBuffer("");
		List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerList = connectSecondaryOwnerMap.get(connectId);
		if(connectSecondaryOwnerList!=null){
			for(ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT: connectSecondaryOwnerList){
				UserT secondaryOwner = userIdUserMap.get(connectSecondaryOwnerLinkT.getSecondaryOwner());
				if(StringUtils.isEmpty(secondaryOwnerBuffer.toString())){
					secondaryOwnerBuffer.append(secondaryOwner.getUserName());
				} else {
					secondaryOwnerBuffer.append("," + secondaryOwner.getUserName());
				}
			}
		}
		return secondaryOwnerBuffer.toString();
	}

	private String getOfferingStr(String connectId) {
		StringBuffer offeringBuffer = new StringBuffer("");
		List<ConnectOfferingLinkT> connectOfferingList = connectOfferingMap.get(connectId);	
		if(connectOfferingList!=null){
			for(ConnectOfferingLinkT connectOfferingLinkT: connectOfferingList){
				if(StringUtils.isEmpty(offeringBuffer.toString())){
					offeringBuffer.append(connectOfferingLinkT.getOffering());
				} else {
					offeringBuffer.append("," + connectOfferingLinkT.getOffering());
				}
			}
		}
		return offeringBuffer.toString();
	}

	private String getSubSpStr(String connectId) {
		StringBuffer subspBuffer = new StringBuffer("");
		List<ConnectSubSpLinkT> connectSubSpList = connectSubSpMap.get(connectId);	
		if(connectSubSpList!=null){
			for(ConnectSubSpLinkT connectSubSpLinkT: connectSubSpList){
				if(StringUtils.isEmpty(subspBuffer.toString())){
					subspBuffer.append(connectSubSpLinkT.getSubSp());
				} else {
					subspBuffer.append("," + connectSubSpLinkT.getSubSp());
				}
			}
		}
		return subspBuffer.toString();
	}

	private String getCategoryName(ConnectT connect) {
		String categoryName = "";
		String connectId = connect.getConnectId();
		String category = connect.getConnectCategory();
		if(category.equalsIgnoreCase(EntityType.CUSTOMER.toString())){
			categoryName = customerIdCustomerMap.get(connect.getCustomerId());
		} else if(category.equalsIgnoreCase(EntityType.PARTNER.toString())){
			categoryName = partnerIdPartnerMap.get(connect.getPartnerId());
			//categoryName = partner.getPartnerName();
		}
		return categoryName;
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

	public void setDataProcessingService(DataProcessingService dataProcessingService) {
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
