package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.exception.ContactNotFoundException;

@Component
public class ContactService {

	@Autowired
	ContactRepository contactRepository;

	public List<ContactT> searchforContact(String customerId, String partnerId,
			String contactName) {
		List<ContactT> contactList = contactRepository
				.findByContactNameOrCustomerIdOrPartnerId("%" + contactName
						+ "%", customerId, partnerId);
		
		if(contactList.isEmpty())
			throw new ContactNotFoundException();			
		return contactList;
	}
}
