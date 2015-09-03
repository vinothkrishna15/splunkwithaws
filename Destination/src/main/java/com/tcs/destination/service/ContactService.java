package com.tcs.destination.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;

@Service
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
		removeCyclicForLinkedContactTs(contact);
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
			String customerId, String partnerId, String contactType)
			throws Exception {
		logger.debug("Inside findContactsWithNameContaining Service");

		List<ContactT> contactList = contactRepository.findByContactName("%"
				+ contactName + "%", customerId, partnerId, contactType);

		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		removeCyclicForLinkedContactTs(contactList);
		return contactList;
	}

	/**
	 * This method is used to find all the contacts with the given contact type
	 * and/or for a specific Customer / Partner.
	 * 
	 * @param customerId
	 *            , partnerId, contactType
	 * @return contacts.
	 */
	public List<ContactT> findContactsByContactType(String customerId,
			String partnerId, String contactType) throws Exception {
		logger.debug("Inside findContactsByContactType Service");

		List<ContactT> contactList = contactRepository.findByContactType(
				customerId, partnerId, contactType);

		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Contacts found");
		}
		removeCyclicForLinkedContactTs(contactList);
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
		removeCyclicForLinkedContactTs(contactList);
		return contactList;
	}

	@Transactional
	public boolean save(ContactT contact, boolean isUpdate) throws Exception {
		if (isUpdate) {
			if (contact.getContactId() == null) {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Cannot Update Contact without contactId");
			}
			if (contact.getDeleteContactCustomerLinkTs() != null) {
				for (ContactCustomerLinkT contactCustomerLinkT : contact
						.getDeleteContactCustomerLinkTs()) {
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
		// Set Contact Customer Links
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
			if (contactT.getContactType().equals(ContactType.EXTERNAL)) {
				contactT.setContactEmailId(null);
				contactT.setContactTelephone(null);
			}
		}

	}

}