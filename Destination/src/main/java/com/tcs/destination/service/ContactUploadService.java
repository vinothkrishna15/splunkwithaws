package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.ContactRoleMappingTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.DocumentActionType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ContactsUploadConstants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;

/**
 * This service deals with contacts upload requests 
 * 
 */
@Service
public class ContactUploadService {
	
	@Autowired
	ContactService contactService;
	
	@Autowired
	ContactRoleMappingTRepository contactRoleMappingTRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	PartnerRepository partnerRepository;
	
	private List<ContactRoleMappingT> listOfContactRole = null;
	private List<String> listOfContactId = null;
	private Map<String, String> mapOfCustomerMasterT = null;
	private Map<String, String> mapOfPartnerMasterT = null;

	private static final Logger logger = LoggerFactory.getLogger(ContactUploadService.class);
	
	/**
	 * This service uploads contacts to contact_t table
	 * 
	 * @param file
	 * @param userId
	 */
	public UploadStatusDTO upload(MultipartFile file, String userId, String contactCategory)
			throws Exception {
		logger.debug("begin: inside upload() of ContactUploadService");
		Workbook workbook = ExcelUtils.getWorkBook(file);

		UploadStatusDTO uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());
			
		// Get List of Contact Role from DB for validating the Contact Roles which comes from the sheet	
		listOfContactRole = (List<ContactRoleMappingT>) contactRoleMappingTRepository.findAll();
		
		// Get List of Contact Id from DB for validating the Contact Ids which comes from the sheet	
		listOfContactId =  contactRepository.findContactIdFromContactT();
			
