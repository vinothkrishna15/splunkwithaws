package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class ContactService {
	
	private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

	@Autowired
	ContactRepository contactRepository;
	
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
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Contact found");
		}
		return contact;
	}

	/**
	 * This method is used to find all the contacts with the given contact name 
	 * and/or for a specific Customer / Partner.
	 * @param contactName, customerId, partnerId
	 * @return contacts.
	 */
	public List<ContactT> findContactsWithNameContaining(String contactName, String customerId, String partnerId)
			throws Exception{
		logger.debug("Inside findContactsWithNameContaining Service");
		List<ContactT> contactList = contactRepository
				.findByContactName("%" + contactName + "%", customerId, partnerId);
		
		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Contacts found");	
		}			
		return contactList;
	}

	/**
	 * This method is used to find all the contacts with the given starting alphabet .
	 * @param startsWith
	 * @return contacts.
	 */
	public List<ContactT> findContactsWithNameStarting(String startsWith)
			throws Exception{
		logger.debug("Inside findContactsWithNameStarting Service");
		List<ContactT> contactList = contactRepository
				.findByContactNameIgnoreCaseStartingWithOrderByContactNameAsc(startsWith );
		
		if (contactList == null || contactList.isEmpty()) {
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND, "No Contacts found");	
		}			
		return contactList;
	}

}
