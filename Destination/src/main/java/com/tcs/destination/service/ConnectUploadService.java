package com.tcs.destination.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;
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
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.ContactTMapDTO;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.CompetitorRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectSecondaryOwnerRepository;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.OpportunityCustomerContactLinkTRepository;
import com.tcs.destination.data.repository.OpportunityPartnerLinkTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.StringUtils;

@Service
public class ConnectUploadService {

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ConnectService connectService;

	@Autowired
	OpportunityRepository opportunityRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	OpportunityPartnerLinkTRepository opportunityPartnerLinkTRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	OpportunityCustomerContactLinkTRepository opportunityCustomerContactLinkTRepository;

	@Autowired
	OpportunityService opportunityService;

	@Autowired
	CompetitorRepository competitorRepository;

	@Autowired
	ConnectSecondaryOwnerRepository connectSecondaryOwnerRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	OfferingRepository offeringRepository;

	@Autowired
	ConnectTypeRepository connectTypeRepository;

	private static final Logger logger = LoggerFactory
			.getLogger(ConnectUploadService.class);

	Map<String, String> mapOfPartnerMasterT = null;
	Map<String, String> mapOfCustomerMasterT = null;
	Map<String, String> mapOfCustomerContactT = null;
	Map<String, String> mapOfTCSContactT = null;
	Map<String, String> mapOfConnectSubSp = null;
	Map<String, ContactT> mapOfContactT = null;
	Map<String, SubSpMappingT> mapOfSubSpMappingT = null;
	Map<String, OfferingMappingT> mapOfOfferingMappingT = null;
	Map<String, ConnectTypeMappingT> mapOfConnectTypeMappingT = null;
	Map<String, ContactT> contactListMap=null;
	
