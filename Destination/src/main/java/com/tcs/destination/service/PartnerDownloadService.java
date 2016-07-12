package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ContactT;
import com.tcs.destination.bean.PartnerMasterT;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.PartnerRepository;
import com.tcs.destination.enums.ContactType;
import com.tcs.destination.enums.EntityType;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyUtil;

/**
 * This service downloads partner data from database into an excel
 */
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

	/**
	 * this method downloads the sheet Partner Master
	 * @param oppFlag
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getPartners(boolean oppFlag) throws Exception 
	{
		logger.debug("Begin:Inside getPartners() method of PartnerDownloadService"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook =ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.PARTNER_TEMPLATE_LOCATION_PROPERTY_NAME)));

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
		logger.debug("End:Inside getPartners() method of PartnerDownloadService"); 
		return inputStreamResource;
	}

	/**
	 * this method downloads the sheet Partner Contacts
	 * @param oppFlag
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getPartnerContacts(boolean oppFlag) throws Exception 
	{
		logger.debug("Begin:Inside getPartnerContacts() method of PartnerDownloadService"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook =(XSSFWorkbook) ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.PARTNER_CONTACT_TEMPLATE_LOCATION_PROPERTY_NAME)));

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
		logger.debug("End:Inside getPartnerContacts() method of PartnerDownloadService"); 
		return inputStreamResource;
	}

	/**
	 * This Method Writes partner names into the workbook
	 * @param partnerSheet
	 */
	private void  populatePartnerMasterSheet(Sheet partnerSheet) 
	{
		//Get the Partner Master Sheet From Workbook
		logger.debug("Begin:Inside populatePartnerMasterSheet() method of PartnerDownloadService"); 
		int currentRow = 1; // Excluding the header, header starts with index 0

		List<PartnerMasterT> partnerMasterNamesList=(List<PartnerMasterT>) partnerRepository.findAll();
		if(!(partnerMasterNamesList.isEmpty())&&(partnerMasterNamesList!=null))
		{
		for(PartnerMasterT partner:partnerMasterNamesList){

			Row row = partnerSheet.createRow(currentRow);

			// Get Cell and set cell value
		    if(partner.getPartnerId()!=null)
		    {
			row.createCell(1).setCellValue(partner.getPartnerId().toString());
		    }
		    if(partner.getPartnerName()!=null)
		    {
			row.createCell(2).setCellValue(partner.getPartnerName().toString());
		    }
		    if(partner.getGeography()!=null)
		    {
			row.createCell(3).setCellValue(partner.getGeography().toString());
		    }
		    if(partner.getWebsite()!=null)
		    {
			row.createCell(4).setCellValue(partner.getWebsite().toString());
		    }
		    if(partner.getFacebook()!=null)
		    {
			row.createCell(5).setCellValue(partner.getFacebook().toString());
		    }
		    if(partner.getCorporateHqAddress()!=null)
		    {
			row.createCell(6).setCellValue(partner.getCorporateHqAddress().toString());
		    }
		    
			row.createCell(7).setCellValue(partner.isActive());//TODO inactive indicator - adding a separate column in template with data - done 
		    
			if(partner.getCountry()!=null)
		    {
			row.createCell(8).setCellValue(partner.getCountry().toString());
		    }
			if(partner.getCity()!=null)
		    {
			row.createCell(9).setCellValue(partner.getCity().toString());
		    }
			if(partner.getText1()!=null)
			{
			row.createCell(10).setCellValue(partner.getText1().toString());
			}
			if(partner.getText2()!=null)
			{
			row.createCell(11).setCellValue(partner.getText2().toString());
			}
			if(partner.getText3()!=null)
			{
			row.createCell(12).setCellValue(partner.getText3().toString());
			}
			if(partner.getGroupPartnerName()!=null)
			{
			row.createCell(13).setCellValue(partner.getGroupPartnerName().toString());
			}
			if(partner.getNotes()!=null)
			{
			row.createCell(14).setCellValue(partner.getNotes().toString());
			}
			if(partner.getHqPartnerLinkId()!=null)
			{
			row.createCell(15).setCellValue(partner.getHqPartnerLinkId().toString());
			}
			// Increment row counter
			currentRow++;
		 }
		}
		logger.debug("End:Inside populatePartnerMasterSheet() method of PartnerDownloadService"); 
	}

	/**
	 * This method populates the partner contacts sheet 
	 * 
	 * @param partnerContactSheet
	 */
	public void populateContactSheets(Sheet partnerContactSheet) throws Exception
	{
		logger.debug("Begin:Inside populateContactSheets() method of PartnerDownloadService"); 
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
						cellPartnerName.setCellValue(ct.getPartnerContactLinkTs().get(0).getPartnerMasterT().getPartnerName().trim());
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

					Cell active = row.createCell(7);//TODO inactive indicator - added a separate column for active flag - done
					active.setCellValue(ct.isActive());

					// Increment row counter for partner contact sheet
					rowCountPartnerSheet++;

				}
			}
		}
		logger.debug("Begin:Inside populateContactSheets() method of PartnerDownloadService"); 
	}
}
