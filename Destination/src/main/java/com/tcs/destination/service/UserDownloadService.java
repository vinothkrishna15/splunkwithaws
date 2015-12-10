package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.GeographyMappingT;
import com.tcs.destination.bean.GoalMappingT;
import com.tcs.destination.bean.TimeZoneMappingT;
import com.tcs.destination.data.repository.BeaconRepository;
import com.tcs.destination.data.repository.ContactCustomerLinkTRepository;
import com.tcs.destination.data.repository.ContactRepository;
import com.tcs.destination.data.repository.CustomerIOUMappingRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyCountryRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.GoalGroupMappingRepository;
import com.tcs.destination.data.repository.GoalMappingRepository;
import com.tcs.destination.data.repository.IouBeaconMappingTRepository;
import com.tcs.destination.data.repository.RevenueCustomerMappingTRepository;
import com.tcs.destination.data.repository.SubSpRepository;
import com.tcs.destination.data.repository.TimezoneMappingRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.PropertyReaderUtil;
import com.tcs.destination.utils.StringUtils;

@Service
public class UserDownloadService {

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	CommonWorkbookSheetsForDownloadServices commonWorkbookSheets;

	@Autowired
	BeaconRepository beaconRepository;
	
	@Autowired
	GoalGroupMappingRepository goalGroupMappingRepository;
	
	@Autowired
	SubSpRepository subSpRepository;

	@Autowired
	CustomerIOUMappingRepository customerIOUMappingRepository;

	@Autowired
	RevenueCustomerMappingTRepository revenueCustomerMappingTRepository;

	@Autowired
	IouBeaconMappingTRepository iouBeaconMappingTRepository;

	@Autowired
	ContactRepository contactRepository;

	@Autowired
	GeographyCountryRepository geographyCountryRepository;

	@Autowired
	GeographyRepository geographyRepository;
	
	@Autowired
	GoalMappingRepository goalMappingRepository;

	@Autowired
	TimezoneMappingRepository timezoneMappingRepository;

	@Autowired
	ContactCustomerLinkTRepository contactCustomerLinkTRepository;

	Map<String, CustomerMasterT> mapOfCustomerMasterT = null;
	Map<String,CustomerMasterT> mapOfContactCustomerLinkT = null;

	private static final Logger logger = LoggerFactory.getLogger(CustomerDownloadService.class);

