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
import com.tcs.destination.bean.DocumentRepositoryT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.TaskT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.controller.DocumentController;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.DocumentRepository;
import com.tcs.destination.exception.ConnectionNotFoundException;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.NoDataFoundException;
import com.tcs.destination.exception.NoSuchOwnerTypeException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.Constants.OWNER_TYPE;

@Component
public class ConnectService {

	private static final Logger logger = LoggerFactory.getLogger(ConnectService.class);
	
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

	public ConnectT searchforConnectsById(String connectId) throws Exception {
		ConnectT connect = connectRepository.findByConnectId(connectId);

		if (connect == null)
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connection information not available");

		return connect;

	}

	public List<ConnectT> searchforConnectsByNameContaining(String name)
			throws Exception {
		List<ConnectT> connectList = connectRepository
				.findByConnectNameIgnoreCaseLike("%" + name + "%");

		if (connectList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connection information not available");
		return connectList;
	}

	public DashBoardConnectsResponse searchDateRangwWithWeekAndMonthCount(
			Date fromDate, Date toDate, String userId, String owner,
			String customerId, String partnerId, Date weekStartDate,
			Date weekEndDate, Date monthStartDate, Date monthEndDate)
			throws Exception {
		DashBoardConnectsResponse response = new DashBoardConnectsResponse();
		response.setConnectTs(searchforConnectsBetweenForUserOrCustomerOrPartner(
				fromDate, toDate, userId, owner, customerId, partnerId, false));
		if (weekStartDate.getTime() != weekEndDate.getTime()) {
			response.setWeekCount(searchforConnectsBetweenForUserOrCustomerOrPartner(
					weekStartDate, weekEndDate, userId, owner, customerId,
					partnerId, true).size());
		}
		if (monthStartDate.getTime() != monthEndDate.getTime()) {
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
		if (OWNER_TYPE.contains(owner)) {
			List<ConnectT> connects = new ArrayList<ConnectT>();
			if (owner.equalsIgnoreCase(OWNER_TYPE.PRIMARY.toString())) {
				connects = connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()), customerId,
								partnerId);
				System.out.println("Primary :" + connects.size());
			} else if (owner.equalsIgnoreCase(OWNER_TYPE.SECONDARY.toString())) {
				connects = connectSecondaryOwnerRepository
						.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()), customerId,
								partnerId);
			} else if (owner.equalsIgnoreCase(OWNER_TYPE.ALL.toString())) {
				connects.addAll(connectRepository
						.findByPrimaryOwnerIgnoreCaseAndStartDatetimeOfConnectBetweenForCustomerOrPartner(
								userId, new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()), customerId,
								partnerId));
				if (customerId.equals("") || partnerId.equals("")) {
					connects.addAll(connectSecondaryOwnerRepository
							.findConnectTWithDateWithRangeForSecondaryOwnerForCustomerOrPartner(
									userId, new Timestamp(fromDate.getTime()),
									new Timestamp(toDate.getTime()),
									customerId, partnerId));
				}
			}
			if (connects.isEmpty() && !isForCount)
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Relevent Data Found in the database");
			return connects;
		}
		throw new DestinationException(HttpStatus.BAD_REQUEST,
				"No such Owner Type exists. Please ensure your Owner Type.");
	}

	public boolean insertConnect(ConnectT connect) throws Exception {

		Timestamp currentTimeStamp = Constants.getCurrentTimeStamp();
		UserT currentUser = Constants.getCurrentUserDetails();
		String currentUserId = currentUser.getUserId();

		
		
		connect.setCreatedModifiedBy(currentUserId);
		connect.setCreatedModifiedDatetime(currentTimeStamp);
		logger.info("Connect Insert - user : " + currentUserId);
		logger.info("Connect Insert - timestamp : " + currentTimeStamp);

		ConnectT backupConnect = backup(connect);
		logger.info("Copied connect object.");
		setNullForReferencedObjects(connect);
		logger.info("Reference Objects set null");
		try {
			if (connectRepository.save(connect) != null) {
				String tempId = connect.getConnectId();
				backupConnect.setConnectId(tempId);
				logger.info("Root Object Saved. Id : " + tempId);
				connect = restore(backupConnect);
				// connect.setCreatedModifiedBy(currentUserId);
				// connect.setCreatedModifiedDatetime(currentTimeStamp);
				String categoryUpperCase = connect.getConnectCategory()
						.toUpperCase();
				connect.setConnectCategory(categoryUpperCase);
				
				String connectId = connect.getConnectId();
				String customerId = connect.getCustomerId();
				String partnerId = connect.getPartnerId();

				List<NotesT> noteList = connect.getNotesTs();
				populateNotes(currentTimeStamp, currentUserId, customerId,
						partnerId, categoryUpperCase, connectId, noteList);
				logger.info("Notes Populated ");
				
				List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
						.getConnectCustomerContactLinkTs();
				populateConnectCustomerContactLinks(currentUserId,
						currentTimeStamp, connectId, conCustConLinkTList);
				logger.info("ConnectCustomerContact Populated ");

				List<ConnectOfferingLinkT> conOffLinkTList = connect
						.getConnectOfferingLinkTs();
				populateConnectOfferingLinks(currentUserId, currentTimeStamp,
						connectId, conOffLinkTList);
				logger.info("ConnectOffering Populated ");
				
				List<ConnectSubSpLinkT> conSubSpLinkTList = connect
						.getConnectSubSpLinkTs();
				populateConnectSubSpLinks(currentUserId, currentTimeStamp,
						connectId, conSubSpLinkTList);
				logger.info("ConnectSubSp Populated ");
				
				List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
						.getConnectSecondaryOwnerLinkTs();
				populateConnectSecondaryOwnerLinks(currentUserId,
						currentTimeStamp, connectId, conSecOwnLinkTList);
				logger.info("ConnectSecondaryOwner Populated ");
				
				List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
						.getConnectTcsAccountContactLinkTs();
				populateConnectTcsAccountContactLinks(currentUserId,
						currentTimeStamp, connectId, conTcsAccConLinkTList);
				logger.info("ConnectTcsAccountContact Populated ");

				if (connectRepository.save(connect) != null) {
					logger.info("Connect Record Inserted - child objects saved");
					return true;
				}

			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
		}

		return false;
	}

	private void populateConnectTcsAccountContactLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList) {

		for (ConnectTcsAccountContactLinkT conTcsAccConLink : conTcsAccConLinkTList) {
			conTcsAccConLink.setCreatedModifiedBy(currentUserId);
			conTcsAccConLink.setCreatedModifiedDatetime(currentTimeStamp);
			conTcsAccConLink.setConnectId(connectId);
		}

	}

	private void populateConnectSecondaryOwnerLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList) {

		for (ConnectSecondaryOwnerLinkT conSecOwnLink : conSecOwnLinkTList) {
			conSecOwnLink.setCreatedModifiedBy(currentUserId);
			conSecOwnLink.setCreatedModifiedDatetime(currentTimeStamp);
			conSecOwnLink.setConnectId(connectId);
		}

	}

	private void populateConnectSubSpLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectSubSpLinkT> conSubSpLinkTList) {

		for (ConnectSubSpLinkT conSubSpLink : conSubSpLinkTList) {
			conSubSpLink.setConnectId(connectId);
			conSubSpLink.setCreatedModifiedBy(currentUserId);
			conSubSpLink.setCreatedModifiedDatetime(currentTimeStamp);
		}

	}

	private void populateConnectOfferingLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectOfferingLinkT> conOffLinkTList) {

		for (ConnectOfferingLinkT conOffLink : conOffLinkTList) {
			conOffLink.setCreatedModifiedBy(currentUserId);
			conOffLink.setCreatedModifiedDatetime(currentTimeStamp);
			conOffLink.setConnectId(connectId);
		}

	}

	private void populateConnectCustomerContactLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectCustomerContactLinkT> conCustConLinkTList) {

		for (ConnectCustomerContactLinkT conCustConLink : conCustConLinkTList) {
			conCustConLink.setCreatedModifiedBy(currentUserId);
			conCustConLink.setCreatedModifiedDatetime(currentTimeStamp);
			conCustConLink.setConnectId(connectId);
		}
	}

	private void populateNotes(Timestamp currentTimeStamp,
			String currentUserId, String customerId, String partnerId,
			String categoryUpperCase, String connectId, List<NotesT> noteList) {

		for (NotesT note : noteList) {
			note.setEntityType(categoryUpperCase);
			UserT user = new UserT();
			user.setUserId(currentUserId);
			note.setUserT(user);
			note.setCreatedDatetime(currentTimeStamp);
			note.setConnectId(connectId);

			if (categoryUpperCase.equalsIgnoreCase("CUSTOMER")) {
				CustomerMasterT customer = new CustomerMasterT();
				customer.setCustomerId(customerId);
				note.setCustomerMasterT(customer);
			} else {
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
		// ConnectT backupConnect = backup(connect);
		// setNullForReferencedObjects(connect);

		Timestamp currentTimeStamp = Constants.getCurrentTimeStamp();
		UserT currentUser = Constants.getCurrentUserDetails();
		String currentUserId = currentUser.getUserId();

		connect.setCreatedModifiedBy(currentUserId);
		connect.setCreatedModifiedDatetime(currentTimeStamp);
		logger.info("Connect Edit - user : " + currentUserId);
		logger.info("Connect Edit - timestamp : " + currentTimeStamp);
		
		// backupConnect.setConnectId(connect.getConnectId());
		// connect = restore(backupConnect);
		try {
			String categoryUpperCase = connect.getConnectCategory()
					.toUpperCase();
			connect.setConnectCategory(categoryUpperCase);
			String connectId = connect.getConnectId();
			logger.info("Connect Id : " + connectId);
			
			String customerId = connect.getCustomerId();
			String partnerId = connect.getPartnerId();
			// populateNotes(currentTimeStamp,currentUserId,categoryUpperCase,connectId,
			// noteList,connect);
			List<NotesT> noteList = connect.getNotesTs();
			if (noteList != null)
				populateNotes(currentTimeStamp, currentUserId, customerId,
						partnerId, categoryUpperCase, connectId, noteList);
			logger.info("Notes Populated");

			List<ConnectCustomerContactLinkT> conCustConLinkTList = connect
					.getConnectCustomerContactLinkTs();
			if (conCustConLinkTList != null)
				populateConnectCustomerContactLinks(currentUserId,
						currentTimeStamp, connectId, conCustConLinkTList);
			logger.info("ConnectCustomerContact Populated");

			List<ConnectOfferingLinkT> conOffLinkTList = connect
					.getConnectOfferingLinkTs();
			if (conOffLinkTList != null)
				populateConnectOfferingLinks(currentUserId, currentTimeStamp,
						connectId, conOffLinkTList);
			logger.info("ConnectOffering Populated");
			
			List<ConnectSubSpLinkT> conSubSpLinkTList = connect
					.getConnectSubSpLinkTs();
			if (conSubSpLinkTList != null)
				populateConnectSubSpLinks(currentUserId, currentTimeStamp,
						connectId, conSubSpLinkTList);
			logger.info("ConnectSubSp Populated");
			
			List<ConnectSecondaryOwnerLinkT> conSecOwnLinkTList = connect
					.getConnectSecondaryOwnerLinkTs();
			if (conSecOwnLinkTList != null)
				populateConnectSecondaryOwnerLinks(currentUserId,
						currentTimeStamp, connectId, conSecOwnLinkTList);
			logger.info("ConnectSecondaryOwner Populated");

			List<ConnectTcsAccountContactLinkT> conTcsAccConLinkTList = connect
					.getConnectTcsAccountContactLinkTs();
			if (conTcsAccConLinkTList != null)
				populateConnectTcsAccountContactLinks(currentUserId,
						currentTimeStamp, connectId, conTcsAccConLinkTList);
			logger.info("ConnectTcsAccountContact Populated");

			List<TaskT> taskList = connect.getTaskTs();
			if (taskList != null)
				populateTasks(currentUserId, currentTimeStamp, connectId,
						taskList);
			logger.info("task Populated");

			List<ConnectOpportunityLinkIdT> conOppLinkIdTList = connect
					.getConnectOpportunityLinkIdTs();
			if (conOppLinkIdTList != null)
				populateOppLinks(currentUserId, currentTimeStamp, connectId,
						conOppLinkIdTList);
			logger.info("ConnectOpportunity Populated");

			if (connect.getConnectSubLinkDeletionList() != null) {
				deleteSubSps(connect.getConnectSubLinkDeletionList());
				logger.info("ConnectCustomerContact deleted");
			}
			if (connect.getConnectOfferingLinkDeletionList() != null) {
				deleteOfferings(connect.getConnectOfferingLinkDeletionList());
				logger.info("ConnectOfferingLinks deleted");
			}

			if (connectRepository.save(connect) != null) {
				logger.info("Connect Edit Success");
				return true;
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return false;
	}

	private void deleteOfferings(
			List<ConnectOfferingLinkT> connectOfferingLinkDeletionList) {
		for (ConnectOfferingLinkT connectOffLink : connectOfferingLinkDeletionList) {
			connOffLinkRepo.delete(connectOffLink.getConnectOfferingLinkId());
		}
	}

	private void deleteSubSps(List<ConnectSubSpLinkT> connectSubLinkDeletionList) {
		for (ConnectSubSpLinkT connectSubSp : connectSubLinkDeletionList) {
			connSubSpRepo.delete(connectSubSp.getConnectSubSpLinkId());
		}
	}

	private void populateOppLinks(String currentUserId,
			Timestamp currentTimeStamp, String connectId,
			List<ConnectOpportunityLinkIdT> conOppLinkIdTList) {
		for (ConnectOpportunityLinkIdT conOppLinkId : conOppLinkIdTList) {
			conOppLinkId.setCreatedModifiedBy(currentUserId);
			conOppLinkId.setCreatedModifiedDatetime(currentTimeStamp);
			conOppLinkId.setConnectId(connectId);
		}

	}

	private void populateTasks(String currentUserId,
			Timestamp currentTimeStamp, String connectId, List<TaskT> taskList) {
		for (TaskT task : taskList) {
			task.setCreatedModifiedBy(currentUserId);
			task.setCreatedModifiedDatetime(currentTimeStamp);
			task.setConnectId(connectId);
		}

	}

}
