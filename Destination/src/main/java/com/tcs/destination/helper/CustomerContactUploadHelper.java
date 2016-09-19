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

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.CustomerRepository;

/**
 * This helper class deals with validating the data from the sheet
 * 
 * @author bnpp
 *
 */
@Component("customerContactUploadHelper")
public class CustomerContactUploadHelper {

	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	private List<ContactRoleMappingT> listOfContactRole = null;

	private Map<String, String> mapOfCustomerMasterT = null;
	private Map<String, CustomerMasterT> mapOfCustomerT = null;

	private static final Logger logger = LoggerFactory
			.getLogger(CustomerContactUploadHelper.class);

	/**
	 * This method validates the customer contact data from the sheet and
	 * returns the corresponding error
	 * 
	 * @param data
	 * @param userId
	 * @param contact
	 * @return
	 * @throws Exception
	 */
	public UploadServiceErrorDetailsDTO validateCustomerContactData(
			String[] data, String userId, ContactT contact) throws Exception {

		logger.debug("Begin:inside validateCustomerContactData() of PartnerContactUploadHelper");

		// TODO Auto-generated method stub
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		StringBuffer errorMsg = new StringBuffer();

		int rowNo = Integer.parseInt(data[0]) + 1;

		// Contact Category
		contact.setContactCategory("CUSTOMER");

		// CreatedBy
		contact.setCreatedBy(userId);

		// ModifiedBy
		contact.setModifiedBy(userId);

		// Customer Names
		String customerName = data[3];
		if (StringUtils.isEmpty(customerName)) {
			error.setRowNumber(rowNo);
			errorMsg.append("Customer name is mandatory; ");
		}

		if (StringUtils.isNotEmpty(customerName)) {
			if (mapOfCustomerT == null) {
				mapOfCustomerT = getCustomerMasterT();
			}

			CustomerMasterT customerMasterT = mapOfCustomerT.get(customerName);
			if (customerMasterT != null) {
				List<ContactCustomerLinkT> customerContactLinks = new ArrayList<ContactCustomerLinkT>();
				String contactId = data[2];
				ContactCustomerLinkT cclt = constructCustomerContactLinkT(
						customerMasterT.getCustomerId(), userId, contactId);
				customerContactLinks.add(cclt);

				contact.setContactCustomerLinkTs(customerContactLinks);

			} else {
				error.setRowNumber(rowNo);
				errorMsg.append("Invalid Customer Name; ");
			}
		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Customer name not found; ");
		}

		// Contact Type

		String contactType = data[4];
		if (!StringUtils.isEmpty(contactType)) {
			contact.setContactType(contactType);
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact Type is mandatory ");
		}

		// Employee Number
		String employeeNumber = data[5];
		if (!StringUtils.isEmpty(employeeNumber)) {
			contact.setEmployeeNumber(employeeNumber);
		}

		// CONTACT NAME
		String contactName = data[6];
		String contactEmailId = data[8];
		if (StringUtils.isNotEmpty(contactName)) {
			if (CollectionUtils.isEmpty(contactRepository
					.findByContactNameAndContactEmailId(contactName,
							contactEmailId))) {
				contact.setContactName(contactName);
			} else {
				error.setRowNumber(rowNo);
				errorMsg.append("Contact already availble in the system; ");
			}

		} else {
			error.setRowNumber(rowNo);
			errorMsg.append("Contact name is mandatory; ");
		}

		// Contact Role
		String contactRole = data[7];
		if (!StringUtils.isEmpty(contactRole)) {
			if (validateContactRole(contactRole)) {
				contact.setContactRole(contactRole);
			} else {
				contact.setContactRole("Other");
				contact.setOtherRole(contactRole);
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact role is mandatory ");
		}

		// Contact Email id

		if (!StringUtils.isEmpty(contactEmailId)) {
			contact.setContactEmailId(contactEmailId);
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact email id is mandatory ");

		}

		// Contact Telephone
		String contactTelephone = data[9];
		if ((contactTelephone != null)
				&& (!StringUtils.isEmpty(contactTelephone))) {
			contact.setContactTelephone(contactTelephone.toString());

		}
		// Contact LinkedIn profile
		String contactLinkedInProfile = data[10];
		if (!StringUtils.isEmpty(contactLinkedInProfile)) {
			contact.setContactLinkedinProfile(contactLinkedInProfile);
		}

		// ACTIVE
		String active = data[11];
		boolean activeFlag = false;
		if (!StringUtils.isEmpty(active)) {
			if (active.equalsIgnoreCase("true")) {
				activeFlag = true;
				contact.setActive(activeFlag);

			} else {
				contact.setActive(activeFlag);
			}
		}

		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		return error;
	}

	public UploadServiceErrorDetailsDTO validateContactDataUpdate(
			String[] data, String userId, ContactT contact) throws Exception {

		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();
		StringBuffer errorMsg = new StringBuffer();

		String contactId = data[2];

		if (!StringUtils.isEmpty(contactId)) {

			ContactT contactT = contactRepository.findByContactId(contactId);
			if (contactT == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Invalid Contact Id");
			}

		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact Id is mandatory");
		}

		if (contact.isActive()) {

			// CUSTOMER NAME
			String customerName = data[3];
			if (StringUtils.isNotEmpty(customerName)) {

				if (mapOfCustomerT == null) {
					mapOfCustomerT = getCustomerMasterT();
				}
				customerName = customerName.trim();
				CustomerMasterT customerMasterT = mapOfCustomerT
						.get(customerName);
				if (customerMasterT != null) {
					List<ContactCustomerLinkT> customerContactLinkTs = contact
							.getContactCustomerLinkTs();
					customerContactLinkTs = CollectionUtils
							.isNotEmpty(customerContactLinkTs) ? customerContactLinkTs
							: new ArrayList<ContactCustomerLinkT>();
					List<ContactCustomerLinkT> existingContactCustomerLinkTs = contactCustomerLinkTRepository
							.findByCustomerIdAndContactId(
									customerMasterT.getCustomerId(),
									contact.getContactId());
					if (CollectionUtils.isEmpty(existingContactCustomerLinkTs)) {
						ContactCustomerLinkT contactCustomerLinkT = constructContactCustomerLinkUpdate(
								userId, contact, customerMasterT);
						customerContactLinkTs.add(contactCustomerLinkT);
						contact.setContactCustomerLinkTs(customerContactLinkTs);
					}
				} else {
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					errorMsg.append("Invalid Customer Name; ");
				}

			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Customer Name is mandatory; ");
			}

			// Contact Type
			String contactType = data[4];
			if (!StringUtils.isEmpty(contactType)) {
				contact.setContactType(contactType);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact Type is mandatory ");
			}

			// Employee Number
			String employeeNumber = data[5];
			if (!StringUtils.isEmpty(employeeNumber)) {
				contact.setEmployeeNumber(employeeNumber);
			}

			// Contact Name
			String contactName = data[6];
			if (!StringUtils.isEmpty(contactName)) {
				contact.setContactName(contactName);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact Name is mandatory ");
			}

			// Contact Role
			String contactRole = data[7];
			if (!StringUtils.isEmpty(contactRole)) {
				if (validateContactRole(contactRole)) {
					contact.setContactRole(contactRole);
				} else {
					contact.setContactRole("Other");
					contact.setOtherRole(contactRole);
				}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact role is mandatory ");
			}

			// Contact Email id
			String contactEmailId = data[8];
			if (!StringUtils.isEmpty(contactEmailId)) {
				contact.setContactEmailId(contactEmailId);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Contact email id is mandatory ");
			}

			// Contact Telephone
			String contactTelephone = data[9];
			if ((contactTelephone != null)
					&& (!StringUtils.isEmpty(contactTelephone))) {
				Long telephoneNumber = Double.valueOf(contactTelephone)
						.longValue();
				if (telephoneNumber != null) {
					contact.setContactTelephone(telephoneNumber.toString());
				}
			}

			// Contact LinkedIn profile
			String contactLinkedInProfile = data[10];
			if (!StringUtils.isEmpty(contactLinkedInProfile)) {
				contact.setContactLinkedinProfile(contactLinkedInProfile);
			}

			// ACTIVE
			String active = data[11];
			boolean activeFlag = false;
			if (!StringUtils.isEmpty(active)) {
				if (active.equalsIgnoreCase("true")) {
					activeFlag = true;
					contact.setActive(activeFlag);

				} else {
					contact.setActive(activeFlag);
				}
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact is inactive to be updated!");
		}
		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}
		return error;

	}

	/**
	 * To delete customer contact details
	 * 
	 * @param data
	 * @param contact
	 * @return
	 */
	public UploadServiceErrorDetailsDTO validateContactId(String[] data,
			ContactT contact) {
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String contactId = data[2];
		StringBuffer errorMsg = new StringBuffer();

		if (StringUtils.isEmpty(contactId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			errorMsg.append("Contact Id Is Mandatory ");
		} else {

			ContactT contactT = contactRepository.findByContactId(contactId);
			if (contactT.getContactId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				errorMsg.append("Invalid Contact Id ");
			} else {
				// ACTIVE
				contact.setActive(false);

			}
		}
		if (!StringUtils.isEmpty(errorMsg.toString())) {
			error.setMessage(errorMsg.toString());
		}

		return error;

	}

	/**
	 * This method is used to validate the contact role by checking whether the
	 * role is available in the list of contact roles
	 * 
	 * @param contactRole
	 * @return
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
	 * This method gets the map with key and value pair of customer name and
	 * customer id respectively
	 * 
	 * @return
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromCustomerMasterT()
			throws Exception {
		Map<String, String> mapOfCMT = new HashMap<String, String>();
		List<CustomerMasterT> listOfCustomerMasterT = customerRepository
				.getNameAndId();
		for (CustomerMasterT customer : listOfCustomerMasterT) {
			mapOfCMT.put(customer.getCustomerName(), customer.getCustomerId());
		}
		return mapOfCMT;
	}

	/**
	 * Method to return customer details as map
	 * 
	 * @return Map<String, CustomerMasterT>
	 */
	public Map<String, CustomerMasterT> getCustomerMasterT() {
		List<CustomerMasterT> listOfCustomerMasterT = null;
		listOfCustomerMasterT = (List<CustomerMasterT>) customerRepository
				.findByActiveTrue();
		Map<String, CustomerMasterT> customerMap = new HashMap<String, CustomerMasterT>();
		for (CustomerMasterT customerT : listOfCustomerMasterT) {
			customerMap.put(customerT.getCustomerName(), customerT);
		}
		return customerMap;
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
	private ContactCustomerLinkT constructCustomerContactLinkT(
			String customerId, String userId, String contactId)
			throws Exception {

		ContactCustomerLinkT cclt = new ContactCustomerLinkT();
		cclt.setCreatedModifiedBy(userId);
		cclt.setCustomerId(customerId);
		if (contactId != null) {
			cclt.setContactId(contactId);
		}

		return cclt;
	}

	private ContactCustomerLinkT constructContactCustomerLinkUpdate(
			String userId, ContactT contact, CustomerMasterT customerMaster) {

		ContactCustomerLinkT cclt = new ContactCustomerLinkT();
		cclt.setCustomerMasterT(customerMaster);
		cclt.setCustomerId(customerMaster.getCustomerId());
		cclt.setContactId(contact.getContactId());
		cclt.setCreatedModifiedBy(userId);
		return cclt;

	}

}
