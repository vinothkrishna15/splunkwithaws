package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.exception.ContactNotFoundException;
import com.tcs.destination.exception.DestinationException;

@Component
public class ContactService {
	
	private static final Logger logger = LoggerFactory.getLogger(ContactService.class);

	@Autowired
	ContactRepository contactRepository;

	public List<ContactT> searchforContact(String customerId, String partnerId,
			String contactName) throws Exception{
		logger.debug("Inside searchforContact Service");
		List<ContactT> contactList = contactRepository
				.findByContactNameOrCustomerIdOrPartnerId("%" + contactName
						+ "%", customerId, partnerId);
		if(contactList.isEmpty())
		{
			logger.error("NOT_FOUND: Contact information not available");
			throw new DestinationException(HttpStatus.NOT_FOUND,"Contact information not available");	
		}			
		return contactList;
	}
}
