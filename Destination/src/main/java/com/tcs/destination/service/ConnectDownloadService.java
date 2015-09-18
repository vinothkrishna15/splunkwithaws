package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ConnectTypeMappingT;
import com.tcs.destination.bean.GeographyCountryMappingT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.OfferingMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.ConnectTypeRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyCountryRepository;
import com.tcs.destination.data.repository.OfferingRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.data.repository.UserRepository;

@Service
public class ConnectDownloadService {

	@Autowired
	ConnectRepository connectRepository;
	
	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	GeographyCountryRepository geographyCountryRepository;
	
	@Autowired
	OpportunityDownloadService opportunityDownloadService;
	
	@Autowired
	OfferingRepository offeringRepository;
	
	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheetsForDownloadServices;
	
	@Autowired
	ConnectTypeRepository connectTypeRepository;
	
	@Autowired
	TimezoneMappingRepository timezoneMappingRepository;
	
	@Autowired
	ContactRepository contactRepository;

	private static final Logger logger = LoggerFactory.getLogger(ConnectDownloadService.class);

	public InputStreamResource getConnects() throws Exception {
		logger.info("Inside getConnects() methos"); 
		List<ConnectT> connectList = (List<ConnectT>) connectRepository.findAll();
		File file = new File("/Users/bnpp/Desktop/Upload/Connect_Template_v01_Data.xlsm");
//	      FileInputStream fIP = new FileInputStream(file);
	      
	      OPCPackage pkg = OPCPackage.open(new FileInputStream(file.getAbsolutePath()));
	        XSSFWorkbook xssfwb = new XSSFWorkbook(pkg);

	      //Get the workbook instance for XLSX file 
	      SXSSFWorkbook workbook = new SXSSFWorkbook(xssfwb, 100);
		if (file.isFile() && file.exists()) {
			writeConnectTsIntoWorkbook(connectList, workbook);
			try{
			writeCustomerMasterRefNamesIntoWorkbook(workbook);
			writePartnerMasterRefNamesIntoWorkbook(workbook);
			writeGeographyCountryIntoWorkbook(workbook);
			opportunityDownloadService.populateSubSpSheet(workbook.getSheet("SubSp(Ref)"));
			writeOfferingMappingTRefIntoWorkbook(workbook);
//			writeCustomerContactRefIntoWorkbook(workbook);
			opportunityDownloadService.populateContactSheets(workbook.getSheet("Partner Contact(Ref)"));
			commonWorkbookSheetsForDownloadServices.populateUserRefSheet(workbook.getSheet("User(Ref)"));
			writeConnectTypeRefIntoWorkbook(workbook);
			writeTimeZoneMappingTRefIntoWorkbook(workbook);
	      }catch(Exception e){
	    	  e.printStackTrace();
	      }
		}
		
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
		return inputStreamResource;
	}

//	private void writeCustomerContactRefIntoWorkbook(SXSSFWorkbook workbook) throws Exception {
//		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Customer Contact(Ref)");
//		SXSSFRow row = null;
//		List<ContactT> listOfContact = (List<ContactT>) contactRepository.findAll();
//		if(listOfContact!=null) {
//		int rowCountPartnerSheet = 1; // Excluding the header, header starts with index 0
//		for (ContactT ct : listOfContact) {
//		    
//		    if ((ct.getContactCategory().equals(EntityType.CUSTOMER.toString()) && 
//			    (ct.getContactType().equals(ContactType.EXTERNAL.toString())) || 
//			    ct.getContactType().equals(ContactType.INTERNAL.toString()))) { // For customer Contact
//			    
//			    // Create row with rowCount
//			    row = (SXSSFRow) spreadSheet.createRow(rowCountPartnerSheet);
//
//			    // Create new Cell and set cell value
//			    Cell cellCustomerName = row.createCell(0);
//			    try {
//			    cellCustomerName.setCellValue(ct.getPartnerMasterT().getPartnerName().trim());
//			    } catch(NullPointerException npe){
//				throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Customer Contact cannot exist without customer");
//			    }
//			    
//			    Cell cellPartnerContactName = row.createCell(1);
//			    cellPartnerContactName.setCellValue(ct.getContactName());
//			    
//			    Cell cellPartnerContactRole = row.createCell(2);
//			    cellPartnerContactRole.setCellValue(ct.getContactRole());
//			    
//			    Cell cellPartnerContactEmailId = row.createCell(3);
//			    if(ct.getContactEmailId()!=null) {
//				cellPartnerContactEmailId.setCellValue(ct.getContactEmailId());
//			    }
//
//			    // Increment row counter for partner contact sheet
//			    rowCountPartnerSheet++;
//		    
//		    }
//		}
//		}
//
//	}

