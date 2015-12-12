package com.tcs.destination.service;

import java.util.ArrayList;
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
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.ReportConstants;

@Component
public class ConnectDetailedReportService {

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	TaskRepository taskRepository;

	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;
	
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
//		spreadSheet.setDefaultColumnWidth(30);
		row.createCell(0).setCellValue(ReportConstants.CONNECTID);
		row.getCell(0).setCellStyle(headerSyle);
//		spreadSheet.autoSizeColumn(0);
		row.createCell(1).setCellValue(ReportConstants.DISPLAYGEO);
		row.getCell(1).setCellStyle(headerSyle);
//		spreadSheet.autoSizeColumn(1);
		row.createCell(2).setCellValue(ReportConstants.DISPLAYSERVICELINE);
		row.getCell(2).setCellStyle(headerSyle);
//		spreadSheet.autoSizeColumn(2);
		row.createCell(3).setCellValue(ReportConstants.DISPLAYIOU);
		row.getCell(3).setCellStyle(headerSyle);
//		spreadSheet.autoSizeColumn(3);
		row.createCell(4).setCellValue(ReportConstants.CONNECTNAME);
		row.getCell(4).setCellStyle(headerSyle);
//		spreadSheet.autoSizeColumn(4);
		row.createCell(5).setCellValue(ReportConstants.GROUPCUSTOMERNAME);
		row.getCell(5).setCellStyle(headerSyle);
//		spreadSheet.autoSizeColumn(5);
	}

	public void createHeaderOptionalFields(List<ConnectT> connectList,
			SXSSFRow row, List<String> fields,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow) {
		// This method creates header for mandatory fields
		createHeaderForMandatoryFields(row, spreadSheet);
		int columnNo = 6;
		CellStyle headerStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADER);
		for (String field : fields) {
			switch (field) {
			case ReportConstants.TASK:
				row.createCell(columnNo).setCellValue(ReportConstants.TASKCOUNT);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.TASKID);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.TASKDESCRIPTION);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.ENTITYREFERENCE);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.TASKOWNER);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.TARGETDATEFORCOMPLETION);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.TASKSTATUS);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				row.createCell(columnNo).setCellValue(ReportConstants.TASKNOTE);
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				break;
			default:
				row.createCell(columnNo).setCellValue(FieldsMap.fieldsMap.get(field));
				row.getCell(columnNo).setCellStyle(headerStyle);
//				spreadSheet.autoSizeColumn(columnNo);
				columnNo++;
				break;
			}
		}
	}

	public int connectReportWithMandatoryFields(List<ConnectT> connectList,SXSSFSheet spreadSheet, int currentRow, SXSSFRow row) {
		for (ConnectT connect : connectList) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow + 1);
			getConnectReportMandatoryFields(spreadSheet, row, connect);
			currentRow++;
		}
