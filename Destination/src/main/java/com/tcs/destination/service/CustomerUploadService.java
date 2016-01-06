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

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.enums.DocumentActionType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ContactsUploadConstants;
import com.tcs.destination.utils.CustomerUploadConstants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;

/**
 * This service is used to upload the customer sheet synchronously
 *
 */
@Service
public class CustomerUploadService {

	@Autowired
	CustomerService customerService;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	CustomerIOUMappingRepository customerIouMappingTRepository;

	private List<String> listOfCustomerId = null;
	private Map<String, String> mapOfCustomerMasterT = null;

	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String, IouCustomerMappingT> mapOfIouMappingT = null;

	private static final Logger logger = LoggerFactory.getLogger(CustomerUploadService.class);

	/* This service uploads customers to customer_master_t table
	 * @param file
	 * @param userId
	 */
	public UploadStatusDTO upload(MultipartFile file)
			throws Exception {
		logger.debug("Start: Inside upload() of CustomerUploadService");
		Workbook workbook = ExcelUtils.getWorkBook(file);

		UploadStatusDTO uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());


		// Get List of Customer Id's from DB for validating the Customer Id which comes from the sheet	
		listOfCustomerId =  customerRepository.getCustomerIds();

		// Get List of geographies from DB for validating the geographies which comes from the sheet	
		mapOfGeographyMappingT = getGeographyMappingT();

		// Get List of IOU from DB for validating the IOU which comes from the sheet	
		mapOfIouMappingT = getIouMappingT();