	private void writeTimeZoneMappingTRefIntoWorkbook(SXSSFWorkbook workbook) {
		//Get the TimeZone(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("TimeZone(Ref)");
		SXSSFRow row = null;
		List<TimeZoneMappingT> listOfTimeZones = (List<TimeZoneMappingT>) timezoneMappingRepository.findAll();
		if(listOfTimeZones!=null) {
		int rowCount = 1; // Excluding the header, header starts with index 0
		for(TimeZoneMappingT timeZones : listOfTimeZones){
		    // Create row with rowCount
		    row = (SXSSFRow) spreadSheet.createRow(rowCount);
		    row.createCell(0).setCellValue(timeZones.getTimeZoneCode());
			row.createCell(1).setCellValue(timeZones.getDescription() + " (UTC"+timeZones.getTimeZoneOffset()+")");

		    // Increment row counter
		    rowCount++;
			}
		}
	}

	private void writeConnectTypeRefIntoWorkbook(SXSSFWorkbook workbook) {
		//Get the Conenct Type(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Connect Type(Ref)");
		SXSSFRow row = null;
		List<ConnectTypeMappingT> listOfConnectTypes = (List<ConnectTypeMappingT>) connectTypeRepository.findAll();
		if(listOfConnectTypes!=null) {
		int rowCount = 1; // Excluding the header, header starts with index 0
		for(ConnectTypeMappingT connectType : listOfConnectTypes){
		    // Create row with rowCount
		    row = (SXSSFRow) spreadSheet.createRow(rowCount);
		    row.createCell(0).setCellValue(connectType.getType());
			row.createCell(1).setCellValue(connectType.getDescription());
			row.createCell(2).setCellValue(connectType.getConnectWith());

		    // Increment row counter
		    rowCount++;
			}
		}
	}

	private void writeOfferingMappingTRefIntoWorkbook(SXSSFWorkbook workbook) {
		//Get the SubSp(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Offering(Ref)");
		SXSSFRow row = null;
		List<OfferingMappingT> listOfOffering = (List<OfferingMappingT>) offeringRepository.findAll();
		if(listOfOffering!=null) {
		int rowCount = 1; // Excluding the header, header starts with index 0
		for(OfferingMappingT offeringT : listOfOffering){
		    // Create row with rowCount
		    row = (SXSSFRow) spreadSheet.createRow(rowCount);
		    row.createCell(0).setCellValue(offeringT.getSubSp());
			row.createCell(1).setCellValue(offeringT.getOffering());
			row.createCell(2).setCellValue(offeringT.getActive());

		    // Increment row counter
		    rowCount++;
			}
		}
	}

//	private void writeSubSpRefIntoWorkbook(SXSSFWorkbook workbook) {
//		//Get the SubSp(Ref) Sheet From Workbook
//		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("SubSp(Ref)");
//		SXSSFRow row = null;
//		int currentRow = 1; // Excluding the header, header starts with index 0
//		List<Object[]> geographyCountryList=geographyCountryRepository.getGeographyCountry();
//		for(Object[] geographyCountry:geographyCountryList){
//			
//			// Get row with rowCount
//			row = (SXSSFRow) spreadSheet.createRow(currentRow);
//			
//			// Get Cell and set cell value
//			row.createCell(0).setCellValue(geographyCountry[0].toString());
//			row.createCell(1).setCellValue(geographyCountry[1].toString());
//			
//			// Increment row counter
//			currentRow++;
//		}
//	}

