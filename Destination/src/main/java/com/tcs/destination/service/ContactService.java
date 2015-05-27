package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.exception.DestinationException;

@Component
public class ContactService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContactService.class);

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;

	@Autowired
	ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	/**
	 * This method is used to find contact details for the given contact id.
	 * 
	 * @param contactId
	 * @return contact details for the given contact id.
	 */
	public ContactT findById(String contactId) throws Exception {
		logger.debug("Inside findTaskById Service");
		ContactT contact = contactRepository.findOne(contactId);

		if (contact == null) {
			logger.error("NOT_FOUND: No contact found for the ContactId");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contact found");
		}
		return contact;
	}

	/**
	 * This method is used to find all the contacts with the given contact name
	 * and/or for a specific Customer / Partner.
	 * 
	 * @param contactName
	 *            , customerId, partnerId
	 * @return contacts.
	 */
	public List<ContactT> findContactsWithNameContaining(String contactName,
			String customerId, String partnerId) throws Exception {
		logger.debug("Inside findContactsWithNameContaining Service");
		List<ContactT> contactList = contactRepository.findByContactName("%"
				+ contactName + "%", customerId, partnerId);

		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		return contactList;
	}

	/**
	 * This method is used to find all the contacts with the given starting
	 * alphabet .
	 * 
	 * @param startsWith
	 * @return contacts.
	 */
	public List<ContactT> findContactsWithNameStarting(String startsWith)
			throws Exception {
		logger.debug("Inside findContactsWithNameStarting Service");
		List<ContactT> contactList = contactRepository
				.findByContactNameIgnoreCaseStartingWithOrderByContactNameAsc(startsWith);

		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		return contactList;
	}

	@Transactional
	public boolean save(ContactT contact, boolean isUpdate) throws Exception {
		if (isUpdate) {
			if (contact.getContactId() == null)
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Cannot Update Contact without contactId.");
			if (contact.getDeleteContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : contact
						.getDeleteContactCustomerLinkTs()) {
					contactCustomerLinkTRepository.delete(contactCustomerLinkT);
				}
			} else {
				if (contact.getContactId() != null)
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Cannot Create Contact with contactId. It is auto-generated");
			}
		}

		if (ContactType.contains(contact.getContactType())) {
			logger.debug("Contact Role is Present");
			switch (ContactType.valueOf(contact.getContactType())) {
			case INTERNAL:
				if (contact.getEmployeeNumber() == null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Internal Contact must have Employee Number");
				}
				if (contact.getPartnerId() != null) {
					throw new DestinationException(HttpStatus.BAD_REQUEST,
							"Internal Contact must be a contact from Partner");
				}
			case EXTERNAL:
				ContactT overallContact = saveBaseContact(contact);
				saveOverallContact(overallContact);
				return true;
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid Contact Role");
		}
		return false;
	}

	private ContactT saveBaseContact(ContactT contact)
			throws CloneNotSupportedException {
		ContactT actualContact = contact.clone();
		contact.setContactCustomerLinkTs(null);
		actualContact.setContactId(contactRepository.save(contact)
				.getContactId());
		return actualContact;
	}

	private ContactT saveOverallContact(ContactT contact) {
		if (contact.getContactCustomerLinkTs() != null) {
			for (ContactCustomerLinkT contactCustomerLinkT : contact
					.getContactCustomerLinkTs()) {
				contactCustomerLinkT.setContactId(contact.getContactId());
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
					"No Contact Roles available");
		}
		return contactRoleMappingTs;
	}
}