package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.PartnerContactLinkT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerContactLinkTRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;

/**
 * This Helper class validates the excel data by checking its constraints
 */
@Component("partnerContactUploadHelper")
public class PartnerContactUploadHelper {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PartnerRepository partnerRepository;

	@Autowired
	private SubSpRepository subSpRepository;

	@Autowired
	private OfferingRepository offeringRepository;

	@Autowired
	private TimezoneMappingRepository timeZoneMappingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private ConnectTypeRepository connectTypeRepository;

	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;

	@Autowired
	PartnerContactLinkTRepository partnerContactLinkTRepository;

	private List<ContactRoleMappingT> listOfContactRole = null;
	private Map<String, PartnerMasterT> mapOfPartnerT = null;

	private static final Logger logger = LoggerFactory
			.getLogger(PartnerContactUploadHelper.class);

	/*
	 * This method validates the data present in Partner Contacts for
	 * constraints. It throws exceptions in case if it fails.
	 */
	public UploadServiceErrorDetailsDTO validatePartnerContactData(
			String[] data, String userId, ContactT partnerContactT) throws Exception {
		
		logger.debug("Begin:inside validatePartnerContactData() of PartnerContactUploadHelper");
		
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		int rowNo = Integer.parseInt(data[0]) + 1;
		// PARTNER NAME
		String partnerName = data[3];
		if (StringUtils.isEmpty(partnerName)) {
			error.setRowNumber(rowNo);
			errorMsg.append("Partner name is mandatory; ");
		}

		// CONTACT NAME
		String contactName = data[4];
		String contactEmailId = data[6];
		if (StringUtils.isNotEmpty(contactName)) {
			if (CollectionUtils.isEmpty(contactRepository.findByContactNameAndContactEmailId(contactName, contactEmailId))) {
				partnerContactT.setContactName(contactName);
			} else {
				error.setRowNumber(rowNo);
				errorMsg.append("Contact already availble in the system; ");
			}
			
		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact name is mandatory; ");
		}

		// CONTACT ROLE (Optional)
		String contactRole = data[5];
		if (StringUtils.isNotEmpty(contactRole)) {
			if (validateContactRole(data[5])) {
				partnerContactT.setContactRole(contactRole);
			} else {
				partnerContactT.setContactRole("Other");
				partnerContactT.setOtherRole(contactRole);
			}

		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact role is mandatory; ");
		}

		// contact email id
		if (StringUtils.isNotEmpty(contactEmailId)) {
			partnerContactT.setContactEmailId(contactEmailId);
		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact email id is mandatory; ");

		}

		// contact telephone (Optional)
		String contactTelephone = data[7];
		if (StringUtils.isNotEmpty(contactTelephone)) {
			partnerContactT.setContactTelephone(contactTelephone);
		}
		// contact linkedin (Optional)
		String contactLinkedIn = data[8];
		if (StringUtils.isNotEmpty(contactLinkedIn)) {
			partnerContactT.setContactLinkedinProfile(contactLinkedIn);
		}

		// PARTNER NAMES
		if (StringUtils.isNotEmpty(partnerName)) {
			if (mapOfPartnerT == null) {
				mapOfPartnerT = getPartnerMasterT();
			}
			
			PartnerMasterT partnerMasterT = mapOfPartnerT.get(partnerName);
			if (partnerMasterT != null) {
				List<PartnerContactLinkT> partnerContactLinks = new ArrayList<PartnerContactLinkT>();
				String contactId = data[2];
				PartnerContactLinkT pclt = constructPartnerContactLinkT(
						partnerMasterT.getPartnerId(), userId, contactId);
				partnerContactLinks.add(pclt);
				
				partnerContactT.setPartnerContactLinkTs(partnerContactLinks);

			} else {
				error.setRowNumber(rowNo);
				errorMsg.append("Invalid Partner Name; ");
			}
		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Partner name not found; ");
		}

		partnerContactT.setContactCategory("PARTNER");
		partnerContactT.setContactType("EXTERNAL");
		partnerContactT.setCreatedBy(userId);
		partnerContactT.setModifiedBy(userId);

		// ACTIVE
		String active = data[9];
		if (StringUtils.isNotEmpty(active) && active.equalsIgnoreCase("false")) {
				partnerContactT.setActive(true);

		} 

		error.setMessage(errorMsg.toString());

		logger.debug("End::inside validatePartnerContactData() of PartnerContactUploadHelper");

		return error;
	}

	public UploadServiceErrorDetailsDTO validatePartnerContactUpdate(
			String[] data, String userId)
			throws Exception {
		
		logger.debug("Begin:inside validatePartnerContactData() of PartnerContactUploadHelper");
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		String contactId = data[2];
		ContactT contactT = null;
		int rowNo = Integer.parseInt(data[0]) + 1;
		if (StringUtils.isNotEmpty(contactId)) {
			contactT = contactRepository.findByContactId(contactId);
			if (contactT == null) {
				error.setRowNumber(rowNo);
				errorMsg.append("Invalid contact id; ");
			} 
		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact id is mandatory; ");
		}

		// PARTNER NAMES
		String partnerName = data[3];
		if (StringUtils.isNotEmpty(partnerName)) {
			if (mapOfPartnerT == null) {
				mapOfPartnerT = getPartnerMasterT();
			}
			PartnerMasterT partnerMasterT = mapOfPartnerT.get(partnerName);
			if (partnerMasterT == null) {
				error.setRowNumber(rowNo);
				errorMsg.append("Invalid partner name; ");
			}
		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Partner name is mandatory;");
		}

		if (StringUtils.isEmpty(data[5])) {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact role not found; ");
		}

		// contact email id
		if (StringUtils.isEmpty(data[6])) {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact email id is mandatory; ");

		}

		error.setMessage(errorMsg.toString());

		return error;
	}

