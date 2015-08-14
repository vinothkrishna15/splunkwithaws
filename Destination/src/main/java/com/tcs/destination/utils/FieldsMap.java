package com.tcs.destination.utils;

import java.util.HashMap;
import java.util.Map;

public class FieldsMap {

	public static Map<String, String> fieldsMap = new HashMap<String, String>();
	public static Map<String, String> childMap = new HashMap<String, String>();
	
	static {
		fieldsMap.put("iou", "Iou");
		fieldsMap.put("geography", "Geography");
		fieldsMap.put("subSp", "SubSp");
		fieldsMap.put("offering", "Offering");
		fieldsMap.put("connectCategory", "Connect Category");
		fieldsMap.put("startDateOfConnect", "Start Date Of Connect");
		fieldsMap.put("endDateOfConnect", "End Date Of Connect");
		fieldsMap.put("country", "Country");
		fieldsMap.put("primaryOwner", "Primary Owner");
		fieldsMap.put("secondaryOwner", "secondary Owner");
		fieldsMap.put("customerOrPartnerName", "Customer/Partner Name");
		fieldsMap.put("tcsAccountContact", "Tcs Account Contact");
		fieldsMap.put("custContactName", "Customer Contact Name");
		fieldsMap.put("linkOpportunity", "Link Opportunity");
		fieldsMap.put("connectNotes", "Connect Notes");
		fieldsMap.put("customerName", "Customer Name");
		fieldsMap.put("digitalDealvalue", "Digital Deal value");
		fieldsMap.put("opportunityDescription", "Opportunity description");
		fieldsMap.put("oppLinkId", "Opportunity Link Id");
		fieldsMap.put("requestReceiveDate", "Opt Request Receive Date");
		fieldsMap.put("newLogo", "New Logo");
		fieldsMap.put("competitors", "Competitors");
		fieldsMap.put("partnershipsInvolved", "Partnerships involved");
		fieldsMap.put("dealType", "Deal Type");
		fieldsMap.put("salesSupportOwner", "Sales Support Owner");
		fieldsMap.put("dealRemarksNotes", "Deal Remarks Notes");
		fieldsMap.put("dealClosureDate", "Deal Closure date");
		fieldsMap.put("factorsForWinLoss", "Factors for Win/Loss");
		fieldsMap.put("descriptionForWinLoss", "Description for Win/Loss");
		fieldsMap.put("bidId", "Bid Id");
		fieldsMap.put("bidRequestType", "BID Request Type");
		fieldsMap.put("bidRequestReceiveDate", "BID Request Receive Date");
		fieldsMap.put("bidOfficeGroupOwner", "BID Office group Owner");
		fieldsMap.put("targetBidSubmissionDate", "Target BID Submission Date");
		fieldsMap.put("actualBidSubmissionDate", "Actual BID Submission Date");
		fieldsMap.put("expectedDateOfOutcome", "Expected Date of Outcome");
		fieldsMap.put("winProbability", "Win probability");
		fieldsMap.put("coreAttributesUsedForWinning", "Core attributes used for winning");
		fieldsMap.put("crmId", "CRM Id");
		fieldsMap.put("opportunityName", "Opportunity Name");
		fieldsMap.put("opportunityOwner", "Opportunity Owner");
		fieldsMap.put("projectDealValue", "Digital Deal Value(Project Currency)");
		fieldsMap.put("requestReceivedDate", "Request Received Date");
	}
	
	static {
		childMap.put("tcsAccountContact", "Contact Name");
		childMap.put("customerContactName", "Contact Name");
		childMap.put("linkOpportunity", "Opportunity Name");
		childMap.put("connectNotes", "Connect Updated Notes");
		childMap.put("secondaryOwner", "Name");
		childMap.put("competitors", "Name");
		childMap.put("partnershipsInvolved", "Partner Id");
		childMap.put("salesSupportOwner", "Owner Name");
		childMap.put("dealRemarksNotes", "Notes");
		childMap.put("factorsForWinLoss", "Win/Loss");
		childMap.put("opportunityLinkId", "Link Id");
		childMap.put("bidId", "Id");
		childMap.put("bidRequestType", "Type");
		childMap.put("bidRequestReceiveDate", "Request Receive Date");
		childMap.put("bidOfficeGroupOwner", "Office group Owner");
		childMap.put("targetBidSubmissionDate", "BID Submission Date");
		childMap.put("actualBidSubmissionDate", "BID Submission Date");
		childMap.put("expectedDateOfOutcome", "Date of Outcome");
		childMap.put("winProbability", "Win probability");
		childMap.put("coreAttributesUsedForWinning", "winning");
//		childMap.put("task", "Task");
	}
	
}