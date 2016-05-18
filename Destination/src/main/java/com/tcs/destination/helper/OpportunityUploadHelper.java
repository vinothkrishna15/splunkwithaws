package com.tcs.destination.helper;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WinLossMappingRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.OpportunityUploadService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;

@Component("opportunityUploadHelper")
public class OpportunityUploadHelper {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	OfferingRepository offeringRepository;

	@Autowired
	WinLossMappingRepository winLossMappingRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	CompetitorRepository competitorRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	private ContactRepository contactRepository;

	private List<String> listOfCompetitors = null;
	private List<String> listOfSubSp = null;
	private List<String> listOfOfferings = null;
	private List<String> listOfWinLossFactors = null;
	private List<String> listOfBidRequestType = null;
	private List<String> listOfCountry = null;

	private Map<String, String> mapOfUserT = null;

	private static final DateFormat dateFormat = new SimpleDateFormat(
			"MM/dd/yy");

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityUploadHelper.class);

	/**
	 * This method validates the data from the sheet and set appropriate error
	 * message
	 * 
	 * @param data
	 * @param userId
	 * @param opportunity
	 * @return UploadServiceErrorDetailsDTO
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validateOpportunityData(String[] data,
			String userId, OpportunityT opportunity) throws Exception {

		logger.debug("Inside Opportunity Data method ");
		listOfCompetitors = competitorRepository.getCompetitorName();

		listOfSubSp = subSpRepository.getSubSp();

		listOfOfferings = offeringRepository.getOffering();

		listOfWinLossFactors = winLossMappingRepository.getWinLossFactor();

		listOfBidRequestType = bidDetailsTRepository.getBidRequestType();

		listOfCountry = geographyRepository.getCountry();

		mapOfUserT = getNameAndIdFromUserT();

		List<String> remarks = new ArrayList<String>();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		// Customer Id
		String customerName = data[3];
		if (!StringUtils.isEmpty(customerName)) {
			CustomerMasterT customerMasterT = customerRepository
					.findByCustomerName(customerName);
			if (customerMasterT != null) {
				opportunity.setCustomerId(customerMasterT.getCustomerId());
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Customer Name; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Customer Name Is Mandatory; ");
		}

		// Country
		String country = data[6];
		if (!StringUtils.isEmpty(country)) {
			if (validateCellByStringLength(country,
					OpportunityUploadConstants.COUNTRY, 6,
					OpportunityUploadConstants.COUNTRY_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.COUNTRY
						+ " should be a maximum of "
						+ OpportunityUploadConstants.COUNTRY_MAX_SIZE
						+ " characters");

			} else {
				if (searchGeographyCountryMappingT(country)) {
					opportunity.setCountry(country);
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid country ");
				}
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Country Is Mandatory; ");
		}

		// CRM ID
		String crmId = data[7];
		if (!StringUtils.isEmpty(crmId)) {
			if (validateCellByStringLength(crmId,
					OpportunityUploadConstants.CRMID, 7,
					OpportunityUploadConstants.CRMID_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.CRMID
						+ " should be a maximum of "
						+ OpportunityUploadConstants.CRMID_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setCrmId(crmId);
			}
		}

		// OPPORTUNITY NAME
		String opportunityName = data[8];
		if (!StringUtils.isEmpty(opportunityName)) {
			if (validateCellByStringLength(opportunityName,
					OpportunityUploadConstants.OPP_NAME, 8,
					OpportunityUploadConstants.OPP_NAME_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.OPP_NAME
						+ " should be a maximum of "
						+ OpportunityUploadConstants.OPP_NAME_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setOpportunityName(opportunityName);
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Opportunity name is Is Mandatory; ");
		}

		// OPPORTUNITY DESCRIPTION
		String opportunityDescription = data[9];
		if (!StringUtils.isEmpty(opportunityDescription)) {
			if (validateCellByStringLength(opportunityDescription,
					OpportunityUploadConstants.OPP_DESC, 9,
					OpportunityUploadConstants.OPP_DESC_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.OPP_DESC
						+ " should be a maximum of "
						+ OpportunityUploadConstants.OPP_DESC_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setOpportunityDescription(opportunityDescription);
			}
		}

		// Sub Sp Params
		String subSp = data[10];
		if (!StringUtils.isEmpty(subSp)) {
			String[] subSps = subSp.split(",");
			List<OpportunitySubSpLinkT> OpportunitySubSpLinkTs = new ArrayList<OpportunitySubSpLinkT>();
			for (String value : subSps) {
				if (searchSubSpMappingT(value.trim())) {
					OpportunitySubSpLinkTs.add(constructOppSubSpLink(
							value.trim(), userId));
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid subSp  ");
				}

			}
			opportunity.setOpportunitySubSpLinkTs(OpportunitySubSpLinkTs);
		}

		// OpportunityOfferingLinkT Params
		String offering = data[11];
		if (!StringUtils.isEmpty(offering)) {
			String[] offerings = offering.split(",");

			List<OpportunityOfferingLinkT> opportunityOfferingLinkTs = new ArrayList<OpportunityOfferingLinkT>();
			for (String value : offerings) {
				if (searchOffering(value.trim())) {
					opportunityOfferingLinkTs.add(constructOppOfferingLink(
							value.trim(), userId));
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid Offering  ");
				}

			}
			opportunity.setOpportunityOfferingLinkTs(opportunityOfferingLinkTs);
		}

		// REQUEST RECEIVE DATE
		String requestReceiveDate = data[12];
		if (!StringUtils.isEmpty(requestReceiveDate)) {
			opportunity.setOpportunityRequestReceiveDate(dateFormat
					.parse(requestReceiveDate));
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Request Receive date is Is Mandatory; ");
		}

		// NEW LOGO
		String newLogo = data[13];
		if (!StringUtils.isEmpty(newLogo)) {
			if (validateCellByStringLength(newLogo,
					OpportunityUploadConstants.NEW_LOGO, 13,
					OpportunityUploadConstants.NEW_LOGO_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.NEW_LOGO
						+ " should be a maximum of "
						+ OpportunityUploadConstants.NEW_LOGO_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setNewLogo(newLogo);
			}
		} else {
			opportunity.setNewLogo(Constants.NO);
		}

		// STRATEGIC INITIATIVE
		String strategicInitiative = data[14];
		if (!StringUtils.isEmpty(strategicInitiative)) {
			if (validateCellByStringLength(strategicInitiative,
					OpportunityUploadConstants.STRATEGIC_INIT, 14,
					OpportunityUploadConstants.STRATEGIC_INIT_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.STRATEGIC_INIT
						+ " should be a maximum of "
						+ OpportunityUploadConstants.STRATEGIC_INIT_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setStrategicInitiative(strategicInitiative);
			}
		} else {
			opportunity.setStrategicInitiative(Constants.NO);
		}

		// DIGITAL FLAG
		String digitalFlag = data[15];
		if (!StringUtils.isEmpty(digitalFlag)) {
			if (validateCellByStringLength(digitalFlag,
					OpportunityUploadConstants.DIGITAL_FLAG, 15,
					OpportunityUploadConstants.DIGITAL_FLAG_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.DIGITAL_FLAG
						+ " should be a maximum of "
						+ OpportunityUploadConstants.DIGITAL_FLAG_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setDigitalFlag(digitalFlag);
			}
		} else {
			opportunity.setDigitalFlag("N");
		}

		// SALES STAGE CODE
		String salesStageCode = data[16];
		if (!StringUtils.isEmpty(salesStageCode)) {
			opportunity.setSalesStageCode((Integer.parseInt(salesStageCode
					.substring(0, 2))));
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Sales stage code is Mandatory; ");
		}

		// DEAL CURRENCY
		String dealCurrency = data[17];
		if (!StringUtils.isEmpty(dealCurrency)) {
			if (validateCellByStringLength(dealCurrency,
					OpportunityUploadConstants.DEAL_CURRENCY, 17,
					OpportunityUploadConstants.DEAL_CURRENCY_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.DEAL_CURRENCY
						+ " should be a maximum of "
						+ OpportunityUploadConstants.DEAL_CURRENCY_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setDealCurrency(dealCurrency);
			}
		}

		// OVERALL BID SIZE
		String overallBidSize = data[18];
		if (!StringUtils.isEmpty(overallBidSize)) {
			opportunity.setOverallDealSize(new BigDecimal((overallBidSize)));
		}

		// DIGITAL DEAL VALUE
		String digitalDealValue = data[20];
		if (!StringUtils.isEmpty(digitalDealValue)) {
			opportunity.setDigitalDealValue(new BigDecimal(digitalDealValue));
		}

		// OPPORTUNITY OWNER
		String opportunityOwner = data[22];
		if (!StringUtils.isEmpty(opportunityOwner)) {
			UserT user = userRepository.findByUserName(opportunityOwner);
			if (user != null) {
				opportunity.setOpportunityOwner(user.getUserId());
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Opportunity owner  ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Opportunity owner is Mandatory; ");
		}

		// //OpportunitySalesSupportLinkT Params
		String salesSupportOwner = data[23];
		if (!StringUtils.isEmpty(salesSupportOwner)) {
			String[] salesSupportOwners = salesSupportOwner.split(",");
			List<OpportunitySalesSupportLinkT> opportunitySalesSupportLinkTs = new ArrayList<OpportunitySalesSupportLinkT>();
			for (String value : salesSupportOwners) {
				opportunitySalesSupportLinkTs.add(constructSalesSupportLink(
						value, userId));
			}
			opportunity
					.setOpportunitySalesSupportLinkTs(opportunitySalesSupportLinkTs);
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Sales support owner is empty  ");
		}

		// TCS Contact Params
		String oppTcsAccContact = data[24];
		if (!StringUtils.isEmpty(oppTcsAccContact)) {
			List<ContactT> contacts = contactRepository
					.findByContactNames(oppTcsAccContact.split(","));
			if (!contacts.isEmpty()) {
				opportunity
						.setOpportunityTcsAccountContactLinkTs(constructOpportunityTCSContactLink(
								contacts, userId));
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Tcs Account Contact; ");
			}
		}

		// Opp Customer Contacts
		String customerContact = data[25];
		if (!StringUtils.isEmpty(customerContact)) {

			List<ContactT> custContactList = contactRepository
					.findByContactNames(customerContact.split(","));
			if (!custContactList.isEmpty()) {
				opportunity
						.setOpportunityCustomerContactLinkTs(constructOpportunityCustomerContactLink(
								custContactList, userId));
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Customer Contact; ");
			}
		}

		// Partner Values
		String partnerName = data[26];
		if (!StringUtils.isEmpty(partnerName)) {
			String[] partnerNames = partnerName.split(",");
			List<PartnerMasterT> partnerList = partnerRepository
					.findByPartnerNames(partnerNames);
			if (partnerList != null && !partnerList.isEmpty()) {
				opportunity
						.setOpportunityPartnerLinkTs(constructOppPartnerLink(
								partnerList, userId));
			}
		}

		// Competitor Params
		String competitor = data[27];
		if (!StringUtils.isEmpty(competitor)) {
			String[] competitors = competitor.split(",");
			List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs = new ArrayList<OpportunityCompetitorLinkT>();
			for (String value : competitors) {
				if (searchCompetitorMappingT(value.trim())) {
					opportunityCompetitorLinkTs.add(constructOppCompetitorLink(
							value, userId));
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid Competitor Name  ");
				}

			}
			opportunity
					.setOpportunityCompetitorLinkTs(opportunityCompetitorLinkTs);
		}

		// Bid Details
		String bidRequestType = data[28];
		String bidRequestReceiveDate = data[30];
		String targetBidSubmissionDate = data[31];
		String actualBidSubmissionDate = data[32];
		String expectedDateOfOutcome = data[33];
		String winProbability = data[34];
		String coreAttributes = data[35];

		if ((!StringUtils.isEmpty(bidRequestType))
				&& (!StringUtils.isEmpty(bidRequestReceiveDate))
				&& (!StringUtils.isEmpty(targetBidSubmissionDate))) {
			if (searchBidDetailsMappingT(bidRequestType)) {
				opportunity.setBidDetailsTs(constructbidDetailsT(
						bidRequestType, bidRequestReceiveDate,
						targetBidSubmissionDate, actualBidSubmissionDate,
						expectedDateOfOutcome, winProbability, coreAttributes,
						userId));
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid bid request type  ");
			}
		}

		// DEAL TYPE
		String dealType = data[36];
		if (!StringUtils.isEmpty(dealType)) {
			if (validateCellByStringLength(dealType,
					OpportunityUploadConstants.DEAL_TYPE, 36,
					OpportunityUploadConstants.DEAL_TYPE_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.DEAL_TYPE
						+ " should be a maximum of "
						+ OpportunityUploadConstants.DEAL_TYPE_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setDealType(dealType);
			}
		}

		// DEAL CLOSURE DATE
		String dealClosureDate = data[37];
		if (!StringUtils.isEmpty(dealClosureDate)) {
			opportunity.setDealClosureDate(dateFormat.parse(dealClosureDate));
		}

		// ENGAGEMENT DURATION
		String engagementDuration = data[38];
		if (!StringUtils.isEmpty(engagementDuration)) {
			if (validateCellByStringLength(engagementDuration,
					OpportunityUploadConstants.ENGAGEMENT_DURATION, 38,
					OpportunityUploadConstants.ENGAGEMENT_DURATION_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.ENGAGEMENT_DURATION
						+ " should be a maximum of "
						+ OpportunityUploadConstants.ENGAGEMENT_DURATION_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setEngagementDuration(engagementDuration);
			}
		}

		// ENGAGEMENT START DATE
		String engagementStartDate = data[39];
		if (!StringUtils.isEmpty(engagementStartDate)) {
			opportunity.setEngagementStartDate(dateFormat
					.parse(engagementStartDate));
		}

		// FACTORS FOR WIN LOSS -
		String factorsForWinLoss = data[40];
		if (!StringUtils.isEmpty(factorsForWinLoss)) {
			String[] factorsArray = factorsForWinLoss.split(",");
			List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs = new ArrayList<OpportunityWinLossFactorsT>();
			for (String factor : factorsArray) {
				if (searchWinLossFactor(factor.trim())) {
					opportunityWinLossFactorsTs.add(constructFactorsForWinLoss(
							factor.trim(), userId));
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Invalid win loss factor  ");
				}
			}
			opportunity
					.setOpportunityWinLossFactorsTs(opportunityWinLossFactorsTs);
		}

		// COMMENTS FOR WIN LOSS
		String commentsForWinLoss = data[41];
		if (!StringUtils.isEmpty(commentsForWinLoss)) {
			if (validateCellByStringLength(commentsForWinLoss,
					OpportunityUploadConstants.COMMENTS_FOR_WIN_LOSS, 41,
					OpportunityUploadConstants.COMMENTS_FOR_WIN_LOSS_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.COMMENTS_FOR_WIN_LOSS
						+ " should be a maximum of "
						+ OpportunityUploadConstants.COMMENTS_FOR_WIN_LOSS_MAX_SIZE
						+ " characters");
			} else {
				opportunity.setDealClosureComments(commentsForWinLoss);
			}
		}

		// Params for opportunity_t Table - manually set
		opportunity.setDocumentsAttached(Constants.NO);
		opportunity.setCreatedBy(userId);
		opportunity.setModifiedBy(userId);

		// Deal Status Remarks
		String dealRemarks = data[42];
		String remark1 = data[44];
		String remark2 = data[45];
		if (!StringUtils.isEmpty(dealRemarks)) {
			if (validateCellByStringLength(dealRemarks,
					OpportunityUploadConstants.DEAL_STATUS_REMARKS, 42,
					OpportunityUploadConstants.DEAL_STATUS_REMARKS_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.DEAL_STATUS_REMARKS
						+ " should be a maximum of "
						+ OpportunityUploadConstants.DEAL_STATUS_REMARKS_MAX_SIZE
						+ " characters");
			} else {
				remarks.add(dealRemarks);
			}
		}
		if (!StringUtils.isEmpty(remark1)) {
			if (validateCellByStringLength(remark1,
					OpportunityUploadConstants.REMARKS_1, 44,
					OpportunityUploadConstants.REMARKS_1_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.REMARKS_1
						+ " should be a maximum of "
						+ OpportunityUploadConstants.REMARKS_1_MAX_SIZE
						+ " characters");
			} else {
				remarks.add(remark1);
			}
		}
		if (!StringUtils.isEmpty(remark2)) {
			if (validateCellByStringLength(remark2,
					OpportunityUploadConstants.REMARKS_2, 45,
					OpportunityUploadConstants.REMARKS_2_MAX_SIZE)) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage(OpportunityUploadConstants.REMARKS_2
						+ " should be a maximum of "
						+ OpportunityUploadConstants.REMARKS_2_MAX_SIZE
						+ " characters");
			} else {
				remarks.add(remark2);
			}
		}
		if (!remarks.isEmpty()) {
			opportunity.setNotesTs(constructNotesT(remarks,
					opportunity.getCustomerId(), userId));
		}

		// else{
		// opportunity.setNotesTs(constructNotesT(dealRemarks,
		// opportunity.getCustomerId(), userId));
		// }
		logger.debug("opportunity" + opportunity);
		return error;
	}

	private List<OpportunityTcsAccountContactLinkT> constructOpportunityTCSContactLink(
			List<ContactT> contacts, String userId) {
		List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs = new ArrayList<OpportunityTcsAccountContactLinkT>();
		for (ContactT contact : contacts) {
			OpportunityTcsAccountContactLinkT otclt = new OpportunityTcsAccountContactLinkT();
			otclt.setContactT(contact);
			otclt.setCreatedBy(userId);
			otclt.setModifiedBy(userId);
			opportunityTcsAccountContactLinkTs.add(otclt);
		}
		return opportunityTcsAccountContactLinkTs;
	}

	private List<OpportunityCustomerContactLinkT> constructOpportunityCustomerContactLink(
			List<ContactT> custContactList, String userId) {
		List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs = new ArrayList<OpportunityCustomerContactLinkT>();
		for (ContactT contact : custContactList) {
			OpportunityCustomerContactLinkT occlt = new OpportunityCustomerContactLinkT();
			occlt.setContactT(contact);
			occlt.setCreatedBy(userId);
			occlt.setModifiedBy(userId);
			opportunityCustomerContactLinkTs.add(occlt);
		}
		return opportunityCustomerContactLinkTs;
	}

	private List<NotesT> constructNotesT(List<String> remarks,
			String customerId, String userUpdated) {
		List<NotesT> listOfNotes = new ArrayList<NotesT>();
		for (String dealRemarks : remarks) {
			NotesT notes = new NotesT();
			notes.setEntityType(EntityType.OPPORTUNITY.toString());
			notes.setNotesUpdated(dealRemarks);
			notes.setCustomerId(customerId);
			notes.setUserUpdated(userUpdated);
			listOfNotes.add(notes);
		}
		return listOfNotes;
	}

	private OpportunityWinLossFactorsT constructFactorsForWinLoss(
			String factor, String userId) {

		// rank is set based on the order of factors which are given as
		// input
		int rank = 1;
		OpportunityWinLossFactorsT owlf = new OpportunityWinLossFactorsT();

		owlf.setCreatedBy(userId);
		owlf.setModifiedBy(userId);
		owlf.setRank(rank);
		owlf.setWinLossFactor(factor);
		rank++;

		return owlf;

	}

	private boolean searchWinLossFactor(String factor) {
		boolean flag = false;
		if (listOfWinLossFactors.contains(factor)) {
			flag = true;
		}
		return flag;
	}

	private List<BidDetailsT> constructbidDetailsT(String bidRequestType,
			String bidRequestReceiveDate, String targetBidSubmissionDate,
			String actualBidSubmissionDate, String expectedDateOfOutcome,
			String winProbability, String coreAttributes, String userId)
			throws Exception {
		List<BidDetailsT> listOfBidDetailsT = new ArrayList<BidDetailsT>();

		BidDetailsT bdt = new BidDetailsT();

		if (!StringUtils.isEmpty(bidRequestType)) {
			bdt.setBidRequestType(bidRequestType);
		}
		if (!StringUtils.isEmpty(bidRequestReceiveDate)) {
			bdt.setBidRequestReceiveDate(dateFormat
					.parse(bidRequestReceiveDate));
		}
		if (!StringUtils.isEmpty(targetBidSubmissionDate)) {
			bdt.setTargetBidSubmissionDate(dateFormat
					.parse(targetBidSubmissionDate));
		}
		if (!StringUtils.isEmpty(actualBidSubmissionDate)) {
			bdt.setActualBidSubmissionDate(dateFormat
					.parse(actualBidSubmissionDate));
		}
		if (!StringUtils.isEmpty(expectedDateOfOutcome)) {
			bdt.setExpectedDateOfOutcome(dateFormat
					.parse(expectedDateOfOutcome));
		}
		if (!StringUtils.isEmpty(winProbability)) {
			validateCellByStringLength(winProbability,
					OpportunityUploadConstants.WIN_PROBABILITY, 34,
					OpportunityUploadConstants.WIN_PROBABILITY_MAX_SIZE);
			bdt.setWinProbability(winProbability);
		}
		if (!StringUtils.isEmpty(coreAttributes)) {
			validateCellByStringLength(coreAttributes,
					OpportunityUploadConstants.CORE_ATTRIBUTES, 35,
					OpportunityUploadConstants.CORE_ATTRIBUTES_MAX_VALUE);
			bdt.setCoreAttributesUsedForWinning(coreAttributes);
		}
		bdt.setCreatedBy(userId);
		bdt.setModifiedBy(userId);

		listOfBidDetailsT.add(bdt);

		return listOfBidDetailsT;
	}

	private boolean searchBidDetailsMappingT(String bidRequestType) {
		boolean flag = false;
		if (listOfBidRequestType.contains(bidRequestType)) {
			flag = true;
		}

		return flag;
	}

	private Map<String, String> getNameAndIdFromUserT() {
		List<Object[]> listOfUsers = userRepository.getNameAndId();

		Map<String, String> mapOfUserT = new HashMap<String, String>();

		for (Object[] ut : listOfUsers) {
			mapOfUserT.put(ut[0].toString().trim(), ut[1].toString().trim());
		}
		return mapOfUserT;

	}

	private OpportunitySalesSupportLinkT constructSalesSupportLink(
			String value, String userId) {
		OpportunitySalesSupportLinkT oslt = new OpportunitySalesSupportLinkT();
		oslt.setSalesSupportOwner(getMapValuesForKey(mapOfUserT, value.trim()));
		oslt.setCreatedBy(userId);
		oslt.setModifiedBy(userId);
		return oslt;
	}

	private String getMapValuesForKey(Map<String, String> map, String key) {
		String value = null;
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		return value;
	}

	private OpportunityOfferingLinkT constructOppOfferingLink(String value,
			String userId) {
		OpportunityOfferingLinkT oolt = new OpportunityOfferingLinkT();

		oolt.setOffering(value);
		oolt.setCreatedBy(userId);
		oolt.setModifiedBy(userId);
		return oolt;
	}

	private boolean searchOffering(String value) {
		boolean flag = false;
		if (listOfOfferings.contains(value)) {
			flag = true;
		}
		return flag;
	}

	private OpportunitySubSpLinkT constructOppSubSpLink(String subSp,
			String userId) {
		OpportunitySubSpLinkT oslt = new OpportunitySubSpLinkT();
		oslt.setSubSp(subSp);
		oslt.setCreatedBy(userId);
		oslt.setModifiedBy(userId);
		return oslt;
	}

	private boolean searchSubSpMappingT(String subSp) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if (listOfSubSp.contains(subSp)) {
			flag = true;
		}
		return flag;
	}

	private boolean searchCompetitorMappingT(String competitorName) {
		boolean flag = false;
		if (listOfCompetitors.contains(competitorName)) {
			flag = true;
		}

		return flag;
	}

	private OpportunityCompetitorLinkT constructOppCompetitorLink(String value,
			String userId) {
		// TODO Auto-generated method stub
		OpportunityCompetitorLinkT oclt = new OpportunityCompetitorLinkT();
		oclt.setCompetitorName(value.trim());
		oclt.setCreatedBy(userId);
		oclt.setModifiedBy(userId);
		oclt.setIncumbentFlag(Constants.N);
		return oclt;
	}

	private List<OpportunityPartnerLinkT> constructOppPartnerLink(
			List<PartnerMasterT> partnerList, String userId) {
		// TODO Auto-generated method stub

		List<OpportunityPartnerLinkT> OpportunityPartnerLinkT = new ArrayList<OpportunityPartnerLinkT>();
		for (PartnerMasterT partner : partnerList) {
			OpportunityPartnerLinkT oplt = new OpportunityPartnerLinkT();
			oplt.setPartnerId(partner.getPartnerId());
			oplt.setCreatedBy(userId);
			oplt.setModifiedBy(userId);
			OpportunityPartnerLinkT.add(oplt);
		}
		return OpportunityPartnerLinkT;
	}

	private boolean searchGeographyCountryMappingT(String country) {
		// TODO Auto-generated method stub
		boolean flag = false;
		if (listOfCountry.contains(country)) {
			flag = true;
		}

		return flag;
	}

	private boolean validateCellByStringLength(String value, String columnName,
			int columnNumber, int length) throws Exception {
		boolean flag = false;
		if (value.length() > length) {
			flag = true;
		}
		return flag;
	}

}
