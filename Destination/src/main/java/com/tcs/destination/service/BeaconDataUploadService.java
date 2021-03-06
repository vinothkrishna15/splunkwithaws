package com.tcs.destination.service;

import java.math.BigDecimal;
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

import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.IouBeaconMappingTRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ContactsUploadConstants;
import com.tcs.destination.utils.CustomerUploadConstants;
import com.tcs.destination.utils.DestinationUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;
/**
 * This service deals with beacon data upload requests 
 *
 */
@Service
public class BeaconDataUploadService {
	@Autowired
	BeaconDataService beaconDataService;

	@Autowired
	IouBeaconMappingTRepository iouBeaconMappingTRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	GeographyRepository geographyRepository;

	Map<String,IouBeaconMappingT> mapOfBeaconIouMappingT = null;

	private static final Logger logger = LoggerFactory.getLogger(BeaconDataUploadService.class);

	/* This service uploads beacon data to beacon_data_t table
	 * @param file
	 * @param userId*/

	public UploadStatusDTO upload(MultipartFile file)
			throws Exception {
		logger.debug("Begin:inside upload() of BeaconDataUploadService");
		String userId= DestinationUtils.getCurrentUserDetails().getUserId();
		Workbook workbook = ExcelUtils.getWorkBook(file);
		UploadStatusDTO uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

		// Get List of IOU from DB table iou_beacon_mapping_t for validating the IOU which comes from the sheet	
		mapOfBeaconIouMappingT = getBeaconIouMappingT();
		
		// To check if no validation errors are present in the workbook
		if (validateSheetForCustomer(workbook)) { 

			Sheet sheet = workbook.getSheet(CustomerUploadConstants.BEACON_DATA_SHEET_NAME);
			if(sheet==null){
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Please upload the workbook for BEACON CUSTOMER UPLOAD or missing " +CustomerUploadConstants.BEACON_DATA_SHEET_NAME +" sheet");
			}

			int rowCount = 0;
			List<String> listOfCellValues = null;

			Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()&& rowCount <= sheet.getLastRowNum()) {
				Row row = rowIterator.next();

				if (rowCount > 0) {
					listOfCellValues = new ArrayList<String>();
					try {
						listOfCellValues = iterateRow(row, CustomerUploadConstants.BEACON_DATA_COLUMN_COUNT);
						beaconDataService.addBeaconData(constructBeaconDataT(listOfCellValues,userId));
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
		logger.debug("End:inside upload() of BeaconDataUploadService");
		return uploadStatus;
	}

	private Map<String, IouBeaconMappingT> getBeaconIouMappingT() {
		List<IouBeaconMappingT> listOfIouBeconMappingT = null;
		logger.debug("Begin:inside getBeaconIouMappingT() of BeaconDataUploadService");
		listOfIouBeconMappingT = (List<IouBeaconMappingT>) iouBeaconMappingTRepository.findAll();
		Map<String, IouBeaconMappingT> iouMap = new HashMap<String, IouBeaconMappingT>();
		for (IouBeaconMappingT iouBeaconMappingT : listOfIouBeconMappingT) {
			iouMap.put(iouBeaconMappingT.getBeaconIou(), iouBeaconMappingT);
		}
		logger.debug("End:inside getBeaconIouMappingT() of BeaconDataUploadService");
		return iouMap;
	}

	/**
	 * This method constructs CustomerMasterT for Customer Upload
	 * @param listOfCellValues
	 * @param userId
	 * @param action
	 * @return CustomerMasterT
	 * @throws Exception
	 */
	private BeaconDataT constructBeaconDataT(List<String> listOfCellValues,String userId) throws Exception {
		BeaconDataT beaconDataT = null;
		logger.debug("Begin:inside constructBeaconDataT() of BeaconDataUploadService");
		
		if ((listOfCellValues.size() > 0)) {
			
		  
			
			beaconDataT = new BeaconDataT();
			
			// BEACON_GROUP_CLIENT - does not have NOT_NULL constraint
			if(!StringUtils.isEmpty(listOfCellValues.get(2))){
				beaconDataT.setBeaconGroupClient(listOfCellValues.get(2));
			}

			// FINANCIAL YEAR 
			if(!StringUtils.isEmpty(listOfCellValues.get(6))){
				beaconDataT.setFinancialYear(listOfCellValues.get(6));
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Finanacial Year NOT Found");
			}


			// QUARTER
			if(!StringUtils.isEmpty(listOfCellValues.get(7))){
				beaconDataT.setQuarter(listOfCellValues.get(7));
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "Quarter NOT Found");
			}
			
			List<BeaconCustomerMappingT> beaconCustomers = null;
			
			beaconCustomers = beaconRepository.findbeaconDuplicates(listOfCellValues.get(1),listOfCellValues.get(0),listOfCellValues.get(3));
			if ((!beaconCustomers.isEmpty()) && (beaconCustomers.size() == 1)) 
			{
				// GEOGRAPHY TO BEACON_GEOGRAPHY 
				if(StringUtils.isEmpty(listOfCellValues.get(0))){
				
					throw new DestinationException(HttpStatus.NOT_FOUND, "Beacon Geography NOT Found");
				}
				// BEACON_CUSTOMER_NAME 
				if(StringUtils.isEmpty(listOfCellValues.get(1))){
					 
					throw new DestinationException(HttpStatus.NOT_FOUND, "Beacon CustomerName NOT Found");
				}
				// BEACON_CUSTOMER_IOU
				// to find whether beacon_iou has foreign key existence in iou_beacon_mapping_t
				if(StringUtils.isEmpty(listOfCellValues.get(3))){
				
					throw new DestinationException(HttpStatus.NOT_FOUND, "Beacon Customer IOU NOT Found");
				}
				
				// BEACON_CUSTOMER_MAP_ID
				BeaconCustomerMappingT beaconCustomerMappingT=beaconCustomers.get(0);
				Long beaconCustomerMapId=beaconCustomerMappingT.getBeaconCustomerMapId();
				beaconDataT.setBeaconCustomerMapId(beaconCustomerMapId);
				 
				  
			}
			
			// TARGET
		    if(!StringUtils.isEmpty(listOfCellValues.get(9)))
		    {
			  BigDecimal target=new BigDecimal(listOfCellValues.get(9));
			  beaconDataT.setTarget(target);
			 }
			else {
							throw new DestinationException(HttpStatus.NOT_FOUND, "TARGET NOT Found");
			}
		} 
		logger.debug("End:inside constructBeaconDataT() of BeaconDataUploadService");
		return beaconDataT;
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
		logger.debug("Begin:inside iterateRow() of BeaconDataUploadService");
		for (int cellCount = 0; cellCount < columnnCount; cellCount++) {

			Cell cell = row.getCell(cellCount);

			String value = getIndividualCellValue(cell);

			if (value != null) {
				listOfCellValues.add(value.trim());
			}
		}
		logger.debug("End:inside iterateRow() of BeaconDataUploadService");
		return listOfCellValues;
	}

	/**
	 * This method accepts a cell, checks the value and returns the response.
	 * The default value sent is an empty string
	 * @param cell
	 * @return String
	 */
	private String getIndividualCellValue(Cell cell) {
		logger.debug("Begin:inside getIndividualCellValue() of BeaconDataUploadService");
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
		logger.debug("End:inside getIndividualCellValue() of BeaconDataUploadService");
		return val;

	}

	/**
	 * This method validates Customer Master Sheet
	 * @param workbook
	 * @return boolean
	 * @throws Exception
	 */
	private boolean validateSheetForCustomer(Workbook workbook) throws Exception {
		logger.debug("inside validateSheetForCustomer() of BeaconDataUploadService");
		return ExcelUtils.isValidWorkbook(workbook,
				OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 8, 1)
				|| ExcelUtils.isValidWorkbook(workbook,
						OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 8, 2);
	}

}