	public UploadServiceErrorDetailsDTO validateContactId(String[] data,
			ContactT contact) {
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String contactId = data[2];
		int rowNo = Integer.parseInt(data[0]) + 1;
		if (StringUtils.isEmpty(contactId)) {
			error.setRowNumber(rowNo);
			error.setMessage("Contact id is mandatory; ");
		} else {
			ContactT contactT = contactRepository.findByContactId(contactId);
			if (contactT == null) {
				error.setRowNumber(rowNo);
				error.setMessage("Invalid contact id; ");
			} else {
				// ACTIVE
				contact.setActive(false);
			}
		}

		return error;

	}

	/**
	 * This method checks if contactRole provided exists in the database
	 * 
	 * @param contactRole
	 * @return boolean
	 */
	private boolean validateContactRole(String contactRole) {

		boolean flag = false;

		for (ContactRoleMappingT role : listOfContactRole) {
			if (contactRole.equalsIgnoreCase(role.getContactRole())) {
				flag = true;
			}
		}
		return flag;
	}


	/**
	 * This method is used to get the list of customer contact link for the list
	 * of customer ids
	 * 
	 * @param listOfCustomerId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private PartnerContactLinkT constructPartnerContactLinkT(
			String partnerId, String userId, String contactId)
			throws Exception {

			PartnerContactLinkT cclt = new PartnerContactLinkT();
			cclt.setCreatedBy(userId);
			cclt.setPartnerId(partnerId);
			cclt.setModifiedBy(userId);
			if (contactId != null) {
				cclt.setContactId(contactId);
			}

		return cclt;
	}

	/**
	 * Method to return partner details as map
	 * 
	 * @return Map<String, PartnerMasterT>
	 */
	public Map<String, PartnerMasterT> getPartnerMasterT() {
		List<PartnerMasterT> listOfPartnerMasterT = null;
		listOfPartnerMasterT = (List<PartnerMasterT>) partnerRepository
				.findByActiveTrue();
		Map<String, PartnerMasterT> partnerMap = new HashMap<String, PartnerMasterT>();
		for (PartnerMasterT partnerT : listOfPartnerMasterT) {
			partnerMap.put(partnerT.getPartnerName(), partnerT);
		}
		return partnerMap;
	}

	private PartnerContactLinkT constructPartnerContactLinkUpdate(
			String partnerName, String userId,
			Map<String, PartnerMasterT> mapOfPartner, ContactT contact) {
		// TODO Auto-generated method stub

		PartnerContactLinkT pslt = new PartnerContactLinkT();

		PartnerMasterT partnerMasterT = getPartnerMasterTMapValuesForKey(
				mapOfPartner, partnerName);
		pslt.setPartnerMasterT(partnerMasterT);
		pslt.setPartnerId(partnerMasterT.getPartnerId());
		pslt.setContactId(contact.getContactId());
		pslt.setCreatedBy(userId);
		pslt.setModifiedBy(userId);
		return pslt;

	}

	private PartnerMasterT getPartnerMasterTMapValuesForKey(
			Map<String, PartnerMasterT> mapOfPartner, String key) {
		PartnerMasterT partner = null;
		if (mapOfPartner.containsKey(key)) {
			partner = mapOfPartner.get(key);
		}
		return partner;
	}

	/**
	 * @param data
	 * @param userId
	 * @param contact
	 * @return
	 */
	public ContactT populatePartnerContactData(String[] data, String userId) {
		
		logger.debug("Begin:inside populatePartnerContactData() of PartnerContactUploadHelper");

		String contactId = data[2];

		ContactT contactT = contactRepository.findByContactId(contactId);

		// PARTNER NAMES
		String partnerName = data[3];
		List<PartnerContactLinkT> partnerContactLinks = contactT.getPartnerContactLinkTs();
		PartnerMasterT partnerMasterT = mapOfPartnerT.get(partnerName);
		List<PartnerContactLinkT> contactLinkTs = partnerContactLinkTRepository.findByPartnerIdAndContactId(partnerMasterT.getPartnerId(), contactT.getContactId());
		if (CollectionUtils.isEmpty(contactLinkTs)) {
			PartnerContactLinkT partnerContactLinkT = constructPartnerContactLinkUpdate(
					partnerName, userId, mapOfPartnerT, contactT);
			partnerContactLinks.add(partnerContactLinkT);
		}

		contactT.setPartnerContactLinkTs(partnerContactLinks);
		contactT.setContactName(data[4]);

		// CONTACT ROLE (Optional)
		String contactRole = data[5];
		if (validateContactRole(contactRole)) {
			contactT.setContactRole(contactRole);
		} else {
			contactT.setContactRole("Other");
			contactT.setOtherRole(contactRole);
		}

		contactT.setContactEmailId(data[6]);
		contactT.setContactTelephone(data[7]);

		// contact linkedin (Optional)
		String contactLinkedIn = data[8];
		if (StringUtils.isNotEmpty(contactLinkedIn)) {
			contactT.setContactLinkedinProfile(contactLinkedIn);
		}

		contactT.setContactCategory("PARTNER");
		contactT.setContactType("EXTERNAL");
		contactT.setModifiedBy(userId);

		// ACTIVE
		String active = data[9];
		if (StringUtils.isNotEmpty(active) && active.equalsIgnoreCase("false")) {
			contactT.setActive(false);

		}

		return contactT;
	}

}
