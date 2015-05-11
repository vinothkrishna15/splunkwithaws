package com.tcs.destination.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains the Bean Object Id mapping to support JsonIdentityInfo during serialization/de-serialization 
 * when user requests for specific fields
 *
 */

public class BeanObjectIdMapper {

	private static final Logger logger = LoggerFactory.getLogger(BeanObjectIdMapper.class);

	private static final Map<String, String> OBJECT_ID_MAP;

	static {		
		Map<String, String> map = new HashMap<String, String>();
		map.put("actualrevenuesdatat", "actualRevenuesDataId");
		map.put("bdmtargett", "bdmTargetId");
		map.put("beaconconvertormappingt", "currencyName");
		map.put("beacondatat", "beaconDataId");
		map.put("biddetailst", "bidId");			
		map.put("bidofficegroupownerlinkt", "bidOfficeGroupOwnerLinkId");		
		map.put("bidrequesttypemappingt", "bidRequestType");		
		map.put("collaborationcommentt", "commentId");		
		map.put("competitormappingt", "competitorName");			
		map.put("connectcustomercontactlinkt", "connectCustomerContactLinkId");	
		map.put("connectofferinglinkt", "connectOfferingLinkId");		
		map.put("connectopportunitylinkidt", "connectOpportunityLinkId");		
		map.put("connectsecondaryownerlinkt", "connectSecondaryOwnerLinkId");		
		map.put("connectsubsplinkt", "connectSubSpLinkId");			
		map.put("connectt", "connectId");				
		map.put("connecttcsaccountcontactlinkt", "connectTcsAccountContactLinkId");	
		map.put("contactrolemappingt", "contactRole");		
		map.put("contactt", "contactId");				
		map.put("customermastert", "customerId");			
		map.put("dealtypemappingt", "dealType");			
		map.put("documentrepositoryt", "documentId");		
		map.put("frequentlysearchedcustomerpartnert", "frequentlySearchedId");	
		map.put("geographycountrymappingt", "country");		
		map.put("geographymappingt", "geography");			
		map.put("ioubeaconmappingt", "beaconIou");			
		map.put("ioucustomermappingt", "iou");		
		map.put("notest", "noteId");
		map.put("offeringmappingt", "offering");
		map.put("opportunitycompetitorlinkt", "opportunityCompetitorLinkId");
		map.put("opportunitycustomercontactlinkt", "opportunityCustomerContactLinkId");
		map.put("opportunityofferinglinkt", "opportunityOfferingLinkId");
		map.put("opportunitypartnerlinkt", "opportunityPartnerLinkId");
		map.put("opportunitysalessupportlinkt", "opportunitySalesSupportLinkId");
		map.put("opportunitysubsplinkt", "opportunitySubSpLinkId");
		map.put("opportunityt", "opportunityId");
		map.put("opportunitytcsaccountcontactlinkt", "opportunityTcsAccountContactLinkId");
		map.put("opportunitytimelinehistoryt", "opportunityTimelineHistoryId");
		map.put("partnermastert", "partnerId");
		map.put("salesstagemappingt", "salesStageCode");
		map.put("searchkeywordst", "searchKeywordsId");
		map.put("subspmappingt", "subSp");
		map.put("taskbdmstaggedlinkt", "taskBdmsTaggedLinkId");
		map.put("taskt", "taskId");
		map.put("timezonemappingt", "timeZoneCode");
		map.put("userfavoritest", "userFavoritesId");
		map.put("usernotificationst", "userNotificationId");
		map.put("userrolemappingt", "userRole");
		map.put("usert", "userId");
		map.put("usertaggedfollowedt", "userTaggedFollowedId");
		map.put("opportunityWinLossFactorsT", "opportunityWinLossFactorsId");
		map.put("winLossFactorMappingT", "winLossFactor");
		OBJECT_ID_MAP = Collections.unmodifiableMap(map);
	}
	
	public static String getObjectId(String key) {
		String objectId = null;
		if (key == null) 
			return null;

		logger.debug("Key: " + key);

		if (key.endsWith("T") || key.endsWith("Ts")) {
			if (key.endsWith("Ts")) {
				key = key.substring(0, key.length()-1);
			}

			String keyStr = key.toLowerCase(); 
			if (OBJECT_ID_MAP.containsKey(keyStr))
				objectId =  OBJECT_ID_MAP.get(keyStr);
		}
		return objectId;
	}
}