	public UploadStatusDTO saveConnectDocument(MultipartFile multipartFile,
			String userId) throws Exception {
	logger.debug("Inside saveConnectDocument Service");
	File file = convert(multipartFile);
	boolean isBulkDataLoad = true;
	UploadStatusDTO uploadStatus = null;
	try {
		
		uploadStatus = new UploadStatusDTO();
	    
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());
		
		uploadStatus.setStatusFlag(true);
		
		 Workbook workbook = ExcelUtils.getWorkBook(multipartFile);
//		FileInputStream fileInputStream = new FileInputStream(file);
		// Create Workbook instance holding reference to .xlsx file
//		Workbook workbook = WorkbookFactory.create(fileInputStream);
		// Get first/desired sheet from the workbook
	
		boolean isValid = ExcelUtils.isValidWorkbook(workbook, "validate", 4, 2);
		System.out.println("IS VALID " + isValid);
		if (isValid) {
		Sheet sheet = workbook.getSheet("connect");
		int rowStart = sheet.getFirstRowNum();
		int rowEnd = sheet.getPhysicalNumberOfRows();
		System.out.println("Row Start" + rowStart);
		System.out.println("Row EWnd" + rowEnd);

		mapOfContactT = getContactTMap();
		mapOfSubSpMappingT = getSubSpMappingT();
		mapOfOfferingMappingT = getOfferingMappingT();
		mapOfConnectTypeMappingT = getConnectTypeMappingT();
		mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT();
		mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();
		ContactTMapDTO cmDTO = getNameAndIdFromContactT();
		mapOfTCSContactT = cmDTO.getMapOfTcsContactT();
		mapOfCustomerContactT = cmDTO.getMapOfCustomerContactT();
		contactListMap= getContactsList();
		rowStart++;
		int rowNo = 0;
		List<String> listOfCellValues = null;
		// Iterate through each rows one by one
		Iterator<Row> rowIterator = sheet.iterator();
		while (rowIterator.hasNext()) {
		List<String> values = null;
		// System.out.println("row " + count);
		Row row = rowIterator.next();
		boolean isRowEmpty = false;
		values = new ArrayList<String>();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			if (row.getCell(i) != null) {
				values.add(row.getCell(i).toString());
			}
			// System.out.println("VALUES SIZE "+ values.size());
			if (values.size() > 10) {
				isRowEmpty = true;
			}
		}
		if (isRowEmpty) {
			if (row != null) {
			System.out.println("ROW N_____------>>>>>" + rowNo);
			// For each row, iterate through all the columns
			if (rowNo > 0) {
		listOfCellValues = new ArrayList<String>();
		for(int cellNo=1; cellNo<row.getLastCellNum();cellNo++){
			System.out.print("cell " + cellNo);
			Cell cell = row.getCell(cellNo);
			String value = getCellValue(cell);
			// System.out.println("VALUES   ------<<<<<<<<<<<"+ value);
			if (row != null && value != null) {
				// System.out.println("VALUES   ------>>>>>>"+ value);
				listOfCellValues.add(value);
			}
		}
		System.out.print("size of list " + listOfCellValues.size());
		try{
			
		if ((listOfCellValues.size() > 0) && (listOfCellValues.size() <= 18)) {
			ConnectT connectT = new ConnectT();
			
		if(!StringUtils.isEmpty(listOfCellValues.get(0))){
		
			connectT.setConnectCategory(listOfCellValues.get(0));
			
			if (listOfCellValues.get(0).equals("CUSTOMER")) {
			
				// CUSTOMER ID
				if(!StringUtils.isEmpty(listOfCellValues.get(1))){
				
					if(mapOfCustomerMasterT.containsKey(listOfCellValues.get(1))){
						connectT.setCustomerId(getMapValuesForKey(mapOfCustomerMasterT,	listOfCellValues.get(1)));
					} else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "Customer Name Is Incorrect");
					}
				}else{
					throw new DestinationException(HttpStatus.NOT_FOUND, "Customer Name Is Mandatory");
				}
			} else {

				// PARTNER ID
				if(!StringUtils.isEmpty(listOfCellValues.get(1))){
				
					if(mapOfPartnerMasterT.containsKey(listOfCellValues.get(1))){
						connectT.setPartnerId(getMapValuesForKey(mapOfPartnerMasterT, listOfCellValues.get(1)));
					} else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name Is Incorrect");
					}
				}else{
					throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name Is Mandatory");
				}
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Connect Category Is Mandatory");
		}

		// COUNTRY
		if(!StringUtils.isEmpty(listOfCellValues.get(2))){
			connectT.setCountry(listOfCellValues.get(2)); 
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Country Is Mandatory");
		}
	
		// CONNECT NAME
		if(!StringUtils.isEmpty(listOfCellValues.get(3))){
			connectT.setConnectName(listOfCellValues.get(3)); 
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Connect Name Is Mandatory");
		}
		
		// CONNECT SUBSP
		System.out.println("SUb Sp  "+listOfCellValues.get(4));
		if(listOfCellValues.get(4).length()>0) {
		
			if(mapOfSubSpMappingT.containsKey(listOfCellValues.get(4))){
				connectT.setConnectSubSpLinkTs(constructConnectSubSpLink(listOfCellValues.get(4), userId,mapOfSubSpMappingT)); 
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "SubSp Is Incorrect");
			}
		}

		//CONNECT OFFERING
		if(listOfCellValues.get(5).length()>0){
		
			if(mapOfOfferingMappingT.containsKey(listOfCellValues.get(5))){
				connectT.setConnectOfferingLinkTs(constructConnectOfferingLink(listOfCellValues.get(5), userId, mapOfOfferingMappingT));
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Offering Is Incorrect");
			}
		}

		// CONNECT START DATE OF CONNECT
		// DateUtils.getNewTimestampFormat(listOfCellValues.get(6));
		if(!StringUtils.isEmpty(listOfCellValues.get(7))){
			Date startDateOfConnect = DateUtils.getNewTimestampFormat(listOfCellValues.get(7));
			connectT.setStartDatetimeOfConnect(new Timestamp(startDateOfConnect.getTime()));
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Start Date Of Connect Is Mandatory");
		}
		
		// CONNECT END DATE OF CONNECT
		if(!StringUtils.isEmpty(listOfCellValues.get(8))){
			Date endDateOfConnect = DateUtils.getNewTimestampFormat(listOfCellValues.get(8));
			connectT.setEndDatetimeOfConnect(new Timestamp(endDateOfConnect.getTime()));
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "End Date Of Connect Is Mandatory");
		}

		// TIME ZONE
		if(!StringUtils.isEmpty(listOfCellValues.get(9))){
			connectT.setTimeZone(listOfCellValues.get(9));
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Time Zone Is Mandatory");
		}

		// LOCATION
		if(!StringUtils.isEmpty(listOfCellValues.get(10))){
			connectT.setLocation(listOfCellValues.get(10));
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Location Is Mandatory");
		}
		
		// PRIMARY OWNER
		if(!StringUtils.isEmpty(listOfCellValues.get(12))){
			connectT.setPrimaryOwner(listOfCellValues.get(12).replace(".0", ""));
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Primary Owner Is Mandatory");
		}

		// CONNECT SECONDARY OWNER
		if(listOfCellValues.get(13).length()>0){
			connectT.setConnectSecondaryOwnerLinkTs(constructConnectSalesSupportLink(listOfCellValues.get(13), userId));
		}
		
		// CONNECT TCS ACCOUNT CONTACT
		if(listOfCellValues.get(14).length()>0){
		
			if(contactListMap.containsKey(listOfCellValues.get(14))){
				connectT.setConnectTcsAccountContactLinkTs(constructConnectTCSContactLink(
					listOfCellValues.get(14), userId, mapOfTCSContactT, mapOfContactT));
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Tcs Account Contact Is Incorrect");
			}
		}

		// CONNECT CUSTOMER CONTACT
		if(!StringUtils.isEmpty(listOfCellValues.get(15))){
		
			if(contactListMap.containsKey(listOfCellValues.get(15))){
				connectT.setConnectCustomerContactLinkTs(constructConnectCustomerContactLink(
					listOfCellValues.get(15), userId, mapOfCustomerContactT, mapOfContactT));
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Connect Customer Contact Is Incorrect");
			}
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Connect Customer Contact Is Mandatory");
		}
		
		// CONNECT NOTES
		if(listOfCellValues.get(16).length()>0){
		connectT.setNotesTs(constructConnectNotes(listOfCellValues.get(16), userId));
		}

		// CONNECT TYPE
		if(mapOfConnectTypeMappingT.containsKey(listOfCellValues.get(11))){
			connectT.setConnectTypeMappingT(constructConnectType(listOfCellValues.get(11), mapOfConnectTypeMappingT));
		} else {
			throw new DestinationException(HttpStatus.NOT_FOUND, "Connect Type Is Incorrect");
		}

		// CREATED BY
		connectT.setCreatedBy(userId);
		
		// MODIFIED BY
		connectT.setModifiedBy(userId); 
		
		// DOCUMENTS ATTACHED
		connectT.setDocumentsAttached("NO");
			
		connectService.insertConnect(connectT, isBulkDataLoad);
		}
		} catch(DestinationException de){
		    
		if(uploadStatus.isStatusFlag()){
			uploadStatus.setStatusFlag(false);
		}
		   UploadServiceErrorDetailsDTO error = new UploadServiceErrorDetailsDTO();
		   error.setRowNumber(rowNo+1);
		   error.setMessage(de.getMessage());
		   uploadStatus.getListOfErrors().add(error);
		}
			}
			rowNo++;
			}
		}
		}
