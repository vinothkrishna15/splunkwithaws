package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.CurrencyValue;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.CustomerRevenueValues;
import com.tcs.destination.bean.GroupCustomerGeoIouResponse;
import com.tcs.destination.bean.OpportunitySummaryValue;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.ReportSummaryOpportunity;
import com.tcs.destination.bean.TargetVsActualDetailed;
import com.tcs.destination.bean.TargetVsActualQuarter;
import com.tcs.destination.bean.TargetVsActualYearToDate;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.ConnectRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.GeographyRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.ProjectedRevenuesDataTRepository;
import com.tcs.destination.data.repository.UserAccessPrivilegesRepository;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.ExcelUtils;
import com.tcs.destination.utils.ReportConstants;

@Service
public class BDMReportsService {

	private static final Logger logger = LoggerFactory.getLogger(BDMReportsService.class);

	@Autowired
	ActualRevenuesDataTRepository actualRevenuesDataTRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	BidDetailsTRepository bidDetailsTRepository;

	@Autowired
	BeaconConverterService beaconConverterService;

	@Autowired
	BuildBidReportService buildBidReportService;

	@Autowired
	ConnectRepository connectRepository;

	@Autowired
	ConnectDetailedReportService connectDetailedReportService;

	@Autowired
	ConnectSummaryReportService connectSummaryReportService;

	@Autowired
	ProjectedRevenuesDataTRepository projectedRevenuesDataTRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	BuildExcelTargetVsActualDetailedReportService buildExcelTargetVsActualDetailedReportService;

	@Autowired
	BuildExcelTargetVsActualSummaryReportService buildExcelTargetVsActualSummaryReportService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserAccessPrivilegesRepository userAccessPrivilegesRepository;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	@Autowired
	UserService userService;

	@Autowired
	GeographyRepository geographyRepository;

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	BuildOpportunityReportService buildOpportunityReportService;
	
	@Autowired
	OpportunityRepository opportunityRepository;


		public InputStreamResource getBdmDetailedReport(String from, String to,
				List<String> geography, List<String> country,
				List<String> currency, List<String> serviceLines,
				List<Integer> salesStage, List<String> opportunityOwnerIds,
				String supervisorId) {
			// TODO Auto-generated method stub
			return null;
		}

		public InputStreamResource getBdmSummaryReport(String from, String to,
				List<String> geography, List<String> country,
				List<String> currency, List<String> serviceLines,
				List<Integer> salesStage, List<String> opportunityOwnerIds,
				String supervisorId) throws Exception {
			SXSSFWorkbook workbook = new SXSSFWorkbook(50);
			String tillDate=DateUtils.getCurrentDate();
//						buildOpportunityReportService.getTitleSheet(workbook,geography,iou,serviceLines,salesStage,userId,tillDate);
			getBdmReportExcel(from, to , geography, country, currency, serviceLines, salesStage, opportunityOwnerIds,supervisorId,workbook);
			ExcelUtils.arrangeSheetOrder(workbook);
			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
			return inputStreamResource;
		}
		
				
		public void getBdmReportExcel(String from, String to,
				List<String> geography, List<String> country,
				List<String> currency, List<String> serviceLines,
				List<Integer> salesStage, List<String> opportunityOwnerIds,
				String supervisorId, SXSSFWorkbook workbook) throws Exception {
			logger.debug("Inside Report Service getBdmReportExcel Method");
			
			// check for sales stage code Destination Exception
			Date fromDate=new Date();
			Date toDate = new Date();
			Map<String, List<OpportunitySummaryValue>> oppSummaryValueMap = new TreeMap<String,List<OpportunitySummaryValue>>();
			Map<String,List<Integer>> salesStageMap = new TreeMap<String,List<Integer>>();
			if(!from.isEmpty() && !to.isEmpty()){  // have to check for the default month
				fromDate=DateUtils.getDateFromMonth(from, true);
				toDate=DateUtils.getDateFromMonth(to, false);
			}else{
				logger.error("From And To Date cannot be empty");
				throw new DestinationException(HttpStatus.BAD_REQUEST, "From And To Date cannot be empty");
			}
			List<String> userIds = new ArrayList<String>();
			List<Object[]> bdmObjectList=new ArrayList<Object[]>();
			List<String> opportunityOwnerList = new ArrayList<String>();
			List<String> geoList = new ArrayList<String>();
			List<String> iouList = new ArrayList<String>();
			List<String> countryList = new ArrayList<String>();
			List<String> serviceLinesList = new ArrayList<String>();
			addItemToListGeo(geography,geoList);
			addItemToList(country,countryList);
			addItemToList(serviceLines,serviceLinesList);
			addItemToList(opportunityOwnerIds, opportunityOwnerList);
//						addEmptyItemToListIfEmpty(userIds);
			String[] serviceTypes={"Wins","Losses","Pipeline","Opportunities"};
			for(int i=0;i<4;i++){
				oppSummaryValueMap.put(serviceTypes[i], new ArrayList<OpportunitySummaryValue>());
				salesStageMap.put(serviceTypes[i], new ArrayList<Integer>());
			}
			salesStageMap.put("winLoss", new ArrayList<Integer>());
			UserT user = userRepository.findByUserId(supervisorId);
			if(user == null){
				logger.error("User Id Not Found");
				throw new DestinationException(HttpStatus.NOT_FOUND," Supervisor Id Not Found");
			}
//					
			if (user.getUserGroupMappingT().getUserGroup().equals(ReportConstants.BDM)) {
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "User do not have previlege to view this report");
			
			} else if (user.getUserGroupMappingT().getUserGroup().equals(ReportConstants.BDMSUPERVISOR)) {
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(user.getSupervisorUserId()); // change this to supervisor id
//					if(userIds.contains(supervisorId)){
//						userIds.remove(supervisorId);
//					}
			} else {
				userIds.add("");
			}
			// throws exception if opportunityOwnerIds is not present in the userIds 
//				if( !(userIds == null || userIds.isEmpty()) && !(opportunityOwnerIds == null || opportunityOwnerIds.isEmpty()) ){
//				CheckExistenceOfUsers(userIds,opportunityOwnerIds);
//				}
			