	private void writeGeographyCountryIntoWorkbook(SXSSFWorkbook workbook) {
		//Get the Geography Country(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Geography Country(Ref)");
		
		SXSSFRow row = null;
		int currentRow = 1; // Excluding the header, header starts with index 0
		List<Object[]> geographyCountryList=geographyCountryRepository.getGeographyCountry();
		for(Object[] geographyCountry:geographyCountryList){
			
			// Get row with rowCount
			row = (SXSSFRow) spreadSheet.createRow(currentRow);
			
			// Get Cell and set cell value
			row.createCell(0).setCellValue(geographyCountry[0].toString());
			row.createCell(1).setCellValue(geographyCountry[1].toString());
			
			// Increment row counter
			currentRow++;
		}
	}

	/**
	 * This Method Writes customer names into the workbook
	 * @param workbook
	 */
	private void writeCustomerMasterRefNamesIntoWorkbook(SXSSFWorkbook workbook) {
		
		//Get the Customer Master(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Customer Master(Ref)");
		
		SXSSFRow row = null;
		int currentRow = 1; // Excluding the header, header starts with index 0
		List<Object[]> customerMasterNamesList=customerRepository.getNameAndId();
		for(Object[] customerName:customerMasterNamesList){
			
			// Get row with rowCount
			row = (SXSSFRow) spreadSheet.createRow(currentRow);
			
			// Get Cell and set cell value
			row.createCell(0).setCellValue(customerName[0].toString());
			
			// Increment row counter
			currentRow++;
		}
	}
	
	/**
	 * This Method Writes partner names into the workbook
	 * @param workbook
	 */
	private void writePartnerMasterRefNamesIntoWorkbook(SXSSFWorkbook workbook) {
		
		//Get the Partner Master(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Partner Master(Ref)");
		SXSSFRow row = null;
		int currentRow = 1; // Excluding the header, header starts with index 0
		List<Object[]> partnerMasterNamesList=partnerRepository.getPartnerNameAndGeography();
		for(Object[] partnerName:partnerMasterNamesList){
			
			// Get row with rowCount
			row = (SXSSFRow) spreadSheet.createRow(currentRow);
			
			// Get Cell and set cell value
			row.createCell(0).setCellValue(partnerName[0].toString());
			row.createCell(1).setCellValue(partnerName[1].toString());
			
			// Increment row counter
			currentRow++;
		}
	}
	
