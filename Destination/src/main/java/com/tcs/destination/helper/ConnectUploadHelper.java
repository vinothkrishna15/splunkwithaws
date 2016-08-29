/**
 * 
 * ConnectUploadHelper.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.ConnectOfferingLinkRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectSubSpLinkRepository;
import com.tcs.destination.data.repository.ConnectTcsAccountContactLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.service.ConnectService;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;

/**
 * This ConnectUploadHelper class holds the functionality to aid connect upload
 * 
 */
@Component("connectUploadHelper")
public class ConnectUploadHelper {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private PartnerRepository partnerRepository;

	@Autowired
	private SubSpRepository subSpRepository;

	@Autowired
	private TimezoneMappingRepository timeZoneMappingRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	ConnectSubSpLinkRepository connectSubSpLinkRepository;

	@Autowired
	ConnectCustomerContactLinkTRepository connectCustomerContactLinkTRepository;

	@Autowired
	ConnectTcsAccountContactLinkTRepository connectTcsAccountContactLinkTRepository;

	@Autowired
	ConnectRepository connectRepository;
	
	@Autowired
	ConnectOfferingLinkRepository connectOfferingLinkRepository;
	
	@Autowired
	ConnectSecondaryOwnerRepository connectSecondaryOwnerRepository;
	
	@Autowired
	private CommonHelper commonHelper;
	
	@Autowired
	ConnectService connectService;

	private Map<String, String> timeZoneMap = null;
	private Map<String, SubSpMappingT> mapOfSubSpMappingT = null;
	private Map<String, OfferingMappingT> mapOfOfferingMappingT = null;
	private Map<String, ConnectTypeMappingT> mapOfConnectTypeMappingT = null;

