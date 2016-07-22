package com.tcs.destination.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
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
	
	@Autowired
	PartnerContactLinkTRepository partnerContactLinkTRepository;
	
	private Map<String, String> mapOfPartnerMasterT = null;
	private List<ContactRoleMappingT> listOfContactRole = null;
	private Map<String, PartnerMasterT> mapOfPartnerT = null;
	
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
				String partnerName = data[3];
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
				String contactName = data[4];
				if(!StringUtils.isEmpty(contactName)){
					partnerContactT.setContactName(contactName);
				}
				 else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Name NOT Found");
				}
				
				//CONTACT ROLE (Optional)
				String contactRole = data[5];
				if(!StringUtils.isEmpty(contactRole)){
					if(validateContactRole(data[5])){
						partnerContactT.setContactRole(contactRole);
					}
						else {
							throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Role");
						}
						
					} else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Role NOT Found");
					}
					
				//contact email id (Optional)
				String contactEmailid = data[6];
				if(!StringUtils.isEmpty(contactEmailid))
				{
					partnerContactT.setContactEmailId(contactEmailid);
				}
				
				//contact telephone (Optional)
				String contactTelephone = data[7];
				if((contactTelephone!=null)&&(!StringUtils.isEmpty(contactTelephone)))
				{
				 Long telephoneNumber=Double.valueOf(contactTelephone).longValue();
				 if(telephoneNumber!=null)
				 {
					partnerContactT.setContactTelephone(telephoneNumber.toString());
				 }
				}
				//contact linkedin (Optional)
				String contactLinkedIn = data[8];
				if(!StringUtils.isEmpty(contactLinkedIn))
				{
					partnerContactT.setContactLinkedinProfile(contactLinkedIn);
				}
				
				// PARTNER NAMES
				if(!StringUtils.isEmpty(partnerName)){
					String partnerId = getMapValuesForKey(mapOfPartnerMasterT, partnerName);
					if(!StringUtils.isEmpty(partnerId)&&(partnerId!=null)){
						String contactId=data[2];
						List<PartnerContactLinkT> pclt = constructPartnerContactLinkT(partnerId, userId,contactId);
						
						partnerContactT.setPartnerContactLinkTs(pclt);
						
					} else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid Partner Name");
					}
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name NOT Found");
				}
				
			    partnerContactT.setContactCategory("PARTNER");
				
				partnerContactT.setContactType("EXTERNAL");
				
				partnerContactT.setCreatedBy(userId);
				
				partnerContactT.setModifiedBy(userId);
				
				//ACTIVE
				String active=data[9];
				boolean activeFlag=false;
				if (!StringUtils.isEmpty(active)) {
				 if(active.equalsIgnoreCase("true"))
				 {
					activeFlag=true;
					partnerContactT.setActive(activeFlag);
					
				 }
				 else
				 {
					 partnerContactT.setActive(activeFlag);
				 }
				}
				logger.info("End::inside validatePartnerContactData() of PartnerContactUploadHelper");
		return error;
	}
	public UploadServiceErrorDetailsDTO  validatePartnerContactUpdate(String[] data, String userId, ContactT partnerContactT) throws Exception 
	{
		logger.info("Begin:inside validatePartnerContactData() of PartnerContactUploadHelper");
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository.findAll();
		
		String contactId=data[2];
		
		if (!StringUtils.isEmpty(contactId)) {

		ContactT contactT = contactRepository.findByContactId(contactId);
		if(contactT==null)
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Invalid Contact Id");
			
		}
		}
		else
		{
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact id is mandatory");
		}
		
		// PARTNER NAMES
		String partnerName = data[3];
		if (!StringUtils.isEmpty(partnerName)) {
			if (mapOfPartnerT == null) {
				mapOfPartnerT = getPartnerMasterT();
			}
			List<PartnerContactLinkT> deleteList = new ArrayList<PartnerContactLinkT>();
			List<PartnerContactLinkT> updateList = new ArrayList<PartnerContactLinkT>();
			List<String> partnerNamesFromExcel = new ArrayList<String>();
			partnerNamesFromExcel.addAll(Arrays.asList(partnerName.split(",")));
			List<PartnerContactLinkT> partnerContactLinkTs = partnerContactT.getPartnerContactLinkTs();
			for (PartnerContactLinkT partnerContactLinkT : partnerContactLinkTs) {
				if(partnerContactLinkT.getPartnerMasterT().isActive())
				{
				 if (!partnerNamesFromExcel.contains(partnerContactLinkT.getPartnerMasterT().getPartnerName())) {
					deleteList.add(partnerContactLinkT);
				 } else {
					updateList.add(partnerContactLinkT);
					partnerNamesFromExcel.remove(partnerContactLinkT.getPartnerMasterT().getPartnerName());
				 }
				}
			}
			if (!partnerNamesFromExcel.isEmpty()) {
				for (String parName : partnerNamesFromExcel) {

					if (mapOfPartnerT.containsKey(parName)) {
						PartnerContactLinkT partnerContactLinkT = constructPartnerContactLinkUpdate(parName, userId, mapOfPartnerT,partnerContactT);
						updateList.add(partnerContactLinkT);
					} else {
						error.setRowNumber(Integer.parseInt(data[0]) + 1);
						error.setMessage("Invalid Partner Name ");
					}
				}
			}

			partnerContactLinkTRepository.delete(deleteList);
			partnerContactT.setPartnerContactLinkTs(updateList);
		}

		
		// CONTACT NAME 
		String contactName = data[4];
		if(!StringUtils.isEmpty(contactName)){
			partnerContactT.setContactName(contactName);
		}
		 else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Name NOT Found");
		}
		
		//CONTACT ROLE (Optional)
		String contactRole = data[5];
		if(!StringUtils.isEmpty(contactRole)){
			if(validateContactRole(data[5])){
				partnerContactT.setContactRole(contactRole);
			}
				else {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Role");
				}
				
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Role NOT Found");
			}
			
		//contact email id (Optional)
		String contactEmailid = data[6];
		if(!StringUtils.isEmpty(contactEmailid))
		{
			partnerContactT.setContactEmailId(contactEmailid);
		}
		
		//contact telephone (Optional)
		String contactTelephone = data[7];
		Long telephoneNumber=Double.valueOf(contactTelephone).longValue();
		if(telephoneNumber!=null)
		{
			partnerContactT.setContactTelephone(telephoneNumber.toString());
		}
		
		//contact linkedin (Optional)
		String contactLinkedIn = data[8];
		if(!StringUtils.isEmpty(contactLinkedIn))
		{
			partnerContactT.setContactLinkedinProfile(contactLinkedIn);
		}
		
		partnerContactT.setContactCategory("PARTNER");
		
		partnerContactT.setContactType("EXTERNAL");
		
		partnerContactT.setModifiedBy(userId);
		
		//ACTIVE
		String active=data[9];
		boolean activeFlag=false;
		if (!StringUtils.isEmpty(active)) {
		 if(active.equalsIgnoreCase("true"))
		 {
			activeFlag=true;
			partnerContactT.setActive(activeFlag);
			
		 }
		 else
		 {
			 partnerContactT.setActive(activeFlag);
		 }
		}
		
		return error;
	}
	
	public UploadServiceErrorDetailsDTO validateContactId(String[] data,
			ContactT contact) {
		// TODO Auto-generated method stub
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String contactId = data[2];

		if (StringUtils.isEmpty(contactId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Contact Id is mandatory ");
		} else {
			ContactT contactT = contactRepository.findByContactId(contactId);
			if (contactT.getContactId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Contact Id ");
			}
			else
			{
				//ACTIVE
				contact.setActive(false);
				
			}
		}

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
	
	/**
	 * This method is used to get the list of customer contact link for the list
	 * of customer ids
	 * 
	 * @param listOfCustomerId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	private List<PartnerContactLinkT> constructPartnerContactLinkT(
			String listOfPartnerId, String userId,String contactId) throws Exception {
		List<PartnerContactLinkT> listOfPartnerContactLinkT = null;
		if ((listOfPartnerId != null) && (!listOfPartnerId.isEmpty())) {
			listOfPartnerContactLinkT = new ArrayList<PartnerContactLinkT>();
			
				PartnerContactLinkT cclt = new PartnerContactLinkT();
				cclt.setCreatedBy(userId);
				cclt.setPartnerId(listOfPartnerId);
				cclt.setModifiedBy(userId);
				if(contactId!=null)
				{
					cclt.setContactId(contactId);
				}
			
				listOfPartnerContactLinkT.add(cclt);
			
		}
		return listOfPartnerContactLinkT;
	}
	
	/**
	 * Method to return partner details as map
	 * @return  Map<String, PartnerMasterT>
	 */
	public Map<String, PartnerMasterT> getPartnerMasterT() {
		List<PartnerMasterT> listOfPartnerMasterT = null;
		listOfPartnerMasterT = (List<PartnerMasterT>) partnerRepository.findAll();
		Map<String, PartnerMasterT> partnerMap = new HashMap<String, PartnerMasterT>();
		for (PartnerMasterT partnerT : listOfPartnerMasterT) {
			partnerMap.put(partnerT.getPartnerName(), partnerT);
		}
		return partnerMap;
	}
	

	private PartnerContactLinkT constructPartnerContactLinkUpdate(String partnerName,
			String userId,  Map<String, PartnerMasterT> mapOfPartner,
			ContactT contact) {
		// TODO Auto-generated method stub
		
		PartnerContactLinkT pslt = new PartnerContactLinkT();

		PartnerMasterT partnerMasterT = getPartnerMasterTMapValuesForKey(mapOfPartner, partnerName);
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
	
}
