package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.tcs.destination.bean.OpportunityNameKeywordSearch;
import com.tcs.destination.bean.OpportunityOfferingLinkT;
import com.tcs.destination.bean.OpportunityPartnerLinkT;
import com.tcs.destination.bean.OpportunitySalesSupportLinkT;
import com.tcs.destination.bean.OpportunitySubSpLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.OpportunityTimelineHistoryT;
import com.tcs.destination.bean.OpportunityWinLossFactorsT;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.QueryBufferDTO;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.TeamOpportunityDetailsDTO;
import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.controller.JobLauncherController;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.BidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.ConnectOpportunityLinkTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CountryRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OfferingRepository;
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
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.WinLossMappingRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OpportunityRole;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.enums.WorkflowStatus;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.AutoCommentsHelper;
import com.tcs.destination.helper.AutoCommentsLazyLoader;
import com.tcs.destination.helper.NotificationHelper;
import com.tcs.destination.helper.NotificationsLazyLoader;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.PropertyUtil;

import static com.tcs.destination.utils.ErrorConstants.ERR_INAC_01;

@Service
public class OpportunityService {

	private static final int ONE_DAY_IN_MILLIS = 86400000;
	
	private static final String OPPORTUNITY_QUERY_PREFIX = "select distinct(OPP.opportunity_id) from opportunity_t OPP "
			+ "LEFT JOIN geography_country_mapping_t GCMT on OPP.country =GCMT.country "
			+ "LEFT JOIN customer_master_t CMT on OPP.customer_id = CMT.customer_id  "
			+ "LEFT JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou "
			+ "LEFT JOIN opportunity_sub_sp_link_t OSSLT on OSSLT.opportunity_id=OPP.opportunity_id "
			+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSLT.sub_sp=SSMT.sub_sp where";

	private static final String OPPORTUNITY_GEO_INCLUDE_COND_PREFIX = "GCMT.geography in (";
	private static final String OPPORTUNITY_SUBSP_INCLUDE_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String OPPORTUNITY_IOU_INCLUDE_COND_PREFIX = "ICMT.display_iou in (";
	private static final String OPPORTUNITY_CUSTOMER_INCLUDE_COND_PREFIX = "CMT.customer_name in (";
	
