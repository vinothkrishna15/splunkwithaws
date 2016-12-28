package com.tcs.destination.service;

import static com.tcs.destination.utils.ErrorConstants.ERR_INAC_01;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.CityMapping;
import com.tcs.destination.bean.CommentsT;
import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectNameKeywordSearch;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ContentDTO;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DashBoardConnectsResponse;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.bean.UserTaggedFollowedT;
import com.tcs.destination.bean.dto.ConnectDTO;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.CityMappingRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.CommentsTRepository;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectOpportunityLinkTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ConnectTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CountryRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.data.repository.NotesTRepository;
import com.tcs.destination.data.repository.NotificationEventGroupMappingTRepository;
import com.tcs.destination.data.repository.NotificationsEventFieldsTRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.PartnerSubSpMappingTRepository;
import com.tcs.destination.data.repository.PartnerSubSpProductMappingTRepository;
import com.tcs.destination.data.repository.ProductContactLinkTRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TaskRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsConditionRepository;
import com.tcs.destination.data.repository.UserNotificationSettingsRepository;
import com.tcs.destination.data.repository.UserNotificationsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.data.repository.UserTaggedFollowedRepository;
import com.tcs.destination.enums.ConnectStatusType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.OwnerType;
import com.tcs.destination.enums.PrivilegeType;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.AutoCommentsHelper;
import com.tcs.destination.helper.AutoCommentsLazyLoader;
import com.tcs.destination.helper.NotificationHelper;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.PropertyUtil;

@Service("connectService")
public class ConnectService {

