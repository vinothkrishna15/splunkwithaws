package com.tcs.destination.service;

import static com.tcs.destination.utils.ErrorConstants.ERR_INAC_01;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.AsyncJobRequest;
import com.tcs.destination.bean.AuditOpportunityDeliveryCentreT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.BidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DeliveryCentreT;
import com.tcs.destination.bean.DeliveryIntimatedT;
import com.tcs.destination.bean.DeliveryMasterT;
import com.tcs.destination.bean.DeliveryOwnershipT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OpportunitiesBySupervisorIdDTO;
import com.tcs.destination.bean.OpportunityCompetitorLinkT;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityDeliveryCentreMappingT;
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
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.TeamOpportunityDetailsDTO;
import com.tcs.destination.bean.UserFavoritesT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.WorkflowBfmT;
import com.tcs.destination.bean.WorkflowRequestT;
import com.tcs.destination.bean.dto.OpportunityDTO;
import com.tcs.destination.data.repository.AuditOpportunityDeliveryCenterRepository;
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
import com.tcs.destination.data.repository.DeliveryCentreRepository;
import com.tcs.destination.data.repository.DeliveryIntimatedRepository;
import com.tcs.destination.data.repository.DeliveryMasterRepository;
import com.tcs.destination.data.repository.DeliveryOwnershipRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.OpportunityCompetitorLinkTRepository;
import com.tcs.destination.data.repository.OpportunityCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityDao;
import com.tcs.destination.data.repository.OpportunityDeliveryCentreMappingTRepository;
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
import com.tcs.destination.data.repository.WorkflowBfmTRepository;
import com.tcs.destination.data.repository.WorkflowRequestTRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.EntityTypeId;
import com.tcs.destination.enums.JobName;
import com.tcs.destination.enums.OpportunityRole;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.SalesStageCode;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.enums.Switch;
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
import com.tcs.destination.utils.QueryConstants;

@Service
public class OpportunityService {

	private static final int ONE_DAY_IN_MILLIS = 86400000;

	private String bidId = null;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	@Autowired
	DozerBeanMapper beanMapper;

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
	OpportunityDeliveryCentreMappingTRepository opportunityDeliveryCentreMappingTRepository;

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

	@Autowired
	DeliveryOwnershipRepository deliveryOwnershipRepository;

	@Autowired
	DeliveryCentreRepository deliveryCentreRepository;

	@Autowired @Lazy
	OpportunityDao opportunityDao;

	@Autowired
	OpportunityDownloadService opportunityDownloadService;

	@Autowired
	WorkflowBfmTRepository workflowBfmTRepository;

	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	DeliveryMasterRepository deliveryMasterRepository;
	
	@Autowired
	AuditOpportunityDeliveryCenterRepository auditOpportunityDeliveryCenterRepository;

	@Autowired
	private DeliveryMasterService deliveryMasterService;
	
	@Autowired
	DeliveryIntimatedRepository deliveryIntimatedRepository;
	
	private static final String GEO_COND_PREFIX = "GMT.geography in (";
	private static final String SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String IOU_COND_PREFIX = "ICMT.display_iou in (";
	private static final String CUSTOMER_COND_PREFIX = "CMT.customer_name in (";

	/**
	 * Fetch opportunities by opportunity name
	 * 
	 * @param nameWith
	 * @param customerId
	 * @param currencies
	 * @param isAjax
	 * @param user
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse getOpportunitiesByOpportunityName(String nameWith,
			String customerId, List<String> currencies, boolean isAjax,
			UserT user, int page, int count) throws Exception {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			paginatedResponse = findByOpportunityNameAndDelivaryFlag(nameWith,
					customerId, currencies, isAjax, user, page, count);
		} else {
			paginatedResponse = findByOpportunityName(nameWith, customerId,
					currencies, isAjax, user, page, count);
		}
		return paginatedResponse;
	}

	/**
	 * To fetch opportunities by name
	 * 
	 * @param nameWith
	 * @param customerId
	 * @param toCurrency
	 * @param isAjax
	 * @param userId
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findByOpportunityName(String nameWith,
			String customerId, List<String> toCurrency, boolean isAjax,
			UserT user, int page, int count) throws Exception {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count, new Sort(
				Sort.Direction.DESC, "modifiedDatetime"));
		logger.debug("Inside findByOpportunityName() service");
		if (!user.getUserId().equals(
				DestinationUtils.getCurrentUserDetails().getUserId()))
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User Id and Login User detail does not match");

		Page<OpportunityT> opportunities = null;
		if (customerId.isEmpty()) {
			opportunities = opportunityRepository
					.findByOpportunityNameIgnoreCaseLikeOrderByModifiedDatetimeDesc(
							"%" + nameWith + "%", pageable);
		} else {
			opportunities = opportunityRepository
					.findByOpportunityNameIgnoreCaseLikeAndCustomerIdOrderByModifiedDatetimeDesc(
							"%" + nameWith + "%", customerId, pageable);
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

	/**
	 * Fetch delivery opportunities by name
	 * 
	 * @param nameWith
	 * @param customerId
	 * @param toCurrency
	 * @param isAjax
	 * @param userId
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findByOpportunityNameAndDelivaryFlag(
			String nameWith, String customerId, List<String> toCurrency,
			boolean isAjax, UserT user, int page, int count) throws Exception {
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Set<OpportunityT> opportunitiesSet = new HashSet<OpportunityT>();
		List<OpportunityT> opportunitiesList = new ArrayList<OpportunityT>();
		logger.debug("Inside findByOpportunityNameAndDelivaryFlag() service");

		List<String> userIds = userRepository
				.getAllSubordinatesIdBySupervisorId(user.getUserId());
		userIds.add(user.getUserId());

		if (customerId.isEmpty()) {
			opportunitiesSet
					.addAll(opportunityRepository
							.findByOpportunityNameIgnoreCaseLikeAndDeliveryTeamFlagOrderByModifiedDatetimeDesc(
									"%" + nameWith + "%", true));
			opportunitiesSet.addAll(opportunityRepository
					.findByOpportunityNameAndCentreHeadOrClusterHead("%"
							+ nameWith + "%", userIds));
			opportunitiesList.addAll(opportunitiesSet);
		} else {
			opportunitiesSet
					.addAll(opportunityRepository
							.findByOpportunityNameIgnoreCaseLikeAndCustomerIdAndDeliveryTeamFlagOrderByModifiedDatetimeDesc(
									"%" + nameWith + "%", customerId, true));
			opportunitiesSet
					.addAll(opportunityRepository
							.findByOpportunityNameAndCustomerIdAndCentreHeadOrClusterHead(
									"%" + nameWith + "%", customerId, userIds));
			opportunitiesList.addAll(opportunitiesSet);
		}
		if (!isAjax) {
			prepareOpportunity(opportunitiesList);
			beaconConverterService.convertOpportunityCurrency(
					opportunitiesList, toCurrency);
		} else {
			// Don't perform the check and hide sensitive information without
			// checking the privilege as it is might reduce te performance.
			preventSensitiveInfo(opportunitiesList);
		}
		paginatedResponse.setTotalCount(opportunitiesList.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count,
				opportunitiesList.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					opportunitiesList.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					opportunitiesList.size()) + 1;
			opportunitiesList = opportunitiesList.subList(fromIndex, toIndex);
			paginatedResponse.setOpportunityTs(opportunitiesList);
			logger.debug("Opportunities after pagination size is "
					+ opportunitiesList.size());
		} else {
			logger.error(
					"NOT_FOUND: Opportunities not found with the given name: {}",
					nameWith);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunities not found with the given name: " + nameWith);
		}
		return paginatedResponse;
	}

	/**
	 * To fetch all the delivery centres except open
	 * 
	 * @return
	 */
//	@Cacheable("deliveryCentre")
	public List<DeliveryCentreT> fetchDeliveryCentre() {
		logger.debug("Inside fetchDeliveryCentre() service");
		List<DeliveryCentreT> deliveryCentre = new ArrayList<DeliveryCentreT>();
		// Retrieving all delivery centres except open ->delivery centre id -1
		deliveryCentre = deliveryCentreRepository
				.findByDeliveryCentreIdGreaterThanEqualOrderByDeliveryCentreIdAsc(Constants.CONSTANT_ZERO);
		return deliveryCentre;
	}

	/**
	 * To fetch all the delivery ownership
	 * 
	 * @return
	 */
//	@Cacheable("deliveryOwnershipDetails")
	public List<DeliveryOwnershipT> fetchDeliveryOwnershipDetails() {
		logger.debug("Inside fetchDeliveryOwnershipDetails() service");
		List<DeliveryOwnershipT> deliveryOwnership = new ArrayList<DeliveryOwnershipT>();
		deliveryOwnership = (List<DeliveryOwnershipT>) deliveryOwnershipRepository
				.findAll();
		return deliveryOwnership;
	}

	/**
	 * This method is used to fetch the recent opportunities
	 * 
	 * @param fromDate
	 * @param customerId
	 * @param toCurrency
	 * @param smartSearchType
	 * @param opportunityNameWith
	 * @param count
	 * @param page
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findOpportunitiesByCustomerIdAndSearchTerm(
			Date fromDate, String customerId, List<String> toCurrency,
			SmartSearchType smartSearchType, String term, int page, int count)
			throws Exception {
		logger.debug("Inside findOpportunitiesByCustomerIdAndSearchTerm() service");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		List<OpportunityT> opportunitiesList = new ArrayList<OpportunityT>();

		Set<OpportunityT> opportunitiesSet = new HashSet<OpportunityT>();
		UserT user = DestinationUtils.getCurrentUserDetails();
		if (smartSearchType != null) {

			switch (smartSearchType) {
			case ALL:
				opportunitiesSet
						.addAll(getOpportunitiesByOpportunityIdAndCustomerIdAndReceivedDate(
								term, customerId,
								new Timestamp(fromDate.getTime()), user));
				opportunitiesSet
						.addAll(getOpportunitiesByNameAndCustomerIdAndReceivedDate(
								term, customerId,
								new Timestamp(fromDate.getTime()), user));
				opportunitiesSet
						.addAll(getOpportunitiesByOwnerAndCustomerIdAndReceivedDate(
								term, customerId,
								new Timestamp(fromDate.getTime()), user));
				opportunitiesSet
						.addAll(getOpportunitiesBySubSpsAndCustomerIdAndReceivedDate(
								term, customerId,
								new Timestamp(fromDate.getTime()), user));
				opportunitiesList.addAll(opportunitiesSet);
				break;
			case ID:
				opportunitiesList = getOpportunitiesByOpportunityIdAndCustomerIdAndReceivedDate(
						term, customerId, new Timestamp(fromDate.getTime()), user);
				break;
			case NAME:
				opportunitiesList = getOpportunitiesByNameAndCustomerIdAndReceivedDate(
						term, customerId, new Timestamp(fromDate.getTime()), user);
				break;
			case PRIMARY_OWNER:
				opportunitiesList = getOpportunitiesByOwnerAndCustomerIdAndReceivedDate(
						term, customerId, new Timestamp(fromDate.getTime()), user);
				break;
			case SUBSP:
				opportunitiesList = getOpportunitiesBySubSpsAndCustomerIdAndReceivedDate(
						term, customerId, new Timestamp(fromDate.getTime()), user);
				break;
			default:
				break;
			}
		} else {
			opportunitiesList = getAllOpportunitiesByCustomerIdAndStartDateOfConnectBetween(
					customerId, new Timestamp(fromDate.getTime()));
		}

		prepareOpportunity(opportunitiesList);
		beaconConverterService.convertOpportunityCurrency(opportunitiesList,
				toCurrency);
		paginatedResponse.setTotalCount(opportunitiesList.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count,
				opportunitiesList.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					opportunitiesList.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					opportunitiesList.size()) + 1;
			opportunitiesList = opportunitiesList.subList(fromIndex, toIndex);
			paginatedResponse.setOpportunityTs(opportunitiesList);
			logger.debug("Opportunities after pagination size is "
					+ opportunitiesList.size());
		} else {
			logger.info(" opportunities not found for CustomerId: {} ");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					" opportunities not found for CustomerId: {} ");
		}
		return paginatedResponse;
	}

	/**
	 * This method is used to find the opportunities for the customerId,
	 * opportunityId like search and after request received date
	 * 
	 * @param term
	 * @param customerId
	 * @param user 
	 * @param timestamp
	 * @return
	 */
	private List<OpportunityT> getOpportunitiesByOpportunityIdAndCustomerIdAndReceivedDate(
			String term, String customerId, Timestamp fromTimestamp, UserT user) {
		List<OpportunityT> opportunitiesList = null;
		
			opportunitiesList = opportunityRepository
					.findByCustomerIdAndOpportunityRequestReceiveDateAfterAndOpportunityIdLike(
							customerId, fromTimestamp, "%" + term.toUpperCase()
									+ "%");
		return opportunitiesList;
	}

