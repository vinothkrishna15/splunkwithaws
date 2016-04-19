package com.tcs.destination.service;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DestinationUtils;

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
		return contact;
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
	public List<ContactT> findContactsWithNameContaining(String contactName,
			String customerId, String partnerId, String contactType,
			String userId) throws Exception {
		logger.debug("Inside findContactsWithNameContaining Service");

		List<ContactT> contactList = contactRepository.findByContactName("%"
				+ contactName + "%", customerId, partnerId, contactType);
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
		return contactList;
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
	public List<ContactT> findContactsByContactType(String customerId,
			String partnerId, String contactType, String userId)
			throws Exception {
		logger.debug("Inside findContactsByContactType Service");

		List<ContactT> contactList = contactRepository.findByContactType(
				customerId, partnerId, contactType);
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
		return contactList;
	}

	/**
	 * This method is used to find all the contacts with the given starting
	 * alphabet .
	 * 
	 * @param startsWith
	 * @param userId
	 * @return contacts.
	 */
	public List<ContactT> findContactsWithNameStarting(String startsWith,
			String userId) throws Exception {
		logger.debug("Inside findContactsWithNameStarting Service");
		List<ContactT> contactList = contactRepository
				.findByContactNameIgnoreCaseStartingWithOrderByContactNameAsc(startsWith);

		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		removeCyclicForLinkedContactTs(contactList);
		prepareContactDetails(contactList);
		return contactList;
	}

	@Transactional
	public boolean save(ContactT contact, boolean isUpdate) throws Exception {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		contact.setCreatedModifiedBy(userId);
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
		} else {
			if (contact.getContactId() != null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"ContactId should not be passed");
			}
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

		if (EntityType.contains(contact.getContactCategory())) {
			if (contact.getContactCategory().equals(EntityType.CUSTOMER.name())) {
				if (contact.getContactCustomerLinkTs() == null
						|| contact.getContactCustomerLinkTs().size() == 0) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"CustomerId is required");
				}
				contact.setPartnerId(null);
			} else if (contact.getContactCategory().equals(
					EntityType.PARTNER.name())) {
				if (contact.getPartnerId() == null
						|| contact.getPartnerId().isEmpty()) {
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
				if (contact.getPartnerId() != null
						&& !(contact.getPartnerId().isEmpty())) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Internal Contact cannot be added to Partner");
				}
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

	private ContactT saveBaseContact(ContactT requestContact)
			throws CloneNotSupportedException, Exception {
		ContactT contact = requestContact.clone();
		requestContact.setContactCustomerLinkTs(null);
		contact.setContactId(contactRepository.save(requestContact)
				.getContactId());
		return contact;
	}

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
		return contactRepository.save(contact);
	}

	public List<ContactRoleMappingT> findContactRoles()
			throws DestinationException {
		List<ContactRoleMappingT> contactRoleMappingTs = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();
		if (contactRoleMappingTs != null && contactRoleMappingTs.size() == 0) {
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contact Roles found");
		}
		return contactRoleMappingTs;
	}

	public void removeCyclicForLinkedContactTs(List<ContactT> contactTs) {
		if (contactTs != null) {
			for (ContactT contactT : contactTs) {
				removeCyclicForLinkedContactTs(contactT);
			}
		}
	}

	public void removeCyclicForLinkedContactTs(ContactT contactT) {
		if (contactT != null) {
			if (contactT.getContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : contactT
						.getContactCustomerLinkTs()) {
					contactCustomerLinkT.getCustomerMasterT()
							.setContactCustomerLinkTs(null);
				}
			}
		}
	}

	public void preventSensitiveInfo(List<ContactT> contactTs) {
		for (ContactT contactT : contactTs) {
			if (contactT != null) {
				preventSensitiveInfo(contactT);
			}
		}
	}

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
			contactT.setCreatedModifiedBy(contactToInsert
					.getCreatedModifiedBy());
			contactT.setContactType(contactToInsert.getContactType());
			contactT.setEmployeeNumber(contactToInsert.getEmployeeNumber());
			contactT.setContactName(contactToInsert.getContactName());
			contactT.setContactRole(contactToInsert.getContactRole());
			contactT.setOtherRole(contactToInsert.getOtherRole());
			contactT.setContactEmailId(contactToInsert.getContactEmailId());
			contactT.setContactTelephone(contactToInsert.getContactTelephone());
			contactT.setContactLinkedinProfile(contactToInsert
					.getContactLinkedinProfile());
			contactT.setPartnerId(contactToInsert.getPartnerId());

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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		for (ContactCustomerLinkT contactCustomerLinkT : contactCustomerList) {
			contactCustomerLinkT.setContactId(contactId);
		}
	}
	

	/**
	 * method to validate the contact request to avoid duplicates
	 * @param contact
	 */
	public boolean validateContactRequest(ContactT contact) {
		// TODO Auto-generated method stub
		List<ContactT> contactList = new ArrayList<ContactT>(); 
		for(int i=0; i<contact.getContactCustomerLinkTs().size();i++){
			String customerId = contact.getContactCustomerLinkTs().get(i).getCustomerId();
			contactList = contactRepository.findDuplicateContacts(customerId, contact.getContactType(),contact.getContactCategory(), contact.getContactName(), contact.getContactRole());
		}
		if(contactList.size()>0){
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"This Contact details already exists for this customer !!!");
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



}