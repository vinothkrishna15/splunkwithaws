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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tcs.destination.bean.ContactCustomerLinkT;
import com.tcs.destination.bean.ContactRoleMappingT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.PartnerMasterT;
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
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;

@Service
public class PartnerUploadService {
	/**
	 * This service uploads partner details to partner_t table
	 * 
	 * @param file
	 * @param userId
	 */
	
	@Autowired
	PartnerRepository partnerRepository;
	
	@Autowired
	PartnerService partnerService;
	
	private List<String> listOfPartnerId = null;
	
	
	public UploadStatusDTO upload(MultipartFile file, String userId) throws Exception 
	{

		UploadStatusDTO uploadStatus = null;
		Workbook workbook = ExcelUtils.getWorkBook(file);
        
		uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

		try {
			
			//listOfPartnerId =  partnerRepository.findPartnerIdFromPartnerT();
            
            if (validateSheetForPartner(workbook)) 
            {
				
				//mapOfPartnerMasterT = getNameAndIdFromPartnerMasterT();
				
				Sheet sheet = workbook.getSheet("Partner Master");

				int rowCount = 0;
				
				List<String> listOfCellValues = null;

				Iterator<Row> rowIterator = sheet.iterator();
				
				while (rowIterator.hasNext()&& rowCount <= 66) {

					Row row = rowIterator.next();

					if (rowCount > 0) {
						String actionCellValue = getIndividualCellValue(row.getCell(0));
						System.out.println("row count : "+rowCount);
						listOfCellValues = new ArrayList<String>();
						try {
						if (actionCellValue.equalsIgnoreCase(DocumentActionType.ADD.name())) 
						{
							System.out.println("Cell 0 at "+rowCount+" : "+actionCellValue);
							listOfCellValues = iterateRow(row);
							partnerService.addPartner(constructPartnerTForPartner(listOfCellValues, userId, DocumentActionType.ADD.name()));
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
				
			} 
            else 
            {
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Validation has failed in Validate Sheet");
			}

	} catch(Exception e){
		e.printStackTrace();
	}
	return uploadStatus;
	}
	
	/**
	 * This method constructs ContactT for the given input for Partner Upload
	 * 
	 * @param listOfCellValues
	 * @param contactCategory
	 * @param userId
	 * @param action
	 * @return PartnerT
	 * @throws Exception
	 */
	private PartnerMasterT constructPartnerTForPartner(List<String> listOfCellValues,
			 String userId, String action) throws Exception{

		PartnerMasterT partnerMasterT = null;
		
		if ((listOfCellValues.size() > 0)) {
			
			partnerMasterT = new PartnerMasterT();
		
			
            // CREATED_MODIFIED_BY
			partnerMasterT.setCreatedModifiedBy(userId);
			
			//DOCUMENTS_ATTACHED
			partnerMasterT.setDocumentsAttached("NO");
			
			// PARTNER_NAME
			if(!StringUtils.isEmpty(listOfCellValues.get(2)))
			{
				
				partnerMasterT.setPartnerName(listOfCellValues.get(2));
			} 
			else 
			{
				throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name NOT Found");
			}
			
			// GEOGRAPHY 
			if(!StringUtils.isEmpty(listOfCellValues.get(3)))
			{
				GeographyMappingT geography=new GeographyMappingT();
				geography.setGeography(listOfCellValues.get(3));
				partnerMasterT.setGeographyMappingT(geography);
			}
			
			//WEBSITE (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(4)))
			{
				partnerMasterT.setWebsite(listOfCellValues.get(4));
			}
			
			//FACEBOOK (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(5)))
			{
				partnerMasterT.setFacebook(listOfCellValues.get(5));
			}
			
			//CORPORATE_HQ_ADDRESS (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(6)))
			{
				partnerMasterT.setCorporateHqAddress(listOfCellValues.get(6));
			}
			
			
		}
		
		return partnerMasterT;
	}
	
	/**
	 * This method iterates the given row for values
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private List<String> iterateRow(Row row) throws Exception{

		List<String> listOfCellValues = new ArrayList<String>();
				
		for (int cellCount = 0; cellCount < 11; cellCount++) {

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
     * 
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
	 * This method validates Partner Master Sheet
	 * 
	 * @param workbook
	 * @return boolean
	 * @throws Exception
	 */
	private boolean validateSheetForPartner(Workbook workbook) throws Exception 
	  {
		  return ExcelUtils.isValidWorkbook(workbook,
			OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 5, 1)
			|| ExcelUtils.isValidWorkbook(workbook,
				OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 5, 2);
	    }



	
}
