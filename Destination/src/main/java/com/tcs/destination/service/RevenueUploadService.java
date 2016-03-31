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
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.enums.DocumentActionType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ContactsUploadConstants;
import com.tcs.destination.utils.CustomerUploadConstants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;

/**
 *This service handles the revenue upload operations
 *in which data from excel is written into the database 
 *
 */
@Service
public class RevenueUploadService {
	@Autowired
	CustomerService customerService;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	RevenueService revenueService;

	@Autowired
	CustomerIOUMappingRepository iouCustomerMappingRepository;

	@Autowired
	GeographyRepository geographyRepository;

	Map<String, String> mapOfCustomerNamesT = null;
	Map<String, GeographyMappingT> mapOfGeographyMappingT = null;
	Map<String,IouCustomerMappingT> mapOfIouCustomerMappingT = null;

	private static final Logger logger = LoggerFactory.getLogger(RevenueUploadService.class);

	/* This service uploads customers to customer_master_t table
	 * @param file
	 * @param userId*/

	public UploadStatusDTO upload(MultipartFile file, String userId)
			throws Exception {
		 logger.debug("Begin: inside upload() of RevenueUploadService");
		Workbook workbook = ExcelUtils.getWorkBook(file);
		UploadStatusDTO uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

		// Get List of customer names from DB for validating the customer names which comes from the sheet	
		mapOfCustomerNamesT = getCustomerNamesTMappingT();

		// Get List of geographies from DB for validating the geographies which comes from the sheet	
		mapOfGeographyMappingT = getGeographyMappingT();

		// Get List of IOU from DB for validating the IOU which comes from the sheet	
		mapOfIouCustomerMappingT = getIouCustomerMappingT();
		// To check if no validation errors are present in the workbook
		if (validateSheetForCustomer(workbook)) { 

			Sheet sheet = workbook.getSheet(CustomerUploadConstants.FINANACE_MAPPING_SHEET_NAME);
			if(sheet==null){
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Please upload the workbook for FINANCE MAPPING UPLOAD or missing " +CustomerUploadConstants.FINANACE_MAPPING_SHEET_NAME +" sheet");
			}

			int rowCount = 0;
			List<String> listOfCellValues = null;

			Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()&& rowCount <= sheet.getLastRowNum()) {

				Row row = rowIterator.next();

				if (rowCount > 0) {
					String actionCellValue = getIndividualCellValue(row.getCell(0));
					logger.debug("row count : "+rowCount);
					listOfCellValues = new ArrayList<String>();
					try {
						if (actionCellValue.equalsIgnoreCase(DocumentActionType.ADD.name())) {
							listOfCellValues = iterateRow(row, CustomerUploadConstants.FINANACE_MAPPING_COLUMN_COUNT);
							revenueService.addFinance(constructRevenueCustomerMappingT(listOfCellValues,  userId, DocumentActionType.ADD.name()));
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
		 logger.debug("End: inside upload() of RevenueUploadService");
		return uploadStatus;

	}

	/**
	 * To generate a map of CustomerMasterT and customerName
	 * @return
	 */
	private Map<String, String> getCustomerNamesTMappingT() {
		 logger.debug("Begin: inside getCustomerNamesTMappingT() of RevenueUploadService");
		List<CustomerMasterT> listOfCustomerMappingT = null;
		listOfCustomerMappingT = (List<CustomerMasterT>) customerRepository.findAll();
		Map<String, String> customerMap = new HashMap<String, String>();
		for (CustomerMasterT customerMasterT : listOfCustomerMappingT) {
			customerMap.put(customerMasterT.getCustomerName(),customerMasterT.getGroupCustomerName());
		}
		 logger.debug("End: inside getCustomerNamesTMappingT() of RevenueUploadService");
		return customerMap;
	}


	/**
	 * To generate a map of IouCustomerMappingT and customerName
	 * @return
	 */
	private Map<String, IouCustomerMappingT> getIouCustomerMappingT() {
		 logger.debug("Begin: inside getIouCustomerMappingT() of RevenueUploadService");

		List<IouCustomerMappingT> listOfIouCustomerMappingT = null;
		listOfIouCustomerMappingT = (List<IouCustomerMappingT>) iouCustomerMappingRepository.findAll();
		Map<String, IouCustomerMappingT> iouMap = new HashMap<String, IouCustomerMappingT>();
		for (IouCustomerMappingT iouCustomerMappingT : listOfIouCustomerMappingT) {
			iouMap.put(iouCustomerMappingT.getIou(), iouCustomerMappingT);
		}
		 logger.debug("End: inside getIouCustomerMappingT() of RevenueUploadService");
		return iouMap;
	}

	/**
	 * This method creates a geography Map
	 * @return geographyMap
	 */
	private Map<String, GeographyMappingT> getGeographyMappingT() {
		 logger.debug("Begin: inside getGeographyMappingT() of RevenueUploadService");
		List<GeographyMappingT> listOfGeographyMappingT = null;
		listOfGeographyMappingT = (List<GeographyMappingT>) geographyRepository.findAll();
		Map<String, GeographyMappingT> geographyMap = new HashMap<String, GeographyMappingT>();
		for (GeographyMappingT geographyMappingT : listOfGeographyMappingT) {
			geographyMap.put(geographyMappingT.getGeography(), geographyMappingT);
		}
		 logger.debug("End: inside getGeographyMappingT() of RevenueUploadService");
		return geographyMap;
	}

	/**
	 * This method constructs CustomerMasterT for Customer Upload
	 * @param listOfCellValues
	 * @param userId
	 * @param action
	 * @return CustomerMasterT
	 * @throws Exception
	 */
	private RevenueCustomerMappingT constructRevenueCustomerMappingT(List<String> listOfCellValues,String userId, String action) throws Exception {
		RevenueCustomerMappingT revenueT = null;
		 logger.debug("Begin: inside constructRevenueCustomerMappingT() of RevenueUploadService");
		if ((listOfCellValues.size() > 0)) {
			revenueT = new RevenueCustomerMappingT();

			// CUSTOMER_NAME
			if(!StringUtils.isEmpty(listOfCellValues.get(2))){
				if(mapOfCustomerNamesT.containsKey(listOfCellValues.get(2))){
					CustomerMasterT customerMasterT=customerRepository.findByCustomerName(listOfCellValues.get(2));
					revenueT.setCustomerId(customerMasterT.getCustomerId());
					//revenueT.setCustomerName(listOfCellValues.get(2));
				}
				else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "customer name is not present in customer table");
				}
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Customer Name NOT Found");
			}

			// FINANACE_CUSTOMER_NAME
			if(!StringUtils.isEmpty(listOfCellValues.get(5))){
				revenueT.setFinanceCustomerName(listOfCellValues.get(5));
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "finance Customer Name NOT Found");
			}


			// FINANACE_IOU
			if(!StringUtils.isEmpty(listOfCellValues.get(6))){
				if(mapOfIouCustomerMappingT.containsKey(listOfCellValues.get(6))){
					revenueT.setFinanceIou(listOfCellValues.get(6));
				}
				else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "finance iou is not present in master table");
				}
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Finanace IOU NOT Found");
			}

			// FINANACE_GEOGRAPHY
			if(listOfCellValues.get(7).length()>0){
				if(mapOfGeographyMappingT.containsKey(listOfCellValues.get(7))){
					revenueT.setCustomerGeography(listOfCellValues.get(7));
				}
				else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "CustomerGeography is not present in master table");
				}
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Finanace Geography NOT Found");
			}
			} 
		 logger.debug("End: inside constructRevenueCustomerMappingT() of RevenueUploadService");
			return revenueT;
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
			 logger.debug("Begin: inside iterateRow() of RevenueUploadService");
			for (int cellCount = 0; cellCount < columnnCount; cellCount++) {

				Cell cell = row.getCell(cellCount);

				String value = getIndividualCellValue(cell);

				if (value != null) {
					listOfCellValues.add(value.trim());
				}
			}
			 logger.debug("End: inside iterateRow() of RevenueUploadService");
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
