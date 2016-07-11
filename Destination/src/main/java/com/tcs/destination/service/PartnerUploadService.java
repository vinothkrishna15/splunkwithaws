package com.tcs.destination.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UploadServiceErrorDetailsDTO;
import com.tcs.destination.bean.UploadStatusDTO;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.DocumentActionType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.OpportunityUploadConstants;
import com.tcs.destination.utils.StringUtils;


/**
 * This service uploads partner details to partner_master_t table
 * @param file
 * @param userId
 */
@Service
public class PartnerUploadService {

	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	PartnerService partnerService;

	private static final Logger logger = LoggerFactory.getLogger(PartnerUploadService.class);

	/**
	 * This method reads the Partner Master sheet and populates it with DB data
	 * @param file
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public UploadStatusDTO upload(MultipartFile file, String userId) throws Exception 
	{
		logger.debug("Begin: upload() of PartnerUploadService");
		UploadStatusDTO uploadStatus = null;
		Workbook workbook = ExcelUtils.getWorkBook(file);

		uploadStatus = new UploadStatusDTO();
		uploadStatus.setStatusFlag(true);
		uploadStatus.setListOfErrors(new ArrayList<UploadServiceErrorDetailsDTO>());

		try {
			if (validateSheetForPartner(workbook)) 
			{
				Sheet sheet = workbook.getSheet("Partner Master");
				int rowCount = 0;
				List<String> listOfCellValues = null;
				Iterator<Row> rowIterator = sheet.iterator();
				int columnSize=rowIterator.next().getLastCellNum();
				while (rowIterator.hasNext()&& rowCount <= sheet.getLastRowNum()) {
					Row row = rowIterator.next();
				
					if (rowCount > 0) {
						String actionCellValue = getIndividualCellValue(row.getCell(0));
						listOfCellValues = new ArrayList<String>();
						try {
							if (actionCellValue.equalsIgnoreCase(DocumentActionType.ADD.name())) 
							{
								listOfCellValues = iterateRow(row,columnSize);
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
		logger.debug("End: upload() of PartnerUploadService");
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

		logger.debug("Begin: constructPartnerTForPartner() of PartnerUploadService");
		PartnerMasterT partnerMasterT = null;

		if ((listOfCellValues.size() > 0)) {

			partnerMasterT = new PartnerMasterT();


			// CREATED_BY
			partnerMasterT.setCreatedBy(userId);
			
			// MODIFIED_BY
			partnerMasterT.setModifiedBy(userId);

			//DOCUMENTS_ATTACHED
			partnerMasterT.setDocumentsAttached("NO");
			
			
			

			// PARTNER_NAME
			if(!StringUtils.isEmpty(listOfCellValues.get(2))&&(listOfCellValues.get(2)!=null))
			{

				partnerMasterT.setPartnerName(listOfCellValues.get(2));
			} 
			else 
			{
				throw new DestinationException(HttpStatus.NOT_FOUND, "Partner Name NOT Found");
			}

			// GEOGRAPHY 
			if(!StringUtils.isEmpty(listOfCellValues.get(3))&&(listOfCellValues.get(3)!=null))
			{
			partnerMasterT.setGeography(listOfCellValues.get(3));
			}

			//WEBSITE (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(4))&&(listOfCellValues.get(4)!=null))
			{
				partnerMasterT.setWebsite(listOfCellValues.get(4));
			}

			//FACEBOOK (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(5))&&(listOfCellValues.get(5)!=null))
			{
				partnerMasterT.setFacebook(listOfCellValues.get(5));
			}

			//CORPORATE_HQ_ADDRESS (Optional)
			if(!StringUtils.isEmpty(listOfCellValues.get(6))&&(listOfCellValues.get(6)!=null))
			{
				partnerMasterT.setCorporateHqAddress(listOfCellValues.get(6));
			}
            
			//ACTIVE
           boolean activeFlag;
           if(!StringUtils.isEmpty(listOfCellValues.get(7))&&(listOfCellValues.get(7)!=null))
		   {
		    String active=listOfCellValues.get(7);
            if (active.equalsIgnoreCase("TRUE"))
            {
        	   activeFlag=true;
            }
            else
            {
        	   activeFlag=false;
            }
			 partnerMasterT.setActive(activeFlag);
		   }
			//COUNTRY
           if(!StringUtils.isEmpty(listOfCellValues.get(8))&&(listOfCellValues.get(8)!=null))
		   {
            partnerMasterT.setCountry(listOfCellValues.get(8));
		   }
			
           //CITY
           if(!StringUtils.isEmpty(listOfCellValues.get(9))&&(listOfCellValues.get(9)!=null))
		   {
            partnerMasterT.setCity(listOfCellValues.get(9));
		   }
           
           //TEXT1
           if(!StringUtils.isEmpty(listOfCellValues.get(10))&&(listOfCellValues.get(10)!=null))
		   {
			partnerMasterT.setText1(listOfCellValues.get(10));
		   }
           
           //TEXT2
           if(!StringUtils.isEmpty(listOfCellValues.get(11))&&(listOfCellValues.get(11)!=null))
		   {
			partnerMasterT.setText2(listOfCellValues.get(11));
		   }
           
           //TEXT3
           if(!StringUtils.isEmpty(listOfCellValues.get(12))&&(listOfCellValues.get(12)!=null))
		   {
			partnerMasterT.setText3(listOfCellValues.get(12));
		   }
           
           //GROUP_PARTNER_NAME
           if(!StringUtils.isEmpty(listOfCellValues.get(13))&&(listOfCellValues.get(13)!=null))
		   {
			partnerMasterT.setGroupPartnerName(listOfCellValues.get(13));
		   }
           
           //NOTES
           if(!StringUtils.isEmpty(listOfCellValues.get(14))&&(listOfCellValues.get(14)!=null))
		   {
			partnerMasterT.setNotes(listOfCellValues.get(14));
		   }
           
          //HQ_PARTNER_LINK_ID
           if(!StringUtils.isEmpty(listOfCellValues.get(15))&&(listOfCellValues.get(15)!=null))
		   {
			partnerMasterT.setHqPartnerLinkId(listOfCellValues.get(15));
		   }
			
		}
		logger.debug("End: constructPartnerTForPartner() of PartnerUploadService");
		return partnerMasterT;
	}

	/**
	 * This method iterates the given row for values
	 * 
	 * @param row
	 * @return
	 * @throws Exception
	 */
	private List<String> iterateRow(Row row,int columnSize) throws Exception{
		List<String> listOfCellValues = new ArrayList<String>();

		for (int cellCount = 0; cellCount < columnSize; cellCount++) {

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
		logger.debug("Inside constructPartnerTForPartner() of PartnerUploadService");
		return ExcelUtils.isValidWorkbook(workbook,
				OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 5, 1)
				|| ExcelUtils.isValidWorkbook(workbook,
						OpportunityUploadConstants.VALIDATOR_SHEET_NAME, 5, 2);
	}

	public UploadStatusDTO uploadContacts(MultipartFile file, String userId) {
		// TODO Auto-generated method stub
		return null;
	}




}
