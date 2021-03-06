package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.IouBeaconMappingTRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyUtil;

@Service
public class CustomerDownloadService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheets;

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	CustomerIOUMappingRepository customerIOUMappingRepository;

	@Autowired
	RevenueCustomerMappingTRepository revenueCustomerMappingTRepository;

	@Autowired
	IouBeaconMappingTRepository iouBeaconMappingTRepository;

	@Autowired
	ContactRepository contactRepository;
	
	@Autowired
	ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	Map<String, CustomerMasterT> mapOfCustomerMasterT = null;
	Map<String,CustomerMasterT> mapOfContactCustomerLinkT = null;

	private static final Logger logger = LoggerFactory.getLogger(CustomerDownloadService.class);

	public InputStreamResource getCustomers(boolean oppFlag)
			throws Exception {

		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		logger.debug("Begin: inside getCustomers() of CustomerDownloadService");
		mapOfCustomerMasterT = getcustomerMappingT();

		try {
			
			workbook = ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.CUSTOMER_TEMPLATE_LOCATION_PROPERTY_NAME)));
			if(oppFlag){
				// Populate Customer Master sheet
				populateCustomerMasterSheet(workbook.getSheet(Constants.CUSTOMER_MASTER_SHEET_NAME));
				// Populate Beacon Mapping Sheet
				populateBeaconMappingSheet(workbook.getSheet(Constants.BEACON_MAPPING_SHEET_NAME));
				// Populate Finance Mapping Sheet
				populateFinanceMappingSheet(workbook.getSheet(Constants.FINANCE_MAPPING_SHEET_NAME));
			}
			// Populate Iou Customer REF Sheet
			populateIouCustomerSheet(workbook.getSheet(Constants.IOU_CUSTOMER_MAPPING_REF));
			// Populate Beacon Iou REF Sheet
			populateBeaconIouSheet(workbook.getSheet(Constants.IOU_BEACON_MAP_REF));

			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));

		} catch (Exception e) {
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An Internal Exception has occured");
		}
		logger.debug("End: inside getCustomers() of CustomerDownloadService");
		return inputStreamResource;
	}

	/**
	 * This method creates a Customer Map
	 * @return customerMap
	 */
	private Map<String, CustomerMasterT> getcustomerMappingT() {
		List<CustomerMasterT> listOfCustomerMappingT = null;
		logger.debug("Begin: inside getcustomerMappingT() of CustomerDownloadService");
		listOfCustomerMappingT = (List<CustomerMasterT>) customerRepository.findAll();
		Map<String, CustomerMasterT> customerMap = new HashMap<String, CustomerMasterT>();
		for (CustomerMasterT customerMappingT : listOfCustomerMappingT) {
			customerMap.put(customerMappingT.getCustomerName(), customerMappingT);
		}
		logger.debug("End: inside getcustomerMappingT() of CustomerDownloadService");
		return customerMap;
	}

	private void populateBeaconIouSheet(Sheet beaconIouSheet) {
		logger.debug("Begin: inside populateBeaconIouSheet() of CustomerDownloadService");
		List<IouBeaconMappingT> listOfBeaconIou = (List<IouBeaconMappingT>) iouBeaconMappingTRepository.findAll();

		if(listOfBeaconIou!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (IouBeaconMappingT beaconIou : listOfBeaconIou) {
				// Create row with rowCount
				Row row = beaconIouSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellBeaconCustomerName = row.createCell(0);
				cellBeaconCustomerName.setCellValue(beaconIou.getDisplayIou().trim());

				Cell cellBeaconIou = row.createCell(1);
				cellBeaconIou.setCellValue(beaconIou.getBeaconIou().trim());

				// Increment row counter
				rowCount++;
			}
		} 	
		logger.debug("End: inside populateBeaconIouSheet() of CustomerDownloadService");
	}

	private void populateIouCustomerSheet(Sheet iouCustomerMap)  throws Exception{
		logger.debug("Begin: inside populateIouCustomerSheet() of CustomerDownloadService");
		List<IouCustomerMappingT> listOfIou = (List<IouCustomerMappingT>) customerIOUMappingRepository.findAll();

		if(listOfIou!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (IouCustomerMappingT iou : listOfIou) {
				// Create row with rowCount
				Row row = iouCustomerMap.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellBeaconCustomerName = row.createCell(0);
				cellBeaconCustomerName.setCellValue(iou.getDisplayIou().trim());

				Cell cellBeaconIou = row.createCell(1);
				cellBeaconIou.setCellValue(iou.getIou().trim());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.debug("End: inside populateIouCustomerSheet() of CustomerDownloadService");
	}

	/*
	 * Populate Beacon Mapping from beacon_mapping_t
	 */
	private void populateBeaconMappingSheet(Sheet beaconMappingSheet)  throws Exception{
		logger.debug("Begin: inside populateBeaconMappingSheet() of CustomerDownloadService");
		List<BeaconCustomerMappingT> listOfBeacon = (List<BeaconCustomerMappingT>) beaconRepository.findAll();
		CustomerMasterT customerObj = null;
		if(listOfBeacon!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (BeaconCustomerMappingT beacon : listOfBeacon) {
				// Create row with rowCount
				Row row = beaconMappingSheet.createRow(rowCount);

				customerObj = mapOfCustomerMasterT.get(beacon.getCustomerMasterT());

				Cell cellGroupCustomerName = row.createCell(1);
				cellGroupCustomerName.setCellValue(customerObj.getGroupCustomerName().trim());

				Cell cellCustomerName = row.createCell(2);
				cellCustomerName.setCellValue(customerObj.getCustomerName().trim());

				Cell cellIou = row.createCell(3);
				cellIou.setCellValue(customerObj.getIou().trim());

				Cell cellGeo = row.createCell(4);
				cellGeo.setCellValue(customerObj.getGeography().trim());

				// Create new Cell and set cell value
				Cell cellBeaconCustomerName = row.createCell(5);
				cellBeaconCustomerName.setCellValue(beacon.getBeaconCustomerName().trim());

				Cell cellBeaconIou = row.createCell(6);
				cellBeaconIou.setCellValue(beacon.getBeaconIou().trim());

				Cell cellBeaconGeo = row.createCell(7);
				cellBeaconGeo.setCellValue(beacon.getGeographyMappingT().getGeography().trim());

				Cell active = row.createCell(8);//TODO inactive indicator - adding a separate column for active flag -done
				active.setCellValue(beacon.isActive());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.debug("End: inside populateBeaconMappingSheet() of CustomerDownloadService");
	}

	private void populateFinanceMappingSheet(Sheet financeMappingSheet)  throws Exception{
		logger.debug("Begin: inside populateFinanceMappingSheet() of CustomerDownloadService");
		List<RevenueCustomerMappingT> listOffinance = (List<RevenueCustomerMappingT>) revenueCustomerMappingTRepository.findAll();
		CustomerMasterT customerObj = null;
		if(listOffinance!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (RevenueCustomerMappingT finance : listOffinance) {
				// Create row with rowCount
				Row row = financeMappingSheet.createRow(rowCount);

			
				customerObj = mapOfCustomerMasterT.get(finance.getCustomerMasterT());

				Cell cellGroupCustomerName = row.createCell(1);
				cellGroupCustomerName.setCellValue(customerObj.getGroupCustomerName().trim());

				Cell cellCustomerName = row.createCell(2);
				cellCustomerName.setCellValue(customerObj.getCustomerName().trim());

				Cell cellIou = row.createCell(3);
				cellIou.setCellValue(customerObj.getIou().trim());

				Cell cellGeo = row.createCell(4);
				cellGeo.setCellValue(customerObj.getGeography().trim());

				// Create new Cell and set cell value
				Cell cellFinanceCustomerName = row.createCell(5);
				cellFinanceCustomerName.setCellValue(finance.getFinanceCustomerName().trim());

				Cell cellFinanceIou = row.createCell(6);
				cellFinanceIou.setCellValue(finance.getFinanceIou().trim());

				Cell cellFinanceGeo = row.createCell(7);
				cellFinanceGeo.setCellValue(finance.getCustomerGeography().trim());

				Cell active = row.createCell(8);//TODO inactive indicator - adding a separate column for active flag -done
				active.setCellValue(finance.isActive());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.debug("End: inside populateFinanceMappingSheet() of CustomerDownloadService");
	}

	/*
	 * Populate CustomerMaster Sheet from customer_master_t
	 */
	public void populateCustomerMasterSheet(Sheet customerMasterSheet) throws Exception{
		logger.debug("Begin: inside populateCustomerMasterSheet() of CustomerDownloadService");
		List<CustomerMasterT> listOfCMT = (List<CustomerMasterT>) customerRepository.findAll();

		if(listOfCMT!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (CustomerMasterT cmt : listOfCMT) {
				// Create row with rowCount
				Row row = customerMasterSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellGrpClient = row.createCell(1);
				cellGrpClient.setCellValue(cmt.getGroupCustomerName().trim());

				Cell cellCustName = row.createCell(2);
				cellCustName.setCellValue(cmt.getCustomerName().trim());

				Cell cellIou = row.createCell(3);
				cellIou.setCellValue(cmt.getIouCustomerMappingT().getIou().trim());

				Cell cellGeo = row.createCell(4);
				cellGeo.setCellValue(cmt.getGeographyMappingT().getGeography()
						.trim());
				
				Cell active = row.createCell(5);//TODO inactive indicator - adding a separate column for active flag -done
				active.setCellValue(cmt.isActive());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.debug("End: inside populateCustomerMasterSheet() of CustomerDownloadService");
	}

	public InputStreamResource getCustomerContacts(boolean oppFlag)
			throws Exception {

		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		logger.debug("Begin: inside getCustomerContacts() of CustomerDownloadService");
		mapOfCustomerMasterT = getcustomerMappingT();
		// Get List of IOU from DB for validating the IOU which comes from the sheet	
		mapOfContactCustomerLinkT = getContactCustomerLinkT();

		try {
			workbook = ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.CUSTOMER_CONTACT_TEMPLATE_LOCATION_PROPERTY_NAME)));
			
			if(oppFlag){
				populateCustomerContactSheet(workbook.getSheet(Constants.CUSTOMER_CONTACT_SHEET_NAME));
			}
			// Populate Customer Master sheet
			populateCustomerMasterRefSheet(workbook.getSheet(Constants.CUSTOMER_MASTER_REF));
			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));

		} catch (Exception e) {
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
					"An Internal Exception has occured");
		}
		logger.debug("End: inside getCustomerContacts() of CustomerDownloadService");
		return inputStreamResource;
	}

		private Map<String, CustomerMasterT> getContactCustomerLinkT() {
		List<ContactCustomerLinkT> listOfContactCustomerLinkT = null;
		logger.debug("Begin: inside getContactCustomerLinkT() of CustomerDownloadService");
		listOfContactCustomerLinkT = (List<ContactCustomerLinkT>) contactCustomerLinkTRepository.findAll();
		Map<String, CustomerMasterT> contactCustomerMap = new HashMap<String, CustomerMasterT>();
		for (ContactCustomerLinkT contactCustomerMappingT : listOfContactCustomerLinkT) {
			contactCustomerMap.put(contactCustomerMappingT.getContactId(), contactCustomerMappingT.getCustomerMasterT());
		}
		logger.debug("End: inside getContactCustomerLinkT() of CustomerDownloadService");
		return contactCustomerMap;
	}
	/*
	 * Populate CustomerMaster Sheet from customer_master_t
	 */
	public void populateCustomerMasterRefSheet(Sheet customerMasterSheet) throws Exception{
		logger.debug("Begin: inside populateCustomerMasterRefSheet() of CustomerDownloadService");
		List<CustomerMasterT> listOfCMT = (List<CustomerMasterT>) customerRepository.findAll();

		if(listOfCMT!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (CustomerMasterT cmt : listOfCMT) {
				// Create row with rowCount
				Row row = customerMasterSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellGrpClient = row.createCell(0);
				cellGrpClient.setCellValue(cmt.getGroupCustomerName().trim());

				Cell cellCustName = row.createCell(1);
				cellCustName.setCellValue(cmt.getCustomerName().trim());

				Cell cellIou = row.createCell(2);
				cellIou.setCellValue(cmt.getIouCustomerMappingT().getIou().trim());

				Cell cellGeo = row.createCell(3);
				cellGeo.setCellValue(cmt.getGeographyMappingT().getGeography()
						.trim());

				// Increment row counter
				logger.debug("End: inside populateCustomerMasterRefSheet() of CustomerDownloadService");
				rowCount++;
			}
		} 
	}



	private void populateCustomerContactSheet(Sheet customerContactSheet) {
		// TODO Auto-generated method stub
		logger.debug("Begin: inside populateCustomerContactSheet() of CustomerDownloadService");
		List<ContactT> listOfContact = (List<ContactT>) contactRepository.findAll();

		if(listOfContact!=null) {
			int rowCountCustomerSheet = 1; // Excluding the header, header starts with index 0
			for (ContactT ct : listOfContact) {

				if ((ct.getContactCategory().equals(EntityType.CUSTOMER.toString()) && 
						(ct.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For Customer Contact

					// Create row with rowCount
					Row row = customerContactSheet.createRow(rowCountCustomerSheet);

					// Create new Cell and set cell value
					Cell cellContactId = row.createCell(1);
					cellContactId.setCellValue(ct.getContactId());
					
					// Create new Cell and set cell value saturday modified
					Cell cellCustomerName = row.createCell(2);
					if(mapOfContactCustomerLinkT.containsKey(ct.getContactId())){
					CustomerMasterT customerObj = mapOfContactCustomerLinkT.get(ct.getContactId());
					cellCustomerName.setCellValue(customerObj.getCustomerName());
					}
					else {
						throw new DestinationException(HttpStatus.NOT_FOUND, "customername NOT Found");
					}
				
                    Cell cellCustomerContactType = row.createCell(3);
					cellCustomerContactType.setCellValue(ct.getContactType());

					Cell cellCustomerContactName = row.createCell(5);
					cellCustomerContactName.setCellValue(ct.getContactName());

					Cell cellCustomerContactRole = row.createCell(6);
					cellCustomerContactRole.setCellValue(ct.getContactRole());

					Cell cellCustomerContactEmailId = row.createCell(7);
					if(ct.getContactEmailId()!=null) {
						cellCustomerContactEmailId.setCellValue(ct.getContactEmailId());
					}
					
					Cell active = row.createCell(10);//TODO inactive indicator - adding a separate column for active flag -done
					active.setCellValue(ct.isActive());

					// Increment row counter for partner contact sheet
					rowCountCustomerSheet++;

				}
			}
		}
		logger.debug("End: inside populateCustomerContactSheet() of CustomerDownloadService");
	}
}
