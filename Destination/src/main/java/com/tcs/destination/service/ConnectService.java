package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectOpportunityLinkIdT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.DashBoardConnectsResponse;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.SearchKeywordsT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.data.repository.AutoCommentsEntityFieldsTRepository;
import com.tcs.destination.data.repository.AutoCommentsEntityTRepository;
import com.tcs.destination.data.repository.CollaborationCommentsRepository;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ConnectTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.OwnerType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.AutoCommentsHelper;
import com.tcs.destination.helper.AutoCommentsLazyLoader;
import com.tcs.destination.utils.DestinationUtils;

@Service
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

	public ConnectT findConnectById(String connectId) throws Exception {
		logger.debug("Inside findConnectById() service");
		ConnectT connectT = connectRepository.findByConnectId(connectId);
		if (connectT != null) {
			prepareConnect(connectT);
		} else {
			logger.error("NOT_FOUND: Connect not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connect not found");
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

	public List<ConnectT> searchforConnectsByNameContaining(String name,
			String customerId) throws Exception {
		logger.debug("Inside searchforConnectsByNameContaining() service");
		List<ConnectT> connectList = null;
		if (customerId.isEmpty()) {
			connectList = connectRepository.findByConnectNameIgnoreCaseLike("%"
					+ name + "%");
		} else {
			connectList = connectRepository
					.findByConnectNameIgnoreCaseLikeAndCustomerId("%" + name
							+ "%", customerId);
		}

		if (connectList.isEmpty()) {
			logger.error("NOT_FOUND: Connects not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connects not found");
		}
		prepareConnect(connectList);
		return connectList;
	}

	public DashBoardConnectsResponse searchDateRangwWithWeekAndMonthCount(
			Date fromDate, Date toDate, String userId, String owner,
			String customerId, String partnerId, Date weekStartDate,
			Date weekEndDate, Date monthStartDate, Date monthEndDate)
					throws Exception {
		logger.debug("Inside searchDateRangwWithWeekAndMonthCount() service");
		DashBoardConnectsResponse response = new DashBoardConnectsResponse();
		response.setConnectTs(searchforConnectsBetweenForUserOrCustomerOrPartner(
				fromDate, toDate, userId, owner, customerId, partnerId, false));
		if (weekStartDate.getTime() != weekEndDate.getTime()) {
			logger.debug("WeekStartDate and WeekEndDate Time are Not Equal");
			response.setWeekCount(searchforConnectsBetweenForUserOrCustomerOrPartner(
					weekStartDate, weekEndDate, userId, owner, customerId,
					partnerId, true).size());
		}
		if (monthStartDate.getTime() != monthEndDate.getTime()) {
			logger.debug("MonthStartDate  and MonthEndDate are Not Equal");
			response.setMonthCount(searchforConnectsBetweenForUserOrCustomerOrPartner(
					monthStartDate, monthEndDate, userId, owner, customerId,
					partnerId, true).size());
		}
		return response;
	}

	public List<ConnectT> searchforConnectsBetweenForUserOrCustomerOrPartner(
			Date fromDate, Date toDate, String userId, String owner,
			String customerId, String partnerId, boolean isForCount)
					throws Exception {
		logger.debug("Inside searchforConnectsBetweenForUserOrCustomerOrPartner() service");
		Timestamp toTimestamp=new Timestamp(toDate.getTime() + ONE_DAY_IN_MILLIS - 1);
		if (OwnerType.contains(owner)) {
			logger.debug("Owner Type Contains owner");
			List<ConnectT> connects = new ArrayList<ConnectT>();
			if (owner.equalsIgnoreCase(OwnerType.PRIMARY.toString())) {
				logger.debug("Owner is PRIMARY");
				// Exclude the toDate from the date range
				connects = connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								toTimestamp,
								customerId, partnerId);
				System.out.println("Primary :" + connects.size());
			} else if (owner.equalsIgnoreCase(OwnerType.SECONDARY.toString())) {
				logger.debug("Owner is SECONDARY");
				connects = connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								toTimestamp, customerId,
								partnerId);
			} else if (owner.equalsIgnoreCase(OwnerType.ALL.toString())) {
				logger.debug("Owner is ALL");
				connects.addAll(connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								toTimestamp, customerId,
								partnerId));
				if (customerId.equals("") || partnerId.equals("")) {
					logger.debug("CustomerId or partnerId are empty");
					connects.addAll(connectSecondaryOwnerRepository
							.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
									userId, new Timestamp(fromDate.getTime()),
									toTimestamp,
									customerId, partnerId));
				}
			}
			if (connects.isEmpty() && !isForCount) {
				logger.error("NOT_FOUND: Connects not found");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Connects not found");
			}
			prepareConnect(connects);
			return connects;
		}
		logger.error("BAD_REQUEST: Invalid Owner Type.");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"Ivalid Owner Type.");
	}

	@Transactional
	public boolean insertConnect(ConnectT connect) throws Exception {
		logger.debug("Inside insertConnect() service");

		validateRequest(connect,true);

		ConnectT backupConnect = backup(connect);
		logger.debug("Copied connect object.");
		setNullForReferencedObjects(connect);
		logger.debug("Reference Objects set null");

		if (connectRepository.save(connect) != null) {
			String tempId = connect.getConnectId();
			backupConnect.setConnectId(tempId);
			logger.debug("Parent Object Saved. Id : " + tempId);
			connect = restore(backupConnect);
			String categoryUpperCase = connect.getConnectCategory()
					.toUpperCase();
			connect.setConnectCategory(categoryUpperCase);

			String connectId = connect.getConnectId();
			String customerId = connect.getCustomerId();
			String partnerId = connect.getPartnerId();

			List<NotesT> noteList = connect.getNotesTs();
			if (noteList != null)
				populateNotes(customerId, partnerId,
						categoryUpperCase, connectId, noteList);
			logger.debug("Notes Populated ");

			List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
					.getConnectCustomerContactLinkTs();
			if (conCustConLinkTList != null) {
				populateConnectCustomerContactLinks(
						connectId, conCustConLinkTList);
				logger.debug("ConnectCustomerContact Populated ");
			} else {
				logger.error("Connect Customer Contact List null");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Connect Customer Contact List null");
			}

			List<ConnectOfferingLinkT> conOffLinkTList = connect
					.getConnectOfferingLinkTs();
			if (conOffLinkTList != null) {
				populateConnectOfferingLinks(connectId,
						conOffLinkTList);
				logger.debug("ConnectOffering Populated ");
			}

			List<ConnectSubSpLinkT> conSubSpLinkTList = connect
					.getConnectSubSpLinkTs();
			if (conSubSpLinkTList != null) {
				populateConnectSubSpLinks(connectId,
						conSubSpLinkTList);
				logger.debug("ConnectSubSp Populated ");
			}

			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
					.getConnectSecondaryOwnerLinkTs();
			if (conSecOwnLinkTList != null) {
				populateConnectSecondaryOwnerLinks(
						connectId, conSecOwnLinkTList);
				logger.debug("ConnectSecondaryOwner Populated ");
			}

			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
					.getConnectTcsAccountContactLinkTs();
			if (conTcsAccConLinkTList != null) {
				populateConnectTcsAccountContactLinks(
						connectId, conTcsAccConLinkTList);
				logger.debug("ConnectTcsAccountContact Populated ");
			}

			// Save Search Keywords
			if (connect.getSearchKeywordsTs() != null) {
				for (SearchKeywordsT searchKeywordT : connect
						.getSearchKeywordsTs()) {
					searchKeywordT.setEntityType(EntityType.CONNECT
							.toString());
					searchKeywordT.setEntityId(connect.getConnectId());
					searchKeywordsRepository.save(searchKeywordT);
				}
			}

			if (connectRepository.save(connect) != null) {
				logger.info("Connect has been added successfully");
				// Invoke Asynchronous Auto Comments Thread
				processAutoComments(connect.getConnectId(), null);
				return true;
			}

		}
		logger.debug("Connect not Saved");
		return false;
	}

	private void validateRequest(ConnectT connect,boolean isInsert) throws Exception {
		logger.debug("Inside validateRequest() method");
		String connectCategory = connect.getConnectCategory();

		if (connectCategory == null || connectCategory.trim().isEmpty()) {
			logger.error("Connect Category is required");
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
				break;
			case PARTNER:
				if (partnerId != null && !partnerId.trim().isEmpty())
					isValid = true;
				break;
			default:
				logger.error("Invalid Connect Category");
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Connect Category");
			}
		} else {
			logger.error("Invalid Connect Category");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Connect Category");
		}

		if (!isValid) {
			logger.error("Missing PartnerId/CustomerId");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing PartnerId/CustomerId");
		}

		if(isInsert && connect.getCreatedBy()==null){
			logger.error("Missing UserCreated in connect");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing UserCreated in connect");
		}

		if(connect.getModifiedBy()==null){
			logger.error("Missing UserModified");
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Missing UserModified");
		}
	}

	private void populateConnectTcsAccountContactLinks(
			String connectId,
			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList)
					throws Exception {
		logger.debug("Inside populateConnectTcsAccountContactLinks() method");
		for (ConnectTcsAccountContactLinkT conTcsAccConLink : conTcsAccConLinkTList) {
			//conTcsAccConLink.setCreatedModifiedBy(currentUserId);
			conTcsAccConLink.setConnectId(connectId);
		}

	}

	private void populateConnectSecondaryOwnerLinks(
			String connectId,
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList) {
		logger.debug("Inside populateConnectSecondaryOwnerLinks() method");
		for (ConnectSecondaryOwnerLinkT conSecOwnLink : conSecOwnLinkTList) {
			//conSecOwnLink.setCreatedModifiedBy(currentUserId);
			conSecOwnLink.setConnectId(connectId);
		}

	}

	private void populateConnectSubSpLinks(
			String connectId, List<ConnectSubSpLinkT> conSubSpLinkTList) {
		logger.debug("Inside populateConnectSubSpLinks() method");
		for (ConnectSubSpLinkT conSubSpLink : conSubSpLinkTList) {
			conSubSpLink.setConnectId(connectId);
			//conSubSpLink.setCreatedModifiedBy(currentUserId);
		}

	}

	private void populateConnectOfferingLinks(
			String connectId, List<ConnectOfferingLinkT> conOffLinkTList) {
		logger.debug("Inside populateConnectOfferingLinks() method");
		for (ConnectOfferingLinkT conOffLink : conOffLinkTList) {
			//conOffLink.setCreatedModifiedBy(currentUserId);
			conOffLink.setConnectId(connectId);
		}

	}

	private void populateConnectCustomerContactLinks(
			String connectId,
			List<ConnectCustomerContactLinkT> conCustConLinkTList) {
		logger.debug("Inside populateConnectCustomerContactLinks() method");
		for (ConnectCustomerContactLinkT conCustConLink : conCustConLinkTList) {
			//conCustConLink.setCreatedModifiedBy(currentUserId);
			conCustConLink.setConnectId(connectId);
		}
	}

	private void populateNotes(String customerId,
			String partnerId, String categoryUpperCase, String connectId,
			List<NotesT> noteList) {
		logger.debug("Inside populateNotes() method");
		for (NotesT note : noteList) {
			note.setEntityType(categoryUpperCase);
			note.setConnectId(connectId);

			if (categoryUpperCase.equalsIgnoreCase("CUSTOMER")) {
				logger.debug("Category is CUSTOMER");
				CustomerMasterT customer = new CustomerMasterT();
				customer.setCustomerId(customerId);
				note.setCustomerMasterT(customer);
			} else {
				logger.debug("Category is not CUSTOMER");
				PartnerMasterT partner = new PartnerMasterT();
				partner.setPartnerId(partnerId);
				note.setPartnerMasterT(partner);
			}
		}

	}

	private ConnectT restore(ConnectT backupConnect) {
		return backupConnect;
	}

	private ConnectT backup(ConnectT connect) {
		return new ConnectT(connect);
	}

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
		String connectId = connect.getConnectId();
		if (connectId == null) {
			logger.error("ConnectId is required for update");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "ConnectId is required for update");
		}
		// Check if connect exists
		if (!connectRepository.exists(connectId)) {
			logger.error("Connect not found for update: {}", connectId);
			throw new DestinationException(HttpStatus.NOT_FOUND, "Connect not found for update: " + connectId);
		}
		// Load db object before update with lazy collections populated for auto comments
		ConnectT beforeConnect = loadDbConnectWithLazyCollections(connectId);
		// Copy the db object as the above object is managed by current hibernate session
		ConnectT oldObject = (ConnectT) DestinationUtils.copy(beforeConnect);
		
		// Update database
		ConnectT afterConnect = editConnect(connect);

		if (afterConnect != null) {
			logger.info("Connect has been updated successfully: " + connectId);
			// Invoke Asynchronous Auto Comments Thread
			processAutoComments(connectId, oldObject);
			return true;	
		}
		return false;
	}
	
	// This method is used to load database object with auto comments eligible lazy collections populated
	public ConnectT loadDbConnectWithLazyCollections(String connectId) throws Exception {
		logger.debug("Inside loadDbConnectWithLazyCollections() method");
		ConnectT connect = (ConnectT) AutoCommentsLazyLoader.loadLazyCollections(connectId, EntityType.CONNECT.name(), 
				connectRepository, autoCommentsEntityTRepository, autoCommentsEntityFieldsTRepository, null);
		return connect;
	}
	
	public ConnectT editConnect(ConnectT connect) throws Exception {
		logger.debug("inside editConnect() method");

		// Validate request
		validateRequest(connect,false);

		String categoryUpperCase = connect.getConnectCategory()
				.toUpperCase();
		connect.setConnectCategory(categoryUpperCase);
		String connectId = connect.getConnectId();
		logger.debug("Connect Id : " + connectId);

		String customerId = connect.getCustomerId();
		String partnerId = connect.getPartnerId();

		List<NotesT> noteList = connect.getNotesTs();
		if (noteList != null)
			populateNotes(customerId, partnerId,
					categoryUpperCase, connectId, noteList);
		logger.debug("Notes Populated");

		List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
				.getConnectCustomerContactLinkTs();
		if (conCustConLinkTList != null)
			populateConnectCustomerContactLinks(connectId,
					conCustConLinkTList);
		logger.debug("ConnectCustomerContact Populated");

		List<ConnectOfferingLinkT> conOffLinkTList = connect
				.getConnectOfferingLinkTs();
		if (conOffLinkTList != null)
			populateConnectOfferingLinks(connectId,
					conOffLinkTList);
		logger.debug("ConnectOffering Populated");

		List<ConnectSubSpLinkT> conSubSpLinkTList = connect
				.getConnectSubSpLinkTs();
		if (conSubSpLinkTList != null)
			populateConnectSubSpLinks(connectId,
					conSubSpLinkTList);
		logger.debug("ConnectSubSp Populated");

		List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
				.getConnectSecondaryOwnerLinkTs();
		if (conSecOwnLinkTList != null)
			populateConnectSecondaryOwnerLinks(connectId,
					conSecOwnLinkTList);
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

		// Save Search Keywords
		if (connect.getSearchKeywordsTs() != null) {
			for (SearchKeywordsT searchKeywordT : connect
					.getSearchKeywordsTs()) {
				searchKeywordT.setEntityType(EntityType.CONNECT.toString());
				searchKeywordT.setEntityId(connect.getConnectId());
				searchKeywordsRepository.save(searchKeywordT);
			}
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
			//conOppLinkId.setCreatedModifiedBy(currentUserId);
			conOppLinkId.setConnectId(connectId);
		}

	}

	private void populateTasks(String connectId,
			List<TaskT> taskList) {
		logger.debug("Inside populateTasks() method");
		for (TaskT task : taskList) {
			//task.setCreatedBy(currentUserId);
			task.setConnectId(connectId);
		}
	}

	private void prepareConnect(List<ConnectT> connectTs) {
		logger.debug("Inside prepareConnect(List<>) method");
		if (connectTs != null) {
			for (ConnectT connectT : connectTs) {
				prepareConnect(connectT);
			}
		}
	}

	private void prepareConnect(ConnectT connectT) {
		logger.debug("Inside prepareConnect() method");
		if (connectT != null) {
			setSearchKeywordTs(connectT);
			removeCyclicForLinkedOpportunityTs(connectT);
		}
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
	private void processAutoComments(String connectId, Object oldObject) throws Exception {
		logger.debug("Calling processAutoComments() method");
		AutoCommentsHelper autoCommentsHelper = new AutoCommentsHelper();
		autoCommentsHelper.setEntityId(connectId);
		autoCommentsHelper.setEntityType(EntityType.CONNECT.name());
		if (oldObject != null) {
			autoCommentsHelper.setOldObject(oldObject);
		}
		autoCommentsHelper.setAutoCommentsEntityTRepository(autoCommentsEntityTRepository);
		autoCommentsHelper.setAutoCommentsEntityFieldsTRepository(autoCommentsEntityFieldsTRepository);
		autoCommentsHelper.setCollaborationCommentsRepository(collaborationCommentsRepository);
		autoCommentsHelper.setCrudRepository(connectRepository);
		autoCommentsHelper.setEntityManagerFactory(entityManager.getEntityManagerFactory());
		// Invoking Auto Comments Task Executor Thread
		autoCommentsTaskExecutor.execute(autoCommentsHelper);
	}
	
	/**
	 * This service method retrieves all the users under a supervisor, 
	 * calls for all connects between dates and also provides the 
	 * count of connects per week and month 
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
	public DashBoardConnectsResponse getTeamConnects(String supervisorId, Date fromDate, Date toDate, Date weekStartDate, Date weekEndDate, Date monthStartDate, Date monthEndDate) {
		
		DashBoardConnectsResponse dashBoardConnectsResponse = new DashBoardConnectsResponse();
		
		// Get all users under a supervisor
		List<String> users = userRepository.getAllSubordinatesIdBySupervisorId(supervisorId);
		
		// Get connects between two dates 
		List<ConnectT> connects = connectRepository.getTeamConnects(users, new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime() + ONE_DAY_IN_MILLIS - 1));
		prepareConnect(connects);
		dashBoardConnectsResponse.setConnectTs(connects);
		
		// Get weekly Count of connects
		List<ConnectT> weekConnects = connectRepository.getTeamConnects(users, new Timestamp(weekStartDate.getTime()), new Timestamp(weekEndDate.getTime() + ONE_DAY_IN_MILLIS - 1));
		dashBoardConnectsResponse.setWeekCount(weekConnects.size());
		
		// Get monthly Count of connects
		List<ConnectT> monthConnects = connectRepository.getTeamConnects(users, new Timestamp(monthStartDate.getTime()), new Timestamp(monthEndDate.getTime() + ONE_DAY_IN_MILLIS - 1));
		dashBoardConnectsResponse.setMonthCount(monthConnects.size());
		
		return dashBoardConnectsResponse;
	}
}