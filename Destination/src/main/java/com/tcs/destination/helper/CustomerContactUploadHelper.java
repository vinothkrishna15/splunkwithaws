package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.utils.StringUtils;

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

	private List<ContactRoleMappingT> listOfContactRole = null;

	private Map<String, String> mapOfCustomerMasterT = null;

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
		// TODO Auto-generated method stub
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository
				.findAll();

		mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		// Contact Category
		contact.setContactCategory("CUSTOMER");

		// CreatedModifiedBy
		contact.setCreatedModifiedBy(userId);

		// Customer Names
		String customerName = data[3];
		if (!StringUtils.isEmpty(customerName)) {
			String[] customerNameList = customerName.split(",");
			List<String> customerIds = retrieveCustomerIdFromName(customerNameList);
			if ((customerIds != null) && (!customerIds.isEmpty())) {
				List<ContactCustomerLinkT> cclt = constructContactCustomerLinkT(
						customerIds, userId);
				contact.setContactCustomerLinkTs(cclt);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Customer Name");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Customer Name is mandatory ");
		}

		// Contact Type

		String contactType = data[4];
		if (!StringUtils.isEmpty(contactType)) {
			contact.setContactType(contactType);
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact Type is mandatory ");
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
			error.setMessage("Contact Name is mandatory ");
		}

		// Contact Role
		String contactRole = data[7];
		if (!StringUtils.isEmpty(contactRole)) {
			if (validateContactRole(contactRole)) {
				contact.setContactRole(contactRole);
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Contact role ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact role is mandatory ");
		}

		// Contact Email id
		String contactEmailId = data[8];
		if (!StringUtils.isEmpty(contactEmailId)) {
			contact.setContactEmailId(contactEmailId);
		}

		// Contact Telephone
		String contactTelephone = data[9];
		if (!StringUtils.isEmpty(contactTelephone)) {
			contact.setContactTelephone(contactTelephone);
		}

		// Contact LinkedIn profile
		String contactLinkedInProfile = data[10];
		if (!StringUtils.isEmpty(contactLinkedInProfile)) {
			contact.setContactLinkedinProfile(contactLinkedInProfile);
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
		List<Object[]> listOfCustomerMasterT = customerRepository
				.getNameAndId();
		for (Object[] st : listOfCustomerMasterT) {
			mapOfCMT.put(st[0].toString().trim(), st[1].toString().trim());
		}
		return mapOfCMT;
	}

	/**
	 * This method is used to retrieve the list of customer ids for
	 * corresponding list of customer names given
	 * 
	 * @param listOfCustomerName
	 * @return
	 * @throws Exception
	 */
	private List<String> retrieveCustomerIdFromName(String[] listOfCustomerName)
			throws Exception {

		List<String> listOfCustId = null;
		if ((listOfCustomerName != null)) {
			listOfCustId = new ArrayList<String>();
			for (String custName : listOfCustomerName) {
				String custId = getMapValuesForKey(mapOfCustomerMasterT,
						custName);
				listOfCustId.add(custId);
			}
		}
		return listOfCustId;
	}

	/**
	 * This method returns the value to which the specified key is mapped
	 * 
	 * @param map
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private String getMapValuesForKey(Map<String, String> map, String key)
			throws Exception {
		String value = null;
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		return value;
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
	private List<ContactCustomerLinkT> constructContactCustomerLinkT(
			List<String> listOfCustomerId, String userId) throws Exception {
		List<ContactCustomerLinkT> listOfContactCustomerLinkT = null;
		if ((listOfCustomerId != null) && (!listOfCustomerId.isEmpty())) {
			listOfContactCustomerLinkT = new ArrayList<ContactCustomerLinkT>();
			for (String custId : listOfCustomerId) {
				ContactCustomerLinkT cclt = new ContactCustomerLinkT();
				cclt.setCreatedModifiedBy(userId);
				cclt.setCustomerId(custId);
				listOfContactCustomerLinkT.add(cclt);
			}
		}
		return listOfContactCustomerLinkT;
	}

}