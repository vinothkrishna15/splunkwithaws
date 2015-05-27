package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ContactService;
import com.tcs.destination.utils.ResponseConstructors;

/**
 * Controller to handle contact details search requests.
 * 
 */
@RestController
@RequestMapping("/contact")
public class ContactController {

	private static final Logger logger = LoggerFactory
			.getLogger(ContactController.class);

	@Autowired
	ContactService contactService;

	/**
	 * This method is used to find contact details for the given contact id.
	 * 
	 * @param id
	 *            is the contact id.
	 * @return contact details for the given contact id.
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody String findOne(
			@PathVariable("id") String contactId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ContactController /contact/id=" + contactId
				+ " GET");
		ContactT contact = contactService.findById(contactId);
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				contact);
	}

	/**
	 * This method is used to find all the Contacts with the given contact name
	 * or starting with given alphabet.
	 * 
	 * @param nameWith
	 *            is the contact name.
	 * @param startsWith
	 *            is the starting alphabet name.
	 * @return contacts.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findContactsWithName(
			@RequestParam(value = "nameWith", defaultValue = "") String nameWith,
			@RequestParam(value = "startsWith", defaultValue = "") String startsWith,
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ContactController /contact GET");
		List<ContactT> contactlist = null;

		if (!nameWith.isEmpty()) {
			contactlist = contactService.findContactsWithNameContaining(
					nameWith, customerId, partnerId);
		} else if (!startsWith.isEmpty()) {
			contactlist = contactService
					.findContactsWithNameStarting(startsWith);
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Either nameWith / startsWith is required");
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews(fields, view,
						contactlist), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addContact(
			@RequestBody ContactT contact) throws Exception {
		logger.debug("contact Insert Request Received /contact POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (contactService.save(contact, false)) {
				status.setStatus(Status.SUCCESS, contact.getContactId());
				logger.debug("Contact Created Successfully"
						+ contact.getContactId());
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editContact(
			@RequestBody ContactT contact) throws Exception {
		logger.debug("contact Insert Request Received /contact POST");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (contactService.save(contact, true)) {
				status.setStatus(Status.SUCCESS, contact.getContactId());
				logger.debug("Contact Updated Successfully"
						+ contact.getContactId());
			}
		} catch (Exception e) {
			logger.error("INTERNAL_SERVER_ERROR" + e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					e.getMessage());
		}
		return new ResponseEntity<String>(
				ResponseConstructors.filterJsonForFieldAndViews("all", "",
						status), HttpStatus.OK);
	}

	@RequestMapping(value = "/role", method = RequestMethod.GET)
	public @ResponseBody String findRole(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws Exception {
		logger.debug("Inside ContactController /contact/role GET");
		List<ContactRoleMappingT> contactRole = contactService
				.findContactRoles();
		return ResponseConstructors.filterJsonForFieldAndViews(fields, view,
				contactRole);
	}

}