		// To check if no validation errors are present in the workbook
		if (validateSheetForCustomer(workbook)) { 

			// Get Name and Id from DB to get Id from Name of customer
			//	mapOfCustomerMasterT = getNameAndIdFromCustomerMasterT(); 
			Sheet sheet = workbook.getSheet(CustomerUploadConstants.CUSTOMER_MASTER_SHEET_NAME);
			if(sheet==null){
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Please upload the workbook for CUSTOMER UPLOAD or missing "+CustomerUploadConstants.CUSTOMER_MASTER_SHEET_NAME+" sheet");
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
							listOfCellValues = iterateRow(row, CustomerUploadConstants.CUSTOMER_MASTER_SHEET_COLUMN_COUNT);
							customerService.addCustomer(constructCustomerMasterTForCustomer(listOfCellValues,  DestinationUtils.getCurrentUserDetails().getUserId(), DocumentActionType.ADD.name()));
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
		logger.debug("End: Inside upload() of CustomerUploadService");
		return uploadStatus;
	}

	/**
	 * This method creates a geography Map
	 * @return geographyMap
	 */
	private Map<String, GeographyMappingT> getGeographyMappingT() {
		logger.debug("Start: Inside getGeographyMappingT() of CustomerUploadService");
		List<GeographyMappingT> listOfGeographyMappingT = null;
		listOfGeographyMappingT = (List<GeographyMappingT>) geographyRepository.findAll();
		Map<String, GeographyMappingT> geographyMap = new HashMap<String, GeographyMappingT>();
		for (GeographyMappingT geographyMappingT : listOfGeographyMappingT) {
			geographyMap.put(geographyMappingT.getGeography(), geographyMappingT);
		}
		logger.debug("End: Inside getGeographyMappingT() of CustomerUploadService");
		return geographyMap;
	}

	/**
	 * This method creates a IOU Map
	 * @return iouMap
	 */
	private Map<String, IouCustomerMappingT> getIouMappingT() {
		logger.debug("Start: Inside getIouMappingT() of CustomerUploadService");
		List<IouCustomerMappingT> listOfIouMappingT = null;
		listOfIouMappingT = (List<IouCustomerMappingT>) customerIouMappingTRepository.findAll();
		Map<String, IouCustomerMappingT> iouMap = new HashMap<String, IouCustomerMappingT>();
		for (IouCustomerMappingT iouMappingT : listOfIouMappingT) {
			iouMap.put(iouMappingT.getIou(), iouMappingT);
		}
		logger.debug("End: Inside getIouMappingT() of CustomerUploadService");
		return iouMap;
	}


	/**
	 * This method checks if customerId is present in the 
	 * list of customerIds which are present in the database
	 * @param customerId
	 * @return boolean
	 */
	private boolean validateCustomerId(String customerId) throws Exception{

		logger.debug("Start: Inside validateCustomerId() of CustomerUploadService");
		boolean flag = false;

		if(customerId!=null){
			for(String cId : listOfCustomerId){
				if(cId.equalsIgnoreCase(customerId)){
					flag = true;
				}
			}
		}

		if(!flag){
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid Customer Id");
		}
		logger.debug("End: Inside validateCustomerId() of CustomerUploadService");
		return flag;
	}


	/**
	 * This method constructs CustomerMasterT for Customer Upload
	 * @param listOfCellValues
	 * @param userId
	 * @param action
	 * @return CustomerMasterT
	 * @throws Exception
	 */
	private CustomerMasterT constructCustomerMasterTForCustomer(List<String> listOfCellValues,String userId, String action) throws Exception {
		logger.debug("Begin:Inside constructCustomerMasterTForCustomer() for CustomerUploadService");
		CustomerMasterT customerT = null; 

		if ((listOfCellValues.size() > 0)) {

			customerT = new CustomerMasterT();

			// CREATED_MODIFIED_BY
			customerT.setCreatedModifiedBy(userId);

			// MASTER_GROUP_CLIENT
			if(!StringUtils.isEmpty(listOfCellValues.get(1))){
				customerT.setGroupCustomerName(listOfCellValues.get(1));
			} else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Contact Type NOT Found");
			}

			// MASTER_CUSTOMER_NAME	
			if(!StringUtils.isEmpty(listOfCellValues.get(2))){
				customerT.setCustomerName(listOfCellValues.get(2));
			}
			
			// IOU 
			if(listOfCellValues.get(3).length()>0){
				if(mapOfIouMappingT.containsKey(listOfCellValues.get(3))){
					customerT.setIou(listOfCellValues.get(3));
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid IOU");
				}
			}

			// MASTER_GEOGRAPHY
			if(listOfCellValues.get(4).length()>0){
				if(mapOfGeographyMappingT.containsKey(listOfCellValues.get(4))){
					customerT.setGeography(listOfCellValues.get(4));
				} else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid geography");
				}
			}
		} 
		logger.debug("End:Inside constructCustomerMasterTForCustomer() for CustomerUploadService");
		return customerT;
	}

	/**
	 * This method iterates the given row for values
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private List<String> iterateRow(Row row, int columnnCount) throws Exception{
		logger.debug("Begin:Inside iterateRow() for CustomerUploadService");
		List<String> listOfCellValues = new ArrayList<String>();

		for (int cellCount = 0; cellCount < columnnCount; cellCount++) {

			Cell cell = row.getCell(cellCount);

			String value = getIndividualCellValue(cell);

			if (value != null) {
				listOfCellValues.add(value.trim());
			}
		}
		logger.debug("End:Inside iterateRow() for CustomerUploadService");
		return listOfCellValues;

	}


	/**
	 * This method accepts a cell, checks the value and returns the response.
	 * The default value sent is an empty string
	 * @param cell
	 * @return String
	 */
	private String getIndividualCellValue(Cell cell) {

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

		Map<String, String> mapOfCMT = new HashMap<String, String>();
		logger.debug("Begin:Inside getNameAndIdFromCustomerMasterT() for CustomerUploadService");
		List<Object[]> listOfCustomerMasterT = customerRepository
				.getNameAndId();

		for (Object[] st : listOfCustomerMasterT) {
			mapOfCMT.put(st[0].toString().trim(), st[1].toString().trim());
		}
		logger.debug("End:Inside getNameAndIdFromCustomerMasterT() for CustomerUploadService");
		return mapOfCMT;
	}

	/**
	 * This method validates Customer Master Sheet
	 * @param workbook
	 * @return boolean
	 * @throws Exception
	 */
	private boolean validateSheetForCustomer(Workbook workbook) throws Exception {
		return ExcelUtils.isValidWorkbook(workbook,
				OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 4, 1)
				|| ExcelUtils.isValidWorkbook(workbook,
						OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 4, 2);
	}

}