package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.ReportConstants;

@Component
public class ConnectDetailedReportService {

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	ConnectSubSpLinkRepository connectSubSpLinkRepository;

	@Autowired
	ConnectOfferingLinkRepository connectOfferingLinkRepository;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	NotesTRepository notesTRepository;

	@Autowired
	GeographyRepository geographyRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectDetailedReportService.class);

	/**
	 * This method is used to set connect detailed report to spreadSheet
	 * 
	 * @param connectIdList
	 * @param fields
	 * @param workbook
	 * @param connectCategory
	 * @throws Exception
	 */
	public void setConnectDetailedReport(List<String> connectIdList,
			List<String> fields, SXSSFWorkbook workbook, String connectCategory)
			throws Exception {
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook
				.createSheet(ReportConstants.DETAILEDREPORT);

		SXSSFRow row = null;
		int currentRow = 0;
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);

		if (fields.size() == 0 && fields.isEmpty()) {
			createHeaderForMandatoryFields(row, spreadSheet, connectCategory);
			currentRow = connectReportWithMandatoryFields(connectIdList,
					spreadSheet, currentRow, row, connectCategory);
			currentRow++;
		} else {
			createHeaderOptionalFields(row, fields, workbook, spreadSheet,
					currentRow, connectCategory);
			currentRow = connectReportWithOptionalFields(connectIdList,
					workbook, spreadSheet, currentRow, fields, row,
					connectCategory);
			currentRow++;
		}
	}

	/**
	 * This Method is used to set mandatory header fields to spreadSheet
	 * 
	 * @param row
	 * @param spreadSheet
	 * @param connectCategory
	 */
	public void createHeaderForMandatoryFields(SXSSFRow row,
			SXSSFSheet spreadSheet, String connectCategory) {
		CellStyle headerSyle = ExcelUtils.createRowStyle(
				spreadSheet.getWorkbook(), ReportConstants.REPORTHEADER);
		int colNo = 0;
		row.createCell(colNo).setCellValue(ReportConstants.CONNECTID);
		row.getCell(colNo).setCellStyle(headerSyle);
		colNo++;
		row.createCell(colNo).setCellValue(ReportConstants.DISPLAYGEO);
		row.getCell(colNo).setCellStyle(headerSyle);
		colNo++;
		row.createCell(colNo).setCellValue(ReportConstants.DISPLAYSERVICELINE);
		row.getCell(colNo).setCellStyle(headerSyle);
		colNo++;
		if (!connectCategory.equals(ReportConstants.PARTNER)) {
			row.createCell(colNo).setCellValue(ReportConstants.DISPLAYIOU);
			row.getCell(colNo).setCellStyle(headerSyle);
			colNo++;
			row.createCell(colNo).setCellValue(
					ReportConstants.GROUPCUSTOMERNAME);
			row.getCell(colNo).setCellStyle(headerSyle);
			colNo++;
		}
		row.createCell(colNo).setCellValue(ReportConstants.CONNECTNAME);
		row.getCell(colNo).setCellStyle(headerSyle);
		colNo++;

	}

	/**
	 * This Method is used to set connect mandatory fields to spreadSheet
	 * 
	 * @param spreadSheet
	 * @param row
	 * @param connect
	 */
	public void setConnectReportMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, ConnectT connect, String connectCategory) {
		int colNo = 0;
		row.createCell(colNo).setCellValue(connect.getConnectId());
		colNo++;
		if (!connectCategory.equals(ReportConstants.PARTNER)) {
			if (connect.getCustomerMasterT() != null) {
				row.createCell(colNo).setCellValue(
						connect.getCustomerMasterT().getGeographyMappingT()
								.getDisplayGeography());
				colNo++;
				setDisplaySubSpToSpreadSheet(row, connect, colNo);
				colNo++;
				row.createCell(colNo).setCellValue(
						connect.getCustomerMasterT().getIouCustomerMappingT()
								.getDisplayIou());
				colNo++;
				row.createCell(colNo).setCellValue(
						connect.getCustomerMasterT().getGroupCustomerName());
				colNo++;
			} else {
				row.createCell(colNo).setCellValue(
						connect.getPartnerMasterT().getGeographyMappingT()
								.getDisplayGeography());
				colNo++;
				setDisplaySubSpToSpreadSheet(row, connect, colNo);
				colNo++;
				row.createCell(colNo).setCellValue(Constants.SPACE);
				colNo++;
				row.createCell(colNo).setCellValue(Constants.SPACE);
				colNo++;
			}
			row.createCell(colNo).setCellValue(connect.getConnectName());
		} else {
			row.createCell(colNo).setCellValue(
					connect.getPartnerMasterT().getGeographyMappingT()
							.getDisplayGeography());
			colNo++;
			setDisplaySubSpToSpreadSheet(row, connect, colNo);
			colNo++;
			row.createCell(colNo).setCellValue(connect.getConnectName());
		}
	}

	/**
	 * This Method is used create the connect report header both mandatory and
	 * optional fields
	 * 
	 * @param row
	 * @param fields
	 * @param workbook
	 * @param spreadSheet
	 * @param currentRow
	 * @param connectCategory
	 */
	public void createHeaderOptionalFields(SXSSFRow row, List<String> fields,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow,
			String connectCategory) {
		// This method creates header for mandatory fields
		createHeaderForMandatoryFields(row, spreadSheet, connectCategory);
		int columnNo = 4;
		if (!connectCategory.equals(ReportConstants.PARTNER)) {
			columnNo = 6;
		}
		CellStyle headerStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		List<String> orderedFields = Arrays.asList("iou", "geography", "country", "subSp", "offering", "tcsAccountContact",
				"tcsAccountRole", "custContactName", "custContactRole","locationOfConnect","connectType","partnerContactName","partnerContactRole","startDateOfConnect", "endDateOfConnect", "primaryOwner",
				"secondaryOwner", "connectNotes", "linkOpportunity", "connectCategory", "customerOrPartnerName", "createdDate",
				"createdBy", "modifiedDate", "modifiedBy");
		boolean isTimeZone = true;
		
		for (String field : orderedFields) {
			if(fields.contains(field)){
				row.createCell(columnNo).setCellValue(FieldsMap.fieldsMap.get(field));
				row.getCell(columnNo).setCellStyle(headerStyle);
				columnNo++;
			}
			
			if(field.equals("startDateOfConnect")) {
				row.createCell(columnNo).setCellValue("Start Time Of Connect");
				row.getCell(columnNo).setCellStyle(headerStyle);
				columnNo++;
			}
			
			if((field.equals("startDateOfConnect") || field.equals("endDateOfConnect")) && isTimeZone) {
				row.createCell(columnNo).setCellValue("Time Zone");
				row.getCell(columnNo).setCellStyle(headerStyle);
				isTimeZone = false;
				columnNo++;
			}
		
			if(field.equals("endDateOfConnect")) {
				row.createCell(columnNo).setCellValue("End Time Of Connect");
				row.getCell(columnNo).setCellStyle(headerStyle);
				columnNo++;
			}
		}

		// set connect task details header
		if (fields.contains(ReportConstants.TASK)) {
			row.createCell(columnNo).setCellValue(ReportConstants.TASKCOUNT);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKID);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(
					ReportConstants.TASKDESCRIPTION);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(
					ReportConstants.ENTITYREFERENCE);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKOWNER);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(
					ReportConstants.TARGETDATEFORCOMPLETION);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKSTATUS);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKNOTE);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
		}
	}

	/**
	 * This method is used set connect report mandatory details to spreadSheet
	 * 
	 * @param connectIdList
	 * @param spreadSheet
	 * @param currentRow
	 * @param row
	 * @param connectCategory
	 * @return
	 */
	public int connectReportWithMandatoryFields(List<String> connectIdList,
			SXSSFSheet spreadSheet, int currentRow, SXSSFRow row,
			String connectCategory) {
		for (String connectId : connectIdList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow + 1);
			ConnectT connect = connectRepository.findByConnectId(connectId);
			setConnectReportMandatoryFields(spreadSheet, row, connect,
					connectCategory);
			currentRow++;
		}
		return currentRow;
	}

	/**
	 * This Method is used to set connect object list to excel
	 * 
	 * @param connectList
	 * @param workbook
	 * @param spreadSheet
	 * @param currentRow
	 * @param fields
	 * @param row
	 * @param connectCategory
	 * @return
	 * @throws DestinationException
	 */
	public int connectReportWithOptionalFields(List<String> connectIdList,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow,
			List<String> fields, SXSSFRow row, String connectCategory)
			throws DestinationException {
		logger.info("Inside connectReportWithOptionalFields() method");
		currentRow = currentRow + 1;
		boolean iouFlag = fields.contains(ReportConstants.IOU);
		boolean geoFlag = fields.contains(ReportConstants.GEOGRAPHY);
		boolean subSpFlag = fields.contains(ReportConstants.SUBSP);
		boolean countryFlag = fields.contains(ReportConstants.COUNTRY);
		boolean offeringFlag = fields.contains(ReportConstants.OFFERING);
		boolean categoryFlag = fields.contains(ReportConstants.CATEGORY);
		boolean startDateFlag = fields.contains(ReportConstants.STARTDATE);
		boolean endDateFlag = fields.contains(ReportConstants.ENDDATE);
		boolean primaryOwnerFlag = fields.contains(ReportConstants.PRIMARYOWNER);
		boolean secondaryOwnerFlag = fields.contains(ReportConstants.SECONDARYOWNER);
		boolean custPartNameFlag = fields.contains(ReportConstants.CUSTOMERORPARTNERNAME);
		boolean tcsContactNameFlag = fields.contains(ReportConstants.TCSACCOUNTCONTACT);
		boolean tcsContactRoleFlag = fields.contains(ReportConstants.TCSACCOUNTROLE);
		boolean custContactNameFlag = fields.contains(ReportConstants.CUSTOMERCONTACTNAME);
		boolean custContactRoleFlag = fields.contains(ReportConstants.CUSTOMERCONTACTROLE);
		boolean locationOfConnectFlag = fields.contains(ReportConstants.LOCATION_OF_CONNECT);
		boolean connectTypeFlag = fields.contains(ReportConstants.CONNECT_TYPE);
		boolean partnerContactNameFlag = fields.contains(ReportConstants.PARTNERCONTACTNAME);
		boolean partnerContactRoleFlag = fields.contains(ReportConstants.PARTNERCONTACTROLE);
		boolean linkOppFlag = fields.contains(ReportConstants.LINKOPPORTUNITY);
		boolean notesFlag = fields.contains(ReportConstants.CONNECTNOTES);
		boolean taskFlag = fields.contains(ReportConstants.TASK);
		// 4 columns added as per prod tracker
		boolean createdDateFlag = fields.contains(ReportConstants.CREATEDDATE);
		boolean createdByFlag = fields.contains(ReportConstants.CREATEDBY);
		boolean modifiedDateFlag = fields.contains(ReportConstants.MODIFIEDDATE);
		boolean modifiedByFlag = fields.contains(ReportConstants.MODIFIEDBY);
		CellStyle cellStyleDateTimeFormat = spreadSheet.getWorkbook().createCellStyle(); 
		
		CreationHelper createHelper = spreadSheet.getWorkbook().getCreationHelper();
		cellStyleDateTimeFormat.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy hh:mm")); 
		
		CellStyle cellStyleDateFormat = spreadSheet.getWorkbook().createCellStyle(); 
		cellStyleDateFormat.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy")); 
		
		CellStyle cellStyleTimeFormat = spreadSheet.getWorkbook().createCellStyle(); 
		cellStyleTimeFormat.setDataFormat(createHelper.createDataFormat().getFormat("hh:mm")); 
		
		for (String connectId : connectIdList) {
			ConnectT connect = connectRepository.findByConnectId(connectId);
			List<TaskT> taskList = taskRepository.findByConnectId(connect.getConnectId());
			List<Object[]> tcsAccountContactList=contactRepository.findTcsAccountContactNamesByConnectId(connect.getConnectId());
			List<Object[]> cusContactList=contactRepository.findCustomerContactNamesByConnectId(connect.getConnectId());
			List<Object[]> partnerContactList=contactRepository.findPartnerContactNamesByConnectId(connect.getConnectId());

			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);

			//set Connect Mandatory Details 
			setConnectReportMandatoryFields(spreadSheet, row, connect,connectCategory);

			int colValue = 4;
			if(!connectCategory.equals(ReportConstants.PARTNER)){
				colValue = 6;
			}
			
			if(iouFlag) {
				SXSSFCell iouCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				if(connect.getCustomerMasterT()!=null){
					iouCell.setCellValue(connect.getCustomerMasterT().getIouCustomerMappingT().getIou());
				}
				colValue++;
			}
			
			if(geoFlag) {
				SXSSFCell geographyCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				if(connect.getCustomerMasterT()!=null){
					geographyCell.setCellValue(connect.getCustomerMasterT().getGeographyMappingT().getGeography());
				}else{
					geographyCell.setCellValue(connect.getPartnerMasterT().getGeographyMappingT().getGeography());
				}
				colValue++;
			}

			if(countryFlag) {
				SXSSFCell countryCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				countryCell.setCellValue(connect.getGeographyCountryMappingT().getCountry());
				colValue++;
			}
			
			if(subSpFlag) {
				List<String> connectSubSpList =connectSubSpLinkRepository.findSubSpByConnectId(connect.getConnectId());
				SXSSFCell subSpCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				subSpCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(connectSubSpList));
				colValue++;
			}

			if(offeringFlag) {
				List<String> connectOffering=connectOfferingLinkRepository.findOfferingByConnectId(connect.getConnectId());
				SXSSFCell offeringCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				offeringCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(connectOffering));
				colValue++;
			}

			if(tcsContactNameFlag) {
				List<String> tcsContactNamesList=new ArrayList<String>();
				List<String> tcsContactNames=new ArrayList<String>();
				SXSSFCell tcsAccountContactCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				for(Object[] tcsAccountContact:tcsAccountContactList){
					tcsContactNames.add((String) tcsAccountContact[0]);
				}
				for(int i=1;i<=tcsAccountContactList.size();i++){
					tcsContactNamesList.add(i+"-"+tcsContactNames.get(i-1));
				}
				tcsAccountContactCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(tcsContactNamesList));
				colValue++;
			}
			
			if(tcsContactRoleFlag) {
				List<String> tcsContactNamesList=new ArrayList<String>();
				List<String> tcsContactRoles=new ArrayList<String>();
				SXSSFCell tcsAccountContactCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				for(Object[] tcsAccountContact:tcsAccountContactList){
					tcsContactRoles.add((String) tcsAccountContact[1]);
				}
				for(int i=1;i<=tcsAccountContactList.size();i++){
					tcsContactNamesList.add(i+"-"+tcsContactRoles.get(i-1));
				}
				tcsAccountContactCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(tcsContactNamesList));
				colValue++;
			}

			if(custContactNameFlag) {
				List<String> cusContactNamesList=new ArrayList<String>();
				List<String> cusContactNames=new ArrayList<String>();
				SXSSFCell customerContactNameCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				for(Object[] cusContact:cusContactList){
					cusContactNames.add((String) cusContact[0]);
				}
				for(int i=1;i<=cusContactList.size();i++){
					cusContactNamesList.add(i+"-"+cusContactNames.get(i-1));
				}
				customerContactNameCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(cusContactNamesList));
				colValue++;
			}
			
			if(custContactRoleFlag) {
				List<String> cusContactNamesList=new ArrayList<String>();
				List<String> cusContactRole=new ArrayList<String>();
				SXSSFCell customerContactRoleCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				for(Object[] cusContact:cusContactList){
					cusContactRole.add((String) cusContact[1]);
				}
				for(int i=1;i<=cusContactList.size();i++){
					cusContactNamesList.add(i+"-"+cusContactRole.get(i-1));
				}
				customerContactRoleCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(cusContactNamesList));
				colValue++;
			}
			
			if(locationOfConnectFlag) {
				SXSSFCell connectLocationCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				if(StringUtils.isNotEmpty(connect.getLocation())) {
					connectLocationCell.setCellValue(connect.getLocation());
				}
				colValue++;
			}
			
			if(connectTypeFlag) {
				SXSSFCell connectTypeCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				if(StringUtils.isNotEmpty(connect.getType())) {
					connectTypeCell.setCellValue(connect.getType());
				}
				colValue++;
			}
			
		    if(partnerContactNameFlag) {
				List<String> partnerContactNamesList=new ArrayList<String>();
				List<String> partnerContactNames=new ArrayList<String>();
				SXSSFCell partnerContactNameCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				for(Object[] partnerContact:partnerContactList){
					partnerContactNames.add((String)partnerContact[0]);
				}
				for(int i=1;i<=partnerContactList.size();i++){
					partnerContactNamesList.add(i+"-"+partnerContactNames.get(i-1));
				}
				partnerContactNameCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(partnerContactNamesList));
				colValue++;
			}
			
			if(partnerContactRoleFlag) {
				List<String> partnerContactRolesList=new ArrayList<String>();
				List<String> partnerContactRole=new ArrayList<String>();
				SXSSFCell partnerContactRoleCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				for(Object[] partnerContact:partnerContactList){
					partnerContactRole.add((String) partnerContact[1]);
				}
				for(int i=1;i<=partnerContactList.size();i++){
					partnerContactRolesList.add(i+"-"+partnerContactRole.get(i-1));
				}
				partnerContactRoleCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(partnerContactRolesList));
				colValue++;
			}

			if(startDateFlag) {
				SXSSFCell startDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				startDateOfConnectCell.setCellValue(connect.getStartDatetimeOfConnect());
				startDateOfConnectCell.setCellStyle(cellStyleDateFormat);
				colValue++;
				SXSSFCell startTimeOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				startTimeOfConnectCell.setCellValue(connect.getStartDatetimeOfConnect());
				startTimeOfConnectCell.setCellStyle(cellStyleTimeFormat);
				colValue++;
			}
			
			if(startDateFlag || endDateFlag) {
				SXSSFCell startDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				startDateOfConnectCell.setCellValue(connect.getTimeZoneMappingT().getTimeZoneCode());
				colValue++;
			}

			if(endDateFlag) {
				SXSSFCell endDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				endDateOfConnectCell.setCellValue(connect.getEndDatetimeOfConnect());
				endDateOfConnectCell.setCellStyle(cellStyleDateFormat);
				colValue++;
				SXSSFCell endTimeOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				endTimeOfConnectCell.setCellValue(connect.getEndDatetimeOfConnect());
				endTimeOfConnectCell.setCellStyle(cellStyleTimeFormat);
				colValue++;
			}

			if(primaryOwnerFlag) {
				SXSSFCell primaryOwnerCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				primaryOwnerCell.setCellValue(connect.getPrimaryOwnerUser().getUserName());
				colValue++;
			}

			if(secondaryOwnerFlag) {
				List<String> secondaryOwnersList=userRepository.getSecondaryOwnerNamesByConnectId(connect.getConnectId());
				SXSSFCell secondaryOwnerCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				secondaryOwnerCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(secondaryOwnersList));
				colValue++;
			}

			if(notesFlag) {
				List<String> connectNotesList=notesTRepository.findConnectNotesByConnectId(connect.getConnectId());
				SXSSFCell connectNotesCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				connectNotesCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(connectNotesList));
				colValue++;
			}

			if(linkOppFlag) {
				List<String> opportunityNames= opportunityRepository.findLinkOpportunityByConnectId(connect.getConnectId());
				SXSSFCell opportunityCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				opportunityCell.setCellValue(ExcelUtils.removeSquareBracesAndAppendListElementsAsString(opportunityNames));
				colValue++;
			}

			if(categoryFlag) {
				SXSSFCell categoryCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				categoryCell.setCellValue(connect.getConnectCategory());
				colValue++;
			}

			if(custPartNameFlag) {
				if (connect.getCustomerMasterT() != null) {
					SXSSFCell cusPartcell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					cusPartcell.setCellValue(connect.getCustomerMasterT().getCustomerName());
					colValue++;
				} else if (connect.getPartnerMasterT() != null) {
					SXSSFCell cusPartcell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					cusPartcell.setCellValue(connect.getPartnerMasterT().getPartnerName());
					colValue++;
				} else {
					colValue++;
				}
			}

			// 4 columns added as per prod tracker 
			if(createdDateFlag) {
				SXSSFCell createdDateCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				Timestamp createdDateTimeStamp = connect.getCreatedDatetime();
				Date createdDate = DateUtils.toDate(createdDateTimeStamp);
				createdDateCell.setCellValue(createdDate);
				createdDateCell.setCellStyle(cellStyleDateTimeFormat);
				colValue++;
			}
			
			if(createdByFlag) {
				if(!connect.getCreatedBy().isEmpty()){
				SXSSFCell createdByCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				createdByCell.setCellValue(connect.getCreatedByUser().getUserName());
				colValue++;
				}
			}
			
			if(modifiedDateFlag) {
				SXSSFCell modifiedDateCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				Timestamp modifiedDateTimeStamp = connect.getModifiedDatetime();
				Date modifiedDate = DateUtils.toDate(modifiedDateTimeStamp);
				modifiedDateCell.setCellValue(modifiedDate);
				modifiedDateCell.setCellStyle(cellStyleDateTimeFormat);
				colValue++;
			}
			
			if(modifiedByFlag) {
				if(!connect.getModifiedBy().isEmpty()){
				SXSSFCell modifiedByCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				modifiedByCell.setCellValue(connect.getModifiedByUser().getUserName());
				colValue++;
				}
			}
			
			if(taskFlag) {
				SXSSFCell taskCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				taskCell.setCellValue(taskList.size());
				if (taskList.size() > 0) {
					for(int i=0;i<=colValue;i++){
						spreadSheet.addMergedRegion(new CellRangeAddress(currentRow - 1, currentRow + taskList.size()- 2, i, i));
					}
					for (int task = 0; task < taskList.size(); task++) {
						colValue =	getTaskDetails(spreadSheet, row, taskList, colValue, task);
						row = (SXSSFRow) spreadSheet.createRow((short) currentRow	+ task);
					}
					colValue=colValue+8;
					currentRow = currentRow + 0;
				} else {
					colValue=colValue+8;
				}
				
				if (taskList.size() > 1) {
					currentRow = currentRow + taskList.size() - 1;
				} else {
					currentRow = currentRow + 0;
				}
			}
		}
		return currentRow;
	}

	/**
	 * This Method is used to set primary and secondary displaySubSp to spreadSheet
	 * 
	 * @param row
	 * @param connect
	 * @param colNo
	 */
	private void setDisplaySubSpToSpreadSheet(SXSSFRow row, ConnectT connect,
			int colNo) {
		List<String> connectDisplaySubSpList = connectSubSpLinkRepository
				.findDisplaySubSpByConnectId(connect.getConnectId());
		if (!connectDisplaySubSpList.isEmpty()) {
			row.createCell(colNo).setCellValue(
					ExcelUtils.removeSquareBracesAndAppendListElementsAsString(connectDisplaySubSpList));
		}
	}

	/**
	 * This method is used to set connect task details to spreadSheet
	 * 
	 * @param spreadSheet
	 * @param row
	 * @param taskList
	 * @param columnNo
	 * @param i
	 * @return
	 */
	public int getTaskDetails(SXSSFSheet spreadSheet, SXSSFRow row,
			List<TaskT> taskList, int columnNo, int i) {
		int columnOffset = 1;
		row.createCell(columnNo + columnOffset).setCellValue(
				taskList.get(i).getTaskId());
		columnOffset++;
		row.createCell(columnNo + columnOffset).setCellValue(
				taskList.get(i).getTaskDescription());
		columnOffset++;
		row.createCell(columnNo + columnOffset).setCellValue(
				taskList.get(i).getEntityReference());
		columnOffset++;
		UserT taskOwnerName = userRepository.findByUserId(taskList.get(i)
				.getTaskOwner());
		row.createCell(columnNo + columnOffset).setCellValue(
				taskOwnerName.getUserName());
		columnOffset++;
		row.createCell(columnNo + columnOffset).setCellValue(
				taskList.get(i).getTargetDateForCompletion().toString());
		columnOffset++;
		row.createCell(columnNo + columnOffset).setCellValue(
				taskList.get(i).getTaskStatus());
		columnOffset++;
		List<String> taskNotesUpdatedList = notesTRepository
				.findNotesUpdatedByNotesId(taskList.get(i).getTaskId());
		row.createCell(columnNo + columnOffset).setCellValue(
				ExcelUtils.removeSquareBracesAndAppendListElementsAsString(taskNotesUpdatedList));
		return columnNo;
	}

	/**
	 * This Method used to set Connect Report Title Page To spreadSheet
	 * 
	 * @param workbook
	 * @param displayGeography
	 * @param iou
	 * @param serviceLines
	 * @param userId
	 * @param tillDate
	 * @param string
	 * @param year
	 * @param quarter
	 * @param month
	 * @param country
	 * @param connectCategory
	 */
	public void getConnectTitlePage(SXSSFWorkbook workbook,
			String displayGeography, List<String> iou,
			List<String> serviceLines, UserT user, List<String> country,
			String month, String quarter, String year, String reportType,
			String connectCategory) {
		logger.info("Inside getConnectTitlePage() method");
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook
				.createSheet(ReportConstants.TITLE);

		List<String> privilegeValueList = new ArrayList<String>();

		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);

		SXSSFRow row = null;
		int currentRowNo = 4;
		int currentColumnNo = 4;

		row = (SXSSFRow) spreadSheet.createRow(currentRowNo);
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRowNo,
				currentRowNo, currentColumnNo, currentColumnNo + 3));
		row.createCell(currentColumnNo).setCellValue(
				ReportConstants.HEADING + DateUtils.getCurrentDate());
		row.getCell(currentColumnNo).setCellStyle(headinStyle);
		currentRowNo = currentRowNo + 2;

		row = (SXSSFRow) spreadSheet.createRow(currentRowNo);
		row.createCell(currentColumnNo).setCellValue(
				ReportConstants.USERSELECTIONFILTER);
		spreadSheet.autoSizeColumn(currentColumnNo);
		row.getCell(currentColumnNo).setCellStyle(subHeadingStyle);

		currentRowNo++;
		ExcelUtils.writeDetailsForSearchType(spreadSheet,
				ReportConstants.CONNECTCATEGORY, connectCategory, currentRowNo,
				currentColumnNo);
		currentRowNo++;
		ExcelUtils.writeDetailsForSearchType(spreadSheet, ReportConstants.GEO,
				displayGeography, currentRowNo, currentColumnNo);
		currentRowNo++;
		ExcelUtils.writeDetailsForSearchType(spreadSheet,
				ReportConstants.Country, country, currentRowNo, dataRow);
		currentRowNo++;
		ExcelUtils.writeDetailsForSearchType(spreadSheet, Constants.IOU, iou,
				currentRowNo, dataRow);
		currentRowNo++;
		ExcelUtils.writeDetailsForSearchType(spreadSheet,
				ReportConstants.SERVICELINES, serviceLines, currentRowNo,
				dataRow);
		currentRowNo++;

		row = (SXSSFRow) spreadSheet.createRow(currentRowNo++);
		row.createCell(currentColumnNo).setCellValue(ReportConstants.PERIOD);
		String period = ExcelUtils.getPeriod(month, quarter, year);
		row.createCell(currentColumnNo + 1).setCellValue(period);

		List<UserAccessPrivilegesT> userPrivilegesList = userAccessPrivilegesRepository
				.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(
						user.getUserId(), Constants.Y);
		String userGroup = user.getUserGroupMappingT().getUserGroup();

		currentRowNo++;
		row = (SXSSFRow) spreadSheet.createRow(currentRowNo++);
		row.createCell(currentColumnNo).setCellValue(
				ReportConstants.USERACCESSFILTER);

		row.getCell(currentColumnNo).setCellStyle(subHeadingStyle);
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
		case BDM:
		case CONSULTING_USER:
			ExcelUtils.writeUserFilterConditions(spreadSheet, user,
					ReportConstants.CONNECTSWHEREPRIMARYORSECONDARYOWNER,
					currentRowNo++, currentColumnNo);
			currentRowNo = currentRowNo + 4;
			break;

		case BDM_SUPERVISOR:
		case CONSULTING_HEAD:
			ExcelUtils
					.writeUserFilterConditions(
							spreadSheet,
							user,
							ReportConstants.CONNECTSWHEREBDMSUPERVISORPRIMARYORSECONDARYOWNER,
							currentRowNo++, currentColumnNo);
			currentRowNo = currentRowNo + 4;
			break;

		case GEO_HEADS:
			for (UserAccessPrivilegesT accessPrivilegesT : userPrivilegesList) {
				String previlageType = accessPrivilegesT.getPrivilegeType();
				String privilageValue = accessPrivilegesT.getPrivilegeValue();
				if (previlageType.equals(PrivilegeType.GEOGRAPHY.name())) {
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadSheet,
					ReportConstants.PRIVILEGEGEOGRAPHY, privilegeValueList,
					user, dataRow,
					ReportConstants.CONNECTSGEOORIOUHEADSCONDITION,
					currentRowNo++, currentColumnNo);
			currentRowNo = currentRowNo + 4;
			break;

		case IOU_HEADS:
			for (UserAccessPrivilegesT accessPrivilegesT : userPrivilegesList) {
				String previlageType = accessPrivilegesT.getPrivilegeType();
				String privilageValue = accessPrivilegesT.getPrivilegeValue();
				if (previlageType.equals(PrivilegeType.IOU.name())) {
					privilegeValueList.add(privilageValue);
				}
			}
			ExcelUtils.writeDetailsForSearchTypeUserAccessFilter(spreadSheet,
					ReportConstants.PRIVILEGEIOU, privilegeValueList, user,
					dataRow, ReportConstants.CONNECTSGEOORIOUHEADSCONDITION,
					currentRowNo++, currentColumnNo);
			currentRowNo = currentRowNo + 4;
			break;
		default:
			ExcelUtils
					.writeUserFilterConditions(spreadSheet, user,
							ReportConstants.FULLACCESS, currentRowNo++,
							currentColumnNo);
			currentRowNo = currentRowNo + 4;
		}

		row = (SXSSFRow) spreadSheet.createRow(currentRowNo++);
		row.createCell(currentColumnNo).setCellValue(
				ReportConstants.DISPLAYPREFERENCE);
		row.getCell(currentColumnNo).setCellStyle(subHeadingStyle);

		row = (SXSSFRow) spreadSheet.createRow(currentRowNo++);
		row.createCell(currentColumnNo)
				.setCellValue(ReportConstants.REPORTTYPE);
		row.createCell(currentColumnNo + 1).setCellValue(reportType);
		currentRowNo++;
		spreadSheet.addMergedRegion(new CellRangeAddress(currentRowNo,
				currentRowNo, currentColumnNo, currentColumnNo + 3));
		row = (SXSSFRow) spreadSheet.createRow(currentRowNo);
		row.createCell(currentColumnNo)
				.setCellValue(ReportConstants.REPORTNOTE);
	}

}
