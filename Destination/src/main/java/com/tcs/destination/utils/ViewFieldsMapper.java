package com.tcs.destination.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewFieldsMapper {

	private static final Logger logger = LoggerFactory.getLogger(ViewFieldsMapper.class);
	private static final Map<String,String>VIEW_FIELD_MAP;// = new HashMap<String,String>();

	static {
		Map<String, String> map = new HashMap<String, String>();
		map.put("timeline", "connectT,opportunityT,commentId,updatedDatetime,taskId,taskDescription,connectId,connectName,collaborationCommentTs,comments,collaborationCommentT,startDatetimeOfConnect,endDatetimeOfConnect,connectSecondaryOwnerLinkTs,ConnectSecondaryOwnerLinkT,secondaryOwner,customerMasterT,primaryOwner,customerId,customerName,opportunityId,opportunityName,opportunityDescription,salesStageMappingT,salesStageCode,userT,opportunitySalesSupportLinkTs,OpportunitySalesSupportLinkT,bidDetailsTs,bidOfficeGroupOwnerLinkTs,bid_office_group_owner_link_id,bidOfficeGroupOwner,targetDateForCompletion");
		map.put("topopps", "opportunityId,opportunityName,opportunityDescription");
		map.put("oppreport", "customerMasterT,country,customerName,groupCustomerName,iou,iouCustomerMappingT,displayIou,geography,geographyMappingT,displayGeography,country,opportunityCustomerContactLinkTs,contactId,contactT,contactName,opportunitySubSpLinkTs,subSp,subSpMappingT,displaySubSp,opportunityId,salesStageCode,digitalDealValue,opportunityName,opportunityDescription,opportunityRequestReceiveDate,newLogo,dealType,opportunityOwner,dealClosureDate,dealClosureComments,opportunityPartnerLinkTs,partnerId,partnerMasterT,partnerName,opportunitySalesSupportLinkTs,salesSupportOwner,opportunityOfferingLinkTs,offering,opportunityCompetitorLinkTs,competitorName,opportunityWinLossFactorsTs,winLossFactor,bidDetailsTs,bidId,bidRequestType,bidRequestReceiveDate,targetBidSubmissionDate,actualBidSubmissionDate,expectedDateOfOutcome,winProbability,coreAttributesUsedForWinning,bidOfficeGroupOwnerLinkTs,bidOfficeGroupOwner,opportunityTcsAccountContactLinkTs,contactT,contactName,notesTs,notesUpdated");
		map.put("feedback", "feedbackId,title,description,issueType,module,subModule,priority,status,createdDatetime,updatedDatetime,resolutionComments,createdUser,modifiedUser,userId,userName");
		map.put("connectreport", "connectId,connectName,customerMasterT,groupCustomerName,geographyMappingT,displayGeography,iouCustomerMappingT,displayIou,connectSubSpLinkTs,subSpMappingT,displaySubSp");
		map.put("bidreport", "bidRequestType,bidRequestReceiveDate,opportunityT,opportunityDealValues,currency,digitalDealValue,overallDealSize,salesStageCode,opportunitySubSpLinkTs,subSpMappingT,displaySubSp,customerMasterT,groupCustomerName,country,iouCustomerMappingT,displayIou,geographyMappingT,displayGeography");
		map.put("dealClosureData", "dealReportingStartDate,dealReportingEndDate");
		VIEW_FIELD_MAP = Collections.unmodifiableMap(map);
	}
	
	public static String getFields(String key) {
		String fields = null;
		if (key == null) 
			return null;
		logger.debug("Key: " + key);
		if (VIEW_FIELD_MAP.containsKey(key))
			fields =  VIEW_FIELD_MAP.get(key);
		return fields;
	}
	
}
