package com.tcs.destination.utils;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;

public class FieldNameMapper {

	private static final Map<String,String> FIELD_NAME_MAP;

	static {
		Map<String, String> map = Maps.newHashMap();
		map.put("Comments", "Comments");
		map.put("Status", "Status");
		map.put("GroupCustomerName", "Group Customer Name");
		map.put("CustomerName", "Customer Name");
		map.put("Website", "Website");
		map.put("Facebook", "Facebook");
		map.put("CorporateHqAddress", "Corporate Headquarters Address");
		map.put("Iou", "IOU");
		map.put("Remarks", "Remarks");
		map.put("Geography", "Geography");
		map.put("PartnerName", "Partner Name");
		map.put("Notes", "Notes");
		map.put("WorkflowCompetitorName", "Workflow Competitor Name");
		map.put("WorkflowCompetitorWebsite", "Workflow Competitor Website");
		map.put("WorkflowCompetitorNotes", "Workflow Competitor Notes");
		
		map.put("CrmId", "CRM ID");
		map.put("OpportunityName", "Opportunity Name");
		map.put("OpportunityDescription", "Opportunity Description");
		map.put("StrategicDeal", "Strategic Deal");
		map.put("DealCurrency", "Deal Currency");
		map.put("OverallDealSize", "Overall Deal Size");
		map.put("DigitalDealValue", "Digital Deal Value");
		map.put("OpportunityOwner", "Opportunity Owner");
		map.put("DealClosureDate", "Deal Closure Date");
		map.put("DescriptionForWinLoss", "Description For Win Loss");
		map.put("EngagementDuration", "Engagement Duration");
		map.put("SalesStageCode", "Sales Stage");
		map.put("DealType", "Deal Type");
		map.put("Country", "Country");
		map.put("DigitalFlag", "Digital Flag");
		map.put("IncumbentFlag", "Incumbent Flag");
		map.put("Competitor", "Competitor");
		map.put("CustomerContact", "Customer Contact");
		map.put("Offering", "Offering");
		map.put("Partner", "Partner");
		map.put("SalesSupport", "Sales Support Owner");
		map.put("SubspPrimary", "Sub SP Primary");
		map.put("SubSp", "Sub SP");
		map.put("TcsAccountContact", "TCS Contact");
		map.put("WinLossFactors", "Win Loss Factor");
		map.put("BidOfficeGroupOwner", "Bid Office Group Owner");
		map.put("BidRequestReceiveDate", "Bid Request Receive Date");
		map.put("TargetBidSubmissionDate", "Target Bid Submission Date");
		map.put("ActualBidSubmissionDate", "Actual Bid Submission Date");
		map.put("ExpectedDateOfOutcome", "Expected Date Of Outcome");
		map.put("WinProbability", "Win Probability");
		map.put("CoreAttributesUsedForWinning", "Core Attributes Used For Winning");
		map.put("BidRequestType", "Bid Request Type");
		
		map.put("ScheduledStartDate", "Scheduled Start Date");
		map.put("ActualStartDate", "Actual Start Date");
		map.put("ExpectedEndDate", "Expected End Date");
		map.put("WonNum", "WON");
		map.put("odc", "Offshore Developement Centre");
		map.put("DeliveryPartnerId", "Delivery Partner Emp ID");
		map.put("DeliveryPartnerName", "Delivery Partner Name");
		map.put("GlId", "GL Emp ID");
		map.put("GlName", "GL Name");
		map.put("PlId", "PL Emp ID");
		map.put("PlName", "PL Name");
		map.put("DeliveryCentreId", "Delivery Centre");
		map.put("DeliveryStage", "Engagement Stage");
		map.put("EngagementName", "Engagement Name");
		map.put("EmployeeId", "Employee Id");
		map.put("EmployeeName", "Employee Name");
		map.put("Role", "Role");
		map.put("Skill", "Skill Sets");
		map.put("RequirementFulfillment", "No. of Resources");
		map.put("DeliveryRgsId", "RGS ID");
		
		FIELD_NAME_MAP = Collections.unmodifiableMap(map);
	}
	
	public static String getFieldLabel(String fieldName) {
		if (fieldName != null) {
			if (FIELD_NAME_MAP.containsKey(fieldName))
				return FIELD_NAME_MAP.get(fieldName);
		}
		return fieldName;
	}
	
}
