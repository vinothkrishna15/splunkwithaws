package com.tcs.destination.service;

import java.math.BigDecimal;
import java.text.ParseException;
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

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ContactsUploadConstants;
import com.tcs.destination.utils.CustomerUploadConstants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;

@Service
public class ActualRevenueDataUploadService {
	@Autowired
	CustomerService customerService;

	@Autowired
	RevenueService revenueService;

	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;
	
	@Autowired
	RevenueCustomerMappingTRepository revenueCustomerMappingTRepository;
	
	@Autowired
	CustomerIOUMappingRepository iouCustomerMappingRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	GeographyRepository geographyRepository;

	@Autowired
	SubSpRepository subSpRepository;

	Map<String, String> mapOfCustomerNamesT = null;
	Map<String, SubSpMappingT> mapOfSubSpMappingT = null;
	Map<String,IouCustomerMappingT> mapOfIouCustomerMappingT = null;

	private static final Logger logger = LoggerFactory.getLogger(CustomerUploadService.class);

	/* This service uploads revenue actual data to actual_revenue_data_t table
	 * @param file
	 * @param userId*/

	public UploadStatusDTO upload(MultipartFile file, String userId)
			throws Exception {
		Workbook workbook = ExcelUtils.getWorkBook(file);
		UploadStatusDTO uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

		// Get List of IOU from DB for validating the IOU which comes from the sheet	
		mapOfIouCustomerMappingT = getIouCustomerMappingT();

		// Get List of IOU from DB for validating the IOU which comes from the sheet	
		mapOfSubSpMappingT = getSubSpMappingT();

		// To check if no validation errors are present in the workbook
		if (validateSheetForCustomer(workbook)) { 

			Sheet sheet = workbook.getSheet(CustomerUploadConstants.ACTUAL_REVENUE_DATA_SHEET_NAME);
			if(sheet==null){
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Please upload the workbook for BEACON CUSTOMER UPLOAD or missing " +CustomerUploadConstants.ACTUAL_REVENUE_DATA_SHEET_NAME +" sheet");
			}

			int rowCount = 0;
			List<String> listOfCellValues = null;

			Iterator<Row> rowIterator = sheet.iterator();

			while (rowIterator.hasNext()&& rowCount <= sheet.getLastRowNum()) {
				Row row = rowIterator.next();

				if (rowCount > 0) {
					logger.debug("row count : "+rowCount);
					listOfCellValues = new ArrayList<String>();
					try {
							listOfCellValues = iterateRow(row, CustomerUploadConstants.ACTUAL_REVENUE_DATA_COLUMN_COUNT);
							revenueService.addActualRevenue(constructActualRevenuesDataT(listOfCellValues, userId));
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

		return uploadStatus;

	}

	private Map<String, IouCustomerMappingT> getIouCustomerMappingT() {
		List<IouCustomerMappingT> listOfIouCustomerMappingT = null;
		listOfIouCustomerMappingT = (List<IouCustomerMappingT>) iouCustomerMappingRepository.findAll();
		Map<String, IouCustomerMappingT> iouMap = new HashMap<String, IouCustomerMappingT>();
		for (IouCustomerMappingT iouCustomerMappingT : listOfIouCustomerMappingT) {
			iouMap.put(iouCustomerMappingT.getIou(), iouCustomerMappingT);
		}
		return iouMap;
	}
	
	// to get the map of SUB_SP
	private Map<String, SubSpMappingT> getSubSpMappingT() {
		List<SubSpMappingT> listOfSubSpMappingT = null;
		listOfSubSpMappingT = (List<SubSpMappingT>) subSpRepository.findAll();
		Map<String, SubSpMappingT> subSpMap = new HashMap<String, SubSpMappingT>();
		for (SubSpMappingT subSpMappingT : listOfSubSpMappingT) {
			subSpMap.put(subSpMappingT.getActualSubSp(), subSpMappingT);
		}
		return subSpMap;
	}

	/**
	 * This method constructs ActualRevenuesDataT for actual revenue data upload
	 * @param listOfCellValues
	 * @param userId
	 * @param action
	 * @return ActualRevenuesDataT
	 * @throws Exception
	 */
		private ActualRevenuesDataT constructActualRevenuesDataT(List<String> listOfCellValues,String userId) throws Exception {
		ActualRevenuesDataT actualRevenueT = null;
		if ((listOfCellValues.size() > 0)) {
			actualRevenueT = new ActualRevenuesDataT();
			// QUARTER
			if(StringUtils.isEmpty(listOfCellValues.get(4))){
				throw new DestinationException(HttpStatus.NOT_FOUND, "QUARTER NOT Found");
				
			}

			// MONTH
			if(!StringUtils.isEmpty(listOfCellValues.get(3))){
				
				try {
					String[] strArr = DateUtils.formatUploadDateData(listOfCellValues.get(3), PropertyUtil.getProperty("upload.month.db.format"), PropertyUtil.getProperty("upload.month.format"));
					actualRevenueT.setMonth(strArr[0]);
					actualRevenueT.setQuarter(strArr[1]);
					actualRevenueT.setFinancialYear(strArr[2]);

				} catch (ParseException e) {
					throw new DestinationException(HttpStatus.NOT_FOUND, "Invalid month format.");
				}
				
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "MONTH NOT Found");
			}

			// FINANCIAL YEAR
			if(StringUtils.isEmpty(listOfCellValues.get(5))){
				throw new DestinationException(HttpStatus.NOT_FOUND, "FINANCIAL YEAR NOT Found");
			}
			// REVENUE AMOUNT
			if(!StringUtils.isEmpty(listOfCellValues.get(6))){
				BigDecimal target=new BigDecimal(listOfCellValues.get(6));
				actualRevenueT.setRevenue((target));
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "REVENUE AMOUNT NOT Found");
			}

			// CLIENT COUNTRY NAME
			if(!StringUtils.isEmpty(listOfCellValues.get(7))){
				actualRevenueT.setClientCountry(listOfCellValues.get(7));
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "CLIENT COUNTRY NAME NOT Found");
			}

			//SUB_SP
			if(!StringUtils.isEmpty(listOfCellValues.get(9))){
				if(mapOfSubSpMappingT.containsKey(listOfCellValues.get(9))){
				actualRevenueT.setSubSp(listOfCellValues.get(9));
				}
			}
			else {
				throw new DestinationException(HttpStatus.NOT_FOUND, "SUB SP NOT Found");
			}
			
			List<RevenueCustomerMappingT> revenueCustomerData = null;
			// to find whether finance_geography, finance_iou, finance_customer_name and (composite key) has foreign key existence in revenue_customer_mapping_t
			revenueCustomerData = revenueCustomerMappingTRepository.checkRevenueMappingPK(listOfCellValues.get(10),listOfCellValues.get(8),listOfCellValues.get(11));
			
			if ((!revenueCustomerData.isEmpty()) && (revenueCustomerData.size() == 1)) 
			{
				//FINANACE EOGRAPHY
				if(!StringUtils.isEmpty(listOfCellValues.get(8))){
					actualRevenueT.setFinanceGeography(listOfCellValues.get(8));
				}
				else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "CLIENT GEOGRAPHY NOT Found");
				}
				
				//END CUSTOMER NAME
				if(!StringUtils.isEmpty(listOfCellValues.get(10))){
					actualRevenueT.setFinanceCustomerName(listOfCellValues.get(10));
				}
				else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "END CUSTOMER NAME NOT Found");
				}
				
				//IOU
				if(!StringUtils.isEmpty(listOfCellValues.get(11))){
					if(mapOfIouCustomerMappingT.containsKey(listOfCellValues.get(11))){
						actualRevenueT.setFinanceIou(listOfCellValues.get(11));
					}
				}
				else {
					throw new DestinationException(HttpStatus.NOT_FOUND, "IOU NOT Found");
				}
			}
		} 
		
		return actualRevenueT;
	}

	/**
	 * This method iterates the given row for values
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private List<String> iterateRow(Row row, int columnnCount) throws Exception{
		List<String> listOfCellValues = new ArrayList<String>();

		for (int cellCount = 0; cellCount < columnnCount; cellCount++) {

			Cell cell = row.getCell(cellCount);

			String value = getIndividualCellValue(cell);

			if (value != null) {
				listOfCellValues.add(value.trim());
			}
		}

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