		if (contactCategory.equalsIgnoreCase(EntityType.CUSTOMER.name())) {

			// To check if no validation errors are present in the workbook
			if (validateSheetForCustomer(workbook)) { 
				
				// Get Name and Id from DB to get Id from Name of customer
				mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT(); 
				
				Sheet sheet = workbook.getSheet(ContactsUploadConstants.CUSTOMER_CONTACT_SHEET_NAME);
				if(sheet==null){
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Please upload the workbook for "+contactCategory.toUpperCase()+"  CONTACTS UPLOAD or missing "+ContactsUploadConstants.CUSTOMER_CONTACT_SHEET_NAME+" sheet");
				}

				int rowCount = 0;
				List<String> listOfCellValues = null;

				Iterator<Row> rowIterator = sheet.iterator();

				while (rowIterator.hasNext()&& rowCount <= sheet.getLastRowNum()) {

					Row row = rowIterator.next();

					if (rowCount > 0) {
						String actionCellValue = getIndividualCellValue(row.getCell(0));
						listOfCellValues = new ArrayList<String>();
						try {
						if (actionCellValue.equalsIgnoreCase(DocumentActionType.ADD.name())) {
							listOfCellValues = iterateRow(row, ContactsUploadConstants.CUSTOMER_CONTACT_SHEET_COLUMN_COUNT);
							contactService.addContact(constructContactTForCustomer(listOfCellValues, contactCategory.toUpperCase(), userId, DocumentActionType.ADD.name()));
						} else if(actionCellValue.equalsIgnoreCase(DocumentActionType.UPDATE.name())){
							listOfCellValues = iterateRow(row, ContactsUploadConstants.CUSTOMER_CONTACT_SHEET_COLUMN_COUNT);
							contactService.save(constructContactTForCustomer(listOfCellValues, contactCategory.toUpperCase(), userId, DocumentActionType.UPDATE.name()), true);
						} else if(actionCellValue.equalsIgnoreCase(DocumentActionType.DELETE.name())){
							listOfCellValues = iterateRow(row, ContactsUploadConstants.CUSTOMER_CONTACT_SHEET_COLUMN_COUNT);
							contactService.remove(constructContactTFromContactId(listOfCellValues));
						} 
					} catch (Exception e) {
						if (uploadStatus.isStatusFlag()) {
							uploadStatus.setStatusFlag(false);
						}

						UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

						error.setRowNumber(rowCount + 1);
						error.setMessage(e.getMessage());

						uploadStatus.getListOfErrors().add(error);
					}
					
				}
				rowCount++;
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, ContactsUploadConstants.VALIDATION_ERROR_MESSAGE);
		}
		} else if (contactCategory.equalsIgnoreCase(EntityType.PARTNER.name())) {
			if (validateSheetForPartner(workbook)) {
				
				// Get Name and Id from DB to get Id from Name of partner
				mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();
				
				Sheet sheet = workbook.getSheet(ContactsUploadConstants.PARTNER_CONTACT_SHEET_NAME);
				if(sheet==null){
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Please upload the workbook for "+contactCategory.toUpperCase()+" CONTACTS UPLOAD or missing "+ContactsUploadConstants.PARTNER_CONTACT_SHEET_NAME+" sheet");
				}
				
				int rowCount = 0;
				List<String> listOfCellValues = null;

				Iterator<Row> rowIterator = sheet.iterator();
				
				while (rowIterator.hasNext()&& rowCount <= sheet.getLastRowNum()) {

					Row row = rowIterator.next();

					if (rowCount > 0) {
						String actionCellValue = getIndividualCellValue(row.getCell(0));
						listOfCellValues = new ArrayList<String>();
						try {
						if (actionCellValue.equalsIgnoreCase(DocumentActionType.ADD.name())) {
							listOfCellValues = iterateRow(row, ContactsUploadConstants.PARTNER_CONTACT_SHEET_COLUMN_COUNT);
							contactService.addContact(constructContactTForPartner(listOfCellValues, contactCategory.toUpperCase(), userId, DocumentActionType.ADD.name()));
						} else if(actionCellValue.equalsIgnoreCase(DocumentActionType.UPDATE.name())){
							listOfCellValues = iterateRow(row, ContactsUploadConstants.PARTNER_CONTACT_SHEET_COLUMN_COUNT);
							contactService.save(constructContactTForPartner(listOfCellValues, contactCategory.toUpperCase(), userId, DocumentActionType.UPDATE.name()), true);
						} else if(actionCellValue.equalsIgnoreCase(DocumentActionType.DELETE.name())){
							listOfCellValues = iterateRow(row, ContactsUploadConstants.PARTNER_CONTACT_SHEET_COLUMN_COUNT);
							contactService.remove(constructContactTFromContactId(listOfCellValues));
						} 
					} catch (Exception e) {
						if (uploadStatus.isStatusFlag()) {
							uploadStatus.setStatusFlag(false);
						}

						UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();

						error.setRowNumber(rowCount + 1);
						error.setMessage(e.getMessage());

						uploadStatus.getListOfErrors().add(error);
					}

				}
				rowCount++;
			}
				
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST, ContactsUploadConstants.VALIDATION_ERROR_MESSAGE);
			}
		} else {
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Category");
		}
		logger.debug("End: inside upload() of ContactUploadService");
		return uploadStatus;
	}
	
	/**
	 * This method constructs ContactT for the given input for Partner Upload
	 * 
	 * @param listOfCellValues
	 * @param contactCategory
	 * @param userId
	 * @param action
	 * @return ContactT
	 * @throws Exception
	 */
	private ContactT constructContactTForPartner(List<String> listOfCellValues,
			String contactCategory, String userId, String action) throws Exception{

		ContactT contactT = null;
		logger.debug("begin: inside constructContactTForPartner() of ContactUploadService");
		if ((listOfCellValues.size() > 0)) {
			
			contactT = new ContactT();
		
			// Contact_id if action is update or delete
			if(action.equalsIgnoreCase(DocumentActionType.UPDATE.name())){
				if(StringUtils.isEmpty(listOfCellValues.get(1))){
					throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Id NOT Found");
				} else {
					if(validateContactId(listOfCellValues.get(1))){
						contactT.setContactId(listOfCellValues.get(1));
					}
				}
			}

			// CONTACT CATEGORY
			contactT.setContactCategory(contactCategory);
			
			// CREATED_MODIFIED_BY
			contactT.setCreatedModifiedBy(userId);
			
			// CONTACT_TYPE
			contactT.setContactType(ContactType.EXTERNAL.name());
			
			// CONTACT_NAME
			if(!StringUtils.isEmpty(listOfCellValues.get(3))){
				contactT.setContactName(listOfCellValues.get(3));
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Name NOT Found");
			}
			
			// CONTACT_ROLE
			if(!StringUtils.isEmpty(listOfCellValues.get(4))){
				if(validateContactRole(listOfCellValues.get(4))){
					contactT.setContactRole(listOfCellValues.get(4));
				} else {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Role");
				}
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Role NOT Found");
			}
			
			// OTHER_ROLE
			if(!StringUtils.isEmpty(listOfCellValues.get(5))){
				contactT.setOtherRole(listOfCellValues.get(5));
			}
			
			//CONTACT_EMAIL_ID (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(6))){
				contactT.setContactEmailId(listOfCellValues.get(6));
			}
			
			//CONTACT_TELEPHONE (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(7))){
				contactT.setContactTelephone(listOfCellValues.get(7));
			}
			
			//CONTACT_LINKEDIN_PROFILE (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(8))){
				contactT.setContactLinkedinProfile(listOfCellValues.get(8));
			}
			
			// PARTNER ID
			if(!StringUtils.isEmpty(listOfCellValues.get(2))){
				String partnerId = getMapValuesForKey(mapOfPartnerMasterT, listOfCellValues.get(2));
				if(!StringUtils.isEmpty(partnerId)){
					contactT.setPartnerId(partnerId);
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid Partner Name");
				}
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name NOT Found");
			}
		}
		logger.debug("End: inside constructContactTForPartner() of ContactUploadService");
		return contactT;
	}

	/**
	 * This method constructs ContactT from contactId
	 * 
	 * @param listOfCellValues
	 * @return ContactT
	 * @throws Exception
	 */
	private ContactT constructContactTFromContactId(List<String> listOfCellValues) throws Exception {

		ContactT contactT = null;
		logger.debug("Begin: inside constructContactTFromContactId() of ContactUploadService");
		if ((listOfCellValues.size() > 0)) {
			contactT = new ContactT();
			if (StringUtils.isEmpty(listOfCellValues.get(1))) {
				throw new DestinationException(HttpStatus.NOT_FOUND,"Contact Id NOT Found");
			} else {
				if(validateContactId(listOfCellValues.get(1))){
					contactT.setContactId(listOfCellValues.get(1));
				}
			}
		}
		logger.debug("End: inside constructContactTFromContactId() of ContactUploadService");
		return contactT;
	}

	/**
	 * This method constructs ContactT for Customer Contact Upload
	 * 
	 * @param listOfCellValues
	 * @param contactCategory
	 * @param userId
	 * @param action
	 * @return ContactT
	 * @throws Exception
	 */
	private ContactT constructContactTForCustomer(List<String> listOfCellValues, String contactCategory, String userId, String action) throws Exception {
		
		ContactT contactT = null; 
		logger.debug("Begin: inside constructContactTForCustomer() of ContactUploadService");
		if ((listOfCellValues.size() > 0)) {

				contactT = new ContactT();
				
				// Contact_id if action is update or delete
				if(action.equalsIgnoreCase(DocumentActionType.UPDATE.name())){
					if(StringUtils.isEmpty(listOfCellValues.get(1))){
						throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Id NOT Found");
					} else {
						if(validateContactId(listOfCellValues.get(1))){
							contactT.setContactId(listOfCellValues.get(1));
						}
					}
				}
				
				// CONTACT CATEGORY
				contactT.setContactCategory(contactCategory);
				
				// CREATED_MODIFIED_BY
				contactT.setCreatedModifiedBy(userId);
				
				// CONTACT_TYPE
				if(!StringUtils.isEmpty(listOfCellValues.get(3))){
					contactT.setContactType(listOfCellValues.get(3));
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Type NOT Found");
				}
				
				// EMPLOYEE_NUMBER	
				if(!StringUtils.isEmpty(listOfCellValues.get(4))){
					contactT.setEmployeeNumber(listOfCellValues.get(4));
				}
				
				// CONTACT_NAME
				if(!StringUtils.isEmpty(listOfCellValues.get(5))){
					contactT.setContactName(listOfCellValues.get(5));
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Name NOT Found");
				}
				
				// CONTACT_ROLE
				if(!StringUtils.isEmpty(listOfCellValues.get(6))){
					if(validateContactRole(listOfCellValues.get(6))){
						contactT.setContactRole(listOfCellValues.get(6));
					} else {
						throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Role");
					}
					
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Role NOT Found");
				}
				
				// OTHER_ROLE
				if(!StringUtils.isEmpty(listOfCellValues.get(7))){
					contactT.setOtherRole(listOfCellValues.get(7));
				}
				
				//CONTACT_EMAIL_ID (Optional)
				if(!StringUtils.isEmpty(listOfCellValues.get(8))){
					contactT.setContactEmailId(listOfCellValues.get(8));
				}
				
				//CONTACT_TELEPHONE (Optional)
				if(!StringUtils.isEmpty(listOfCellValues.get(9))){
					contactT.setContactTelephone(listOfCellValues.get(9));
				}
				
				//CONTACT_LINKEDIN_PROFILE (Optional)
				if(!StringUtils.isEmpty(listOfCellValues.get(10))){
					contactT.setContactLinkedinProfile(listOfCellValues.get(10));
				}
				
				// Customer Contact Link 
				List<String> listOfCustomerName = splitStringByCommas(listOfCellValues.get(2));
				List<String> listOfCustId = retrieveCustomerIdFromName(listOfCustomerName);
				
				List<ContactCustomerLinkT> cclt =  constructContactCustomerLinkT(listOfCustId, userId);
				contactT.setContactCustomerLinkTs(cclt);
				
		}
		logger.debug("End: inside constructContactTForCustomer() of ContactUploadService");
		return contactT;
	}

	/**
	 * This method checks if contactRole provided exists in the database  
	 * 
	 * @param contactRole
	 * @return boolean
	 */
	private boolean validateContactRole(String contactRole) {
		logger.debug("Begin: inside validateContactRole() of ContactUploadService");
		boolean flag = false;
		
		for(ContactRoleMappingT role : listOfContactRole){
			if(contactRole.equalsIgnoreCase(role.getContactRole())){
				flag = true;
			}
		}
		logger.debug("End: inside validateContactRole() of ContactUploadService");
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
		logger.debug("Begin: inside getMapValuesForKey() of ContactUploadService");
		String value = null;
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		logger.debug("End: inside getMapValuesForKey() of ContactUploadService");
		return value;
	}
	
	/**
	 * This method retrieves CustomerId from customer master table 
	 * 
	 * @param listOfCustomerName
	 * @return List<String>
	 * @throws Exception
	 */
	private List<String> retrieveCustomerIdFromName(List<String> listOfCustomerName) throws Exception{

		List<String> listOfCustId = null;
		logger.debug("Begin: inside retrieveCustomerIdFromName() of ContactUploadService");
		if((listOfCustomerName!=null)&&(!listOfCustomerName.isEmpty())){
			listOfCustId = new ArrayList<String>();
			for(String custName : listOfCustomerName){
				String custId = getMapValuesForKey(mapOfCustomerMasterT, custName);
				listOfCustId.add(custId);
			}
		}
		logger.debug("End: inside retrieveCustomerIdFromName() of ContactUploadService");
		return listOfCustId;
	}

	/**
	 * This method constructs ContactCustomerLinkT for ContactT
	 * 
	 * @param listOfCustomerId
	 * @param userId
	 * @return List<ContactCustomerLinkT>
	 * @throws Exception
	 */
	private List<ContactCustomerLinkT> constructContactCustomerLinkT(
			List<String> listOfCustomerId, String userId) throws Exception{

		List<ContactCustomerLinkT> listOfContactCustomerLinkT = null;
		logger.debug("Begin: inside constructContactCustomerLinkT() of ContactUploadService");
		if((listOfCustomerId!=null)&&(!listOfCustomerId.isEmpty())){
			
			listOfContactCustomerLinkT = new ArrayList<ContactCustomerLinkT>();
			
			for(String custId : listOfCustomerId){
				ContactCustomerLinkT cclt = new ContactCustomerLinkT();
				cclt.setCreatedModifiedBy(userId);
				if(custId!=null) {
					cclt.setCustomerId(custId);
				} else {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Customer Id");
				}
				listOfContactCustomerLinkT.add(cclt);
			}
		}		
		logger.debug("End: inside constructContactCustomerLinkT() of ContactUploadService");
		return listOfContactCustomerLinkT;
	}

	/**
	 * This method split the input data based on commas
	 * 
	 * @param customerNames
	 * @return List<String>
	 * @throws Exception
	 */
	private List<String> splitStringByCommas(String customerNames) throws Exception{

		List<String> listOfCustName = null;
		logger.debug("Begin: inside splitStringByCommas() of ContactUploadService");
		if(!StringUtils.isEmpty(customerNames)){
			
			listOfCustName = new ArrayList<String>();
			
			String[] custNameArray = customerNames.split(",");
			
			for(String custName : custNameArray){
				listOfCustName.add(custName.trim());
			}
		}
		logger.debug("End: inside splitStringByCommas() of ContactUploadService");
		return listOfCustName;
	}

	/**
	 * This method iterates the given row for values
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private List<String> iterateRow(Row row, int columnnCount) throws Exception{

		List<String> listOfCellValues = new ArrayList<String>();
		logger.debug("Begin: inside iterateRow() of ContactUploadService");
		for (int cellCount = 0; cellCount < columnnCount; cellCount++) {

			Cell cell = row.getCell(cellCount);

			String value = getIndividualCellValue(cell);

			if (value != null) {
				listOfCellValues.add(value.trim());
			}
		}
		logger.debug("End: inside iterateRow() of ContactUploadService");
		return listOfCellValues;
	}
	
	/**
	 * This method validates Customer Contact Sheet
	 * 
	 * @param workbook
	 * @return boolean
	 * @throws Exception
	 */
	private boolean validateSheetForCustomer(Workbook workbook) throws Exception {
		logger.debug("inside validateSheetForCustomer() of ContactUploadService");
		return ExcelUtils.isValidWorkbook(workbook,
			OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 4, 1)
			|| ExcelUtils.isValidWorkbook(workbook,
				OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 4, 2);
	    }
	
	/**
	 * This method validates Partner Contact Sheet
	 * 
	 * @param workbook
	 * @return boolean
	 * @throws Exception
	 */
	private boolean validateSheetForPartner(Workbook workbook) throws Exception {
		logger.debug("inside validateSheetForPartner() of ContactUploadService");
		return ExcelUtils.isValidWorkbook(workbook,
			OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 5, 1)
			|| ExcelUtils.isValidWorkbook(workbook,
				OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 5, 2);
	    }
	
	/**
     * This method accepts a cell, checks the value and returns the response.
     * The default value sent is an empty string
     * 
     * @param cell
     * @return String
     */
	private String getIndividualCellValue(Cell cell) {
		logger.debug("Begin:inside getIndividualCellValue() of ContactUploadService");
		String val = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					Date date = DateUtil
							.getJavaDate(cell.getNumericCellValue());
					String dateFmt = cell.getCellStyle().getDataFormatString();
					val = new CellDateFormatter(dateFmt).format(date);
				} else {
					val = String.valueOf(cell.getNumericCellValue()).trim();
				}
				break;
			case Cell.CELL_TYPE_STRING:
				val = String.valueOf(cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BLANK:
				val = "";
				break;
			}
		} else {
			val = "";
		}
		logger.debug("End:inside getIndividualCellValue() of ContactUploadService");
		return val;
	}
	
	/**
	 * This method retrieves Customer Name and Id from CustomerMasterT
	 * 
	 * @return Map
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromCustomerMasterT()
			throws Exception {
		logger.debug("Begin:inside getNameAndIdFromCustomerMasterT() of ContactUploadService");
		Map<String, String> mapOfCMT = new HashMap<String, String>();

		List<Object[]> listOfCustomerMasterT = customerRepository
				.getNameAndId();

		for (Object[] st : listOfCustomerMasterT) {
			mapOfCMT.put(st[0].toString().trim(), st[1].toString().trim());
		}
		logger.debug("End:inside getNameAndIdFromCustomerMasterT() of ContactUploadService");
		return mapOfCMT;

	}

	/**
	 * This method retrieves Customer Name and Id from CustomerMasterT
	 * 
	 * @return Map
	 * @throws Exception
	 */
	private Map<String, String> getNameAndIdFromPartnerMasterT()
			throws Exception {
		logger.debug("Begin:inside getNameAndIdFromPartnerMasterT() of ContactUploadService");
		Map<String, String> mapOfCMT = new HashMap<String, String>();

		List<Object[]> listOfPartnerMasterT = partnerRepository
				.getPartnerNameAndId();

		for (Object[] st : listOfPartnerMasterT) {
			mapOfCMT.put(st[0].toString().trim(), st[1].toString().trim());
		}
		logger.debug("End:inside getNameAndIdFromPartnerMasterT() of ContactUploadService");
		return mapOfCMT;

	}
	
	/**
	 * This method checks if contactId is present in the 
	 * list of ContactIds which are present in the database
	 * 
	 * @param contactId
	 * @return boolean
	 */
	private boolean validateContactId(String contactId) throws Exception{
		logger.debug("Begin:inside validateContactId() of ContactUploadService");
		boolean flag = false;
		
		if(contactId!=null){
			for(String cId : listOfContactId){
				if(cId.equalsIgnoreCase(contactId)){
					flag = true;
				}
			}
		}
		
		if(!flag){
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Contact Id");
		}
		logger.debug("End:inside validateContactId() of ContactUploadService");
		return flag;
	}
 	
}