			salesStageMap = mapSalesStageCodeWithType(salesStageMap, salesStage);
			if (salesStageMap.get("winLoss").size() > 0) {
				List<Integer> salesStageList = salesStageMap.get("winLoss");
				bdmObjectList = opportunityRepository.findBdmsDetailWinOrLoss(fromDate, toDate, geography, country, serviceLines,
						salesStageList, opportunityOwnerIds, userIds);
				System.out.println("Object"+ bdmObjectList.get(0)[0]);
				
				if (bdmObjectList.size() > 0) {
					List<OpportunitySummaryValue> opportunitySummaryValueList = ConvertObjectListIntoOpportunityList(bdmObjectList);
					setCurrencyValue(opportunitySummaryValueList, currency);
					mapOpportunitiesWithSalesStage(oppSummaryValueMap,opportunitySummaryValueList);
				}
			}
	 		
			bdmObjectList = opportunityRepository.findBdmsDetailPipelineOrOpportunities(fromDate,toDate,geography, country, serviceLines, salesStage, opportunityOwnerIds,userIds);
//						List<OpportunitySummaryValue> opportunitySummaryValueList = ConvertObjectListIntoOpportunityList(bdmObjectList);
//						setCurrencyValue(opportunitySummaryValueList,currency);
//						mapOpportunitiesWithSalesStage(oppSummaryValueMap,opportunitySummaryValueList); 
			
			// Will enable this after testing the templete
			
			
			if (oppSummaryValueMap.size() > 0) {
				buildOpportunityReportService.getBdmPerformanceExcel(oppSummaryValueMap,currency,workbook);
//							buildOpportunityReportService.getBdmPerformanceExcel(opportunitySummaryValueList,currency,workbook);
			} else {
				logger.error("No Data Found in Database");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"No Data Found in Database");
			}