	/**
	 * This method is used to find the opportunities for the customerId,
	 * opportunityName like search and after request received date
	 * 
	 * @param term
	 * @param customerId
	 * @param user 
	 * @param timestamp
	 * @return
	 */
	private List<OpportunityT> getOpportunitiesByNameAndCustomerIdAndReceivedDate(
			String term, String customerId, Timestamp fromTimestamp, UserT user) {
		List<OpportunityT> opportunitiesList = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfterAndOpportunityNameLike(
						customerId, fromTimestamp, "%" + term.toUpperCase()
								+ "%");
		return opportunitiesList;
	}

	/**
	 * This method is used to find the opportunities for the customerId,
	 * opportunityOwner like search and after request received date
	 * 
	 * @param term
	 * @param customerId
	 * @param user 
	 * @param timestamp
	 * @return
	 */
	private List<OpportunityT> getOpportunitiesByOwnerAndCustomerIdAndReceivedDate(
			String term, String customerId, Timestamp fromTimestamp, UserT user) {
		List<OpportunityT> opportunitiesList = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfterAndOpportunityOwnerLike(
						customerId, fromTimestamp, "%" + term.toUpperCase()
								+ "%");
		return opportunitiesList;
	}

	/**
	 * This method is used to find the opportunities for the customerId,
	 * opportunity SubSp like search and after request received date
	 * 
	 * @param term
	 * @param customerId
	 * @param user 
	 * @param timestamp
	 * @return
	 */
	private List<OpportunityT> getOpportunitiesBySubSpsAndCustomerIdAndReceivedDate(
			String term, String customerId, Timestamp fromTimestamp, UserT user) {
		List<OpportunityT> opportunitiesList = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfterAndSubSpLike(
						customerId, fromTimestamp, "%" + term.toUpperCase()
								+ "%");
		return opportunitiesList;
	}

	/**
	 * This method is used to find the opportunities for the customerId after
	 * request received date
	 * 
	 * @param customerId
	 * @param timestamp
	 * @return
	 */
	private List<OpportunityT> getAllOpportunitiesByCustomerIdAndStartDateOfConnectBetween(
			String customerId, Timestamp fromTimestamp) {
		List<OpportunityT> opportunitiesList = opportunityRepository
				.findByCustomerIdAndOpportunityRequestReceiveDateAfter(
						customerId, fromTimestamp);
		return opportunitiesList;
	}

