package com.tcs.destination.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ConnectsSplitDTO;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.OpportunitiesSplitDTO;
import com.tcs.destination.bean.OpportunityCustomerContactLinkT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.OpportunityTcsAccountContactLinkT;
import com.tcs.destination.bean.PageDTO;
import com.tcs.destination.bean.PaginatedResponse;
import com.tcs.destination.bean.PartnerContactLinkT;
import com.tcs.destination.bean.ProductContactLinkT;
import com.tcs.destination.bean.SearchResultDTO;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.PartnerContactLinkTRepository;
import com.tcs.destination.data.repository.ProductContactLinkTRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.enums.SmartSearchType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.PaginationUtils;
import com.tcs.destination.utils.StringUtils;

@Service
public class ContactService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContactService.class);

	private static final String CONACT_QUERY_PREFIX = "select distinct(CONT.contact_id) from contact_t CONT "
			+ " JOIN contact_customer_link_t CCLT on CONT.contact_id=CCLT.contact_id "
			+ " JOIN customer_master_t CMT on CMT.customer_id=CCLT.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT on CMT.iou=ICMT.iou ";

	private static final String CUSTOMER_IOU_COND_SUFFIX = "ICMT.display_iou in (";
	private static final String CUSTOMER_GEO_COND_SUFFIX = "CMT.geography in (";
	private static final String CONTACT_ID_COND_SUFFIX = "CONT.contact_id in ";

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;

	@Autowired
	ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	@Autowired
	PartnerContactLinkTRepository partnerContactLinkTRepository;

	@Autowired
	ProductContactLinkTRepository productContactLinkTRepository;



	/**
	 * This service saves partner details into Contact_t
	 * 
	 * @param insertList
	 * @param keyword
	 * @throws Exception
	 */
	public void save(List<ContactT> insertList) throws Exception {
		logger.debug("Inside save method");
		contactRepository.save(insertList);
	}

	/**
	 * This service updates contact details in contact_t
	 * 
	 * @param contactList
	 * @param keyword
	 * @throws Exception
	 */
	public void updateContact(List<ContactT> updateList) {
		logger.debug("Begin:Inside updateContact method of ContactService");
		contactRepository.save(updateList);
		logger.debug("End:Inside updateContact method of ContactService");
	}

	/**
	 * This service deletes contact details from contact_t
	 * 
	 * @param contactList
	 * @param keyword
	 * @throws Exception
	 */
	public void deleteContact(List<ContactT> deleteList) {
		logger.debug("Begin:Inside deleteContact method of ContactService");
		contactRepository.save(deleteList);
		logger.debug("End:Inside deleteContact method of ContactService");

	}

	/**
	 * This method is used to find contact details for the given contact id.
	 * 
	 * @param contactId
	 * @param userId
	 * @return contact details for the given contact id.
	 */
	public ContactT findById(String contactId, String userId) throws Exception {
		logger.debug("Inside findTaskById Service");
		ContactT contact = contactRepository.findOne(contactId);
		// if (!userId
		// .equals(DestinationUtils.getCurrentUserDetails().getUserId()))
		// throw new DestinationException(HttpStatus.FORBIDDEN,
		// "User Id and Login User Detail does not match");
		if (contact == null) {
			logger.error("NOT_FOUND: No contact found for the ContactId");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contact found");
		}
		removeCyclicForLinkedContactTs(contact);
		if (contact.getContactCategory().equals(EntityType.CUSTOMER.name())) {
			prepareContactDetails(contact, null);
		}
		updateContactTFor360(contact);
		return contact;
	}

	/**
	 * This method is used to update contact object to include connects by date wise and opportunities by sales stage
	 * @param contact
	 */
	private void updateContactTFor360(ContactT contact) {
		handleConnects(contact);
		handleOpportunities(contact);
	}

	/**
	 * This method is used for handling opportunities for 360
	 * @param contact
	 */
	private void handleOpportunities(ContactT contact) {
		handleCustomerContactForOpportunities(contact);
		handleTcsAccountContactForOpportunities(contact);
	}

	/**
	 * This method is used for handling tcs account opportunities for 360
	 * @param contact
	 */
	private void handleTcsAccountContactForOpportunities(ContactT contact) {
		List<OpportunityTcsAccountContactLinkT> opportunityTcsAccountContactLinkTs
		= contact.getOpportunityTcsAccountContactLinkTs();
		OpportunitiesSplitDTO opportunitiesTcsAccountContactSplitDTO
		= new OpportunitiesSplitDTO();

		List<OpportunityT> wonOpportunitiesList = Lists.newArrayList();
		List<OpportunityT> lostOpportunitiesList = Lists.newArrayList();
		List<OpportunityT> pipelineOpportunitiesList = Lists.newArrayList();
		List<OpportunityT> anticipatingOpportunitiesList = Lists.newArrayList();

		for(OpportunityTcsAccountContactLinkT opportunityTcsAccountContactLinkT :
			opportunityTcsAccountContactLinkTs){
			OpportunityT opportunity = opportunityTcsAccountContactLinkT.getOpportunityT();
			int salesStageCode = opportunity.getSalesStageCode();
			switch(salesStageCode){
			case 0 :
			case 1 :
			case 2 :
			case 3 :
				anticipatingOpportunitiesList.add(opportunity);
				break;
			case 4 :
			case 5 :
			case 6 :
			case 7 :
			case 8 : 
				pipelineOpportunitiesList.add(opportunity);
				break;
			case 9 :
				wonOpportunitiesList.add(opportunity);
				break;
			case 10 :
			case 11 :
			case 12 :
			case 13 :
				lostOpportunitiesList.add(opportunity);
				break;
			}
		}

		opportunitiesTcsAccountContactSplitDTO.setAnticipatingOpportunitiesDTO(anticipatingOpportunitiesList);
		opportunitiesTcsAccountContactSplitDTO.setPipelineOpportunitiesDTO(pipelineOpportunitiesList);
		opportunitiesTcsAccountContactSplitDTO.setWonOpportunitiesDTO(wonOpportunitiesList);
		opportunitiesTcsAccountContactSplitDTO.setLostOpportunitiesDTO(lostOpportunitiesList);
		contact.setTcsAccountContactOpportunitiesDTO(opportunitiesTcsAccountContactSplitDTO);
	}

	/**
	 * This method is used for handling customer opportunities for 360
	 * @param contact
	 */
	private void handleCustomerContactForOpportunities(ContactT contact) {
		List<OpportunityCustomerContactLinkT> opportunityCustomerContactLinkTs
		= contact.getOpportunityCustomerContactLinkTs();
		OpportunitiesSplitDTO opportunitiesCustomerContactSplitDTO
		= new OpportunitiesSplitDTO();

		List<OpportunityT> wonOpportunitiesList = Lists.newArrayList();
		List<OpportunityT> lostOpportunitiesList = Lists.newArrayList();
		List<OpportunityT> pipelineOpportunitiesList = Lists.newArrayList();
		List<OpportunityT> anticipatingOpportunitiesList = Lists.newArrayList();

		for(OpportunityCustomerContactLinkT opportunityCustomerContactLinkT :
			opportunityCustomerContactLinkTs){
			OpportunityT opportunity = opportunityCustomerContactLinkT.getOpportunityT();
			int salesStageCode = opportunity.getSalesStageCode();
			switch(salesStageCode){
			case 0 :
			case 1 :
			case 2 :
			case 3 :
				anticipatingOpportunitiesList.add(opportunity);
				break;
			case 4 :
			case 5 :
			case 6 :
			case 7 :
			case 8 : 
				pipelineOpportunitiesList.add(opportunity);
				break;
			case 9 :
				wonOpportunitiesList.add(opportunity);
				break;
			case 10 :
			case 11 :
			case 12 :
			case 13 :
				lostOpportunitiesList.add(opportunity);
				break;
			}
		}

		opportunitiesCustomerContactSplitDTO.setAnticipatingOpportunitiesDTO(anticipatingOpportunitiesList);
		opportunitiesCustomerContactSplitDTO.setPipelineOpportunitiesDTO(pipelineOpportunitiesList);
		opportunitiesCustomerContactSplitDTO.setWonOpportunitiesDTO(wonOpportunitiesList);
		opportunitiesCustomerContactSplitDTO.setLostOpportunitiesDTO(lostOpportunitiesList);
		contact.setCustomerContactOpportunitiesDTO(opportunitiesCustomerContactSplitDTO);
	}


	/**
	 * This method is used for handling connects for 360
	 * @param contact
	 */
	private void handleConnects(ContactT contact) {
		handleCustomerContactConnects(contact);
		handleTcsAccountContactConnects(contact);
	}

	/**
	 * This method is used for handling tcs account connects for 360
	 * @param contact
	 */
	private void handleTcsAccountContactConnects(ContactT contact) {
		List<ConnectTcsAccountContactLinkT> connectTcsAccountContactLinkTs 
		= contact.getConnectTcsAccountContactLinkTs();
		ConnectsSplitDTO connectSplitDTO = new ConnectsSplitDTO();
		List<ConnectT> pastConnects = Lists.newArrayList();
		List<ConnectT> upcomingConnects = Lists.newArrayList();
		for(ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT : connectTcsAccountContactLinkTs){
			ConnectT connect = connectTcsAccountContactLinkT.getConnectT();
			Timestamp nowStamp = DateUtils.getCurrentTimeStamp();
			if(connect.getStartDatetimeOfConnect().before(nowStamp)){
				pastConnects.add(connect);
			} else {
				upcomingConnects.add(connect);
			}
		}
		connectSplitDTO.setPastConnects(pastConnects);
		connectSplitDTO.setUpcomingConnects(upcomingConnects);
		contact.setTcsAccountContactConnectsDTO(connectSplitDTO);
	}

	/**
	 * This method is used for handling customer connects for 360
	 * @param contact
	 */
	private void handleCustomerContactConnects(ContactT contact) {
		List<ConnectCustomerContactLinkT> connectCustomerContactLinkTs 
		= contact.getConnectCustomerContactLinkTs();
		ConnectsSplitDTO connectSplitDTO = new ConnectsSplitDTO();
		List<ConnectT> pastConnects = Lists.newArrayList();
		List<ConnectT> upcomingConnects = Lists.newArrayList();
		for(ConnectCustomerContactLinkT connectCustomerContactLinkT : connectCustomerContactLinkTs){
			ConnectT connect = connectCustomerContactLinkT.getConnectT();
			Timestamp nowStamp = DateUtils.getCurrentTimeStamp();
			if(connect.getStartDatetimeOfConnect().before(nowStamp)){
				pastConnects.add(connect);
			} else {
				upcomingConnects.add(connect);
			}
		}
		connectSplitDTO.setPastConnects(pastConnects);
		connectSplitDTO.setUpcomingConnects(upcomingConnects);
		contact.setCustomerContactConnectsDTO(connectSplitDTO);
	}

	/**
	 * This method is used to find all the contacts with the given contact name
	 * and/or for a specific Customer / Partner.
	 * 
	 * @param contactName
	 *            , customerId, partnerId
	 * @param userId
	 * @return contacts.
	 */
	public PaginatedResponse findContactsWithNameContaining(String contactName,
			String customerId, String partnerId, String contactType,
			String userId,int page,
			int count) throws Exception {
		logger.debug("Inside findContactsWithNameContaining Service");

		PaginatedResponse contactResponse = new PaginatedResponse();


		List<ContactT> contactList = contactRepository.findByActiveTrueAndContactName("%"
				+ contactName + "%", customerId, partnerId, contactType);
		contactResponse.setTotalCount(contactList.size());
		contactList = paginateContacts(page, count, contactList);
		contactResponse.setContactTs(contactList);
		// if (!userId
		// .equals(DestinationUtils.getCurrentUserDetails().getUserId()))
		// throw new DestinationException(HttpStatus.FORBIDDEN,
		// "User Id and Login User Detail does not match");
		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		removeCyclicForLinkedContactTs(contactList);
		prepareContactDetails(contactList);
		return contactResponse;
	}

	/**
	 * This method is used to find all the contacts with the given contact type
	 * and/or for a specific Customer / Partner.
	 * 
	 * @param customerId
	 *            , partnerId, contactType
	 * @param userId
	 * @return contacts.
	 */
	public PaginatedResponse findContactsByContactType(String customerId,
			String partnerId, String contactType, String userId,int page,
			int count)
					throws Exception {
		logger.debug("Inside findContactsByContactType Service");

		PaginatedResponse contactResponse = new PaginatedResponse();

		List<ContactT> contactList = contactRepository.findByContactType(
				customerId, partnerId, contactType);
		contactResponse.setTotalCount(contactList.size());
		contactList = paginateContacts(page, count, contactList);
		contactResponse.setContactTs(contactList);
		// if (!userId
		// .equals(DestinationUtils.getCurrentUserDetails().getUserId()))
		// throw new DestinationException(HttpStatus.FORBIDDEN,
		// "User Id and Login User Detail does not match");
		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		removeCyclicForLinkedContactTs(contactList);
		prepareContactDetails(contactList);
		return contactResponse;
	}

	/**
	 * This method is used to find all the contacts with the given starting
	 * alphabet .
	 * 
	 * @param startsWith
	 * @param userId
	 * @return contacts.
	 */
	public PaginatedResponse findContactsWithNameStarting(String startsWith,
			String userId,int page,int count) throws Exception {
		logger.debug("Inside findContactsWithNameStarting Service");
		Pageable pageable = new PageRequest(page, count);
		PaginatedResponse paginatedResponse = new PaginatedResponse();
		Page<ContactT> contactPage = contactRepository
				.findByActiveTrueAndContactNameIgnoreCaseStartingWithOrderByContactNameAsc(startsWith, pageable);
		paginatedResponse.setTotalCount(contactPage.getTotalElements());
		List<ContactT> contactList = contactPage.getContent();
		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		removeCyclicForLinkedContactTs(contactList);
		prepareContactDetails(contactList);
		paginatedResponse.setContactTs(contactList);
		return paginatedResponse;
	}

	/**
	 * This method is used to save contact
	 * @param contact
	 * @param isUpdate
	 * @return savedStatus
	 */
	@Transactional
	public boolean save(ContactT contact, boolean isUpdate) throws Exception {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();

		if (isUpdate) {
			if (contact.getContactId() == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Cannot Update Contact without contactId");
			}
			if (contact.getDeleteContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : contact
						.getDeleteContactCustomerLinkTs()) {

					contactCustomerLinkT.setCreatedModifiedBy(userId);
					contactCustomerLinkTRepository.delete(contactCustomerLinkT);
				}
			}
			// created by and modified by spitted for partner data model changes
			contact.setModifiedBy(userId);
			ContactT contactObj = contactRepository.findByContactId(contact.getContactId());
			contact.setCreatedBy(contactObj.getCreatedBy());
			contact.setCreatedDatetime(contactObj.getCreatedDatetime());
		} else {
			if (contact.getContactId() != null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"ContactId should not be passed");
			}
			// created by and modified by spitted for partner data model changes
			contact.setCreatedBy(userId);
			contact.setModifiedBy(userId);
		}

		// Validate input parameters
		validateRequest(contact);

		ContactT managedContact = saveBaseContact(contact);
		saveChildContactObjects(managedContact);
		return true;
	}

	/**
	 * This method is used to validate contact input parameters.
	 * 
	 * @param contact
	 * @return
	 */
	private void validateRequest(ContactT contact) throws DestinationException {
		//		List<PartnerContactLinkT> partnerContactList=new ArrayList<PartnerContactLinkT>();
		//		PartnerContactLinkT pclt=new PartnerContactLinkT();

		if (EntityType.contains(contact.getContactCategory())) {
			if (contact.getContactCategory().equals(EntityType.CUSTOMER.name())) {
				if (contact.getContactCustomerLinkTs() == null
						|| contact.getContactCustomerLinkTs().size() == 0) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"CustomerId is required");
				}

				//				pclt.setPartnerId(null);
				//				partnerContactList.add(pclt);
				//				contact.setPartnerContactLinkTs(partnerContactList);
				contact.setPartnerContactLinkTs(null);
			} else if (contact.getContactCategory().equals(
					EntityType.PARTNER.name())) {
				String partnerId=contact.getPartnerContactLinkTs().get(0).getPartnerId();
				if (partnerId == null
						|| partnerId.isEmpty()) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"PartnerId is required");
				}
				contact.setContactCustomerLinkTs(null);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid Contact Entity Type");
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Contact Entity Type");
		}

		if (ContactType.contains(contact.getContactType())) {
			logger.debug("Contact Type is Present");
			if (contact.getContactType().equals(ContactType.INTERNAL.name())) {
				if (contact.getEmployeeNumber() == null
						|| contact.getEmployeeNumber().isEmpty()) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Internal Contact must have Employee Number");
				}
				//				String partnerId=contact.getPartnerContactLinkTs().get(0).getPartnerId();
				//				if (partnerId != null
				//						&& !(partnerId.isEmpty())) {
				//					throw new DestinationException(HttpStatus.BAD_REQUEST,
				//							"Internal Contact cannot be added to Partner");
				//				}
			}

			if (contact.getContactType().equals(ContactType.EXTERNAL.name())) {
				if (contact.getEmployeeNumber() != null
						&& !(contact.getEmployeeNumber().isEmpty())) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"External Contact should not have Employee Number");
				}
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Contact Type");
		}
	}

	/**
	 * This method is used to save contact - base object
	 * 
	 * @param contact
	 * @return contact
	 */
	private ContactT saveBaseContact(ContactT requestContact)
			throws CloneNotSupportedException, Exception {
		ContactT contact = requestContact.clone();
		requestContact.setContactCustomerLinkTs(null);
		requestContact.setPartnerContactLinkTs(null);
		contact.setContactId(contactRepository.save(requestContact)
				.getContactId());
		return contact;
	}

	/**
	 * This method is used to save contact - child objects
	 * 
	 * @param contact
	 * @return contact
	 */
	private ContactT saveChildContactObjects(ContactT contact) {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		// Set Contact Customer Links
		if (contact.getContactCustomerLinkTs() != null) {
			for (ContactCustomerLinkT contactCustomerLinkT : contact
					.getContactCustomerLinkTs()) {
				contactCustomerLinkT.setContactId(contact.getContactId());
				contactCustomerLinkT.setCreatedModifiedBy(userId);
			}
		}
		if (contact.getPartnerContactLinkTs() != null) {
			for (PartnerContactLinkT partnerContactLinkT : contact
					.getPartnerContactLinkTs()) {
				partnerContactLinkT.setContactId(contact.getContactId());
				partnerContactLinkT.setCreatedBy(userId);
				partnerContactLinkT.setModifiedBy(userId);
			}
		}
		if(contact.getContactCategory().equals(EntityType.PARTNER.toString()) && !StringUtils.isEmpty(contact.getProductId())){
			ProductContactLinkT productcontatcLinkT = new ProductContactLinkT();
			productcontatcLinkT.setContactId(contact.getContactId());
			productcontatcLinkT.setProductId(contact.getProductId());
			productcontatcLinkT.setCreatedBy(DestinationUtils.getCurrentUserDetails().getUserId());
			productcontatcLinkT.setModifiedBy(DestinationUtils.getCurrentUserDetails().getUserId());
			List<ProductContactLinkT> productContactDuplicates = productContactLinkTRepository.findByProductIdAndContactId(productcontatcLinkT.getProductId(), productcontatcLinkT.getContactId());
			if (productContactDuplicates.size() > 0) {
				throw new DestinationException(HttpStatus.BAD_REQUEST, "This Product and Contact detail already exists!!");
			} else {
			productContactLinkTRepository.save(productcontatcLinkT);
			}
		}
		return contactRepository.save(contact);
	}

	/**
	 * This method is used to retrieve all contact roles
	 * 
	 * @param page
	 * @param count
	 * @return PaginatedResponse 
	 */
	public PaginatedResponse findContactRoles(int page,
			int count)
					throws DestinationException {
		PaginatedResponse contactResponse = new PaginatedResponse();
		List<ContactRoleMappingT> contactRoleMappingTs = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();
		contactResponse.setTotalCount(contactRoleMappingTs.size());
		if (PaginationUtils.isValidPagination(page, count,
				contactRoleMappingTs.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					contactRoleMappingTs.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					contactRoleMappingTs.size()) + 1;
			contactRoleMappingTs = contactRoleMappingTs.subList(fromIndex, toIndex);
			logger.debug("ConnectT  after pagination size is "
					+ contactRoleMappingTs.size());
		} else {
			contactRoleMappingTs=null;
		}
		contactResponse.setContactRoleMappingTs(contactRoleMappingTs);
		if (contactRoleMappingTs != null && contactRoleMappingTs.size() == 0) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contact Roles found");
		}
		return contactResponse;
	}

	/**
	 * This method is used to remove all cyclic references in the contacts while retrieval
	 * @param ListOfContacts
	 */
	public void removeCyclicForLinkedContactTs(List<ContactT> contactTs) {
		if (contactTs != null) {
			for (ContactT contactT : contactTs) {
				removeCyclicForLinkedContactTs(contactT);
			}
		}
	}

	/**
	 * This method is used to remove all cyclic references in the given contact
	 * @param contactT
	 */
	public void removeCyclicForLinkedContactTs(ContactT contactT) {
		if (contactT != null) {
			if (contactT.getContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : contactT
						.getContactCustomerLinkTs()) {
					contactCustomerLinkT.getCustomerMasterT()
					.setContactCustomerLinkTs(null);
				}
			}
			if (contactT.getPartnerContactLinkTs() != null) {
				for (PartnerContactLinkT partnerContactLinkT : contactT
						.getPartnerContactLinkTs()) {
					partnerContactLinkT.getPartnerMasterT().setPartnerContactLinkTs(null);
					partnerContactLinkT.getCreatedByUser().setContactCustomerLinkTs(null);
				}
			}
			if (contactT.getProductContactLinkTs() != null) {
				for (ProductContactLinkT ProductContactLinkT : contactT
						.getProductContactLinkTs()) {
					ProductContactLinkT.getProductMasterT().setProductContactLinkTs(null);
					ProductContactLinkT.getCreatedByUser().setContactCustomerLinkTs(null);
				}
			}
		}
	}

	/**
	 * This method is used to prevent sensitive info in the contacts while retrieval
	 * @param ListOfContacts
	 */
	public void preventSensitiveInfo(List<ContactT> contactTs) {
		for (ContactT contactT : contactTs) {
			if (contactT != null) {
				preventSensitiveInfo(contactT);
			}
		}
	}

	/**
	 * This method is used to prevent sensitive info in the contact while retrieval
	 * @param contact
	 */
	public void preventSensitiveInfo(ContactT contactT) {
		if (contactT != null) {
			if (contactT.getContactType().equals(ContactType.EXTERNAL.name())
					&& contactT.getContactCategory().equalsIgnoreCase(
							EntityType.CUSTOMER.name())) {
				contactT.setContactEmailId(null);
				contactT.setContactTelephone(null);
			}
		}
	}
	/**
	 * This method is used to prepare contact object - prevent sensitive info and remove cyclic references
	 * @param contactList
	 * @throws Exception
	 */
	public void prepareContactDetails(ContactT contact,
			ArrayList<String> contactIdList) throws DestinationException {
		logger.debug("Inside prepareContactDetails() method");
		try {
			if (contactIdList == null) {
				contactIdList = new ArrayList<String>();
				contactIdList.add(contact.getContactId());
				contactIdList = getPreviledgedContactIds(DestinationUtils
						.getCurrentUserDetails().getUserId(), contactIdList,
						true);
			}
			if (contactIdList == null || contactIdList.isEmpty()
					|| (!contactIdList.contains(contact.getContactId()))) {
				preventSensitiveInfo(contact);
			}
		} catch (Exception e) {
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
	}

	/**
	 * This method is used to get list of contact with privileges
	 * @param user
	 * @param contactList
	 * @param geographyIouCheckNeeded
	 * @throws Exception
	 */
	private ArrayList<String> getPreviledgedContactIds(String userId,
			ArrayList<String> contactIdList, boolean considerGeoIou)
					throws Exception {
		logger.debug("Inside getPreviledgedCustomerName() method");
		String queryString = getContactPrevilegeQueryString(userId,
				contactIdList, considerGeoIou);
		logger.info("Query string: {}", queryString);
		Query contactQuery = entityManager.createNativeQuery(queryString);
		return (ArrayList<String>) contactQuery.getResultList();
	}

	/**
	 * This method is used to get the query for retrieving the list of contact with privileges
	 * @param user
	 * @param contactList
	 * @param geographyIouCheckNeeded
	 * @throws Exception
	 */
	private String getContactPrevilegeQueryString(String userId,
			ArrayList<String> contactIdList, boolean considerGeoIou)
					throws Exception {
		logger.debug("Inside getRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(CONACT_QUERY_PREFIX);

		HashMap<String, String> queryPrefixMap = null;

		if (considerGeoIou) {
			queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(
					CUSTOMER_GEO_COND_SUFFIX, null, CUSTOMER_IOU_COND_SUFFIX,
					null);
		}

		// Get WHERE clause string
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);

		if ((whereClause != null && !whereClause.isEmpty())
				|| (contactIdList != null && contactIdList.size() > 0)) {
			queryBuffer.append(" where ");
		}

		if (contactIdList != null && contactIdList.size() > 0) {
			String contactIdQueryList = "(";
			{
				for (String contactId : contactIdList)
					contactIdQueryList += "'" + contactId.replace("\'", "\'\'")
					+ "',";
			}
			contactIdQueryList = contactIdQueryList.substring(0,
					contactIdQueryList.length() - 1);
			contactIdQueryList += ")";

			queryBuffer.append(CONTACT_ID_COND_SUFFIX + contactIdQueryList);
		}

		if ((whereClause != null && !whereClause.isEmpty())
				&& (contactIdList != null && contactIdList.size() > 0)) {
			queryBuffer.append(Constants.AND_CLAUSE);
		}

		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(whereClause);
		}

		logger.info("queryString = " + queryBuffer.toString());
		return queryBuffer.toString();
	}

	/**
	 * This method is used to prepare contact object - prevent sensitive info and remove cyclic references
	 * @param contactList
	 * @throws Exception
	 */
	private void prepareContactDetails(List<ContactT> contactList)
			throws Exception {
		removeCyclicForLinkedContactTs(contactList);
		logger.debug("Inside prepareContactDetails() method");

		if (contactList != null && !contactList.isEmpty()) {
			ArrayList<String> contactIdList = new ArrayList<String>();
			for (ContactT contactT : contactList) {
				contactIdList.add(contactT.getContactId());
			}
			contactIdList = getPreviledgedContactIds(DestinationUtils
					.getCurrentUserDetails().getUserId(), contactIdList, true);

			for (ContactT contactT : contactList) {
				if (contactT.getContactCategory().equals(
						EntityType.CUSTOMER.name())) {
					prepareContactDetails(contactT, contactIdList);
				}
			}
		}
	}

	/**
	 * This method inserts contact to the database
	 * 
	 * @param contactToInsert
	 * @return ContactT
	 * @throws Exception
	 */
	@Transactional
	public ContactT addContact(ContactT contactToInsert) throws Exception {

		ContactT contactT = null;

		if (contactToInsert != null) {

			contactT = new ContactT();

			contactT.setContactCategory(contactToInsert.getContactCategory());
			contactT.setCreatedBy(contactToInsert.getCreatedBy());
			contactT.setModifiedBy(contactToInsert.getModifiedBy());
			contactT.setContactType(contactToInsert.getContactType());
			contactT.setEmployeeNumber(contactToInsert.getEmployeeNumber());
			contactT.setContactName(contactToInsert.getContactName());
			contactT.setContactRole(contactToInsert.getContactRole());
			contactT.setOtherRole(contactToInsert.getOtherRole());
			contactT.setContactEmailId(contactToInsert.getContactEmailId());
			contactT.setContactTelephone(contactToInsert.getContactTelephone());
			contactT.setContactLinkedinProfile(contactToInsert
					.getContactLinkedinProfile());
			contactT.getPartnerContactLinkTs().get(0).setPartnerId(contactToInsert.getPartnerContactLinkTs().get(0).getPartnerId());
			contactT = contactRepository.save(contactT);
			logger.debug("Contact Saved .... " + contactT.getContactId());

			if ((contactToInsert.getContactCustomerLinkTs() != null)
					&& (!contactToInsert.getContactCustomerLinkTs().isEmpty())) {
				logger.debug("Inside getContactCustomerLinkTs save");

				String contactId = contactT.getContactId();

				List<ContactCustomerLinkT> listOfCclt = new ArrayList<ContactCustomerLinkT>();

				for (ContactCustomerLinkT cclt : contactToInsert
						.getContactCustomerLinkTs()) {
					cclt.setContactId(contactId);
					listOfCclt.add(cclt);
				}

				listOfCclt = (List<ContactCustomerLinkT>) contactCustomerLinkTRepository
						.save(listOfCclt);

				contactT.setContactCustomerLinkTs(listOfCclt);
				logger.debug("ContactCustomerLinkTs saved...");
			}

		}

		return contactT;
	}

	/**
	 * This method deletes the Contact from the database
	 * 
	 * @param contactT
	 * @throws Exception
	 */
	@Transactional
	public void remove(ContactT contactT) throws Exception {

		if (contactT != null) {
			contactRepository.delete(contactT);
		}

	}

	/**
	 * This method saves the contact list which are retreived from spreadsheet
	 * to the database
	 * 
	 * @param contactList
	 */
	public void saveCustomerContact(List<ContactT> contactList) {
		logger.debug("Inside save method");

		Map<Integer, List<ContactCustomerLinkT>> mapContactCustomer = new HashMap<Integer, List<ContactCustomerLinkT>>(
				contactList.size());
		int i = 0;
		for (ContactT contact : contactList) {
			mapContactCustomer.put(i, contact.getContactCustomerLinkTs());
			contact.setContactCustomerLinkTs(null);
			i++;
		}
		Iterable<ContactT> savedList = contactRepository.save(contactList);
		Iterator<ContactT> saveIterator = savedList.iterator();
		i = 0;
		while (saveIterator.hasNext()) {
			ContactT contact = saveIterator.next();
			List<ContactCustomerLinkT> contactCustomerList = mapContactCustomer
					.get(i);
			if (CollectionUtils.isNotEmpty(contactCustomerList)) {
				populateContactCustomerLink(contact.getContactId(),
						contactCustomerList);
			}
			i++;
		}
		List<ContactCustomerLinkT> contactCustomerList = new ArrayList<ContactCustomerLinkT>();
		for (List<ContactCustomerLinkT> list : mapContactCustomer.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				contactCustomerList.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(contactCustomerList)) {
			contactCustomerLinkTRepository.save(contactCustomerList);
		}
	}

	/**
	 * This method sets the contact id of the contact created in contact
	 * customer link
	 * 
	 * @param contactId
	 * @param contactCustomerList
	 */
	private void populateContactCustomerLink(String contactId,
			List<ContactCustomerLinkT> contactCustomerList) {
		for (ContactCustomerLinkT contactCustomerLinkT : contactCustomerList) {
			contactCustomerLinkT.setContactId(contactId);
		}
	}


	/**
	 * method to validate the contact request to avoid duplicates
	 * 
	 * @param contact
	 */
	public boolean validateContactRequest(ContactT contact) {
		BigInteger contactCount = BigInteger.valueOf(0);

		if(contact.getContactType().equals("EXTERNAL")){
			for(int i=0; i<contact.getContactCustomerLinkTs().size();i++){
				String customerId = contact.getContactCustomerLinkTs().get(i).getCustomerId();
				contactCount = contactRepository.findDuplicateExternalContacts(contact.getContactName(),contact.getContactCategory(),contact.getContactType(), customerId, contact.getContactId());
			}
		}else {
			contactCount = contactRepository.findDuplicateInternalContact(contact.getEmployeeNumber(),contact.getContactCategory(), contact.getContactType(), contact.getContactId());

		}
		if(contactCount.intValue()>0){
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"This Contact details already exists for this customer !!!");
		}
		return validateEmail(contact);
	}

	/**
	 * method to validate the contact request to avoid duplicates contacts
	 * 
	 * @param contact
	 */
	public boolean validateCreateContactRequest(ContactT contact) {
		List<ContactT> contactList = new ArrayList<ContactT>(); 
		if(contact.getContactType().equals("EXTERNAL") && contact.getContactCategory().equals("CUSTOMER")){
			for(int i=0; i<contact.getContactCustomerLinkTs().size();i++){
				String customerId = contact.getContactCustomerLinkTs().get(i).getCustomerId();
				contactList = contactRepository.findDuplicateCustomerContacts(customerId, contact.getContactType(),contact.getContactCategory(), contact.getContactName(), contact.getContactRole());
			}
		} 
		if(contact.getContactType().equals("EXTERNAL") && contact.getContactCategory().equals("PARTNER")){
			for(int i=0; i<contact.getPartnerContactLinkTs().size();i++){
				String partnerId = contact.getPartnerContactLinkTs().get(i).getPartnerId();
				contactList = contactRepository.findDuplicatePartnerContacts(partnerId, contact.getContactType(),contact.getContactCategory(), contact.getContactName(), contact.getContactRole());
			}
		}else {
			contactList = contactRepository.findDuplicateInternalContacts(contact.getEmployeeNumber(), contact.getContactType(), contact.getContactName());
		}
		if(contactList.size()>0){
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"This Contact details already exists !!!");
		}
		return validateEmail(contact);
	}

	/**
	 * Method to validate the internal and external email address
	 * @param contact
	 */
	private boolean validateEmail(ContactT contact) {

		String email = contact.getContactEmailId();
		String tcsDomain = "tcs.com";
		boolean emailValidated = true;
		if(contact.getContactType().equalsIgnoreCase("INTERNAL") && (!email.contains(tcsDomain))){
			emailValidated = false;
			throw new DestinationException(HttpStatus.BAD_REQUEST, "This is not a valid internal(tcs.com) email address");
		}else if(contact.getContactType().equalsIgnoreCase("EXTERNAL") && (email.contains(tcsDomain))){
			emailValidated = false;
			throw new DestinationException(HttpStatus.BAD_REQUEST, "This is not a valid external email address:");
		}
		return emailValidated;
	}

	/**
	 * Find contacts by name starting with and name containing
	 * 
	 * @param contactName
	 * @param category
	 * @param type
	 * @param page
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public PaginatedResponse findContactsByName(String contactName,
			String category, String type, int page, int count) throws Exception{
		logger.debug("Inside find Contacts With Name Service");
		PaginatedResponse paginatedResponse = null;
		List<ContactT> contactList = null;
		if (contactName.equals("@%")) {
			contactList = contactRepository.findContactsStartingWithNumbers(category.toUpperCase(), type.toUpperCase());
		} else {
			contactList = contactRepository.findByContactNameAndCategoryAndType(contactName, category.toUpperCase(), type.toUpperCase());
		}
		if (contactList == null || contactList.isEmpty()) {
			// If No Contacts are found for the given search
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		paginatedResponse = new PaginatedResponse();
		paginatedResponse.setTotalCount(contactList.size());

		List<ContactT> pageContactList = paginateContacts(page, count, contactList);
		prepareContactDetails(pageContactList);
		removeCyclicContactForPartner(pageContactList);

		if (pageContactList == null || pageContactList.isEmpty()) {
			// If No Contacts are found for the given search and page
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		paginatedResponse.setContactTs(pageContactList);

		return paginatedResponse;
	}

	/**
	 * Remove Contacts inside Partner Contacts 
	 * 
	 * @param contactTs
	 */
	private void removeCyclicContactForPartner(List<ContactT> contactTs) throws Exception{

		if(contactTs!=null){
			for(ContactT contactT : contactTs){
				if (!contactT.getPartnerContactLinkTs().isEmpty()) {
					if(contactT.getPartnerContactLinkTs().get(0).getPartnerMasterT()!=null){
						if(contactT.getPartnerContactLinkTs().get(0).getContactT()!=null){
							contactT.getPartnerContactLinkTs().get(0).setContactT(null);
						}
					}
				}
			}
		}
	}

	/**
	 * This method enables pagination of the response for Contacts
	 * 
	 * @param page
	 * @param count
	 * @param contacts
	 * @return
	 */
	private List<ContactT> paginateContacts(int page, int count,
			List<ContactT> contacts) throws Exception {
		if (PaginationUtils.isValidPagination(page, count,
				contacts.size())) {
			int fromIndex = PaginationUtils.getStartIndex(page, count,
					contacts.size());
			int toIndex = PaginationUtils.getEndIndex(page, count,
					contacts.size()) + 1;
			contacts = contacts.subList(fromIndex, toIndex);
			logger.debug("ConnectT  after pagination size is "
					+ contacts.size());
		} else {
			contacts=null;
		}
		return contacts;
	}

	/**
	 * Ajax Search for Contacts
	 * 
	 * @param string
	 * @param category
	 * @param type
	 * @return
	 */
	public List<ContactT> findContactsAjaxSearch(String contactName, String category, String type) throws Exception{

		List<ContactT> contactList = null;

		contactList = contactRepository.findByContactNameAndCategoryAndType(contactName, category.toUpperCase(), type.toUpperCase());

		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}

		return contactList;
	}

	/**
	 * this method saves the contact subtables list.
	 * 
	 * @param insertList
	 */
	public void saveContacts(List<ContactT> insertList) {
		logger.debug("Inside save method");

		Map<Integer, List<PartnerContactLinkT>> mapPartnerContact = new HashMap<Integer, List<PartnerContactLinkT>>(
				insertList.size());
		Map<Integer, List<ProductContactLinkT>> mapProductContact = new HashMap<Integer, List<ProductContactLinkT>>(
				insertList.size());
		Map<Integer, List<ContactCustomerLinkT>> mapCustomerContact = new HashMap<Integer, List<ContactCustomerLinkT>>(
				insertList.size());

		int i = 0;
		for (ContactT contactT : insertList) {
			if ((contactT.getPartnerContactLinkTs() != null)
					&& (!contactT.getPartnerContactLinkTs().isEmpty()))
			{
				mapPartnerContact.put(i, contactT.getPartnerContactLinkTs());
			}

			if ((contactT.getProductContactLinkTs() != null)
					&& (!contactT.getProductContactLinkTs().isEmpty()))
			{
				mapProductContact.put(i, contactT.getProductContactLinkTs());
			}

			if ((contactT.getContactCustomerLinkTs() != null)
					&& (!contactT.getContactCustomerLinkTs().isEmpty()))
			{
				mapCustomerContact.put(i, contactT.getContactCustomerLinkTs());
			}
			setNullForReferencedObjects(contactT);
			i++;
		}

		Iterable<ContactT> savedList = contactRepository.save(insertList);
		Iterator<ContactT> saveIterator = savedList.iterator();
		System.out.println("Contacts" + insertList);
		i = 0;
		while (saveIterator.hasNext()) {
			ContactT contact = saveIterator.next();
			List<PartnerContactLinkT> partnerContactList = mapPartnerContact.get(i);
			if (CollectionUtils.isNotEmpty(partnerContactList)) {
				populatePartnerContactLinks(
						contact.getContactId(), partnerContactList);
			}
			List<ProductContactLinkT> productContactList = mapProductContact.get(i);
			if (CollectionUtils.isNotEmpty(productContactList)) {
				populateProductContactLinks(
						contact.getContactId(), productContactList);
			}
			List<ContactCustomerLinkT> contactCustomerList = mapCustomerContact.get(i);
			if (CollectionUtils.isNotEmpty(contactCustomerList)) {
				populateContactCustomerLinks(
						contact.getContactId(), contactCustomerList);
			}
			i++;
		}

		List<PartnerContactLinkT> partnerContList = new ArrayList<PartnerContactLinkT>();
		for (List<PartnerContactLinkT> list : mapPartnerContact.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				partnerContList.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(partnerContList)) {
			partnerContactLinkTRepository.save(partnerContList);

		}

		List<ProductContactLinkT> productContList = new ArrayList<ProductContactLinkT>();
		for (List<ProductContactLinkT> list : mapProductContact.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				productContList.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(productContList)) {
			productContactLinkTRepository.save(productContList);

		}

		List<ContactCustomerLinkT> customerContList = new ArrayList<ContactCustomerLinkT>();
		for (List<ContactCustomerLinkT> list : mapCustomerContact.values()) {
			if (CollectionUtils.isNotEmpty(list)) {
				customerContList.addAll(list);
			}
		}
		if (CollectionUtils.isNotEmpty(customerContList)) {
			contactCustomerLinkTRepository.save(customerContList);

		}

	}

	private void setNullForReferencedObjects(ContactT contactT) {
		logger.debug("Inside setNullForReferencedObjects() method");
		if(contactT!=null)
		{
			contactT.setPartnerContactLinkTs(null);
			contactT.setProductContactLinkTs(null);
			contactT.setContactCustomerLinkTs(null);
		}
	}

	private void populatePartnerContactLinks(String contactId,
			List<PartnerContactLinkT> partnerContactList) {
		for(PartnerContactLinkT partnerContactLinkT : partnerContactList)
		{
			partnerContactLinkT.setContactId(contactId);
		}

	}

	private void populateProductContactLinks(String contactId,
			List<ProductContactLinkT> productContactList) {
		for(ProductContactLinkT productContactLinkT : productContactList)
		{
			productContactLinkT.setContactId(contactId);
		}

	}

	private void populateContactCustomerLinks(String contactId,
			List<ContactCustomerLinkT> contactCustomerList) {
		for(ContactCustomerLinkT contactCustomerLinkT : contactCustomerList)
		{
			contactCustomerLinkT.setContactId(contactId);
		}

	}


	/**
	 * Service to fetch the contact related information based on search type and the search keyword
	 * @param smartSearchType
	 * @param term
	 * @param getAll
	 * @param page
	 * @param count
	 * @return
	 */
	public PageDTO<SearchResultDTO<ContactT>> smartSearch(SmartSearchType smartSearchType,
			String term, boolean getAll, int page, int count) {
		logger.info("ContactService::smartSearch type {}",smartSearchType);
		PageDTO<SearchResultDTO<ContactT>> res = new PageDTO<SearchResultDTO<ContactT>>();
		List<SearchResultDTO<ContactT>> resList = Lists.newArrayList();
		SearchResultDTO<ContactT> searchResultDTO = new SearchResultDTO<ContactT>();
		if(smartSearchType != null) {

			switch(smartSearchType) {
			case ALL:
				resList.add(getContactsByName(term, getAll));
				resList.add(getContactsByPartner(term, getAll));
				resList.add(getContactsByCustomer(term, getAll));
				break;
			case NAME:
				searchResultDTO = getContactsByName(term, getAll);
				break;
			case PARTNER:
				searchResultDTO = getContactsByPartner(term, getAll);
				break;
			case CUSTOMER:
				searchResultDTO = getContactsByCustomer(term, getAll);
				break;
			default:
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid search type");

			}

			if(smartSearchType != SmartSearchType.ALL) {//paginate the result if it is fetching entire record(ie. getAll=true)
				if(getAll) {
					List<ContactT> values = searchResultDTO.getValues();
					List<ContactT> records = PaginationUtils.paginateList(page, count, values);
					if(CollectionUtils.isNotEmpty(records)) {
						removeCyclicForLinkedContactTs(records);
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

	private SearchResultDTO<ContactT> getContactsByName(String term,
			boolean getAll) {
		List<ContactT> records = contactRepository.getContactsByName("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.NAME, getAll);
	}

	private SearchResultDTO<ContactT> getContactsByPartner(String term,
			boolean getAll) {
		List<ContactT> records = contactRepository.getContactsByPartner("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.PARTNER, getAll);
	}

	private SearchResultDTO<ContactT> getContactsByCustomer(String term,
			boolean getAll) {
		List<ContactT> records = contactRepository.getContactsByCustomer("%"+term+"%", getAll);
		return createSearchResultFrom(records, SmartSearchType.CUSTOMER, getAll);
	}

	/**
	 * creates {@link SearchResultDTO} from the list of contacts
	 * @param records
	 * @param type
	 * @param getAll
	 * @return
	 */
	private SearchResultDTO<ContactT> createSearchResultFrom(
			List<ContactT> records, SmartSearchType type, boolean getAll) {
		SearchResultDTO<ContactT> conRes = new SearchResultDTO<ContactT>();
		conRes.setSearchType(type);
		conRes.setValues(records);
		return conRes;
	}

}