//						return oppSummaryValueMap;
		}
					
			private Map<String, List<Integer>> mapSalesStageCodeWithType(
					Map<String, List<Integer>> salesStageMap, List<Integer> salesStage) {
				
				List<Integer> salesStageList = new ArrayList<Integer>();
				for(Integer salesStageCode : salesStage){
					if(salesStageCode < 4){
						salesStageList = salesStageMap.get(ReportConstants.OPPORTUNITIES);
						salesStageList.add(salesStageCode);
						salesStageMap.put(ReportConstants.OPPORTUNITIES, salesStageList);
					} else if(salesStageCode > 3 && salesStageCode < 9) {
						salesStageList = salesStageMap.get(ReportConstants.PIPELINE);
						salesStageList.add(salesStageCode);
						salesStageMap.put(ReportConstants.PIPELINE, salesStageList);
					} else {
						salesStageList = salesStageMap.get("winLoss");
						salesStageList.add(salesStageCode);
						salesStageMap.put("winLoss", salesStageList);
					}
				}
				return salesStageMap; 
			}

			private List<OpportunitySummaryValue> ConvertObjectListIntoOpportunityList(
					List<Object[]> bdmObjectList) {
				List<OpportunitySummaryValue> opportunitySummaryValueList = new ArrayList<OpportunitySummaryValue>();
				if (bdmObjectList.size() > 0) {
					for (Object[] bdmObject : bdmObjectList) {
						OpportunitySummaryValue oppSummaryValue = new OpportunitySummaryValue();
						oppSummaryValue.setTitle(bdmObject[0].toString());
						oppSummaryValue.setCount((BigInteger) bdmObject[1]);
						oppSummaryValue.setBidValue((BigDecimal) bdmObject[2]);
						if( !(bdmObject[3] == null) ){
						oppSummaryValue.setSalesStageCode((int) bdmObject[3]);
						}
						opportunitySummaryValueList.add(oppSummaryValue);
					}
				}
				return opportunitySummaryValueList;
			}
			
			private void setCurrencyValue(
					List<OpportunitySummaryValue> opportunitySummaryValueList,
					List<String> currencyList) throws DestinationException {
				if (opportunitySummaryValueList.size() > 0) {
					for (String currency : currencyList) {
						for (int i=0;i< opportunitySummaryValueList.size();i++) {
							if (opportunitySummaryValueList.get(i).getBidValue() != null) {
								CurrencyValue currencyValue = new CurrencyValue();
								currencyValue.setCurrency(currency);
								currencyValue.setValue(beaconConverterService.convert("INR",currency, opportunitySummaryValueList.get(i).getBidValue()));
								opportunitySummaryValueList.get(i).getBidValues().add(currencyValue);
							}
						}
					}
				}
			}
			

			private void CheckExistenceOfUsers(List<String> userIds,
					List<String> opportunityOwnerIds) throws Exception {
				Boolean isPresent = false;
				for(String opportunityOwnerId: opportunityOwnerIds) {
						if(userIds.contains(opportunityOwnerId)){
							isPresent = true;
						} else {
							throw new DestinationException(HttpStatus.NOT_FOUND,
									"User Id Not Found");
						}
					}
					
				}

			private void mapOpportunitiesWithSalesStage(
					Map<String, List<OpportunitySummaryValue>> oppSummaryValueMap,
					List<OpportunitySummaryValue> opportunitySummaryValueList) {
			
				List<OpportunitySummaryValue> list=new ArrayList<OpportunitySummaryValue>();
				for(OpportunitySummaryValue opportunitySummaryValue:opportunitySummaryValueList){
					if(opportunitySummaryValue.getSalesStageCode() == 9){
						list=oppSummaryValueMap.get(ReportConstants.WINS);
						list.add(opportunitySummaryValue);
						oppSummaryValueMap.put(ReportConstants.WINS, list);
					} else if(opportunitySummaryValue.getSalesStageCode() == 10){
						list=oppSummaryValueMap.get(ReportConstants.LOSSES);
						list.add(opportunitySummaryValue);
						oppSummaryValueMap.put(ReportConstants.LOSSES, list);
					} 
//					else if(opportunitySummaryValue.getSalesStageCode() >= 0 && opportunitySummaryValue.getSalesStageCode() <4){
//						list=oppSummaryValueMap.get(ReportConstants.OPPORTUNITIES);
//						list.add(opportunitySummaryValue);
//						oppSummaryValueMap.put(ReportConstants.OPPORTUNITIES, list);
//					}else if(opportunitySummaryValue.getSalesStageCode() > 3 && opportunitySummaryValue.getSalesStageCode() <9){
//						list=oppSummaryValueMap.get(ReportConstants.PIPELINE);
//						list.add(opportunitySummaryValue);
//						oppSummaryValueMap.put(ReportConstants.PIPELINE, list);
//					}
				}
				
			}

			public void addItemToList(List<String> itemList, List<String> targetList){
				if(itemList.contains("All") || itemList.isEmpty()){
					targetList.add("");
				} else {
					targetList.addAll(itemList);
				}
			}
			
			public void addItemToListGeo(List<String> itemList, List<String> targetList){
				if(itemList.contains("All") || itemList.isEmpty()){
					targetList.add("");
				} else {
					targetList.addAll(geographyRepository.findByDisplayGeography(itemList));
				}
			}
}