//		spreadSheet.autoSizeColumn(4);
//		spreadSheet.autoSizeColumn(5);
		return currentRow;
	}

	public void getConnectReportMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, ConnectT connect) {
//		CellStyle rowStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(), ReportConstants.DATAROW);
		row.createCell(0).setCellValue(connect.getConnectId());
//		row.getCell(0).setCellStyle(rowStyle);
		row.createCell(4).setCellValue(connect.getConnectName());
//		row.getCell(4).setCellStyle(rowStyle);
		List<String> displaySubSpList = new ArrayList<String>();
		for (ConnectSubSpLinkT connectSubSpLinkT : connect.getConnectSubSpLinkTs()) {
			displaySubSpList.add(connectSubSpLinkT.getSubSpMappingT().getSubSp());
		}
		row.createCell(2).setCellValue(displaySubSpList.toString().replace("[", "").replace("]", ""));
//		row.getCell(2).setCellStyle(rowStyle);
		if(connect.getCustomerMasterT()!=null){
		row.createCell(1).setCellValue(connect.getCustomerMasterT().getGeographyMappingT().getDisplayGeography());
//		row.getCell(1).setCellStyle(rowStyle);
		
		row.createCell(3).setCellValue(connect.getCustomerMasterT().getIouCustomerMappingT().getDisplayIou());
//		row.getCell(3).setCellStyle(rowStyle);
		
//		spreadSheet.autoSizeColumn(4);
		row.createCell(5).setCellValue(connect.getCustomerMasterT().getGroupCustomerName());
//		row.getCell(5).setCellStyle(rowStyle);
//		spreadSheet.autoSizeColumn(5);
	}else{
		row.createCell(1).setCellValue(connect.getPartnerMasterT().getGeographyMappingT().getDisplayGeography());
//		row.getCell(1).setCellStyle(rowStyle);
		row.createCell(3).setCellValue(Constants.SPACE);
//		row.getCell(3).setCellStyle(rowStyle);
		row.createCell(5).setCellValue(Constants.SPACE);
//		row.getCell(5).setCellStyle(rowStyle);
	}
	}
	public int connectReportWithOptionalFields(List<ConnectT> connectList,
			SXSSFWorkbook workbook, SXSSFSheet spreadSheet, int currentRow,
			List<String> fields, SXSSFRow row)
			throws DestinationException {
//		CellStyle cellStyle = ExcelUtils.createRowStyle(workbook,
//				ReportConstants.DATAROW);
		
		currentRow = currentRow + 1;
		for (ConnectT connect : connectList) {

			List<TaskT> taskList = taskRepository.findByConnectId(connect
					.getConnectId());
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
			getConnectReportMandatoryFields(spreadSheet, row, connect);
			int colValue = 6;
			for (String field : fields) {
				switch (field) {
				case ReportConstants.IOU:
					SXSSFCell iouCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1)
							.createCell(colValue);
					if(connect.getCustomerMasterT()!=null){
					iouCell.setCellValue(connect.getCustomerMasterT()
							.getIouCustomerMappingT().getIou());
//					spreadSheet.autoSizeColumn(colValue);
					}else{
						iouCell.setCellValue(Constants.SPACE);
					}
//					iouCell.setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.GEOGRAPHY:
					SXSSFCell geographyCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1)
							.createCell(colValue);
					if(connect.getCustomerMasterT()!=null){
					geographyCell.setCellValue(connect.getCustomerMasterT()
							.getGeographyMappingT().getGeography());
					}else{
						geographyCell.setCellValue(connect.getPartnerMasterT()
								.getGeographyMappingT().getGeography());
					}
//					geographyCell.setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.SUBSP:
					List<String> subSpList = new ArrayList<String>();
					SXSSFCell subSpCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					for (ConnectSubSpLinkT connectSubSpLinkT : connect.getConnectSubSpLinkTs()) {
					subSpList.add(connectSubSpLinkT.getSubSpMappingT().getSubSp());
					}
					subSpCell.setCellValue(subSpList.toString().replace("[", "").replace("]", ""));
//					subSpCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.COUNTRY:
					SXSSFCell countryCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1)
							.createCell(colValue);
					countryCell.setCellValue(connect
							.getGeographyCountryMappingT().getCountry());
//					countryCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.OFFERING:
					List<String> offering = new ArrayList<String>();
					SXSSFCell offeringCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					for (ConnectOfferingLinkT connectOfferingLinkT : connect.getConnectOfferingLinkTs()) {
						offering.add(connectOfferingLinkT.getOfferingMappingT().getOffering());
					}
					offeringCell.setCellValue(offering.toString().replace("[", "").replace("]", ""));
//					offeringCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.CATEGORY:
					SXSSFCell categoryCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1)
							.createCell(colValue);
					categoryCell.setCellValue(connect.getConnectCategory());
//					categoryCell.setCellStyle(cellStyle);
					colValue++;
					break;
				case ReportConstants.STARTDATE:
					SXSSFCell startDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(
							currentRow - 1).createCell(colValue);
					startDateOfConnectCell.setCellValue(connect.getStartDatetimeOfConnect().toString());
//					startDateOfConnectCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.ENDDATE:
					SXSSFCell endDateOfConnectCell = (SXSSFCell) spreadSheet.getRow(
							currentRow - 1).createCell(colValue);
					endDateOfConnectCell.setCellValue(connect
							.getEndDatetimeOfConnect().toString());
//					endDateOfConnectCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.PRIMARYOWNER:
					SXSSFCell primaryOwnerCell = (SXSSFCell) spreadSheet.getRow(
							currentRow - 1).createCell(colValue);
					UserT userT = userRepository.findByUserId(connect
							.getPrimaryOwner());
					primaryOwnerCell.setCellValue(userT.getUserName());
//					primaryOwnerCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.SECONDARYOWNER:
					List<String> secondaryOwnersList=connectRepository.getSecondaryOwnerByConnectId(connect.getConnectId());
					List<String> secondaryOwners=new ArrayList<String>();
					SXSSFCell secondaryOwnerCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					if(!secondaryOwnersList.isEmpty()){
					for (String secondaryOwner : secondaryOwnersList) {
						if(secondaryOwner!=null){
						UserT user = userRepository.findByUserId(secondaryOwner);
						secondaryOwners.add(user.getUserName());
						}
					}
						secondaryOwnerCell.setCellValue(secondaryOwners.toString().replace("[", "").replace("]", ""));
					}else{
						secondaryOwnerCell.setCellValue(Constants.SPACE);
					}
