package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectCustomerContactLinkT;
import com.tcs.destination.bean.ConnectOfferingLinkT;
import com.tcs.destination.bean.ConnectSubSpLinkT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.ConnectTcsAccountContactLinkT;
import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.NotesT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyReaderUtil;

@Service
public class PartnerDownloadService 
{
	@Autowired
	PartnerRepository partnerRepository;

	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheets;

	@Autowired
	ContactRepository contactRepository;

	private static final Logger logger = LoggerFactory.getLogger(PartnerDownloadService .class);

	public InputStreamResource getPartners(boolean oppFlag) throws Exception 
	{
		logger.info("Inside getPartners() method"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook = ExcelUtils.getWorkBook(new File(PropertyReaderUtil.readPropertyFile(
					Constants.APPLICATION_PROPERTIES_FILENAME, 
					Constants.PARTNER_TEMPLATE_LOCATION_PROPERTY_NAME)));
			// Populate Partner Master Sheet
			if(oppFlag)
				populatePartnerMasterSheet(workbook.getSheet(Constants.PARTNER_TEMPLATE_PARTNER_SHEET_NAME));

			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"An Internal Exception has occured");
		}
		return inputStreamResource;
	}

	public InputStreamResource getPartnerContacts(boolean oppFlag) throws Exception 
	{
		logger.info("Inside getPartnerscontacts() method"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook = ExcelUtils.getWorkBook(new File(PropertyReaderUtil.readPropertyFile(
					Constants.APPLICATION_PROPERTIES_FILENAME, 
					Constants.PARTNER_CONTACT_TEMPLATE_LOCATION_PROPERTY_NAME)));
			// Populate Partner Contacts Sheet
			if(oppFlag){
				populateContactSheets(workbook.getSheet(Constants.PARTNER_TEMPLATE_PARTNER_CONTACT_SHEET_NAME));
			}
			// Populate Partner Master Sheet  
			populatePartnerMasterSheet(workbook.getSheet(Constants.PARTNER_MASTER_REF_PARTNER_SHEET_NAME));

			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,"An Internal Exception has occured");
		}
		return inputStreamResource;
	}
	/**
	 * This Method Writes partner names into the workbook
	 * 
	 */
	private void  populatePartnerMasterSheet(Sheet partnerSheet) 
	{
		//Get the Partner Master Sheet From Workbook
		logger.info("Populating Partner Master Sheet"); 


		int currentRow = 1; // Excluding the header, header starts with index 0

		List<Object[]> partnerMasterNamesList=partnerRepository.getPartnerNameAndGeography();
		for(Object[] partnerName:partnerMasterNamesList){

			Row row = partnerSheet.createRow(currentRow);

			// Get Cell and set cell value
			row.createCell(2).setCellValue(partnerName[0].toString());
			row.createCell(3).setCellValue(partnerName[1].toString());

			// Increment row counter
			currentRow++;
		}
	}

	/**
	 * This method populates the partner contacts sheet 
	 * 
	 * @param partnerContactSheet
	 */
	public void populateContactSheets(Sheet partnerContactSheet) throws Exception
	{

		List<ContactT> listOfContact = (List<ContactT>) contactRepository.findAll();

		if(listOfContact!=null) {
			int rowCountPartnerSheet = 1; // Excluding the header, header starts with index 0
			for (ContactT ct : listOfContact) {

				if ((ct.getContactCategory().equals(EntityType.PARTNER.toString()) && 
						(ct.getContactType().equals(ContactType.EXTERNAL.toString())))) { // For Partner Contact

					// Create row with rowCount
					Row row = partnerContactSheet.createRow(rowCountPartnerSheet);

					// Create new Cell and set cell value
					Cell cellPartnerName = row.createCell(1);
					try {
						cellPartnerName.setCellValue(ct.getPartnerMasterT().getPartnerName().trim());
					} catch(NullPointerException npe){
						throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR, "Partner Contact cannot exist without Partner");
					}

					Cell cellPartnerContactName = row.createCell(2);
					cellPartnerContactName.setCellValue(ct.getContactName());

					Cell cellPartnerContactRole = row.createCell(3);
					cellPartnerContactRole.setCellValue(ct.getContactRole());

					Cell cellPartnerContactEmailId = row.createCell(4);
					if(ct.getContactEmailId()!=null) {
						cellPartnerContactEmailId.setCellValue(ct.getContactEmailId());
					}

					// Increment row counter for partner contact sheet
					rowCountPartnerSheet++;

				}
			}
		}

	}

}
