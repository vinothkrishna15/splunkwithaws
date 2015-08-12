package com.tcs.destination.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunitiesBySupervisorIdDTO;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityDetailsDTO;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunityReopenRequestT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityTimelineHistoryT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.TeamOpportunityDetailsDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.BidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
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
import com.tcs.destination.data.repository.OpportunityTimelineHistoryTRepository;
import com.tcs.destination.data.repository.OpportunityWinLossFactorsTRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.OpportunityRole;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.AutoCommentsHelper;
import com.tcs.destination.helper.AutoCommentsLazyLoader;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;

@Service
public class OpportunityService {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityService.class);

	// Required for auto comments
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	SearchKeywordsRepository searchKeywordsRepository;

	@Autowired
	OpportunityTimelineHistoryTRepository opportunityTimelineHistoryTRepository;

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

	// Required beans for Auto comments - start
	@Autowired
	ThreadPoolTaskExecutor autoCommentsTaskExecutor;

	@Autowired
	AutoCommentsEntityTRepository autoCommentsEntityTRepository;

	@Autowired
	AutoCommentsEntityFieldsTRepository autoCommentsEntityFieldsTRepository;

	@Autowired
	CollaborationCommentsRepository collaborationCommentsRepository;

	// Required beans for Auto comments - end
	@Autowired
	UserRepository userRepository;

	public List<OpportunityT> findByOpportunityName(String nameWith,
			String customerId, List<String> toCurrency) throws Exception {
		logger.debug("Inside findByOpportunityName() service");
		List<OpportunityT> opportunities = null;
		if (customerId.isEmpty()) {
			opportunities = opportunityRepository
					.findByOpportunityNameIgnoreCaseLike("%" + nameWith + "%");
		} else {
			opportunities = opportunityRepository
					.findByOpportunityNameIgnoreCaseLikeAndCustomerId("%"
							+ nameWith + "%", customerId);
		}
		if (opportunities.isEmpty()) {
			logger.error(
					"NOT_FOUND: Opportunities not found with the given name: {}",
					nameWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunities not found with the given name: " + nameWith);
		}

		beaconConverterService.convertOpportunityCurrency(opportunities,
				toCurrency);
		prepareOpportunity(opportunities);
		return opportunities;
	}

	public List<OpportunityT> findRecentOpportunities(String customerId,
			List<String> toCurrency) throws Exception {
		logger.debug("Inside findRecentOpportunities() service");
		// Date date = new Date(); // Or where ever you get it from
		// Date daysAgo = new DateTime(date).minusDays(300).toDate();
		Calendar now = Calendar.getInstance();
		now.set(Calendar.YEAR, now.get(Calendar.YEAR) - 1);
		Date fromDate = new Date(now.getTimeInMillis());
		List<OpportunityT> opportunities = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
						customerId, fromDate);
		if (opportunities.isEmpty()) {
			logger.error(
					"NOT_FOUND: Recent opportunities not found for CustomerId: {}",
					customerId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Recent opportunities not found for CustomerId: "
							+ customerId);
		}

		beaconConverterService.convertOpportunityCurrency(opportunities,
				toCurrency);

		prepareOpportunity(opportunities);

		return opportunities;
	}

	public List<OpportunityT> findOpportunitiesByOwnerAndRole(String userId,
			String opportunityRole, List<String> toCurrency) throws Exception {
		List<OpportunityT> opportunities = null;
		logger.debug("Inside findOpportunitiesByOwnerAndRole() service");
		if (OpportunityRole.contains(opportunityRole)) {
			logger.debug("Opportunity Role is Present");
			switch (OpportunityRole.valueOf(opportunityRole)) {
			case PRIMARY_OWNER:
				logger.debug("Primary Owner Found");
				opportunities = opportunityRepository
						.findOpportunityTsByOwnerAndRole(userId, "", "");
				break;
			case SALES_SUPPORT:
				logger.debug("Sales Support Found");
				opportunities = opportunityRepository
						.findOpportunityTsByOwnerAndRole("", userId, "");
				break;
			case BID_OFFICE:
				logger.debug("Bid Office Found");
				opportunities = opportunityRepository
						.findOpportunityTsByOwnerAndRole("", "", userId);
				break;
			case ALL:
				logger.debug("ALL Found");
				opportunities = opportunityRepository
						.findOpportunityTsByOwnerAndRole(userId, userId, userId);
				break;
			}
		} else {
			logger.error("BAD_REQUEST: Invalid Opportunity Role: {}",
					opportunityRole);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role: " + opportunityRole);
		}
		opportunities = validateAndReturnOpportunitesData(opportunities, true);
		beaconConverterService.convertOpportunityCurrency(opportunities,
				toCurrency);

		prepareOpportunity(opportunities);

		return opportunities;

	}

	public List<OpportunityT> findByTaskOwnerForRole(String opportunityOwner,
			String opportunityRole, Date fromDate, Date toDate,
			List<String> toCurrency) throws Exception {
		logger.debug("Inside findByTaskOwnerForRole() service");
		if (OpportunityRole.contains(opportunityRole)) {
			List<OpportunityT> opportunities = null;
			logger.debug("Opportunity Role is Present");
			switch (OpportunityRole.valueOf(opportunityRole)) {
			case PRIMARY_OWNER:
				logger.debug("Primary Owner Found");
				opportunities = findForPrimaryOwner(opportunityOwner, true,
						fromDate, toDate);
			case SALES_SUPPORT:
				logger.debug("Sales Support Found");
				opportunities = findForSalesSupport(opportunityOwner, true,
						fromDate, toDate);
			case BID_OFFICE:
				logger.debug("Bid Office Found");
				opportunities = findForBidOffice(opportunityOwner, true,
						fromDate, toDate);
			case ALL:
				logger.debug("ALL Found");
				opportunities = new ArrayList<OpportunityT>();
				opportunities.addAll(findForPrimaryOwner(opportunityOwner,
						false, fromDate, toDate));
				opportunities.addAll(findForSalesSupport(opportunityOwner,
						false, fromDate, toDate));
				opportunities.addAll(findForBidOffice(opportunityOwner, false,
						fromDate, toDate));
				opportunities = validateAndReturnOpportunitesData(
						opportunities, true);
			}
			beaconConverterService.convertOpportunityCurrency(opportunities,
					toCurrency);

			prepareOpportunity(opportunities);

			return opportunities;
		} else {
			logger.error("BAD_REQUEST: Invalid Opportunity Role: {}",
					opportunityRole);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role: " + opportunityRole);
		}
	}

	private List<OpportunityT> findForPrimaryOwner(String userId,
			boolean isOnly, Date fromDate, Date toDate)
			throws DestinationException {
		logger.debug("Inside findForPrimaryOwner() service");
		List<OpportunityT> opportunities = opportunityRepository
				.findByOpportunityOwnerAndDealClosureDateBetween(userId,
						fromDate, toDate);

		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> validateAndReturnOpportunitesData(
			List<OpportunityT> opportunities, boolean validate)
			throws DestinationException {
		logger.debug("Inside validateAndReturnOpportunitesData() method");
		if (validate) {
			if (opportunities.size() > 0) {
				logger.debug("Opportunity List Is Present");
				return opportunities;
			} else {
				logger.error("NOT_FOUND: Opportunities not found");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Opportunities not found");
			}
		} else {
			return opportunities;
		}
	}

	private List<OpportunityT> findForBidOffice(String userId, boolean isOnly,
			Date fromDate, Date toDate) throws DestinationException {
		logger.debug("Inside findForBidOffice() service");
		UserT userT = new UserT();
		userT.setUserId(userId);
		List<OpportunityT> opportunities = opportunityRepository
				.findOpportunityTFromBidDetailsTFromBidOfficeGroupOwnerLinkTByUserId(
						userId, fromDate, toDate);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	private List<OpportunityT> findForSalesSupport(String userId,
			boolean isOnly, Date fromDate, Date toDate)
			throws DestinationException {
		logger.debug("Inside findForSalesSupport() service");
		List<OpportunityT> opportunities = opportunityRepository
				.findOpportunityTForSalesSupportOwnerWithDateBetween(userId,
						fromDate, toDate);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	public OpportunityT findByOpportunityId(String opportunityId,
			List<String> toCurrency) throws DestinationException {
		logger.debug("Inside findByOpportunityId() service");
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityId(opportunityId);
		if (opportunity != null) {
			// Add Search Keywords
			List<SearchKeywordsT> searchKeywords = searchKeywordsRepository
					.findByEntityTypeAndEntityId(
							EntityType.OPPORTUNITY.toString(),
							opportunity.getOpportunityId());
			if (searchKeywords != null && searchKeywords.size() > 0) {
				opportunity.setSearchKeywordsTs(searchKeywords);
			}
			beaconConverterService.convertOpportunityCurrency(opportunity,
					toCurrency);

			prepareOpportunity(opportunity);

			return opportunity;
		} else {
			logger.error("NOT_FOUND: Opportunity not found: {}", opportunityId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportuinty not found: " + opportunityId);
		}
	}

	// Method called from controller
	@Transactional
	public void createOpportunity(OpportunityT opportunity) throws Exception {
		logger.debug("Inside createOpportunity() service");
		if (opportunity != null) {
			opportunity.setOpportunityId(null);
			saveOpportunity(opportunity, false);
			// Invoke Asynchronous Auto Comments Thread
			processAutoComments(opportunity.getOpportunityId(), null);
		}
	}

	private OpportunityT saveOpportunity(OpportunityT opportunity,
			boolean isUpdate) throws Exception {
		logger.debug("Inside saveOpportunity() method");
		if (isUpdate) {
			deleteChildObjects(opportunity);
		}
		saveBaseObject(opportunity);
		return saveChildObject(opportunity);

	}

	private OpportunityT saveChildObject(OpportunityT opportunity)
			throws Exception {
		logger.debug("Inside saveChildObject() method");

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
						+ bidDetailsT.getModifiedBy());
				List<BidOfficeGroupOwnerLinkT> bidOfficeOwnerLinkTs = null;
				if (bidDetailsT.getBidOfficeGroupOwnerLinkTs() != null) {
					bidOfficeOwnerLinkTs = new ArrayList<BidOfficeGroupOwnerLinkT>(
							bidDetailsT.getBidOfficeGroupOwnerLinkTs());
					bidDetailsT.setBidOfficeGroupOwnerLinkTs(null);
				}
				bidDetailsTRepository.save(bidDetailsT);
				if (bidOfficeOwnerLinkTs != null
						&& bidOfficeOwnerLinkTs.size() > 0) {
					bidDetailsT
							.setBidOfficeGroupOwnerLinkTs(bidOfficeOwnerLinkTs);
				}
				logger.debug("Saved Bid Details " + bidDetailsT.getBidId());
				if (bidDetailsT.getBidOfficeGroupOwnerLinkTs() != null) {
					for (BidOfficeGroupOwnerLinkT bidOfficeOwnerLinkT : bidDetailsT
							.getBidOfficeGroupOwnerLinkTs()) {
						bidOfficeOwnerLinkT.setBidId(bidDetailsT.getBidId());
					}
					bidOfficeGroupOwnerLinkTRepository
							.save(bidOfficeOwnerLinkTs);
				}
				// As Bid details are already saved,
				opportunity.setBidDetailsTs(null);
				if (opportunity.getOpportunityId() != null) {
					List<OpportunityTimelineHistoryT> savedOpportunityTimelineHistoryTs = opportunityTimelineHistoryTRepository
							.findByOpportunityId(opportunity.getOpportunityId());
					if (savedOpportunityTimelineHistoryTs != null
							&& savedOpportunityTimelineHistoryTs.size() > 0) {

						OpportunityTimelineHistoryT opportunityTimelineHistoryT = savedOpportunityTimelineHistoryTs
								.get(savedOpportunityTimelineHistoryTs.size() - 1);
						opportunityTimelineHistoryT
								.setOpportunityId(opportunity
										.getOpportunityId());
						opportunityTimelineHistoryT.setBidId(bidDetailsT
								.getBidId());
						opportunity
								.setOpportunityTimelineHistoryTs(savedOpportunityTimelineHistoryTs);
					}
				}
			}
		}

		if (opportunity.getSearchKeywordsTs() != null) {
			for (SearchKeywordsT searchKeywordT : opportunity
					.getSearchKeywordsTs()) {
				searchKeywordT.setEntityType(EntityType.OPPORTUNITY.toString());
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
	}

	private void saveBaseObject(OpportunityT opportunity) throws Exception {
		logger.debug("Inside saveBaseObject() method");
		OpportunityT baseOpportunityT = new OpportunityT();
		baseOpportunityT.setCreatedBy(opportunity.getCreatedBy());
		baseOpportunityT.setCreatedDatetime(opportunity.getCreatedDatetime());

		baseOpportunityT.setModifiedBy(opportunity.getModifiedBy());
		baseOpportunityT.setModifiedDatetime(opportunity.getModifiedDatetime());

		baseOpportunityT.setCrmId(baseOpportunityT.getCrmId());
		baseOpportunityT.setCustomerId(opportunity.getCustomerId());
		baseOpportunityT.setDealClosureDate(opportunity.getDealClosureDate());
		baseOpportunityT.setDescriptionForWinLoss(opportunity
				.getDescriptionForWinLoss());
		baseOpportunityT.setDigitalDealValue(opportunity.getDigitalDealValue());
		baseOpportunityT.setDocumentsAttached(opportunity
				.getDocumentsAttached());
		baseOpportunityT.setEngagementDuration(opportunity
				.getEngagementDuration());
		baseOpportunityT.setEngagementStartDate(opportunity
				.getEngagementStartDate());
		baseOpportunityT.setNewLogo(opportunity.getNewLogo());
		baseOpportunityT.setOpportunityDescription(opportunity
				.getOpportunityDescription());
		baseOpportunityT.setOpportunityName(opportunity.getOpportunityName());
		baseOpportunityT.setOpportunityRequestReceiveDate(opportunity
				.getOpportunityRequestReceiveDate());
		baseOpportunityT.setOverallDealSize(opportunity.getOverallDealSize());
		baseOpportunityT.setStrategicInitiative(opportunity
				.getStrategicInitiative());
		baseOpportunityT.setDealType(opportunity.getDealType());
		baseOpportunityT.setCountry(opportunity.getCountry());
		baseOpportunityT.setDealClosureDate(opportunity.getDealClosureDate());
		baseOpportunityT.setEngagementStartDate(opportunity
				.getEngagementStartDate());
		baseOpportunityT.setEngagementDuration(opportunity
				.getEngagementDuration());
		baseOpportunityT.setOpportunityId(opportunity.getOpportunityId());
		baseOpportunityT.setOpportunityOwner(opportunity.getOpportunityOwner());
		baseOpportunityT.setSalesStageCode(opportunity.getSalesStageCode());
		opportunity.setOpportunityId(opportunityRepository.save(
				baseOpportunityT).getOpportunityId());
		logger.debug("ID " + baseOpportunityT.getOpportunityId());

	}

	// Method called from controller
	@Transactional
	public void updateOpportunity(OpportunityT opportunity) throws Exception {
		logger.debug("Inside updateOpportunity() service");
		String opportunityId = opportunity.getOpportunityId();
		if (opportunityId == null) {
			logger.error("OpportunityId is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"OpportunityId is required for update");

		}
		// Check if opportunity exists
		if (!opportunityRepository.exists(opportunityId)) {
			logger.error("Opportunity not found for update: {}", opportunityId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunity not found for update: " + opportunityId);
		}

		// Load db object before update with lazy collections populated for auto
		// comments
		OpportunityT beforeOpp = loadDbOpportunityWithLazyCollections(opportunityId);
		// Copy the db object as the above object is managed by current
		// hibernate session
		OpportunityT oldObject = (OpportunityT) DestinationUtils
				.copy(beforeOpp);

		// Update database
		OpportunityT afterOpp = saveOpportunity(opportunity, true);
		if (afterOpp != null) {
			logger.info("Opportunity has been updated successfully: "
					+ opportunityId);
			// Invoke Asynchronous Auto Comments Thread
			processAutoComments(opportunityId, oldObject);
		}
	}

	// This method is used to load database object with auto comments eligible
	// lazy collections populated
	public OpportunityT loadDbOpportunityWithLazyCollections(
			String opportunityId) throws Exception {
		logger.debug("Inside loadDbOpportunityWithLazyCollections() method");
		OpportunityT opportunity = (OpportunityT) AutoCommentsLazyLoader
				.loadLazyCollections(opportunityId,
						EntityType.OPPORTUNITY.name(), opportunityRepository,
						autoCommentsEntityTRepository,
						autoCommentsEntityFieldsTRepository, null);
		return opportunity;
	}

	private void deleteChildObjects(OpportunityT opportunity) throws Exception {
		logger.debug("Inside deleteChildObjects() method");

		if (opportunity.getDeleteConnectOpportunityLinkIdTs() != null
				&& opportunity.getDeleteConnectOpportunityLinkIdTs().size() > 0) {
			connectOpportunityLinkTRepository.delete(opportunity
					.getDeleteConnectOpportunityLinkIdTs());
			opportunity.setDeleteConnectOpportunityLinkIdTs(null);
		}

		if (opportunity.getDeleteOpportunityPartnerLinkTs() != null
				&& opportunity.getDeleteOpportunityPartnerLinkTs().size() > 0) {
			opportunityPartnerLinkTRepository.delete(opportunity
					.getDeleteOpportunityPartnerLinkTs());
			opportunity.setOpportunityPartnerLinkTs(null);
		}

		if (opportunity.getDeleteOpportunityCompetitorLinkTs() != null
				&& opportunity.getDeleteOpportunityCompetitorLinkTs().size() > 0) {
			opportunityCompetitorLinkTRepository.delete(opportunity
					.getDeleteOpportunityCompetitorLinkTs());
			opportunity.setDeleteOpportunityCompetitorLinkTs(null);
		}

		if (opportunity.getDeleteOpportunityCustomerContactLinkTs() != null
				&& opportunity.getDeleteOpportunityCustomerContactLinkTs()
						.size() > 0) {
			opportunityCustomerContactLinkTRepository.delete(opportunity
					.getDeleteOpportunityCustomerContactLinkTs());
		}

		if (opportunity.getDeleteOpportunityOfferingLinkTs() != null
				&& opportunity.getDeleteOpportunityOfferingLinkTs().size() > 0) {
			opportunityOfferingLinkTRepository.delete(opportunity
					.getDeleteOpportunityOfferingLinkTs());
		}

		if (opportunity.getDeleteOpportunitySalesSupportLinkTs() != null
				&& opportunity.getDeleteOpportunitySalesSupportLinkTs().size() > 0) {
			opportunitySalesSupportLinkTRepository.delete(opportunity
					.getDeleteOpportunitySalesSupportLinkTs());
		}

		if (opportunity.getDeleteOpportunitySubSpLinkTs() != null
				&& opportunity.getDeleteOpportunitySubSpLinkTs().size() > 0) {
			opportunitySubSpLinkTRepository.delete(opportunity
					.getDeleteOpportunitySubSpLinkTs());
		}

		if (opportunity.getDeleteOpportunityTcsAccountContactLinkTs() != null
				&& opportunity.getDeleteOpportunityTcsAccountContactLinkTs()
						.size() > 0) {
			opportunityTcsAccountContactLinkTRepository.delete(opportunity
					.getDeleteOpportunityTcsAccountContactLinkTs());
		}

		if (opportunity.getDeleteOpportunityWinLossFactorsTs() != null
				&& opportunity.getDeleteOpportunityWinLossFactorsTs().size() > 0) {
			opportunityWinLossFactorsTRepository.delete(opportunity
					.getDeleteOpportunityWinLossFactorsTs());
		}
	}

	public List<OpportunityT> findByOpportunityOwnerAndDate(String userId,
			Date fromDate, Date toDate, List<String> toCurrency)
			throws Exception {
		logger.debug("Inside findByOpportunityOwnerAndDate() service");
		List<OpportunityT> opportunityList = null;
		opportunityList = opportunityRepository
				.findByOpportunityOwnerAndDealClosureDateBetween(userId,
						fromDate, toDate);
		if ((opportunityList == null) || opportunityList.isEmpty()) {
			logger.error(
					"NOT_FOUND: No Opportunity found for the UserId:{} and Target Bid Submission date:{}, {}",
					userId, fromDate, toDate);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunity found for the UserId: " + userId
							+ " and Target Bid Submission date: " + fromDate
							+ ", " + toDate);
		}
		beaconConverterService.convertOpportunityCurrency(opportunityList,
				toCurrency);

		prepareOpportunity(opportunityList);

		return opportunityList;
	}

	private void prepareOpportunity(List<OpportunityT> opportunityTs) {
		logger.debug("Inside prepareOpportunity(List<>) method");
		if (opportunityTs != null) {
			for (OpportunityT opportunityT : opportunityTs) {
				prepareOpportunity(opportunityT);
			}
		}
	}

	private void prepareOpportunity(OpportunityT opportunityT) {
		logger.debug("Inside prepareOpportunity() method");
		setSearchKeywordTs(opportunityT);
		removeCyclicForLinkedConnects(opportunityT);
	}

	private void removeCyclicForLinkedConnects(OpportunityT opportunityT) {
		logger.debug("Inside removeCyclicForLinkedConnects() method");

		if (opportunityT != null) {
			if (opportunityT.getConnectOpportunityLinkIdTs() != null) {
				for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunityT
						.getConnectOpportunityLinkIdTs()) {
					connectOpportunityLinkIdT.getConnectT()
							.setConnectOpportunityLinkIdTs(null);
				}
			}
		}
	}

	private void setSearchKeywordTs(OpportunityT opportunityT) {
		logger.debug("Inside setSearchKeywordTs() method");
		// Add Search Keywords
		List<SearchKeywordsT> searchKeywords = searchKeywordsRepository
				.findByEntityTypeAndEntityId(EntityType.OPPORTUNITY.toString(),
						opportunityT.getOpportunityId());
		if (searchKeywords != null && searchKeywords.size() > 0) {
			opportunityT.setSearchKeywordsTs(searchKeywords);
		}
	}

	public List<OpportunityT> findOpportunitiesBySalesStageCode(
			List<String> currencies, int salesStageCode, String customerId) {
		List<OpportunityT> opportunityTs = null;
		if (customerId.equals(""))
			opportunityTs = opportunityRepository
					.findBySalesStageCode(salesStageCode);
		else
			opportunityTs = opportunityRepository
					.findBySalesStageCodeAndCustomerId(salesStageCode,
							customerId);
		prepareOpportunity(opportunityTs);
		return opportunityTs;
	}

	// This method is used to invoke asynchronous thread for auto comments
	private void processAutoComments(String opportunitytId, Object oldObject)
			throws Exception {
		logger.debug("Calling processAutoComments() method");
		AutoCommentsHelper autoCommentsHelper = new AutoCommentsHelper();
		autoCommentsHelper.setEntityId(opportunitytId);
		autoCommentsHelper.setEntityType(EntityType.OPPORTUNITY.name());
		if (oldObject != null) {
			autoCommentsHelper.setOldObject(oldObject);
		}
		autoCommentsHelper
				.setAutoCommentsEntityTRepository(autoCommentsEntityTRepository);
		autoCommentsHelper
				.setAutoCommentsEntityFieldsTRepository(autoCommentsEntityFieldsTRepository);
		autoCommentsHelper
				.setCollaborationCommentsRepository(collaborationCommentsRepository);
		autoCommentsHelper.setCrudRepository(opportunityRepository);
		autoCommentsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		// Invoking Auto Comments Task Executor Thread
		autoCommentsTaskExecutor.execute(autoCommentsHelper);

	}

	/**
	 * This is the service method which deals with the retrieval of deal values
	 * of opportunities of subordinates under a supervisor
	 * 
	 * @param supervisorUserId
	 * @return
	 */
	public List<OpportunitiesBySupervisorIdDTO> findDealValueOfOpportunitiesBySupervisorId(
			String supervisorUserId) throws Exception {
		logger.debug("Inside findDealValueOfOpportunitiesBySupervisorId() service");

		List<OpportunitiesBySupervisorIdDTO> listOfopportunitiesDTO = null;

		// Get all users under a supervisor
		List<String> users = userRepository
				.getAllSubordinatesIdBySupervisorId(supervisorUserId);

		if ((users != null) && (users.size() > 0)) {

			// Get all opportunities for the users under supervisor
			List<Object[]> opportunities = opportunityRepository
					.findDealValueOfOpportunitiesBySupervisorId(users);

			if ((opportunities != null) && (opportunities.size() > 0)) {

				listOfopportunitiesDTO = new ArrayList<OpportunitiesBySupervisorIdDTO>();

				// Iterate the result and set the response object
				for (Object[] oppDTOArray : opportunities) {
					if (oppDTOArray[0] != null && oppDTOArray[1] != null
							&& oppDTOArray[2] != null && oppDTOArray[3] != null) {

						OpportunitiesBySupervisorIdDTO opp = new OpportunitiesBySupervisorIdDTO();

						opp.setDigitalDealValue(oppDTOArray[0].toString());
						opp.setSalesStageCode(oppDTOArray[1].toString());
						opp.setSalesCount(oppDTOArray[2].toString());
						opp.setSalesStageDescription(oppDTOArray[3].toString());

						listOfopportunitiesDTO.add(opp);
					}
				}
			} else {
				logger.error(
						"NOT_FOUND: No Opportunity found for supervisor id : {}",
						supervisorUserId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No opportunity found for supervisor id : "
								+ supervisorUserId);
			}
		} else {
			logger.error(
					"NOT_FOUND: No subordinate found for supervisor id : {}",
					supervisorUserId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No subordinate found for supervisor id "
							+ supervisorUserId);
		}

		return listOfopportunitiesDTO;
	}

	public OpportunityT findOpportunityById(String oppId) {
		return opportunityRepository.findOne(oppId);
	}

	/**
	 * This service retrieves the users based on the supervisorUserId and
	 * retrieves the Team Opportunity Details
	 * 
	 * @param supervisorUserId
	 * @return
	 * @throws Exception
	 */
	public TeamOpportunityDetailsDTO findTeamOpportunityDetailsBySupervisorId(
			String supervisorUserId, int page, int count) throws Exception {

		logger.debug("Inside findOpportunityDetailsBySupervisorId() service");

		List<OpportunityDetailsDTO> listOfOpportunityDetails = null;
		List<OpportunityT> opportunities = null;
		List<OpportunityT> opportunitiesSubList = null;
		TeamOpportunityDetailsDTO teamOpportunityDetails = null;

		// Get all users under a supervisor
		List<String> users = userRepository
				.getAllSubordinatesIdBySupervisorId(supervisorUserId);

		if ((users != null) && (users.size() > 0)) {

			// Retrieve opportunities from users
			opportunities = opportunityRepository
					.findTeamOpportunityDetailsBySupervisorId(users);

			if ((opportunities != null) && (!opportunities.isEmpty())) {

				int oppSize = opportunities.size();
				teamOpportunityDetails = new TeamOpportunityDetailsDTO();
				teamOpportunityDetails.setSizeOfOpportunityDetails(oppSize);

				if (PaginationUtils.isValidPagination(page, count, oppSize)) {

					int fromIndex = PaginationUtils.getStartIndex(page, count,
							oppSize);

					// toIndex is incremented by 1 because subList toIndex
					// position
					// is excluded while constructing the sub ArrayList
					int toIndex = PaginationUtils.getEndIndex(page, count,
							oppSize) + 1;

					opportunitiesSubList = new ArrayList<OpportunityT>(
							opportunities.subList(fromIndex, toIndex));

					listOfOpportunityDetails = new ArrayList<OpportunityDetailsDTO>();

					for (OpportunityT opportunity : opportunitiesSubList) {

						OpportunityDetailsDTO teamDetails = new OpportunityDetailsDTO();
						teamDetails.setOpportunityName(opportunity
								.getOpportunityName());
						teamDetails.setCustomerName(opportunity
								.getCustomerMasterT().getCustomerName());
						teamDetails.setGeography(opportunity
								.getGeographyCountryMappingT().getGeography());
						teamDetails.setOwner(opportunity.getPrimaryOwnerUser()
								.getUserName());
						teamDetails.setSalesStageCode(opportunity
								.getSalesStageCode());
						teamDetails.setCreatedDate(new SimpleDateFormat(
								"dd-MMM-yyyy").format(opportunity
								.getCreatedDatetime()));

						listOfOpportunityDetails.add(teamDetails);
					}

					teamOpportunityDetails
							.setOpportunityDetails(listOfOpportunityDetails);
				} else {
					logger.error("BAD_REQUEST: Invalid Pagination Error",
							supervisorUserId);
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Invalid Pagination Error");
				}

			} else {
				logger.error(
						"NOT_FOUND: No Opportunity Details found for supervisor id : {}",
						supervisorUserId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Opportunity Details found for supervisor id : "
								+ supervisorUserId);
			}

		} else {
			logger.error(
					"NOT_FOUND: No subordinate found for supervisor id : {}",
					supervisorUserId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No subordinate found for supervisor id "
							+ supervisorUserId);
		}

		return teamOpportunityDetails;
	}

	public List<OpportunityT> getByOpportunities(List<String> customerIdList,
			List<Integer> salesStageCode, String strategicInitiative,
			String newLogo, double minDigitalDealValue, double maxDigitalDealValue,
			String dealCurrency, String digitalFlag, List<String> displayIou,
			List<String> country, List<String> partnerId,
			List<String> competitorName, List<String> searchKeywords,
			List<String> bidRequestType, List<String> offering,
			List<String> displaySubSp, List<String> opportunityName,
			List<String> userId) throws DestinationException {
		String searchKeywordString = searchForContaining(searchKeywords);
		String opportunityNameString = searchForContaining(opportunityName);
		customerIdList = fillIfEmpty(customerIdList);
		userId = fillIfEmpty(userId);
		displaySubSp = fillIfEmpty(displaySubSp);
		offering = fillIfEmpty(offering);
		bidRequestType = fillIfEmpty(bidRequestType);
		competitorName = fillIfEmpty(competitorName);
		partnerId = fillIfEmpty(partnerId);
		country = fillIfEmpty(country);
		displayIou = fillIfEmpty(displayIou);
		if (salesStageCode.isEmpty())
			salesStageCode.add(-1);
		String defaultDealRange = "NO";
		if (minDigitalDealValue == 0
				&& maxDigitalDealValue == Integer.MAX_VALUE)
			defaultDealRange = "YES";
		List<OpportunityT> opportunity = opportunityRepository
				.findByOpportunitiesIgnoreCaseLike(customerIdList,
						salesStageCode, strategicInitiative, newLogo,
						defaultDealRange, minDigitalDealValue,
						maxDigitalDealValue, dealCurrency, digitalFlag,
						displayIou, country, partnerId, competitorName,
						searchKeywordString, bidRequestType, offering,
						displaySubSp, opportunityNameString, userId);

		if (opportunity.isEmpty()) {
			logger.error("NOT_FOUND: No Opportunities found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunities Found.");
		}
		return opportunity;
	}

	private String searchForContaining(List<String> containingWords) {
		String actualWords = "";
		if (containingWords != null)
			for (String containgWord : containingWords) {
				containgWord = "%" + containgWord.toUpperCase() + "%";
				actualWords += containgWord + "|";
			}
		if (actualWords.length() > 2)
			actualWords = actualWords.substring(0, actualWords.length() - 1);
		return actualWords;
	}

	private List<String> fillIfEmpty(List<String> stringList) {
		if (stringList == null)
			stringList = new ArrayList<String>();
		if (stringList.isEmpty())
			stringList.add("");
		return stringList;

	}

	public List<OpportunityT> findAll(String sortBy, String order,
			Boolean isCurrentFinancialYear, int page, int count)
			throws DestinationException {

		List<OpportunityT> opportunityTs = null;
		try {
			if (isCurrentFinancialYear) {

				// Create the query and execute
				String queryString = "select OPP from OpportunityT OPP where OPP.salesStageCode < 9 or (OPP.dealClosureDate > ?1 ) order by "
						+ sortBy + " " + order;
				Query query = entityManager.createQuery(queryString)
						.setParameter(
								1,
								DateUtils.getDateFromFinancialYear(
										DateUtils.getCurrentFinancialYear(),
										true));
				opportunityTs = (List<OpportunityT>) query.getResultList();

				// Code for pagination
				if (PaginationUtils.isValidPagination(page, count,
						opportunityTs.size())) {
					int fromIndex = PaginationUtils.getStartIndex(page, count,
							opportunityTs.size());
					int toIndex = PaginationUtils.getEndIndex(page, count,
							opportunityTs.size()) + 1;
					opportunityTs = opportunityTs.subList(fromIndex, toIndex);
					logger.error("OpportunityT  after pagination size is "
							+ opportunityTs.size());
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Opportunity available for the specified page");
				}
			} else {
				// Page the opportunities for all financial year
				Page<OpportunityT> opportunityPagable = opportunityRepository
						.findAll(constructPageSpecification(page, count,
								sortBy, order));
				opportunityTs = new ArrayList<OpportunityT>();
				for (OpportunityT opportunityT : opportunityPagable) {
					opportunityTs.add(opportunityT);
				}
			}
		} catch (Exception e) {
			// Throw exceptions where Order by parameter is invalid
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}

		if (opportunityTs == null || opportunityTs.size() == 0)
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunities found");
		prepareOpportunity(opportunityTs);
		return opportunityTs;
	}

	/**
	 * Returns a Sort object which sorts in ascending order by using the
	 * parameter specified.
	 * 
	 * @param order
	 * 
	 * @return
	 */
	private Sort sortBy(String sortBy, String order) {
		if (order.equals("ASC"))
			return new Sort(Sort.Direction.ASC, sortBy);
		else
			return new Sort(Sort.Direction.DESC, sortBy);
	}

	/**
	 * Create paging for the Parameter specified. Along with order by
	 * 
	 * @param pageIndex
	 *            the pageIndex (starting from 0)
	 * @param count
	 *            The size of the page
	 * @param sortBy
	 *            The column to sort
	 * @param order
	 *            Specify ascending ASC or descending order DESC
	 * @return The Pagable that is plugged into repository method
	 */
	private Pageable constructPageSpecification(int pageIndex, int count,
			String sortBy, String order) {
		Pageable pageSpecification = new PageRequest(pageIndex, count, sortBy(
				sortBy, order));
		return pageSpecification;
	}
}
