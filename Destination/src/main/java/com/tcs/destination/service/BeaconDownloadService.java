package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

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
import com.tcs.destination.bean.BeaconDataT;
import com.tcs.destination.bean.IouBeaconMappingT;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.IouBeaconMappingTRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyUtil;

/**
 * This service deals with requests related to beacon download 
 *
 */
@Service
public class BeaconDownloadService 
{
	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	BeaconRepository beaconRepository;

	@Autowired
	IouBeaconMappingTRepository iouBeaconMappingTRepository;


	private static final Logger logger = LoggerFactory.getLogger(BeaconDownloadService.class);

	public InputStreamResource getBeaconData(boolean oppFlag) throws Exception 
	{


		logger.debug("Begin:Inside getBeaconData() method"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook = ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.BEACON_TEMPLATE_LOCATION_PROPERTY_NAME)));
			
			if(oppFlag){
				// Populate BEACON Data Sheet
				populateBeaconDataSheet(workbook.getSheet(Constants.BEACON_TEMPLATE_BEACON_SHEET_NAME));
			}
			// Populate BEACON Mapping(Ref) Sheet
			populateBeaconMappingSheet(workbook.getSheet(Constants.BEACON_MAPPING_TEMPLATE_BEACON_SHEET_NAME));

			// Populate IOU Mapping(Ref) Sheet
			populateIouMappingSheet(workbook.getSheet(Constants.BEACON_IOU_MAPPING_TEMPLATE_BEACON_SHEET_NAME));

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
		logger.debug("End:Inside getBeaconData() method"); 
		return inputStreamResource;
	}

	/**
	 * This Method Writes BEACON data into the workbook
	 * 
	 */
	private void  populateBeaconDataSheet(Sheet beaconDataSheet) 
	{
		//Get the Beacon Data Sheet From Workbook
		logger.debug("Begin:Inside populateBeaconDataSheet() method"); 

		int currentRow = 1; // Excluding the header, header starts with index 0

		List<BeaconDataT> beaconDataList=(List<BeaconDataT>)beaconDataTRepository.findAll();
		for(BeaconDataT beaconData:beaconDataList)
		{

			Row row = beaconDataSheet.createRow(currentRow);

			// Get Cell and set cell value

			// Geography
			Cell cellGeography = row.createCell(0);
			cellGeography.setCellValue(beaconData.getBeaconGeography());

			// End Client
			Cell cellEndClient = row.createCell(1);
			cellEndClient.setCellValue(beaconData.getBeaconCustomerName());

			// Group Client
			Cell cellGroupClient = row.createCell(2);
			cellGroupClient.setCellValue(beaconData.getBeaconGroupClient());

			// IOU
			Cell cellIsu = row.createCell(3);
			cellIsu.setCellValue(beaconData.getBeaconIou());

			// SP
			Cell cellSp = row.createCell(4);
			cellSp.setCellValue("Digital Initiatives");

			// Version
			Cell cellVersion = row.createCell(5);
			cellVersion.setCellValue("Target");

			// Financial Year
			Cell cellFinancialYear = row.createCell(6);
			cellFinancialYear.setCellValue(beaconData.getFinancialYear());

			// Quarter
			Cell cellQuarter = row.createCell(7);
			cellQuarter.setCellValue(beaconData.getQuarter());

			// Month
			Cell cellMonth = row.createCell(8);
			cellMonth.setCellValue(beaconData.getQuarter());

			//Total Revenue (INR)
			Cell cellTotalRevenue = row.createCell(9);
			BigDecimal totalRevenue=beaconData.getTarget();
			BigDecimal totalRevenueRounded = totalRevenue.round(new MathContext(3, RoundingMode.HALF_UP));
			cellTotalRevenue.setCellValue(totalRevenueRounded.doubleValue());

			// Increment row counter
			currentRow++;
		}
		logger.debug("End:Inside populateBeaconDataSheet() method"); 
	}

	/**
	 * This Method Writes BEACON Mapping into the workbook
	 * 
	 */
	private void  populateBeaconMappingSheet(Sheet beaconMappingDataSheet) 
	{
		//Get the Beacon Mapping Data Sheet From Workbook
		logger.debug("Begin:Inside populateBeaconMappingSheet() method"); 


		int currentRow = 1; // Excluding the header, header starts with index 0

		List<BeaconCustomerMappingT> beaconDataList=(List<BeaconCustomerMappingT>)beaconRepository.findAll();

		for(BeaconCustomerMappingT beaconMappingData:beaconDataList)
		{

			Row row = beaconMappingDataSheet.createRow(currentRow);

			// Get Cell and set cell value

			// Master Customer Name
			Cell cellCustomerName = row.createCell(0);
			cellCustomerName.setCellValue(beaconMappingData.getCustomerName());

			// Beacon Customer Name
			Cell cellBeaconCustomerName = row.createCell(1);
			cellBeaconCustomerName.setCellValue(beaconMappingData.getBeaconCustomerName());

			// Beacon IOU
			Cell cellBeaconIOU = row.createCell(2);
			cellBeaconIOU.setCellValue(beaconMappingData.getBeaconIou());

			// Beacon Geography
			Cell cellGeography = row.createCell(3);
			cellGeography.setCellValue(beaconMappingData.getCustomerGeography());

			// Increment row counter
			currentRow++;
		}
		logger.debug("End:Inside populateBeaconMappingSheet() method"); 
	}

	/**
	 * This Method Writes BEACON IOU Map(Ref) data into the workbook
	 * 
	 */
	private void  populateIouMappingSheet(Sheet beaconIouMapSheet) 
	{
		//Get the Beacon Iou Mapping Data Sheet From Workbook
		logger.debug("Begin:Inside populateIouMappingSheet() method"); 

		int currentRow = 1; // Excluding the header, header starts with index 0

		List<IouBeaconMappingT> iouMapBeaconDataList=(List<IouBeaconMappingT>)iouBeaconMappingTRepository.findAll();

		for(IouBeaconMappingT iouMapBeaconData:iouMapBeaconDataList)
		{

			Row row = beaconIouMapSheet.createRow(currentRow);

			// Get Cell and set cell value

			// Beacon IOU
			Cell cellBeaconIou = row.createCell(0);
			cellBeaconIou.setCellValue(iouMapBeaconData.getBeaconIou());

			// Display IOU
			Cell cellDisplayIou = row.createCell(1);
			cellDisplayIou.setCellValue(iouMapBeaconData.getDisplayIou());

			// Increment row counter
			currentRow++;

		}
		logger.debug("End:Inside populateIouMappingSheet() method"); 
	}
}