	private static final int ONE_DAY_IN_MILLIS = 86400000;

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectService.class);

	// Required for auto comments
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ConnectSecondaryOwnerRepository connectSecondaryOwnerRepository;

	@Autowired
	ConnectSubSpLinkRepository connSubSpRepo;

	@Autowired
	ConnectOfferingLinkRepository connOffLinkRepo;

	@Autowired
	DocumentRepository docRepo;

	@Autowired
	SearchKeywordsRepository searchKeywordsRepository;

	@Autowired
	ConnectCustomerContactLinkTRepository connCustContRepo;

	@Autowired
	ConnectTcsAccountContactLinkTRepository connTcsAcctContRepo;

	@Autowired
	ConnectSecondaryOwnerRepository connSecOwnerRepo;

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	UserTaggedFollowedRepository userTaggedFollowedRepository;

	@Autowired
	CommentsTRepository commentsTRepository;

	@Autowired
	OpportunityService opportunityService;

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
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

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
	PartnerRepository partnerRepository;

	@Autowired
	ConnectOpportunityLinkTRepository connectOpportunityLinkTRepository;

	@Autowired
	NotesTRepository notesRepository;

	@Autowired
	CityMappingRepository cityMappingRepository;

	@Autowired
	ConnectOfferingLinkRepository connectOfferingLinkRepository;

	@Autowired
	ConnectSubSpLinkRepository connectSubSpLinkRepository;

	@Autowired
	ConnectCustomerContactLinkTRepository connectCustomerContactLinkTRepository;

	@Autowired
	ConnectTcsAccountContactLinkTRepository connectTcsAccountContactLinkTRepository;

	@Autowired
	NotificationEventGroupMappingTRepository notificationEventGroupMappingTRepository;

	@Autowired
	CollaborationCommentsService collaborationCommentsService;

	@Autowired
	UserNotificationSettingsConditionRepository userNotificationSettingsConditionRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	OfferingRepository offeringRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	PartnerSubSpMappingTRepository partnerSubSpMappingRepository;

	@Autowired
	PartnerSubSpProductMappingTRepository partnerSubSpProductMappingRepository;

	@Autowired
	ProductContactLinkTRepository productContactLinkTRepository;
	
	@Autowired
	private DozerBeanMapper beanMapper;

	public ConnectT findConnectById(String connectId) throws Exception {
		logger.debug("Inside findConnectById() service");
		ConnectT connectT = connectRepository.findByConnectId(connectId);
		if (connectT != null) {
			String userId = DestinationUtils.getCurrentUserId();
			String userGroup = userRepository.findByUserId(userId)
					.getUserGroup();
			prepareConnect(connectT, userId, userGroup);
		} else {
			logger.error("NOT_FOUND: Connect not found: {}", connectId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connect not found: " + connectId);
		}
		return connectT;
	}

	private void setSearchKeywordTs(ConnectT connect) {
		logger.debug("Inside setSearchKeywordTs() method");
		// Add Search Keywords
		List<SearchKeywordsT> searchKeywords = searchKeywordsRepository
				.findByEntityTypeAndEntityId(EntityType.CONNECT.toString(),
						connect.getConnectId());
		if (searchKeywords != null && searchKeywords.size() > 0) {
			connect.setSearchKeywordsTs(searchKeywords);
		}
	}

	public PaginatedResponse searchforConnectsByNameContaining(String name,
			String customerId, int page, int count) throws Exception {
		logger.debug("Inside searchforConnectsByNameContaining() service");
		Pageable pageable = new PageRequest(page, count);
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		List<ConnectT> connectList = null;
		if (customerId.isEmpty()) {
			Page<ConnectT> connectPage = connectRepository
					.findByConnectNameIgnoreCaseLikeOrderByModifiedDatetimeDesc(
							"%" + name + "%", pageable);
			paginatedResponse.setTotalCount(connectPage.getTotalElements());
			connectList = connectPage.getContent();
		} else {
			Page<ConnectT> connectPage = connectRepository
					.findByConnectNameIgnoreCaseLikeAndCustomerIdOrderByModifiedDatetimeDesc(
							"%" + name + "%", customerId, pageable);
			paginatedResponse.setTotalCount(connectPage.getTotalElements());
			connectList = connectPage.getContent();
		}

		if (connectList.isEmpty()) {
			logger.error(
					"NOT_FOUND: Connects not found with the given name: {}",
					name);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connects not found with the given name: " + name);
		}
		prepareConnect(connectList);
		paginatedResponse.setConnectTs(connectList);
		return paginatedResponse;
	}

	public DashBoardConnectsResponse searchDateRangwWithWeekAndMonthCount(
			Date fromDate, Date toDate, String userId, String owner,
			String customerId, String partnerId, Date weekStartDate,
			Date weekEndDate, Date monthStartDate, Date monthEndDate, int page,
			int count, String connectName) throws Exception {
		logger.debug("Inside searchDateRangwWithWeekAndMonthCount() service");
		DashBoardConnectsResponse response = new DashBoardConnectsResponse();
		response.setPaginatedConnectResponse(searchforConnectsBetweenForUserOrCustomerOrPartner(
				fromDate, toDate, userId, owner, customerId, partnerId, page,
				count));
		if (weekStartDate.getTime() != weekEndDate.getTime()) {
			logger.debug("WeekStartDate and WeekEndDate Time are Not Equal");
			long totalCount = searchforConnectsBetweenForUserOrCustomerOrPartner(
					weekStartDate, weekEndDate, userId, owner, customerId,
					partnerId, page, count).getTotalCount();
			int weekCount = (int) totalCount;
			response.setWeekCount(weekCount);
		}
		if (monthStartDate.getTime() != monthEndDate.getTime()) {
			logger.debug("MonthStartDate  and MonthEndDate are Not Equal");
			long totalCount = searchforConnectsBetweenForUserOrCustomerOrPartner(
					monthStartDate, monthEndDate, userId, owner, customerId,
					partnerId, page, count).getTotalCount();
			int monthCount = (int) totalCount;
			response.setMonthCount(monthCount);
		}
		validateDashboardConnectResponse(response,
				new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), response
				.getPaginatedConnectResponse().getConnectTs(),
				new Timestamp(weekStartDate.getTime()), new Timestamp(
						weekEndDate.getTime()),
						new Timestamp(monthStartDate.getTime()), new Timestamp(
								monthEndDate.getTime()));
		return response;
	}

	public PaginatedResponse searchforConnectsBetweenForUserOrCustomerOrPartner(
			Date fromDate, Date toDate, String userId, String owner,
			String customerId, String partnerId, int page, int count)
					throws Exception {
		logger.debug("Inside searchforConnectsBetweenForUserOrCustomerOrPartner() service");
		PaginatedResponse connectResponse = new PaginatedResponse();
		Timestamp toTimestamp = new Timestamp(toDate.getTime()
				+ ONE_DAY_IN_MILLIS - 1);
		if (OwnerType.contains(owner)) {
			logger.debug("Owner Type Contains owner");
			List<ConnectT> connects = new ArrayList<ConnectT>();
			if (owner.equalsIgnoreCase(OwnerType.PRIMARY.toString())) {
				logger.debug("Owner is PRIMARY");
				// Exclude the toDate from the date range
				connects = connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								toTimestamp, customerId, partnerId);
				connectResponse.setTotalCount(connects.size());
				connects = paginateConnects(page, count, connects);

				connectResponse.setConnectTs(connects);

			} else if (owner.equalsIgnoreCase(OwnerType.SECONDARY.toString())) {
				logger.debug("Owner is SECONDARY");
				connects = connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								toTimestamp, customerId, partnerId);
				connectResponse.setTotalCount(connects.size());
				connects = paginateConnects(page, count, connects);

				connectResponse.setConnectTs(connects);

			} else if (owner.equalsIgnoreCase(OwnerType.ALL.toString())) {
				logger.debug("Owner is ALL");
				connects = connectRepository
						.findForAllOwnersStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								toTimestamp, customerId, partnerId);
				connectResponse.setTotalCount(connects.size());
				connects = paginateConnects(page, count, connects);
				connectResponse.setConnectTs(connects);

			}

			prepareConnect(connects);
			return connectResponse;
		}

		logger.error("BAD_REQUEST: Invalid Owner Type: {}", owner);
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Invalid Owner Type: " + owner);

	}

	private List<ConnectT> paginateConnects(int page, int count,
			List<ConnectT> connects) {
		if (PaginationUtils.isValidPagination(page, count, connects.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					connects.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					connects.size()) + 1;
			connects = connects.subList(fromIndex, toIndex);
			logger.debug("ConnectT  after pagination size is "
					+ connects.size());
		} else {
			connects = null;
		}
		return connects;
	}

	@Transactional
	public boolean createConnect(ConnectT connect, boolean isBulkDataLoad)
			throws Exception {
		logger.debug("Inside insertConnect() service");
		connect.setCreatedBy(DestinationUtils.getCurrentUserDetails()
				.getUserId());
		connect.setModifiedBy(DestinationUtils.getCurrentUserDetails()
				.getUserId());
		// Validate request
		validateRequest(connect, true, null);
		// Take a copy to keep child objects
		ConnectT requestConnect = (ConnectT) DestinationUtils.copy(connect);
		logger.debug("Copied connect object.");
		// Set null for all child objects
		setNullForReferencedObjects(connect);
		logger.debug("Reference Objects set null");

		if (connectRepository.save(connect) != null) {
			requestConnect.setConnectId(connect.getConnectId());
			logger.debug("Parent Object Saved, ConnectId: {}",
					connect.getConnectId());
			// Re-attach request connect
			connect = requestConnect;
			String categoryUpperCase = connect.getConnectCategory()
					.toUpperCase();
			connect.setConnectCategory(categoryUpperCase);

			String connectId = connect.getConnectId();
			String customerId = connect.getCustomerId();
			String partnerId = connect.getPartnerId();

			List<NotesT> noteList = connect.getNotesTs();
			if (noteList != null)
				populateNotes(customerId, partnerId, categoryUpperCase,
						connectId, noteList);
			logger.debug("Notes Populated ");

			List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
					.getConnectCustomerContactLinkTs();
			if (conCustConLinkTList != null) {
				populateConnectCustomerContactLinks(connect,
						conCustConLinkTList);
				logger.debug("ConnectCustomerContact Populated ");
			} else {
				logger.error("BAD_REQUEST: Connect Customer Contact is required");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Connect Customer Contact is required");
			}

			List<ConnectOfferingLinkT> conOffLinkTList = connect
					.getConnectOfferingLinkTs();
			if (conOffLinkTList != null) {
				populateConnectOfferingLinks(connectId, conOffLinkTList);
				logger.debug("ConnectOffering Populated ");
			}

			List<ConnectSubSpLinkT> conSubSpLinkTList = connect
					.getConnectSubSpLinkTs();
			if (conSubSpLinkTList != null) {
				populateConnectSubSpLinks(connectId, conSubSpLinkTList);
				logger.debug("ConnectSubSp Populated ");
			}

			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
					.getConnectSecondaryOwnerLinkTs();
			if (conSecOwnLinkTList != null) {
				populateConnectSecondaryOwnerLinks(connectId,
						conSecOwnLinkTList);
				logger.debug("ConnectSecondaryOwner Populated ");
			}

			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
					.getConnectTcsAccountContactLinkTs();
			if (conTcsAccConLinkTList != null) {
				populateConnectTcsAccountContactLinks(connectId,
						conTcsAccConLinkTList);
				logger.debug("ConnectTcsAccountContact Populated ");
			}

			// Save Search Keywords
			if (connect.getSearchKeywordsTs() != null) {
				for (SearchKeywordsT searchKeywordT : connect
						.getSearchKeywordsTs()) {
					searchKeywordT.setEntityType(EntityType.CONNECT.toString());
					searchKeywordT.setEntityId(connect.getConnectId());
					searchKeywordT.setCreatedModifiedBy(DestinationUtils
							.getCurrentUserDetails().getUserId());
					searchKeywordsRepository.save(searchKeywordT);
				}
			}

			if (connectRepository.save(connect) != null) {
				logger.info("Connect has been added successfully");
				if (!isBulkDataLoad) {
					//					// Invoke Asynchronous Auto Comments Thread
					processAutoComments(connect.getConnectId(), null);
					//					// Invoke Asynchronous Notifications Thread
					//					processNotifications(connect.getConnectId(), null);
				}
				return true;
			}

		}
		logger.debug("Connect not Saved");
		return false;
	}

	private void processNotifications(String connectId, Object oldObject) {
		logger.debug("Calling processNotifications() method");
		NotificationHelper notificationsHelper = new NotificationHelper();
		notificationsHelper.setEntityId(connectId);
		notificationsHelper.setEntityType(EntityType.CONNECT.name());
		if (oldObject != null) {
			notificationsHelper.setOldObject(oldObject);
		}
		notificationsHelper
		.setNotificationsEventFieldsTRepository(notificationEventFieldsTRepository);
		notificationsHelper
		.setUserNotificationsTRepository(userNotificationsTRepository);
		notificationsHelper
		.setUserNotificationSettingsRepo(userNotificationSettingsRepo);
		notificationsHelper
		.setNotificationEventGroupMappingTRepository(notificationEventGroupMappingTRepository);
		notificationsHelper.setCrudRepository(connectRepository);
		notificationsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		notificationsHelper
		.setUserNotificationSettingsConditionsRepository(userNotificationSettingsConditionRepository);
		notificationsHelper
		.setSearchKeywordsRepository(searchKeywordsRepository);
		notificationsHelper
		.setAutoCommentsEntityTRepository(autoCommentsEntityTRepository);
		// Invoking Auto Comments Task Executor Thread
		notificationsTaskExecutor.execute(notificationsHelper);
	}

	private void validateRequest(ConnectT connect, boolean isInsert,
			ConnectT connectBeforeEdit) throws Exception {
		logger.debug("Inside validateRequest() method");
		String connectCategory = connect.getConnectCategory();

		if (connectCategory == null || connectCategory.trim().isEmpty()) {
			logger.error("BAD_REQUEST: Connect Category is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Connect Category is required");
		}
		String customerId = connect.getCustomerId();
		String partnerId = connect.getPartnerId();
		boolean isValid = false;
		if (EntityType.contains(connectCategory)) {
			switch (EntityType.valueOf(connectCategory)) {
			case CUSTOMER:
				if (customerId != null && !customerId.trim().isEmpty())
					isValid = true;
				if (partnerId != null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Invalid Request - Partner Id set for category : "
									+ connectCategory);
				}
				break;
			case PARTNER:
				if (partnerId != null && !partnerId.trim().isEmpty())
					isValid = true;
				if (customerId != null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Invalid Request - Customer Id set for category : "
									+ connectCategory);
				}
				break;
			default:
				logger.error("BAD_REQUEST: Invalid Connect Category: {}",
						connectCategory);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Connect Category: " + connectCategory);
			}
		} else {
			logger.error("BAD_REQUEST: Invalid Connect Category: {}",
					connectCategory);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Connect Category: " + connectCategory);
		}

		if (!isValid) {
			logger.error("BAD_REQUEST: CustomerId / PartnetId is required");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"CustomerId / PartnetId is required");
		}

		if (isInsert && connect.getCreatedBy() == null) {
			logger.error("BAD_REQUEST: CreatedBy is requried");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"CreatedBy is required");
		}

		if (connect.getModifiedBy() == null) {
			logger.error("BAD_REQUEST: ModifiedBy is requried");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"ModifiedBy is requried");
		}
		String userId = DestinationUtils.getCurrentUserId();
		UserT user = userRepository.findByUserId(userId);
		String userGroup = user.getUserGroup();
		if (UserGroup.contains(userGroup)) {
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case PRACTICE_HEAD:
			case PRACTICE_OWNER:
				Set<String> owners = new HashSet<String>();
				owners.add(connect.getPrimaryOwner());
				if (connect.getConnectSecondaryOwnerLinkTs() != null
						&& !connect.getConnectSecondaryOwnerLinkTs().isEmpty()) {
					for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
							.getConnectSecondaryOwnerLinkTs()) {
						owners.add(connectSecondaryOwnerLinkT
								.getSecondaryOwner());
					}
				}
				if (connectBeforeEdit != null) {
					owners.addAll(connectRepository
							.findOwnersOfConnect(connectBeforeEdit
									.getConnectId()));
				}
				if (connect.getDeleteConnectSecondaryOwnerLinkTs() != null
						&& connect.getDeleteConnectSecondaryOwnerLinkTs()
						.size() > 0) {
					for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
							.getDeleteConnectSecondaryOwnerLinkTs()) {
						if (!connect
								.getPrimaryOwner()
								.equals(connectSecondaryOwnerRepository
										.findSecondaryOwner(connectSecondaryOwnerLinkT
												.getConnectSecondaryOwnerLinkId()))) {
							owners.remove(connectSecondaryOwnerRepository
									.findSecondaryOwner(connectSecondaryOwnerLinkT
											.getConnectSecondaryOwnerLinkId()));
						}

					}
				}
				if (owners != null) {
					if (!isOwnersAreBDMorBDMSupervisorOrGeoHead(owners)) {
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								PropertyUtil.getProperty(ERR_INAC_01));
					}
				}
				break;
			default:
				break;
			}
		}

		validateInactiveIndicators(connect);
		validateAndUpdateCityMapping(connect);
	}

	private void validateAndUpdateCityMapping(ConnectT connect)
			throws DestinationException {
		String location = connect.getLocation();
		// validate only if the location info is set
		// To remove the mandatory constraint for location and its co-ordinates
		// while Location API doesn't return value
		if (!com.tcs.destination.utils.StringUtils.isEmpty(location)) {
			CityMapping cityMapping = connect.getCityMapping();
			if (cityMapping != null) {
				String city = cityMapping.getCity();
				if (!city.equalsIgnoreCase(location)) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Location mismatch with city Mapping");
				}
				CityMapping cityMappingDB = cityMappingRepository.findOne(city);

				if (cityMappingDB == null) {
					String latitude = cityMapping.getLatitude();
					if (StringUtils.isEmpty(latitude)) {
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"latitude is required");
					}
					String longitude = cityMapping.getLongitude();
					if (StringUtils.isEmpty(longitude)) {
						throw new DestinationException(HttpStatus.BAD_REQUEST,
								"longitude is required");
					}

					cityMappingRepository.save(cityMapping);
				}
			}
		}
	}

	private void populateConnectTcsAccountContactLinks(String connectId,
			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList)
					throws Exception {
		logger.debug("Inside populateConnectTcsAccountContactLinks() method");
		for (ConnectTcsAccountContactLinkT conTcsAccConLink : conTcsAccConLinkTList) {
			// conTcsAccConLink.setCreatedModifiedBy(currentUserId);
			conTcsAccConLink.setConnectId(connectId);
			conTcsAccConLink.setCreatedBy(DestinationUtils
					.getCurrentUserDetails().getUserId());
			conTcsAccConLink.setModifiedBy(DestinationUtils
					.getCurrentUserDetails().getUserId());
		}

	}
	
	private void populateConnectTcsAccountContactBatch(String connectId,
			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList)
					throws Exception {
		logger.debug("Inside populateConnectTcsAccountContactLinks() method");
		for (ConnectTcsAccountContactLinkT conTcsAccConLink : conTcsAccConLinkTList) {
			// conTcsAccConLink.setCreatedModifiedBy(currentUserId);
			conTcsAccConLink.setConnectId(connectId);
		}

	}

	private void populateConnectSecondaryOwnerLinks(String connectId,
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList) {
		logger.debug("Inside populateConnectSecondaryOwnerLinks() method");
		for (ConnectSecondaryOwnerLinkT conSecOwnLink : conSecOwnLinkTList) {
			// conSecOwnLink.setCreatedModifiedBy(currentUserId);
			conSecOwnLink.setConnectId(connectId);
			conSecOwnLink.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			conSecOwnLink.setModifiedBy(DestinationUtils
					.getCurrentUserDetails().getUserId());
		}

	}
	
	private void populateConnectSecondaryOwnerBatch(String connectId,
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList) {
		logger.debug("Inside populateConnectSecondaryOwnerLinks() method");
		for (ConnectSecondaryOwnerLinkT conSecOwnLink : conSecOwnLinkTList) {
			// conSecOwnLink.setCreatedModifiedBy(currentUserId);
			conSecOwnLink.setConnectId(connectId);
			
		}

	}

	private void populateConnectSubSpLinks(String connectId,
			List<ConnectSubSpLinkT> conSubSpLinkTList) {
		logger.debug("Inside populateConnectSubSpLinks() method");
		for (ConnectSubSpLinkT conSubSpLink : conSubSpLinkTList) {
			conSubSpLink.setConnectId(connectId);
			conSubSpLink.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			conSubSpLink.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			// conSubSpLink.setCreatedModifiedBy(currentUserId);
		}

	}
	
	private void populateConnectSubSpBatch(String connectId,
			List<ConnectSubSpLinkT> conSubSpLinkTList) {
		logger.debug("Inside populateConnectSubSpLinks() method");
		for (ConnectSubSpLinkT conSubSpLink : conSubSpLinkTList) {
			conSubSpLink.setConnectId(connectId);
			
			// conSubSpLink.setCreatedModifiedBy(currentUserId);
		}

	}

	private void populateConnectOfferingLinks(String connectId,
			List<ConnectOfferingLinkT> conOffLinkTList) {
		logger.debug("Inside populateConnectOfferingLinks() method");
		for (ConnectOfferingLinkT conOffLink : conOffLinkTList) {
			// conOffLink.setCreatedModifiedBy(currentUserId);
			conOffLink.setConnectId(connectId);
			conOffLink.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			conOffLink.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
		}

	}
	
	private void populateConnectOfferingBatch(String connectId,
			List<ConnectOfferingLinkT> conOffLinkTList) {
		logger.debug("Inside populateConnectOfferingLinks() method");
		for (ConnectOfferingLinkT conOffLink : conOffLinkTList) {
			// conOffLink.setCreatedModifiedBy(currentUserId);
			conOffLink.setConnectId(connectId);
		}

	}

	private void populateConnectCustomerContactLinks(ConnectT connect,
			List<ConnectCustomerContactLinkT> conCustConLinkTList) {
		logger.debug("Inside populateConnectCustomerContactLinks() method");
		for (ConnectCustomerContactLinkT conCustConLink : conCustConLinkTList) {
			// conCustConLink.setCreatedModifiedBy(currentUserId);
			conCustConLink.setConnectId(connect.getConnectId());
			conCustConLink.setCreatedBy(DestinationUtils
					.getCurrentUserDetails().getUserId());
			conCustConLink.setModifiedBy(DestinationUtils
					.getCurrentUserDetails().getUserId());
		}
	}
	
	private void populateConnectCustomerContactBatch(ConnectT connect,
			List<ConnectCustomerContactLinkT> conCustConLinkTList) {
		logger.debug("Inside populateConnectCustomerContactLinks() method");
		for (ConnectCustomerContactLinkT conCustConLink : conCustConLinkTList) {
			// conCustConLink.setCreatedModifiedBy(currentUserId);
			conCustConLink.setConnectId(connect.getConnectId());
		}
	}


	private void populateNotes(String customerId, String partnerId,
			String categoryUpperCase, String connectId, List<NotesT> noteList) {
		logger.debug("Inside populateNotes() method");
		for (NotesT note : noteList) {
			note.setEntityType(EntityType.CONNECT.name());
			note.setConnectId(connectId);
			note.setUserUpdated(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			if (categoryUpperCase.equalsIgnoreCase(EntityType.CUSTOMER.name())) {
				logger.debug("Category is CUSTOMER");
				CustomerMasterT customer = new CustomerMasterT();
				customer.setCustomerId(customerId);
				customer.setCreatedModifiedBy(DestinationUtils
						.getCurrentUserDetails().getUserId());
				note.setCustomerMasterT(customer);
			} else if (categoryUpperCase.equalsIgnoreCase(EntityType.PARTNER
					.name())) {
				logger.debug("Category is not CUSTOMER");
				PartnerMasterT partner = new PartnerMasterT();
				partner.setPartnerId(partnerId);
				note.setPartnerMasterT(partner);
			}
		}

	}

	// This method is used to set null for child objects
	private void setNullForReferencedObjects(ConnectT connect) {
		logger.debug("Inside setNullForReferencedObjects() method");
		connect.setCollaborationCommentTs(null);
		connect.setConnectCustomerContactLinkTs(null);
		connect.setConnectOfferingLinkTs(null);
		connect.setConnectOpportunityLinkIdTs(null);
		connect.setConnectSecondaryOwnerLinkTs(null);
		connect.setConnectSubSpLinkTs(null);
		connect.setConnectTcsAccountContactLinkTs(null);
		connect.setCustomerMasterT(null);
		connect.setDocumentRepositoryTs(null);
		connect.setGeographyCountryMappingT(null);
		connect.setNotesTs(null);
		connect.setPartnerMasterT(null);
		connect.setUserFavoritesTs(null);
		connect.setUserNotificationsTs(null);
		connect.setPrimaryOwnerUser(null);
	}

	@Transactional
	public boolean updateConnect(ConnectT connect) throws Exception {
		logger.debug("Inside updateConnect() service");
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		connect.setCreatedBy(DestinationUtils.getCurrentUserDetails()
				.getUserId());
		connect.setModifiedBy(DestinationUtils.getCurrentUserDetails()
				.getUserId());
		String connectId = connect.getConnectId();
		if (connectId == null) {
			logger.error("BAD_REQUEST: ConnectId is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"ConnectId is required for update");
		}
		// Check if connect exists
		if (!connectRepository.exists(connectId)) {
			logger.error("NOT_FOUND: Connect not found for update: {}",
					connectId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connect not found for update: " + connectId);
		}
		// Load db object before update with lazy collections populated for auto
		// comments
		// for edit access
		UserT user = userRepository.findByUserId(userId);
		String userGroup = user.getUserGroup();
		ConnectT connectBeforeEdit = connectRepository.findOne(connectId);

		if (!isEditAccessRequiredForConnect(connectBeforeEdit, userGroup,
				userId)) {
			throw new DestinationException(HttpStatus.FORBIDDEN,
					"User is not authorized to edit this Connect");
		}
		ConnectT beforeConnect = loadDbConnectWithLazyCollections(connectId);
		// Copy the db object as the above object is managed by current
		// hibernate session
		ConnectT oldObject = (ConnectT) DestinationUtils.copy(beforeConnect);

		// Update database
		ConnectT afterConnect = editConnect(connect, connectBeforeEdit);

		if (afterConnect != null) {
			logger.info("Connect has been updated successfully: " + connectId);
			//			// Invoke Asynchronous Auto Comments Thread
			processAutoComments(connectId, oldObject);
			//			// Invoke Asynchronous Notifications Thread
			//			processNotifications(connectId, oldObject);
			return true;
		}
		return false;
	}

	// This method is used to load database object with auto comments eligible
	// lazy collections populated
	public ConnectT loadDbConnectWithLazyCollections(String connectId)
			throws Exception {
		logger.debug("Inside loadDbConnectWithLazyCollections() method");
		ConnectT connect = (ConnectT) AutoCommentsLazyLoader
				.loadLazyCollections(connectId, EntityType.CONNECT.name(),
						connectRepository, autoCommentsEntityTRepository,
						autoCommentsEntityFieldsTRepository, null);
		return connect;
	}

	public ConnectT editConnect(ConnectT connect, ConnectT connectBeforeEdit)
			throws Exception {
		logger.debug("inside editConnect() method");

		// Validate request
		validateRequest(connect, false, connectBeforeEdit);

		String categoryUpperCase = connect.getConnectCategory().toUpperCase();
		connect.setConnectCategory(categoryUpperCase);
		String connectId = connect.getConnectId();
		logger.debug("Connect Id : " + connectId);

		String customerId = connect.getCustomerId();
		String partnerId = connect.getPartnerId();

		List<NotesT> noteList = connect.getNotesTs();
		if (noteList != null)
			populateNotes(customerId, partnerId, categoryUpperCase, connectId,
					noteList);
		logger.debug("Notes Populated");

		List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
				.getConnectCustomerContactLinkTs();
		if (conCustConLinkTList != null)
			populateConnectCustomerContactLinks(connect, conCustConLinkTList);
		logger.debug("ConnectCustomerContact Populated");

		List<ConnectOfferingLinkT> conOffLinkTList = connect
				.getConnectOfferingLinkTs();
		if (conOffLinkTList != null)
			populateConnectOfferingLinks(connectId, conOffLinkTList);
		logger.debug("ConnectOffering Populated");

		List<ConnectSubSpLinkT> conSubSpLinkTList = connect
				.getConnectSubSpLinkTs();
		if (conSubSpLinkTList != null)
			populateConnectSubSpLinks(connectId, conSubSpLinkTList);
		logger.debug("ConnectSubSp Populated");

		List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
				.getConnectSecondaryOwnerLinkTs();
		if (conSecOwnLinkTList != null)
			populateConnectSecondaryOwnerLinks(connectId, conSecOwnLinkTList);
		logger.debug("ConnectSecondaryOwner Populated");

		List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
				.getConnectTcsAccountContactLinkTs();
		if (conTcsAccConLinkTList != null)
			populateConnectTcsAccountContactLinks(connectId,
					conTcsAccConLinkTList);
		logger.debug("ConnectTcsAccountContact Populated");

		List<TaskT> taskList = connect.getTaskTs();
		if (taskList != null)
			populateTasks(connectId, taskList);
		logger.debug("task Populated");

		List<ConnectOpportunityLinkIdT> conOppLinkIdTList = connect
				.getConnectOpportunityLinkIdTs();
		if (conOppLinkIdTList != null)
			populateOppLinks(connectId, conOppLinkIdTList);
		logger.debug("ConnectOpportunity Populated");

		if (connect.getConnectSubLinkDeletionList() != null) {
			deleteSubSps(connect.getConnectSubLinkDeletionList());
			logger.debug("ConnectCustomerContact deleted");
		}
		if (connect.getConnectOfferingLinkDeletionList() != null) {
			deleteOfferings(connect.getConnectOfferingLinkDeletionList());
			logger.debug("ConnectOfferingLinks deleted");
		}

		if (connect.getDeleteConnectOpportunityLinkIdTs() != null) {
			deleteConnectOpportunityLinkIdTs(connect
					.getDeleteConnectOpportunityLinkIdTs());
			logger.debug("ConnectOpportunityLinkIdTs deleted");
		}

		// Save Search Keywords
		if (connect.getSearchKeywordsTs() != null) {
			for (SearchKeywordsT searchKeywordT : connect.getSearchKeywordsTs()) {
				searchKeywordT.setEntityType(EntityType.CONNECT.toString());
				searchKeywordT.setEntityId(connect.getConnectId());
				searchKeywordT.setCreatedModifiedBy(DestinationUtils
						.getCurrentUserDetails().getUserId());
				searchKeywordsRepository.save(searchKeywordT);
			}
		}

		if (connect.getDeleteSearchKeywordsTs() != null) {
			deleteSearchKeywordsTs(connect.getDeleteSearchKeywordsTs());
			logger.debug("SearchKeywordsTs deleted");
		}

		// Delete connectCustomerContactLinkTs
		if (connect.getDeleteConnectCustomerContactLinkTs() != null
				&& connect.getDeleteConnectCustomerContactLinkTs().size() > 0) {
			deleteConnectCustomerContacts(connect
					.getDeleteConnectCustomerContactLinkTs());
			logger.debug("ConnectCustomerContacts deleted");
		}

		// Delete connectTcsAccountContactLinkTs
		if (connect.getDeleteConnectTcsAccountContactLinkTs() != null
				&& connect.getDeleteConnectTcsAccountContactLinkTs().size() > 0) {
			deleteConnectTcsAccountContacts(connect
					.getDeleteConnectTcsAccountContactLinkTs());
			logger.debug("ConnectTcsAccountContacts deleted");
		}

		// Delete ConnectSecondaryOwnerLinkTs
		if (connect.getDeleteConnectSecondaryOwnerLinkTs() != null
				&& connect.getDeleteConnectSecondaryOwnerLinkTs().size() > 0) {
			deleteConnectSecondaryOwnerLinks(connect
					.getDeleteConnectSecondaryOwnerLinkTs());
			logger.debug("ConnectSecondaryOwnerLinks deleted");
		}
		return (connectRepository.save(connect));
	}

	private void deleteSearchKeywordsTs(
			List<SearchKeywordsT> deleteSearchKeywordsTs) {
		logger.debug("Inside deleteSearchKeywordsTs() method");
		for (SearchKeywordsT searchKeywordsTs : deleteSearchKeywordsTs) {
			searchKeywordsRepository.delete(searchKeywordsTs
					.getSearchKeywordsId());
		}
	}

	private void deleteConnectOpportunityLinkIdTs(
			List<ConnectOpportunityLinkIdT> connectOpportunityDeleteLinkIdTs) {
		logger.debug("Inside deleteConnectOpportunityLinkIdTs() method");
		for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : connectOpportunityDeleteLinkIdTs) {
			connectOpportunityLinkTRepository.delete(connectOpportunityLinkIdT
					.getConnectOpportunityLinkId());
		}
	}

	private void deleteOfferings(
			List<ConnectOfferingLinkT> connectOfferingLinkDeletionList) {
		logger.debug("Inside deleteOfferings() method");
		for (ConnectOfferingLinkT connectOffLink : connectOfferingLinkDeletionList) {
			connOffLinkRepo.delete(connectOffLink.getConnectOfferingLinkId());
		}
	}

	private void deleteSubSps(List<ConnectSubSpLinkT> connectSubLinkDeletionList) {
		logger.debug("Inside deleteSubSps() method");
		for (ConnectSubSpLinkT connectSubSp : connectSubLinkDeletionList) {
			connSubSpRepo.delete(connectSubSp.getConnectSubSpLinkId());
		}
	}

	private void deleteConnectCustomerContacts(
			List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs) {
		logger.debug("Inside deleteConnectCustomerContacts() method");
		for (ConnectCustomerContactLinkT connectCustomerContact : connectCustomerContactLinkTs) {
			connCustContRepo.delete(connectCustomerContact
					.getConnectCustomerContactLinkId());
		}
	}

	private void deleteConnectTcsAccountContacts(
			List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs) {
		logger.debug("Inside deleteConnectTcsAccountContacts() method");
		for (ConnectTcsAccountContactLinkT connectTcsContact : connectTcsAccountContactLinkTs) {
			connTcsAcctContRepo.delete(connectTcsContact
					.getConnectTcsAccountContactLinkId());
		}
	}

	private void deleteConnectSecondaryOwnerLinks(
			List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs) {
		logger.debug("Inside deleteConnectSecondaryOwnerLink() method");
		for (ConnectSecondaryOwnerLinkT connectSecondaryOwner : connectSecondaryOwnerLinkTs) {
			connSecOwnerRepo.delete(connectSecondaryOwner
					.getConnectSecondaryOwnerLinkId());
		}
	}

	private void populateOppLinks(String connectId,
			List<ConnectOpportunityLinkIdT> conOppLinkIdTList) {
		logger.debug("Inside populateOppLinks() method");
		for (ConnectOpportunityLinkIdT conOppLinkId : conOppLinkIdTList) {
			// conOppLinkId.setCreatedModifiedBy(currentUserId);
			conOppLinkId.setConnectId(connectId);
			conOppLinkId.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			conOppLinkId.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
		}

	}

	private void populateTasks(String connectId, List<TaskT> taskList) {
		logger.debug("Inside populateTasks() method");
		for (TaskT task : taskList) {
			// task.setCreatedBy(currentUserId);
			task.setConnectId(connectId);
			task.setCreatedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
			task.setModifiedBy(DestinationUtils.getCurrentUserDetails()
					.getUserId());
		}
	}

	private void prepareConnect(List<ConnectT> connectTs) {
		logger.debug("Inside prepareConnect(List<>) method");
		if (CollectionUtils.isNotEmpty(connectTs)) {
			String userId = DestinationUtils.getCurrentUserId();
			String userGroup = userRepository.findByUserId(userId)
					.getUserGroup();
			for (ConnectT connectT : connectTs) {
				prepareConnect(connectT, userId, userGroup);
			}
		}
	}

	private void prepareConnect(ConnectT connectT, String userId, String userGroup) {
		logger.debug("Inside prepareConnect() method");
		if (connectT != null) {
			try {
				connectT.setEnableEditAccess(isEditAccessRequiredForConnect(
						connectT, userGroup, userId));
			} catch (Exception e) {
				throw new DestinationException(
						HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
			}
			setSearchKeywordTs(connectT);
			removeCyclicForLinkedOpportunityTs(connectT);
			removeCyclicForLinkedCustomerMasterTs(connectT);
			removeCyclicForLinkedPartnerMasterTs(connectT);
		}
	}

	/**
	 * Remove cyclic data of list of connects for that partner who belongs to
	 * this connect
	 * 
	 * @param connectT
	 */
	private void removeCyclicForLinkedPartnerMasterTs(ConnectT connectT) {
		if (connectT != null)
			if (connectT.getPartnerMasterT() != null)
				connectT.getPartnerMasterT().setConnectTs(null);
	}

	/**
	 * Remove cyclic data of list of connects for that customer who belongs to
	 * this connect
	 * 
	 * @param connectT
	 */
	private void removeCyclicForLinkedCustomerMasterTs(ConnectT connectT) {
		if (connectT != null)
			if (connectT.getCustomerMasterT() != null)
				connectT.getCustomerMasterT().setConnectTs(null);

	}

	private void removeCyclicForLinkedOpportunityTs(ConnectT connectT) {
		logger.debug("Inside removeCyclicForLinkedOpportunityTs() method");
		if (connectT != null) {
			if (connectT.getConnectOpportunityLinkIdTs() != null) {
				for (ConnectOpportunityLinkIdT connectOpportunityLinkIdT : connectT
						.getConnectOpportunityLinkIdTs()) {
					connectOpportunityLinkIdT.getOpportunityT()
					.setConnectOpportunityLinkIdTs(null);
				}
			}
		}
	}

	// This method is used to invoke asynchronous thread for auto comments
	private void processAutoComments(String connectId, Object oldObject)
			throws Exception {
		logger.debug("Calling processAutoComments() method");
		AutoCommentsHelper autoCommentsHelper = new AutoCommentsHelper();
		autoCommentsHelper.setEntityId(connectId);
		autoCommentsHelper.setEntityType(EntityType.CONNECT.name());
		if (oldObject != null) {
			autoCommentsHelper.setOldObject(oldObject);
		}
		autoCommentsHelper
		.setAutoCommentsEntityTRepository(autoCommentsEntityTRepository);
		autoCommentsHelper
		.setAutoCommentsEntityFieldsTRepository(autoCommentsEntityFieldsTRepository);
		autoCommentsHelper
		.setCollaborationCommentsRepository(collaborationCommentsRepository);
		autoCommentsHelper.setCrudRepository(connectRepository);
		autoCommentsHelper.setEntityManagerFactory(entityManager
				.getEntityManagerFactory());
		autoCommentsHelper.setCollCommentsService(collaborationCommentsService);
		// Invoking Auto Comments Task Executor Thread
		autoCommentsTaskExecutor.execute(autoCommentsHelper);
	}

	/**
	 * This service method retrieves all the users under a supervisor, calls for
	 * all connects between dates and also provides the count of connects per
	 * week and month
	 * 
	 * @param supervisorId
	 * @param fromDate
	 * @param toDate
	 * @param weekStartDate
	 * @param weekEndDate
	 * @param monthStartDate
	 * @param monthEndDate
	 * @return
	 */
	public DashBoardConnectsResponse getTeamConnects(String supervisorId,
			Date fromDate, Date toDate, String role, Date weekStartDate,
			Date weekEndDate, Date monthStartDate, Date monthEndDate, int page,
			int count) throws Exception {
		logger.debug("Inside getTeamConnects service");
		PaginatedResponse connectResponse = new PaginatedResponse();
		Pageable pageable = new PageRequest(page, count);
		DashBoardConnectsResponse dashBoardConnectsResponse = null;

		// Get all users under a supervisor
		List<String> users = userRepository
				.getAllSubordinatesIdBySupervisorId(supervisorId);

		if ((users != null) && (users.size() > 0)) {

			dashBoardConnectsResponse = new DashBoardConnectsResponse();

			Timestamp fromDateTs = new Timestamp(fromDate.getTime());
			Timestamp toDateTs = new Timestamp(toDate.getTime()
					+ ONE_DAY_IN_MILLIS - 1);

			// If ROLE is ALL
			if (role.equalsIgnoreCase(OwnerType.ALL.toString())) {
				// Get connects between two dates
				List<ConnectT> connects = connectRepository.getTeamConnects(
						users, fromDateTs, toDateTs);
				connectResponse.setTotalCount(connects.size());
				connects = paginateConnects(page, count, connects);
				connectResponse.setConnectTs(connects);
				dashBoardConnectsResponse
				.setPaginatedConnectResponse(connectResponse);

				prepareConnect(connects);

				// Get weekly count of connects
				Timestamp weekStartDateTs = new Timestamp(
						weekStartDate.getTime());
				Timestamp weekEndDateTs = new Timestamp(weekEndDate.getTime()
						+ ONE_DAY_IN_MILLIS - 1);
				List<ConnectT> weekConnects = connectRepository
						.getTeamConnects(users, weekStartDateTs, weekEndDateTs);

				dashBoardConnectsResponse.setWeekCount(weekConnects.size());

				// Get monthly count of connects
				Timestamp monthStartDateTs = new Timestamp(
						monthStartDate.getTime());
				Timestamp monthEndDateTs = new Timestamp(monthEndDate.getTime()
						+ ONE_DAY_IN_MILLIS - 1);
				List<ConnectT> monthConnects = connectRepository
						.getTeamConnects(users, monthStartDateTs,
								monthEndDateTs);
				dashBoardConnectsResponse.setMonthCount(monthConnects.size());

				validateDashboardConnectResponse(dashBoardConnectsResponse,
						fromDateTs, toDateTs, connects, weekStartDateTs,
						weekEndDateTs, monthStartDateTs, monthEndDateTs);

			}

			// If ROLE is PRIMARY
			else if (role.equalsIgnoreCase(OwnerType.PRIMARY.toString())) {

				Page<ConnectT> pageConnects = connectRepository
						.findByPrimaryOwnerInAndStartDatetimeOfConnectBetweenOrderByStartDatetimeOfConnectAsc(
								users, fromDateTs, toDateTs, pageable);
				connectResponse.setTotalCount(pageConnects.getTotalElements());
				List<ConnectT> connects = pageConnects.getContent();

				if ((connects != null) && (connects.isEmpty())) {
					logger.error(
							"NOT FOUND : No Connects found with role PRIMARY for supervisor Id : {}",
							supervisorId);
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Connects found with role PRIMARY for supervisor Id : "
									+ supervisorId);
				}

				prepareConnect(connects);
				connectResponse.setConnectTs(connects);
				dashBoardConnectsResponse
				.setPaginatedConnectResponse(connectResponse);
			}

			// If ROLE is SECONDARY
			else if (role.equalsIgnoreCase(OwnerType.SECONDARY.toString())) {

				List<ConnectT> connects = connectRepository
						.findTeamConnectsBySecondaryowner(users, fromDateTs,
								toDateTs);

				if ((connects != null) && (connects.isEmpty())) {
					logger.error(
							"NOT FOUND : No Connects found with role SECONDARY for supervisor Id : {}",
							supervisorId);
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Connects found with role SECONDARY for supervisor Id : "
									+ supervisorId);
				}
				connectResponse.setTotalCount(connects.size());
				connects = paginateConnects(page, count, connects);
				connectResponse.setConnectTs(connects);
				dashBoardConnectsResponse
				.setPaginatedConnectResponse(connectResponse);

				prepareConnect(connects);

			}

			else {

				logger.error("NOT_FOUND: Invalid Role", supervisorId);
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"invalid Role");

			}

		} else {
			logger.error(
					"NOT_FOUND: No subordinate found for supervisor id : {}",
					supervisorId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No subordinate found for supervisor id " + supervisorId);
		}

		removeCyclicConnectsInCustomerMappingT(dashBoardConnectsResponse);
		return dashBoardConnectsResponse;
	}

	private void validateDashboardConnectResponse(
			DashBoardConnectsResponse dashBoardConnectsResponse,
			Timestamp fromDateTs, Timestamp toDateTs, List<ConnectT> connects,
			Timestamp weekStartDateTs, Timestamp weekEndDateTs,
			Timestamp monthStartDateTs, Timestamp monthEndDateTs) {
		// throw an exception if connects is empty and
		// size of monthConnects and weekConnects are zero
		if ((connects == null || connects.isEmpty())
				&& dashBoardConnectsResponse.getWeekCount() == 0
				&& dashBoardConnectsResponse.getMonthCount() == 0) {
			logger.error(
					"NOT_FOUND: No Connects found for for days between {} and {}, "
							+ "days of week between {} and {}, days of month between {} and {}",
							fromDateTs, toDateTs, weekStartDateTs, weekEndDateTs,
							monthStartDateTs, monthEndDateTs);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Connects found for days between " + fromDateTs
					+ " and " + toDateTs + ", days of week between "
					+ weekStartDateTs + " and " + weekEndDateTs
					+ ", days of month between " + monthStartDateTs
					+ " and " + monthEndDateTs);
		}
	}

	/**
	 * This method removes the ConnectTs present in CustomerMappingT in ConnectT
	 * 
	 * @param dashBoardConnectsResponse
	 */
	public void removeCyclicConnectsInCustomerMappingT(
			DashBoardConnectsResponse dashBoardConnectsResponse) {

		if (dashBoardConnectsResponse != null) {

			if ((dashBoardConnectsResponse.getPaginatedConnectResponse()
					.getConnectTs() != null)
					&& (!dashBoardConnectsResponse
							.getPaginatedConnectResponse().getConnectTs()
							.isEmpty())) {

				for (ConnectT connectT : dashBoardConnectsResponse
						.getPaginatedConnectResponse().getConnectTs()) {

					if (connectT.getCustomerMasterT() != null) {

						connectT.getCustomerMasterT().setConnectTs(null);
					}
				}
			}
		}
	}

	/**
	 * This method retrieves all the connects for the Financial Year and status
	 * 
	 * @param status
	 * @param financialYear
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse getAllConnectsForDashbaord(String status,
			String financialYear, int page, int count) throws Exception {
		Pageable pageable = new PageRequest(page, count);
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Page<ConnectT> pageConnects = null;
		List<ConnectT> listOfConnects = null;
		List<String> connectIds = null;
		try {
			if (StringUtils.isEmpty(financialYear)) {
				financialYear = DateUtils.getCurrentFinancialYear();
			}

			Timestamp startTimestamp = new Timestamp(DateUtils
					.getDateFromFinancialYear(financialYear, true).getTime());
			Timestamp endTimestamp = new Timestamp(DateUtils
					.getDateFromFinancialYear(financialYear, false).getTime()
					+ Constants.ONE_DAY_IN_MILLIS - 1);

			if ((status != null) && (ConnectStatusType.contains(status))) {
				// Retrieve all connectIds present within the FY
				connectIds = connectRepository.getAllConnectsForDashbaord(
						startTimestamp, endTimestamp);
				if ((connectIds != null) && (!connectIds.isEmpty())) {
					List<String> connectIdsForStatusOpenClosed = null;
					if (status.equalsIgnoreCase(ConnectStatusType.OPEN
							.toString())) { // If Status is open, check for
						// connects which has no notes in
						// notes_t table
						connectIdsForStatusOpenClosed = connectRepository
								.getAllConnectsForDashbaordStatusOpen(
										connectIds, startTimestamp,
										endTimestamp);
						pageConnects = retrieveConnectsByConnetIdOrderByStartDateTime(
								connectIdsForStatusOpenClosed, pageable);
						paginatedResponse.setTotalCount(pageConnects
								.getTotalElements());
						listOfConnects = pageConnects.getContent();

					} else if (status.equalsIgnoreCase(ConnectStatusType.CLOSED
							.toString())) { // If Status is closed, check for
						// connects which has notes in
						// notes_t table
						connectIdsForStatusOpenClosed = notesRepository
								.getAllConnectsForDashbaordStatusClosed(connectIds);
						pageConnects = retrieveConnectsByConnetIdOrderByStartDateTime(
								connectIdsForStatusOpenClosed, pageable);
						paginatedResponse.setTotalCount(pageConnects
								.getTotalElements());
						listOfConnects = pageConnects.getContent();
					} else if (status.equalsIgnoreCase(ConnectStatusType.ALL
							.toString())) { // If status is ALL, get connects
						// from connect_t
						pageConnects = connectRepository
								.findByConnectIdInOrderByStartDatetimeOfConnectAsc(
										connectIds, pageable);
						paginatedResponse.setTotalCount(pageConnects
								.getTotalElements());
						listOfConnects = pageConnects.getContent();
					}
				}
			} else {
				logger.error("BAD_REQUEST: Invalid Status Type");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Status Type");
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR: " + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		paginatedResponse.setConnectTs(listOfConnects);
		prepareConnect(listOfConnects);
		return paginatedResponse;
	}

	/**
	 * This method retrieves list of Connects based on the connectIds provided
	 * 
	 * @param connectIds
	 * @return List<ConnectT>
	 */
	private Page<ConnectT> retrieveConnectsByConnetIdOrderByStartDateTime(
			List<String> connectIds, Pageable pageable) {

		Page<ConnectT> listOfConnects = null;

		if ((connectIds != null) && (!connectIds.isEmpty())) {
			listOfConnects = connectRepository
					.findByConnectIdInOrderByStartDatetimeOfConnectAsc(
							connectIds, pageable);
		}

		return listOfConnects;
	}

	/**
	 * This method retrieves list of Connects based on the connectIds provided
	 * 
	 * @param connectIds
	 * @return List<ConnectT>
	 */
	private List<ConnectT> retrieveConnectsByConnetIdOrderByStartDateTime(
			List<String> connectIds) {

		List<ConnectT> listOfConnects = null;

		if ((connectIds != null) && (!connectIds.isEmpty())) {
			listOfConnects = connectRepository
					.findByConnectIdInOrderByStartDatetimeOfConnectAsc(connectIds);
		}

		return listOfConnects;
	}

	/**
	 * This service performs search of connects and searchKeywords based on name
	 * and keyword
	 * 
	 * @param name
	 * @param keyword
	 * @return List<ConnectNameKeywordSearch>
	 * @throws Exception
	 */
	public List<ConnectNameKeywordSearch> findConnectNameOrKeywords(
			String name, String keyword) throws Exception {

		List<ConnectNameKeywordSearch> connectNameKeywordSearchList = null;

		try {

			if (name.length() > 0)
				name = "%" + name + "%";
			if (keyword.length() > 0)
				keyword = "%" + keyword + "%";

			List<Object[]> results = connectRepository
					.findConnectNameOrKeywords(name.toUpperCase(),
							keyword.toUpperCase());

			if ((results != null) && (!results.isEmpty())) {
				connectNameKeywordSearchList = new ArrayList<ConnectNameKeywordSearch>();
				for (Object[] result : results) {
					ConnectNameKeywordSearch connectNameKeywordSearch = new ConnectNameKeywordSearch();
					connectNameKeywordSearch.setResult(result[0].toString());
					ConnectT connectT = connectRepository.findOne(result[1]
							.toString());
					setSearchKeywordTs(connectT);
					connectNameKeywordSearch.setConnectT(connectT);
					connectNameKeywordSearch.setIsName(result[2].toString());
					connectNameKeywordSearchList.add(connectNameKeywordSearch);
				}
			}
		} catch (Exception e) {
			logger.error("An Exception has occured : {}", e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return connectNameKeywordSearchList;
	}

	public void save(List<ConnectT> insertList) throws Exception {

		logger.debug("Inside save method");

		Map<Integer, List<ConnectOfferingLinkT>> mapConnectOffering = new HashMap<Integer, List<ConnectOfferingLinkT>>(
				insertList.size());
		Map<Integer, List<ConnectOpportunityLinkIdT>> mapOpportunityLink = new HashMap<Integer, List<ConnectOpportunityLinkIdT>>(
				insertList.size());
		Map<Integer, List<ConnectSecondaryOwnerLinkT>> mapSecondaryOwner = new HashMap<Integer, List<ConnectSecondaryOwnerLinkT>>(
				insertList.size());
		Map<Integer, List<ConnectSubSpLinkT>> mapSubSp = new HashMap<Integer, List<ConnectSubSpLinkT>>(
				insertList.size());
		Map<Integer, List<ConnectTcsAccountContactLinkT>> mapTcsContact = new HashMap<Integer, List<ConnectTcsAccountContactLinkT>>(
				insertList.size());
		Map<Integer, List<ConnectCustomerContactLinkT>> mapCustomerContact = new HashMap<Integer, List<ConnectCustomerContactLinkT>>(
				insertList.size());

		int i = 0;
		for (ConnectT connectT : insertList) {
			mapConnectOffering.put(i, connectT.getConnectOfferingLinkTs());
			mapOpportunityLink.put(i, connectT.getConnectOpportunityLinkIdTs());
			mapSecondaryOwner.put(i, connectT.getConnectSecondaryOwnerLinkTs());
			mapSubSp.put(i, connectT.getConnectSubSpLinkTs());
			mapTcsContact.put(i, connectT.getConnectTcsAccountContactLinkTs());
			mapCustomerContact.put(i,
					connectT.getConnectCustomerContactLinkTs());

			setNullForReferencedObjects(connectT);

			i++;
		}

		Iterable<ConnectT> savedList = connectRepository.save(insertList);
		Iterator<ConnectT> saveIterator = savedList.iterator();
		i = 0;
		while (saveIterator.hasNext()) {
			ConnectT connectT = saveIterator.next();
			List<ConnectOfferingLinkT> offeringList = mapConnectOffering.get(i);
			if (CollectionUtils.isNotEmpty(offeringList)) {
				populateConnectOfferingBatch(connectT.getConnectId(),
						offeringList);
			}
			List<ConnectOpportunityLinkIdT> oppourtunityList = mapOpportunityLink
					.get(i);
			if (CollectionUtils.isNotEmpty(oppourtunityList)) {
				populateOppLinks(connectT.getConnectId(), oppourtunityList);
			}
			List<ConnectSecondaryOwnerLinkT> secOwnerList = mapSecondaryOwner
					.get(i);
			if (CollectionUtils.isNotEmpty(secOwnerList)) {
				populateConnectSecondaryOwnerBatch(connectT.getConnectId(),
						secOwnerList);
			}
			List<ConnectSubSpLinkT> subSpList = mapSubSp.get(i);
			if (CollectionUtils.isNotEmpty(subSpList)) {
				populateConnectSubSpBatch(connectT.getConnectId(), subSpList);
			}
			List<ConnectTcsAccountContactLinkT> tcsContactList = mapTcsContact
					.get(i);
			if (CollectionUtils.isNotEmpty(tcsContactList)) {
				populateConnectTcsAccountContactBatch(connectT.getConnectId(),
						tcsContactList);
			}
			List<ConnectCustomerContactLinkT> custContactList = mapCustomerContact
					.get(i);
			if (CollectionUtils.isNotEmpty(custContactList)) {
				populateConnectCustomerContactBatch(connectT,
						custContactList);
			}

			i++;
		}

		List<ConnectOpportunityLinkIdT> connectOppList = new ArrayList<ConnectOpportunityLinkIdT>();
		for (List<ConnectOpportunityLinkIdT> list : mapOpportunityLink.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				connectOppList.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(connectOppList)) {
			connectOpportunityLinkTRepository.save(connectOppList);
		}

		List<ConnectOfferingLinkT> connectOfferings = new ArrayList<ConnectOfferingLinkT>();
		for (List<ConnectOfferingLinkT> list : mapConnectOffering.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				connectOfferings.addAll(list);
			}

		}
		if (CollectionUtils.isNotEmpty(connectOfferings)) {
			connectOfferingLinkRepository.save(connectOfferings);
		}

		List<ConnectSecondaryOwnerLinkT> connectSecOwner = new ArrayList<ConnectSecondaryOwnerLinkT>();
		for (List<ConnectSecondaryOwnerLinkT> list : mapSecondaryOwner.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				connectSecOwner.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(connectSecOwner)) {
			connectSecondaryOwnerRepository.save(connectSecOwner);
		}

		List<ConnectSubSpLinkT> connectSubSps = new ArrayList<ConnectSubSpLinkT>();
		for (List<ConnectSubSpLinkT> list : mapSubSp.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				connectSubSps.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(connectSubSps)) {
			connectSubSpLinkRepository.save(connectSubSps);
		}

		List<ConnectTcsAccountContactLinkT> connectTcsContacts = new ArrayList<ConnectTcsAccountContactLinkT>();
		for (List<ConnectTcsAccountContactLinkT> list : mapTcsContact.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				connectTcsContacts.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(connectTcsContacts)) {
			connectTcsAccountContactLinkTRepository.save(connectTcsContacts);
		}

		List<ConnectCustomerContactLinkT> connectCustContacts = new ArrayList<ConnectCustomerContactLinkT>();
		for (List<ConnectCustomerContactLinkT> list : mapCustomerContact
				.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				connectCustContacts.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(connectCustContacts)) {
			connectCustomerContactLinkTRepository.save(connectCustContacts);
		}

	}

	public void deleteConnect(List<ConnectT> deleteList) {

		List<ConnectCustomerContactLinkT> connectCustomerContactLinkT = new ArrayList<ConnectCustomerContactLinkT>();
		List<ConnectOfferingLinkT> connectOfferingLinkT = new ArrayList<ConnectOfferingLinkT>();
		List<ConnectOpportunityLinkIdT> connectOpportunityLinkIdT = new ArrayList<ConnectOpportunityLinkIdT>();
		List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkT = new ArrayList<ConnectSecondaryOwnerLinkT>();
		List<ConnectSubSpLinkT> connectSubSpLinkT = new ArrayList<ConnectSubSpLinkT>();
		List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkT = new ArrayList<ConnectTcsAccountContactLinkT>();
		List<CommentsT> commentsT = new ArrayList<CommentsT>();
		List<UserTaggedFollowedT> userTaggedFollowedT = new ArrayList<UserTaggedFollowedT>();
		List<TaskT> taskList = new ArrayList<TaskT>();

		for (ConnectT connect : deleteList) {
			connectSubSpLinkT.addAll(connect.getConnectSubSpLinkTs());
			connectCustomerContactLinkT.addAll(connect
					.getConnectCustomerContactLinkTs());
			connectOfferingLinkT.addAll(connect.getConnectOfferingLinkTs());
			connectOpportunityLinkIdT.addAll(connect
					.getConnectOpportunityLinkIdTs());
			connectSecondaryOwnerLinkT.addAll(connect
					.getConnectSecondaryOwnerLinkTs());
			connectTcsAccountContactLinkT.addAll(connect
					.getConnectTcsAccountContactLinkTs());
			commentsT.addAll(connect.getCommentsTs());
			userTaggedFollowedT.addAll(connect.getUserTaggedFollowedTs());
			taskList.addAll(connect.getTaskTs());
		}

		connCustContRepo.delete(connectCustomerContactLinkT);
		connOffLinkRepo.delete(connectOfferingLinkT);
		connectOpportunityLinkTRepository.delete(connectOpportunityLinkIdT);
		connSecOwnerRepo.delete(connectSecondaryOwnerLinkT);
		connSubSpRepo.delete(connectSubSpLinkT);
		connTcsAcctContRepo.delete(connectTcsAccountContactLinkT);
		commentsTRepository.delete(commentsT);
		userTaggedFollowedRepository.delete(userTaggedFollowedT);
		taskRepository.delete(taskList);

		connectRepository.delete(deleteList);

	}

	public void updateConnect(List<ConnectT> connectList) {

		connectRepository.save(connectList);

	}

	/**
	 * This Method used to find List of Connect for the specified connect ids
	 * 
	 * @param connectIds
	 * @return
	 */
	public List<ConnectT> getConnectsByConnetIds(List<String> connectIds) {
		logger.debug("Inside getConnectsByConnetIds() method");
		List<ConnectT> connectList = null;
		connectList = retrieveConnectsByConnetIdOrderByStartDateTime(connectIds);
		if (connectList == null || connectList.isEmpty()) {
			logger.error("Connects Not Found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connects Not Found");
		}
		return connectList;
	}

	// edit access for connect
	private boolean isEditAccessRequiredForConnect(ConnectT connect,
			String userGroup, String userId) {
		logger.debug("Inside validateEditAccessForConnect method");
		boolean isEditAccessRequired = false;
		if (userGroup.equals(UserGroup.STRATEGIC_INITIATIVES.getValue())) {
			isEditAccessRequired = true;
		} else if (isUserOwner(userId, connect)) {
			isEditAccessRequired = true;
		} else {
			switch (UserGroup.getUserGroup(userGroup)) {
			case BDM :
			case DELIVERY_MANAGER:	
				isEditAccessRequired = false;
				break;
			case BDM_SUPERVISOR:
			case DELIVERY_CLUSTER_HEAD:
			case DELIVERY_CENTRE_HEAD:	
				isEditAccessRequired = opportunityService.isSubordinateAsOwner(userId, null,
						connect.getConnectId());
				break;
			case GEO_HEADS:
			case PMO:
			case IOU_HEADS:
				if (!StringUtils.isEmpty(connect.getCustomerId())) {
					isEditAccessRequired = opportunityService
							.checkEditAccessForGeoAndIou(userGroup, userId,
									connect.getCustomerId());
				}
				if (!StringUtils.isEmpty(connect.getPartnerId())) {
					isEditAccessRequired = isEditAccessNotAuthorisedForPartner(
							userId, userGroup, connect.getPartnerId());
				}
				break;
			default:
				break;
			}
		}
		logger.debug("Is Edit Access Required for connect: " +isEditAccessRequired);
		return isEditAccessRequired;
	}

	/**
	 * This method is used to check whether the logged in user is one of the
	 * owner of connect
	 * 
	 * @param userId
	 * @param connect
	 * @return
	 */
	private boolean isUserOwner(String userId, ConnectT connect) {
		if (StringUtils.equals(connect.getPrimaryOwner(), userId))
			return true;
		else if(CollectionUtils.isNotEmpty(connect.getConnectSecondaryOwnerLinkTs())) {
			for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
					.getConnectSecondaryOwnerLinkTs()) {
				if (connectSecondaryOwnerLinkT.getSecondaryOwner().equals(userId))
					return true;
			}
		}
		return false;
	}

	public boolean isEditAccessNotAuthorisedForPartner(String userId,
			String userGroup, String partnerId) {
		boolean isEditAccessRequired = false;
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
		case GEO_HEADS:
		case PMO:
			String geography = partnerRepository
			.findGeographyByPartnerId(partnerId);

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
			isEditAccessRequired = false;
			break;
		default:
			break;
		}
		return isEditAccessRequired;
	}

	/**
	 * This method is used to check whether the one of the owners
	 * of connect is BDM or BDM Supervisor or GEO Head 
	 * @param owners
	 * @return
	 */
	public boolean isOwnersAreBDMorBDMSupervisorOrGeoHead(Set<String> owners) {
		logger.info("Inside isOwnersAreBDMorBDMSupervisorOrGeoHead method");
		boolean isBDMOrBDMSupervisorOrGeoHead = false;
		List<String> userGroups = userRepository.findUserGroupByUserIds(owners,true);
		if(CollectionUtils.isNotEmpty(userGroups)){
			for (String userGroup : userGroups) {
				if (userGroup.equals(UserGroup.BDM.getValue())
						|| userGroup.equals(UserGroup.BDM_SUPERVISOR.getValue())
						|| userGroup.equals(UserGroup.GEO_HEADS.getValue())) {
					isBDMOrBDMSupervisorOrGeoHead = true;
					break;
				}
			}
		}

		return isBDMOrBDMSupervisorOrGeoHead;
	}


	/**
	 * validate the connect for any inactive fields(owners, customer, etc)
	 * @param connect
	 */
	public void validateInactiveIndicators(ConnectT connect) {

		// createdBy,
		String createdBy = connect.getCreatedBy();
		if(StringUtils.isNotBlank(createdBy) && userRepository.findByActiveTrueAndUserId(createdBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The user createdBy is inactive");
		}

		// modifiedBy,
		String modifiedBy = connect.getModifiedBy();
		if(StringUtils.isNotBlank(modifiedBy) && userRepository.findByActiveTrueAndUserId(modifiedBy) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The user modifiedBy is inactive");
		}

		// primaryOwner,
		String primaryOwner = connect.getPrimaryOwner();
		if(StringUtils.isNotBlank(primaryOwner) && userRepository.findByActiveTrueAndUserId(primaryOwner) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Please assign an active primary owner before making any changes.");
		}

		// customerId,
		String customerId = connect.getCustomerId();
		if(StringUtils.isNotBlank(customerId) && customerRepository.findByActiveTrueAndCustomerId(customerId) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The customer is inactive");
		}

		// partnerId,
		String partnerId = connect.getPartnerId();
		if(StringUtils.isNotBlank(partnerId) && partnerRepository.findByActiveTrueAndPartnerId(partnerId) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The partner is inactive");
		}

		//connectCustomerContactLinkTs,
		List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs = connect.getConnectCustomerContactLinkTs();
		if(CollectionUtils.isNotEmpty(connectCustomerContactLinkTs)) {
			for (ConnectCustomerContactLinkT contact : connectCustomerContactLinkTs) {
				String contactId = contact.getContactId();
				if(StringUtils.isNotBlank(contactId) && contactRepository.findByActiveTrueAndContactId(contactId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The customer contact is inactive");
				}
			}
		}

		// connectOfferingLinkTs,
		List<ConnectOfferingLinkT> connectOfferingLinkTs = connect.getConnectOfferingLinkTs();
		if(CollectionUtils.isNotEmpty(connectOfferingLinkTs)) {
			for (ConnectOfferingLinkT offeringLink : connectOfferingLinkTs) {
				String offering = offeringLink.getOffering();
				if(StringUtils.isNotBlank(offering) && offeringRepository.findByActiveTrueAndOffering(offering) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The offering is inactive");
				}
			}
		}

		//connectSecondaryOwnerLinkTs,
		List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs = connect.getConnectSecondaryOwnerLinkTs();
		if(CollectionUtils.isNotEmpty(connectSecondaryOwnerLinkTs)) {
			for (ConnectSecondaryOwnerLinkT secOwnerLink : connectSecondaryOwnerLinkTs) {
				String owner = secOwnerLink.getSecondaryOwner();
				if(StringUtils.isNotBlank(owner) && userRepository.findByActiveTrueAndUserId(owner) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The secondary owner is inactive");
				}
			}
		}

		//connectSubSpLinkTs,
		List<ConnectSubSpLinkT> connectSubSpLinkTs = connect.getConnectSubSpLinkTs();
		if(CollectionUtils.isNotEmpty(connectSubSpLinkTs)) {
			for (ConnectSubSpLinkT subSpLink : connectSubSpLinkTs) {
				String subSp = subSpLink.getSubSp();
				if(StringUtils.isNotBlank(subSp) && subSpRepository.findByActiveTrueAndSubSp(subSp) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The subsp is inactive");
				}
			}
		}

		//List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs,
		List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs = connect.getConnectTcsAccountContactLinkTs();
		if(CollectionUtils.isNotEmpty(connectTcsAccountContactLinkTs)) {
			for (ConnectTcsAccountContactLinkT contactLink : connectTcsAccountContactLinkTs) {
				String contactId = contactLink.getContactId();
				if(StringUtils.isNotBlank(contactId) && contactRepository.findByActiveTrueAndContactId(contactId) == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "The account contact is inactive");
				}
			}
		}

		// country
		String country = connect.getCountry();
		if(StringUtils.isNotBlank(country) && countryRepository.findByActiveTrueAndCountry(country) == null) {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "The country is inactive");
		}

	}

	/**
	 * Service method to fetch the connect related information based on search type and the search keyword 
	 * @param smartSearchType
	 * @param term
	 * @param getAll 
	 * @param count 
	 * @param page 
	 * @return
	 */
	public PageDTO<SearchResultDTO<ConnectT>> smartSearch(SmartSearchType smartSearchType,
			String term, boolean getAll, int page, int count) {
		logger.info("ConnectService::smartSearch type {}",smartSearchType);
		PageDTO<SearchResultDTO<ConnectT>> res = new PageDTO<SearchResultDTO<ConnectT>>();
		List<SearchResultDTO<ConnectT>> resList = Lists.newArrayList();
		SearchResultDTO<ConnectT> searchResultDTO = new SearchResultDTO<ConnectT>();
		if(smartSearchType != null) {

			switch(smartSearchType) {
			case ALL:
				resList.add(getConnectsByName(term, getAll));
				resList.add(getConnectCustomers(term, getAll));
				resList.add(getConnectPartners(term, getAll));
				resList.add(getConnectSubSps(term, getAll));
				break;
			case CONNECT:
				searchResultDTO = getConnectsByName(term, getAll);
				break;
			case CUSTOMER:
				searchResultDTO = getConnectCustomers(term, getAll);
				break;
			case PARTNER:
				searchResultDTO = getConnectPartners(term, getAll);
				break;
			case SUBSP:
				searchResultDTO = getConnectSubSps(term, getAll);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid search type");

			}

			if(smartSearchType != SmartSearchType.ALL) {//paginate the result if it is fetching entire record(ie. getAll=true)
				if(getAll) {
					List<ConnectT> values = searchResultDTO.getValues();
					searchResultDTO.setValues(PaginationUtils.paginateList(page, count, values));
					res.setTotalCount(values.size());
				}
				resList.add(searchResultDTO);
			}
		}
		res.setContent(resList);
		return res;
	}

	/**
	 * fetch all connects where the subsp contains the provided term
	 * @param term - search keyword
	 * @param getAll - number to limit the record, true - to fetch all
	 * @return
	 */
	private SearchResultDTO<ConnectT> getConnectSubSps(String term, boolean getAll) {
		List<ConnectT> records = connectRepository.searchBySubsp("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.SUBSP, getAll);
	}

	/**
	 * fetch all connects where the partner name contains the provided term
	 * @param term - search keyword
	 * @param getAll - number to limit the record, true - to fetch all
	 * @return
	 */
	private SearchResultDTO<ConnectT> getConnectPartners(String term, boolean getAll) {
		List<ConnectT> records = connectRepository.searchByPartnerName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.PARTNER, getAll);
	}

	/**
	 * fetch all connects where the customer contains the provided term
	 * @param term - search keyword
	 * @param getAll - number to limit the record, true - to fetch all
	 * @return
	 */
	private SearchResultDTO<ConnectT> getConnectCustomers(String term, boolean getAll) {
		List<ConnectT> records = connectRepository.searchByCustomerName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.CUSTOMER, getAll);
	}

	/**
	 * fetch all connects where the connect name contains the provided term
	 * @param term - search keyword
	 * @param getAll - number to limit the record, true - to fetch all
	 * @return
	 */
	private SearchResultDTO<ConnectT> getConnectsByName(String term, boolean getAll) {
		List<ConnectT> records = connectRepository.searchByConnectName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.CONNECT, getAll);
	}

	/**
	 * creates {@link SearchResultDTO} from the list of connects
	 * @param records
	 * @param type
	 * @param getAll
	 * @return
	 */
	private SearchResultDTO<ConnectT> createSearchResultFrom(
			List<ConnectT> records, SmartSearchType type, boolean getAll) {
		SearchResultDTO<ConnectT> conRes = new SearchResultDTO<ConnectT>();
		conRes.setSearchType(type);
		if(getAll) {
			prepareConnect(records);
		}
		conRes.setValues(records);
		return conRes;
	}

	/**
	 * This Method is used to find the list of connect for the given customerId and connectName
	 *  
	 * @param fromDate
	 * @param toDate
	 * @param userId
	 * @param customerId
	 * @param page
	 * @param count
	 * @param term 
	 * @param smartSearchType 
	 * @param connectName
	 * @param term 
	 * @return
	 */
	public PaginatedResponse searchConnectsWithCustomerIdAndConnectName(
			Date fromDate, String userId, String customerId,
			int page, int count, SmartSearchType smartSearchType, String term) {
		logger.info("Inside searchConnectsWithCustomerIdAndConnectName() service");
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		List<ConnectT> connectsList = new ArrayList<ConnectT>();
		Set<ConnectT> connectsSet = new HashSet<ConnectT>();

		if(smartSearchType != null) {

			switch(smartSearchType) {
			case ALL:
				connectsSet.addAll(getConnectsByNameAndCustomerId(term, customerId,
						new Timestamp(fromDate.getTime())));
				connectsSet.addAll(getConnectsByOwnerAndCustomerId(term, customerId,
						new Timestamp(fromDate.getTime())));
				connectsSet.addAll(getConnectsSubSpsAndCustomerId(term, customerId,
						new Timestamp(fromDate.getTime())));
				connectsList.addAll(connectsSet);
				break;
			case NAME:
				connectsList = getConnectsByNameAndCustomerId(term, customerId,
						new Timestamp(fromDate.getTime()));
				break;
			case PRIMARY_OWNER:
				connectsList = getConnectsByOwnerAndCustomerId(term, customerId,
						new Timestamp(fromDate.getTime()));
				break;
			case SUBSP:
				connectsList = getConnectsSubSpsAndCustomerId(term, customerId,
						new Timestamp(fromDate.getTime()));
				break;
			default:
				break;
			}
		} else {
			connectsList = getAllConnectsByCustomerIdAndStartDateOfConnectsAfter(
					customerId, new Timestamp(fromDate.getTime()));
		}

		prepareConnect(connectsList);
		paginatedResponse.setTotalCount(connectsList.size());
		// Code for pagination
		if (PaginationUtils.isValidPagination(page, count, connectsList.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count, connectsList.size());
			int toIndex = PaginationUtils.getEndIndex(page, count, connectsList.size()) + 1;
			connectsList = connectsList.subList(fromIndex, toIndex);
			paginatedResponse.setConnectTs(connectsList);
			logger.debug("connects after pagination size is " + connectsList.size());
		} else {
			logger.info(" Connects Not Available For The Customer");
			throw new DestinationException(HttpStatus.NOT_FOUND, " Connects Not Available For The Customer ");
		}
		return paginatedResponse;
	}


	/**
	 * @param customerId
	 * @param fromTimestamp
	 * @param toTimestamp
	 * @return
	 */
	private List<ConnectT> getAllConnectsByCustomerIdAndStartDateOfConnectsAfter(
			String customerId, Timestamp fromTimestamp) {
		List<ConnectT> connects = connectRepository
				.findByCustomerIdAndStartDatetimeOfConnectAfterOrderByStartDatetimeOfConnectDesc(
						customerId, fromTimestamp);
		return connects;
	}

	/**
	 * This method is used to find the connects for the customerId and connect name after start date of connect
	 * @param term
	 * @param customerId
	 * @param fromTimestamp
	 * @param toTimestamp
	 * @return
	 */
	private List<ConnectT> getConnectsByNameAndCustomerId(String term,
			String customerId, Timestamp fromTimestamp) {
		List<ConnectT> connects = connectRepository
				.findConnectsByStartDatetimeOfConnectAfterForCustomerAndConnectNameLike(
						fromTimestamp, customerId, "%"+ term.toUpperCase() +"%");
		return connects;
	}

	/**
	 * This method is used to find the connects for the customerId and primary owner after start date of connect
	 * @param term
	 * @param customerId
	 * @param fromTimestamp
	 * @param toTimestamp
	 * @return
	 */
	private List<ConnectT> getConnectsByOwnerAndCustomerId(String term,
			String customerId, Timestamp fromTimestamp) {
		List<ConnectT> connects = connectRepository
				.findConnectsByStartDatetimeOfConnectAfterForCustomerAndPrimaryOwnerLike(
						fromTimestamp, customerId, "%"+ term.toUpperCase() +"%");
		return connects;
	}

	/**
	 * This method is used to find the connects for the customerId and subSp after start date of connect
	 * @param term
	 * @param customerId
	 * @param fromTimestamp
	 * @param toTimestamp
	 * @return
	 */
	private List<ConnectT> getConnectsSubSpsAndCustomerId(String term,
			String customerId, Timestamp fromTimestamp) {
		List<ConnectT> connects = connectRepository
				.findConnectsByStartDatetimeOfConnectAfterForCustomerAndSubSpLike(
						fromTimestamp, customerId, "%"+ term.toUpperCase() +"%");
		return connects;
	}

	/**
	 * fetch all customers and their connects between dates, and by type of subsp
	 * @param cntDateFrom
	 * @param cntDateTo
	 * @param subSPType
	 * @return
	 */
	public ContentDTO<ConnectDTO> getAllByType(Date cntDateFrom, Date cntDateTo, String subSPType, String category, String mapId) {
		
		Date startDate = cntDateFrom != null ? cntDateFrom : DateUtils.getFinancialYrStartDate();
		Date endDate = cntDateTo != null ? cntDateTo : DateUtils.getFinancialYrEndDate();
		
		List<ConnectDTO> dtos = Lists.newArrayList();
		
		List<ConnectT> connects = connectRepository.findAllConnectByDateAndSubSP(startDate, endDate, subSPType, category);
		for (ConnectT connecT : connects) {
			ConnectDTO custDto = beanMapper.map(connecT, ConnectDTO.class, mapId);
			dtos.add(custDto);
		}
		return new ContentDTO<ConnectDTO>(dtos);
	}

	/**
	 * fetch all connects of a group customer between dates
	 * @param cntDateFrom
	 * @param cntDateTo
	 * @param subSPType
	 * @return
	 */
	public ContentDTO<ConnectDTO> getAllByGrpCustomer(Date cntDateFrom, Date cntDateTo, String grpCustomer, String mapId) {
		
		Date startDate = cntDateFrom != null ? cntDateFrom : DateUtils.getFinancialYrStartDate();
		Date endDate = cntDateTo != null ? cntDateTo : DateUtils.getFinancialYrEndDate();
		
		List<ConnectDTO> dtos = Lists.newArrayList();
		
		List<ConnectT> connects = connectRepository.findAllConnectByGrpCustomer(startDate, endDate, grpCustomer);
		for (ConnectT connecT : connects) {
			ConnectDTO custDto = beanMapper.map(connecT, ConnectDTO.class, mapId);
			dtos.add(custDto);
		}
		return new ContentDTO<ConnectDTO>(dtos);
	}

	public ConnectDTO getById(String connectId, String mapId) {
		logger.debug("Inside findConnectById() service");
		ConnectDTO connectdto = null;
		ConnectT connectT = connectRepository.findByConnectId(connectId);
		if (connectT != null) {
			connectdto = beanMapper.map(connectT, ConnectDTO.class, mapId);
		} else {
			logger.error("NOT_FOUND: Connect not found: {}", connectId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connect not found: " + connectId);
		}
		return connectdto;
	}
	
}
