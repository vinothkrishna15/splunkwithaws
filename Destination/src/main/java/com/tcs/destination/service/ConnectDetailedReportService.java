package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserAccessPrivilegesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
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

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectDetailedReportService.class);

	public void getConnectDetailedReport(List<ConnectT> connectList,
			List<String> fields, SXSSFWorkbook workbook) throws Exception {
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.createSheet("Detailed Report");
		SXSSFRow row = null;
		int currentRow = 0;
		row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		if (fields.size() == 0 && fields.isEmpty()) {
			createHeaderForMandatoryFields(row, spreadSheet);
			currentRow = connectReportWithMandatoryFields(connectList,
					spreadSheet, currentRow, row);
			currentRow++;
		} else {
			createHeaderOptionalFields(connectList, row, fields, workbook, spreadSheet, currentRow);
			currentRow = connectReportWithOptionalFields(connectList, workbook,
					spreadSheet, currentRow, fields, row);
			currentRow++;
		}
	}

	public void createHeaderForMandatoryFields(SXSSFRow row, SXSSFSheet spreadSheet) {
		CellStyle headerSyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),ReportConstants.REPORTHEADER);
		row.createCell(0).setCellValue(ReportConstants.CONNECTID);
		row.getCell(0).setCellStyle(headerSyle);
		row.createCell(1).setCellValue(ReportConstants.DISPLAYGEO);
		row.getCell(1).setCellStyle(headerSyle);
		row.createCell(2).setCellValue(ReportConstants.DISPLAYSERVICELINE);
		row.getCell(2).setCellStyle(headerSyle);
		row.createCell(3).setCellValue(ReportConstants.DISPLAYIOU);
		row.getCell(3).setCellStyle(headerSyle);
		row.createCell(4).setCellValue(ReportConstants.CONNECTNAME);
		row.getCell(4).setCellStyle(headerSyle);
		row.createCell(5).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
		row.getCell(5).setCellStyle(headerSyle);
	}

	public void createHeaderOptionalFields(List<ConnectT> connectList,
			SXSSFRow row, List<String> fields,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow) {
		// This method creates header for mandatory fields
		createHeaderForMandatoryFields(row, spreadSheet);
		int columnNo = 6;
		CellStyle headerStyle = ExcelUtils.createRowStyle(workbook,	ReportConstants.REPORTHEADER);
		List<String> orderedFields = Arrays.asList("iou","geography","country","subSp","offering","tcsAccountContact","custContactName",
				"startDateOfConnect","endDateOfConnect","primaryOwner", "secondaryOwner","connectNotes","linkOpportunity","connectCategory",
				"customerOrPartnerName","createdDate","createdBy", "modifiedDate","modifiedBy");
		for (String field : orderedFields) {
			if(fields.contains(field)){
				row.createCell(columnNo).setCellValue(FieldsMap.fieldsMap.get(field));
				row.getCell(columnNo).setCellStyle(headerStyle);
				columnNo++;
			}
		}

		if(fields.contains(ReportConstants.TASK)) {
			row.createCell(columnNo).setCellValue(ReportConstants.TASKCOUNT);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKID);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKDESCRIPTION);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.ENTITYREFERENCE);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TASKOWNER);
			row.getCell(columnNo).setCellStyle(headerStyle);
			columnNo++;
			row.createCell(columnNo).setCellValue(ReportConstants.TARGETDATEFORCOMPLETION);
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

	public int connectReportWithMandatoryFields(List<ConnectT> connectList,SXSSFSheet spreadSheet, int currentRow, SXSSFRow row) {
		for (ConnectT connect : connectList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow + 1);
			getConnectReportMandatoryFields(spreadSheet, row, connect);
			currentRow++;
		}
		return currentRow;
	}

	/**
	 * This Method is used to set connect mandatory fields to sheet
	 * @param spreadSheet
	 * @param row
	 * @param connect
	 */
	public void getConnectReportMandatoryFields(SXSSFSheet spreadSheet, SXSSFRow row, ConnectT connect) {
		//		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		row.createCell(0).setCellValue(connect.getConnectId());
		row.createCell(4).setCellValue(connect.getConnectName());
		List<String> displaySubSpList = new ArrayList<String>();

		for (ConnectSubSpLinkT connectSubSpLinkT : connect.getConnectSubSpLinkTs()) {
			displaySubSpList.add(connectSubSpLinkT.getSubSpMappingT().getSubSp());
		}
		row.createCell(2).setCellValue(displaySubSpList.toString().replace("[", "").replace("]", ""));

		if(connect.getCustomerMasterT()!=null){
			row.createCell(1).setCellValue(connect.getCustomerMasterT().getGeographyMappingT().getDisplayGeography());
			row.createCell(3).setCellValue(connect.getCustomerMasterT().getIouCustomerMappingT().getDisplayIou());
			row.createCell(5).setCellValue(connect.getCustomerMasterT().getGroupCustomerName());

		}else{
			row.createCell(1).setCellValue(connect.getPartnerMasterT().getGeographyMappingT().getDisplayGeography());
			//			row.createCell(3).setCellValue(Constants.SPACE);
			//			row.createCell(5).setCellValue(Constants.SPACE);
		}
	}

	/**
	 * This Method is used to set connect object list to excel
	 * @param connectList
	 * @param workbook
	 * @param spreadSheet
	 * @param currentRow
	 * @param fields
	 * @param row
	 * @return
	 * @throws DestinationException
	 */
	public int connectReportWithOptionalFields(List<ConnectT> connectList, SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow,
			List<String> fields, SXSSFRow row) throws DestinationException {
		//		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook, ReportConstants.DATAROW);
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
		boolean custContactNameFlag = fields.contains(ReportConstants.CUSTOMERCONTACTNAME);
		boolean linkOppFlag = fields.contains(ReportConstants.LINKOPPORTUNITY);
		boolean notesFlag = fields.contains(ReportConstants.CONNECTNOTES);
		boolean taskFlag = fields.contains(ReportConstants.TASK);
		//4 columns added as per prod tracker
		boolean createdDateFlag = fields.contains(ReportConstants.CREATEDDATE);
		boolean createdByFlag = fields.contains(ReportConstants.CREATEDBY);
		boolean modifiedDateFlag = fields.contains(ReportConstants.MODIFIEDDATE);
		boolean modifieddByFlag = fields.contains(ReportConstants.MODIFIEDBY);


		for (ConnectT connect : connectList) {
			List<TaskT> taskList = taskRepository.findByConnectId(connect.getConnectId());
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);

			//set Connect Mandatory Details 
			getConnectReportMandatoryFields(spreadSheet, row, connect);

			int colValue = 6;
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
				subSpCell.setCellValue(connectSubSpList.toString().replace("[", "").replace("]", ""));
				colValue++;
			}

			if(offeringFlag) {
				List<String> connectOffering=connectOfferingLinkRepository.findOfferingByConnectId(connect.getConnectId());
				SXSSFCell offeringCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				offeringCell.setCellValue(connectOffering.toString().replace("[", "").replace("]", ""));
				colValue++;
			}

			if(tcsContactNameFlag) {
				List<String> tcsContactNames=contactRepository.findTcsAccountContactNamesByConnectId(connect.getConnectId());
				SXSSFCell tcsAccountContactCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				tcsAccountContactCell.setCellValue(tcsContactNames.toString().replace("[", "").replace("]", ""));
				colValue++;
			}

			if(custContactNameFlag) {
				List<String> cusContactNames=contactRepository.findCustomerContactNamesByConnectId(connect.getConnectId());
				SXSSFCell customerContactNameCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				customerContactNameCell.setCellValue(cusContactNames.toString().replace("[", "").replace("]", ""));
				colValue++;
			}

			if(startDateFlag) {
				SXSSFCell startDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				startDateOfConnectCell.setCellValue(connect.getStartDatetimeOfConnect().toString());
				colValue++;
			}

			if(endDateFlag) {
				SXSSFCell endDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				endDateOfConnectCell.setCellValue(connect.getEndDatetimeOfConnect().toString());
				colValue++;
			}

			if(primaryOwnerFlag) {
				SXSSFCell primaryOwnerCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				UserT userT = userRepository.findByUserId(connect.getPrimaryOwner());
				primaryOwnerCell.setCellValue(userT.getUserName());
				colValue++;
			}

			if(secondaryOwnerFlag) {
				List<String> secondaryOwnersList=userRepository.getSecondaryOwnerNamesByConnectId(connect.getConnectId());
				SXSSFCell secondaryOwnerCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				secondaryOwnerCell.setCellValue(secondaryOwnersList.toString().replace("[", "").replace("]", ""));
				colValue++;
			}

			if(notesFlag) {
				List<String> connectNotesList=notesTRepository.findConnectNotesByConnectId(connect.getConnectId());
				SXSSFCell connectNotesCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				connectNotesCell.setCellValue(connectNotesList.toString().replace("[", "").replace("]", ""));
				colValue++;
			}

			if(linkOppFlag) {
				List<String> opportunityNames= opportunityRepository.findLinkOpportunityByConnectId(connect.getConnectId());
				SXSSFCell opportunityCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
				opportunityCell.setCellValue(opportunityNames.toString().replace("[", "").replace("]", ""));
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
				java.util.Date createdDate = DateUtils.toDate(createdDateTimeStamp);
				String dateOfCreation = DateUtils.convertDateToString(createdDate);
				createdDateCell.setCellValue(dateOfCreation);
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
				java.util.Date modifiedDate = DateUtils.toDate(modifiedDateTimeStamp);
				String dateOfModification = DateUtils.convertDateToString(modifiedDate);
				modifiedDateCell.setCellValue(dateOfModification);
				colValue++;
			}
			if(modifieddByFlag) {
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

	public int getTaskDetails(SXSSFSheet spreadSheet, SXSSFRow row,
			List<TaskT> taskList, int columnNo, int i) {
		//		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),
		//				ReportConstants.DATAROW);
		int columnOffset=1;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTaskId());
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTaskDescription());
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getEntityReference());
		columnOffset++;
		UserT taskOwnerName = userRepository.findByUserId(taskList.get(i).getTaskOwner());
		row.createCell(columnNo+columnOffset).setCellValue(taskOwnerName.getUserName());
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTargetDateForCompletion().toString());
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTaskStatus());
		columnOffset++;
		List<String> taskNotesUpdatedList=notesTRepository.findNotesUpdatedByNotesId(taskList.get(i).getTaskId());
		row.createCell(columnNo+columnOffset).setCellValue(taskNotesUpdatedList.toString().replace("[", "").replace("]", ""));
		return columnNo;
	}

	/**
	 * This Method Writes The Connect Report Title Page In WorkBook
	 * @param workbook
	 * @param geography
	 * @param iou
	 * @param serviceLines
	 * @param userId
	 * @param tillDate
	 * @param string 
	 * @param year 
	 * @param quarter 
	 * @param month 
	 * @param country 
	 */
	public void getConnectTitlePage(SXSSFWorkbook workbook,
			List<String> geography, List<String> iou,
			List<String> serviceLines, String userId, String tillDate, List<String> country, String month, String quarter, String year, String reportType) {

		SXSSFSheet spreadsheet = (SXSSFSheet) workbook.createSheet("Title");
		List<String> privilegeValueList = new ArrayList<String>();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook,	ReportConstants.REPORTHEADER);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		SXSSFRow row = null;


		row = (SXSSFRow) spreadsheet.createRow(4);
		spreadsheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 7));
		row.createCell(4).setCellValue("Connect report as on " + tillDate);
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, ReportConstants.GEO, geography, 7, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Country", country, 8, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, Constants.IOU, iou, 9, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Service Line", serviceLines, 10, dataRow);
		row = (SXSSFRow) spreadsheet.createRow(11);
		row.createCell(4).setCellValue("Period");
		String period=ExcelUtils.getPeriod(month, quarter, year);
		row.createCell(5).setCellValue(period);


		String userAccessField = null;
		List<UserAccessPrivilegesT> userPrivilegesList = 
				userAccessPrivilegesRepository.findByUserIdAndParentPrivilegeIdIsNullAndIsactive(userId, Constants.Y);
		UserT user = userRepository.findByUserId(userId);
		String userGroup=user.getUserGroupMappingT().getUserGroup();
		row = (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue("User Access Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		switch (userGroup) {
		case ReportConstants.GEOHEAD:
			userAccessField = "Geography";
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(PrivilegeType.GEOGRAPHY.name())){
					privilegeValueList.add(privilageValue);
				}
			}
			writeDetailsForSearchType(spreadsheet, userAccessField, privilegeValueList, 15, dataRow);
			break;
		case ReportConstants.IOUHEAD:
			userAccessField = "IOU";
			for(UserAccessPrivilegesT accessPrivilegesT:userPrivilegesList){
				String previlageType=accessPrivilegesT.getPrivilegeType();
				String privilageValue=accessPrivilegesT.getPrivilegeValue();
				if(previlageType.equals(PrivilegeType.IOU.name())){
					privilegeValueList.add(privilageValue);
				}
			}
			//			
			writeDetailsForSearchType(spreadsheet, userAccessField, privilegeValueList, 15, dataRow);
			break;
		case ReportConstants.BDM:
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.CONNECTSWHEREPRIMARYORSECONDARYOWNER);
			break;
		case ReportConstants.BDMSUPERVISOR:
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.CONNECTSWHEREBDMSUPERVISORPRIMARYORSECONDARYOWNER);
			break;
		default :
			ExcelUtils.writeUserFilterConditions(spreadsheet, user, ReportConstants.FULLACCESS);
		}
		row = (SXSSFRow) spreadsheet.createRow(21);
		//		spreadsheet.addMergedRegion(new CellRangeAddress(21, 21, 4, 7));
		row.createCell(4).setCellValue("Display Preferences");
		row.getCell(4).setCellStyle(subHeadingStyle);
		row = (SXSSFRow) spreadsheet.createRow(22);
		row.createCell(4).setCellValue("Report Type");
		row.createCell(5).setCellValue(reportType);

		spreadsheet.addMergedRegion(new CellRangeAddress(24, 24, 4, 7));
		row = (SXSSFRow) spreadsheet.createRow(24);
		row.createCell(4).setCellValue(ReportConstants.REPORTNOTE);

	}

	private void writeDetailsForSearchType(SXSSFSheet spreadsheet,
			String searchType, List<String> searchList, int rowValue,
			CellStyle dataRowStyle) {
		SXSSFRow row = null;
		row = (SXSSFRow) spreadsheet.createRow(rowValue);
		row.createCell(4).setCellValue(searchType);
		//		spreadsheet.autoSizeColumn(4);
		String completeList = getCompleteList(searchList);
		row.createCell(5).setCellValue(completeList);
		//		spreadsheet.autoSizeColumn(5);
	}

	private String getCompleteList(List<String> itemList) {
		if (itemList.size() == 0) {
			return "All";
		} else {
			return itemList.toString().replace("[", "").replace("]", "");
		}
	}
}
