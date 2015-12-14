package com.tcs.destination.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.Status;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ContactService;
import com.tcs.destination.service.ContactUploadService;
import com.tcs.destination.service.UploadErrorReport;
import com.tcs.destination.utils.DestinationUtils;
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

	@Autowired
	ContactUploadService contactUploadService;

	@Autowired
	UploadErrorReport uploadErrorReport;

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
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside ContactController: Start of retrieving the contact by contact id");
		try {
			ContactT contact = contactService.findById(contactId, userId);
			logger.info("Inside ContactController: End of retrieving the contact by contact id");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, contact);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the contact for the contact id "
							+ contactId);
		}
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
			@RequestParam(value = "contactType", defaultValue = "") String contactType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside ContactController: Start of retrieving the contacts by name");
		List<ContactT> contactlist = null;
		try {
			// If NameWith service
			if (!nameWith.isEmpty()) {
				contactlist = contactService.findContactsWithNameContaining(
						nameWith, customerId, partnerId, contactType, userId);
			} else if (!startsWith.isEmpty()) {
				contactlist = contactService.findContactsWithNameStarting(
						startsWith, userId);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Either nameWith / startsWith is required");
			}
			logger.info("Inside ContactController: End of retrieving the contacts by name");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, contactlist), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the contacts for namewith "
							+ nameWith);
		}
	}

	/**
	 * This method is used to get the contacts by contact type
	 * 
	 * @param customerId
	 * @param partnerId
	 * @param contactType
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/contacttype", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<String> findContactsByContactType(
			@RequestParam(value = "customerId", defaultValue = "") String customerId,
			@RequestParam(value = "partnerId", defaultValue = "") String partnerId,
			@RequestParam(value = "contactType", defaultValue = "") String contactType,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside ContactController: Start of retrieving the contacts by contact type");
		List<ContactT> contactlist = null;
		try {
			if (!customerId.isEmpty() || !partnerId.isEmpty()) {
				contactlist = contactService.findContactsByContactType(
						customerId, partnerId, contactType, userId);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Either CustomerId or PartnerId is required");
			}
			logger.info("Inside ContactController: End of retrieving the contacts by contact type");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews(fields,
							view, contactlist), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the contacts by contact type");
		}
	}

	/**
	 * This method is used to add a new contact
	 * 
	 * @param contact
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> addContact(
			@RequestBody ContactT contact) throws DestinationException {
		logger.info("Inside ContactController: Start of Adding a Contact");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (contactService.save(contact, false)) {
				status.setStatus(Status.SUCCESS, contact.getContactId());
				logger.debug("Contact Created Successfully"
						+ contact.getContactId());
			}
			logger.info("Inside ContactController: End of Adding a Contact");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while inserting the contact");
		}
	}

	/**
	 * This method is used to update the contact
	 * 
	 * @param contact
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> editContact(
			@RequestBody ContactT contact) throws DestinationException {
		logger.info("Inside ContactController: Start of Editing the Contact");
		Status status = new Status();
		status.setStatus(Status.FAILED, "");
		try {
			if (contactService.save(contact, true)) {
				status.setStatus(Status.SUCCESS, contact.getContactId());
				logger.debug("Contact Updated Successfully"
						+ contact.getContactId());
			}
			logger.info("Inside ContactController: End of Editing the Contact");
			return new ResponseEntity<String>(
					ResponseConstructors.filterJsonForFieldAndViews("all", "",
							status), HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in editing the contact");
		}

	}

	/**
	 * This method is used to get the contact role
	 * 
	 * @param fields
	 * @param view
	 * @return
	 * @throws DestinationException
	 */
	@RequestMapping(value = "/role", method = RequestMethod.GET)
	public @ResponseBody String findRole(
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		logger.info("Inside ContactController: Start of retrieving the Contact Role Mapping");
		try {
			List<ContactRoleMappingT> contactRole = contactService
					.findContactRoles();
			logger.info("Inside ContactController: End of retrieving the Contact Role Mapping");
			return ResponseConstructors.filterJsonForFieldAndViews(fields,
					view, contactRole);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error in retrieving the contact role");
		}
	}

	/**
	 * This controller uploads the Contact Sheets (Partner and Customer) to the
	 * database
	 * 
	 * @param file
	 * @param contactCategory
	 * @param fields
	 * @param view
	 * @return ResponseEntity<InputStreamResource>
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<InputStreamResource> uploadContacts(
			@RequestParam("file") MultipartFile file,
			@RequestParam("contactCategory") String contactCategory,
			@RequestParam(value = "fields", defaultValue = "all") String fields,
			@RequestParam(value = "view", defaultValue = "") String view)
			throws DestinationException {
		String userId = DestinationUtils.getCurrentUserDetails().getUserId();
		logger.info("Inside ContactController: Start of contact upload");
		List<UploadServiceErrorDetailsDTO> errorDetailsDTOs = null;
		try {
			UploadStatusDTO status = contactUploadService.upload(file, userId,
					contactCategory);
			if (status != null) {
				errorDetailsDTOs = status.getListOfErrors();
			}

			InputStreamResource excelFile = uploadErrorReport
					.getErrorSheet(errorDetailsDTOs);
			HttpHeaders respHeaders = new HttpHeaders();
			respHeaders
					.setContentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
			respHeaders.setContentDispositionFormData("attachment",
					"contact_upload_error.xlsx");
			logger.info("Inside ContactController: End of contact upload");
			return new ResponseEntity<InputStreamResource>(excelFile,
					respHeaders, HttpStatus.OK);
		} catch (DestinationException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Backend error while uploading the contacts");
		}
	}

}