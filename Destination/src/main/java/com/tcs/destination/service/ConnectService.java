package com.tcs.destination.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
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
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ConnectTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.data.repository.SearchKeywordsRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.OwnerType;
import com.tcs.destination.enums.PlaceType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

@Component
public class ConnectService {

	private static final int ONE_DAY_IN_MILLIS = 86400000;

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectService.class);

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

	public ConnectT findConnectById(String connectId) throws Exception {
		logger.debug("Inside searchforConnectsById service");
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
		logger.debug("Inside searchforConnectsByNameContaining service");
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
			logger.error("NOT_FOUND: Connection information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connection information not available");
		}
		prepareConnect(connectList);
		return connectList;
	}

	public DashBoardConnectsResponse searchDateRangwWithWeekAndMonthCount(
			Date fromDate, Date toDate, String userId, String owner,
			String customerId, String partnerId, Date weekStartDate,
			Date weekEndDate, Date monthStartDate, Date monthEndDate)
			throws Exception {
		logger.debug("Inside DashBoardConnectsResponse Service");
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
		logger.debug("Inside searchforConnectsBetweenForUserOrCustomerOrPartner Service");
		if (OwnerType.contains(owner)) {
			logger.debug("Owner Type Contains owner");
			List<ConnectT> connects = new ArrayList<ConnectT>();
			if (owner.equalsIgnoreCase(OwnerType.PRIMARY.toString())) {
				logger.debug("owner is PRIMARY");
				// Exclude the toDate from the date range
				connects = connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime() + ONE_DAY_IN_MILLIS - 1),
								customerId, partnerId);
				System.out.println("Primary :" + connects.size());
			} else if (owner.equalsIgnoreCase(OwnerType.SECONDARY.toString())) {
				logger.debug("Owner is SECONDARY");
				connects = connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()), customerId,
								partnerId);
			} else if (owner.equalsIgnoreCase(OwnerType.ALL.toString())) {
				logger.debug("Owner value is ALL");
				connects.addAll(connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()), customerId,
								partnerId));
				if (customerId.equals("") || partnerId.equals("")) {
					logger.debug("CustomerId or partnerId are empty");
					connects.addAll(connectSecondaryOwnerRepository
							.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
									userId, new Timestamp(fromDate.getTime()),
									new Timestamp(toDate.getTime()),
									customerId, partnerId));
				}
			}
			if (connects.isEmpty() && !isForCount) {
				logger.error("NOT_FOUND: No Relevent Data Found in the database");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Relevent Data Found in the database");
			}
			prepareConnect(connects);
			return connects;
		}
		logger.error("BAD_REQUEST: No such Owner Type exists. Please ensure your Owner Type.");
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"No such Owner Type exists. Please ensure your Owner Type.");
	}

	@Transactional
	public boolean insertConnect(ConnectT connect) throws Exception {
		logger.debug("Inside insertConnect Service");
		UserT currentUser = DestinationUtils.getCurrentUserDetails();
		String currentUserId = currentUser.getUserId();
		connect.setCreatedModifiedBy(currentUserId);
		logger.debug("Connect Insert - user : " + currentUserId);

		validateRequest(connect);

		ConnectT backupConnect = backup(connect);
		logger.debug("Copied connect object.");
		setNullForReferencedObjects(connect);
		logger.debug("Reference Objects set null");

		try {
			if (connectRepository.save(connect) != null) {
				String tempId = connect.getConnectId();
				backupConnect.setConnectId(tempId);
				logger.debug("Root Object Saved. Id : " + tempId);
				connect = restore(backupConnect);
				String categoryUpperCase = connect.getConnectCategory()
						.toUpperCase();
				connect.setConnectCategory(categoryUpperCase);

				String connectId = connect.getConnectId();
				String customerId = connect.getCustomerId();
				String partnerId = connect.getPartnerId();

				List<NotesT> noteList = connect.getNotesTs();
				if (noteList != null)
					populateNotes(currentUserId, customerId, partnerId,
							categoryUpperCase, connectId, noteList);
				logger.debug("Notes Populated ");

				List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
						.getConnectCustomerContactLinkTs();
				if (conCustConLinkTList != null) {
					populateConnectCustomerContactLinks(currentUserId,
							connectId, conCustConLinkTList);
					logger.debug("ConnectCustomerContact Populated ");
				} else {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"conCustConLinkTList null");
				}

				List<ConnectOfferingLinkT> conOffLinkTList = connect
						.getConnectOfferingLinkTs();
				if (conOffLinkTList != null) {
					populateConnectOfferingLinks(currentUserId, connectId,
							conOffLinkTList);
					logger.debug("ConnectOffering Populated ");
				}

				List<ConnectSubSpLinkT> conSubSpLinkTList = connect
						.getConnectSubSpLinkTs();
				if (conSubSpLinkTList != null) {
					populateConnectSubSpLinks(currentUserId, connectId,
							conSubSpLinkTList);
					logger.debug("ConnectSubSp Populated ");
				}

				List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
						.getConnectSecondaryOwnerLinkTs();
				if (conSecOwnLinkTList != null) {
					populateConnectSecondaryOwnerLinks(currentUserId,
							connectId, conSecOwnLinkTList);
					logger.debug("ConnectSecondaryOwner Populated ");
				}

				List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
						.getConnectTcsAccountContactLinkTs();
				if (conTcsAccConLinkTList != null) {
					populateConnectTcsAccountContactLinks(currentUserId,
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
					logger.debug("Connect Record Inserted - child objects saved");
					return true;
				}

			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		logger.debug("Connect Details are not Saved successfully");
		return false;
	}

	private void validateRequest(ConnectT connect) throws Exception {
		String connectCategory = connect.getConnectCategory();

		if (connectCategory == null || connectCategory.trim().isEmpty()) {
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
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Connect Category is Invalid");
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Connect Category is Invalid");
		}

		if (!isValid) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Request - Missing PartnerId/CustomerId");
		}
		
		//check for valid place(TCS/CLIENT)
		String place = connect.getPlace();
		if (!PlaceType.contains(place)) {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Place is invalid");
		}
	}

	private void populateConnectTcsAccountContactLinks(String currentUserId,
			String connectId,
			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList)
			throws Exception {
		logger.debug("Inside populateConnectTcsAccountContactLinks Service");
		for (ConnectTcsAccountContactLinkT conTcsAccConLink : conTcsAccConLinkTList) {
			conTcsAccConLink.setCreatedModifiedBy(currentUserId);
			conTcsAccConLink.setConnectId(connectId);
		}

	}

	private void populateConnectSecondaryOwnerLinks(String currentUserId,
			String connectId,
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList) {
		logger.debug("Inside populateConnectSecondaryOwnerLinks Service");
		for (ConnectSecondaryOwnerLinkT conSecOwnLink : conSecOwnLinkTList) {
			conSecOwnLink.setCreatedModifiedBy(currentUserId);
			conSecOwnLink.setConnectId(connectId);
		}

	}

	private void populateConnectSubSpLinks(String currentUserId,
			String connectId, List<ConnectSubSpLinkT> conSubSpLinkTList) {
		logger.debug("Inside populateConnectSubSpLinks Service");
		for (ConnectSubSpLinkT conSubSpLink : conSubSpLinkTList) {
			conSubSpLink.setConnectId(connectId);
			conSubSpLink.setCreatedModifiedBy(currentUserId);
		}

	}

	private void populateConnectOfferingLinks(String currentUserId,
			String connectId, List<ConnectOfferingLinkT> conOffLinkTList) {
		logger.debug("Inside populateConnectOfferingLinks Service");
		for (ConnectOfferingLinkT conOffLink : conOffLinkTList) {
			conOffLink.setCreatedModifiedBy(currentUserId);
			conOffLink.setConnectId(connectId);
		}

	}

	private void populateConnectCustomerContactLinks(String currentUserId,
			String connectId,
			List<ConnectCustomerContactLinkT> conCustConLinkTList) {
		logger.debug("Inside populateConnectCustomerContactLinks service");
		for (ConnectCustomerContactLinkT conCustConLink : conCustConLinkTList) {
			conCustConLink.setCreatedModifiedBy(currentUserId);
			conCustConLink.setConnectId(connectId);
		}
	}

	private void populateNotes(String currentUserId, String customerId,
			String partnerId, String categoryUpperCase, String connectId,
			List<NotesT> noteList) {
		logger.debug("Inside populateNotes service");
		for (NotesT note : noteList) {
			note.setEntityType(categoryUpperCase);
			UserT user = new UserT();
			user.setUserId(currentUserId);
			note.setUserT(user);
			note.setConnectId(connectId);

			if (categoryUpperCase.equalsIgnoreCase("CUSTOMER")) {
				logger.debug("Category Equals to CUSTOMER");
				CustomerMasterT customer = new CustomerMasterT();
				customer.setCustomerId(customerId);
				note.setCustomerMasterT(customer);
			} else {
				logger.debug("Category Not Equals to CUSTOMER");
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
		connect.setUserT(null);
	}

	@Transactional
	public boolean editConnect(ConnectT connect) throws Exception {
		logger.debug("inside editConnect Service");

		UserT currentUser = DestinationUtils.getCurrentUserDetails();
		String currentUserId = currentUser.getUserId();
		connect.setCreatedModifiedBy(currentUserId);
		logger.debug("Connect Edit - user : " + currentUserId);

		validateRequest(connect);

		try {
			String categoryUpperCase = connect.getConnectCategory()
					.toUpperCase();
			connect.setConnectCategory(categoryUpperCase);
			String connectId = connect.getConnectId();
			logger.debug("Connect Id : " + connectId);

			String customerId = connect.getCustomerId();
			String partnerId = connect.getPartnerId();

			List<NotesT> noteList = connect.getNotesTs();
			if (noteList != null)
				populateNotes(currentUserId, customerId, partnerId,
						categoryUpperCase, connectId, noteList);
			logger.debug("Notes Populated");

			List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
					.getConnectCustomerContactLinkTs();
			if (conCustConLinkTList != null)
				populateConnectCustomerContactLinks(currentUserId, connectId,
						conCustConLinkTList);
			logger.debug("ConnectCustomerContact Populated");

			List<ConnectOfferingLinkT> conOffLinkTList = connect
					.getConnectOfferingLinkTs();
			if (conOffLinkTList != null)
				populateConnectOfferingLinks(currentUserId, connectId,
						conOffLinkTList);
			logger.debug("ConnectOffering Populated");

			List<ConnectSubSpLinkT> conSubSpLinkTList = connect
					.getConnectSubSpLinkTs();
			if (conSubSpLinkTList != null)
				populateConnectSubSpLinks(currentUserId, connectId,
						conSubSpLinkTList);
			logger.debug("ConnectSubSp Populated");

			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
					.getConnectSecondaryOwnerLinkTs();
			if (conSecOwnLinkTList != null)
				populateConnectSecondaryOwnerLinks(currentUserId, connectId,
						conSecOwnLinkTList);
			logger.debug("ConnectSecondaryOwner Populated");

			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
					.getConnectTcsAccountContactLinkTs();
			if (conTcsAccConLinkTList != null)
				populateConnectTcsAccountContactLinks(currentUserId, connectId,
						conTcsAccConLinkTList);
			logger.debug("ConnectTcsAccountContact Populated");

			List<TaskT> taskList = connect.getTaskTs();
			if (taskList != null)
				populateTasks(currentUserId, connectId, taskList);
			logger.debug("task Populated");

			List<ConnectOpportunityLinkIdT> conOppLinkIdTList = connect
					.getConnectOpportunityLinkIdTs();
			if (conOppLinkIdTList != null)
				populateOppLinks(currentUserId, connectId, conOppLinkIdTList);
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

			if (connectRepository.save(connect) != null) {
				logger.debug("Connect Edit Success");
				return true;
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR:" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return false;
	}

	private void deleteOfferings(
			List<ConnectOfferingLinkT> connectOfferingLinkDeletionList) {
		logger.debug("Inside deleteOfferings Service");
		for (ConnectOfferingLinkT connectOffLink : connectOfferingLinkDeletionList) {
			connOffLinkRepo.delete(connectOffLink.getConnectOfferingLinkId());
		}
	}

	private void deleteSubSps(List<ConnectSubSpLinkT> connectSubLinkDeletionList) {
		logger.debug("Inside deleteSubSps Service");
		for (ConnectSubSpLinkT connectSubSp : connectSubLinkDeletionList) {
			connSubSpRepo.delete(connectSubSp.getConnectSubSpLinkId());
		}
	}

	private void deleteConnectCustomerContacts(
			List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs) {
		logger.debug("Inside deleteConnectCustomerContacts Service");
		for (ConnectCustomerContactLinkT connectCustomerContact : connectCustomerContactLinkTs) {
			connCustContRepo.delete(connectCustomerContact
					.getConnectCustomerContactLinkId());
		}
	}

	private void deleteConnectTcsAccountContacts(
			List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs) {
		logger.debug("Inside deleteConnectTcsAccountContacts Service");
		for (ConnectTcsAccountContactLinkT connectTcsContact : connectTcsAccountContactLinkTs) {
			connTcsAcctContRepo.delete(connectTcsContact
					.getConnectTcsAccountContactLinkId());
		}
	}

	private void deleteConnectSecondaryOwnerLinks(
			List<ConnectSecondaryOwnerLinkT> connectSecondaryOwnerLinkTs) {
		logger.debug("Inside deleteConnectSecondaryOwnerLink Service");
		for (ConnectSecondaryOwnerLinkT connectSecondaryOwner : connectSecondaryOwnerLinkTs) {
			connSecOwnerRepo.delete(connectSecondaryOwner
					.getConnectSecondaryOwnerLinkId());
		}
	}

	private void populateOppLinks(String currentUserId, String connectId,
			List<ConnectOpportunityLinkIdT> conOppLinkIdTList) {
		logger.debug("Inside populateOppLinks Service");
		for (ConnectOpportunityLinkIdT conOppLinkId : conOppLinkIdTList) {
			conOppLinkId.setCreatedModifiedBy(currentUserId);
			conOppLinkId.setConnectId(connectId);
		}

	}

	private void populateTasks(String currentUserId, String connectId,
			List<TaskT> taskList) {
		logger.debug("Inside populateTasks Service");
		for (TaskT task : taskList) {
			task.setCreatedBy(currentUserId);
			task.setConnectId(connectId);
		}
	}

	private void prepareConnect(List<ConnectT> connectTs) {
		if (connectTs != null) {
			for (ConnectT connectT : connectTs) {
				prepareConnect(connectT);
			}
		}
	}

	private void prepareConnect(ConnectT connectT) {
		if (connectT != null) {
			setSearchKeywordTs(connectT);
			removeCyclicForLinkedOpportunityTs(connectT);
		}
	}

	private void removeCyclicForLinkedOpportunityTs(ConnectT connectT) {
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

}
