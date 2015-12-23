package com.tcs.destination.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.StringUtils;

/**
 * This Helper class validates the excel data
 * by checking its constraints
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
	private  UserRepository userRepository;
	
	@Autowired
	private  ContactRepository contactRepository;
	
	@Autowired
	private  ConnectTypeRepository connectTypeRepository;
	
	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;
	
	private Map<String, String> mapOfPartnerMasterT = null;
	private List<ContactRoleMappingT> listOfContactRole = null;
	
	private static final Logger logger = LoggerFactory
			.getLogger(PartnerContactUploadHelper.class);
	
	/*
	 * This method validates the data present in Partner Contacts
	 * for constraints. It throws exceptions in case if it fails.
	 */
	public UploadServiceErrorDetailsDTO validatePartnerContactData(String[] data, String userId, ContactT partnerContactT) throws Exception 
	{
		logger.info("Begin:inside validatePartnerContactData() of PartnerContactUploadHelper");
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository.findAll();

		// PARTNER NAME
				String partnerName = data[2];
				if(!StringUtils.isEmpty(partnerName))
				{
					logger.info("partner name is",partnerName);
				}
				else
				{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("partner name Is Mandatory; ");
				}
				
				// CONTACT NAME 
				String contactName = data[3];
				if(!StringUtils.isEmpty(contactName)){
					partnerContactT.setContactName(contactName);
				}
				 else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Role NOT Found");
				}
				
				//CONTACT ROLE (Optional)
				String contactRole = data[4];
				if(!StringUtils.isEmpty(contactRole)){
					if(validateContactRole(data[4])){
						partnerContactT.setContactRole(contactRole);
					}
						else {
							throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Role");
						}
						
					} else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Role NOT Found");
					}
					
				//contact email id (Optional)
				String contactEmailid = data[5];
				if(!StringUtils.isEmpty(contactEmailid))
				{
					partnerContactT.setContactEmailId(contactEmailid);
				}
				
				//contact telephone (Optional)
				String contactTelephone = data[6];
				if(!StringUtils.isEmpty(contactTelephone))
				{
					partnerContactT.setContactTelephone(contactTelephone);
				}
				
				//contact linkedin (Optional)
				String contactLinkedIn = data[7];
				if(!StringUtils.isEmpty(contactLinkedIn))
				{
					partnerContactT.setContactLinkedinProfile(contactLinkedIn);
				}
				
				// PARTNER ID
				if(!StringUtils.isEmpty(partnerName)){
					String partnerId = getMapValuesForKey(mapOfPartnerMasterT, partnerName);
					if(!StringUtils.isEmpty(partnerId)){
						partnerContactT.setPartnerId(partnerId);
					} else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid Partner Name");
					}
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name NOT Found");
				}
				
				partnerContactT.setContactCategory("PARTNER");
				
				partnerContactT.setContactType("EXTERNAL");
				
				partnerContactT.setCreatedModifiedBy(userId);
				
				logger.info("End::inside validatePartnerContactData() of PartnerContactUploadHelper");
		return error;
	}

	
	/**
	 * This method checks if contactRole provided exists in the database  
	 * @param contactRole
	 * @return boolean
	 */
	private boolean validateContactRole(String contactRole) {

		boolean flag = false;
		
		for(ContactRoleMappingT role : listOfContactRole){
			if(contactRole.equalsIgnoreCase(role.getContactRole())){
				flag = true;
			}
		}
		return flag;
	}

	/**
     * This method retrieves the value for the key
     * 
     * @param map
     * @param key
     * @return String
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
	 * This method retrieves Customer Name and Id from CustomerMasterT
	 * 
	 * @return Map
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromPartnerMasterT()
			throws Exception {

		Map<String, String> mapOfCMT = new HashMap<String, String>();

		List<Object[]> listOfPartnerMasterT = partnerRepository
				.getPartnerNameAndId();

		for (Object[] st : listOfPartnerMasterT) {
			mapOfCMT.put(st[0].toString().trim(), st[1].toString().trim());
		}

		return mapOfCMT;
	}
}