	private String bidId = null;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityService.class);

	// Required for auto comments
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	OpportunityRepository opportunityRepository;
	
	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

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

	// Required beans for Notifications - start
	@Autowired
	NotificationsEventFieldsTRepository notificationEventFieldsTRepository;

	@Autowired
	UserNotificationsRepository userNotificationsTRepository;

	@Autowired
	UserNotificationSettingsRepository userNotificationSettingsRepo;

	@Autowired
	ThreadPoolTaskExecutor notificationsTaskExecutor;
	// Required beans for Notifications - end

	@Autowired
	UserRepository userRepository;

	@Autowired
	CollaborationCommentsService collaborationCommentsService;

	@Autowired
	NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;

	@Autowired
	FollowedService followService;
	
	@Autowired
	JobLauncherController jobLauncherController;

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	ConnectRepository connectRepository;
	
	@Autowired
	ContactRepository contactRepository;

	@Autowired
	OfferingRepository offeringRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	WinLossMappingRepository winlossFactorRepository;
	
	@Autowired
	CompetitorRepository competitorRepository;
	
	@Autowired
	PartnerRepository partnerRepository;
	
	@Autowired
	WorkflowRequestTRepository workflowRequestRepository;
	
	QueryBufferDTO queryBufferDTO=new QueryBufferDTO(); //DTO object used to pass query string and parameters for applying access priviledge

	public PaginatedResponse findByOpportunityName(String nameWith,
			String customerId, List<String> toCurrency, boolean isAjax,
			String userId, int page, int count) throws Exception {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count, new Sort(
				Sort.Direction.DESC, "modifiedDatetime"));
		logger.debug("Inside findByOpportunityName() service");
		if (!userId
				.equals(DestinationUtils.getCurrentUserDetails().getUserId()))
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User Id and Login User detail does not match");

		Page<OpportunityT> opportunities = null;
		if (customerId.isEmpty()) {
			opportunities = opportunityRepository
					.findByOpportunityNameIgnoreCaseLikeOrderByModifiedDatetimeDesc("%" + nameWith + "%",
							pageable);
		} else {
			opportunities = opportunityRepository
					.findByOpportunityNameIgnoreCaseLikeAndCustomerIdOrderByModifiedDatetimeDesc("%"
							+ nameWith + "%", customerId, pageable);
		}
		List<OpportunityT> opportunityTs = opportunities.getContent();

		paginatedResponse.setTotalCount(opportunities.getTotalElements());

		if (opportunityTs.isEmpty()) {
			logger.error(
					"NOT_FOUND: Opportunities not found with the given name: {}",
					nameWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunities not found with the given name: " + nameWith);
		}
		if (!isAjax) {
			prepareOpportunity(opportunityTs);
			beaconConverterService.convertOpportunityCurrency(opportunityTs,
					toCurrency);
		} else {
			// Don't perform the check and hide sensitive information without
			// checking the privilege as it is might reduce te performance.
			preventSensitiveInfo(opportunityTs);
		}

		paginatedResponse.setOpportunityTs(opportunityTs);
		return paginatedResponse;
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

		prepareOpportunity(opportunities);
		
		beaconConverterService.convertOpportunityCurrency(opportunities,
				toCurrency);

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

		prepareOpportunity(opportunities);
		
		beaconConverterService.convertOpportunityCurrency(opportunities,
				toCurrency);

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

			prepareOpportunity(opportunities);
			
			beaconConverterService.convertOpportunityCurrency(opportunities,
					toCurrency);

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
			List<String> toCurrency) throws Exception {
		logger.debug("Inside findByOpportunityId() service");	
		
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityId(opportunityId);
		if (opportunity != null) {
			// Add Search Keywords
			List<SearchKeywordsT> searchKeywords = searchKeywordsRepository
					.findByEntityTypeAndEntityId(
							EntityType.OPPORTUNITY.toString(),
							opportunity.getOpportunityId());

			prepareOpportunity(opportunity, null);

			beaconConverterService.convertOpportunityCurrency(opportunity,
					toCurrency);
			//Getting the workflow request in order to check whether if the opportunity is placed for reopen request
			WorkflowRequestT workflowRequestPending = workflowRequestRepository
					.findByEntityTypeIdAndEntityIdAndStatus(
							EntityTypeId.OPPORTUNITY.getType(), opportunityId,
							WorkflowStatus.PENDING.getStatus());
				opportunity.setWorkflowRequest(workflowRequestPending);
			return opportunity;
		} else {
			logger.error("NOT_FOUND: Opportunity not found: {}", opportunityId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportuinty not found: " + opportunityId);
		}
	} 

	private void restrictOpportunity(OpportunityT opportunity) {
		opportunity.setDigitalDealValue(null);
		opportunity.setOverallDealSize(null);

	}

	private void restrictOpportunity(List<OpportunityT> opportunities) {
		if (opportunities != null && opportunities.size() > 0) {
			for (OpportunityT opportunityT : opportunities) {
				restrictOpportunity(opportunityT);
			}
		}
	}

	private boolean isUserOwner(String userId, OpportunityT opportunity) {
		if (opportunity.getOpportunityOwner().equals(userId))
			return true;
		else {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				if (opportunitySalesSupportLinkT.getSalesSupportOwner().equals(
						userId))
					return true;
			}
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				if (opportunitySalesSupportLinkT.getSalesSupportOwner().equals(
						userId))
					return true;
			}
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetailsT
						.getBidOfficeGroupOwnerLinkTs()) {
					if (bidOfficeGroupOwnerLinkT.getBidOfficeGroupOwner()
							.equals(userId)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	// Method called from controller
	@Transactional
	public void createOpportunity(OpportunityT opportunity,
			boolean isBulkDataLoad, String bidRequestType, String actualSubmissionDate) throws Exception {
		logger.debug("Inside createOpportunity() service");
		OpportunityT createdOpportunity = null;
		if (opportunity != null) {
			opportunity.setOpportunityId(null);
			opportunity.setCreatedBy(DestinationUtils.getCurrentUserDetails().getUserId());
			opportunity.setModifiedBy(DestinationUtils.getCurrentUserDetails().getUserId());
			String userId = DestinationUtils.getCurrentUserDetails().getUserId();
			UserT user = userRepository.findByUserId(userId);
			String userGroup = user.getUserGroup();
			createdOpportunity = saveOpportunity(opportunity, false, userGroup,
					null);
			if (!isBulkDataLoad) {
				// Invoke Asynchronous Auto Comments Thread
				processAutoComments(opportunity.getOpportunityId(), null);
				// Invoke Asynchronous Notification Thread
				processNotifications(opportunity.getOpportunityId(), null);
			} else {
				// This statement is to update the opportunity timeline history
				saveOpportunityTimelineHistoryForUpload(createdOpportunity, bidRequestType, actualSubmissionDate);
			}
		}
	}

	/**
	 * This method is used to update Opportunity Timeline History.
	 * Sales Stage Codes from 6-13 are only updated here with 
	 * Sales Stage Codes based on the Bid Request Type
	 * 
	 * @param createdOpportunity
	 * @param bidRequestType
	 * @param actualSubmissionDate
	 * @throws Exception
	 */
	private void saveOpportunityTimelineHistoryForUpload(
			OpportunityT createdOpportunity, String bidRequestType, String actualSubmissionDate) throws Exception{
		try {
			
		switch(createdOpportunity.getSalesStageCode()){
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:{
				saveOppTimelineHistoryInUpload(createdOpportunity, 5);
				break;
			}
			case 11:
			case 12:
			case 13:{
				if(!StringUtils.isEmpty(bidRequestType)) {
					if(bidRequestType.equalsIgnoreCase("RFI")||
							bidRequestType.equalsIgnoreCase("RFQ")||
								bidRequestType.equalsIgnoreCase("Approach Note")){
						if(StringUtils.isEmpty(actualSubmissionDate)){
							saveOppTimelineHistoryInUpload(createdOpportunity, 2);
						} else if(!StringUtils.isEmpty(actualSubmissionDate)) {
							saveOppTimelineHistoryInUpload(createdOpportunity, 3);
						}
					} else if(bidRequestType.equalsIgnoreCase("RFP")||
							bidRequestType.equalsIgnoreCase("Proactive")){
						if(StringUtils.isEmpty(actualSubmissionDate)){
							saveOppTimelineHistoryInUpload(createdOpportunity, 4);
						} else if(!StringUtils.isEmpty(actualSubmissionDate)) {
							saveOppTimelineHistoryInUpload(createdOpportunity, 5);
						}
					}
				}
				break;
			}
			default:
				break;
		}
		}catch(Exception e){
			throw new DestinationException(HttpStatus.BAD_REQUEST,"Record Saved! Error while updating Opportunity Timeline History");
		} finally{
			bidId=null;
		}
		
	}
	
	private void saveOppTimelineHistoryInUpload(OpportunityT createdOpportunity, int salesStageCode){
		OpportunityTimelineHistoryT history = new OpportunityTimelineHistoryT();
		
		history.setOpportunityId(createdOpportunity.getOpportunityId());
		history.setSalesStageCode(salesStageCode);
		if(!StringUtils.isEmpty(bidId)) {
			history.setBidId(bidId);
		}
		history.setUserUpdated(DestinationUtils.getCurrentUserDetails().getUserId());
		opportunityTimelineHistoryTRepository.save(history);
	}

	/** 
	 * This method is used to save an opportunity and also used to edit an existing opportunity
	 * 
	 * @param opportunity
	 * @param isUpdate
	 * @param userGroup
	 * @param opportunityBeforeEdit
	 * @return
	 * @throws Exception
	 */
	private OpportunityT saveOpportunity(OpportunityT opportunity,
			boolean isUpdate, String userGroup,
			OpportunityT opportunityBeforeEdit) throws Exception {
		logger.debug("Inside saveOpportunity() method");
		validateOpportunityPrimarySubSp(opportunity);
		if (userGroup.equals(UserGroup.PRACTICE_HEAD.getValue())
				|| userGroup.equals(UserGroup.PRACTICE_OWNER.getValue())) {
			Set<String> owners = new HashSet<String>();
			owners.add(opportunity.getOpportunityOwner());
			if (opportunity.getOpportunitySalesSupportLinkTs() != null
					&& !opportunity.getOpportunitySalesSupportLinkTs()
							.isEmpty()) {
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
						.getOpportunitySalesSupportLinkTs()) {
					owners.add(opportunitySalesSupportLinkT
							.getSalesSupportOwner());
				}
			}

			if (opportunity.getBidDetailsTs() != null
					&& !opportunity.getBidDetailsTs().isEmpty()) {
				for (BidDetailsT bidDetails : opportunity.getBidDetailsTs()) {
					if (bidDetails.getBidOfficeGroupOwnerLinkTs() != null) {
						for (BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT : bidDetails
								.getBidOfficeGroupOwnerLinkTs()) {
							owners.add(bidOfficeGroupOwnerLinkT
									.getBidOfficeGroupOwner());
						}
					}
				}
			}

			if (opportunity.getOpportunityId() != null) {
				owners.addAll(opportunityRepository.getAllOwners(opportunity
						.getOpportunityId()));
			}
			if (opportunity.getDeleteOpportunitySalesSupportLinkTs() != null
					&& opportunity.getDeleteOpportunitySalesSupportLinkTs()
							.size() > 0) {
				for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
						.getDeleteOpportunitySalesSupportLinkTs()) {

					// int index= owners.indexOf();
                 if(!opportunity.getOpportunityOwner().equals(opportunitySalesSupportLinkTRepository
							.findSalesSupportOwner(opportunitySalesSupportLinkT
									.getOpportunitySalesSupportLinkId()))) {
                	 owners.remove(opportunitySalesSupportLinkTRepository
 							.findSalesSupportOwner(opportunitySalesSupportLinkT
 									.getOpportunitySalesSupportLinkId()));
                 }
					
				}
			}

			if (owners != null && !owners.isEmpty()) {
				if (!isOwnersAreBDMorBDMSupervisor(owners)) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							PropertyUtil.getProperty(ERR_INAC_01));
				}
			}

		}

		validateInactiveIndicators(opportunity);
		
		if (isUpdate) {
			deleteChildObjects(opportunity);
		}
		saveBaseObject(opportunity);
		return saveChildObject(opportunity);

	}

	/**
	* This method validates primary SubSp for a opportunity
	* 
	* @param opportunity
	* @return
	*/
	private void validateOpportunityPrimarySubSp(OpportunityT opportunity) {
		logger.info("Inside validation for opportunity primary SubSp");
		if(opportunity.getOpportunitySubSpLinkTs()!=null){
			int countOfPrimarySubSp = 0;
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
					.getOpportunitySubSpLinkTs()) {
				if (opportunitySubSpLinkT.isSubspPrimary()) {
					countOfPrimarySubSp++;
				}
			}
			if (countOfPrimarySubSp > 1) {
				logger.error("Only one primary SubSp is allowed");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Only one SubSp can be primary");
			} else if (countOfPrimarySubSp == 0) {
				logger.error("There should be atleast one primary SubSp");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"No primary SubSp");
			}
		}
		logger.info("End of validation for opportunity primary SubSp");
	}
	
	/**
	 * validate a opertunity which has any inactive fields
	 * @param opportunity
	 */
	public void validateInactiveIndicators(OpportunityT opportunity) {
		//createdBy
		String createdBy = opportunity.getCreatedBy();
		if(StringUtils.isNotBlank(createdBy) && userRepository.findByActiveTrueAndUserId(createdBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The user createdBy is inactive");
		}
		
		// modifiedBy,
		String modifiedBy = opportunity.getModifiedBy();
		if(StringUtils.isNotBlank(modifiedBy) && userRepository.findByActiveTrueAndUserId(modifiedBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The user modifiedBy is inactive");
		}
		
		// customerId,
		String customerId = opportunity.getCustomerId();
		if(StringUtils.isNotBlank(customerId) && customerRepository.findByActiveTrueAndCustomerId(customerId) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The customer is inactive");
		}
				
		// country
		String country = opportunity.getCountry();
		if(StringUtils.isNotBlank(country) && countryRepository.findByActiveTrueAndCountry(country) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The country is inactive");
		}
		
		// opportunityOwner,
		String opportunityOwner = opportunity.getOpportunityOwner();
		if(StringUtils.isNotBlank(opportunityOwner) && userRepository.findByActiveTrueAndUserId(opportunityOwner) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The opportunity owner is inactive");
		}
		
		// opportunityCompetitorLinkTs,
		List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs = opportunity.getOpportunityCompetitorLinkTs();
		if(CollectionUtils.isNotEmpty(opportunityCompetitorLinkTs)) {
			for (OpportunityCompetitorLinkT compLink : opportunityCompetitorLinkTs) {
				String competitorName = compLink.getCompetitorName();
				if(StringUtils.isNotBlank(competitorName) && competitorRepository.findByActiveTrueAndCompetitorName(competitorName) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The competitor is inactive");
				}
			}
		}
		
		
		// opportunityCustomerContactLinkTs,
		List<OpportunityCustomerContactLinkT> oppCustomerContactLinkTs = opportunity.getOpportunityCustomerContactLinkTs();
		if(CollectionUtils.isNotEmpty(oppCustomerContactLinkTs)) {
			for (OpportunityCustomerContactLinkT contact : oppCustomerContactLinkTs) {
				String contactId = contact.getContactId();
				if(StringUtils.isNotBlank(contactId) && contactRepository.findByActiveTrueAndContactId(contactId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The customer contact is inactive");
				}
			}
		}
		
		// opportunityOfferingLinkTs,
		List<OpportunityOfferingLinkT> connectOfferingLinkTs = opportunity.getOpportunityOfferingLinkTs();
		if(CollectionUtils.isNotEmpty(connectOfferingLinkTs)) {
			for (OpportunityOfferingLinkT offeringLink : connectOfferingLinkTs) {
				String offering = offeringLink.getOffering();
				if(StringUtils.isNotBlank(offering) && offeringRepository.findByActiveTrueAndOffering(offering) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The offering is inactive");
				}
			}
		}
		
		//List<OpportunityPartnerLinkT> opportunityPartnerLinkTs,
		List<OpportunityPartnerLinkT> opportunityPartnerLinkTs = opportunity.getOpportunityPartnerLinkTs();
		if(CollectionUtils.isNotEmpty(opportunityPartnerLinkTs)) {
			for (OpportunityPartnerLinkT partnerLink : opportunityPartnerLinkTs) {
				String partnerId = partnerLink.getPartnerId();
				if(StringUtils.isNotBlank(partnerId) && partnerRepository.findByActiveTrueAndPartnerId(partnerId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The partner is inactive");
				}
			}
		}
		
		
		//opportunitySubSpLinkTs,
		List<OpportunitySubSpLinkT> oppSubSpLinkTs = opportunity.getOpportunitySubSpLinkTs();
		if(CollectionUtils.isNotEmpty(oppSubSpLinkTs)) {
			for (OpportunitySubSpLinkT subSpLink : oppSubSpLinkTs) {
				String subSp = subSpLink.getSubSp();
				if(StringUtils.isNotBlank(subSp) && subSpRepository.findByActiveTrueAndSubSp(subSp) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The subsp is inactive");
				}
			}
		}
		
		
		// opportunityTcsAccountContactLinkTs,
		List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs = opportunity.getOpportunityTcsAccountContactLinkTs();
		if(CollectionUtils.isNotEmpty(opportunityTcsAccountContactLinkTs)) {
			for (OpportunityTcsAccountContactLinkT contactLink : opportunityTcsAccountContactLinkTs) {
				String contactId = contactLink.getContactId();
				if(StringUtils.isNotBlank(contactId) && contactRepository.findByActiveTrueAndContactId(contactId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The account contact is inactive");
				}
			}
		}
		
		// opportunityWinLossFactorsTs,
		List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs = opportunity.getOpportunityWinLossFactorsTs();
		if(CollectionUtils.isNotEmpty(opportunityWinLossFactorsTs)) {
			for (OpportunityWinLossFactorsT oppWLFactor : opportunityWinLossFactorsTs) {
				String wlFactor = oppWLFactor.getWinLossFactor();
				if(StringUtils.isNotBlank(wlFactor) && winlossFactorRepository.findByActiveTrueAndWinLossFactor(wlFactor) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The account contact is inactive");
				}
			}
		}

		// OpportunitySalesSupportLink,
		List<OpportunitySalesSupportLinkT> opportunitySaleSupOwnTs = opportunity.getOpportunitySalesSupportLinkTs();
		if(CollectionUtils.isNotEmpty(opportunitySaleSupOwnTs)) {
			for (OpportunitySalesSupportLinkT oppWLFactor : opportunitySaleSupOwnTs) {
				String userId = oppWLFactor.getSalesSupportOwner();
				if(StringUtils.isNotBlank(userId) && userRepository.findByActiveTrueAndUserId(userId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The sales support owner is inactive");
				}
			}
		}

		// BidDetails,
		List<BidDetailsT> bidDetailsT = opportunity.getBidDetailsTs();
		if(CollectionUtils.isNotEmpty(bidDetailsT)) {
			for (BidDetailsT bidDetail : bidDetailsT) {
				List<BidOfficeGroupOwnerLinkT> bidofficeGrpOwners = bidDetail.getBidOfficeGroupOwnerLinkTs();
				if(CollectionUtils.isNotEmpty(bidofficeGrpOwners)) {
					for (BidOfficeGroupOwnerLinkT bidgrpOwner : bidofficeGrpOwners) {
						String userId = bidgrpOwner.getBidOfficeGroupOwner();
						if(StringUtils.isNotBlank(userId) && userRepository.findByActiveTrueAndUserId(userId) == null) {
							throw new DestinationException(HttpStatus.BAD_REQUEST, "The Bid Office Group Owner is inactive");
						}
					}
				}
			}
		}

	}



	private OpportunityT saveChildObject(OpportunityT opportunity)
			throws Exception {
		logger.debug("Inside saveChildObject() method");

		//Getting the userId from the session
				String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		if (opportunity.getOpportunityCustomerContactLinkTs() != null) {
			for (OpportunityCustomerContactLinkT customerContact : opportunity
					.getOpportunityCustomerContactLinkTs()) {
				customerContact
						.setOpportunityId(opportunity.getOpportunityId());
				customerContact.setCreatedBy(userId);
				customerContact.setModifiedBy(userId);
			}
		}

		if (opportunity.getOpportunityTcsAccountContactLinkTs() != null) {
			for (OpportunityTcsAccountContactLinkT tcsContact : opportunity
					.getOpportunityTcsAccountContactLinkTs()) {
				tcsContact.setOpportunityId(opportunity.getOpportunityId());
				tcsContact.setCreatedBy(userId);
				tcsContact.setModifiedBy(userId);
			}
		}

		if (opportunity.getOpportunityPartnerLinkTs() != null) {
			for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunity
					.getOpportunityPartnerLinkTs()) {
				opportunityPartnerLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityPartnerLinkT.setCreatedBy(userId);
				opportunityPartnerLinkT.setModifiedBy(userId);
			}
		}

		if (opportunity.getOpportunityCompetitorLinkTs() != null) {
			for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
					.getOpportunityCompetitorLinkTs()) {
				opportunityCompetitorLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityCompetitorLinkT.setCreatedBy(userId);
				opportunityCompetitorLinkT.setModifiedBy(userId);
			}
		}

		if (opportunity.getOpportunitySubSpLinkTs() != null) {
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
					.getOpportunitySubSpLinkTs()) {
				opportunitySubSpLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunitySubSpLinkT.setCreatedBy(userId);
				opportunitySubSpLinkT.setModifiedBy(userId);
			}
		}

		if (opportunity.getOpportunityOfferingLinkTs() != null) {
			for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity
					.getOpportunityOfferingLinkTs()) {
				opportunityOfferingLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityOfferingLinkT.setCreatedBy(userId);
				opportunityOfferingLinkT.setModifiedBy(userId);
			}
		}

		if (opportunity.getConnectOpportunityLinkIdTs() != null) {
			for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunity
					.getConnectOpportunityLinkIdTs()) {
				connectOpportunityLinkIdT.setOpportunityId(opportunity
						.getOpportunityId());
				connectOpportunityLinkIdT.setCreatedBy(userId);
				connectOpportunityLinkIdT.setModifiedBy(userId);
			}
		}

		if (opportunity.getOpportunitySalesSupportLinkTs() != null) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				opportunitySalesSupportLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunitySalesSupportLinkT.setCreatedBy(userId);
				opportunitySalesSupportLinkT.setModifiedBy(userId);
			}
		}

		if (opportunity.getNotesTs() != null) {
			for (NotesT notesT : opportunity.getNotesTs()) {
				notesT.setOpportunityId(opportunity.getOpportunityId());
				notesT.setUserUpdated(userId);
			}
		}
		
		if (opportunity.getBidDetailsTs() != null) {
			for (BidDetailsT bidDetailsT : opportunity.getBidDetailsTs()) {
				bidDetailsT.setOpportunityId(opportunity.getOpportunityId());
				bidDetailsT.setCreatedBy(userId);
				bidDetailsT.setModifiedBy(userId);
				logger.debug("Saving Bid Details by "
						+ bidDetailsT.getModifiedBy());
				List<BidOfficeGroupOwnerLinkT> bidOfficeOwnerLinkTs = null;
				if (bidDetailsT.getBidOfficeGroupOwnerLinkTs() != null) {
					bidOfficeOwnerLinkTs = new ArrayList<BidOfficeGroupOwnerLinkT>(
							bidDetailsT.getBidOfficeGroupOwnerLinkTs());
					bidDetailsT.setBidOfficeGroupOwnerLinkTs(null);
				}
				bidDetailsTRepository.save(bidDetailsT);
				bidId = bidDetailsT.getBidId();
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
						bidOfficeOwnerLinkT.setCreatedBy(userId);
						bidOfficeOwnerLinkT.setModifiedBy(userId);
					}
					bidOfficeGroupOwnerLinkTRepository
							.save(bidOfficeOwnerLinkTs);
				}
				
				// As Bid details are already saved,
				opportunity.setBidDetailsTs(null);
				if (opportunity.getOpportunityId() != null) {
					List<OpportunityTimelineHistoryT> savedOpportunityTimelineHistoryTs = opportunityTimelineHistoryTRepository
							.findByOpportunityIdOrderByUpdatedDatetimeAsc(opportunity
									.getOpportunityId());
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
						opportunity.setCreatedBy(userId);
						opportunity.setModifiedBy(userId);
					}
				}
			}
		}

		if (opportunity.getSearchKeywordsTs() != null) {
			for (SearchKeywordsT searchKeywordT : opportunity
					.getSearchKeywordsTs()) {
				searchKeywordT.setEntityType(EntityType.OPPORTUNITY.toString());
				searchKeywordT.setEntityId(opportunity.getOpportunityId());
				searchKeywordT.setCreatedModifiedBy(userId);
				searchKeywordsRepository.save(searchKeywordT);
			}
		}

		if (opportunity.getOpportunityWinLossFactorsTs() != null) {
			for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
					.getOpportunityWinLossFactorsTs()) {
				opportunityWinLossFactorsT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityWinLossFactorsT.setCreatedBy(userId);
				opportunityWinLossFactorsT.setModifiedBy(userId);
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
		baseOpportunityT.setDealClosureComments(opportunity
				.getDealClosureComments());
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
		baseOpportunityT.setStrategicDeal(opportunity
				.getStrategicDeal());
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
	public void updateOpportunity(OpportunityT opportunity, OpportunityT opportunityBeforeEdit) throws Exception {
		String userId=DestinationUtils.getCurrentUserDetails().getUserId();
		String opportunityId = opportunity.getOpportunityId();
		opportunity.setCreatedBy(userId);
		opportunity.setModifiedBy(userId);
		logger.debug("Inside updateOpportunity() service");
		
		UserT user = userRepository.findByUserId(userId);
		String userGroup = user.getUserGroup();
		
		if (!userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {

			if (!isEditAccessRequiredForOpportunity(opportunityBeforeEdit,
					userGroup, userId)) {
				throw new DestinationException(HttpStatus.FORBIDDEN,
						"User is not authorized to edit this opportunity");
			}
		}

		// Load db object before update with lazy collections populated for auto
		// comments
		OpportunityT beforeOpp = loadDbOpportunityWithLazyCollections(opportunityId);
		// Copy the db object as the above object is managed by current
		// hibernate session
		OpportunityT oldObject = (OpportunityT) DestinationUtils
				.copy(beforeOpp);

		// deal closure comments is mandatory for sales stage codes (11/12/13) 
		if(opportunity.getSalesStageCode() == 11 || opportunity.getSalesStageCode() == 12 || opportunity.getSalesStageCode() == 13){
		if((opportunity.getDealClosureComments()==null) && StringUtils.isEmpty(opportunity.getDealClosureComments())){
			logger.error("Deal closure comments is mandatory for the opportuniy for sales stage codes (11,12 and 13)");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Deal closure comments is mandatory for the opportuniy for sales stage codes (11,12 and 13)");
		}
		}
		// Update database
		OpportunityT afterOpp = saveOpportunity(opportunity, true, userGroup,
				opportunityBeforeEdit);
		if (afterOpp != null) {
			logger.info("Opportunity has been updated successfully: "
					+ opportunityId);
			// Invoke Asynchronous Auto Comments Thread
			processAutoComments(opportunityId, oldObject);
			// Invoke Asynchronous Notifications Thread
			processNotifications(opportunityId, oldObject);
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
		if (opportunity != null) {
			opportunity = (OpportunityT) NotificationsLazyLoader
					.loadLazyCollections(opportunityId,
							EntityType.OPPORTUNITY.name(),
							opportunityRepository,
							notificationEventFieldsTRepository, null);
		}
		return opportunity;
	}

	private void deleteChildObjects(OpportunityT opportunity) throws Exception {
		logger.debug("Inside deleteChildObjects() method");

		if (opportunity.getDeleteConnectOpportunityLinkIdTs() != null
				&& opportunity.getDeleteConnectOpportunityLinkIdTs().size() > 0) {
			for(ConnectOpportunityLinkIdT connectOpportunityLinkIdT: opportunity
					.getDeleteConnectOpportunityLinkIdTs()){
			connectOpportunityLinkTRepository.delete(connectOpportunityLinkIdT.getConnectOpportunityLinkId());
			}
			opportunity.setDeleteConnectOpportunityLinkIdTs(null);
		}

		if (opportunity.getDeleteOpportunityPartnerLinkTs() != null
				&& opportunity.getDeleteOpportunityPartnerLinkTs().size() > 0) {
			for(OpportunityPartnerLinkT opportunityPartnerLinkT: opportunity.getDeleteOpportunityPartnerLinkTs()){
			opportunityPartnerLinkTRepository.delete(opportunityPartnerLinkT
					.getOpportunityPartnerLinkId());
		}
			opportunity.setOpportunityPartnerLinkTs(null);
		}

		if (opportunity.getDeleteOpportunityCompetitorLinkTs() != null
				&& opportunity.getDeleteOpportunityCompetitorLinkTs().size() > 0) {
			for(OpportunityCompetitorLinkT opportunityCompetitorLinkT: opportunity.getDeleteOpportunityCompetitorLinkTs()){
			opportunityCompetitorLinkTRepository.delete(opportunityCompetitorLinkT.getOpportunityCompetitorLinkId());
			}
			opportunity.setDeleteOpportunityCompetitorLinkTs(null);
		}

		if (opportunity.getDeleteOpportunityCustomerContactLinkTs() != null
				&& opportunity.getDeleteOpportunityCustomerContactLinkTs()
						.size() > 0) {
			for(OpportunityCustomerContactLinkT opportunityCustomerContactLinkT : opportunity.getDeleteOpportunityCustomerContactLinkTs())
			{
			opportunityCustomerContactLinkTRepository.delete(opportunityCustomerContactLinkT.getOpportunityCustomerContactLinkId());
			}
		}

		if (opportunity.getDeleteOpportunityOfferingLinkTs() != null
				&& opportunity.getDeleteOpportunityOfferingLinkTs().size() > 0) {
			for(OpportunityOfferingLinkT opportunityOfferingLinkT:opportunity.getDeleteOpportunityOfferingLinkTs()){
			opportunityOfferingLinkTRepository.delete(opportunityOfferingLinkT.getOpportunityOfferingLinkId());
		}
			}

		if (opportunity.getDeleteOpportunitySalesSupportLinkTs() != null
				&& opportunity.getDeleteOpportunitySalesSupportLinkTs().size() > 0) {
			for(OpportunitySalesSupportLinkT opportunitySalesSupportLinkT:opportunity.getDeleteOpportunitySalesSupportLinkTs()){
				
			opportunitySalesSupportLinkTRepository.delete(opportunitySalesSupportLinkT.getOpportunitySalesSupportLinkId());
			}
		}

		if (opportunity.getDeleteOpportunitySubSpLinkTs() != null
				&& opportunity.getDeleteOpportunitySubSpLinkTs().size() > 0) {
			for(OpportunitySubSpLinkT opportunitySubSpLinkT:opportunity.getDeleteOpportunitySubSpLinkTs()){
				opportunitySubSpLinkTRepository.delete(opportunitySubSpLinkT.getOpportunitySubSpLinkId());
			}
		}

		if (opportunity.getDeleteOpportunityTcsAccountContactLinkTs() != null
				&& opportunity.getDeleteOpportunityTcsAccountContactLinkTs()
						.size() > 0) {
			for(OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT:opportunity.getDeleteOpportunityTcsAccountContactLinkTs())
			opportunityTcsAccountContactLinkTRepository.delete(opportunityTcsAccountContactLinkT.getOpportunityTcsAccountContactLinkId());
		}

		if (opportunity.getDeleteOpportunityWinLossFactorsTs() != null
				&& opportunity.getDeleteOpportunityWinLossFactorsTs().size() > 0) {
			for(OpportunityWinLossFactorsT opportunityWinLossFactorsT: opportunity.getDeleteOpportunityWinLossFactorsTs())
			opportunityWinLossFactorsTRepository.delete(opportunityWinLossFactorsT.getOpportunityWinLossFactorsId());
		}

		if (opportunity.getDeleteSearchKeywordsTs() != null
				&& opportunity.getDeleteSearchKeywordsTs().size() > 0) {
			for(SearchKeywordsT searchKeywordsT:opportunity.getDeleteSearchKeywordsTs())
			searchKeywordsRepository.delete(searchKeywordsT.getSearchKeywordsId());
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
		

		prepareOpportunity(opportunityList);
		
		beaconConverterService.convertOpportunityCurrency(opportunityList,
				toCurrency);

		return opportunityList;
	}

	private void prepareOpportunity(List<OpportunityT> opportunityTs)
			throws DestinationException {
		logger.debug("Inside prepareOpportunity(List<>) method");
		List<String> opportunityIds = new ArrayList<String>();
		for (OpportunityT opportunityT : opportunityTs) {
			opportunityIds.add(opportunityT.getOpportunityId());
		}
		try {
			List<String> previledgedOpportuniyies = getPriviledgedOpportunityId(opportunityIds);

			if (opportunityTs != null) {
				for (OpportunityT opportunityT : opportunityTs) {
					prepareOpportunity(opportunityT, previledgedOpportuniyies);
				}
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	private void prepareOpportunity(OpportunityT opportunityT,
			List<String> previledgedOppIdList) throws DestinationException {
		logger.debug("Inside prepareOpportunity() method");

		try {
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			String userGroup = userRepository.findByUserId(userId)
					.getUserGroup();
			if (userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {
				opportunityT.setEnableEditAccess(true);
			} else {
				opportunityT
						.setEnableEditAccess(isEditAccessRequiredForOpportunity(
								opportunityT, userGroup, userId));
				checkAccessControl(opportunityT, previledgedOppIdList);
			}

		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		setUserFavourite(opportunityT);
		setSearchKeywordTs(opportunityT);
		removeCyclicForLinkedConnects(opportunityT);
		removeCyclicForCustomers(opportunityT);}

	private void setUserFavourite(OpportunityT opportunityT) {
		boolean flag = false;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		for(UserFavoritesT userFavorite : opportunityT.getUserFavoritesTs()) {
			if(userFavorite.getUserId().equalsIgnoreCase(userId)){
				flag = true;
			}
		}
		opportunityT.setUserFavourite(flag);
	}

	private void checkAccessControl(OpportunityT opportunityT,
			List<String> previledgedOppIdList) throws Exception {
		// previledgedOppIdList is null only while it is a single opportunity.
		if (previledgedOppIdList != null) {
			if (!previledgedOppIdList.contains(opportunityT.getOpportunityId())) {
				preventSensitiveInfo(opportunityT);
			}
		} else {
			List<String> opportunityIdList = new ArrayList<String>();
			opportunityIdList.add(opportunityT.getOpportunityId());
			previledgedOppIdList = getPriviledgedOpportunityId(opportunityIdList);
			if ((previledgedOppIdList == null || previledgedOppIdList.size() == 0)
					&& (!opportunityT.isEnableEditAccess())) {

				preventSensitiveInfo(opportunityT);
			}
		}

	}

	public void preventSensitiveInfo(List<OpportunityT> opportunityTs) {
		for (OpportunityT opportunityT : opportunityTs) {
			if (opportunityT != null) {
				preventSensitiveInfo(opportunityT);
			}
		}

	}

	public void preventSensitiveInfo(OpportunityT opportunityT) {
		if (opportunityT != null) {
			opportunityT.setDigitalDealValue(null);
			opportunityT.setOverallDealSize(null);
		}

	}

    private List<String> getPriviledgedOpportunityId(List<String> opportunityIds)
			throws Exception 
	{       
		    logger.debug("Inside setPreviledgeConstraints(opportunityIds) method");
		    HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
			queryBufferDTO = getOpportunityPriviledgeString(DestinationUtils.getCurrentUserDetails().getUserId(), opportunityIds);
		    logger.info("Query string: {}", queryBufferDTO.getQuery());
			Query opportunityQuery = entityManager.createNativeQuery(queryBufferDTO.getQuery());
			parameterMap=queryBufferDTO.getParameterMap();
			if(parameterMap!=null)
			{
				for(int i=1;i<=parameterMap.size();i++)
				{
					opportunityQuery.setParameter(i, parameterMap.get(i));
					
				}
			}
			return opportunityQuery.getResultList();
   }


	private List<String> getPriviledgedOpportunityId(String opportunityId)

			throws Exception {
		    logger.debug("Inside setPreviledgeConstraints(opportunityId) method");
		    HashMap<Integer, String> parameterMap = new HashMap<Integer,String>();
			List<String> opportunityIds = new ArrayList<String>();
			opportunityIds.add(opportunityId);
			queryBufferDTO  = getOpportunityPriviledgeString(DestinationUtils
					.getCurrentUserDetails().getUserId(), opportunityIds);
			logger.info("Query string: {}", queryBufferDTO.getQuery());
			Query opportunityQuery = entityManager.createNativeQuery(queryBufferDTO.getQuery(),
					OpportunityT.class);
			parameterMap=queryBufferDTO.getParameterMap();
			if(parameterMap!=null)
			{
			 for(int i=1;i<=parameterMap.size();i++)
			 {
				opportunityQuery.setParameter(i, parameterMap.get(i));
				
			 }
			}

			return opportunityQuery.getResultList();
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

	private void removeCyclicForCustomers(OpportunityT opportunityT) {
		logger.debug("Inside removeCyclicForLinkedConnects() method");

		if (opportunityT != null) {
			if (opportunityT.getCustomerMasterT() != null) {
				opportunityT.getCustomerMasterT().setOpportunityTs(null);
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
			List<String> currencies, int salesStageCode, String customerId)
			throws DestinationException {
		List<OpportunityT> opportunityTs = null;
		if (customerId.equals(""))
			opportunityTs = opportunityRepository
					.findBySalesStageCode(salesStageCode);
		else
			opportunityTs = opportunityRepository
					.findBySalesStageCodeAndCustomerId(salesStageCode,
							customerId);
		prepareOpportunity(opportunityTs);
		beaconConverterService.convertOpportunityCurrency(opportunityTs,
				currencies);
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
		autoCommentsHelper.setCollCommentsService(collaborationCommentsService);
		// Invoking Auto Comments Task Executor Thread
		autoCommentsTaskExecutor.execute(autoCommentsHelper);

	}

	// This method is used to invoke asynchronous thread for notifications
	private void processNotifications(String opportunitytId, Object oldObject) {
		logger.debug("Calling processNotifications() method");
		NotificationHelper notificationsHelper = new NotificationHelper();
		notificationsHelper.setEntityId(opportunitytId);
		notificationsHelper.setEntityType(EntityType.OPPORTUNITY.name());
		if (oldObject != null) {
			notificationsHelper.setOldObject(oldObject);
		}
		notificationsHelper
				.setNotificationsEventFieldsTRepository(notificationEventFieldsTRepository);
		notificationsHelper
				.setUserNotificationsTRepository(userNotificationsTRepository);
		notificationsHelper
				.setUserNotificationSettingsRepo(userNotificationSettingsRepo);
		notificationsHelper.setCrudRepository(opportunityRepository);
		notificationsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		notificationsHelper
				.setNotificationEventGroupMappingTRepository(notificationEventGroupMappingTRepository);
		notificationsHelper.setUserRepository(userRepository);
		notificationsHelper.setFollowService(followService);
		notificationsHelper
				.setUserNotificationSettingsConditionsRepository(userNotificationSettingsConditionRepository);
		notificationsHelper
				.setSearchKeywordsRepository(searchKeywordsRepository);
		notificationsHelper
				.setAutoCommentsEntityTRepository(autoCommentsEntityTRepository);
		// Invoking notifications Task Executor Thread
		notificationsTaskExecutor.execute(notificationsHelper);
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

		if (!StringUtils.isEmpty(supervisorUserId)) {

			// Get all users under a supervisor
			List<String> users = userRepository
					.getAllSubordinatesIdBySupervisorId(supervisorUserId);

			//Get FromDate and ToDate based on Current Financial Year
			String finYear = DateUtils.getCurrentFinancialYear();
			Date fromDate = DateUtils.getDateFromFinancialYear(finYear,true);
			Date toDate = DateUtils.getDateFromFinancialYear(finYear,false);
			
			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp toDateTs = new Timestamp(toDate.getTime()
					+ ONE_DAY_IN_MILLIS - 1);
			
			// Adding the user himself
			users.add(supervisorUserId);

			// Get all opportunities for the users under supervisor
			List<Object[]> opportunities = opportunityRepository
					.findDealValueOfOpportunitiesBySupervisorId(users,fromDateTs,toDateTs);

			if ((opportunities != null) && (opportunities.size() > 0)) {

				listOfopportunitiesDTO = new ArrayList<OpportunitiesBySupervisorIdDTO>();

				// Iterate the result and set the response object
				for (Object[] oppDTOArray : opportunities) {

					OpportunitiesBySupervisorIdDTO opp = new OpportunitiesBySupervisorIdDTO();

					if (oppDTOArray[0] != null) {
						opp.setDigitalDealValue(oppDTOArray[0].toString());
					} else {
						opp.setDigitalDealValue("0");
					}
					if (oppDTOArray[1] != null) {
						opp.setSalesStageCode(oppDTOArray[1].toString());
					}
					if (oppDTOArray[2] != null) {
						opp.setSalesCount(oppDTOArray[2].toString());
					} else {
						opp.setSalesCount("0");
					}
					if (oppDTOArray[3] != null) {
						opp.setSalesStageDescription(oppDTOArray[3].toString());
					}

					listOfopportunitiesDTO.add(opp);

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
			logger.error("NOT_FOUND: Supervisor Id is empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Supervisor Id is empty");
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
			String supervisorUserId, int page, int count,
			boolean isCurrentFinancialYear, String salesStageCode)
			throws Exception {

		logger.debug("Inside findOpportunityDetailsBySupervisorId() service");

		List<OpportunityDetailsDTO> listOfOpportunityDetails = null;
		List<OpportunityT> opportunities = null;
		List<OpportunityT> opportunitiesSubList = null;
		TeamOpportunityDetailsDTO teamOpportunityDetails = null;

		if (!StringUtils.isEmpty(supervisorUserId)) {

			// Get all users under a supervisor
			List<String> users = userRepository
					.getAllSubordinatesIdBySupervisorId(supervisorUserId);

			// Adding the user himself
			users.add(supervisorUserId);

			// Retrieve opportunities from users
			if (!isCurrentFinancialYear) {
				if (salesStageCode.equalsIgnoreCase("ALL")) {
					opportunities = opportunityRepository
							.findTeamOpportunityDetailsBySupervisorId(users);
				} else {
					opportunities = opportunityRepository
							.findTeamOpportunityDetailsBySupervisorId(users,
									Integer.parseInt(salesStageCode));
				}
			} else if (isCurrentFinancialYear) {
				String financialYear = DateUtils.getCurrentFinancialYear();

				Timestamp startTimestamp = new Timestamp(DateUtils
						.getDateFromFinancialYear(financialYear, true)
						.getTime());
				Timestamp endTimestamp = new Timestamp(DateUtils
						.getDateFromFinancialYear(financialYear, false)
						.getTime()
						+ Constants.ONE_DAY_IN_MILLIS - 1);

				if (salesStageCode.equalsIgnoreCase("ALL")) {
					opportunities = opportunityRepository
							.findTeamOpportunityDetailsBySupervisorIdInFinancialYear(
									users, startTimestamp, endTimestamp);
				} else {
					opportunities = opportunityRepository
							.findTeamOpportunityDetailsBySupervisorIdInFinancialYear(
									users, startTimestamp, endTimestamp,
									Integer.parseInt(salesStageCode));
				}
			}

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
						teamDetails.setOpportunityId(opportunity
								.getOpportunityId());
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
						teamDetails.setModifiedDate(DateUtils.ACTUAL_FORMAT
								.format(opportunity.getModifiedDatetime()));

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
			logger.error("NOT_FOUND: Supervisor Id is empty");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Supervisor Id is empty");
		}

		return teamOpportunityDetails;
	}

	public PaginatedResponse getByOpportunities(List<String> customerIdList,
			List<Integer> salesStageCode, String strategicInitiative,
			String newLogo, double minDigitalDealValue,
			double maxDigitalDealValue, String dealCurrency,
			String digitalFlag, List<String> displayIou, List<String> country,
			List<String> partnerId, List<String> competitorName,
			List<String> searchKeywords, List<String> bidRequestType,
			List<String> offering, List<String> displaySubSp,
			List<String> opportunityName, List<String> userId,
			List<String> toCurrency, int page, int count, String role,Boolean isCurrentFinancialYr)
			throws DestinationException 
	{
		PaginatedResponse opportunityResponse = new PaginatedResponse();
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
		if (minDigitalDealValue == 0 && maxDigitalDealValue == Double.MAX_VALUE)
			defaultDealRange = "YES";
		boolean isPrimary=false;
		boolean isSalesSupport=false;
		boolean isBidOffice=false;
		List<OpportunityT> opportunity = new ArrayList<OpportunityT>();
		if(OpportunityRole.contains(role)) {
			switch (OpportunityRole.valueOf(role)) {
			case PRIMARY_OWNER:
				logger.debug("Primary Owner Found");
				isPrimary=true;
				break;
			case SALES_SUPPORT:
				logger.debug("Sales Support Found");
				isSalesSupport = true;
				break;
			case BID_OFFICE:
				logger.debug("Bid Office Found");
				isBidOffice = true;
				break;
			case ALL:
				logger.debug("ALL Found");
				isPrimary=true;
				isSalesSupport = true;
				isBidOffice = true;
				break;
			}
			if (isCurrentFinancialYr) {
				
				Date fromDate=DateUtils.getDateFromFinancialYear(
						DateUtils.getCurrentFinancialYear(),true);
				
				Date toDate=DateUtils.getDateFromFinancialYear(
						DateUtils.getCurrentFinancialYear(),false);
			
			opportunity = opportunityRepository
					.findByOpportunitiesForCurrentFyIgnoreCaseLike(customerIdList,
							salesStageCode, strategicInitiative, newLogo,
							defaultDealRange, minDigitalDealValue,
							maxDigitalDealValue, dealCurrency, digitalFlag,
							displayIou, country, partnerId, competitorName,
							searchKeywordString, bidRequestType, offering,
							displaySubSp, opportunityNameString, userId, isPrimary, isSalesSupport, isBidOffice,fromDate,toDate);
			}
			else
			{
				opportunity = opportunityRepository
						.findByOpportunitiesIgnoreCaseLike(customerIdList,
								salesStageCode, strategicInitiative, newLogo,
								defaultDealRange, minDigitalDealValue,
								maxDigitalDealValue, dealCurrency, digitalFlag,
								displayIou, country, partnerId, competitorName,
								searchKeywordString, bidRequestType, offering,
								displaySubSp, opportunityNameString, userId, isPrimary, isSalesSupport, isBidOffice);
			}

		} else {
			logger.error("BAD_REQUEST: Invalid Opportunity Role: {}",
					role);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role: " + role);
		}

		if (opportunity.isEmpty()) {
			logger.error("NOT_FOUND: No Opportunities found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunities Found.");
		}
		opportunityResponse.setTotalCount(opportunity.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count, opportunity.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					opportunity.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					opportunity.size()) + 1;
			opportunity = opportunity.subList(fromIndex, toIndex);
			opportunityResponse.setOpportunityTs(opportunity);
			logger.debug("OpportunityT  after pagination size is "
					+ opportunity.size());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunity available for the specified page");
		}
		prepareOpportunity(opportunity);
		beaconConverterService.convertOpportunityCurrency(opportunity,
				toCurrency);

		return opportunityResponse;
	}

	public String searchForContaining(List<String> containingWords) {
		String actualWords = "";
		if (containingWords != null)
			for (String containgWord : containingWords) {
				containgWord = containgWord.toUpperCase();
				actualWords += containgWord + "|";
			}
		if (actualWords.length() > 2)
			actualWords = actualWords.substring(0, actualWords.length() - 1);
		return actualWords;
	}

	public List<String> fillIfEmpty(List<String> stringList) {
		if (stringList == null)
			stringList = new ArrayList<String>();
		if (stringList.isEmpty())
			stringList.add("");
		return stringList;

	}

	public PaginatedResponse findAll(String sortBy, String order,
			Boolean isCurrentFinancialYear, int page, int count)
			throws DestinationException {

		PaginatedResponse opportunityResponse = new PaginatedResponse();

		List<OpportunityT> opportunityTs = null;

		if (isCurrentFinancialYear) {

			try {
				// Create the query and execute
				String queryString = "select OPP from OpportunityT OPP where (OPP.salesStageCode < 9) or ((OPP.dealClosureDate between ?1 and ?2) and (OPP.salesStageCode >= 9)) order by "
						+ sortBy + " " + order;
				Query query = entityManager
						.createQuery(queryString)
						.setParameter(
								1,
								DateUtils.getDateFromFinancialYear(
										DateUtils.getCurrentFinancialYear(),
										true))
						.setParameter(
								2,
								DateUtils.getDateFromFinancialYear(
										DateUtils.getCurrentFinancialYear(),
										false));
				opportunityTs = (List<OpportunityT>) query.getResultList();
				opportunityResponse.setTotalCount(opportunityTs.size());
			} catch (Exception e) {
				// Throw exceptions where Order by parameter is invalid
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
			// Code for pagination
			if (PaginationUtils.isValidPagination(page, count,
					opportunityTs.size())) {
				int fromIndex = PaginationUtils.getStartIndex(page, count,
						opportunityTs.size());
				int toIndex = PaginationUtils.getEndIndex(page, count,
						opportunityTs.size()) + 1;
				opportunityTs = opportunityTs.subList(fromIndex, toIndex);
				opportunityResponse.setOpportunityTs(opportunityTs);
				logger.debug("OpportunityT  after pagination size is "
						+ opportunityTs.size());
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Opportunity available for the specified page");
			}
		} else {
			try {
				// Page the opportunities for all financial year
				Page<OpportunityT> opportunityPagable = opportunityRepository
						.findAll(constructPageSpecification(page, count,
								sortBy, order));
				opportunityResponse.setTotalCount(opportunityPagable
						.getTotalElements());
				opportunityTs = new ArrayList<OpportunityT>();
				for (OpportunityT opportunityT : opportunityPagable) {
					opportunityTs.add(opportunityT);
				}
				opportunityResponse.setOpportunityTs(opportunityTs);
			} catch (Exception e) {
				// Throw exceptions where Order by parameter is invalid
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}

		if (opportunityTs == null || opportunityTs.size() == 0)
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunities found");
		 prepareOpportunity(opportunityTs);
		return opportunityResponse;
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
    
	

	private QueryBufferDTO getOpportunityPriviledgeString(String userId,
			List<String> opportunityIds) throws Exception {
		logger.debug("Inside getOpportunityPriviledgeString() method");
		StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_QUERY_PREFIX);
		
		// Get user access privilege groups

		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(OPPORTUNITY_GEO_INCLUDE_COND_PREFIX,
						OPPORTUNITY_SUBSP_INCLUDE_COND_PREFIX,
						OPPORTUNITY_IOU_INCLUDE_COND_PREFIX,
						OPPORTUNITY_CUSTOMER_INCLUDE_COND_PREFIX);

		// Get WHERE clause string
		queryBufferDTO= userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereCondition(userId, queryPrefixMap);

		if (opportunityIds.size() > 0) {
			String oppIdList = "(";
			{
				for (String opportunityId : opportunityIds)
					oppIdList += "'" + opportunityId + "',";
			}
			oppIdList = oppIdList.substring(0, oppIdList.length() - 1);
			oppIdList += ")";


				queryBuffer.append(" OPP.opportunity_id in " + oppIdList);
			}

               if(queryBufferDTO!=null)
               {
			    if (queryBufferDTO.getQuery() != null && !queryBufferDTO.getQuery().isEmpty()) 
			    {
				 queryBuffer.append(Constants.AND_CLAUSE + queryBufferDTO.getQuery());
				}
			    queryBufferDTO.setQuery(queryBuffer.toString());
               }
               else
			   {
				queryBufferDTO=new QueryBufferDTO();
				queryBufferDTO.setQuery(queryBuffer.toString());
				queryBufferDTO.setParameterMap(null);
			   }
			   return queryBufferDTO;
	}

	public ArrayList<OpportunityNameKeywordSearch> findOpportunityNameOrKeywords(
			String name, String keyword) {
		ArrayList<OpportunityNameKeywordSearch> opportunityNameKeywordSearchList = new ArrayList<OpportunityNameKeywordSearch>();
		if (name.length() > 0)
			name = "%" + name + "%";
		if (keyword.length() > 0)
			keyword = "%" + keyword + "%";
		List<Object[]> results = opportunityRepository
				.findOpportunityNameOrKeywords(name.toUpperCase(),
						keyword.toUpperCase());

		for (Object[] result : results) {
			OpportunityNameKeywordSearch opportunityNameKeywordSearch = new OpportunityNameKeywordSearch();
			opportunityNameKeywordSearch.setResult(result[0].toString());
			OpportunityT opportunityT = opportunityRepository.findOne(result[1]
					.toString());
			setSearchKeywordTs(opportunityT);
			opportunityNameKeywordSearch.setOpportunityT(opportunityT);
			opportunityNameKeywordSearch.setIsName(result[2].toString());
			;
			opportunityNameKeywordSearchList.add(opportunityNameKeywordSearch);
		}
		return opportunityNameKeywordSearchList;
	}

	/**
	 * This Method used to get list of opportunities for the specified opportunity ids
	 * @param opportunityIds
	 * @return
	 */
	public List<OpportunityT> findByOpportunityIds(List<String> opportunityIds, List<String> toCurrency) {
		logger.debug("Inside findByOpportunityIds() method");
		List<OpportunityT> opportunityList = null;
		if ((opportunityIds != null) && (!opportunityIds.isEmpty())) {
		opportunityList = opportunityRepository.findByOpportunityIds(opportunityIds);
		}
		if(opportunityList==null || opportunityList.isEmpty() ){
			logger.error("Opportunities not found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Opportunities not found");
		}
		prepareOpportunity(opportunityList);
		
		beaconConverterService.convertOpportunityCurrency(opportunityList, toCurrency);

		return opportunityList;
	}
    
	/**
	 * this method saves the opportunity list.
	 * @param insertList
	 */
	public void save(List<OpportunityT> insertList) {
		logger.debug("Inside save method");

		Map<Integer, List<OpportunityOfferingLinkT>> mapOppOffering = new HashMap<Integer, List<OpportunityOfferingLinkT>>(
				insertList.size());
		Map<Integer, List<OpportunityPartnerLinkT>> mapOpportunityPartnerLink = new HashMap<Integer, List<OpportunityPartnerLinkT>>(
				insertList.size());
		Map<Integer, List<OpportunitySalesSupportLinkT>> mapSalesSupport = new HashMap<Integer, List<OpportunitySalesSupportLinkT>>(
				insertList.size());
		Map<Integer, List<OpportunitySubSpLinkT>> mapSubSp = new HashMap<Integer, List<OpportunitySubSpLinkT>>(
				insertList.size());
		Map<Integer, List<OpportunityCustomerContactLinkT>> mapCustomerContact = new HashMap<Integer, List<OpportunityCustomerContactLinkT>>(
				insertList.size());
		Map<Integer, List<OpportunityTcsAccountContactLinkT>> mapTcsContact = new HashMap<Integer, List<OpportunityTcsAccountContactLinkT>>(
				insertList.size());
		Map<Integer, List<OpportunityCompetitorLinkT>> mapOppCompetitor = new HashMap<Integer, List<OpportunityCompetitorLinkT>>(
				insertList.size());
		Map<Integer, List<NotesT>> mapOppNotes = new HashMap<Integer, List<NotesT>>(
				insertList.size());

		int i = 0;
		for (OpportunityT opportunityT : insertList) {
			mapOppOffering.put(i, opportunityT.getOpportunityOfferingLinkTs());
			mapOpportunityPartnerLink.put(i, opportunityT.getOpportunityPartnerLinkTs());
			mapSalesSupport.put(i, opportunityT.getOpportunitySalesSupportLinkTs());
			mapSubSp.put(i, opportunityT.getOpportunitySubSpLinkTs());
			mapCustomerContact.put(i,
					opportunityT.getOpportunityCustomerContactLinkTs());
			mapTcsContact.put(i, opportunityT.getOpportunityTcsAccountContactLinkTs());
			mapOppCompetitor.put(i, opportunityT.getOpportunityCompetitorLinkTs());
			mapOppNotes.put(i, opportunityT.getNotesTs());
			setNullForReferencedObjects(opportunityT);

			i++;
		}

		Iterable<OpportunityT> savedList = opportunityRepository.save(insertList);
		Iterator<OpportunityT> saveIterator = savedList.iterator();
		System.out.println("Opportunities"+insertList);
		i = 0;
		while (saveIterator.hasNext()) {
			OpportunityT opportunity = saveIterator.next();
			List<OpportunityOfferingLinkT> offeringList = mapOppOffering.get(i);
			if (CollectionUtils.isNotEmpty(offeringList)) {
				populateOpportunityOfferingLinks(opportunity.getOpportunityId(),
						offeringList);
			}
			List<OpportunityPartnerLinkT> oppourtunityPartnerList = mapOpportunityPartnerLink
					.get(i);
			if (CollectionUtils.isNotEmpty(oppourtunityPartnerList)) {
				populateOpportunityPartnerLink(opportunity.getOpportunityId(), oppourtunityPartnerList);
			}
			List<OpportunitySalesSupportLinkT> salesSupportList = mapSalesSupport
					.get(i);
			if (CollectionUtils.isNotEmpty(salesSupportList)) {
				populateOppSalesSupportLink(opportunity.getOpportunityId(),
						salesSupportList);
			}
			List<OpportunitySubSpLinkT> subSpList = mapSubSp.get(i);
			if (CollectionUtils.isNotEmpty(subSpList)) {
				populateOpportunitySubSpLink(opportunity.getOpportunityId(), subSpList);
			}
			List<OpportunityCustomerContactLinkT> custContactList = mapCustomerContact
					.get(i);
			if (CollectionUtils.isNotEmpty(custContactList)) {
				populateOppCustomerContactLinks(opportunity.getOpportunityId(),
						custContactList);
			}
			List<OpportunityTcsAccountContactLinkT> tcsContactList = mapTcsContact
					.get(i);
			if (CollectionUtils.isNotEmpty(tcsContactList)) {
				populateOpportunityTcsAccountContactLink(opportunity.getOpportunityId(),
						tcsContactList);
			}
			List<OpportunityCompetitorLinkT> competitorList = mapOppCompetitor
					.get(i);
			if (CollectionUtils.isNotEmpty(competitorList)) {
				populateOpportunityCompetitorLink(opportunity.getOpportunityId(),
						competitorList);
			}
			List<NotesT> notes = mapOppNotes
					.get(i);
			if (CollectionUtils.isNotEmpty(notes)) {
				populateOpportunityNotes(opportunity.getOpportunityId(),
						notes);
			}

			i++;
		}

		List<OpportunityOfferingLinkT> oppOfferingList = new ArrayList<OpportunityOfferingLinkT>();
		for (List<OpportunityOfferingLinkT> list : mapOppOffering.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppOfferingList.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppOfferingList)) {
			opportunityOfferingLinkTRepository.save(oppOfferingList);
		}

		List<OpportunityPartnerLinkT> oppPartnerList = new ArrayList<OpportunityPartnerLinkT>();
		for (List<OpportunityPartnerLinkT> list : mapOpportunityPartnerLink.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppPartnerList.addAll(list);
			}

		}
		if (CollectionUtils.isNotEmpty(oppPartnerList)) {
			opportunityPartnerLinkTRepository.save(oppPartnerList);
		}

		List<OpportunitySalesSupportLinkT> oppSalesSupport = new ArrayList<OpportunitySalesSupportLinkT>();
		for (List<OpportunitySalesSupportLinkT> list : mapSalesSupport.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppSalesSupport.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppSalesSupport)) {
			opportunitySalesSupportLinkTRepository.save(oppSalesSupport);
		}

		List<OpportunitySubSpLinkT> oppSubSps = new ArrayList<OpportunitySubSpLinkT>();
		for (List<OpportunitySubSpLinkT> list : mapSubSp.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppSubSps.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppSubSps)) {
			opportunitySubSpLinkTRepository.save(oppSubSps);
		}

		List<OpportunityCustomerContactLinkT> oppCustContact = new ArrayList<OpportunityCustomerContactLinkT>();
		for (List<OpportunityCustomerContactLinkT> list : mapCustomerContact.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppCustContact.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppCustContact)) {
			opportunityCustomerContactLinkTRepository.save(oppCustContact);
		}

		List<OpportunityTcsAccountContactLinkT> oppTcsAccContact = new ArrayList<OpportunityTcsAccountContactLinkT>();
		for (List<OpportunityTcsAccountContactLinkT> list : mapTcsContact.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppTcsAccContact.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppTcsAccContact)) {
			opportunityTcsAccountContactLinkTRepository.save(oppTcsAccContact);
		}
		
		List<OpportunityCompetitorLinkT> oppCompetitor = new ArrayList<OpportunityCompetitorLinkT>();
		for (List<OpportunityCompetitorLinkT> list : mapOppCompetitor.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppCompetitor.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppCompetitor)) {
			opportunityCompetitorLinkTRepository.save(oppCompetitor);
		}
		
		List<NotesT> oppNotes = new ArrayList<NotesT>();
		for (List<NotesT> list : mapOppNotes.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppNotes.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppNotes)) {
			notesTRepository.save(oppNotes);
		}


	}

	private void populateOpportunityNotes(String opportunityId,
			List<NotesT> notes) {
		for(NotesT notesT : notes)
		{
			notesT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOpportunityCompetitorLink(String opportunityId,
			List<OpportunityCompetitorLinkT> competitorList) {
		for(OpportunityCompetitorLinkT opportunityCompetitorLinkT : competitorList)
		{
			opportunityCompetitorLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOpportunityTcsAccountContactLink(String opportunityId,
			List<OpportunityTcsAccountContactLinkT> tcsContactList) {
		for(OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : tcsContactList)
		{
			opportunityTcsAccountContactLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOppCustomerContactLinks(String opportunityId,
			List<OpportunityCustomerContactLinkT> custContactList) {
		for(OpportunityCustomerContactLinkT opportunityCustomerContactLinkT : custContactList)
		{
			opportunityCustomerContactLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOpportunitySubSpLink(String opportunityId,
			List<OpportunitySubSpLinkT> subSpList) {
		for(OpportunitySubSpLinkT opportunitySubSpLinkT : subSpList)
		{
			opportunitySubSpLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOppSalesSupportLink(String opportunityId,
			List<OpportunitySalesSupportLinkT> salesSupportList) {
		for(OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : salesSupportList)
		{
			opportunitySalesSupportLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOpportunityPartnerLink(String opportunityId,
			List<OpportunityPartnerLinkT> oppourtunityPartnerList) {
		for(OpportunityPartnerLinkT opportunityPartnerLinkT : oppourtunityPartnerList)
		{
			opportunityPartnerLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void populateOpportunityOfferingLinks(String opportunityId,
			List<OpportunityOfferingLinkT> offeringList) {
		for(OpportunityOfferingLinkT opportunityOfferingLinkT : offeringList)
		{
			opportunityOfferingLinkT.setOpportunityId(opportunityId);
		}
		
	}

	private void setNullForReferencedObjects(OpportunityT opportunityT) {
		logger.debug("Inside setNullForReferencedObjects() method");
		opportunityT.setBidDetailsTs(null);
		opportunityT.setNotesTs(null);
		opportunityT.setOpportunityCompetitorLinkTs(null);
		opportunityT.setOpportunityCustomerContactLinkTs(null);
		opportunityT.setOpportunityDealValues(null);
		opportunityT.setOpportunityOfferingLinkTs(null);
		opportunityT.setOpportunityPartnerLinkTs(null);
		opportunityT.setOpportunitySalesSupportLinkTs(null);
		opportunityT.setOpportunitySubSpLinkTs(null);
		opportunityT.setOpportunityTcsAccountContactLinkTs(null);
		opportunityT.setOpportunityWinLossFactorsTs(null);
	}
	
	/**
	 * This method is used to check whether the owners of opportunity or connect are BDM or BDM Supervisor
	 * @param owners
	 * @return
	 */
	public boolean isOwnersAreBDMorBDMSupervisor(Set<String> owners) {
		boolean isBDMOrBDMSupervisor = false;
		List<String> userGroups = userRepository.findUserGroupByUserIds(owners,true);
		if(CollectionUtils.isNotEmpty(userGroups)){
			for (String userGroup : userGroups) {
				if (userGroup.equals(UserGroup.BDM.getValue())
						|| userGroup.equals(UserGroup.BDM_SUPERVISOR.getValue())
						|| userGroup.equals(UserGroup.GEO_HEADS.getValue())) {
					isBDMOrBDMSupervisor = true;
					break;
				}
			}
		}
		
		return isBDMOrBDMSupervisor;
	}
	
	/**
	 * This method is used to check whether any of the subordinate is being one
	 * of the owners of connect or opportunity
	 * 
	 * @param userId
	 * @param opportunityId
	 * @param connectId
	 * @return
	 */
	public boolean isSubordinateAsOwner(String userId, String opportunityId,
			String connectId) {
		boolean isSubordinateAsOwner = false;
		List<String> owners = new ArrayList<String>();
		List<String> subordinates = userRepository
				.getAllSubordinatesIdBySupervisorId(userId);
		if (CollectionUtils.isNotEmpty(subordinates)) {
			if (!StringUtils.isEmpty(opportunityId)) {
				owners = opportunityRepository.getAllOwners(opportunityId);
			}
			if (!StringUtils.isEmpty(connectId)) {
				owners = connectRepository.findOwnersOfConnect(connectId);
			}
			if (owners != null) {
				for (String owner : owners) {
					if (subordinates.contains(owner)) {
						isSubordinateAsOwner = true;
						break;
					}
				}
			}
		}
		return isSubordinateAsOwner;
	}

	
	/**
	 * This method is used to check whether Geo heads PMO and Iou Heads have the
	 * edit access for an opportunity
	 * 
	 * @param userGroup
	 * @param userId
	 * @param customerId
	 * @return
	 */
	public boolean checkEditAccessForGeoAndIou(String userGroup, String userId,
			String customerId) {
		logger.info("Inside checkEditAccessForGeoAndIou method");
		boolean isEditAccessRequired = false;
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
		case GEO_HEADS:
		case PMO:
			String geography = customerRepository
					.findGeographyByCustomerId(customerId);

			List<String> geographyList = userAccessPrivilegesRepository
					.getPrivilegeValueForUser(userId,
							PrivilegeType.GEOGRAPHY.getValue());
			if (CollectionUtils.isNotEmpty(geographyList)) {
				if (geographyList.contains(geography)) {
					isEditAccessRequired = true;
				}
			}
			break;
		case IOU_HEADS:
			String iou = customerRepository.findIouByCustomerId(customerId);
			List<String> iouList = userAccessPrivilegesRepository
					.getIouPrivilegeValue(userId, PrivilegeType.IOU.getValue());
			if (CollectionUtils.isNotEmpty(iouList)) {
				if (iouList.contains(iou)) {
					isEditAccessRequired = true;

				}
			}
			break;
		default:
			break;
		}
        
				
		return isEditAccessRequired;

	}
	
	/**
	* This method is used to update the opportunity details and also
	* send email notification if opportunity won or lost
	* @param opportunity
	* @throws Exception
	*/
	public void updateOpportunityT(OpportunityT opportunity) throws Exception {
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
		OpportunityT opportunityBeforeEdit = opportunityRepository
				.findOne(opportunityId);
		int oldSalesStageCode = opportunityBeforeEdit.getSalesStageCode();
		updateOpportunity(opportunity, opportunityBeforeEdit);
		// If won or lost, sending email notification to group of users using
		// asynchronous job
		if ((oldSalesStageCode != 9 && opportunity.getSalesStageCode() == 9)
				|| (oldSalesStageCode != 10 && opportunity.getSalesStageCode() == 10)) {
			logger.info("Opportunity : " + opportunityId
					+ " is either won or lost");
			jobLauncherController.asyncJobLaunch(
					JobName.opportunityWonLostEmailNotification,
					EntityType.OPPORTUNITY.toString(),
					opportunity.getOpportunityId());
		}
	}
	
	/**
	 * This method is used to check wheteher the logged in user has edit access
	 * for an opportunity
	 * 
	 * @param opportunity
	 * @param userGroup
	 * @param userId
	 * @return
	 */
	private boolean isEditAccessRequiredForOpportunity(
			OpportunityT opportunity, String userGroup, String userId) {
		logger.info("Inside isEditAccessRequiredForOpportunity method");
		boolean isEditAccessRequired = false;
		if (isUserOwner(userId, opportunity)) {
			isEditAccessRequired = true;

		} 
		else {
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM :
				isEditAccessRequired = false;
				break;
			case BDM_SUPERVISOR:
				isEditAccessRequired = isSubordinateAsOwner(userId, opportunity.getOpportunityId(),
						null);
				break;
			case GEO_HEADS:
			case PMO:
			case IOU_HEADS:	
				isEditAccessRequired = checkEditAccessForGeoAndIou(userGroup,
						userId, opportunity.getCustomerId());
				break;
			default:
				break;	 
				
				
			}
		}
		
		logger.info("Is Edit Access Required for connect: " +isEditAccessRequired);
		return isEditAccessRequired;
	}
}

