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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.IouCustomerMappingT;
import com.tcs.destination.bean.SubSpMappingT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyReaderUtil;

@Service
public class RevenueDownloadService {

	@Autowired
	CustomerIOUMappingRepository customerIOUMappingRepository;

	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	@Autowired
	SubSpRepository subSpRepository;

	Map<String, CustomerMasterT> mapOfCustomerMasterT = null;
	private static final Logger logger = LoggerFactory.getLogger(RevenueDownloadService.class);

	public InputStreamResource getActualRevenueData() throws Exception {

		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;

		try {
			workbook = ExcelUtils.getWorkBook(new File(PropertyReaderUtil.readPropertyFile(
					Constants.APPLICATION_PROPERTIES_FILENAME, 
					Constants.ACTUAL_REVENUE_DATA_TEMPLATE_LOCATION_PROPERTY_NAME)));

			// Populate Iou Customer REF Sheet
			populateActualRevenueDataSheet(workbook.getSheet(Constants.ACTUAL_REVENUE_DATA));
			populateFinanceMapRefSheet(workbook.getSheet(Constants.FINANCE_MAP_REF));
			populateIouCustomerSheet(workbook.getSheet(Constants.CUSTOMER_IOU_MAPPING_REF));
			populateSubSPMapSheet(workbook.getSheet(Constants.SUB_SP_MAP_REF));
			logger.info("actual_revenue_data_template download in progress");
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
		return inputStreamResource;
	}

/*
 * populate finance map sheet 
 */
	private void populateFinanceMapRefSheet(Sheet financeMapSheet)  throws Exception{
		List<ActualRevenuesDataT> listOfActualRevenueData = (List<ActualRevenuesDataT>) actualRevenuesDataTRepository.findAll();

		if(listOfActualRevenueData!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (ActualRevenuesDataT revenue : listOfActualRevenueData) {
				// Create row with rowCount
				Row row = financeMapSheet.createRow(rowCount);

				// Create new Cell and set cell value

				Cell cellDisplaySubSp = row.createCell(0);
				cellDisplaySubSp.setCellValue(revenue.getRevenueCustomerMappingT().getCustomerName().trim());

				Cell cellFinanceCustomerName = row.createCell(1);
				cellFinanceCustomerName.setCellValue(revenue.getFinanceCustomerName().trim());

				Cell cellFinanceIou = row.createCell(2);
				cellFinanceIou.setCellValue(revenue.getFinanceIou().trim());

				Cell cellFinanceGeography = row.createCell(3);
				cellFinanceGeography.setCellValue(revenue.getFinanceGeography().trim());

				// Increment row counter
				rowCount++;
			}
		} 
	}


/*
 * To populate the sheet actualRevenueData
 */
	private void populateActualRevenueDataSheet (Sheet actualRevenueDataSheet)  throws Exception{
		List<ActualRevenuesDataT> listOfActualRevenue = (List<ActualRevenuesDataT>) actualRevenuesDataTRepository.findAll();

		if(listOfActualRevenue!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (ActualRevenuesDataT actualRevenue : listOfActualRevenue) {
				// Create row with rowCount
				Row row = actualRevenueDataSheet.createRow(rowCount);

				// Create new Cell and set cell value

				Cell cellDisplaySubSp = row.createCell(3);
				cellDisplaySubSp.setCellValue(actualRevenue.getMonth().trim());

				Cell cellSubSp = row.createCell(4);
				cellSubSp.setCellValue(actualRevenue.getQuarter().trim());

				Cell cellActualSubSp = row.createCell(5);
				cellActualSubSp.setCellValue(actualRevenue.getFinancialYear().trim());

				Cell cellActive = row.createCell(6);
				cellActive.setCellValue(actualRevenue.getRevenue().toString());
				
				Cell cellClientCountryName = row.createCell(7);
				cellClientCountryName.setCellValue(actualRevenue.getClientCountry().toString());
				
				Cell cellFinanceGeography = row.createCell(8);
				cellFinanceGeography.setCellValue(actualRevenue.getFinanceGeography().toString());
				
				Cell cellSubsp = row.createCell(9);
				cellSubsp.setCellValue(actualRevenue.getSubSp().toString());
				
				Cell cellFinanceCustomerName = row.createCell(10);
				cellFinanceCustomerName.setCellValue(actualRevenue.getFinanceCustomerName().toString());
				
				Cell cellFinanceIou = row.createCell(11);
				cellFinanceIou.setCellValue(actualRevenue.getFinanceIou().toString());
				
				// Increment row counter
				rowCount++;
			}
		} 
	}

/*
 * To populate the sheet Sub_sp
 */
	private void populateSubSPMapSheet(Sheet subSpSheet)  throws Exception{
		List<SubSpMappingT> listOfSubSp = (List<SubSpMappingT>) subSpRepository.findAll();

		if(listOfSubSp!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (SubSpMappingT subSp : listOfSubSp) {
				// Create row with rowCount
				Row row = subSpSheet.createRow(rowCount);

				// Create new Cell and set cell value

				Cell cellDisplaySubSp = row.createCell(0);
				cellDisplaySubSp.setCellValue(subSp.getDisplaySubSp().trim());

				Cell cellSubSp = row.createCell(1);
				cellSubSp.setCellValue(subSp.getSubSp().trim());

				Cell cellActualSubSp = row.createCell(2);
				cellActualSubSp.setCellValue(subSp.getActualSubSp().trim());

				Cell cellSpCode = row.createCell(3);
				String spCode = String.valueOf(subSp.getSpCode());
				cellSpCode.setCellValue(spCode);

				Cell cellActive = row.createCell(4);
				cellActive.setCellValue(subSp.getActive().trim());

				// Increment row counter
				rowCount++;
			}
		} 
	}

/*
 * To populate the sheet Customer IOU
 */
	private void populateIouCustomerSheet(Sheet iouCustomerSheet)  throws Exception{
		List<IouCustomerMappingT> listOfIou = (List<IouCustomerMappingT>) customerIOUMappingRepository.findAll();

		if(listOfIou!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (IouCustomerMappingT iou : listOfIou) {
				// Create row with rowCount
				Row row = iouCustomerSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellBeaconCustomerName = row.createCell(0);
				cellBeaconCustomerName.setCellValue(iou.getDisplayIou().trim());

				Cell cellBeaconIou = row.createCell(1);
				cellBeaconIou.setCellValue(iou.getIou().trim());

				// Increment row counter
				rowCount++;
			}
		} 
	}
}