//		fileInputStream.close();
		} else {
			logger.error("BAD_REQUEST: Validation Failed Please Check");
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Validation Failed Please Check");
		}
	} catch (Exception e) {
		logger.error("INTERNAL_SERVER_ERROR: An Exception has occured while processing the request for : {}", userId);
		throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
			"An Exception has occured while processing the request for "+userId);
	}
	return uploadStatus;
	}

	private String getCellValue(Cell cell) {
		String value = null;
		if(cell != null){
		// Check the cell type and format accordingly
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
				String dateFmt = cell.getCellStyle().getDataFormatString();
				value = new CellDateFormatter(dateFmt).format(date);
			} else {
				value = String.valueOf(cell.getNumericCellValue()).trim();
			}
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BLANK: {
			value = "";
			System.out.println("Blank >>>>");
			break;
		}
		}
		}else{
			value = "";
		}
		return value;
	}

	public File convert(MultipartFile file) throws Exception {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private Map<String, String> getNameAndIdFromCustomerMasterT() {
		List<CustomerMasterT> listOfCustomerMasterT = null;
		listOfCustomerMasterT = (List<CustomerMasterT>) customerRepository
				.findAll();
		Map<String, String> mapOfCMT = new HashMap<String, String>();
		for (CustomerMasterT cmt : listOfCustomerMasterT) {
			mapOfCMT.put(cmt.getCustomerName().trim(), cmt.getCustomerId()
					.trim());
		}
		return mapOfCMT;
	}

	private Map<String, String> getNameAndIdFromPartnerMasterT() {

		List<PartnerMasterT> listOfPartnerMasterT = null;
		listOfPartnerMasterT = (List<PartnerMasterT>) partnerRepository
				.findAll();
		Map<String, String> mapOfPMT = new HashMap<String, String>();

		for (PartnerMasterT pmt : listOfPartnerMasterT) {
			mapOfPMT.put(pmt.getPartnerName().trim(), pmt.getPartnerId().trim());
		}
		return mapOfPMT;
	}

	private ContactTMapDTO getNameAndIdFromContactT() {

		List<ContactT> listOfContactT = null;
		listOfContactT = (List<ContactT>) contactRepository.findAll();

		ContactTMapDTO cmDTO = new ContactTMapDTO();
		Map<String, String> mapOfCustomerContactT = null;
		Map<String, String> mapOfTcsContactT = null;

		if ((listOfContactT != null) && (!listOfContactT.isEmpty())) {
			mapOfCustomerContactT = new HashMap<String, String>();
			mapOfTcsContactT = new HashMap<String, String>();
			for (ContactT ct : listOfContactT) {
				if ((ct.getContactCategory().equals(
						EntityType.PARTNER.toString()) && (ct.getContactType()
						.equals(ContactType.EXTERNAL.toString())))) {
					mapOfCustomerContactT.put(ct.getContactName().trim(), ct
							.getContactId().trim());
				} else if ((ct.getContactCategory().equals(
						EntityType.CUSTOMER.toString()) && (ct.getContactType()
						.equals(ContactType.INTERNAL.toString())))) {
					mapOfTcsContactT.put(ct.getContactName().trim(), ct
							.getContactId().trim());
				} else if ((ct.getContactCategory().equals(
						EntityType.CUSTOMER.toString()) && (ct.getContactType()
						.equals(ContactType.EXTERNAL.toString())))) {
					mapOfCustomerContactT.put(ct.getContactName().trim(), ct
							.getContactId().trim());
				}
			}
			if (mapOfCustomerContactT != null) {
				cmDTO.setMapOfCustomerContactT(mapOfCustomerContactT);
			}
			if (mapOfTcsContactT != null) {
				cmDTO.setMapOfTcsContactT(mapOfTcsContactT);
			}
		}
		return cmDTO;
	}

	private List<String> splitValuesBasedOnSeparator(String keysWithSeparator,
			Map map) {

		List<String> listOfValues = null;
		if (keysWithSeparator != null) {
			listOfValues = new ArrayList<String>();
			String[] keysArray = keysWithSeparator.split(",");
			for (String key : keysArray) {
				listOfValues.add(getMapValuesForKey(map, key.trim()));
			}
		}
		return listOfValues;
	}

	private String getMapValuesForKey(Map<String, String> map, String key) {
		String value = null;
		if (map.containsKey(key)) {
			value = map.get(key);
		}
		return value;
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

	private List<ConnectTcsAccountContactLinkT> constructConnectTCSContactLink(
			String tcsNames, String userId, Map map,
			Map<String, ContactT> mapOfContactT) {

		List<ConnectTcsAccountContactLinkT> listTcsContactLinkT = null;
		if (tcsNames != null) {
			listTcsContactLinkT = new ArrayList<ConnectTcsAccountContactLinkT>();
			List<String> listOfcId = splitValuesBasedOnSeparator(tcsNames, map);
			if ((listOfcId != null) && (!listOfcId.isEmpty())) {
				for (String id : listOfcId) {
					ConnectTcsAccountContactLinkT occlt = new ConnectTcsAccountContactLinkT();
					ContactT contact = getContactMapValuesForKey(mapOfContactT, id);
					occlt.setContactT(contact);
					occlt.setCreatedBy(userId);
					occlt.setModifiedBy(userId);
					listTcsContactLinkT.add(occlt);
				}
			}
		}
		return listTcsContactLinkT;
	}

	private List<ConnectCustomerContactLinkT> constructConnectCustomerContactLink(
			String custNames, String userId, Map map,
			Map<String, ContactT> mapOfContactT) {

		List<ConnectCustomerContactLinkT> listConnectCustomerLinkT = null;
		if (custNames != null) {
			listConnectCustomerLinkT = new ArrayList<ConnectCustomerContactLinkT>();
			List<String> listOfcId = splitValuesBasedOnSeparator(custNames, map);
			if ((listOfcId != null) && (!listOfcId.isEmpty())) {
				for (String cId : listOfcId) {
					ConnectCustomerContactLinkT ccclt = new ConnectCustomerContactLinkT();
					ContactT contact = getContactMapValuesForKey(mapOfContactT, 
							cId);
					ccclt.setContactT(contact);
					ccclt.setCreatedBy(userId);
					ccclt.setModifiedBy(userId);
					listConnectCustomerLinkT.add(ccclt);
				}
			}
		}
		return listConnectCustomerLinkT;
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

	public Map<String, ContactT> getContactTMap() {
		List<ContactT> listOfContactT = null;
		listOfContactT = (List<ContactT>) contactRepository.findAll();
		Map<String, ContactT> contactMap = new HashMap<String, ContactT>();
		for (ContactT contactT : listOfContactT) {
			contactMap.put(contactT.getContactId(), contactT);
		}
		return contactMap;
	}

	public Map<String, ContactT> getContactsList() {
		List<ContactT> listOfContactT = null;
		listOfContactT = (List<ContactT>) contactRepository.findAll();
		Map<String, ContactT> contactListMap = new HashMap<String, ContactT>();
		for (ContactT contactT : listOfContactT) {
			contactListMap.put(contactT.getContactName(), contactT);
		}
		return contactListMap;
	}
	
	private Map<String, SubSpMappingT> getSubSpMappingT() {
		List<SubSpMappingT> listOfSubSpT = null;
		listOfSubSpT = (List<SubSpMappingT>) subSpRepository.findAll();
		Map<String, SubSpMappingT> subSpMap = new HashMap<String, SubSpMappingT>();
		for (SubSpMappingT subSpT : listOfSubSpT) {
			subSpMap.put(subSpT.getSubSp(), subSpT);
		}
		return subSpMap;
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

	private ContactT getContactMapValuesForKey(
			Map<String, ContactT> contactMap, String key) {
		ContactT contact = null;
		if (contactMap.containsKey(key)) {
			contact = contactMap.get(key);
		}
		return contact;
	}

	private SubSpMappingT getSubSpMappingTMapValuesForKey(
			Map<String, SubSpMappingT> subSpMap, String key) {
		SubSpMappingT subSp = null;
		if (subSpMap.containsKey(key)) {
			subSp = subSpMap.get(key);
		}
		return subSp;
	}

	private OfferingMappingT getOfferingMappingTMapValuesForKey(
			Map<String, OfferingMappingT> offeringMap, String key) {
		OfferingMappingT offering = null;
		if (offeringMap.containsKey(key)) {
			offering = offeringMap.get(key);
		}
		return offering;
	}

	private List<ConnectSecondaryOwnerLinkT> constructConnectSalesSupportLink(
			String values, String userId) {

		List<ConnectSecondaryOwnerLinkT> listOfConnectSecOwnerLink = null;

		if (values != null) {
			listOfConnectSecOwnerLink = new ArrayList<ConnectSecondaryOwnerLinkT>();
			String[] valuesArray = values.split(",");
			for (String value : valuesArray) {
				ConnectSecondaryOwnerLinkT oclt = new ConnectSecondaryOwnerLinkT();
				oclt.setConnectSecondaryOwnerLinkId(validateAndRectifyValue(value
						.trim()));
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
			if (value.substring(value.length() - 2, value.length())
					.equals(".0")) {
				val = value.substring(0, value.length() - 2);
			}
		}
		return val;
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

}