//						secondaryOwnerCell.setCellStyle(cellStyle);
//						spreadSheet.autoSizeColumn(colValue);
//					}
					colValue++;
					break;
				case ReportConstants.CUSTOMERORPARTNERNAME:
					if (connect.getCustomerMasterT() != null) {
						SXSSFCell cusPartcell = (SXSSFCell) spreadSheet.getRow(
								currentRow - 1).createCell(colValue);
						cusPartcell.setCellValue(connect.getCustomerMasterT()
								.getCustomerName());
//						cusPartcell.setCellStyle(cellStyle);
//						spreadSheet.autoSizeColumn(colValue);
						colValue++;
					} else if (connect.getPartnerMasterT() != null) {
						SXSSFCell cusPartcell = (SXSSFCell) spreadSheet.getRow(
								currentRow - 1).createCell(colValue);
						cusPartcell.setCellValue(connect.getPartnerMasterT()
								.getPartnerName());
//						cusPartcell.setCellStyle(cellStyle);
//						spreadSheet.autoSizeColumn(colValue);
						colValue++;
					} else {
						colValue++;
					}
					break;
				case ReportConstants.TCSACCOUNTCONTACT:
					List<String> tcsContactNames=new ArrayList<String>();
					SXSSFCell tcsAccountContactCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					for (ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT : connect.getConnectTcsAccountContactLinkTs()) {
						tcsContactNames.add(connectTcsAccountContactLinkT.getContactT().getContactName());
					}
					tcsAccountContactCell.setCellValue(tcsContactNames.toString().replace("[", "").replace("]", ""));
//					tcsAccountContactCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.CUSTOMERCONTACTNAME:
					List<String> cusContactNames=new ArrayList<String>();
					SXSSFCell customerContactNameCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					for (ConnectCustomerContactLinkT connectCustomerContactLinkT : connect.getConnectCustomerContactLinkTs()) {
						cusContactNames.add(connectCustomerContactLinkT.getContactT().getContactName());
					}
						customerContactNameCell.setCellValue(cusContactNames.toString().replace("[", "").replace("]", ""));
//						customerContactNameCell.setCellStyle(cellStyle);
//						spreadSheet.autoSizeColumn(colValue);
						colValue++;
					break;
				case ReportConstants.LINKOPPORTUNITY:
					List<String> opportunityNames=new ArrayList<String>();
					SXSSFCell opportunityCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : connect.getConnectOpportunityLinkIdTs()) {
						opportunityNames.add(connectOpportunityLinkIdT.getOpportunityT().getOpportunityName());
					}
						opportunityCell.setCellValue(opportunityNames.toString().replace("[", "").replace("]", ""));
//						opportunityCell.setCellStyle(cellStyle);
//						spreadSheet.autoSizeColumn(colValue);
						colValue++;
					break;
				case ReportConstants.CONNECTNOTES:
					List<String> connectNotesList=new ArrayList<String>();
					SXSSFCell connectNotesCell = (SXSSFCell) spreadSheet.getRow(
							currentRow - 1).createCell(colValue);
					for (NotesT notes : connect.getNotesTs()) {
						connectNotesList.add(notes.getNotesUpdated());
					}
					connectNotesCell.setCellValue(connectNotesList.toString().replace("[", "").replace("]", ""));
//					connectNotesCell.setCellStyle(cellStyle);
//					spreadSheet.autoSizeColumn(colValue);
					colValue++;
					break;
				case ReportConstants.TASK:
					SXSSFCell taskCell = (SXSSFCell) spreadSheet.getRow(currentRow - 1).createCell(colValue);
					taskCell.setCellValue(taskList.size());
//					taskCell.setCellStyle(cellStyle);
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
					break;
				default:
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"field name" + field + "does not exist");
				}
			}
			if (taskList.size() > 1) {
				currentRow = currentRow + taskList.size() - 1;
			} else {
				currentRow = currentRow + 0;
			}
		}
//		int lastCol = row.getLastCellNum();
//		for(int startCol=0;startCol<lastCol;startCol++){
//			spreadSheet.autoSizeColumn(startCol);
//		}
		return currentRow;

	}

	public int getTaskDetails(SXSSFSheet spreadSheet, SXSSFRow row,
			List<TaskT> taskList, int columnNo, int i) {
//		CellStyle cellStyle = ExcelUtils.createRowStyle(spreadSheet.getWorkbook(),
//				ReportConstants.DATAROW);
		int columnOffset=1;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTaskId());
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(columnNo+columnOffset);
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTaskDescription());
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(columnNo+columnOffset);
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getEntityReference());
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(columnNo+columnOffset);
		columnOffset++;
		UserT taskOwnerName = userRepository.findByUserId(taskList.get(i).getTaskOwner());
		row.createCell(columnNo+columnOffset).setCellValue(taskOwnerName.getUserName());
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(columnNo+columnOffset);
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTargetDateForCompletion().toString());
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(columnNo+columnOffset);
		columnOffset++;
		row.createCell(columnNo+columnOffset).setCellValue(taskList.get(i).getTaskStatus());
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
//		spreadSheet.autoSizeColumn(columnNo+columnOffset);
		columnOffset++;
		List<String> taskNotesUpdatedList=new ArrayList<String>();
		for (NotesT notes : taskList.get(i).getNotesTs()) {
		taskNotesUpdatedList.add(notes.getNotesUpdated());
		}
		row.createCell(columnNo+columnOffset).setCellValue(taskNotesUpdatedList.toString().replace("[", "").replace("]", ""));
//		row.getCell(columnNo+columnOffset).setCellStyle(cellStyle);
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
//		spreadsheet.autoSizeColumn(4);
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
//		spreadsheet.autoSizeColumn(4);
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
//		spreadsheet.autoSizeColumn(4);
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