	/**
	 * This method is used to fetch the opportunities by owner and role
	 * 
	 * @param userId
	 * @param opportunityRole
	 * @param toCurrency
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * To fetch the opportunities by task owner for specific role
	 * 
	 * @param opportunityOwner
	 * @param opportunityRole
	 * @param fromDate
	 * @param toDate
	 * @param toCurrency
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * To fetch the opportunity for primary owner
	 * 
	 * @param userId
	 * @param isOnly
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws DestinationException
	 */
	private List<OpportunityT> findForPrimaryOwner(String userId,
			boolean isOnly, Date fromDate, Date toDate)
			throws DestinationException {
		logger.debug("Inside findForPrimaryOwner() service");
		List<OpportunityT> opportunities = opportunityRepository
				.findByOpportunityOwnerAndDealClosureDateBetween(userId,
						fromDate, toDate);

		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	/**
	 * To validate and return the opportunities data
	 * 
	 * @param opportunities
	 * @param validate
	 * @return
	 * @throws DestinationException
	 */
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

	/**
	 * To find the opportunities for bid details
	 * 
	 * @param userId
	 * @param isOnly
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws DestinationException
	 */
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

	/**
	 * To fetch the opportunities for sales support owner
	 * 
	 * @param userId
	 * @param isOnly
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws DestinationException
	 */
	private List<OpportunityT> findForSalesSupport(String userId,
			boolean isOnly, Date fromDate, Date toDate)
			throws DestinationException {
		logger.debug("Inside findForSalesSupport() service");
		List<OpportunityT> opportunities = opportunityRepository
				.findOpportunityTForSalesSupportOwnerWithDateBetween(userId,
						fromDate, toDate);
		return validateAndReturnOpportunitesData(opportunities, isOnly);
	}

	/**
	 * To fetch opportunities by opportunity id
	 * 
	 * @param opportunityId
	 * @param toCurrency
	 * @return
	 * @throws Exception
	 */
	public OpportunityT findByOpportunityId(String opportunityId,
			List<String> toCurrency) throws Exception {
		logger.debug("Inside findByOpportunityId() service");	
		boolean isBfmRaied = false;
		OpportunityT opportunity = opportunityRepository
				.findByOpportunityId(opportunityId);
		int[] bfmEntityTypeIds = {Constants.CONSTANT_FOUR,Constants.CONSTANT_FIVE,Constants.CONSTANT_SIX,Constants.CONSTANT_SEVEN,Constants.CONSTANT_EIGHT};
		List<String> workflowBfmIds = new ArrayList<String>();
		if (opportunity != null) {
			// Add Search Keywords
			List<SearchKeywordsT> searchKeywords = searchKeywordsRepository
					.findByEntityTypeAndEntityId(
							EntityType.OPPORTUNITY.toString(),
							opportunity.getOpportunityId());

			logger.debug("Search Keywords" + searchKeywords);
			prepareOpportunity(opportunity, null);

			beaconConverterService.convertOpportunityCurrency(opportunity,
					toCurrency);
			// Getting the workflow request in order to check whether if the
			// opportunity is placed for reopen request
			List<WorkflowRequestT> workflowRequests = workflowRequestRepository
					.findByEntityTypeIdAndEntityIdAndStatus(
							EntityTypeId.OPPORTUNITY.getType(), opportunityId,
							WorkflowStatus.PENDING.getStatus());

			if (CollectionUtils.isNotEmpty(workflowRequests)) {
				opportunity.setWorkflowRequest(workflowRequests.get(0));
			}
			// flag to check whether workflow bfm is raised already for this opportunity
			List<WorkflowBfmT> workflowBfmTs = workflowBfmTRepository.findWorkflowBfmIdByOpportunityId(opportunityId);
			if(workflowBfmTs.size() > 0){
				for(WorkflowBfmT bfm : workflowBfmTs){
					workflowBfmIds.add(bfm.getWorkflowBfmId());
				}
				WorkflowRequestT workflowBfmRequest = workflowRequestRepository.findByEntityIdInAndStatusAndEntityTypeIdIn(workflowBfmIds,
						WorkflowStatus.PENDING.getStatus(), bfmEntityTypeIds);
				if(workflowBfmRequest != null) {
					isBfmRaied = true;
				}
			}
			
			setNullForDeliveryMasterCyclic(opportunity);
			opportunity.setWorkflowBfmRaised(isBfmRaied);
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

	public void restrictOpportunity(List<OpportunityT> opportunities) {
		if (opportunities != null && opportunities.size() > 0) {
			for (OpportunityT opportunityT : opportunities) {
				restrictOpportunity(opportunityT);
			}
		}
	}

	/**
	 * This method is used to validate whether the given user is owner for the
	 * particular opportunity
	 * 
	 * @param userId
	 * @param opportunity
	 * @return
	 */
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

	/**
	 * This method is used to create a new opportunity
	 * 
	 * @param opportunity
	 * @param isBulkDataLoad
	 * @param bidRequestType
	 * @param actualSubmissionDate
	 * @param status
	 * @throws Exception
	 */
	@Transactional
	public List<AsyncJobRequest> createOpportunity(OpportunityT opportunity,
			boolean isBulkDataLoad, String bidRequestType,
			String actualSubmissionDate, Status status) throws Exception {
		List<AsyncJobRequest> asyncJobRequests = Lists.newArrayList();
		logger.debug("Inside createOpportunity() service");
		
		Boolean deliveryTeamFlag = false;
		OpportunityT createdOpportunity = null;
		if (opportunity != null) {
			opportunity.setOpportunityId(null);
			String userId = DestinationUtils.getCurrentUserDetails()
					.getUserId();
			UserT user = userRepository.findByUserId(userId);
			String userGroup = user.getUserGroup();
			UserGroup userGroupE = UserGroup.getUserGroup(userGroup);
			if (userGroupE == UserGroup.DELIVERY_CENTRE_HEAD
					|| userGroupE == UserGroup.DELIVERY_CLUSTER_HEAD
					|| userGroupE == UserGroup.DELIVERY_MANAGER) {
				deliveryTeamFlag = true;
			}
			opportunity.setDeliveryTeamFlag(deliveryTeamFlag);
			opportunity.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			opportunity.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			createdOpportunity = saveOpportunity(opportunity, false, userGroup,
					null, -1);
			// check sales stage code and save deal financial file in
			// workflowbfm_t
			if (createdOpportunity.getOpportunityId() != null) {
				saveBfmFile(createdOpportunity, opportunity, status);
			}

			if (createdOpportunity.getDigitalDealValue() != null) {
				BigDecimal dealValueInUSD = opportunityDownloadService
						.convertCurrencyToUSD(
								createdOpportunity.getDealCurrency(),
								createdOpportunity.getDigitalDealValue());
				if ((createdOpportunity.getSalesStageCode() >= SalesStageCode.RFP_SUBMITTED
						.getCodeValue() && createdOpportunity
						.getSalesStageCode() <= SalesStageCode.CONTRACT_NEGOTIATION
						.getCodeValue())
						&& isAboveOrEqualHighDeal(dealValueInUSD)) {
					
					asyncJobRequests.add(constructAsyncJobRequest(opportunity.getOpportunityId(), EntityType.OPPORTUNITY, JobName.opportunityEmailNotification, 
							dealValueInUSD.doubleValue(),null));
				}
			}
			List<OpportunityDeliveryCentreMappingT> opportunityDeliveryCentreMappingTs = createdOpportunity.getOpportunityDeliveryCentreMappingTs();
			if(CollectionUtils.isNotEmpty(opportunityDeliveryCentreMappingTs)) {
				for(OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT : opportunityDeliveryCentreMappingTs) {
					asyncJobRequests.add(constructAsyncJobRequest(opportunityDeliveryCentreMappingT.getOpportunityId(), EntityType.OPPORTUNITY, JobName.deliveryEmailNotification, 
							null,opportunityDeliveryCentreMappingT.getDeliveryCentreId()));
				}
			}
			if (!isBulkDataLoad) {
				// // Invoke Asynchronous Auto Comments Thread
				processAutoComments(opportunity.getOpportunityId(), null);
				// // Invoke Asynchronous Notification Thread
				// processNotifications(opportunity.getOpportunityId(), null);
			} else {
				// This statement is to update the opportunity timeline history
				saveOpportunityTimelineHistoryForUpload(createdOpportunity,
						bidRequestType, actualSubmissionDate);
			}
		}
		return asyncJobRequests;
	}

	private void saveBfmFile(OpportunityT createdOpportunity,
			OpportunityT opportunity, Status status) throws Exception {
		List<Integer> bfmSalesStages = Arrays.asList(5, 6, 7, 8, 9);
		WorkflowBfmT workflowBfmSaved = new WorkflowBfmT();
		if( bfmSalesStages.contains(opportunity.getSalesStageCode()) 
				&& opportunity.getDealFinancialFile() != null 
				&& opportunity.getDealFinancialFile().length > 0) {
			WorkflowBfmT workflowBfmt = new WorkflowBfmT();
			
			if (opportunity.getBfmFileName() == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"BFM file name should not be empty!");
			}
			
			String fileName = getFormattedBFMFileName(opportunity);
			workflowBfmt.setBfmFileName(fileName);
			
			workflowBfmt.setDealFinancialFile(opportunity
					.getDealFinancialFile());
			workflowBfmt
					.setOpportunityId(createdOpportunity.getOpportunityId());
			workflowBfmt.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			workflowBfmt.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			workflowBfmSaved = workflowBfmTRepository.save(workflowBfmt);

			// workflow getting triggered from here by making workflow request
			if (workflowBfmSaved.getWorkflowBfmId() != null) {
				workflowService.createworkflowBfmRequest(
						workflowBfmSaved.getWorkflowBfmId(),
						createdOpportunity, status);
			}
		}else{
			status.setStatus(Status.SUCCESS, opportunity.getOpportunityId());	
		}
	}


	/**
	 * This method returns the formatted file name for BFM for a given opportunity
	 * @param opportunity
	 * @return
	 */
	private String getFormattedBFMFileName(OpportunityT opportunity) {
		CustomerMasterT customerMaster = customerRepository.findOne(opportunity.getCustomerId());
		return opportunity.getOpportunityId() + "_" + customerMaster.getCustomerName() + "." + DestinationUtils.getExtension(opportunity.getBfmFileName());
	}
	

	/**
	 * This method is used to update Opportunity Timeline History. Sales Stage
	 * Codes from 6-13 are only updated here with Sales Stage Codes based on the
	 * Bid Request Type
	 * 
	 * @param createdOpportunity
	 * @param bidRequestType
	 * @param actualSubmissionDate
	 * @throws Exception
	 */
	private void saveOpportunityTimelineHistoryForUpload(
			OpportunityT createdOpportunity, String bidRequestType,
			String actualSubmissionDate) throws Exception {
		try {

			switch (createdOpportunity.getSalesStageCode()) {
			case 6:
			case 7:
			case 8:
			case 9:
			case 10: {
				saveOppTimelineHistoryInUpload(createdOpportunity, 5);
				break;
			}
			case 11:
			case 12:
			case 13: {
				if (!StringUtils.isEmpty(bidRequestType)) {
					if (bidRequestType.equalsIgnoreCase("RFI")
							|| bidRequestType.equalsIgnoreCase("RFQ")
							|| bidRequestType.equalsIgnoreCase("Approach Note")) {
						if (StringUtils.isEmpty(actualSubmissionDate)) {
							saveOppTimelineHistoryInUpload(createdOpportunity,
									2);
						} else if (!StringUtils.isEmpty(actualSubmissionDate)) {
							saveOppTimelineHistoryInUpload(createdOpportunity,
									3);
						}
					} else if (bidRequestType.equalsIgnoreCase("RFP")
							|| bidRequestType.equalsIgnoreCase("Proactive")) {
						if (StringUtils.isEmpty(actualSubmissionDate)) {
							saveOppTimelineHistoryInUpload(createdOpportunity,
									4);
						} else if (!StringUtils.isEmpty(actualSubmissionDate)) {
							saveOppTimelineHistoryInUpload(createdOpportunity,
									5);
						}
					}
				}
				break;
			}
			default:
				break;
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Record Saved! Error while updating Opportunity Timeline History");
		} finally {
			bidId = null;
		}

	}

	private void saveOppTimelineHistoryInUpload(
			OpportunityT createdOpportunity, int salesStageCode) {
		OpportunityTimelineHistoryT history = new OpportunityTimelineHistoryT();

		history.setOpportunityId(createdOpportunity.getOpportunityId());
		history.setSalesStageCode(salesStageCode);
		if (!StringUtils.isEmpty(bidId)) {
			history.setBidId(bidId);
		}
		history.setUserUpdated(DestinationUtils.getCurrentUserDetails()
				.getUserId());
		opportunityTimelineHistoryTRepository.save(history);
	}

	/**
	 * This method is used to save an opportunity and also used to edit an
	 * existing opportunity
	 * 
	 * @param opportunity
	 * @param isUpdate
	 * @param userGroup
	 * @param opportunityBeforeEdit
	 * @param oldSalesStageCode 
	 * @return
	 * @throws Exception
	 */
	private OpportunityT saveOpportunity(OpportunityT opportunity,
			boolean isUpdate, String userGroup,
			OpportunityT opportunityBeforeEdit, int oldSalesStageCode) throws Exception {
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
					if (!opportunity
							.getOpportunityOwner()
							.equals(opportunitySalesSupportLinkTRepository
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
//		boolean isISU = validateForCreateEngagement(opportunity,opportunityBeforeEdit);

		if (isUpdate) {
			deleteChildObjects(opportunity);
		}
		OpportunityT baseOpportunity = saveBaseObject(opportunity);
		saveChildObject(opportunity,oldSalesStageCode);
		opportunity.setOpportunityId(baseOpportunity.getOpportunityId());
		return opportunity;

	}

	/**
	 * This method validates primary SubSp for a opportunity
	 * 
	 * @param opportunity
	 * @return
	 */
	private void validateOpportunityPrimarySubSp(OpportunityT opportunity) {
		logger.info("Inside validation for opportunity primary SubSp");
		if (opportunity.getOpportunitySubSpLinkTs() != null) {
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
	 * 
	 * @param opportunity
	 */
	public void validateInactiveIndicators(OpportunityT opportunity) {

		//createdBy
		String createdBy = opportunity.getCreatedBy();
		if (StringUtils.isNotBlank(createdBy)
				&& userRepository.findByActiveTrueAndUserId(createdBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The user createdBy is inactive");
		}

		// modifiedBy,
		String modifiedBy = opportunity.getModifiedBy();
		if (StringUtils.isNotBlank(modifiedBy)
				&& userRepository.findByActiveTrueAndUserId(modifiedBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The user modifiedBy is inactive");
		}

		// customerId,
		String customerId = opportunity.getCustomerId();
		if (StringUtils.isNotBlank(customerId)
				&& customerRepository.findByActiveTrueAndCustomerId(customerId) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The customer is inactive");
		}

		// country
		String country = opportunity.getCountry();
		if (StringUtils.isNotBlank(country)
				&& countryRepository.findByActiveTrueAndCountry(country) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"The country is inactive");
		}

		// opportunityOwner,
		String opportunityOwner = opportunity.getOpportunityOwner();
		if (StringUtils.isNotBlank(opportunityOwner)
				&& userRepository.findByActiveTrueAndUserId(opportunityOwner) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Please assign an active primary owner before making any changes.");
		}

		// opportunityCompetitorLinkTs,
		List<OpportunityCompetitorLinkT> opportunityCompetitorLinkTs = opportunity
				.getOpportunityCompetitorLinkTs();
		if (CollectionUtils.isNotEmpty(opportunityCompetitorLinkTs)) {
			for (OpportunityCompetitorLinkT compLink : opportunityCompetitorLinkTs) {
				String competitorName = compLink.getCompetitorName();
				if (StringUtils.isNotBlank(competitorName)
						&& competitorRepository
								.findByActiveTrueAndCompetitorName(competitorName) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The competitor is inactive");
				}
			}
		}

		// opportunityCustomerContactLinkTs,
		List<OpportunityCustomerContactLinkT> oppCustomerContactLinkTs = opportunity
				.getOpportunityCustomerContactLinkTs();
		if (CollectionUtils.isNotEmpty(oppCustomerContactLinkTs)) {
			for (OpportunityCustomerContactLinkT contact : oppCustomerContactLinkTs) {
				String contactId = contact.getContactId();
				if (StringUtils.isNotBlank(contactId)
						&& contactRepository
								.findByActiveTrueAndContactId(contactId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The customer contact is inactive");
				}
			}
		}

		// opportunityOfferingLinkTs,
		List<OpportunityOfferingLinkT> connectOfferingLinkTs = opportunity
				.getOpportunityOfferingLinkTs();
		if (CollectionUtils.isNotEmpty(connectOfferingLinkTs)) {
			for (OpportunityOfferingLinkT offeringLink : connectOfferingLinkTs) {
				String offering = offeringLink.getOffering();
				if (StringUtils.isNotBlank(offering)
						&& offeringRepository
								.findByActiveTrueAndOffering(offering) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The offering is inactive");
				}
			}
		}

		// List<OpportunityPartnerLinkT> opportunityPartnerLinkTs,
		List<OpportunityPartnerLinkT> opportunityPartnerLinkTs = opportunity
				.getOpportunityPartnerLinkTs();
		if (CollectionUtils.isNotEmpty(opportunityPartnerLinkTs)) {
			for (OpportunityPartnerLinkT partnerLink : opportunityPartnerLinkTs) {
				String partnerId = partnerLink.getPartnerId();
				if (StringUtils.isNotBlank(partnerId)
						&& partnerRepository
								.findByActiveTrueAndPartnerId(partnerId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The partner is inactive");
				}
			}
		}

		// opportunitySubSpLinkTs,
		List<OpportunitySubSpLinkT> oppSubSpLinkTs = opportunity
				.getOpportunitySubSpLinkTs();
		if (CollectionUtils.isNotEmpty(oppSubSpLinkTs)) {
			for (OpportunitySubSpLinkT subSpLink : oppSubSpLinkTs) {
				String subSp = subSpLink.getSubSp();
				if (StringUtils.isNotBlank(subSp)
						&& subSpRepository.findByActiveTrueAndSubSp(subSp) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The subsp is inactive");
				}
			}
		}

		// opportunityTcsAccountContactLinkTs,
		List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs = opportunity
				.getOpportunityTcsAccountContactLinkTs();
		if (CollectionUtils.isNotEmpty(opportunityTcsAccountContactLinkTs)) {
			for (OpportunityTcsAccountContactLinkT contactLink : opportunityTcsAccountContactLinkTs) {
				String contactId = contactLink.getContactId();
				if (StringUtils.isNotBlank(contactId)
						&& contactRepository
								.findByActiveTrueAndContactId(contactId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The account contact is inactive");
				}
			}
		}

		// opportunityWinLossFactorsTs,
		List<OpportunityWinLossFactorsT> opportunityWinLossFactorsTs = opportunity
				.getOpportunityWinLossFactorsTs();
		if (CollectionUtils.isNotEmpty(opportunityWinLossFactorsTs)) {
			for (OpportunityWinLossFactorsT oppWLFactor : opportunityWinLossFactorsTs) {
				String wlFactor = oppWLFactor.getWinLossFactor();
				if (StringUtils.isNotBlank(wlFactor)
						&& winlossFactorRepository
								.findByActiveTrueAndWinLossFactor(wlFactor) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The win loss factor is inactive");
				}
			}
		}

		// OpportunitySalesSupportLink,
		List<OpportunitySalesSupportLinkT> opportunitySaleSupOwnTs = opportunity
				.getOpportunitySalesSupportLinkTs();
		if (CollectionUtils.isNotEmpty(opportunitySaleSupOwnTs)) {
			for (OpportunitySalesSupportLinkT oppWLFactor : opportunitySaleSupOwnTs) {
				String userId = oppWLFactor.getSalesSupportOwner();
				if (StringUtils.isNotBlank(userId)
						&& userRepository.findByActiveTrueAndUserId(userId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"The sales support owner is inactive");
				}
			}
		}

		// BidDetails,
		List<BidDetailsT> bidDetailsT = opportunity.getBidDetailsTs();
		if (CollectionUtils.isNotEmpty(bidDetailsT)) {
			for (BidDetailsT bidDetail : bidDetailsT) {
				List<BidOfficeGroupOwnerLinkT> bidofficeGrpOwners = bidDetail
						.getBidOfficeGroupOwnerLinkTs();
				if (CollectionUtils.isNotEmpty(bidofficeGrpOwners)) {
					for (BidOfficeGroupOwnerLinkT bidgrpOwner : bidofficeGrpOwners) {
						String userId = bidgrpOwner.getBidOfficeGroupOwner();
						if (StringUtils.isNotBlank(userId)
								&& userRepository
										.findByActiveTrueAndUserId(userId) == null) {
							throw new DestinationException(
									HttpStatus.BAD_REQUEST,
									"The Bid Office Group Owner is inactive");
						}
					}
				}
			}
		}

	}

	private OpportunityT saveChildObject(OpportunityT opportunity, int oldSalesStageCode)
			throws Exception {
		logger.debug("Inside saveChildObject() method");

		// Getting the userId from the session
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (opportunity.getOpportunityCustomerContactLinkTs() != null) {
			for (OpportunityCustomerContactLinkT customerContact : opportunity
					.getOpportunityCustomerContactLinkTs()) {
				customerContact
						.setOpportunityId(opportunity.getOpportunityId());
				customerContact.setCreatedBy(userId);
				customerContact.setModifiedBy(userId);
				opportunityCustomerContactLinkTRepository.save(customerContact);
			}
		}

		if (opportunity.getOpportunityTcsAccountContactLinkTs() != null) {
			for (OpportunityTcsAccountContactLinkT tcsContact : opportunity
					.getOpportunityTcsAccountContactLinkTs()) {
				tcsContact.setOpportunityId(opportunity.getOpportunityId());
				tcsContact.setCreatedBy(userId);
				tcsContact.setModifiedBy(userId);
				opportunityTcsAccountContactLinkTRepository.save(tcsContact);
			}
		}

		if (opportunity.getOpportunityPartnerLinkTs() != null) {
			for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunity
					.getOpportunityPartnerLinkTs()) {
				opportunityPartnerLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityPartnerLinkT.setCreatedBy(userId);
				opportunityPartnerLinkT.setModifiedBy(userId);
				opportunityPartnerLinkTRepository.save(opportunityPartnerLinkT);
			}
		}

		if (opportunity.getOpportunityCompetitorLinkTs() != null) {
			for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
					.getOpportunityCompetitorLinkTs()) {
				opportunityCompetitorLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityCompetitorLinkT.setCreatedBy(userId);
				opportunityCompetitorLinkT.setModifiedBy(userId);
				opportunityCompetitorLinkTRepository
						.save(opportunityCompetitorLinkT);
			}
		}

		if (opportunity.getOpportunitySubSpLinkTs() != null) {
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
					.getOpportunitySubSpLinkTs()) {
				opportunitySubSpLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunitySubSpLinkT.setCreatedBy(userId);
				opportunitySubSpLinkT.setModifiedBy(userId);
				opportunitySubSpLinkTRepository.save(opportunitySubSpLinkT);
			}
		}

		if (opportunity.getOpportunityOfferingLinkTs() != null) {
			for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity
					.getOpportunityOfferingLinkTs()) {
				opportunityOfferingLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunityOfferingLinkT.setCreatedBy(userId);
				opportunityOfferingLinkT.setModifiedBy(userId);
				opportunityOfferingLinkTRepository
						.save(opportunityOfferingLinkT);
			}
		}

		if (opportunity.getConnectOpportunityLinkIdTs() != null) {
			for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunity
					.getConnectOpportunityLinkIdTs()) {
				connectOpportunityLinkIdT.setOpportunityId(opportunity
						.getOpportunityId());
				connectOpportunityLinkIdT.setCreatedBy(userId);
				connectOpportunityLinkIdT.setModifiedBy(userId);
				connectOpportunityLinkTRepository
						.save(connectOpportunityLinkIdT);
			}
		}

		if (opportunity.getOpportunitySalesSupportLinkTs() != null) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getOpportunitySalesSupportLinkTs()) {
				opportunitySalesSupportLinkT.setOpportunityId(opportunity
						.getOpportunityId());
				opportunitySalesSupportLinkT.setCreatedBy(userId);
				opportunitySalesSupportLinkT.setModifiedBy(userId);
				opportunitySalesSupportLinkTRepository
						.save(opportunitySalesSupportLinkT);
			}
		}

		if (opportunity.getNotesTs() != null) {
			for (NotesT notesT : opportunity.getNotesTs()) {
				notesT.setOpportunityId(opportunity.getOpportunityId());
				notesT.setUserUpdated(userId);
				notesTRepository.save(notesT);
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

				logger.debug("Saved Bid Details " + bidDetailsT.getBidId());
				if (bidOfficeOwnerLinkTs != null
						&& bidOfficeOwnerLinkTs.size() > 0) {
					bidDetailsT
							.setBidOfficeGroupOwnerLinkTs(bidOfficeOwnerLinkTs);
				}
				BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLinkT = bidOfficeGroupOwnerLinkTRepository
						.findFirstByBidId(bidDetailsT.getBidId());
				if (CollectionUtils.isNotEmpty(bidDetailsT
						.getBidOfficeGroupOwnerLinkTs())) {
					BidOfficeGroupOwnerLinkT bidOfficeGroupOwnerLink = bidDetailsT
							.getBidOfficeGroupOwnerLinkTs().get(0);
					if (bidOfficeGroupOwnerLinkT != null) {

						if (!StringUtils.equals(bidOfficeGroupOwnerLink
								.getBidOfficeGroupOwner(),
								bidOfficeGroupOwnerLinkT
										.getBidOfficeGroupOwner())) {
							bidOfficeGroupOwnerLinkTRepository
									.delete(bidOfficeGroupOwnerLinkT);
							bidOfficeGroupOwnerLink.setBidId(bidDetailsT
									.getBidId());
							bidOfficeGroupOwnerLink.setCreatedBy(userId);
							bidOfficeGroupOwnerLink.setModifiedBy(userId);
							bidOfficeGroupOwnerLinkTRepository
									.save(bidOfficeGroupOwnerLink);
						}
					} else {
						if (StringUtils.isEmpty(bidOfficeGroupOwnerLink
								.getBidId())) {
							bidOfficeGroupOwnerLink.setBidId(bidDetailsT
									.getBidId());
							bidOfficeGroupOwnerLink.setCreatedBy(userId);
							bidOfficeGroupOwnerLink.setModifiedBy(userId);
							bidOfficeGroupOwnerLinkTRepository
									.save(bidOfficeGroupOwnerLink);
						}

					}

				} else {
					if (bidOfficeGroupOwnerLinkT != null) {
						bidOfficeGroupOwnerLinkTRepository
								.delete(bidOfficeGroupOwnerLinkT);
					}

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
						opportunityTimelineHistoryTRepository
								.save(savedOpportunityTimelineHistoryTs);
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
				opportunityWinLossFactorsTRepository
						.save(opportunityWinLossFactorsT);
			}
		}

		List<OpportunityDeliveryCentreMappingT> deliveryCentresFromUI = opportunity
				.getOpportunityDeliveryCentreMappingTs();
		List<Integer> storedCentres = opportunityDeliveryCentreMappingTRepository.getIdByOpportunityId(opportunity
				.getOpportunityId());

		if (deliveryCentresFromUI != null) {
			for (OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT : deliveryCentresFromUI) {
				if(opportunityDeliveryCentreMappingT.getOpportunityDeliveryCentreId() != null && storedCentres != null) {
					storedCentres.remove(opportunityDeliveryCentreMappingT.getOpportunityDeliveryCentreId());
				}

				opportunityDeliveryCentreMappingT.setOpportunityId(opportunity.getOpportunityId());
				opportunityDeliveryCentreMappingT.setModifiedBy(userId);
				opportunityDeliveryCentreMappingT.setCreatedBy(userId);
				opportunityDeliveryCentreMappingTRepository.save(opportunityDeliveryCentreMappingT);
			}
			
			//deleting the removed delivery centres
			for (Integer id : storedCentres) {
				opportunityDeliveryCentreMappingTRepository.delete(id);
			}
			// Creating Intimated Delivery if Opportunity Wins
			if (opportunity.getSalesStageCode() != oldSalesStageCode
					&& opportunity.getSalesStageCode() == SalesStageCode.WIN
							.getCodeValue()) {
				List<Integer> deliveryCentreIds = Lists.newArrayList();
				for (OpportunityDeliveryCentreMappingT opportunityDeliveryCentreMappingT : opportunity
						.getOpportunityDeliveryCentreMappingTs()) {
					deliveryCentreIds.add(opportunityDeliveryCentreMappingT
							.getDeliveryCentreId());
				}
				//Getting Cluster and their Respective delivery centres Map
				Map<Integer, List<Integer>> deliveryCentreMap = deliveryMasterService
						.getDeliveryCentreForCluster(deliveryCentreIds);
				deliveryMasterService.createDeliveryIntimated(opportunity,
						deliveryCentreMap, userId);
			}
		}
			
		// return opportunityRepository.save(opportunity);
		return opportunity;
	}


	private OpportunityT saveBaseObject(OpportunityT opportunity)
			throws Exception {
		logger.debug("Inside saveBaseObject() method");
		OpportunityT baseOpportunityT = new OpportunityT();
		baseOpportunityT.setCreatedBy(opportunity.getCreatedBy());
		baseOpportunityT.setCreatedDatetime(opportunity.getCreatedDatetime());

		baseOpportunityT.setModifiedBy(opportunity.getModifiedBy());
		baseOpportunityT.setModifiedDatetime(opportunity.getModifiedDatetime());
		baseOpportunityT.setDealCurrency(opportunity.getDealCurrency());
		baseOpportunityT.setDigitalFlag(opportunity.getDigitalFlag());
		baseOpportunityT.setCrmId(opportunity.getCrmId());
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
		baseOpportunityT.setStrategicDeal(opportunity.getStrategicDeal());
		baseOpportunityT.setDealType(opportunity.getDealType());
		baseOpportunityT.setCountry(opportunity.getCountry());
		baseOpportunityT.setEngagementStartDate(opportunity
				.getEngagementStartDate());
		baseOpportunityT.setEngagementDuration(opportunity
				.getEngagementDuration());
		baseOpportunityT.setOpportunityId(opportunity.getOpportunityId());
		baseOpportunityT.setOpportunityOwner(opportunity.getOpportunityOwner());

		baseOpportunityT.setIsuOwnReason(opportunity.getIsuOwnReason());
		if (opportunity.getDeliveryOwnershipId() != null) {
			baseOpportunityT.setDeliveryOwnershipId(opportunity
					.getDeliveryOwnershipId());
		}
		baseOpportunityT.setSalesStageCode(opportunity.getSalesStageCode());
		baseOpportunityT.setDeliveryTeamFlag(opportunity.getDeliveryTeamFlag());
		opportunity.setOpportunityId(opportunityRepository.save(
				baseOpportunityT).getOpportunityId());
		logger.debug("ID " + baseOpportunityT.getOpportunityId());
		return baseOpportunityT;

	}

	// Method called from controller
	@Transactional
	public void updateOpportunity(OpportunityT opportunity,
			OpportunityT opportunityBeforeEdit, Status status, int oldSalesStageCode) throws Exception {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		String opportunityId = opportunity.getOpportunityId();
		opportunity.setCreatedBy(userId);
		opportunity.setModifiedBy(userId);
		logger.debug("Inside updateOpportunity() service");

		UserT user = userRepository.findByUserId(userId);
		String userGroup = user.getUserGroup();

		if (!userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {

			if (!isEditAccessRequiredForOpportunity(opportunityBeforeEdit,
					userGroup, userId, true)) {
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
		logger.debug("oldObject" + oldObject);
		
		// deal closure comments is mandatory for sales stage codes (11/12/13)
		if (opportunity.getSalesStageCode() == 11
				|| opportunity.getSalesStageCode() == 12
				|| opportunity.getSalesStageCode() == 13) {
			if ((opportunity.getDealClosureComments() == null)
					&& StringUtils
							.isEmpty(opportunity.getDealClosureComments())) {
				logger.error("Deal closure comments is mandatory for the opportuniy for sales stage codes (11,12 and 13)");
				throw new DestinationException(
						HttpStatus.BAD_REQUEST,
						"Deal closure comments is mandatory for the opportuniy for sales stage codes (11,12 and 13)");
			}
		}
		opportunity.setDeliveryTeamFlag(opportunityBeforeEdit.getDeliveryTeamFlag());
		// Update database
		OpportunityT afterOpp = saveOpportunity(opportunity, true, userGroup,
				opportunityBeforeEdit, oldSalesStageCode);
		// check sales stage code and save deal financial file in workflowbfm_t  

		if(afterOpp.getOpportunityId() != null) {
			saveBfmFile(afterOpp, opportunity, status);
		}
		if (afterOpp != null) {
			logger.info("Opportunity has been updated successfully: "
					+ opportunityId);
			// // Invoke Asynchronous Auto Comments Thread
			processAutoComments(opportunityId, oldObject);
			// // Invoke Asynchronous Notifications Thread
			// processNotifications(opportunityId, oldObject);
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
			for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : opportunity
					.getDeleteConnectOpportunityLinkIdTs()) {
				connectOpportunityLinkTRepository
						.delete(connectOpportunityLinkIdT
								.getConnectOpportunityLinkId());
			}
			opportunity.setDeleteConnectOpportunityLinkIdTs(null);
		}

		if (opportunity.getDeleteOpportunityPartnerLinkTs() != null
				&& opportunity.getDeleteOpportunityPartnerLinkTs().size() > 0) {
			for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunity
					.getDeleteOpportunityPartnerLinkTs()) {
				opportunityPartnerLinkTRepository
						.delete(opportunityPartnerLinkT
								.getOpportunityPartnerLinkId());
			}
			opportunity.setOpportunityPartnerLinkTs(null);
		}

		if (opportunity.getDeleteOpportunityCompetitorLinkTs() != null
				&& opportunity.getDeleteOpportunityCompetitorLinkTs().size() > 0) {
			for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : opportunity
					.getDeleteOpportunityCompetitorLinkTs()) {
				opportunityCompetitorLinkTRepository
						.delete(opportunityCompetitorLinkT
								.getOpportunityCompetitorLinkId());
			}
			opportunity.setDeleteOpportunityCompetitorLinkTs(null);
		}

		if (opportunity.getDeleteOpportunityCustomerContactLinkTs() != null
				&& opportunity.getDeleteOpportunityCustomerContactLinkTs()
						.size() > 0) {
			for (OpportunityCustomerContactLinkT opportunityCustomerContactLinkT : opportunity
					.getDeleteOpportunityCustomerContactLinkTs()) {
				opportunityCustomerContactLinkTRepository
						.delete(opportunityCustomerContactLinkT
								.getOpportunityCustomerContactLinkId());
			}
		}

		if (opportunity.getDeleteOpportunityOfferingLinkTs() != null
				&& opportunity.getDeleteOpportunityOfferingLinkTs().size() > 0) {
			for (OpportunityOfferingLinkT opportunityOfferingLinkT : opportunity
					.getDeleteOpportunityOfferingLinkTs()) {
				opportunityOfferingLinkTRepository
						.delete(opportunityOfferingLinkT
								.getOpportunityOfferingLinkId());
			}
		}

		if (opportunity.getDeleteOpportunitySalesSupportLinkTs() != null
				&& opportunity.getDeleteOpportunitySalesSupportLinkTs().size() > 0) {
			for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : opportunity
					.getDeleteOpportunitySalesSupportLinkTs()) {

				opportunitySalesSupportLinkTRepository
						.delete(opportunitySalesSupportLinkT
								.getOpportunitySalesSupportLinkId());
			}
		}

		if (opportunity.getDeleteOpportunitySubSpLinkTs() != null
				&& opportunity.getDeleteOpportunitySubSpLinkTs().size() > 0) {
			for (OpportunitySubSpLinkT opportunitySubSpLinkT : opportunity
					.getDeleteOpportunitySubSpLinkTs()) {
				opportunitySubSpLinkTRepository.delete(opportunitySubSpLinkT
						.getOpportunitySubSpLinkId());
			}
		}

		if (opportunity.getDeleteOpportunityTcsAccountContactLinkTs() != null
				&& opportunity.getDeleteOpportunityTcsAccountContactLinkTs()
						.size() > 0) {
			for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : opportunity
					.getDeleteOpportunityTcsAccountContactLinkTs())
				opportunityTcsAccountContactLinkTRepository
						.delete(opportunityTcsAccountContactLinkT
								.getOpportunityTcsAccountContactLinkId());
		}

		if (opportunity.getDeleteOpportunityWinLossFactorsTs() != null
				&& opportunity.getDeleteOpportunityWinLossFactorsTs().size() > 0) {
			for (OpportunityWinLossFactorsT opportunityWinLossFactorsT : opportunity
					.getDeleteOpportunityWinLossFactorsTs())
				opportunityWinLossFactorsTRepository
						.delete(opportunityWinLossFactorsT
								.getOpportunityWinLossFactorsId());
		}

		if (opportunity.getDeleteSearchKeywordsTs() != null
				&& opportunity.getDeleteSearchKeywordsTs().size() > 0) {
			for (SearchKeywordsT searchKeywordsT : opportunity
					.getDeleteSearchKeywordsTs())
				searchKeywordsRepository.delete(searchKeywordsT
						.getSearchKeywordsId());
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
		if (CollectionUtils.isNotEmpty(opportunityTs)) {
			List<String> opportunityIds = new ArrayList<String>();
			for (OpportunityT opportunityT : opportunityTs) {
				opportunityIds.add(opportunityT.getOpportunityId());
			}
			try {
				List<String> previledgedOpportuniyies = opportunityDao
						.getPriviledgedOpportunityId(opportunityIds);

				if (opportunityTs != null) {
					for (OpportunityT opportunityT : opportunityTs) {
						prepareOpportunity(opportunityT,
								previledgedOpportuniyies);
					}
				}
			} catch (Exception e) {
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * This method is used to prepare the opportunity based on user access and
	 * remove cyclic data
	 * 
	 * @param opportunityT
	 * @param previledgedOppIdList
	 * @throws DestinationException
	 */
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
								opportunityT, userGroup, userId, false));
				checkAccessControl(opportunityT, previledgedOppIdList);
			}

		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		setUserFavourite(opportunityT);
		setSearchKeywordTs(opportunityT);
		removeCyclicForLinkedContacts(opportunityT);
		removeCyclicForLinkedConnects(opportunityT);
		removeCyclicForCustomers(opportunityT);
	}

	private void setUserFavourite(OpportunityT opportunityT) {
		boolean flag = false;
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		for (UserFavoritesT userFavorite : opportunityT.getUserFavoritesTs()) {
			if (userFavorite.getUserId().equalsIgnoreCase(userId)) {
				flag = true;
			}
		}
		opportunityT.setUserFavourite(flag);
	}

	private void checkAccessControl(OpportunityT opportunityT,
			List<String> previledgedOppIdList) throws Exception {
		// previledgedOppIdList is null only while it is a single opportunity.

		if (previledgedOppIdList != null) {
			if (!previledgedOppIdList.contains(opportunityT.getOpportunityId()) && (!opportunityT.isEnableEditAccess())) {
				preventSensitiveInfo(opportunityT);
			}
		} else {
			List<String> opportunityIdList = new ArrayList<String>();
			opportunityIdList.add(opportunityT.getOpportunityId());
			previledgedOppIdList = opportunityDao
					.getPriviledgedOpportunityId(opportunityIdList);
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
	private void  removeCyclicForLinkedContacts(OpportunityT opportunityT) {
		logger.debug("Inside removeCyclicForLinkedContacts() method");

		if (opportunityT != null) {
			if (opportunityT.getOpportunityPartnerLinkTs() != null) {
				for (OpportunityPartnerLinkT opportunityPartnerLinkT : opportunityT
						.getOpportunityPartnerLinkTs()) {
					opportunityPartnerLinkT.getPartnerMasterT().setOpportunityPartnerLinkTs(null);
					opportunityPartnerLinkT.getPartnerMasterT().setPartnerMasterT(null);

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
	public void processAutoComments(String opportunitytId, Object oldObject)
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
	public void processNotifications(String opportunitytId, Object oldObject) {
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

			// Get FromDate and ToDate based on Current Financial Year
			String finYear = DateUtils.getCurrentFinancialYear();
			Date fromDate = DateUtils.getDateFromFinancialYear(finYear, true);
			Date toDate = DateUtils.getDateFromFinancialYear(finYear, false);

			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp toDateTs = new Timestamp(toDate.getTime()
					+ ONE_DAY_IN_MILLIS - 1);

			// Adding the user himself
			users.add(supervisorUserId);

			// Get all opportunities for the users under supervisor
			List<Object[]> opportunities = opportunityRepository
					.findDealValueOfOpportunitiesBySupervisorId(users,
							fromDateTs, toDateTs);

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
			List<Integer> salesStageCode, String strategicDeal, String newLogo,
			double minDigitalDealValue, double maxDigitalDealValue,
			String dealCurrency, String digitalFlag, List<String> displayIou,
			List<String> country, List<String> partnerId,
			List<String> competitorName, List<String> searchKeywords,
			List<String> bidRequestType, List<String> offering,
			List<String> displaySubSp, List<String> opportunityName,
			List<String> userId, List<String> toCurrency, int page, int count,
			String role, Boolean isCurrentFinancialYr, UserT user)
			throws DestinationException {
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
		boolean isPrimary = false;
		boolean isSalesSupport = false;
		boolean isBidOffice = false;
		List<OpportunityT> opportunity = new ArrayList<OpportunityT>();
		if (OpportunityRole.contains(role)) {
			switch (OpportunityRole.valueOf(role)) {
			case PRIMARY_OWNER:
				logger.debug("Primary Owner Found");
				isPrimary = true;
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
				isPrimary = true;
				isSalesSupport = true;
				isBidOffice = true;
				break;
			}
			if (isCurrentFinancialYr) {

				Date fromDate = DateUtils.getDateFromFinancialYear(
						DateUtils.getCurrentFinancialYear(), true);

				Date toDate = DateUtils.getDateFromFinancialYear(
						DateUtils.getCurrentFinancialYear(), false);

				opportunity = opportunityRepository
						.findByOpportunitiesForCurrentFyIgnoreCaseLike(
								customerIdList, salesStageCode, strategicDeal,
								newLogo, defaultDealRange, minDigitalDealValue,
								maxDigitalDealValue, dealCurrency, digitalFlag,
								displayIou, country, partnerId, competitorName,
								searchKeywordString, bidRequestType, offering,
								displaySubSp, opportunityNameString, userId,
								isPrimary, isSalesSupport, isBidOffice,
								fromDate, toDate);
			} else {
				opportunity = opportunityRepository
						.findByOpportunitiesIgnoreCaseLike(customerIdList,
								salesStageCode, strategicDeal, newLogo,
								defaultDealRange, minDigitalDealValue,
								maxDigitalDealValue, dealCurrency, digitalFlag,
								displayIou, country, partnerId, competitorName,
								searchKeywordString, bidRequestType, offering,
								displaySubSp, opportunityNameString, userId,
								isPrimary, isSalesSupport, isBidOffice);
			}

		} else {
			logger.error("BAD_REQUEST: Invalid Opportunity Role: {}", role);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Oppurtunity Role: " + role);
		}
		List<OpportunityT> opportunityList = new ArrayList<OpportunityT>();
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			List<OpportunityT> deliveryOppList = validateAndGetDeliveryOpportunities(opportunity, userIds);
			opportunityList.addAll(deliveryOppList);
		} else {
			opportunityList.addAll(opportunity);
		}

		if (opportunityList.isEmpty()) {
			logger.error("NOT_FOUND: No Opportunities found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunities Found.");
		}
		opportunityResponse.setTotalCount(opportunityList.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count,
				opportunityList.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					opportunityList.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					opportunityList.size()) + 1;
			opportunityList = opportunityList.subList(fromIndex, toIndex);
			opportunityResponse.setOpportunityTs(opportunityList);
			logger.debug("OpportunityT  after pagination size is "
					+ opportunityList.size());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Opportunity available for the specified page");
		}
		prepareOpportunity(opportunityList);
		beaconConverterService.convertOpportunityCurrency(opportunityList,
				toCurrency);

		return opportunityResponse;
	}

	private List<OpportunityT> validateAndGetDeliveryOpportunities(
			List<OpportunityT> opportunities, List<String> userIds) {
		List<OpportunityT> deliveryOppList = new ArrayList<OpportunityT>();
		//TODO Refactor the logic
		List<String> deliveryOpportunities = opportunityRepository.findDeliveryOpportunityIdsByDeliveryFlagAndOwner(userIds);
		for(OpportunityT opportunityT:opportunities){
			if(deliveryOpportunities.contains(opportunityT.getOpportunityId())){
				deliveryOppList.add(opportunityT);
			}
		}
		return deliveryOppList;
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
			Boolean isCurrentFinancialYear, int page, int count, UserT user)
			throws DestinationException {

		PaginatedResponse opportunityResponse = new PaginatedResponse();
		Date fromDate = DateUtils.getDateFromFinancialYear(DateUtils.getCurrentFinancialYear(),	true);
		Date toDate = DateUtils.getDateFromFinancialYear(DateUtils.getCurrentFinancialYear(), false);
		List<OpportunityT> opportunityTs = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			opportunityResponse = getAllDeliveryOpportunities(fromDate, toDate, userIds, isCurrentFinancialYear, page, count);
		} else {
			if (isCurrentFinancialYear) {
				try {
					// Create the query and execute
					String queryString = "select OPP from OpportunityT OPP where (OPP.salesStageCode < 9) or ((OPP.dealClosureDate between ?1 and ?2) and (OPP.salesStageCode >= 9)) order by "
							+ sortBy + " " + order;
					Query query = entityManager
							.createQuery(queryString)
							.setParameter(
									1,
									fromDate)
							.setParameter(
									2,
									toDate);
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
		}
		return opportunityResponse;
	}

	/**
	 * This method is used to fetch All delivery opportunities
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param userIds
	 * @param isCurrentFinancialYear
	 * @param page
	 * @param count
	 * @return
	 */
	private PaginatedResponse getAllDeliveryOpportunities(Date fromDate,
			Date toDate, List<String> userIds, Boolean isCurrentFinancialYear, int page, int count) {
		logger.info("Inside getAllDeliveryOpportunities() method");
		PaginatedResponse delOppResponse = new PaginatedResponse();
		List<OpportunityT> opportunityTs = null;
		if (isCurrentFinancialYear) {
			 opportunityTs = opportunityRepository.findAllDeliveryOpportunitiesByYearAndOwners(fromDate, toDate, userIds);
		} else {
			 opportunityTs = opportunityRepository.findAllDeliveryOpportunitiesByOwners(userIds);
		}
		delOppResponse.setTotalCount(opportunityTs.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count, opportunityTs.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count, opportunityTs.size());
			int toIndex = PaginationUtils.getEndIndex(page, count, opportunityTs.size()) + 1;
			opportunityTs = opportunityTs.subList(fromIndex, toIndex);
			delOppResponse.setOpportunityTs(opportunityTs);
			logger.debug("OpportunityT  after pagination size is " + opportunityTs.size());
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, 
					"No Opportunity available for the specified page");
		}
		prepareOpportunity(opportunityTs);
		return delOppResponse;
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

	public ArrayList<OpportunityNameKeywordSearch> findOpportunityNameOrKeywords(
			String name, String keyword, UserT user) {
		ArrayList<OpportunityNameKeywordSearch> opportunityNameKeywordSearchList = new ArrayList<OpportunityNameKeywordSearch>();
		if (name.length() > 0)
			name = "%" + name + "%";
		if (keyword.length() > 0)
			keyword = "%" + keyword + "%";
		List<Object[]> results = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			results = opportunityRepository
					.findDeliveryOpportunityNameKeywordSearch(
							name.toUpperCase(), userIds);
		} else {
			results = opportunityRepository.findOpportunityNameOrKeywords(
					name.toUpperCase(), keyword.toUpperCase());
		}
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
	 * This Method used to get list of opportunities for the specified
	 * opportunity ids
	 * 
	 * @param opportunityIds
	 * @return
	 */
	public List<OpportunityT> findByOpportunityIds(List<String> opportunityIds,
			List<String> toCurrency) {
		logger.debug("Inside findByOpportunityIds() method");
		List<OpportunityT> opportunityList = null;
		if ((opportunityIds != null) && (!opportunityIds.isEmpty())) {
			opportunityList = opportunityRepository
					.findByOpportunityIds(opportunityIds);
		}
		if (opportunityList == null || opportunityList.isEmpty()) {
			logger.error("Opportunities not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunities not found");
		}
		prepareOpportunity(opportunityList);

		beaconConverterService.convertOpportunityCurrency(opportunityList,
				toCurrency);

		return opportunityList;
	}

	/**
	 * this method saves the opportunity list.
	 * 
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
			mapOpportunityPartnerLink.put(i,
					opportunityT.getOpportunityPartnerLinkTs());
			mapSalesSupport.put(i,
					opportunityT.getOpportunitySalesSupportLinkTs());
			mapSubSp.put(i, opportunityT.getOpportunitySubSpLinkTs());
			mapCustomerContact.put(i,
					opportunityT.getOpportunityCustomerContactLinkTs());
			mapTcsContact.put(i,
					opportunityT.getOpportunityTcsAccountContactLinkTs());
			mapOppCompetitor.put(i,
					opportunityT.getOpportunityCompetitorLinkTs());
			mapOppNotes.put(i, opportunityT.getNotesTs());
			setNullForReferencedObjects(opportunityT);

			i++;
		}

		Iterable<OpportunityT> savedList = opportunityRepository
				.save(insertList);
		Iterator<OpportunityT> saveIterator = savedList.iterator();
		System.out.println("Opportunities" + insertList);
		i = 0;
		while (saveIterator.hasNext()) {
			OpportunityT opportunity = saveIterator.next();
			List<OpportunityOfferingLinkT> offeringList = mapOppOffering.get(i);
			if (CollectionUtils.isNotEmpty(offeringList)) {
				populateOpportunityOfferingLinks(
						opportunity.getOpportunityId(), offeringList);
			}
			List<OpportunityPartnerLinkT> oppourtunityPartnerList = mapOpportunityPartnerLink
					.get(i);
			if (CollectionUtils.isNotEmpty(oppourtunityPartnerList)) {
				populateOpportunityPartnerLink(opportunity.getOpportunityId(),
						oppourtunityPartnerList);
			}
			List<OpportunitySalesSupportLinkT> salesSupportList = mapSalesSupport
					.get(i);
			if (CollectionUtils.isNotEmpty(salesSupportList)) {
				populateOppSalesSupportLink(opportunity.getOpportunityId(),
						salesSupportList);
			}
			List<OpportunitySubSpLinkT> subSpList = mapSubSp.get(i);
			if (CollectionUtils.isNotEmpty(subSpList)) {
				populateOpportunitySubSpLink(opportunity.getOpportunityId(),
						subSpList);
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
				populateOpportunityTcsAccountContactLink(
						opportunity.getOpportunityId(), tcsContactList);
			}
			List<OpportunityCompetitorLinkT> competitorList = mapOppCompetitor
					.get(i);
			if (CollectionUtils.isNotEmpty(competitorList)) {
				populateOpportunityCompetitorLink(
						opportunity.getOpportunityId(), competitorList);
			}
			List<NotesT> notes = mapOppNotes.get(i);
			if (CollectionUtils.isNotEmpty(notes)) {
				populateOpportunityNotes(opportunity.getOpportunityId(), notes);
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
		for (List<OpportunityPartnerLinkT> list : mapOpportunityPartnerLink
				.values()) {
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
		for (List<OpportunityCustomerContactLinkT> list : mapCustomerContact
				.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				oppCustContact.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(oppCustContact)) {
			opportunityCustomerContactLinkTRepository.save(oppCustContact);
		}

		List<OpportunityTcsAccountContactLinkT> oppTcsAccContact = new ArrayList<OpportunityTcsAccountContactLinkT>();
		for (List<OpportunityTcsAccountContactLinkT> list : mapTcsContact
				.values()) {
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
		for (NotesT notesT : notes) {
			notesT.setOpportunityId(opportunityId);
		}

	}

	private void populateOpportunityCompetitorLink(String opportunityId,
			List<OpportunityCompetitorLinkT> competitorList) {
		for (OpportunityCompetitorLinkT opportunityCompetitorLinkT : competitorList) {
			opportunityCompetitorLinkT.setOpportunityId(opportunityId);
		}

	}

	private void populateOpportunityTcsAccountContactLink(String opportunityId,
			List<OpportunityTcsAccountContactLinkT> tcsContactList) {
		for (OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT : tcsContactList) {
			opportunityTcsAccountContactLinkT.setOpportunityId(opportunityId);
		}

	}

	private void populateOppCustomerContactLinks(String opportunityId,
			List<OpportunityCustomerContactLinkT> custContactList) {
		for (OpportunityCustomerContactLinkT opportunityCustomerContactLinkT : custContactList) {
			opportunityCustomerContactLinkT.setOpportunityId(opportunityId);
		}

	}

	private void populateOpportunitySubSpLink(String opportunityId,
			List<OpportunitySubSpLinkT> subSpList) {
		for (OpportunitySubSpLinkT opportunitySubSpLinkT : subSpList) {
			opportunitySubSpLinkT.setOpportunityId(opportunityId);
		}

	}

	private void populateOppSalesSupportLink(String opportunityId,
			List<OpportunitySalesSupportLinkT> salesSupportList) {
		for (OpportunitySalesSupportLinkT opportunitySalesSupportLinkT : salesSupportList) {
			opportunitySalesSupportLinkT.setOpportunityId(opportunityId);
		}

	}

	private void populateOpportunityPartnerLink(String opportunityId,
			List<OpportunityPartnerLinkT> oppourtunityPartnerList) {
		for (OpportunityPartnerLinkT opportunityPartnerLinkT : oppourtunityPartnerList) {
			opportunityPartnerLinkT.setOpportunityId(opportunityId);
		}

	}

	private void populateOpportunityOfferingLinks(String opportunityId,
			List<OpportunityOfferingLinkT> offeringList) {
		for (OpportunityOfferingLinkT opportunityOfferingLinkT : offeringList) {
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
	 * This method is used to check whether the owners of opportunity or connect
	 * are BDM or BDM Supervisor
	 * 
	 * @param owners
	 * @return
	 */
	public boolean isOwnersAreBDMorBDMSupervisor(Set<String> owners) {
		boolean isBDMOrBDMSupervisor = false;
		List<String> userGroups = userRepository.findUserGroupByUserIds(owners,
				true);
		if (CollectionUtils.isNotEmpty(userGroups)) {
			for (String userGroup : userGroups) {
				if (userGroup.equals(UserGroup.BDM.getValue())
						|| userGroup
								.equals(UserGroup.BDM_SUPERVISOR.getValue())
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
	 * This method is used to update the opportunity details and also send email
	 * notification if opportunity won or lost
	 * 
	 * @param opportunity
	 * @return AsyncJobRequest
	 * @throws Exception
	 */
	@Transactional
	public List<AsyncJobRequest> updateOpportunityT(OpportunityT opportunity,
			Status status) throws Exception {

		List<AsyncJobRequest> asyncJobRequests = Lists.newArrayList();
		
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
		Integer oldDealValue = opportunityBeforeEdit.getDigitalDealValue();
		String oldDealCurrency = opportunityBeforeEdit.getDealCurrency();
		updateOpportunity(opportunity, opportunityBeforeEdit, status, oldSalesStageCode);
		// If deal value becomes more than 5 million USD or the opportunity won
		// or lost, Email notification to be triggered
		asyncJobRequests.addAll(sendEmailNotification(opportunity, oldSalesStageCode,
				oldDealValue, oldDealCurrency));
				return asyncJobRequests;
	}

	/**
	 * Service method to fetch the opportunity related information based on
	 * search type and the search keyword
	 * 
	 * @param smartSearchType
	 * @param term
	 * @param getAll
	 * @param currency
	 * @param count
	 * @param page
	 * @param userId
	 * @return
	 */
	public PageDTO<SearchResultDTO<OpportunityT>> smartSearch(
			SmartSearchType smartSearchType, String term, boolean getAll,
			List<String> currency, int page, int count, UserT user) {
		logger.info("OpportunityService::smartSearch type {}", smartSearchType);
		PageDTO<SearchResultDTO<OpportunityT>> res = new PageDTO<SearchResultDTO<OpportunityT>>();
		List<SearchResultDTO<OpportunityT>> resList = Lists.newArrayList();
		SearchResultDTO<OpportunityT> searchResultDTO = new SearchResultDTO<OpportunityT>();
		if (smartSearchType != null) {

			switch (smartSearchType) {
			case ALL:
				resList.add(getOpportunityById(term, getAll, user));
				resList.add(getOpportunityByName(term, getAll, user));
				resList.add(getOpportunityByCustomers(term, getAll, user));
				resList.add(getOpportunitySubSps(term, getAll, user));
				resList.add(getOpportunityByOwner(term, getAll, user));
				break;
			case ID:
				searchResultDTO = getOpportunityById(term, getAll, user);
				break;
			case NAME:
				searchResultDTO = getOpportunityByName(term, getAll, user);
				break;
			case CUSTOMER:
				searchResultDTO = getOpportunityByCustomers(term, getAll, user);
				break;
			case SUBSP:
				searchResultDTO = getOpportunitySubSps(term, getAll, user);
				break;
			case PRIMARY_OWNER:
				searchResultDTO = getOpportunityByOwner(term, getAll, user);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid search type");

			}

			if (smartSearchType != SmartSearchType.ALL) {// paginate the result
															// if it is fetching
															// entire record(ie.
															// getAll=true)
				if (getAll) {
					List<OpportunityT> values = searchResultDTO.getValues();
					List<OpportunityT> records = PaginationUtils.paginateList(
							page, count, values);
					if (CollectionUtils.isNotEmpty(records)) {
						prepareOpportunity(records);
						beaconConverterService.convertOpportunityCurrency(
								records, currency);
					}
					searchResultDTO.setValues(records);
					res.setTotalCount(values.size());
				}
				resList.add(searchResultDTO);
			}
		}
		res.setContent(resList);
		return res;
	}

	private SearchResultDTO<OpportunityT> getOpportunityById(String term,
			boolean getAll, UserT user) {
		List<OpportunityT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			records = opportunityRepository.searchDeliveryOpportunitiesById("%"
					+ term + "%", getAll, userIds);
		} else {
			records = opportunityRepository
					.searchById("%" + term + "%", getAll);
		}
		return createSearchResultFrom(records, SmartSearchType.ID, getAll);
	}

	private SearchResultDTO<OpportunityT> getOpportunityByName(String term,
			boolean getAll, UserT user) {
		List<OpportunityT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			records = opportunityRepository.searchDeliveryOpportunitiesByName(
					"%" + term + "%", getAll, userIds);
		} else {
			records = opportunityRepository.searchByName("%" + term + "%",
					getAll);
		}
		return createSearchResultFrom(records, SmartSearchType.NAME, getAll);
	}

	private SearchResultDTO<OpportunityT> getOpportunityByCustomers(
			String term, boolean getAll, UserT user) {
		List<OpportunityT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			records = opportunityRepository
					.searchDeliveryOpportunitiesByCustomerName(
							"%" + term + "%", getAll, userIds);
		} else {
			records = opportunityRepository.searchByCustomerName("%" + term
					+ "%", getAll);
		}
		return createSearchResultFrom(records, SmartSearchType.CUSTOMER, getAll);
	}

	private SearchResultDTO<OpportunityT> getOpportunitySubSps(String term,
			boolean getAll, UserT user) {
		List<OpportunityT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			records = opportunityRepository.searchDeliveryOpportunitiesBySubSp(
					"%" + term + "%", getAll, userIds);
		} else {
			records = opportunityRepository.searchBySubsp("%" + term + "%",
					getAll);
		}
		return createSearchResultFrom(records, SmartSearchType.SUBSP, getAll);
	}

	private SearchResultDTO<OpportunityT> getOpportunityByOwner(String term,
			boolean getAll, UserT user) {
		List<OpportunityT> records = null;
		String userGroup = user.getUserGroup();
		if (userGroup.equals(UserGroup.DELIVERY_CENTRE_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_CLUSTER_HEAD.getValue())
				|| userGroup.equals(UserGroup.DELIVERY_MANAGER.getValue())) {
			List<String> userIds = userRepository
					.getAllSubordinatesIdBySupervisorId(user.getUserId());
			userIds.add(user.getUserId());
			records = opportunityRepository
					.searchDeliveryOpportunitiesByPrimaryOwner(
							"%" + term + "%", getAll, userIds);
		} else {
			records = opportunityRepository.searchByPrimaryOwner("%" + term
					+ "%", getAll);
		}
		return createSearchResultFrom(records, SmartSearchType.PRIMARY_OWNER,
				getAll);
	}

	/**
	 * creates {@link SearchResultDTO} from the list of connects
	 * 
	 * @param records
	 * @param type
	 * @param getAll
	 * @return
	 */
	private SearchResultDTO<OpportunityT> createSearchResultFrom(
			List<OpportunityT> records, SmartSearchType type, boolean getAll) {
		SearchResultDTO<OpportunityT> conRes = new SearchResultDTO<OpportunityT>();
		conRes.setSearchType(type);
		conRes.setValues(records);
		return conRes;
	}

	/**
	 * This method is used to check whether the logged in user has edit access
	 * for an opportunity
	 * 
	 * @param opportunity
	 * @param userGroup
	 * @param userId
	 * @param isUpdate 
	 * @return
	 */
	private boolean isEditAccessRequiredForOpportunity(
			OpportunityT opportunity, String userGroup, String userId, boolean isUpdate) {

		logger.debug("Inside isEditAccessRequiredForOpportunity method");

		boolean isEditAccessRequired = false;
		if (isUserOwner(userId, opportunity)) {
			isEditAccessRequired = true;
		} else {
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
				isEditAccessRequired = false;
				break;
			case BDM_SUPERVISOR:
				isEditAccessRequired = isSubordinateAsOwner(userId,
						opportunity.getOpportunityId(), null);
				break;
			case DELIVERY_CENTRE_HEAD:
			case DELIVERY_CLUSTER_HEAD:
				if (isUpdate) {
					if (opportunity.getDeliveryTeamFlag()) {
						isEditAccessRequired = isSubordinateAsOwner(userId,
								opportunity.getOpportunityId(), null);
					}
				} else {
					isEditAccessRequired = isSubordinateAsOwner(userId,
							opportunity.getOpportunityId(), null);
				}
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

		logger.debug("Is Edit Access Required for Opportunity: " +isEditAccessRequired);

		return isEditAccessRequired;
	}

	// /**
	// * This method is used to check whether any of the subordinate is being
	// one
	// * of the owners of connect or opportunity
	// *
	// * @param userId
	// * @param opportunity
	// * @param connectId
	// * @return
	// */
	// public boolean isDeliveryTeamSubordinateAsOwner(String userId,
	// OpportunityT opportunity) {
	// boolean isSubordinateAsOwner = false;
	// List<String> owners = new ArrayList<String>();
	// List<String> subordinates = userRepository
	// .getAllSubordinatesIdBySupervisorId(userId);
	// if (CollectionUtils.isNotEmpty(subordinates)) {
	// if (!StringUtils.isEmpty(opportunity.getOpportunityId())) {
	// owners =
	// opportunityRepository.getAllOwners(opportunity.getOpportunityId());
	// }
	// if (owners != null) {
	// for (String owner : owners) {
	// if (subordinates.contains(owner) && opportunity.getDeliveryTeamFlag()) {
	// isSubordinateAsOwner = true;
	// break;
	// }
	// }
	// }
	// }
	// return isSubordinateAsOwner;
	// }

	/**
	 * method used to send the email if required for an opportunity
	 * 
	 * @param opportunity
	 * @param oldSalesStageCode
	 * @param oldDealValue
	 * @param oldDealCurrency
	 * @return
	 * @throws Exception
	 */
	private List<AsyncJobRequest> sendEmailNotification(OpportunityT opportunity,
			int oldSalesStageCode, Integer oldDealValue, String oldDealCurrency)
			throws Exception {
		List<AsyncJobRequest> asyncJobRequests = Lists.newArrayList();
		logger.info("Inside sendEmailNotification method");
		boolean emailJobRequired = false;
		String newDealCurrency = opportunity.getDealCurrency();
		Integer newDealValue = opportunity.getDigitalDealValue();
		int newSalesStageCode = opportunity.getSalesStageCode();
		BigDecimal newDealValueInUSD = opportunityDownloadService
				.convertCurrencyToUSD(newDealCurrency, newDealValue);

		BigDecimal oldDealValueInUSD = opportunityDownloadService
				.convertCurrencyToUSD(oldDealCurrency, oldDealValue);

		if ((oldSalesStageCode != SalesStageCode.WIN.getCodeValue() && newSalesStageCode == SalesStageCode.WIN
				.getCodeValue())
				|| (oldSalesStageCode != SalesStageCode.LOST.getCodeValue() && newSalesStageCode == SalesStageCode.LOST
						.getCodeValue())) {
			emailJobRequired = true;
			
			if(newSalesStageCode == SalesStageCode.WIN.getCodeValue()) {
				List<DeliveryIntimatedT> deliveriesIntimated = deliveryIntimatedRepository.findByOpportunityId(opportunity.getOpportunityId());
				if(CollectionUtils.isNotEmpty(deliveriesIntimated)) {
				for (DeliveryIntimatedT deliveryIntimated : deliveriesIntimated) {
				asyncJobRequests.add(constructAsyncJobRequest(deliveryIntimated.getDeliveryIntimatedId(), 
				EntityType.DELIVERY_INTIMATED, JobName.deliveryEmailNotification, null,null));
				}
			}
		}
			
		} else if (newDealValue != null
				&& newSalesStageCode >= SalesStageCode.RFP_SUBMITTED
						.getCodeValue()
				&& newSalesStageCode <= SalesStageCode.CONTRACT_NEGOTIATION
						.getCodeValue()) {
			List<AuditOpportunityDeliveryCentreT> auditOpportunityDeliveryCentreTs = auditOpportunityDeliveryCenterRepository.
					findNewlyAddedDeliveryCentresForOpportunity(opportunity.getOpportunityId());
			if(CollectionUtils.isNotEmpty(auditOpportunityDeliveryCentreTs)) {
				for(AuditOpportunityDeliveryCentreT auditOppDelivery : auditOpportunityDeliveryCentreTs) {
					asyncJobRequests.add(constructAsyncJobRequest(auditOppDelivery.getOpportunityId(), 
							EntityType.OPPORTUNITY, JobName.deliveryEmailNotification, null,auditOppDelivery.getDeliveryCentreId()));
				}
			}
			
			
			if (isAboveOrEqualHighDeal(newDealValueInUSD)
					&& (!isAboveOrEqualHighDeal(oldDealValueInUSD) || oldSalesStageCode != newSalesStageCode)) {
				emailJobRequired = true;
			}
		}

		if (emailJobRequired) {
			asyncJobRequests.add(constructAsyncJobRequest(opportunity.getOpportunityId(), EntityType.OPPORTUNITY, JobName.opportunityEmailNotification, 
					newDealValueInUSD.doubleValue(),null));
		}
		logger.info("email Job Trigger Required For Opportunity {}");
		return asyncJobRequests;

	}

	/**
	 * Method used to check whether the given deal value is greater than or
	 * equal to 5 million
	 * 
	 * @param value
	 * @return
	 */
	private boolean isAboveOrEqualHighDeal(final BigDecimal value) {
		BigDecimal fiveMillion = new BigDecimal(Constants.FIVE_MILLION);
		return (value != null && value.compareTo(fiveMillion) >= 0);
	}
	
	public AsyncJobRequest constructAsyncJobRequest(String entityId, EntityType entityType, 
			JobName jobName, Double dealValue, Integer deliveryCentreId) {
			AsyncJobRequest asyncJobRequest = new AsyncJobRequest();
			asyncJobRequest.setJobName(jobName);
			asyncJobRequest.setEntityType(entityType);
			asyncJobRequest.setEntityId(entityId);
			asyncJobRequest.setOn(Switch.ON);
			asyncJobRequest.setDealValue(dealValue);
			asyncJobRequest.setDeliveryCentreId(deliveryCentreId);
			return asyncJobRequest;
		}
	
	public PageDTO<OpportunityDTO> getOpportunitiesBasedOnPrivileges(Date fromDate, Date toDate, String mapId) throws Exception {
		PageDTO<OpportunityDTO> response = new PageDTO<OpportunityDTO>();
		List<OpportunityT> opportunityTs = Lists.newArrayList();
		UserT currentUser = DestinationUtils.getCurrentUserDetails();
		String userId = currentUser.getUserId();
		String userGroup = currentUser.getUserGroup();
		List<String> owners = Lists.newArrayList();
		owners.add(userId);

		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();

		String oppQueryString = getOpportunityQueryByPrivilege(userId);
		Query oppQuery = entityManager.createNativeQuery(oppQueryString, OpportunityT.class);
		oppQuery.setParameter("fromDate", startDate);
		oppQuery.setParameter("toDate", endDate);
		opportunityTs = oppQuery.getResultList();

		List<OpportunityDTO> dtos = prepareWinRatioResposeDTO(opportunityTs, mapId);
		response.setContent(dtos);
		return response;

	}
	
	private List<OpportunityDTO> prepareWinRatioResposeDTO(
			List<OpportunityT> opportunityTs, String mapId) {
		List<OpportunityDTO> dtos = Lists.newArrayList();
		for (OpportunityT opportunityT : opportunityTs) {
			OpportunityDTO dto = beanMapper.map(opportunityT, OpportunityDTO.class, mapId);
			if(opportunityT.getDigitalDealValue()!=null && opportunityT.getDealCurrency() != null && !Constants.USD.equals(opportunityT.getDealCurrency())) {
				BigDecimal dealValueInUsd = beaconConverterService.convertCurrencyRate(opportunityT.getDealCurrency(), Constants.USD, opportunityT.getDigitalDealValue());
				dto.setDigitalDealValue(dealValueInUsd.intValue());
			}
			dtos.add(dto);
		}
	
		return dtos;
	}

	/**
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private String getOpportunityQueryByPrivilege(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				QueryConstants.OPPORTUNITY_QUERY_PREFIX);
		queryBuffer.append(QueryConstants.OPPORTUNITY_DEAL_CLOSURE_DATE_ORDER_BY);
		return queryBuffer.toString();
	}
	
	
	private void setNullForDeliveryMasterCyclic(OpportunityT opportunity) {
		if(CollectionUtils.isNotEmpty(opportunity.getDeliveryMasterTs())) {
			for (DeliveryMasterT deliveryMasterT : opportunity.getDeliveryMasterTs()) {
				deliveryMasterT.setDeliveryIntimatedT(null);
				deliveryMasterT.setDeliveryIntimatedId(null);
				deliveryMasterT.getDeliveryCentreT().setDeliveryMasterTs(null);
				deliveryMasterT.getDeliveryCentreT().getDeliveryClusterT().setDeliveryCentreTs(null);
				deliveryMasterT.getDeliveryCentreT().setDeliveryIntimatedCentreLinkTs(null);
				deliveryMasterT.getDeliveryStageMappingT().setDeliveryMasterTs(null);
			}
		}
	}

	public OpportunityDTO findById(String oppId, String mapId) throws Exception {
		OpportunityT opportunity = findByOpportunityId(oppId, Lists.newArrayList(Constants.USD));
		OpportunityDTO dto = beanMapper.map(opportunity, OpportunityDTO.class, mapId);
		return dto;
	}

	/**
	 * @param fromDate
	 * @param toDate
	 * @param grpCustomer
	 * @param mapId
	 * @param page
	 * @param count
	 * @return
	 */
	public PageDTO<OpportunityDTO> getAllByGrpCustomer(Date fromDate, Date toDate, String grpCustomer, List<Integer> stages,
			String mapId, int page, int count) {

		Sort sort = new Sort(Direction.DESC, "dealClosureDate");
		Pageable pageable = new PageRequest(page, count, sort);
		
		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();

		Page<OpportunityT> oppTs = opportunityRepository.findByGrpCustomerAndDealDate(startDate, endDate, grpCustomer, stages, pageable);

		List<OpportunityDTO> dtos = Lists.newArrayList();
		List<OpportunityT> oppList = oppTs.getContent();
		if(CollectionUtils.isNotEmpty(oppList)) {
			prepareOpportunity(oppList);
			for (OpportunityT opportunityT : oppList) {
				OpportunityDTO dto = beanMapper.map(opportunityT, OpportunityDTO.class, mapId);
				if(opportunityT.getDigitalDealValue() != null && opportunityT.getDealCurrency() != null && !Constants.USD.equals(opportunityT.getDealCurrency())) {
					BigDecimal dealValueInUsd = beaconConverterService.convertCurrencyRate(opportunityT.getDealCurrency(), Constants.USD, opportunityT.getDigitalDealValue());
					dto.setDigitalDealValue(dealValueInUsd.intValue());
				}
				dtos.add(dto);
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Opportunities not found");
		}

		return new PageDTO<OpportunityDTO>(dtos, (int)oppTs.getTotalElements());
	}

	@SuppressWarnings("unchecked")
	public PageDTO<OpportunityDTO> getAllByParam(List<Integer> stages, String oppType, String dispGeo, String category,
			String searchTerm, Date fromDate, Date toDate, String mapId, int page, int count) {

		Sort sort = new Sort(Direction.DESC, "modifiedDatetime");
		Pageable pageable = new PageRequest(page, count, sort);
		
		Date startDate = fromDate != null ? fromDate : DateUtils.getFinancialYrStartDate();
		Date endDate = toDate != null ? toDate : new Date();
		
		//opp ids by category
		List<String> oppIds = null;
		if("QUALIFIED".equals(category)) {
			if(CollectionUtils.isEmpty(stages)) {
				stages = Lists.newArrayList(0,1,2,3,4,5,6,7,8);
			}
			oppIds = opportunityRepository.getOppIdsByStage(stages);
		} else if("BID_SUBMITTED".equals(category)) {
			if(CollectionUtils.isEmpty(stages)) {
				stages = Lists.newArrayList(5,6,7,8,9,10,12);
			}
			oppIds = opportunityRepository.getOppIdsByStageAndBidDate(stages, startDate, endDate);
		} else if("REQUEST_RECIEVED".equals(category)) {
			if(CollectionUtils.isEmpty(stages)) {
				stages = Lists.newArrayList(0,1,2,3,4,5,6,7,8,9,10,11,12,13);
			}
			oppIds = opportunityRepository.getOppIdsReqDate(stages, startDate, endDate);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid category");
		}
		
		//apply geography filter
		if(!StringUtils.equals(dispGeo, "ALL")) {
			List<String> oppIdsByGeo = opportunityRepository.getOppIdsByGeo(dispGeo);
			oppIds = (List<String>) CollectionUtils.intersection(oppIds, oppIdsByGeo);
		}

		//apply user grroup filter
		if(!StringUtils.equals(oppType, "ALL")) {
			List<String> userGroups = null;
			if(StringUtils.equals(oppType, "SALES")) {
				userGroups = DestinationUtils.getSalesUserGroups();
			} else if(StringUtils.equals(oppType, "CONSULTING")) {
				userGroups = DestinationUtils.getConsultingUserGroups();
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid opportunity type");
			}
			
			List<String> oppIdsByGroup = opportunityRepository.getOppIdsByUserGroup(userGroups);
			oppIds = (List<String>) CollectionUtils.intersection(oppIds, oppIdsByGroup);
		}
		
		if(CollectionUtils.isEmpty(oppIds)) {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Opportunities not found in this criteria");
		}
		
		Page<OpportunityT> opportunities = opportunityRepository.findByOppNameAndIdsIn("%"+searchTerm+"%", oppIds, pageable);
		List<OpportunityDTO> oppDtos = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(opportunities.getContent())) {
			for (OpportunityT opportunityT : opportunities.getContent()) {
				OpportunityDTO dto = beanMapper.map(opportunityT, OpportunityDTO.class, mapId);
				if(opportunityT.getDigitalDealValue() != null && opportunityT.getDealCurrency() != null && !Constants.USD.equals(opportunityT.getDealCurrency())) {
					BigDecimal dealValueInUsd = beaconConverterService.convertCurrencyRate(opportunityT.getDealCurrency(), Constants.USD, opportunityT.getDigitalDealValue());
					dto.setDigitalDealValue(dealValueInUsd.intValue());
				}
				oppDtos.add(dto);
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Opportunities not found in this criteria");
		}
		
		return new PageDTO<OpportunityDTO>(oppDtos, opportunities.getTotalElements());
	}
}