	 /**
     * This method populates Geography Country Ref sheet
     * 
     * @param geoCountrySheet
     * @throws Exception
     */
	public void populateGeographyCountryRef(SXSSFWorkbook workbook)
			throws Exception {

		//Get the Partner Master(Ref) Sheet From Workbook
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Geography Country(Ref)");
		SXSSFRow row = null;
		List<GeographyCountryMappingT> listOfGeoCountry = (List<GeographyCountryMappingT>) geographyCountryRepository
				.findAll();

		int rowCount = 1; // Excluding the header, header starts with index 0
		for (GeographyCountryMappingT geoCountry : listOfGeoCountry) {

			// Create row with rowCount
			row = (SXSSFRow) spreadSheet.createRow(rowCount);

			// Create new Cell and set cell value
			Cell cellGeo = row.createCell(0);
			cellGeo.setCellValue(geoCountry.getGeography().trim());

			Cell cellCountry = row.createCell(1);
			cellCountry.setCellValue(geoCountry.getCountry().trim());

			// Increment row counter
			rowCount++;
		}
	}
	
	
	public void writeConnectTsIntoWorkbook(List<ConnectT> connectList, SXSSFWorkbook workbook) {
		SXSSFSheet spreadSheet = (SXSSFSheet) workbook.getSheet("Connect");
		int currentRow = 1;
		SXSSFRow row = null;
		try{
		for (ConnectT connectT : connectList) {
			row = (SXSSFRow) spreadSheet.createRow(currentRow);
			row.createCell(1).setCellValue(connectT.getConnectId());
			row.createCell(2).setCellValue(connectT.getConnectCategory());
			if (connectT.getConnectCategory().equals("CUSTOMER")) {
				row.createCell(3).setCellValue(connectT.getCustomerMasterT().getCustomerName());
			} else {
				row.createCell(3).setCellValue(connectT.getPartnerMasterT().getPartnerName());
			}
			row.createCell(4).setCellValue(connectT.getCountry());
			row.createCell(5).setCellValue(connectT.getConnectName());
			List<String> subSpList = new ArrayList<String>();
			for (ConnectSubSpLinkT connectSubSpLinkT : connectT.getConnectSubSpLinkTs()) {
			subSpList.add(connectSubSpLinkT.getSubSpMappingT().getSubSp());
			}
			row.createCell(6).setCellValue(subSpList.toString().replace("[", "").replace("]", ""));
			List<String> offering = new ArrayList<String>();
			for (ConnectOfferingLinkT connectOfferingLinkT : connectT.getConnectOfferingLinkTs()) {
				offering.add(connectOfferingLinkT.getOfferingMappingT().getOffering());
			}
			row.createCell(7).setCellValue(offering.toString().replace("[", "").replace("]", ""));
//				////////////////////////// To Modify
			row.createCell(8).setCellValue(connectT.getStartDatetimeOfConnect());
			row.createCell(9).setCellValue(connectT.getStartDatetimeOfConnect());
			row.createCell(10).setCellValue(connectT.getEndDatetimeOfConnect());
			////To Change TIME Zone Description In Excel
			row.createCell(11).setCellValue(connectT.getTimeZoneMappingT().getDescription());
			row.createCell(12).setCellValue(connectT.getLocation());
			if(connectT.getConnectTypeMappingT()!=null){
			row.createCell(13).setCellValue(connectT.getConnectTypeMappingT().getType());
			}
			row.createCell(14).setCellValue(connectT.getPrimaryOwner());
			List<String> secondaryOwnersList=connectRepository.getSecondaryOwnerByConnectId(connectT.getConnectId());
			if(secondaryOwnersList!=null){
			List<String> secondaryOwners=new ArrayList<String>();
			for (String secondaryOwner : secondaryOwnersList) {
				UserT user = userRepository.findByUserId(secondaryOwner);
				if(user!=null){
				secondaryOwners.add(user.getUserName());
				}
			}
			row.createCell(15).setCellValue(secondaryOwners.toString().replace("[", "").replace("]", ""));
			}
			List<String> tcsContactNames=new ArrayList<String>();
			for (ConnectTcsAccountContactLinkT connectTcsAccountContactLinkT : connectT.getConnectTcsAccountContactLinkTs()) {
				tcsContactNames.add(connectTcsAccountContactLinkT.getContactT().getContactName());
			}
			row.createCell(16).setCellValue(tcsContactNames.toString().replace("[", "").replace("]", ""));
			List<String> cusContactNames=new ArrayList<String>();
			for (ConnectCustomerContactLinkT connectCustomerContactLinkT : connectT.getConnectCustomerContactLinkTs()) {
				cusContactNames.add(connectCustomerContactLinkT.getContactT().getContactName());
			}
			row.createCell(17).setCellValue(cusContactNames.toString().replace("[", "").replace("]", ""));
			List<String> connectNotesList=new ArrayList<String>();
			for (NotesT notes : connectT.getNotesTs()) {
				connectNotesList.add(notes.getNotesUpdated());
			}
			row.createCell(18).setCellValue(connectNotesList.toString().replace("[", "").replace("]", ""));
		    currentRow++;
		  }
	}catch(Exception e){
		e.printStackTrace();
	}
}
	}