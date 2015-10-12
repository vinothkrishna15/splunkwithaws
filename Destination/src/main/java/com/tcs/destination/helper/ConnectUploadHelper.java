package com.tcs.destination.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.StringUtils;

@Component("connectUploadHelper")
public class ConnectUploadHelper {
	
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
	
	private Map<String, String> timeZoneMap=null;
	private Map<String, SubSpMappingT> mapOfSubSpMappingT = null;
	private Map<String, OfferingMappingT> mapOfOfferingMappingT = null;
	private Map<String, ConnectTypeMappingT> mapOfConnectTypeMappingT = null;
	
	
	public UploadServiceErrorDetailsDTO validateConnectData(String[] data, String userId, ConnectT connectT) throws Exception {
		
			
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		
		String category = data[3];
		if(!StringUtils.isEmpty(category)){
		
			connectT.setConnectCategory(category);
			
			if (category.equals(EntityType.CUSTOMER.name())) {
			
				//CUSTOMER
				String customerName = data[4];
				if(!StringUtils.isEmpty(customerName)){
				
					CustomerMasterT customerMasterT = customerRepository.findByCustomerName(customerName);
					if(customerMasterT != null){
						connectT.setCustomerId(customerMasterT.getCustomerId());
					} else {
						error.setRowNumber(Integer.parseInt(data[0]) + 1);
						error.setMessage("Invalid Customer Name; ");
					}
				}else{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Customer Name Is Mandatory; ");
				}
			} else {

				// PARTNER 
				String partnername = data[4];
				if(!StringUtils.isEmpty(partnername)){
					
					List<PartnerMasterT> partners = partnerRepository.findByPartnerName(partnername);
				
					if(!partners.isEmpty()){
						connectT.setPartnerId(partners.get(0).getPartnerId());
					} else {
						error.setRowNumber(Integer.parseInt(data[0]) + 1);
						error.setMessage("Invalid Partner Name; ");
					}
				}else{
					error.setRowNumber(Integer.parseInt(data[0]) + 1);
					error.setMessage("Partner Name Is Mandatory; ");
				}
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Connect Category Is Mandatory; ");
		}

		// COUNTRY
		String country = data[5];
		if(!StringUtils.isEmpty(country)){
			connectT.setCountry(country); 
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Country Is Mandatory; ");
		}
	
		// CONNECT NAME
		String connectName = data[6];
		if(!StringUtils.isEmpty(connectName)){
			connectT.setConnectName(connectName); 
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Connect Name Is Mandatory; ");
		}
		
		// CONNECT SUBSP
		String connectSubSp = data[7];
		if(!StringUtils.isEmpty(connectSubSp)) {
		
			if (mapOfSubSpMappingT == null) {
				mapOfSubSpMappingT = getSubSpMappingT();
			}
				
			if(mapOfSubSpMappingT.containsKey(connectSubSp)){
				connectT.setConnectSubSpLinkTs(constructConnectSubSpLink(connectSubSp, userId,mapOfSubSpMappingT)); 
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid SubSp; ");
			}
		}

		//CONNECT OFFERING
		String offering = data[8];
		if(!StringUtils.isEmpty(offering)){
		
			if (mapOfOfferingMappingT == null) {
				mapOfOfferingMappingT = getOfferingMappingT();
			}
			if(mapOfOfferingMappingT.containsKey(offering)){
				connectT.setConnectOfferingLinkTs(constructConnectOfferingLink(offering, userId, mapOfOfferingMappingT));
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Offering; ");
			}
		}

		// CONNECT START DATE OF CONNECT
		String startDate = data[9];
		if(!StringUtils.isEmpty(startDate)){
			Date startDateOfConnect = DateUtils.getNewTimestampFormat(startDate.trim());
			connectT.setStartDatetimeOfConnect(new Timestamp(startDateOfConnect.getTime()));
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("start Date Of Connect Is Mandatory; ");
		}
		
		// CONNECT END DATE OF CONNECT
		String endDate = data[9];
		if(!StringUtils.isEmpty(endDate)){
			Date endDateOfConnect = DateUtils.getNewTimestampFormat(endDate.trim());
			connectT.setEndDatetimeOfConnect(new Timestamp(endDateOfConnect.getTime()));
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("End Date Of Connect Is Mandatory; ");
		}

		// TIME ZONE
		String timezone = data[12];
		if(!StringUtils.isEmpty(timezone)){
			
			if (timeZoneMap == null) {
				timeZoneMap = getTimeZoneMappingT();
			}
			
			if(timeZoneMap.containsKey(timezone.trim())){
				connectT.setTimeZone(timeZoneMap.get(timezone));
			}
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Time Zone Is Mandatory; ");
			}

		// LOCATION
		String location = data[13];
		if(!StringUtils.isEmpty(location)){
			connectT.setLocation(location.trim());
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Location Is Mandatory; ");
		}
		
		// CONNECT TYPE
		String connectType = data[14];
		if (mapOfConnectTypeMappingT == null) {
			mapOfConnectTypeMappingT = getConnectTypeMappingT();
		}
		
		if(mapOfConnectTypeMappingT.containsKey(connectType)){
			connectT.setConnectTypeMappingT(constructConnectType(connectType, mapOfConnectTypeMappingT));
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Invalid Connect Type; ");
		}
		
		// PRIMARY OWNER
		String primaryOwner = data[15];
		if(!StringUtils.isEmpty(primaryOwner.trim())){
			
			UserT userT = userRepository.findByUserName(primaryOwner);
			if(userT != null){
				connectT.setPrimaryOwner(userT.getUserId());
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Connect Owner; ");
			}
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Primary Owner Is Mandatory; ");
		}

		// CONNECT SECONDARY OWNER
		String secondaryOwner = data[16];
		if(!StringUtils.isEmpty(secondaryOwner)){
			connectT.setConnectSecondaryOwnerLinkTs(constructConnectSecondaryOwnerLink(secondaryOwner, userId));
		}
		
		// CONNECT TCS ACCOUNT CONTACT
		String tcsAccContact = data[17];
		if(!StringUtils.isEmpty(tcsAccContact)){
		
			List<ContactT> contacts = contactRepository.findByContactNames(tcsAccContact.split(","));
			if(!contacts.isEmpty()){
				connectT.setConnectTcsAccountContactLinkTs(constructConnectTCSContactLink(contacts, userId));
			} else {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid Tcs Account Contact; ");
			}
		}

		// CONNECT CUSTOMER CONTACT
		String custContacts = data[18];
		if(!StringUtils.isEmpty(custContacts)){
			
			List<ContactT> custContactList = contactRepository.findByContactNames(custContacts.split(","));
		
			connectT.setConnectCustomerContactLinkTs(constructConnectCustomerContactLink(
					custContactList, userId));
		} else {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Connect Customer Contact Is Mandatory; ");
		}
		
		// CONNECT NOTES
		String connectNotes = data[19];
		if(!StringUtils.isEmpty(connectNotes)){
			connectT.setNotesTs(constructConnectNotes(connectNotes, userId));
		}


		// CREATED BY
		connectT.setCreatedBy(userId);
		
		// MODIFIED BY
		connectT.setModifiedBy(userId); 
		
		// DOCUMENTS ATTACHED
		connectT.setDocumentsAttached(Constants.NO);
			
		return error;
	}


	private Map<String, ConnectTypeMappingT> getConnectTypeMappingT() {
		List<ConnectTypeMappingT> listOfConnectTypeMappingT = null;
		listOfConnectTypeMappingT = (List<ConnectTypeMappingT>) connectTypeRepository
				.findAll();
		Map<String, ConnectTypeMappingT> connectTypeMap = new HashMap<String, ConnectTypeMappingT>();
		for (ConnectTypeMappingT connectTypeMappingT : listOfConnectTypeMappingT) {
			connectTypeMap.put(connectTypeMappingT.getType(),
					connectTypeMappingT);
		}
		return connectTypeMap;
	}
	
	private Map<String, SubSpMappingT> getSubSpMappingT() {
		List<SubSpMappingT> listOfSubSpT = null;
		listOfSubSpT = (List<SubSpMappingT>) subSpRepository.findAll();
		Map<String, SubSpMappingT> subSpMap = new HashMap<String, SubSpMappingT>();
		for (SubSpMappingT subSpT : listOfSubSpT) {
			subSpMap.put(subSpT.getSubSp().trim(), subSpT);
		}
		return subSpMap;
	}
	
	private Map<String, String> getTimeZoneMappingT() {
		List<TimeZoneMappingT> listOfTimeZoneMappingT = null;
		listOfTimeZoneMappingT = (List<TimeZoneMappingT>) timeZoneMappingRepository.findAll();
		Map<String, String> timeZomeMappingTsMap = new HashMap<String, String>();
		for (TimeZoneMappingT timeZoneMappingT : listOfTimeZoneMappingT) {
			timeZomeMappingTsMap.put(timeZoneMappingT.getTimeZoneCode(), timeZoneMappingT.getDescription());
		}
		return timeZomeMappingTsMap;
	}
	
	private List<ConnectSubSpLinkT> constructConnectSubSpLink(String values,
			String userId, Map<String, SubSpMappingT> mapOfSubSpMappingT) {

		List<ConnectSubSpLinkT> listOfOppSubSpLink = null;
		if (values != null) {
			listOfOppSubSpLink = new ArrayList<ConnectSubSpLinkT>();
			String[] valuesArray = values.split(",");
			for (String value : valuesArray) {
				ConnectSubSpLinkT cslt = new ConnectSubSpLinkT();
				SubSpMappingT subSpMappingT = getSubSpMappingTMapValuesForKey(
						mapOfSubSpMappingT, value);
				cslt.setSubSpMappingT(subSpMappingT);
				cslt.setCreatedBy(userId);
				cslt.setModifiedBy(userId);
				listOfOppSubSpLink.add(cslt);
			}
		}
		return listOfOppSubSpLink;
	}
	
	private SubSpMappingT getSubSpMappingTMapValuesForKey(
			Map<String, SubSpMappingT> subSpMap, String key) {
		SubSpMappingT subSp = null;
		if (subSpMap.containsKey(key)) {
			subSp = subSpMap.get(key);
		}
		return subSp;
	}
	
	private List<ConnectOfferingLinkT> constructConnectOfferingLink(
			String values, String userId,
			Map<String, OfferingMappingT> mapOfOfferingMappingT) {
		List<ConnectOfferingLinkT> listOfConnectOfferingLink = null;
		if (values != null) {
			listOfConnectOfferingLink = new ArrayList<ConnectOfferingLinkT>();
			String[] valuesArray = values.split(",");
			for (String value : valuesArray) {
				ConnectOfferingLinkT colt = new ConnectOfferingLinkT();
				OfferingMappingT offeringMappingT = getOfferingMappingTMapValuesForKey(
						mapOfOfferingMappingT, value.trim());
				colt.setOfferingMappingT(offeringMappingT);
				colt.setCreatedBy(userId);
				colt.setModifiedBy(userId);
				listOfConnectOfferingLink.add(colt);
			}
		}
		return listOfConnectOfferingLink;
	}
	
	private OfferingMappingT getOfferingMappingTMapValuesForKey(
			Map<String, OfferingMappingT> offeringMap, String key) {
		OfferingMappingT offering = null;
		if (offeringMap.containsKey(key)) {
			offering = offeringMap.get(key);
		}
		return offering;
	}
	
	private List<ConnectSecondaryOwnerLinkT> constructConnectSecondaryOwnerLink(
			String values, String userId) {

		List<ConnectSecondaryOwnerLinkT> listOfConnectSecOwnerLink = null;

		if (values != null) {
			listOfConnectSecOwnerLink = new ArrayList<ConnectSecondaryOwnerLinkT>();
			String[] valuesArray = values.split(",");
			for (String value : valuesArray) {
				ConnectSecondaryOwnerLinkT oclt = new ConnectSecondaryOwnerLinkT();
				oclt.setConnectSecondaryOwnerLinkId(validateAndRectifyValue(value.trim()));
				oclt.setCreatedBy(userId);
				oclt.setModifiedBy(userId);
				listOfConnectSecOwnerLink.add(oclt);
			}
		}
		return listOfConnectSecOwnerLink;
	}
	
	private String validateAndRectifyValue(String value) {
		String val = value;
		System.out.println(value.substring(value.length() - 2, value.length()));
		if (value != null) {
			if (value.substring(value.length() - 2, value.length()).equals(".0")) {
				val = value.substring(0, value.length() - 2);
			}
		}
		return val;
	}
	
	private List<ConnectTcsAccountContactLinkT> constructConnectTCSContactLink(
			List<ContactT> contacts, String userId) throws Exception {

		List<ConnectTcsAccountContactLinkT> listTcsContactLinkT = new ArrayList<ConnectTcsAccountContactLinkT>();
		for (ContactT contact : contacts) {
			ConnectTcsAccountContactLinkT occlt = new ConnectTcsAccountContactLinkT();
			occlt.setContactT(contact);
			occlt.setCreatedBy(userId);
			occlt.setModifiedBy(userId);
			listTcsContactLinkT.add(occlt);
		}
		return listTcsContactLinkT;
	}
	
	private Map<String, OfferingMappingT> getOfferingMappingT() {
		List<OfferingMappingT> listOfOfferingMappingT = null;
		listOfOfferingMappingT = (List<OfferingMappingT>) offeringRepository
				.findAll();
		Map<String, OfferingMappingT> offeringMap = new HashMap<String, OfferingMappingT>();
		for (OfferingMappingT offeringMappingT : listOfOfferingMappingT) {
			offeringMap.put(offeringMappingT.getOffering(), offeringMappingT);
		}
		return offeringMap;
	}
	
	private List<ConnectCustomerContactLinkT> constructConnectCustomerContactLink(
			List<ContactT> custContacts, String userId) throws Exception {

		List<ConnectCustomerContactLinkT> listConnectCustomerLinkT = new ArrayList<ConnectCustomerContactLinkT>();
		for (ContactT contact : custContacts) {
			ConnectCustomerContactLinkT ccclt = new ConnectCustomerContactLinkT();
			ccclt.setContactT(contact);
			ccclt.setCreatedBy(userId);
			ccclt.setModifiedBy(userId);
			listConnectCustomerLinkT.add(ccclt);
		}
		return listConnectCustomerLinkT;
	}
	
	private List<NotesT> constructConnectNotes(String values, String userId) {
		List<NotesT> notesTs = null;
		if (values != null) {
			notesTs = new ArrayList<NotesT>();
			String[] valuesArray = values.split(",");
			for (String value : valuesArray) {
				NotesT notes = new NotesT();
				notes.setEntityType("CONNECT");
				notes.setUserUpdated(userId);
				notes.setNotesUpdated(value);
				notesTs.add(notes);
			}
		}
		return notesTs;
	}
	
	private ConnectTypeMappingT constructConnectType(String connectType,
			Map<String, ConnectTypeMappingT> mapOfConnectTypeMappingT) {
		ConnectTypeMappingT connectTypeMappingT = null;
		if (connectType != null) {
			connectTypeMappingT = new ConnectTypeMappingT();
			if (mapOfConnectTypeMappingT.containsKey(connectType)) {
				connectTypeMappingT = mapOfConnectTypeMappingT.get(connectType);
			}
		}
		return connectTypeMappingT;
	}


}
