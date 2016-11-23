package com.tcs.destination.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.AuditDeliveryMasterT;
import com.tcs.destination.bean.DeliveryIntimatedT;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.DeliveryResourcesT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.AuditDeliveryMasterRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryMasterManagerLinkRepository;
import com.tcs.destination.data.repository.DeliveryStageMappingRepository;
import com.tcs.destination.data.repository.OpportunityOfferingLinkTRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.DeliveryStage;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.FieldsMap;
import com.tcs.destination.utils.ReportConstants;

/*
 * This service handles the Delivery report functionalities
 */
@Component
public class BuildDeliveryReport {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportsService.class);

	@Autowired
	private OpportunitySubSpLinkTRepository opportunitySubSpLinkTRepository;

	@Autowired
	private DeliveryMasterManagerLinkRepository deliveryMasterManagerLinkRepository;

	@Autowired
	private OpportunityOfferingLinkTRepository opportunityOfferingLinkTRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private PartnerRepository partnerRepository;

	@Autowired
	private AuditDeliveryMasterRepository auditDeliveryMasterRepository;

	@Autowired
	private DeliveryStageMappingRepository deliveryStageMappingRepository;

	@Autowired
	private UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DeliveryCentreRepository deliveryCentreRepository;

	/**
	 * Gets the delivery report with all the mandatory fields
	 * 
	 * @param spreadSheet
	 * @param row
	 * @param deliveryMasterT
	 */

	private void getDeliveryReportWithMandatoryFields(SXSSFSheet spreadSheet,
			SXSSFRow row, DeliveryMasterT deliveryMasterT) {
		int colNo = 0;
		CellStyle cellStyleDateTimeFormat = spreadSheet.getWorkbook()
				.createCellStyle();
		CellStyle cellStyleDateFormat = spreadSheet.getWorkbook()
				.createCellStyle();
		CreationHelper createHelper = spreadSheet.getWorkbook()
				.getCreationHelper();
		cellStyleDateTimeFormat.setDataFormat(createHelper.createDataFormat()
				.getFormat("mm/dd/yyyy hh:mm"));
		cellStyleDateFormat.setDataFormat(createHelper.createDataFormat()
				.getFormat("mm/dd/yyyy"));

		row.createCell(colNo).setCellValue(
				deliveryMasterT.getDeliveryMasterId());// set engagement id
		colNo++;
		String engagementName = deliveryMasterT.getEngagementName();
		if (engagementName != null) {
			row.createCell(colNo).setCellValue(engagementName);// set engagement
																// name
		}
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getDeliveryStageMappingT()
						.getDescription(), Constants.EMPTY_STRING));// set
																	// engagement
																	// stage
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOpportunityId(),
						Constants.EMPTY_STRING));// set opportunity id
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOpportunityT()
						.getCustomerMasterT().getCustomerName(),
						Constants.EMPTY_STRING));// set customer name
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOpportunityT()
						.getCountry(), Constants.EMPTY_STRING));// set country
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOpportunityT()
						.getOpportunityName(), Constants.EMPTY_STRING));// set
																		// opportunity
																		// name
		colNo++;
		String oppPrimarySubSp = opportunitySubSpLinkTRepository
				.findPrimaryDisplaySubSpByOpportunityId(deliveryMasterT
						.getOpportunityT().getOpportunityId());
		if (oppPrimarySubSp != null) {
			row.createCell(colNo).setCellValue(oppPrimarySubSp);// set primary
																// subsp
		}
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOpportunityT()
						.getOpportunityDescription(), Constants.EMPTY_STRING));// set
																				// opportunity
																				// description
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOpportunityT()
						.getDeliveryOwnershipT().getOwnership(),
						Constants.EMPTY_STRING));// set delivery ownership
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getDeliveryCentreT()
						.getDeliveryCentre(), Constants.EMPTY_STRING));// set
																		// delivery
																		// center
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getDeliveryCentreT()
						.getDeliveryClusterT().getDeliveryClusterHeadUser()
						.getUserName(), Constants.EMPTY_STRING));// set delivery
																	// cluster
																	// head
		colNo++;
		if (StringUtils.isNotEmpty(deliveryMasterT.getDeliveryCentreT()
				.getDeliveryCentreHead())) {
			row.createCell(colNo).setCellValue(
					defaultIfEmptyString(deliveryMasterT.getDeliveryCentreT()
							.getDeliveryCentreHeadUser().getUserName(),
							Constants.EMPTY_STRING));// set delivery centre head
		}
		colNo++;

		List<String> deliveryManagers = deliveryMasterManagerLinkRepository
				.getDeliveryManagersByEngagementId(deliveryMasterT
						.getDeliveryMasterId());
		if (CollectionUtils.isNotEmpty(deliveryManagers)) {
			row.createCell(colNo)
					.setCellValue(
							ExcelUtils
									.removeSquareBracesAndAppendListElementsAsString(deliveryManagers));// set
																										// delivery
																										// managers
			
		}
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getDeliveryPartnerName(),
						Constants.EMPTY_STRING));// set delivery partner name
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getDeliveryPartnerId(),
						Constants.EMPTY_STRING));// set delivery partner id
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getWonNum(),
						Constants.EMPTY_STRING));// set won number
		colNo++;
		if (deliveryMasterT.getScheduledStartDate() != null) {
			row.createCell(colNo).setCellValue(
					deliveryMasterT.getScheduledStartDate());// set scheduled
																// start date
																// todo
			row.getCell(colNo).setCellStyle(cellStyleDateFormat);
		}
		colNo++;

		if (deliveryMasterT.getExpectedEndDate() != null) {
			row.createCell(colNo).setCellValue(
					deliveryMasterT.getExpectedEndDate());// set expected end
															// date
			row.getCell(colNo).setCellStyle(cellStyleDateFormat);
		}
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getOdc(),
						Constants.EMPTY_STRING));// set offshore delivery centre
		colNo++;

		// list of rgs ids
		List<String> rgsIds = Lists.newArrayList();
		List<DeliveryResourcesT> deliveryResourcesTs = deliveryMasterT
				.getDeliveryResourcesTs();
		int open = 0, joined = 0, total = 0;
		for (DeliveryResourcesT deliveryResourcesT : deliveryResourcesTs) {
			String rgsId = deliveryResourcesT.getRgsId();
			rgsIds.add(rgsId);
			if (deliveryResourcesT.getDeliveryRgsT() != null) {
				List<DeliveryRequirementT> deliveryRequirementTs = deliveryResourcesT
						.getDeliveryRgsT().getDeliveryRequirementTs();
				if (CollectionUtils.isNotEmpty(deliveryRequirementTs)) {
					for (DeliveryRequirementT deliveryRequirementT : deliveryRequirementTs) {
						if (StringUtils.equalsIgnoreCase("Open",
								deliveryRequirementT.getStatus())) {
							open++;
						} else if (StringUtils.equalsIgnoreCase("closed",
								deliveryRequirementT.getStatus())) {
							joined++;
						}
					}
				}
			}
		}

		row.createCell(colNo)
				.setCellValue(
						ExcelUtils
								.removeSquareBracesAndAppendListElementsAsString(rgsIds));// set
																							// total
																							// resources
		colNo++;

		// TODO total resources calculation
		row.createCell(colNo).setCellValue(total);// set joined resources
		colNo++;

		row.createCell(colNo).setCellValue(joined);// set joined resources
		colNo++;

		row.createCell(colNo).setCellValue(open); // set no. of open positions
		colNo++;

		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getGlName(),
						Constants.EMPTY_STRING));// set GL name
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getGlId(),
						Constants.EMPTY_STRING)); // set GL employee id
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getPlName(),
						Constants.EMPTY_STRING)); // set PL name
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getPlId(),
						Constants.EMPTY_STRING)); // set PL employee id
		colNo++;
		if (deliveryMasterT.getActualStartDate() != null) {
			row.createCell(colNo).setCellValue(
					deliveryMasterT.getActualStartDate()); // set Actual start
															// date
			row.getCell(colNo).setCellStyle(cellStyleDateFormat);
		}
		colNo++;
		row.createCell(colNo).setCellValue(
				defaultIfEmptyString(deliveryMasterT.getModifiedBy(),
						Constants.EMPTY_STRING)); // set Modified By
		colNo++;

		if (deliveryMasterT.getModifiedDatetime() != null) {
			row.createCell(colNo).setCellValue(
					deliveryMasterT.getModifiedDatetime()); // set modified date
			row.getCell(colNo).setCellStyle(cellStyleDateTimeFormat);
		}
		colNo++;

	}

	/**
	 * This method is used for setting the delivery title page fields to excel
	 * 
	 * @param workbook
	 * @param iou
	 * @param serviceLines
	 * @param user
	 * @param country
	 * @param deliveryStage
	 * @param deliveryCentres
	 * @param detailed
	 * 
	 */
	public void setDeliveryTitlePage(SXSSFWorkbook workbook, List<String> iou,
			List<String> serviceLines, UserT user, List<String> geography,
			List<String> country, List<Integer> deliveryStage,
			List<Integer> deliveryCentres, String detailed) {
		logger.info(" Inside setDeliveryTitlePage method");

		String tillDate = DateUtils.getCurrentDate();
		CellStyle headinStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.REPORTHEADINGSTYLE);
		CellStyle subHeadingStyle = ExcelUtils.createRowStyle(workbook,
				ReportConstants.ROWS);
		CellStyle dataRow = ExcelUtils.createRowStyle(workbook,
				ReportConstants.DATAROW);
		String completeList = null;
		SXSSFRow row = null;
		SXSSFSheet spreadsheet = (SXSSFSheet) workbook
				.createSheet(ReportConstants.TITLE);

		row = (SXSSFRow) spreadsheet.createRow(4);
		spreadsheet.addMergedRegion(new CellRangeAddress(4, 4, 4, 7));
		row.createCell(4).setCellValue("Delivery report as on " + tillDate);
		row.getCell(4).setCellStyle(headinStyle);
		row = (SXSSFRow) spreadsheet.createRow(6);
		row.createCell(4).setCellValue("User Selection Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);

		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Geography",
				geography, 8, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, Constants.IOU, iou,
				9, dataRow);
		ExcelUtils.writeDetailsForSearchType(spreadsheet, "Service Line",
				serviceLines, 10, dataRow);

		row = (SXSSFRow) spreadsheet.createRow(11);
		if (deliveryStage.contains(-1)) {
			completeList = ReportConstants.All;
		} else {
			List<String> deliveryStageDescription = deliveryStageMappingRepository
					.findDeliveryStageDescriptionByDeliveryStageId(deliveryStage);
			completeList = joinString(deliveryStageDescription, Constants.COMMA);
		}
		row.createCell(4).setCellValue("Delivery stage");
		row.createCell(5).setCellValue(completeList);
		
		row = (SXSSFRow) spreadsheet.createRow(12);
		if (deliveryCentres.contains(-2)) {
			completeList = ReportConstants.All;
		} else {
			List<String> deliveryCentreNames = deliveryCentreRepository
					.findDeliveryCentreNamesByIds(deliveryCentres);
			completeList = joinString(deliveryCentreNames, Constants.COMMA);
		}
		row.createCell(4).setCellValue("Delivery Centres");
		row.createCell(5).setCellValue(completeList);

		String userGroup = user.getUserGroup();
		row = (SXSSFRow) spreadsheet.createRow(14);
		row.createCell(4).setCellValue("User Access Filter's");
		row.getCell(4).setCellStyle(subHeadingStyle);
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {

		case DELIVERY_MANAGER:
			ExcelUtils
					.writeUserFilterConditions(
							spreadsheet,
							user,
							ReportConstants.DELIVERY_DELOPPWHEREUSERPRIMARYORSALESOWNER);
			break;

		case DELIVERY_CLUSTER_HEAD:
		case DELIVERY_CENTRE_HEAD:
			ExcelUtils
					.writeUserFilterConditions(
							spreadsheet,
							user,
							ReportConstants.DELIVERY_OPPWHEREDELVTEAMPRIMARYORSALESOWNER);
			break;

		case STRATEGIC_INITIATIVES:
			ExcelUtils.writeUserFilterConditions(spreadsheet, user,
					ReportConstants.FULLACCESS);
			break;

		default:
			logger.error("User dont have access to view this report");
			throw new DestinationException(HttpStatus.UNAUTHORIZED,
					"User dont have access to view this report");
		}

		row = (SXSSFRow) spreadsheet.createRow(21);
		row.createCell(4).setCellValue(ReportConstants.DISPLAYPREFERENCE);
		row.getCell(4).setCellStyle(subHeadingStyle);
		row = (SXSSFRow) spreadsheet.createRow(22);
		row.createCell(4).setCellValue(ReportConstants.DETAILED);
		row.createCell(5).setCellValue(detailed);
		row = (SXSSFRow) spreadsheet.createRow(23);

	}

	private void createHeaderDeliveryReportOptionalFields(SXSSFRow headerRow,
			List<String> fields, SXSSFWorkbook workbook,
			SXSSFSheet spreadSheet, int currentRow) {

		CellStyle cellStyle = ExcelUtils.createRowStyle(
				(SXSSFWorkbook) spreadSheet.getWorkbook(),
				ReportConstants.REPORTHEADER);

		/**
		 * This method creates default headers
		 */
		getMandatoryHeaderForDeliveryReport(headerRow, spreadSheet, cellStyle);
		int colNo = 31;
		createHeaderForOptionalFields(headerRow, spreadSheet, cellStyle,
				fields, colNo);
	}

	/**
	 * Method creates the header for optional fields given
	 * 
	 * @param row
	 * @param spreadSheet
	 * @param cellStyle
	 * @param fields
	 * @param colNo
	 */
	private void createHeaderForOptionalFields(SXSSFRow row,
			SXSSFSheet spreadSheet, CellStyle cellStyle, List<String> fields,
			int colNo) {

		List<String> orderedFields = Arrays.asList("groupCustomerName",
				"geography", "crmId", "iou", "secondarySubSp", "offering",
				"tcsAccountContact", "custContactName", "newLogo",
				"partnershipsInvolved", "dealType", "engagementStartDate",
				"engagementDuration", "digitalFlag", "strategicDeal",
				"intimatedOn", "acceptedOn", "assignedOn", "plannedOn",
				"liveOn");
		for (String field : orderedFields) {
			if (CollectionUtils.isNotEmpty(fields)) {
				if (fields.contains(field)) {
					row.createCell(colNo).setCellValue(
							FieldsMap.fieldsMap.get(field));
					row.getCell(colNo).setCellStyle(cellStyle);
					colNo++;
				}
			}
		}
	}

	/**
	 * Gets the Detailed delivery report with all the mandatory fields and the
	 * optional fields given
	 * 
	 * @param deliveryMasterTs
	 * @param fields
	 * @param workbook
	 */
	public void getDeliveryDetailedReport(
			List<DeliveryMasterT> deliveryMasterTs, List<String> fields,
			SXSSFWorkbook workbook) {
		logger.info("Inside getDeliveryDetailedReport method");
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook
				.createSheet(ReportConstants.COMPLETEDATA);
		CellStyle cellStyle = ExcelUtils.createRowStyle(
				(SXSSFWorkbook) spreadSheet.getWorkbook(),
				ReportConstants.REPORTHEADER);
		SXSSFRow headerRow = null;
		int currentRow = 0;
		int headerColumnNo = 0;
		headerRow = (SXSSFRow) spreadSheet.createRow((short) currentRow);
		if (fields.size() == 0 && fields.isEmpty()) {
			getMandatoryHeaderForDeliveryReport(headerRow, spreadSheet,
					cellStyle);
			currentRow++;
			getDeliveryReportWithMandatoryFields(deliveryMasterTs, spreadSheet,
					headerRow, currentRow);
		} else {
			createHeaderDeliveryReportOptionalFields(headerRow, fields,
					workbook, spreadSheet, currentRow);
			getDeliveryReportWithOptionalFields(deliveryMasterTs, headerRow,
					spreadSheet, currentRow, fields, headerRow, headerColumnNo);
		}

	}

	private void getDeliveryReportWithOptionalFields(
			List<DeliveryMasterT> deliveryMasterTs, SXSSFRow headerRow,
			SXSSFSheet spreadSheet, int currentRow, List<String> fields,
			SXSSFRow row, int headerColumnNo) {
		logger.debug("Inside getDeliveryReportWithOptionalFields method");
		boolean grpCustNameFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_GROUP_CUSTOMER_NAME);
		boolean geographyFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_GEOGRAPHY);
		boolean crmIdFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_CRM_ID);
		boolean iouFlag = fields.contains(ReportConstants.FIELD_DELIVERY_IOU);
		boolean secondarySubSpFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_SECONDARY_SUB_SP);
		boolean offeringFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_OFFERING);
		boolean tcsAccConFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_TCS_CONTACT);
		boolean custConNameFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_CUSTOMER_CONTACT);
		boolean newLogoFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_NEW_LOGO);
		boolean partnershipInvolvedFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_PARTNERSHIP_INVOLVED);
		boolean dealTypeFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_DEAL_TYPE);
		boolean engagementStartDateFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_ENGAGEMENT_START_DATE);
		boolean engagementDurationFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_ENGAGEMENT_DURATION);
		boolean digitalFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_DIGITAL_REIMAGINATION);
		boolean strategicDealFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_STRATEGIC_DEAL);
		boolean intimatedOnFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_INTIMATED_ON);
		boolean acceptedOnFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_ACCEPTED_ON);
		boolean assignedOnFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_ASSIGNED_ON);
		boolean plannedOnFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_PLANNED_ON);
		boolean liveOnFlag = fields
				.contains(ReportConstants.FIELD_DELIVERY_LIVE_ON);

		CellStyle cellStyleDateTimeFormat = spreadSheet.getWorkbook()
				.createCellStyle();
		CellStyle cellStyleDateFormat = spreadSheet.getWorkbook()
				.createCellStyle();
		CreationHelper createHelper = spreadSheet.getWorkbook()
				.getCreationHelper();
		cellStyleDateTimeFormat.setDataFormat(createHelper.createDataFormat()
				.getFormat("mm/dd/yyyy hh:mm"));
		cellStyleDateFormat.setDataFormat(createHelper.createDataFormat()
				.getFormat("mm/dd/yyyy"));

		currentRow = currentRow + 1;
		for (DeliveryMasterT deliveryMaster : deliveryMasterTs) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow++);
			getDeliveryReportWithMandatoryFields(spreadSheet, row,
					deliveryMaster);
			int colValue = 31;
			OpportunityT deliveryOpportunity = deliveryMaster.getOpportunityT();
			if (grpCustNameFlag) {
				row.createCell(colValue).setCellValue(
						deliveryOpportunity.getCustomerMasterT()
								.getGroupCustomerName());
				colValue++;
			}
			if (geographyFlag) {
				row.createCell(colValue)
						.setCellValue(
								deliveryOpportunity.getCustomerMasterT()
										.getGeography());
				colValue++;
			}
			if (crmIdFlag) {
				row.createCell(colValue).setCellValue(
						deliveryOpportunity.getCrmId());
				colValue++;
			}
			if (iouFlag) {
				row.createCell(colValue).setCellValue(
						deliveryOpportunity.getCustomerMasterT().getIou());
				colValue++;
			}
			if (secondarySubSpFlag) {
				List<String> oppSecondarysubSpList = (opportunitySubSpLinkTRepository
						.findSecondarySubSpByOpportunityId(deliveryMaster
								.getOpportunityId()));
				if (CollectionUtils.isNotEmpty(oppSecondarysubSpList)) {
					row.createCell(colValue).setCellValue(
							joinString(oppSecondarysubSpList, Constants.COMMA));
				}

				colValue++;
			}
			if (offeringFlag) {
				List<String> oppOfferingList = opportunityOfferingLinkTRepository
						.findOfferingByOpportunityId(deliveryMaster
								.getOpportunityId());
				if (CollectionUtils.isNotEmpty(oppOfferingList)) {
					row.createCell(colValue).setCellValue(
							joinString(oppOfferingList, Constants.COMMA));
				}
				colValue++;
			}
			if (tcsAccConFlag) {
				List<String> oppTcsAccountContactList = contactRepository
						.findTcsAccountContactNamesByOpportinityId(deliveryMaster
								.getOpportunityId());
				if (CollectionUtils.isNotEmpty(oppTcsAccountContactList)) {
					row.createCell(colValue).setCellValue(
							joinString(oppTcsAccountContactList,
									Constants.COMMA));
				}
				colValue++;
			}
			if (custConNameFlag) {
				List<String> oppCustomerContactNameList = contactRepository
						.findCustomerContactNamesByOpportinityId(deliveryMaster
								.getOpportunityId());
				if (CollectionUtils.isNotEmpty(oppCustomerContactNameList)) {
					row.createCell(colValue).setCellValue(
							joinString(oppCustomerContactNameList,
									Constants.COMMA));
				}
				colValue++;
			}
			if (newLogoFlag) {
				String newLogo = deliveryOpportunity.getNewLogo();
				if (StringUtils.isNotEmpty(newLogo))
					row.createCell(colValue).setCellValue(newLogo);
				colValue++;
			}
			if (partnershipInvolvedFlag) {
				List<String> oppPartnershipsInvolvedList = partnerRepository
						.findPartnerNameByOpportunityId(deliveryMaster
								.getOpportunityId());
				if (CollectionUtils.isNotEmpty(oppPartnershipsInvolvedList)) {
					row.createCell(colValue).setCellValue(
							joinString(oppPartnershipsInvolvedList,
									Constants.COMMA));
				}
				colValue++;
			}
			if (dealTypeFlag) {
				String dealType = deliveryOpportunity.getDealType();
				if (StringUtils.isNotEmpty(dealType))
					row.createCell(colValue).setCellValue(dealType);
				colValue++;
			}
			if (engagementStartDateFlag) {
				if (deliveryOpportunity.getEngagementStartDate() != null) {
					row.createCell(colValue).setCellValue(
							deliveryOpportunity.getEngagementStartDate());
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}
			if (engagementDurationFlag) {
				if (deliveryOpportunity.getEngagementDuration() != null) {
					row.createCell(colValue).setCellValue(
							deliveryOpportunity.getEngagementDuration()
									.intValue());
				}
				colValue++;
			}

			if (digitalFlag) {
				if (StringUtils
						.isNotEmpty(deliveryOpportunity.getDigitalFlag())) {
					row.createCell(colValue).setCellValue(
							deliveryOpportunity.getDigitalFlag());
				}
				colValue++;
			}
			if (strategicDealFlag) {
				if (StringUtils.isNotEmpty(deliveryOpportunity
						.getStrategicDeal())) {
					row.createCell(colValue).setCellValue(
							deliveryOpportunity.getStrategicDeal());
				}
				colValue++;
			}
			Date intimatedOn = null;
			Date acceptedOn = null;
			Date assignedOn = null;
			Date plannedOn = null;
			Date liveOn = null;
			List<AuditDeliveryMasterT> auditDeliveryMasterTs = auditDeliveryMasterRepository
					.getDeliveryCodeChanges(deliveryMaster
							.getDeliveryMasterId());
			for (AuditDeliveryMasterT auditDeliveryMaster : auditDeliveryMasterTs) {
				switch (DeliveryStage.byStageCode(auditDeliveryMaster
						.getNewDeliveryStage())) {
				case ACCEPTED:
					acceptedOn = DateUtils.toDate(auditDeliveryMaster
							.getCreatedModifiedDatetime());
					break;
				case ASSIGNED:
					assignedOn = DateUtils.toDate(auditDeliveryMaster
							.getCreatedModifiedDatetime());
					break;
				case PLANNED:
					plannedOn = DateUtils.toDate(auditDeliveryMaster
							.getCreatedModifiedDatetime());
					break;
				case LIVE:
					liveOn = DateUtils.toDate(auditDeliveryMaster
							.getCreatedModifiedDatetime());
					break;
				default:
					break;
				}
				
			}
				
			if (intimatedOnFlag) {
				DeliveryIntimatedT deliveryIntimatedT = deliveryMaster.getDeliveryIntimatedT();
				if(deliveryIntimatedT!=null) {
					intimatedOn = DateUtils.toDate(deliveryIntimatedT.getCreatedDatetime());
					if (intimatedOn != null) {
						row.createCell(colValue).setCellValue(intimatedOn);
						row.getCell(colValue).setCellStyle(cellStyleDateFormat);
					}
				}
				colValue++;
			}
			if (acceptedOnFlag) {
				if (acceptedOn != null) {
					row.createCell(colValue).setCellValue(acceptedOn);
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}
			if (assignedOnFlag) {
				if (assignedOn != null) {
					row.createCell(colValue).setCellValue(assignedOn);
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}
			if (plannedOnFlag) {
				if (plannedOn != null) {
					row.createCell(colValue).setCellValue(plannedOn);
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}
			if (liveOnFlag) {
				if (liveOn != null) {
					row.createCell(colValue).setCellValue(liveOn);
					row.getCell(colValue).setCellStyle(cellStyleDateFormat);
				}
				colValue++;
			}

		}
	}

	private String joinString(List<String> items, String separator) {
		return StringUtils.join(items, separator);
	}

	/**
	 * 
	 * @param headerRow
	 * @param spreadSheet
	 * @param cellStyle
	 */
	private void getMandatoryHeaderForDeliveryReport(SXSSFRow headerRow,
			SXSSFSheet spreadSheet, CellStyle cellStyle) {
		int colNo = 0;
		List<String> headerFields = Arrays.asList("Engagement Id",
				"Engagement Name", "Engagement Stage", "Opportunity ID",
				"Customer Name", "Country", "Opportunity Name",
				"Primary Service Line", "Opportunity Description",
				"Delivery Ownership", "Delivery Centre",
				"Delivery Cluster Head", "Delivery Centre Head",
				"Delivery Manager", "Delivery Partner Name",
				"Delivery Partner Emp ID", "WON", "Scheduled Start Date",
				"Expected End Date", "Offshore Delivery Centre", "RGS ID(s)",
				"Total Resources", "No of Resources Joined",
				"No.of Open positions", "GL NAME", "GL Emp ID", "PL NAME",
				"PL Emp ID", "Actual Start Date", "Modified by",
				"Modified Date");
		for (String header : headerFields) {
			headerRow.createCell(colNo).setCellValue(header);
			headerRow.getCell(colNo++).setCellStyle(cellStyle);
		}
	}

	private void getDeliveryReportWithMandatoryFields(
			List<DeliveryMasterT> deliveryMasterTs, SXSSFSheet spreadSheet,
			SXSSFRow row, int currentRow) {
		logger.debug("Inside getDeliveryReportWithMandatoryFields method");
		for (DeliveryMasterT deliveryMasterT : deliveryMasterTs) {
			row = (SXSSFRow) spreadSheet.createRow((short) currentRow);
			getDeliveryReportWithMandatoryFields(spreadSheet, row,
					deliveryMasterT);
			currentRow++;
		}
	}

	private String defaultIfEmptyString(String value, String defaultValue) {
		return StringUtils.defaultIfEmpty(value, defaultValue);
	}

}
