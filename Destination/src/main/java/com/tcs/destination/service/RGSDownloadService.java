package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.DeliveryRequirementT;
import com.tcs.destination.bean.ProductMasterT;
import com.tcs.destination.data.repository.DeliveryRequirementRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyUtil;
import com.tcs.destination.utils.StringUtils;

/**
 * This service downloads rgs data from database into an excel
 */
/**
 * @author bnpp
 *
 */
@Service
public class RGSDownloadService 
{

	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheets;
	
	@Autowired
	DeliveryRequirementRepository deliveryRequirementRepository;

	private static final DateFormat actualFormat = new SimpleDateFormat("yyyy-MM-dd");
	private static final DateFormat desiredFormat = new SimpleDateFormat("MM/dd/yy");
	
	private static final Logger logger = LoggerFactory.getLogger(RGSDownloadService .class);

	/**
	 * this method downloads the sheet RGS Details
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource getRGSDetails(boolean dataFlag) throws Exception 
	{
		logger.debug("Begin:Inside getRGSDetails() method of RGSDownloadService"); 
		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		try 
		{
			workbook =(XSSFWorkbook) ExcelUtils.getWorkBook(new File
					(PropertyUtil.getProperty
							(Constants.RGS_TEMPLATE_LOCATION_PROPERTY_NAME)));
		
			if(dataFlag)
				populateRGSDetailsSheet(workbook.getSheet(Constants.RGS_DETAILS_SHEET_NAME));

			
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
		logger.debug("End:Inside getRGSDetails() method of RGSDownloadService"); 
		return inputStreamResource;
	}

	
	/**
	 * @param sheet
	 */
	private void populateRGSDetailsSheet(Sheet rgsSheet) throws Exception{
		//Get the RGS Details Sheet From Workbook
		logger.debug("Begin:Inside populateRGSDetailsSheet() method of RGSDownloadService"); 
		int currentRow = 1; // Excluding the header, header starts with index 0

		List<DeliveryRequirementT> deliveryRequirementList=(List<DeliveryRequirementT>) deliveryRequirementRepository.findAll();
		for(DeliveryRequirementT deliveryRequirementT:deliveryRequirementList){
			Row row = rgsSheet.createRow(currentRow);
			
			// Get Cell and set cell value
			String rgsId = deliveryRequirementT.getDeliveryRgsId();
			if(!StringUtils.isEmpty(rgsId)){
				row.createCell(Constants.RGS_ID_COL_INDEX-1).setCellValue(rgsId);
			}
						
			String requirementId = deliveryRequirementT.getRequirementId();
			if(!StringUtils.isEmpty(requirementId)){
				row.createCell(Constants.REQ_ID_COL_INDEX-1).setCellValue(requirementId);
			}
			
			String customerName = deliveryRequirementT.getCustomerName();
			if(!StringUtils.isEmpty(customerName)){
				row.createCell(Constants.CUSTOMER_COL_INDEX-1).setCellValue(customerName);
			}
			
			String branch = deliveryRequirementT.getBranch();
			if(!StringUtils.isEmpty(branch)){
				row.createCell(Constants.BRANCH_COL_INDEX-1).setCellValue(branch);
			}
			
			String location = deliveryRequirementT.getLocation();
			if(!StringUtils.isEmpty(location)){
				row.createCell(Constants.LOCATION_COL_INDEX-1).setCellValue(location);
			}
			
			String competency = deliveryRequirementT.getCompetencyArea();
			if(!StringUtils.isEmpty(competency)){
				row.createCell(Constants.COMPETENCY_COL_INDEX-1).setCellValue(competency);
			}
			
			String subCompetency = deliveryRequirementT.getSubCompetencyArea();
			if(!StringUtils.isEmpty(subCompetency)){
				row.createCell(Constants.SUB_COMP_COL_INDEX-1).setCellValue(subCompetency);
			}
			
			String experience = deliveryRequirementT.getExperience();
			if(!StringUtils.isEmpty(experience)){
				row.createCell(Constants.EXPERIENCE_COL_INDEX-1).setCellValue(experience);
			}
			
			String role = deliveryRequirementT.getRole();
			if(!StringUtils.isEmpty(role)){
				row.createCell(Constants.ROLE_COL_INDEX-1).setCellValue(role);
			}
			
			String status = deliveryRequirementT.getStatus();
			if(!StringUtils.isEmpty(status)){
				row.createCell(Constants.STATUS_COL_INDEX-1).setCellValue(status);
			}
			
			String iou = deliveryRequirementT.getIouName();
			if(!StringUtils.isEmpty(iou)){
				row.createCell(Constants.IOU_COL_INDEX-1).setCellValue(iou);
			}
			
			String employeeId = deliveryRequirementT.getEmployeeId();
			if(!StringUtils.isEmpty(employeeId)){
				row.createCell(Constants.EMP_ID_COL_INDEX-1).setCellValue(employeeId);
			}
			
			String employeeName = deliveryRequirementT.getEmployeeName();
			if(!StringUtils.isEmpty(employeeName)){
				row.createCell(Constants.EMP_NAME_COL_INDEX-1).setCellValue(employeeName);
			}
		
			if (deliveryRequirementT.getFulfillmentDate() != null) {
				String fulfillmentDate = getFormattedDate(deliveryRequirementT
						.getFulfillmentDate().toString(), actualFormat,
						desiredFormat);
				if (!StringUtils.isEmpty(fulfillmentDate)) {
					row.createCell(Constants.FULFILL_DATE_COL_INDEX-1)
							.setCellValue(fulfillmentDate);
				}
			}
			
			if (deliveryRequirementT.getRequirementStartDate() != null) {
				String requirementStartDate = getFormattedDate(
						deliveryRequirementT.getRequirementStartDate()
								.toString(), actualFormat, desiredFormat);
				if (!StringUtils.isEmpty(requirementStartDate)) {
					row.createCell(Constants.REQ_START_DATE_COL_INDEX-1)
							.setCellValue(requirementStartDate);
				}
			}
			
			if (deliveryRequirementT.getRequirementEndDate() != null) {
				String requirementEndDate = getFormattedDate(
						deliveryRequirementT.getRequirementEndDate().toString(),
						actualFormat, desiredFormat);
				if (!StringUtils.isEmpty(requirementEndDate)) {
					row.createCell(Constants.REQ_END_DATE_COL_INDEX-1)
							.setCellValue(requirementEndDate);
				}
			}
			
			currentRow++;
		}
	}
	
	/**
	 * Method to convert date in a format to another format
	 * 
	 * @param Stringdate
	 * @param actualFormat
	 * @param destFormat
	 * @return String
	 * @throws Exception
	 */
	public static String getFormattedDate(String date, DateFormat actualFormat, DateFormat destFormat) throws Exception{
		return destFormat.format(actualFormat.parse(date));
	}

}
