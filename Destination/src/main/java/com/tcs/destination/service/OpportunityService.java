package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
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
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.BidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.ConnectOpportunityLinkTRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.OpportunityCompetitorLinkTRepository;
import com.tcs.destination.data.repository.OpportunityCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityOfferingLinkTRepository;
import com.tcs.destination.data.repository.OpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.OpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.OpportunitySubSpLinkTRepository;
import com.tcs.destination.data.repository.OpportunityTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityWinLossFactorsTRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.enums.OpportunityRole;
import com.tcs.destination.exception.DestinationException;

@Component
public class OpportunityService {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityService.class);

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	SearchKeywordsRepository searchKeywordsRepository;

	@Autowired
	OpportunitySalesSupportLinkTRepository opportunitySalesSupportLinkTRepository;

	@Autowired
	BidOfficeGroupOwnerLinkTRepository bidOfficeGroupOwnerLinkTRepository;

	@Autowired
	ConnectOpportunityLinkTRepository connectOpportunityLinkTRepository;

	@Autowired
	NotesTRepository notesTRepository;

	@Autowired
	OpportunityPartnerLinkTRepository opportunityPartnerLinkTRepository;

	@Autowired
	OpportunityCompetitorLinkTRepository opportunityCompetitorLinkTRepository;

	@Autowired
	OpportunityCustomerContactLinkTRepository opportunityCustomerContactLinkTRepository;

	@Autowired
	OpportunityOfferingLinkTRepository opportunityOfferingLinkTRepository;

	@Autowired
	OpportunitySubSpLinkTRepository opportunitySubSpLinkTRepository;

	@Autowired
	OpportunityTcsAccountContactLinkTRepository opportunityTcsAccountContactLinkTRepository;

	@Autowired
	OpportunityWinLossFactorsTRepository opportunityWinLossFactorsTRepository;

	public List<OpportunityT> findByOpportunityName(String nameWith)
			throws Exception {
		logger.debug("Inside findByOpportunityName Service");
		List<OpportunityT> opportunities = opportunityRepository
				.findByOpportunityNameIgnoreCaseLike("%" + nameWith + "%");
		if (opportunities.isEmpty()) {
			logger.error("NOT_FOUND: No such Opportunity Found. Please ensure your Opportunity name.");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No such Opportunity Found. Please ensure your Opportunity name.");
		}
		return opportunities;
	}

	public List<OpportunityT> findRecentOpportunities(String customerId)
			throws Exception {
		logger.debug("Inside findRecentOpportunities Service");
		// Date date = new Date(); // Or where ever you get it from
		// Date daysAgo = new DateTime(date).minusDays(300).toDate();
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
		Date fromDate = new Date(now.getTimeInMillis());
		List<OpportunityT> opportunities = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
						customerId, fromDate);
		if (!opportunities.isEmpty()) {
			logger.debug("Opportunity Not Empty");
			return opportunities;
		}
		logger.error("NOT_FOUND: No Relevent Data Found in the database");
		throw new DestinationException(HttpStatus.NOT_FOUND,
				"No Relevent Data Found in the database");
	}

	public List<OpportunityT> findByTaskOwnerForRole(String opportunityOwner,
			String opportunityRole, Date fromDate, Date toDate)
			throws Exception {
		logger.debug("Inside findByTaskOwnerForRole Service");
		if (OpportunityRole.contains(opportunityRole)) {
			logger.debug("Opportunity Role is Present");
			switch (OpportunityRole.valueOf(opportunityRole)) {
			case PRIMARY_OWNER:
				logger.debug("Primary Owner Found");
				System.out.println("Primary Owner");
				return findForPrimaryOwner(opportunityOwner, true, fromDate,
						toDate);
			case SALES_SUPPORT:
				logger.debug("Sales Support Found");
				System.out.println("Sales Support");
				return findForSalesSupport(opportunityOwner, true, fromDate,
						toDate);
			case BID_OFFICE:
				logger.debug("Bid Office Found");
				System.out.println("Bid office");
				return findForBidOffice(opportunityOwner, true, fromDate,
						toDate);
			case ALL:
				logger.debug("ALL Found");
				System.out.println("All");
				List<OpportunityT> opportunities = new ArrayList<OpportunityT>();
				opportunities.addAll(findForPrimaryOwner(opportunityOwner,
						false, fromDate, toDate));
				System.out.println("Primary " + opportunities.size());
				opportunities.addAll(findForSalesSupport(opportunityOwner,
						false, fromDate, toDate));
				System.out.println("Sales Support " + opportunities.size());
				opportunities.addAll(findForBidOffice(opportunityOwner, false,
						fromDate, toDate));
				System.out.println("Bid Office " + opportunities.size());
				return validateAndReturnOpportunitesData(opportunities, true);
			}
			return null;
		} else {
			logger.error("BAD_REQUEST: Invalid Oppurtunity Role");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role");
		}
	}

	private List<OpportunityT> findForPrimaryOwner(String userId,
			boolean isOnly, Date fromDate, Date toDate)
			throws DestinationException {
		logger.debug("Inside findForPrimaryOwner Service");
		List<OpportunityT> opportunities = opportunityRepository
				.findByOpportunityOwnerAndDealClosureDateBetween(userId,
						fromDate, toDate);

		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> validateAndReturnOpportunitesData(
			List<OpportunityT> opportunities, boolean validate)
			throws DestinationException {
		logger.debug("validateAndReturnOpportunitesData");
		System.out.println("Opportunity " + opportunities.size());
		if (validate) {
			if (opportunities.size() > 0) {
				logger.debug("Opportunity List Is Present");
				return opportunities;
			} else {
				logger.error("NOT_FOUND: No Opportunities Found");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Opportunities Found");
			}
		} else {
			return opportunities;
		}
	}

	private List<OpportunityT> findForBidOffice(String userId, boolean isOnly,
			Date fromDate, Date toDate) throws DestinationException {
		logger.debug("Inside findForBidOffice Service");
		UserT userT = new UserT();
		userT.setUserId(userId);
		List<OpportunityT> opportunities = opportunityRepository
				.findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(
						userId, fromDate, toDate);
		for (OpportunityT opprtunity : opportunities) {
			System.out.println("Name " + opprtunity.getOpportunityName());
		}
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> findForSalesSupport(String userId,
			boolean isOnly, Date fromDate, Date toDate)
			throws DestinationException {
		logger.debug("Inside findForSalesSupport Service");
		List<OpportunityT> opportunities = opportunityRepository
				.findOpportunityTForSalesSupportOwnerWithDateBetween(userId,
						fromDate, toDate);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	public OpportunityT findByOpportunityId(String opportunityId)
			throws DestinationException {
		logger.debug("Inside findByOpportunityId Service");
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityId(opportunityId);
		if (opportunity != null)
			return opportunity;
		else
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportuinty Id " + opportunityId + " Not Found");
	}

	public void create(OpportunityT opportunity) throws Exception {
		try {
			if (opportunity != null) {
				opportunity.setOpportunityId(null);
				saveOpportunity(opportunity, false);
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	@Transactional
	private OpportunityT saveOpportunity(OpportunityT opportunity,
			boolean isUpdate) throws Exception {

		if (isUpdate) {
			deleteChildObjects(opportunity);
		}
		opportunity.setOnHold("NO");
		logger.error("Before saving opp table with ID "
				+ opportunity.getOpportunityId());
		saveBaseObject(opportunity);
		logger.debug("Base table saved with ID "
				+ opportunity.getOpportunityId());
		return saveChildObject(opportunity);
		// logger.error("Saved the opportunity");

	}

	private OpportunityT saveChildObject(OpportunityT opportunity)
			throws Exception {
		if (opportunity.getOpportunityCustomerContactLinkTs() != null) {
			for (OpportunityCustomerContactLinkT customerContact : opportunity
					.getOpportunityCustomerContactLinkTs()) {
				customerContact
						.setOpportunityId(opportunity.getOpportunityId());
			}
		}

		if (opportunity.getOpportunityTcsAccountContactLinkTs() != null) {
			for (OpportunityTcsAccountContactLinkT tcsContact : opportunity
					.getOpportunityTcsAccountContactLinkTs()) {
				tcsContact.setOpportunityId(opportunity.getOpportunityId());
			}
		}

		if (opportunity.getOpportunityPartnerLinkTs() != null) {
			for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunity
					.getOpportunityPartnerLinkTs()) {
				opportunityPartnerLinkT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		if (opportunity.getOpportunityCompetitorLinkTs() != null) {
			for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
					.getOpportunityCompetitorLinkTs()) {
				opportunityCompetitorLinkT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		if (opportunity.getOpportunitySubSpLinkTs() != null) {
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
					.getOpportunitySubSpLinkTs()) {
				opportunitySubSpLinkT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		if (opportunity.getOpportunityOfferingLinkTs() != null) {
			for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity
					.getOpportunityOfferingLinkTs()) {
				opportunityOfferingLinkT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		if (opportunity.getConnectOpportunityLinkIdTs() != null) {
			for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunity
					.getConnectOpportunityLinkIdTs()) {
				connectOpportunityLinkIdT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		if (opportunity.getOpportunitySalesSupportLinkTs() != null) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				opportunitySalesSupportLinkT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		if (opportunity.getNotesTs() != null) {
			for (NotesT notesT : opportunity.getNotesTs()) {
				notesT.setOpportunityId(opportunity.getOpportunityId());
			}
		}

		if (opportunity.getBidDetailsTs() != null) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				bidDetailsT.setOpportunityId(opportunity.getOpportunityId());
				logger.debug("Saving Bid Details by "
						+ bidDetailsT.getCreatedModifiedBy());
				if (bidDetailsT.getBidOfficeGroupOwnerLinkTs() != null) {
					List<BidOfficeGroupOwnerLinkT> bidOfficeOwnerLinkTs = new ArrayList<BidOfficeGroupOwnerLinkT>(
							bidDetailsT.getBidOfficeGroupOwnerLinkTs());
					bidDetailsT.setBidOfficeGroupOwnerLinkTs(null);
				}
					bidDetailsTRepository.save(bidDetailsT);
					bidDetailsT.setBidOfficeGroupOwnerLinkTs(bidOfficeOwnerLinkTs);
				logger.debug("Saved Bid Details " + bidDetailsT.getBidId());
				if (bidDetailsT.getBidOfficeGroupOwnerLinkTs() != null) {
					for (BidOfficeGroupOwnerLinkT bidOfficeOwnerLinkT : bidDetailsT
							.getBidOfficeGroupOwnerLinkTs()) {
						bidOfficeOwnerLinkT.setBidId(bidDetailsT.getBidId());
					}
				}
			}
		}

		if (opportunity.getSearchKeywordsTs() != null) {
			for (SearchKeywordsT searchKeywordT : opportunity
					.getSearchKeywordsTs()) {
				searchKeywordT.setEntityId(opportunity.getOpportunityId());
				searchKeywordsRepository.save(searchKeywordT);
			}
		}

		if (opportunity.getOpportunityWinLossFactorsTs() != null) {
			for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
					.getOpportunityWinLossFactorsTs()) {
				opportunityWinLossFactorsT.setOpportunityId(opportunity
						.getOpportunityId());
			}
		}

		return opportunityRepository.save(opportunity);
		// System.out.println("Save Successful12345");
	}

	private void saveBaseObject(OpportunityT opportunity) throws Exception {
		OpportunityT childOpportunityT = new OpportunityT();
		childOpportunityT.setCreatedModifiedBy(opportunity
				.getCreatedModifiedBy());

		childOpportunityT.setCreatedModifiedDatetime(opportunity
				.getCreatedModifiedDatetime());
		childOpportunityT.setCrmId(childOpportunityT.getCrmId());
		childOpportunityT.setCustomerId(opportunity.getCustomerId());
		childOpportunityT.setDealClosureDate(opportunity.getDealClosureDate());
		childOpportunityT.setDescriptionForWinLoss(opportunity
				.getDescriptionForWinLoss());
		childOpportunityT
				.setDigitalDealValue(opportunity.getDigitalDealValue());
		childOpportunityT.setDocumentsAttached(opportunity
				.getDocumentsAttached());
		childOpportunityT.setEngagementDuration(opportunity
				.getEngagementDuration());
		childOpportunityT.setEngagementStartDate(opportunity
				.getEngagementStartDate());
		childOpportunityT.setNewLogo(opportunity.getNewLogo());
		childOpportunityT.setOpportunityDescription(opportunity
				.getOpportunityDescription());
		childOpportunityT.setOpportunityName(opportunity.getOpportunityName());
		childOpportunityT.setOpportunityRequestReceiveDate(opportunity
				.getOpportunityRequestReceiveDate());
		childOpportunityT.setOverallDealSize(opportunity.getOverallDealSize());
		childOpportunityT.setStrategicInitiative(opportunity
				.getStrategicInitiative());
		childOpportunityT.setDealType(opportunity.getDealType());
		childOpportunityT.setCountry(opportunity.getCountry());
		childOpportunityT.setDealClosureDate(opportunity.getDealClosureDate());
		childOpportunityT.setEngagementStartDate(opportunity
				.getEngagementStartDate());
		childOpportunityT.setEngagementDuration(opportunity
				.getEngagementDuration());
		childOpportunityT.setOpportunityId(opportunity.getOpportunityId());
		childOpportunityT
				.setOpportunityOwner(opportunity.getOpportunityOwner());
		opportunity.setOpportunityId(opportunityRepository.save(
				childOpportunityT).getOpportunityId());
		logger.debug("ID " + childOpportunityT.getOpportunityId());

	}

	public void edit(OpportunityT opportunity) throws Exception {

		OpportunityT dbOpportunity = opportunityRepository.findByOpportunityId(
				opportunity.getOpportunityId()).clone();
		if (dbOpportunity != null && dbOpportunity.getOnHold() != null) {
			if (dbOpportunity.getOnHold().equals("YES")) {
				throw new DestinationException(HttpStatus.LOCKED,
						"The Opportunity is put on HOLD. Kindly contact your System Administrator");
			}
		}
		saveOpportunity(opportunity, true);

	}

	private void deleteChildObjects(OpportunityT opportunity) throws Exception {
		if (opportunity.getDeleteConnectOpportunityLinkIdTs() != null
				&& opportunity.getDeleteConnectOpportunityLinkIdTs().size() > 0) {
			for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunity
					.getDeleteConnectOpportunityLinkIdTs()) {
				connectOpportunityLinkTRepository
						.delete(connectOpportunityLinkIdT);
			}
		}

		if (opportunity.getDeleteNotesTs() != null
				&& opportunity.getDeleteNotesTs().size() > 0) {
			for (NotesT noteT : opportunity.getDeleteNotesTs()) {
				notesTRepository.delete(noteT);
			}
		}

		if (opportunity.getDeleteOpportunityPartnerLinkTs() != null
				&& opportunity.getDeleteOpportunityPartnerLinkTs().size() > 0) {
			for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunity
					.getDeleteOpportunityPartnerLinkTs()) {
				opportunityPartnerLinkTRepository
						.delete(opportunityPartnerLinkT);
			}
		}

		if (opportunity.getDeleteOpportunityCompetitorLinkTs() != null
				&& opportunity.getDeleteOpportunityCompetitorLinkTs().size() > 0) {
			for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
					.getDeleteOpportunityCompetitorLinkTs()) {
				opportunityCompetitorLinkTRepository
						.delete(opportunityCompetitorLinkT);
			}
		}

		if (opportunity.getDeleteOpportunityCustomerContactLinkTs() != null
				&& opportunity.getDeleteOpportunityCustomerContactLinkTs()
						.size() > 0) {
			for (OpportunityCustomerContactLinkT opportunityCustomerContactLinkT : opportunity
					.getDeleteOpportunityCustomerContactLinkTs()) {
				opportunityCustomerContactLinkTRepository
						.delete(opportunityCustomerContactLinkT);
			}
		}

		if (opportunity.getDeleteOpportunityOfferingLinkTs() != null
				&& opportunity.getDeleteOpportunityOfferingLinkTs().size() > 0) {
			for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity
					.getDeleteOpportunityOfferingLinkTs()) {
				opportunityOfferingLinkTRepository
						.delete(opportunityOfferingLinkT);
			}
		}

		if (opportunity.getDeleteOpportunityOfferingLinkTs() != null
				&& opportunity.getDeleteOpportunityOfferingLinkTs().size() > 0) {
			for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity
					.getDeleteOpportunityOfferingLinkTs()) {
				opportunityOfferingLinkTRepository
						.delete(opportunityOfferingLinkT);
			}
		}

		if (opportunity.getDeleteOpportunitySalesSupportLinkTs() != null
				&& opportunity.getDeleteOpportunitySalesSupportLinkTs().size() > 0) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getDeleteOpportunitySalesSupportLinkTs()) {
				opportunitySalesSupportLinkTRepository
						.delete(opportunitySalesSupportLinkT);
			}
		}

		if (opportunity.getDeleteOpportunitySubSpLinkTs() != null
				&& opportunity.getDeleteOpportunitySubSpLinkTs().size() > 0) {
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
					.getDeleteOpportunitySubSpLinkTs()) {
				opportunitySubSpLinkTRepository.delete(opportunitySubSpLinkT);
			}
		}

		if (opportunity.getDeleteOpportunityTcsAccountContactLinkTs() != null
				&& opportunity.getDeleteOpportunityTcsAccountContactLinkTs()
						.size() > 0) {
			for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : opportunity
					.getDeleteOpportunityTcsAccountContactLinkTs()) {
				opportunityTcsAccountContactLinkTRepository
						.delete(opportunityTcsAccountContactLinkT);
			}
		}

		if (opportunity.getDeleteOpportunityWinLossFactorsTs() != null
				&& opportunity.getDeleteOpportunityWinLossFactorsTs().size() > 0) {
			for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
					.getDeleteOpportunityWinLossFactorsTs()) {
				opportunityWinLossFactorsTRepository
						.delete(opportunityWinLossFactorsT);
			}
		}
	}

	public List<OpportunityT> findByOpportunityOwnerAndDate(String userId,
			Date fromDate, Date toDate) throws Exception {
		List<OpportunityT> opportunityList = null;
		opportunityList = opportunityRepository
				.findByOpportunityOwnerAndDealClosureDateBetween(userId,
						fromDate, toDate);
		if ((opportunityList == null) || opportunityList.isEmpty()) {
			logger.error("NOT_FOUND: No Opportunity found for the UserId and Target Bid Submission date");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunity found for the UserId and Target Bid Submission date");
		}
		return opportunityList;
	}
}