	public InputStreamResource getUsers(boolean oppFlag)
			throws Exception {

		Workbook workbook = null;
		InputStreamResource inputStreamResource = null;
		mapOfCustomerMasterT = getcustomerMappingT();

		try {
			workbook = ExcelUtils.getWorkBook(new File(PropertyReaderUtil.readPropertyFile(
					Constants.APPLICATION_PROPERTIES_FILENAME, 
					Constants.USER_TEMPLATE_LOCATION_PROPERTY_NAME)));
			if(oppFlag){
				// to populate all the master tables
			}
			// Populate Customer Master sheet
			populateCustomerMasterSheet(workbook.getSheet(Constants.USER_TEMPLATE_CUSTOMER));
			// Populate Customer time zone sheet
			populateUserTimezoneSheet(workbook.getSheet(Constants.USER_TEMPLATE_TIMEZONE));
			// Populate other references Sheet
			populateUserGeoCountryIOUSubspSheet(workbook.getSheet(Constants.USER_TEMPLATE_OTHER_REFERENCES));
			// Populate user_goal Mapping Sheet
			populateTargetMappingSheet(workbook.getSheet(Constants.USER_TEMPLATE_USERGOALREF));
			
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

	/**
	 * This method creates a Customer Map
	 * @return customerMap
	 */
	private Map<String, CustomerMasterT> getcustomerMappingT() {
		List<CustomerMasterT> listOfCustomerMappingT = null;
		listOfCustomerMappingT = (List<CustomerMasterT>) customerRepository.findAll();
		Map<String, CustomerMasterT> customerMap = new HashMap<String, CustomerMasterT>();
		for (CustomerMasterT customerMappingT : listOfCustomerMappingT) {
			customerMap.put(customerMappingT.getCustomerName(), customerMappingT);
		}
		return customerMap;
	}


	private void populateUserGeoCountryIOUSubspSheet(Sheet otherReferncesSheet)  throws Exception{
		List<Object[]> listOfReferences = geographyCountryRepository.getGeographyCountry();
		List<GeographyMappingT> listOfGeos = (List<GeographyMappingT>) geographyRepository.findAll();
		List<Object> listOfIous = (List<Object>) customerIOUMappingRepository.findDistintDisplayIou();
		List<String> listOfSubsp = (List<String>) subSpRepository.findDistinctDisplaySubsp();
		
		//to find max number of rows out of 4 table's list
		int maxCount1 = Math.max(listOfReferences.size(),listOfGeos.size());
		int maxCount2 = Math.max(listOfIous.size(),listOfSubsp.size());
		int maxRowCount = Math.max(maxCount1, maxCount2);
		
		for (int i=1; i <= maxRowCount; i++){
			Row row = otherReferncesSheet.createRow(i);
		}

		// for unique geography 
		if(listOfGeos!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (GeographyMappingT geo : listOfGeos) {

				// Create row with rowCount
				Row row = otherReferncesSheet.getRow(rowCount);

				// Create new Cell and set cell value
				Cell cellGeography = row.createCell(3);
				cellGeography.setCellValue(geo.getGeography().trim());

				// Increment row counter
				rowCount++;
			}
		} 
		
		// for geography and country
		if(listOfReferences!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (Object[] ref : listOfReferences) {

				// Create row with rowCount
				Row row = otherReferncesSheet.getRow(rowCount);

				// Create new Cell and set cell value
				Cell cellGeography = row.createCell(0);
				cellGeography.setCellValue(ref[0].toString().trim());

				Cell cellCountry = row.createCell(1);
				cellCountry.setCellValue(ref[1].toString().trim());

				// Increment row counter
				rowCount++;
			}
		} 
		
		// for unique display iou
		if(listOfIous!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (Object iou : listOfIous) {

				// Create row with rowCount
				Row row = otherReferncesSheet.getRow(rowCount);

				// Create new Cell and set cell value
				Cell cellIou = row.createCell(5);
				cellIou.setCellValue(iou.toString());

				// Increment row counter
				rowCount++;
			}
		} 

		// for unique sub sp
		if(listOfSubsp!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (String subsp : listOfSubsp) {

				// Create row with rowCount
				Row row = otherReferncesSheet.getRow(rowCount);

				// Create new Cell and set cell value
				Cell cellSubsp = row.createCell(7);
				cellSubsp.setCellValue(subsp);

				// Increment row counter
				rowCount++;
			}
		} 

	}

	/*
	 * Populate UserTimezoneSheet
	 */
	private void populateUserTimezoneSheet(Sheet userTimezoneSheet)  throws Exception{
		List<TimeZoneMappingT> listOfTimezone = (List<TimeZoneMappingT>) timezoneMappingRepository.findAll();
		if(listOfTimezone!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (TimeZoneMappingT timezone : listOfTimezone) {
				// Create row with rowCount
				Row row = userTimezoneSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellTimezoneCode = row.createCell(0);
				cellTimezoneCode.setCellValue(timezone.getTimeZoneCode().trim());

				Cell cellTimeZoneOffset = row.createCell(1);
				cellTimeZoneOffset.setCellValue(timezone.getTimeZoneOffset().trim());

				Cell cellDescription = row.createCell(2);
				cellDescription.setCellValue(timezone.getDescription().trim());

				// Increment row counter
				rowCount++;
			}
		} 
	}
	
	/*
	 * Populate TargetMappingSheet
	 */
	private void populateTargetMappingSheet(Sheet targetSheet)  throws Exception{
		List<GoalMappingT> listOfGoals = (List<GoalMappingT>) goalMappingRepository.findAll();
		
		//for retrieving the goalGroupName and isActive from goal_group_mapping_t
		List<Object[]> items = (List<Object[]>) goalGroupMappingRepository.findGoalGroup();
		
		Map<String,List<String>> goalIdGroupMap = new HashMap<String,List<String>>();
		if(items!=null){
			for (Object[] goalGroupRecord : items) {
				String goalId = (String)goalGroupRecord[0];
				String group = (String)goalGroupRecord[1];
				if(goalIdGroupMap.isEmpty()){
					
					List<String> groupList = new ArrayList<String>();
					groupList.add(group);
					goalIdGroupMap.put(goalId, groupList);
				} else {
					List<String> chkGroupList = goalIdGroupMap.get(goalId);
					if(chkGroupList==null){
						List<String> groupList = new ArrayList<String>();
						groupList.add(group);
						goalIdGroupMap.put(goalId, groupList);
					} else {
						chkGroupList.add(group);
					}
				}
			}
		}

		if(listOfGoals!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (GoalMappingT goal : listOfGoals) {
				// Create row with rowCount
				Row row = targetSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellGoalId = row.createCell(0);
				cellGoalId.setCellValue(goal.getGoalId());

				Cell cellGoalName = row.createCell(1);
				cellGoalName.setCellValue(goal.getGoalName().trim());

				Cell cellDisplayUnit = row.createCell(2);
				cellDisplayUnit.setCellValue(goal.getDisplayUnit().trim());
				
				Cell cellFinYear = row.createCell(3);
				cellFinYear.setCellValue(goal.getFinancialyear().trim());
				
				Cell cellDefaultTarget = row.createCell(4);
				cellDefaultTarget.setCellValue(goal.getDefaultTarget().toString());
				
				List<String> groups = goalIdGroupMap.get(goal.getGoalId().toString().trim());
				String groupsStr = getAppendedStrGroups(groups);
				ExcelUtils.createCell(groupsStr.trim(), row, 5);

				String isActive = "Y";
				Cell cellIsActive = row.createCell(6);
				cellIsActive.setCellValue(isActive);

				// Increment row counter
				rowCount++;
			}
		} 
	}


	private String getAppendedStrGroups(List<String> groups) {
		StringBuffer groupBuffer = new StringBuffer("");
		for(String group : groups){
			if(StringUtils.isEmpty(groupBuffer.toString())){
				groupBuffer.append(group.trim());
			} else {
				groupBuffer.append("," + group.trim());
			}
		}
		return groupBuffer.toString();
	}

	/*
	 * Populate CustomerMaster Sheet from customer_master_t
	 */
	public void populateCustomerMasterSheet(Sheet customerMasterSheet) throws Exception{

		List<CustomerMasterT> listOfCMT = (List<CustomerMasterT>) customerRepository.findAll();

		if(listOfCMT!=null) {
			int rowCount = 1; // Excluding the header, header starts with index 0
			for (CustomerMasterT cmt : listOfCMT) {
				// Create row with rowCount
				Row row = customerMasterSheet.createRow(rowCount);

				// Create new Cell and set cell value
				Cell cellGrpClient = row.createCell(0);
				cellGrpClient.setCellValue(cmt.getGroupCustomerName().trim());

				Cell cellCustName = row.createCell(1);
				cellCustName.setCellValue(cmt.getCustomerName().trim());

				Cell cellIou = row.createCell(2);
				cellIou.setCellValue(cmt.getIouCustomerMappingT().getIou().trim());

				Cell cellGeo = row.createCell(3);
				cellGeo.setCellValue(cmt.getGeographyMappingT().getGeography()
						.trim());

				// Increment row counter
				rowCount++;
			}
		} 
	}

}
