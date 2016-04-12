package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;
import java.util.Map;

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

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.BeaconCustomerMappingT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.RevenueCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyUtil;

/**
 * This service handles the operations related to
 * downloading the revenue details from database into an excel
 */
@Service
public class RevenueDownloadService {

	@Autowired
	CustomerIOUMappingRepository customerIOUMappingRepository;

	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	@Autowired
	SubSpRepository subSpRepository;
	
	@Autowired
	RevenueCustomerMappingTRepository revenueCustomerMappingTRepository;

	Map<String, CustomerMasterT> mapOfCustomerMasterT = null;
	private static final Logger logger = LoggerFactory.getLogger(RevenueDownloadService.class);

	/**
	 * This method populates the sheets Finance Mapping(Ref),Customer IOU Map(Ref),
	 * Sub Sp Map(Ref) and Actual Revenue - DATA in the excel workbook
	 * @param oppFlag
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getActualRevenueData(boolean oppFlag) throws Exception {
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		logger.info("Begin: inside getActualRevenueData() of RevenueDownloadService");
		try {

			workbook =(XSSFWorkbook) ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.ACTUAL_REVENUE_DATA_TEMPLATE_LOCATION_PROPERTY_NAME)));

			// Populate Iou Customer REF Sheet
			if(oppFlag){
				populateActualRevenueDataSheet(workbook.getSheet(Constants.ACTUAL_REVENUE_DATA));
			}
			populateFinanceMapRefSheet(workbook.getSheet(Constants.FINANCE_MAP_REF));
			populateIouCustomerSheet(workbook.getSheet(Constants.CUSTOMER_IOU_MAPPING_REF));
			populateSubSPMapSheet(workbook.getSheet(Constants.SUB_SP_MAP_REF));
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
		logger.info("End: inside getActualRevenueData() of RevenueDownloadService");
		return inputStreamResource;
	}

	/*
	 *This method populates finance map sheet 
	 */
	private void populateFinanceMapRefSheet(Sheet financeMapSheet)  throws Exception{
		logger.info("Begin: inside populateFinanceMapRefSheet() of RevenueDownloadService");
		List<RevenueCustomerMappingT> listOfActualRevenueMap = (List<RevenueCustomerMappingT>) revenueCustomerMappingTRepository.findAll();

		if(listOfActualRevenueMap!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (RevenueCustomerMappingT revenue : listOfActualRevenueMap) {
				// Create row with rowCount
				Row row = financeMapSheet.createRow(rowCount);

				// Create new Cell and set cell value

				Cell cellDisplaySubSp = row.createCell(0);
				cellDisplaySubSp.setCellValue(revenue.getCustomerMasterT().getCustomerName().trim());

				Cell cellFinanceCustomerName = row.createCell(1);
				cellFinanceCustomerName.setCellValue(revenue.getFinanceCustomerName().trim());

				Cell cellFinanceIou = row.createCell(2);
				cellFinanceIou.setCellValue(revenue.getFinanceIou().trim());

				Cell cellFinanceGeography = row.createCell(3);
				cellFinanceGeography.setCellValue(revenue.getCustomerGeography().trim());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.info("End: inside populateFinanceMapRefSheet() of RevenueDownloadService");
	}


	/*
	 * To populate the sheet actualRevenueData
	 */
	private void populateActualRevenueDataSheet (Sheet actualRevenueDataSheet)  throws Exception{
		logger.info("Begin: inside populateActualRevenueDataSheet() of RevenueDownloadService");
		List<ActualRevenuesDataT> listOfActualRevenue = (List<ActualRevenuesDataT>) actualRevenuesDataTRepository.findAll();

		if(listOfActualRevenue!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (ActualRevenuesDataT actualRevenue : listOfActualRevenue) {
				// Create row with rowCount
				Row row = actualRevenueDataSheet.createRow(rowCount);
				RevenueCustomerMappingT  revenueCustomerMappingT=revenueCustomerMappingTRepository.findByRevenueCustomerMapId(actualRevenue.getRevenueCustomerMapId());
				// Create new Cell and set cell value

				Cell cellDisplaySubSp = row.createCell(3);
				cellDisplaySubSp.setCellValue(actualRevenue.getMonth().trim());

				Cell cellSubSp = row.createCell(4);
				String quarter1=actualRevenue.getQuarter().substring(0, 2);
				String quarter2=actualRevenue.getQuarter().substring(10, 12);
				cellSubSp.setCellValue(quarter1+quarter2);

				Cell cellActualSubSp = row.createCell(5);
				String financialYr1=actualRevenue.getFinancialYear().substring(0,2);
				String financialYr2=actualRevenue.getFinancialYear().substring(8, 10);
				cellActualSubSp.setCellValue(financialYr1+financialYr2);

				Cell cellActive = row.createCell(6);
				cellActive.setCellValue(actualRevenue.getRevenue().toString());

				Cell cellClientCountryName = row.createCell(7);
				cellClientCountryName.setCellValue(actualRevenue.getClientCountry().toString());

				Cell cellFinanceGeography = row.createCell(8);
				cellFinanceGeography.setCellValue(revenueCustomerMappingT.getCustomerGeography());

				Cell cellSubsp = row.createCell(9);
				cellSubsp.setCellValue(actualRevenue.getSubSp().toString());

				Cell cellFinanceCustomerName = row.createCell(10);
				cellFinanceCustomerName.setCellValue(revenueCustomerMappingT.getFinanceCustomerName().toString());

				Cell cellFinanceIou = row.createCell(11);
				cellFinanceIou.setCellValue(revenueCustomerMappingT.getFinanceIou().toString());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.info("End: inside populateActualRevenueDataSheet() of RevenueDownloadService");
	}

	/*
	 * To populate the sheet Sub_sp
	 */
	private void populateSubSPMapSheet(Sheet subSpSheet)  throws Exception{
		logger.info("Begin: inside populateSubSPMapSheet() of RevenueDownloadService");
		List<SubSpMappingT> listOfSubSp = (List<SubSpMappingT>) subSpRepository.findAll();

		if(listOfSubSp!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (SubSpMappingT subSp : listOfSubSp) {
				// Create row with rowCount
				Row row = subSpSheet.createRow(rowCount);

				// Create new Cell and set cell value

				Cell cellActualSubSp = row.createCell(0);
				cellActualSubSp.setCellValue(subSp.getActualSubSp().trim());

				Cell cellSubSp = row.createCell(1);
				cellSubSp.setCellValue(subSp.getSubSp().trim());

				Cell cellDisplaySubSp = row.createCell(2);
				cellDisplaySubSp.setCellValue(subSp.getDisplaySubSp().trim());

				Cell cellSpCode = row.createCell(3);
				String spCode = String.valueOf(subSp.getSpCode());
				cellSpCode.setCellValue(spCode);

				Cell cellActive = row.createCell(4);
				cellActive.setCellValue(subSp.getActive().trim());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.info("End: inside populateSubSPMapSheet() of RevenueDownloadService");
	}

	/*
	 * To populate the sheet Customer IOU
	 */
	private void populateIouCustomerSheet(Sheet iouCustomerSheet)  throws Exception{
		logger.info("Begin: inside populateIouCustomerSheet() of RevenueDownloadService");
		List<IouCustomerMappingT> listOfIou = (List<IouCustomerMappingT>) customerIOUMappingRepository.findAll();
		if(listOfIou!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (IouCustomerMappingT iou : listOfIou) {
				// Create row with rowCount
				Row row = iouCustomerSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellBeaconIou = row.createCell(0);
				cellBeaconIou.setCellValue(iou.getIou().trim());

				Cell cellBeaconCustomerName = row.createCell(1);
				cellBeaconCustomerName.setCellValue(iou.getDisplayIou().trim());

				// Increment row counter
				rowCount++;
			}
		} 
		logger.info("End: inside populateIouCustomerSheet() of RevenueDownloadService");
	}
}