	public UploadServiceErrorDetailsDTO validateConnectData(String[] data,
			String userId, ConnectT connectT) throws Exception {

		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		String category = data[3];
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(category)) {

			connectT.setConnectCategory(category);

			if (category.equals(EntityType.CUSTOMER.name())) {

				// CUSTOMER
				String customerName = data[4];
				if (!StringUtils.isEmpty(customerName)) {

					CustomerMasterT customerMasterT = customerRepository
							.findByCustomerName(customerName);
					if (customerMasterT != null) {
						connectT.setCustomerId(customerMasterT.getCustomerId());
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid Customer Name; ");
					}
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Customer Name Is Mandatory; ");
				}
			} else {

				// PARTNER
				String partnername = data[4];
				if (!StringUtils.isEmpty(partnername)) {

					List<PartnerMasterT> partners = partnerRepository
							.findByPartnerName(partnername);

					if (!partners.isEmpty()) {
						connectT.setPartnerId(partners.get(0).getPartnerId());
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid Partner Name; ");
					}
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Partner Name Is Mandatory; ");
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Connect Category Is Mandatory; ");
		}

		// COUNTRY
		String country = data[5];
		if (!StringUtils.isEmpty(country)) {
			connectT.setCountry(country);
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Country Is Mandatory; ");
		}

		// CONNECT NAME
		String connectName = data[6];
		if (!StringUtils.isEmpty(connectName)) {
			connectT.setConnectName(connectName);
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Connect Name Is Mandatory; ");
		}

		// CONNECT SUBSP
		String connectSubSp = data[7];
		if (!StringUtils.isEmpty(connectSubSp)) {

			if (mapOfSubSpMappingT == null) {
				mapOfSubSpMappingT = commonHelper.getSubSpMappingT();
			}
			String[] subSps = connectSubSp.split(",");
			List<ConnectSubSpLinkT> connectSubSpList = new ArrayList<ConnectSubSpLinkT>();
			for (String subSp : subSps) {

				if (mapOfSubSpMappingT.containsKey(subSp.trim())) {
					connectSubSpList.add(constructConnectSubSpLink(
							subSp, userId, mapOfSubSpMappingT));
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Invalid SubSp; ");
				}
			}

 			connectT.setConnectSubSpLinkTs(connectSubSpList);

		}

		// CONNECT OFFERING
		String connectOffering = data[8];
		if (!StringUtils.isEmpty(connectOffering)) {

			if (mapOfOfferingMappingT == null) {
				mapOfOfferingMappingT = commonHelper.getOfferingMappingT();
			}
			String[] offerings = connectOffering.split(",");
			
			List<ConnectOfferingLinkT> connectOfferingList = new ArrayList<ConnectOfferingLinkT>();
			
			for(String offering : offerings){
				if (mapOfOfferingMappingT.containsKey(offering)) {
					connectOfferingList.add(constructConnectOfferingLink(
							offering, userId, mapOfOfferingMappingT));
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Invalid Offering; ");
				}
			}
			connectT.setConnectOfferingLinkTs(connectOfferingList);
			
		}

		// CONNECT START DATE OF CONNECT
		String startDate = data[9];
		String startTime = data[10];
		String endTime = data[11];
		if (!StringUtils.isEmpty(startDate)) {
			Date date = DateUtils.parse(startDate, DateUtils.FORMAT_DATE_WITH_SLASH);
			Date time = DateUtils.parse(startTime, DateUtils.FORMAT_HH_COLON_MM);
			connectT.setStartDatetimeOfConnect(new Timestamp(DateUtils.mergeDateWithTime(date, time).getTime()));
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("start Date Of Connect Is Mandatory; ");
		}

		// CONNECT END DATE OF CONNECT
		String endDate = data[9];
		if (!StringUtils.isEmpty(endDate)) {
			Date date = DateUtils.parse(endDate, DateUtils.FORMAT_DATE_WITH_SLASH);
			Date time = DateUtils.parse(endTime, DateUtils.FORMAT_HH_COLON_MM);
			connectT.setEndDatetimeOfConnect((new Timestamp(DateUtils.mergeDateWithTime(date, time).getTime())));
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("End Date Of Connect Is Mandatory; ");
		}

		// TIME ZONE
		String timezone = data[12];
		if (!StringUtils.isEmpty(timezone)) {

			if (timeZoneMap == null) {
				timeZoneMap = commonHelper.getTimeZoneMappingT();
			}

			if (timeZoneMap.containsKey(timezone.trim())) {
				connectT.setTimeZone(timezone.trim());
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid Timezone ");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Time Zone Is Mandatory; ");
		}

		// LOCATION
		String location = data[13];
		if (!StringUtils.isEmpty(location)) {
			connectT.setLocation(location.trim());
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Location Is Mandatory; ");
		}

		// CONNECT TYPE
		String connectType = data[14];
		if (mapOfConnectTypeMappingT == null) {
			mapOfConnectTypeMappingT = commonHelper.getConnectTypeMappingT();
		}

		if (mapOfConnectTypeMappingT.containsKey(connectType)) {
			connectT.setConnectTypeMappingT(constructConnectType(connectType,
					mapOfConnectTypeMappingT));
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Invalid Connect Type; ");
		}

		// PRIMARY OWNER
		String primaryOwner = data[15];
		if (!StringUtils.isEmpty(primaryOwner.trim())) {

			UserT userT = userRepository.findByUserName(primaryOwner);
			if (userT != null) {
				connectT.setPrimaryOwner(userT.getUserId());
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid Connect Owner; ");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Primary Owner Is Mandatory; ");
		}

		// CONNECT SECONDARY OWNER
		String connectSecondaryOwner = data[16];
		if (!StringUtils.isEmpty(connectSecondaryOwner)) {
			
			String[] secondaryOwners = connectSecondaryOwner.split(",");
			
			List<ConnectSecondaryOwnerLinkT> secondaryOwnerList = new ArrayList<ConnectSecondaryOwnerLinkT>();
			for(String secondaryOwner : secondaryOwners){
				String userIdSec = userRepository.findUserIdByUserName(secondaryOwner);
				if(StringUtils.isEmpty(userIdSec)) {
					error.setRowNumber(rowNumber);
					error.setMessage("Invalid Secondary Owner :" +secondaryOwner);
				}
				else {
					secondaryOwnerList.add(constructConnectSecondaryOwnerLink(
							userIdSec, userId));
				}
			}
			connectT.setConnectSecondaryOwnerLinkTs(secondaryOwnerList);
		}

		// CONNECT TCS ACCOUNT CONTACT
		String tcsAccContact = data[17];
		if (!StringUtils.isEmpty(tcsAccContact)) {

			List<ContactT> contacts = contactRepository
					.findByContactNames(tcsAccContact.split(","));
			if (CollectionUtils.isNotEmpty(contacts)) {
				connectT.setConnectTcsAccountContactLinkTs(constructConnectTCSContactLink(
						contacts, userId));
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid Tcs Account Contact; ");
			}
		}

		// CONNECT CUSTOMER CONTACT
		String custContacts = data[18];
		if (!StringUtils.isEmpty(custContacts)) {

			List<ContactT> custContactList = contactRepository
					.findByContactNames(custContacts.split(","));
			if(CollectionUtils.isNotEmpty(custContactList)) {
				connectT.setConnectCustomerContactLinkTs(constructConnectCustomerContactLink(
						custContactList, userId));
			}
			else {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid customer contact");
			}
			
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Connect Customer Contact Is Mandatory; ");
		}

		// CONNECT NOTES
		String connectNotes = data[19];
		if (!StringUtils.isEmpty(connectNotes)) {
			connectT.setNotesTs(constructConnectNotes(connectNotes, userId));
		}

		// CREATED BY
		connectT.setCreatedBy(userId);

		// MODIFIED BY
		connectT.setModifiedBy(userId);

		// DOCUMENTS ATTACHED
		connectT.setDocumentsAttached(Constants.NO);

		//check for inactive records and log 
		try {
			connectService.validateInactiveIndicators(connectT);
		} catch(DestinationException e) {
			error.setRowNumber(rowNumber);
			error.setMessage(e.getMessage());
		}
		
		return error;
	}

	

	private ConnectSubSpLinkT constructConnectSubSpLink(String subSp,
			String userId, Map<String, SubSpMappingT> mapOfSubSpMappingT) {

		ConnectSubSpLinkT cslt = new ConnectSubSpLinkT();

		SubSpMappingT subSpMappingT = getSubSpMappingTMapValuesForKey(
				mapOfSubSpMappingT, subSp);
		cslt.setSubSpMappingT(subSpMappingT);
		cslt.setSubSp(subSpMappingT.getSubSp());
		cslt.setCreatedBy(userId);
		cslt.setModifiedBy(userId);
		return cslt;
	}

	private SubSpMappingT getSubSpMappingTMapValuesForKey(
			Map<String, SubSpMappingT> subSpMap, String key) {
		SubSpMappingT subSp = null;
		if (subSpMap.containsKey(key)) {
			subSp = subSpMap.get(key);
		}
		return subSp;
	}

	private ConnectOfferingLinkT constructConnectOfferingLink(
			String Offering, String userId,
			Map<String, OfferingMappingT> mapOfOfferingMappingT) {
		
			
				ConnectOfferingLinkT colt = new ConnectOfferingLinkT();
				OfferingMappingT offeringMappingT = getOfferingMappingTMapValuesForKey(
						mapOfOfferingMappingT, Offering);
				colt.setOfferingMappingT(offeringMappingT);
				colt.setOffering(offeringMappingT.getOffering());
				colt.setCreatedBy(userId);
				colt.setModifiedBy(userId);
				
		return colt;
	}

	private OfferingMappingT getOfferingMappingTMapValuesForKey(
			Map<String, OfferingMappingT> offeringMap, String key) {
		OfferingMappingT offering = null;
		if (offeringMap.containsKey(key)) {
			offering = offeringMap.get(key);
		}
		return offering;
	}

	private ConnectSecondaryOwnerLinkT constructConnectSecondaryOwnerLink(
			String secondaryOwner, String userId) {

		
				ConnectSecondaryOwnerLinkT oclt = new ConnectSecondaryOwnerLinkT();
				oclt.setSecondaryOwner(secondaryOwner);
				oclt.setCreatedBy(userId);
				oclt.setModifiedBy(userId);
				
		return oclt;
	}

	private List<ConnectTcsAccountContactLinkT> constructConnectTCSContactLink(
			List<ContactT> contacts, String userId) throws Exception {

		List<ConnectTcsAccountContactLinkT> listTcsContactLinkT = new ArrayList<ConnectTcsAccountContactLinkT>();
		for (ContactT contact : contacts) {
			ConnectTcsAccountContactLinkT occlt = new ConnectTcsAccountContactLinkT();
			occlt.setContactT(contact);
			occlt.setContactId(contact.getContactId());
			occlt.setCreatedBy(userId);
			occlt.setModifiedBy(userId);
			listTcsContactLinkT.add(occlt);
		}
		return listTcsContactLinkT;
	}

	private List<ConnectCustomerContactLinkT> constructConnectCustomerContactLink(
			List<ContactT> custContacts, String userId) throws Exception {

		List<ConnectCustomerContactLinkT> listConnectCustomerLinkT = new ArrayList<ConnectCustomerContactLinkT>();
		for (ContactT contact : custContacts) {
			ConnectCustomerContactLinkT ccclt = new ConnectCustomerContactLinkT();
			ccclt.setContactT(contact);
			ccclt.setContactId(contact.getContactId());
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

	public UploadServiceErrorDetailsDTO validateConnectId(String[] data,
			ConnectT connect) {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		String connectId = data[2];

		if (StringUtils.isEmpty(connectId)) {
			error.setRowNumber(Integer.parseInt(data[0]) + 1);
			error.setMessage("Connect Id is mandatory ");
		} else {
			connect = connectRepository.findByConnectId(connectId);
			if (connect.getConnectId() == null) {
				error.setRowNumber(Integer.parseInt(data[0]) + 1);
				error.setMessage("Invalid connect Id ");
			}
		}

		return error;
	}

	public UploadServiceErrorDetailsDTO validateConnectDataUpdate(
			String[] data, String userId, ConnectT connect) throws Exception {
		UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

		// Connect
		
			
		String category = data[3];
		int rowNumber = Integer.parseInt(data[0]) + 1;
		if (!StringUtils.isEmpty(category)) {

			connect.setConnectCategory(category);

			if (category.equals(EntityType.CUSTOMER.name())) {

				// CUSTOMER
				String customerName = data[4];
				if (!StringUtils.isEmpty(customerName)) {

					CustomerMasterT customerMasterT = customerRepository
							.findByCustomerName(customerName);
					if (customerMasterT != null) {
						connect.setCustomerId(customerMasterT
								.getCustomerId());
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid Customer Name; ");
					}
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Customer Name Is Mandatory; ");
				}
			} else {

				// PARTNER
				String partnername = data[4];
				if (!StringUtils.isEmpty(partnername)) {

					List<PartnerMasterT> partners = partnerRepository
							.findByPartnerName(partnername);

					if (!partners.isEmpty()) {
						connect.setPartnerId(partners.get(0)
								.getPartnerId());
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid Partner Name; ");
					}
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Partner Name Is Mandatory; ");
				}
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Connect Category Is Mandatory; ");
		}

		// COUNTRY
		String country = data[5];
		if (!StringUtils.isEmpty(country)) {
			connect.setCountry(country);
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Country Is Mandatory; ");
		}

		// CONNECT NAME
		String connectName = data[6];
		if (!StringUtils.isEmpty(connectName)) {
			connect.setConnectName(connectName);
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Connect Name Is Mandatory; ");
		}

		// CONNECT SUBSP
		String connectSubSp = data[7];
		if (!StringUtils.isEmpty(connectSubSp)) {

			if (mapOfSubSpMappingT == null) {
				mapOfSubSpMappingT = commonHelper.getSubSpMappingT();
			}
			List<ConnectSubSpLinkT> deleteList = new ArrayList<ConnectSubSpLinkT>();
			List<ConnectSubSpLinkT> updateList = new ArrayList<ConnectSubSpLinkT>();
			List<String> subSpsFromExcel = new ArrayList<String>();
			subSpsFromExcel.addAll(Arrays.asList(connectSubSp
					.split(",")));
			List<ConnectSubSpLinkT> ConnectSubSpLinkTs = connect.getConnectSubSpLinkTs();
			for (ConnectSubSpLinkT connectSubSpLinkT : ConnectSubSpLinkTs) {
				if (!subSpsFromExcel.contains(connectSubSpLinkT
						.getSubSpMappingT().getSubSp())) {
					deleteList.add(connectSubSpLinkT);
				} else {
					updateList.add(connectSubSpLinkT);
					subSpsFromExcel.remove(connectSubSpLinkT
							.getSubSpMappingT().getSubSp());
				}
			}
			if (!subSpsFromExcel.isEmpty()) {
				for (String subSp : subSpsFromExcel) {

					if (mapOfSubSpMappingT.containsKey(subSp)) {
						ConnectSubSpLinkT connectSubSpLinkT = constructConnectSubSpLinkUpdate(
								subSp, userId, mapOfSubSpMappingT,
								connect);
						updateList.add(connectSubSpLinkT);
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid SubSp; ");
					}
				}
			}

			connectSubSpLinkRepository.delete(deleteList);
			connect.setConnectSubSpLinkTs(updateList);

		}

		// CONNECT OFFERING
		String connectOffering = data[8];
		if (!StringUtils.isEmpty(connectOffering)) {

			if (mapOfOfferingMappingT == null) {
				mapOfOfferingMappingT = commonHelper.getOfferingMappingT();
			}
			List<ConnectOfferingLinkT> deleteList = new ArrayList<ConnectOfferingLinkT>();
			List<ConnectOfferingLinkT> updateList = new ArrayList<ConnectOfferingLinkT>();

			List<String> offeringsFromExcel = new ArrayList<String>();
			offeringsFromExcel.addAll(Arrays
					.asList(connectOffering.split(",")));
			for (ConnectOfferingLinkT connectOfferingLinkT : connect
					.getConnectOfferingLinkTs()) {
				if (!offeringsFromExcel.contains(connectOfferingLinkT
						.getOfferingMappingT().getOffering())) {
					deleteList.add(connectOfferingLinkT);
				} else {
					updateList.add(connectOfferingLinkT);
					offeringsFromExcel.remove(connectOfferingLinkT
							.getOfferingMappingT().getOffering());
				}
			}
			if (!offeringsFromExcel.isEmpty()) {
				for (String offering : offeringsFromExcel) {
					if (mapOfOfferingMappingT.containsKey(offering)) {
						ConnectOfferingLinkT connectOfferingLinkT = constructConnectOfferingLinkUpdate(
								offering, userId,
								mapOfOfferingMappingT, connect);
						updateList.add(connectOfferingLinkT);
					} else {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid Offering; ");
					}
				}
			}
			connectOfferingLinkRepository.delete(deleteList);
			connect.setConnectOfferingLinkTs(updateList);
		}

		// CONNECT START DATE OF CONNECT
				String startDate = data[9];
				String startTime = data[10];
				String endTime = data[11];
				if (!StringUtils.isEmpty(startDate)) {
					Date date = DateUtils.parse(startDate, DateUtils.FORMAT_DATE_WITH_SLASH);
					Date time = DateUtils.parse(startTime, DateUtils.FORMAT_HH_COLON_MM);
					connect.setStartDatetimeOfConnect(new Timestamp(DateUtils.mergeDateWithTime(date, time).getTime()));
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("start Date Of Connect Is Mandatory; ");
				}

				// CONNECT END DATE OF CONNECT
				String endDate = data[9];
				if (!StringUtils.isEmpty(endDate)) {
					Date date = DateUtils.parse(endDate, DateUtils.FORMAT_DATE_WITH_SLASH);
					Date time = DateUtils.parse(endTime, DateUtils.FORMAT_HH_COLON_MM);
					connect.setEndDatetimeOfConnect(new Timestamp(DateUtils.mergeDateWithTime(date, time).getTime()));
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("End Date Of Connect Is Mandatory; ");
				}
		// TIME ZONE
		String timezone = data[12];
		if (!StringUtils.isEmpty(timezone)) {

			if (timeZoneMap == null) {
				timeZoneMap = commonHelper.getTimeZoneMappingT();
			}

			if (timeZoneMap.containsKey(timezone.trim())) {
				connect.setTimeZone(timezone.trim());
			}
			else {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid timezone");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Time Zone Is Mandatory; ");
		}

		// LOCATION
		String location = data[13];
		if (!StringUtils.isEmpty(location)) {
			connect.setLocation(location.trim());
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Location Is Mandatory; ");
		}

		// CONNECT TYPE
		String connectType = data[14];
		if (mapOfConnectTypeMappingT == null) {
			mapOfConnectTypeMappingT = commonHelper.getConnectTypeMappingT();
		}

		if (mapOfConnectTypeMappingT.containsKey(connectType)) {
			connect.setConnectTypeMappingT(constructConnectType(
					connectType, mapOfConnectTypeMappingT));
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Invalid Connect Type; ");
		}

		// PRIMARY OWNER
		String primaryOwner = data[15];
		if (!StringUtils.isEmpty(primaryOwner.trim())) {

			UserT userT = userRepository.findByUserName(primaryOwner);
			if (userT != null) {
				connect.setPrimaryOwner(userT.getUserId());
			} else {
				error.setRowNumber(rowNumber);
				error.setMessage("Invalid Connect Owner; ");
			}
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Primary Owner Is Mandatory; ");
		}

		// CONNECT SECONDARY OWNER
		String connectSecondaryOwner = data[16];
		if (!StringUtils.isEmpty(connectSecondaryOwner)) {

			List<ConnectSecondaryOwnerLinkT> deleteList = new ArrayList<ConnectSecondaryOwnerLinkT>();
			List<ConnectSecondaryOwnerLinkT> updateList = new ArrayList<ConnectSecondaryOwnerLinkT>();

			List<String> secondaryOwnersFromExcel = new ArrayList<String>();
			secondaryOwnersFromExcel.addAll(Arrays
					.asList(connectSecondaryOwner.split(",")));
			for (ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT : connect
					.getConnectSecondaryOwnerLinkTs()) {
				if (!secondaryOwnersFromExcel
						.contains(connectSecondaryOwnerLinkT
								.getSecondaryOwner())) {
					deleteList.add(connectSecondaryOwnerLinkT);
				} else {
					updateList.add(connectSecondaryOwnerLinkT);
					secondaryOwnersFromExcel
							.remove(connectSecondaryOwnerLinkT
									.getSecondaryOwner());
				}
			}
			if (!secondaryOwnersFromExcel.isEmpty()) {
				for (String secondaryOwner : secondaryOwnersFromExcel) {
					String userIdSec = userRepository.findUserIdByUserName(secondaryOwner);
					if(StringUtils.isEmpty(secondaryOwner)) {
						error.setRowNumber(rowNumber);
						error.setMessage("Invalid Secondary owner " +secondaryOwner);
					}
					else {
						ConnectSecondaryOwnerLinkT connectSecondaryOwnerLinkT = constructConnectSecondaryOwnerLinkUpdate(
								userIdSec, userId, connect);
						updateList.add(connectSecondaryOwnerLinkT);
					}
					

				}
			}
			connectSecondaryOwnerRepository.delete(deleteList);
			connect.setConnectSecondaryOwnerLinkTs(updateList);
		}

		// CONNECT TCS ACCOUNT CONTACT
		String tcsAccContact = data[17];
		if (!StringUtils.isEmpty(tcsAccContact)) {
			List<ConnectTcsAccountContactLinkT> deleteList = new ArrayList<ConnectTcsAccountContactLinkT>();
			List<ConnectTcsAccountContactLinkT> updateList = new ArrayList<ConnectTcsAccountContactLinkT>();
			List<String> tcsAccContactFromExcel = new ArrayList<String>();
			tcsAccContactFromExcel.addAll(Arrays
					.asList(tcsAccContact.split(",")));

			for (ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT : connect
					.getConnectTcsAccountContactLinkTs()) {
				if (!tcsAccContactFromExcel
						.contains(connectTcsAccountContactLinkT
								.getContactT().getContactName())) {
					deleteList.add(connectTcsAccountContactLinkT);
				} else {
					updateList.add(connectTcsAccountContactLinkT);
					tcsAccContactFromExcel
							.remove(connectTcsAccountContactLinkT
									.getContactT().getContactName());
				}
			}

			if (!tcsAccContactFromExcel.isEmpty()) {

				List<ContactT> contacts = contactRepository
						.findByContactNameList(tcsAccContactFromExcel);
				if (!contacts.isEmpty()) {
					for (ContactT contact : contacts) {
						ConnectTcsAccountContactLinkT connectTcsAccountContact = constructConnectTCSContactLinkUpdate(
								contact, userId, connect);
						updateList.add(connectTcsAccountContact);

					}
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Invalid Tcs Account Contact; ");
				}

			}
			connectTcsAccountContactLinkTRepository.delete(deleteList);
			connect.setConnectTcsAccountContactLinkTs(updateList);
		}

		// CONNECT CUSTOMER CONTACT
		String custContacts = data[18];
		if (!StringUtils.isEmpty(custContacts)) {
			List<ConnectCustomerContactLinkT> deleteList = new ArrayList<ConnectCustomerContactLinkT>();
			List<ConnectCustomerContactLinkT> updateList = new ArrayList<ConnectCustomerContactLinkT>();
			List<String> custContactFromExcel = new ArrayList<String>();
			custContactFromExcel.addAll(Arrays.asList(custContacts.split(",")));

			for (ConnectCustomerContactLinkT connectCustomerContactLinkT : connect
					.getConnectCustomerContactLinkTs()) {
				if (!custContactFromExcel
						.contains(connectCustomerContactLinkT
								.getContactT().getContactName())) {
					deleteList.add(connectCustomerContactLinkT);
				} else {
					updateList.add(connectCustomerContactLinkT);
					custContactFromExcel
							.remove(connectCustomerContactLinkT
									.getContactT().getContactName());
				}
			}

			if (!custContactFromExcel.isEmpty()) {

				List<ContactT> custContactList = contactRepository
						.findByContactNameList(custContactFromExcel);
				if (!custContactList.isEmpty()) {
					for (ContactT contact : custContactList) {

						ConnectCustomerContactLinkT ConnectCustomerContactLink = constructConnectCustomerContactLinkUpdate(
								contact, userId, connect);
						updateList.add(ConnectCustomerContactLink);

					}
				} else {
					error.setRowNumber(rowNumber);
					error.setMessage("Invalid Connect Customer Contact  ");
				}
			}
			connectCustomerContactLinkTRepository.delete(deleteList);
			connect.setConnectCustomerContactLinkTs(updateList);
		} else {
			error.setRowNumber(rowNumber);
			error.setMessage("Connect Customer Contact Is Mandatory; ");
		}

		// CONNECT NOTES
		String connectNotes = data[19];
		if (!StringUtils.isEmpty(connectNotes)) {
			connect.setNotesTs(constructConnectNotes(connectNotes,
					userId));
		}

		// CREATED BY
		connect.setCreatedBy(userId);

		// MODIFIED BY
		connect.setModifiedBy(userId);

		// DOCUMENTS ATTACHED
		connect.setDocumentsAttached(Constants.NO);
			
		//check for inactive records and log 
		try {
			connectService.validateInactiveIndicators(connect);
		} catch(DestinationException e) {
			error.setRowNumber(rowNumber);
			error.setMessage(e.getMessage());
		}
		
		return error;
	}

	private ConnectSecondaryOwnerLinkT constructConnectSecondaryOwnerLinkUpdate(
			String secondaryOwner, String userId, ConnectT connect) {
		
		ConnectSecondaryOwnerLinkT oclt = new ConnectSecondaryOwnerLinkT();
		oclt.setSecondaryOwner(secondaryOwner);
		oclt.setConnectId(connect.getConnectId());
		oclt.setCreatedBy(userId);
		oclt.setModifiedBy(userId);
		
return oclt;
	}

	private ConnectOfferingLinkT constructConnectOfferingLinkUpdate(
			String offering, String userId,
			Map<String, OfferingMappingT> mapOfOfferingMappingT,
			ConnectT connect) {
		ConnectOfferingLinkT colt = new ConnectOfferingLinkT();
		OfferingMappingT offeringMappingT = getOfferingMappingTMapValuesForKey(
				mapOfOfferingMappingT, offering);
		colt.setOfferingMappingT(offeringMappingT);
		colt.setOffering(offeringMappingT.getOffering());
		colt.setConnectId(connect.getConnectId());
		colt.setCreatedBy(userId);
		colt.setModifiedBy(userId);
		
return colt;
	}

	private ConnectSubSpLinkT constructConnectSubSpLinkUpdate(String subSp,
			String userId, Map<String, SubSpMappingT> mapOfSubSpMappingT,
			ConnectT connect) {
		
		ConnectSubSpLinkT cslt = new ConnectSubSpLinkT();

		SubSpMappingT subSpMappingT = getSubSpMappingTMapValuesForKey(
				mapOfSubSpMappingT, subSp);
		cslt.setSubSpMappingT(subSpMappingT);
		cslt.setSubSp(subSpMappingT.getSubSp());
		cslt.setConnectId(connect.getConnectId());
		cslt.setCreatedBy(userId);
		cslt.setModifiedBy(userId);

		return cslt;
		
	}

	private ConnectTcsAccountContactLinkT constructConnectTCSContactLinkUpdate(
			ContactT contact, String userId, ConnectT connect) {
		
			ConnectTcsAccountContactLinkT occlt = new ConnectTcsAccountContactLinkT();
			occlt.setContactT(contact);
			occlt.setContactId(contact.getContactId());
			occlt.setCreatedBy(userId);
			occlt.setModifiedBy(userId);
			occlt.setConnectId(connect.getConnectId());
			
		
		return occlt;
		
	}

	private ConnectCustomerContactLinkT constructConnectCustomerContactLinkUpdate(
			ContactT contact, String userId, ConnectT connect) {
		
			ConnectCustomerContactLinkT ccclt = new ConnectCustomerContactLinkT();
			ccclt.setContactT(contact);
			ccclt.setContactId(contact.getContactId());
			ccclt.setCreatedBy(userId);
			ccclt.setModifiedBy(userId);
			ccclt.setConnectId(connect.getConnectId());
			
		return ccclt;
		
		
	}
}
