package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.CurrencyValue;
import com.tcs.destination.bean.CustomerMasterT;
import com.tcs.destination.bean.CustomerRevenueValues;
import com.tcs.destination.bean.GroupCustomerGeoIouResponse;
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
/*
 * This service handles the Target Vs Actual, Connect, Bid, Opportunity, BDM Performance Reports functionalities
 */
@Service
public class ReportsService {

	private static final Logger logger = LoggerFactory
			.getLogger(ReportsService.class);

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

private static final String CONNECT_REPORT_QUERY_PREFIX = "select distinct CON.connect_id ";

private static final String CONNECT_SUMMARY_SUBSP_REPORT_QUERY_PREFIX =  "select count(distinct(connectIds)), displaySubSp from ( ";
private static final String CONNECT_SUMMARY_GEO_REPORT_QUERY_PREFIX =  "select count(distinct(connectIds)), displayGeography from ( ";
private static final String CONNECT_SUMMARY_IOU_REPORT_QUERY_PREFIX =  "select count(distinct(connectIds)), displayIou from ( ";
		
private static final String CONNECT_SUMMARY_SUBSP_REPORT_SUB_QUERY_PREFIX = "select distinct(CON.connect_id) as connectIds,COALESCE(display_sub_sp, 'SubSp Not Defined') as displaySubSp from connect_t CON  JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
+ " JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
+ " LEFT OUTER JOIN connect_sub_sp_link_t CSL ON (CON.connect_id=CSL.connect_id) LEFT OUTER JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp "; 

private static final String CONNECT_SUMMARY_GEO_REPORT_SUB_QUERY_PREFIX = "select distinct(CON.connect_id) as connectIds, GMT.display_geography as displayGeography from connect_t CON  JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
+ " JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
+ " LEFT OUTER JOIN connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id LEFT OUTER JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp "; 

private static final String CONNECT_SUMMARY_IOU_REPORT_SUB_QUERY_PREFIX = "select distinct(CON.connect_id) as connectIds, ICMT.display_iou as displayIou from connect_t CON  JOIN customer_master_t CMT ON CMT.customer_id=CON.customer_id "
+ " JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
+ " LEFT OUTER JOIN connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id LEFT OUTER JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp "; 


private static final String CONNECT_JOIN_CUS_GEO_IOU_SUBSP = "from connect_t CON "
		+ "   JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"
		+ "   JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
		+ "   JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
//			+ "   JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography"
		+ "   left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id"
		+ "   left outer JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp"
		+ " where ";

private static final String CONNECT_PARTNER_UNION_REPORT_QUERY_PREFIX = 
		" from connect_t CON   "
		+ "JOIN partner_master_t PAT ON  PAT.partner_id=CON.partner_id " 
		+ "JOIN geography_mapping_t GMT ON PAT.geography=GMT.geography  "  
		+ "JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography "   
		+ "left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "   
		+ "left outer JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp where ";

private static final String BID_REPORT_QUERY_PREFIX = " select distinct BID.bid_id from bid_details_t BID "
		+ "	 left outer JOIN bid_office_group_owner_link_t BIDGO ON BIDGO.bid_id=BID.bid_id"
		+ "  JOIN opportunity_t OPP ON BID.opportunity_id=OPP.opportunity_id"
		+ "  JOIN customer_master_t CMT ON  CMT.customer_id = OPP.customer_id"
		+ "  JOIN iou_customer_mapping_t ICMT on  CMT.iou = ICMT.iou"
		+ "  JOIN geography_mapping_t GMT on CMT.geography = GMT.geography"
		+ "  JOIN geography_country_mapping_t GCM ON GMT.geography = GCM.geography"
		+ "  left outer join opportunity_sub_sp_link_t OPSUBL ON OPP.opportunity_id = OPSUBL.opportunity_id"
		+ "  left outer JOIN sub_sp_mapping_t SSM ON OPSUBL.sub_sp = SSM.sub_sp "
		+ " where ";

// TargetVsActual Detailed Report
private static final String TARGET_VS_ACTUAL_PROJECTED_QUERY_PREFIX = "select CUST.customer_name,PRDT.quarter,sum(PRDT.revenue) from projected_revenues_data_t PRDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where CUST.customer_name not like 'UNKNOWN%' and ";

private static final String TARGET_VS_ACTUAL_ACTUAL_QUERY_PREFIX = "select CUST.customer_name,ARDT.quarter,sum(ARDT.revenue) from actual_revenues_data_t ARDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
		+ " join iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where CUST.customer_name not like 'UNKNOWN%' and ";

private static final String TARGET_VS_ACTUAL_TARGET_QUERY_PREFIX = "select CUST.customer_name,BDT.quarter,sum(BDT.target) from beacon_data_t BDT "
		+ " JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_map_id=BDT.beacon_customer_map_id "
		+ " JOIN customer_master_t CUST on CUST.customer_id=BCMT.customer_id "
		+ "	JOIN geography_mapping_t GMT on BCMT.customer_geography = GMT.geography "
		+ " JOIN iou_beacon_mapping_t ICMT on (ICMT.beacon_iou = CUST.iou) "  
		+ " where CUST.customer_name not like 'UNKNOWN%' AND";

// TargetVsActual Summary
private static final String TARGET_VS_ACTUAL_TOTAL_REVENUE_QUERY_PREFIX = "select sum(BDT.target) from beacon_data_t BDT "
		+ " JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_map_id=BDT.beacon_customer_map_id "
		+ " JOIN customer_master_t CUST on CUST.customer_id=BCMT.customer_id JOIN geography_mapping_t GMT on BCMT.customer_geography = GMT.geography "
		+ " JOIN iou_beacon_mapping_t ICMT on (ICMT.beacon_iou = CUST.iou) where ";

private static final String TARGET_VS_ACTUAL_TOTAL_ACT_PROJ_REVENUE_QUERY_PREFIX = 
		"select sum(actual_revenue) as revenue from ((select sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) " 
		+ " join iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where ";

private static final String TARGET_VS_ACTUAL_TOTAL_UNION_PROJECTED_REVENUE_QUERY_PREFIX = 
		" UNION (select case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
		+ " from projected_revenues_data_t PRDT  "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where ";

private static final String TOP30_CUSTOMERS_REVENUE_SUM_QUERY_PREFIX = "select sum(revenue) as top_revenue from (select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from "
		+ " ((select CUST.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) where CUST.customer_name not like 'UNKNOWN%' and ";

private static final String TOP_CUSTOMER_REVENUE_QUERY_PREFIX = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from  ((select CUST.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
		+ " JOIN sub_sp_mapping_t SSM on ARDT.sub_sp = SSM.actual_sub_sp where CUST.customer_name not like 'UNKNOWN%' and ";

private static final String OVER_ALL_CUSTOMER_REVENUE_QUERY_PREFIX = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from (((select CUST.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) where";
		
private static final String RCMT_GEO_COND_PREFIX = "RCMT.customer_geography in (";

private static final String GROUPBY_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX = " group by CUST.customer_name order by actual_revenue desc)";

private static final String TOP_CUSTOMER_REVENUE_UNION_QUERY_PREFIX = 
	" UNION (select CUST.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
	+ " from projected_revenues_data_t PRDT JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
	+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
	+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) JOIN sub_sp_mapping_t SSM on PRDT.sub_sp = SSM.actual_sub_sp "
	+ " where CUST.customer_name not like 'UNKNOWN%' and " ;

private static final String OVER_ALL_CUSTOMER_REVENUE_UNION_QUERY_PREFIX = 
	" UNION (select CUST.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
	+ " from projected_revenues_data_t PRDT JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
	+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id  JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
	+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where " ;

private static final String TOP_CUSTOMER_REVENUE_SUM_UNION_QUERY_PREFIX = 
	" UNION (select CUST.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
	+ " from projected_revenues_data_t PRDT JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
	+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
	+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where CUST.customer_name not like 'UNKNOWN%' and" ;

private static final String CUST_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX = "group by CUST.customer_name order by projected_revenue desc))"
		+ " as RVNU group by RVNU.customer_name order by revenue desc LIMIT ";

private static final String CUST_GROUP_CUST_ORDER_PROJECTED_REVENUE_COND_PREFIX = "group by CUST.customer_name order by projected_revenue desc)))"
		+ " as RVNU group by RVNU.customer_name order by revenue desc  ";

private static final String TARGET_VS_ACTUAL_TARGET_REVENUE_QUERY_PREFIX = "select CUST.customer_name,sum(BDT.target) as revenue_sum from beacon_data_t BDT "
		+ " JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_map_id=BDT.beacon_customer_map_id JOIN customer_master_t CUST on CUST.customer_id=BCMT.customer_id "
		+ " JOIN geography_mapping_t GMT on BCMT.customer_geography = GMT.geography "
		+ " JOIN iou_beacon_mapping_t ICMT on (BCMT.beacon_iou = ICMT.beacon_iou) where CUST.customer_name not like 'UNKNOWN%' and  ";

private static final String TARGET_VS_ACTUAL_OVERALL_TARGET_REVENUE_QUERY_PREFIX = "select CUST.customer_name,sum(BDT.target) as revenue_sum from beacon_data_t BDT "
		+ " JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_map_id=BDT.beacon_customer_map_id "
		+ " JOIN customer_master_t CUST on CUST.customer_id=BCMT.customer_id JOIN geography_mapping_t GMT on BCMT.customer_geography = GMT.geography "
		+ " JOIN iou_beacon_mapping_t ICMT on (ICMT.beacon_iou = CUST.iou)  where ";

private static final String OVERALL_REVENUE_BY_GEO_QUERY_PREFIX = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue, RVNU.display_geography from  (("
		+ " select CUST.customer_name, sum(ARDT.revenue) as actual_revenue, GMT.display_geography from actual_revenues_data_t ARDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) where ";

private static final String GROUP_BY_OVERALL_ACTUAL_REVENUE_BY_GEO_PREFIX= " group by CUST.customer_name,GMT.display_geography  order by actual_revenue desc) ";

private static final String OVERALL_REVENUE_BY_GEO_UNION_PROJECTED_QUERY_PREFIX = 
		" UNION (select CUST.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue , "
		+ " GMT.display_geography from projected_revenues_data_t PRDT JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where  "; 

private static final String GROUP_BY_OVERALL_PROJECTED_REVENUE_BY_GEO_PREFIX = 
		"group by CUST.customer_name, GMT.display_geography order by projected_revenue desc)) as RVNU group by RVNU.customer_name, RVNU.display_geography order by revenue desc ";

private static final String GROUP_CUST_GEO_IOU_QUERY_PREFIX = "select RVNU.customer_name, RVNU.group_customer_name, RVNU.display_iou, RVNU.display_geography from (( "
		+ " select distinct CUST.customer_name, CUST.group_customer_name, ICMT.display_iou, GMT.display_geography "
		+ " from actual_revenues_data_t ARDT JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=ARDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) where CUST.customer_name not like 'UNKNOWN%' and ";

private static final String GROUP_CUST_GEO_IOU_UNION_QUERY_PREFIX = "  UNION (select CUST.customer_name, CUST.group_customer_name, ICMT.display_iou, GMT.display_geography from projected_revenues_data_t PRDT "
		+ " JOIN revenue_customer_mapping_t RCMT on RCMT.revenue_customer_map_id=PRDT.revenue_customer_map_id "
		+ " JOIN customer_master_t CUST on RCMT.customer_id=CUST.customer_id "
		+ " JOIN geography_mapping_t GMT on (CUST.geography = GMT.geography and CUST.geography=RCMT.customer_geography) "
		+ " JOIN iou_customer_mapping_t ICMT on (CUST.iou = ICMT.iou) where CUST.customer_name not like 'UNKNOWN%' and ";

public static final String OPPORTUNITY_DETAILED_QUERY_PREFIX =
		"select distinct OPP.opportunity_id from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " where ";

public static final String OPPORTUNITY_SUMMARY_SUBSP_QUERY_PREFIX =
		"select distinct SSMT.display_sub_sp,count(SSMT.display_sub_sp),case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digital_deal_value from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " where subsp_primary=true and ";

public static final String OPPORTUNITY_SUMMARY_GEO_QUERY_PREFIX =
		"select distinct GMT.display_geography,count(GMT.display_geography),case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digital_deal_value from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " where ";

public static final String OPPORTUNITY_SUMMARY_IOU_QUERY_PREFIX = 
		"select distinct ICM.display_iou,count(ICM.display_iou),case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as digital_deal_value from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " where ";

public static final String OPPORTUNITY_PIPELINE_PROSPECTS_GEOGRAPHY_QUERY_PREFIX =
		"select distinct SASMT.sales_stage_description,case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,GMT.display_geography,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
		+ " from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
//			+ " inner join bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
		+ " where ";

public static final String OPPORTUNITY_PIPELINE_PROSPECTS_SERVICELINES_QUERY_PREFIX =
		"select distinct SSMT.display_sub_sp,case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
		+ " from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
//			+ " inner join bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " inner join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
		+ " where subsp_primary=true and ";

public static final String OPPORTUNITY_PIPELINE_PROSPECTS_IOU_QUERY_PREFIX =
		"select distinct SASMT.sales_stage_description,case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,ICM.display_iou,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
		+ " from opportunity_t OPP"
		+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
		+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
//			+ " inner join bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id"
		+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
		+ " left outer join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
		+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
		+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
		+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
		+ " where ";

private static final String CONNECT_START_AND_END_DATE_COND_PREFIX = " CON.start_datetime_of_connect between (:startDate) AND (:endDate)";
private static final String CONNECT_PRIMARY_OR_SECONDARY_OWNER_IN_PREFIX = " AND ((CON.primary_owner in (:userIds)) OR CSOL.secondary_owner in (:userIds) OR ('') in (:userIds)) ";

private static final String GEO_COND_PREFIX = "GMT.geography in (";

private static final String DISPLAY_GEOGRAPHY_COND_PREFIX = "AND GMT.display_geography = (:displayGeography) ";

private static final String DISPLAY_IOU_PREFIX = " AND ICMT.display_iou in (:iouList) ";

private static final String DISPLAY_SUBSP_PREFIX = " AND SSM.display_sub_sp in (:serviceLinesList) ";

private static final String COUNTRY_PREFIX = " AND GCM.country in (:countryList) ";


private static final String SUBSP_COND_PREFIX = "SSM.display_sub_sp in (";
private static final String IOU_COND_PREFIX = "ICMT.display_iou in (";
private static final String COUNTRY_COND_PREFIX = "GCM.country in (";

private static final String CONNECT_GROUP_BY_SUBSP_COND_PREFIX = " ) as CONNECT_SUBSP_SUMMARY group by displaySubSp ";
private static final String CONNECT_GROUP_BY_GEOGRAPHY_COND_PREFIX = " ) as CONNECT_GEOGRAPHY_SUMMARY group by displayGeography ";
private static final String CONNECT_GROUP_BY_IOU_COND_PREFIX = " ) as CONNECT_IOU_SUMMARY group by displayIou ";

private static final String BID_START_DATE_COND_PREFIX = "BID.bid_request_receive_date between '";
private static final String BID_END_DATE_COND_C_PREFIX = " AND '";
private static final String BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX = " (BIDGO.bid_office_group_owner in (";

private static final String TARVSACT_GEO_COND_PREFIX = "GMT.geography in  (";

private static final String TARVSACT_PROJECTED_GROUP_BY_COND_PREFIX = "group by CUST.customer_name,PRDT.quarter";
private static final String TARVSACT_ACTUAL_GROUP_BY_COND_PREFIX = "group by CUST.customer_name,ARDT.quarter";
private static final String TARVSACT_TARGET_GROUP_BY_COND_PREFIX = "group by CUST.customer_name,BDT.quarter";

private static final String TARVSACT_ACTUAL_AS_RVNU_COND_PREFIX = "))) as RVNU";
private static final String TOP30_GROUP_BY_COND_PREFIX = "30) as top_Revenue";
private static final String TARVSACT_GROUP_BY_ORDER_BY_REV_COND_PREFIX = "group by CUST.customer_name order by revenue_sum desc";

private static final String TOP_REVENUE_CUSTOMER_COND_PREFIX = "CUST.customer_name in (";

//private static final String TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX = " upper(PRDT.month) in (:months) ";
private static final String TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX = " PRDT.month in (:months) ";
//private static final String TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX = " upper(ARDT.month) in (:months) ";
private static final String TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX = " ARDT.month in (:months) ";
private static final String TARVSACTUAL_BEACON_QUARTER_COND_PREFIX = " BDT.quarter in (:quarters) ";

private static final String GEOGRAPHY_COND_PREFIX = " AND GMT.geography in (:geoList) ";

// ADDED STATIC STRINGS

	private static final String OPPORTUNITY_SALES_STAGE_CODE_COND_PREFIX = " OPP.sales_stage_code between 0 and 8 "+ Constants.OR_CLAUSE;
	private static final String OPPORTUNITY_SALES_STAGE_COND = "SASMT.sales_stage_code";
	private static final String OPPORTUNITY_START_DATE_COND_PREFIX = "OPP.deal_closure_date between "+Constants.SINGLE_QUOTE;
	private static final String OPPORTUNITY_END_DATE_COND_PREFIX = " AND "+Constants.SINGLE_QUOTE;
	private static final String OPPORTUNITY_GEO_COND_PREFIX = "GMT.geography in (";
	private static final String OPPORTUNITY_SUBSP_COND_PREFIX = "SSMT.display_sub_sp IN (";
	private static final String OPPORTUNITY_SALES_STAGE_COND_PREFIX = "OPP.sales_stage_code in (";
	private static final String OPPORTUNITY_IOU_COND_PREFIX = "ICM.display_iou in (";
	private static final String OPPORTUNITY_COUNTRY_COND_PREFIX = "OPP.country in (";
	private static final String OPPORTUNITY_GEO_GROUP_BY_COND_PREFIX = "group by GMT.display_geography";
	private static final String OPPORTUNITY_IOU_GROUP_BY_COND_PREFIX = "group by ICM.display_iou";
	private static final String OPPORTUNITY_SUBSP_GROUP_BY_COND_PREFIX = "group by SSMT.display_sub_sp";

	private static final String CONNECT_CATEGORY_CUSTOMER_PREFIX = " CON.connect_category='CUSTOMER' and ";
	private static final String CONNECT_CATEGORY_PARTNER_PREFIX = " CON.connect_category='PARTNER' and ";
	private static final String CONNECT_SECONDARY_OWNER_LINK_JOIN_PREFIX = " left outer join connect_secondary_owner_link_t CSOL ON CON.connect_id=CSOL.connect_id ";



/**
 * This method is used to get TargetVsActual Details based on privileges and the given input parameters
 * 
 * @param geography
 * @param iou
 * @param fromMonth
 * @param toMonth
 * @param currency
 * @param userId
 * @param countries
 * @return
 * @throws Exception
 */
public List<TargetVsActualDetailed> getTargetVsActual(
		List<String> geography, List<String> iou, String fromMonth,
		String toMonth, List<String> currency, String userId, List<String> countries)
		throws Exception {
	Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap = new TreeMap<String, List<TargetVsActualQuarter>>();
	List<TargetVsActualDetailed> targetVsActualDetails = new ArrayList<TargetVsActualDetailed>();
	List<String> geographyList = new ArrayList<String>();
	List<String> countryList = new ArrayList<String>();
	List<String> iouList = new ArrayList<String>();
	ExcelUtils.addItemToList(countries, countryList);
	if (toMonth.isEmpty()) {
		toMonth = DateUtils.getCurrentMonth();
	}
	List<String> formattedMonths = DateUtils.getAllMonthsBetween(fromMonth,
			toMonth);

	if (formattedMonths != null && !formattedMonths.isEmpty()) {
		for (String formattedMonth : formattedMonths) {
			DateUtils.getQuarterForMonth(formattedMonth);
		}
	}
	UserT user = userService.findByUserId(userId);
	if (user == null) {
		logger.error("NOT_FOUND: User not found: {}", userId);
		throw new DestinationException(HttpStatus.NOT_FOUND,
				"User not found: " + userId);
	} else {
		String userGroup = user.getUserGroupMappingT().getUserGroup();
		if (UserGroup.contains(userGroup)) {
			// Validate user group, BDM's & BDM supervisor's are not authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
			case PRACTICE_OWNER:
			case REPORTING_TEAM:
				logger.error("User is not authorized to access this service");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "User is not authorised to access this service");
			default:
				ExcelUtils.addItemToList(iou, iouList);
				addEmptyItemToListIfGeo(geography, geographyList);
				createMapForProjectedByUserPrevilages(formattedMonths, customerIdQuarterMap, userId,iouList, geographyList);
				mergeMapForActualByUserPrevilages(formattedMonths, customerIdQuarterMap, userId,iouList, geographyList);
				mergeMapForTargetByUserPrevilages(fromMonth, toMonth, customerIdQuarterMap, userId,iouList, geographyList);
				generateReponseFromMap(customerIdQuarterMap, targetVsActualDetails);
				setCurrency(targetVsActualDetails, currency);
				return targetVsActualDetails;
				}
		} else {
			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
		}
	}
}

/**
 * This method is used to get projected revenue  based on privileges and the given input parameters
 * 
 * @param formattedMonths
 * @param customerIdQuarterMap
 * @param userId
 * @param iouList
 * @param geographyList
 * @throws Exception
 */
private void createMapForProjectedByUserPrevilages(List<String> formattedMonths, 
		Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
		String userId, List<String> iouList, List<String> geographyList) throws Exception {

	// Form the native top revenue query string
	String queryString = getProjectedDetailsQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query tergetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	tergetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		tergetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		tergetVsActualReportQuery.setParameter("iouList", iouList);
	}
	
	List<Object[]> resultList = tergetVsActualReportQuery.getResultList();

	if (resultList != null) {
		for (Object[] projectedQuarter : resultList) {
			TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
			String customerName = projectedQuarter[0].toString();
			targetVsActualQuarter.setQuarter(projectedQuarter[1].toString());
			targetVsActualQuarter.setProjected((BigDecimal) projectedQuarter[2]);
			customerIdQuarterMap = createMapforQuarters(customerIdQuarterMap, targetVsActualQuarter, customerName);
		}
	}
}

/**
 * This method is used to get actual revenue based on privileges and the given input parameters
 * 
 * @param formattedMonths
 * @param customerIdQuarterMap
 * @param userId
 * @param iouList
 * @param geographyList
 * @throws Exception
 */
private void mergeMapForActualByUserPrevilages(List<String> formattedMonths, Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
		String userId, List<String> iouList, List<String> geographyList) throws Exception {

	// Form the native top revenue query string
	String queryString = getActualDetailsQueryString(customerIdQuarterMap, userId,iouList, geographyList);
	logger.info("Query string: {}", queryString);
	
	// Execute the native revenue query string
	Query tergetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	tergetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		tergetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		tergetVsActualReportQuery.setParameter("iouList", iouList);
	}
	
	List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
	for (Object[] actualRevenueObj : resultList) {
		String customerName = actualRevenueObj[0].toString();
		String quarter = actualRevenueObj[1].toString();
		BigDecimal actual = (BigDecimal) actualRevenueObj[2];
		if (customerIdQuarterMap.containsKey(customerName)) {
			List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap.get(customerName);
			boolean hasQuarter = false;
			for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualQuarterList) {
				if (targetVsActualQuarter.getQuarter().equals(quarter)) {
					targetVsActualQuarter.setActual(actual);
					hasQuarter = true;
				}
			}
			if (!hasQuarter) {
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				targetVsActualQuarter.setQuarter(quarter);
				targetVsActualQuarter.setActual(actual);
				targetVsActualQuarterList.add(targetVsActualQuarter);
			}
		} else {
			List<TargetVsActualQuarter> targetVsActualQuarterList = new ArrayList<TargetVsActualQuarter>();
			TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
			targetVsActualQuarter.setQuarter(quarter);
			targetVsActualQuarter.setActual(actual);
			targetVsActualQuarterList.add(targetVsActualQuarter);
			customerIdQuarterMap.put(customerName, targetVsActualQuarterList);
		}
	}
}

/**
 * This method is used to get target values  based on privileges and input parameters
 * 
 * @param fromMonth
 * @param toMonth
 * @param customerIdQuarterMap
 * @param userId
 * @param iouList
 * @param geographyList
 * @throws Exception
 */
private void mergeMapForTargetByUserPrevilages(String fromMonth, String toMonth,
		Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap, String userId, List<String> iouList, List<String> geographyList) throws Exception {
	
	String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
	String toQuarter = DateUtils.getQuarterForMonth(toMonth);

	List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter, toQuarter);
	// Form the native top revenue query string
	String queryString = getTargetDetailsQueryString(customerIdQuarterMap, userId,iouList, geographyList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	
	Query tergetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	tergetVsActualReportQuery.setParameter("quarters",quarterList);
	
	if(!geographyList.contains("") && geographyList!=null){
		tergetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		tergetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
	for (Object[] targetObj : resultList) {
		String customerName = targetObj[0].toString();
		String quarter = targetObj[1].toString();
		BigDecimal target = (BigDecimal) targetObj[2];
//k
		// target=target*(4-monthIndex)/3 ; monthIndex={1,2,3}
//			if (quarter.equals(fromQuarter)) {
//				target = target.multiply(
//						new BigDecimal(4 - DateUtils
//								.getMonthIndexOnQuarter(fromMonth))).divide(
//						new BigDecimal(3), 5, RoundingMode.HALF_UP);
//			}
//			if (quarter.equals(toQuarter)) {
//				target = target.multiply(
//						new BigDecimal(4 - DateUtils
//								.getMonthIndexOnQuarter(toMonth))).divide(
//						new BigDecimal(3), 5, RoundingMode.HALF_UP);
//			}
		if (customerIdQuarterMap.containsKey(customerName)) {
			List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap.get(customerName);
			boolean hasQuarter = false;
			for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualQuarterList) {
				if (targetVsActualQuarter.getQuarter().equals(quarter)) {
					targetVsActualQuarter.setTarget(target);
					hasQuarter = true;
				}
			}
			if (!hasQuarter) {
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				targetVsActualQuarter.setQuarter(quarter);
				targetVsActualQuarter.setTarget(target);
				targetVsActualQuarterList.add(targetVsActualQuarter);
			}
		} else {
			List<TargetVsActualQuarter> targetVsActualQuarterList = new ArrayList<TargetVsActualQuarter>();
			TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
			targetVsActualQuarter.setQuarter(quarter);
			targetVsActualQuarter.setTarget(target);
			targetVsActualQuarterList.add(targetVsActualQuarter);
			customerIdQuarterMap.put(customerName, targetVsActualQuarterList);
		}
	}

}

/**
 * This method is used to generate targetVsActual Response
 * 
 * @param customerIdQuarterMap
 * @param targetVsActualDetails
 * @throws Exception
 */
private void generateReponseFromMap(Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
		List<TargetVsActualDetailed> targetVsActualDetails) throws Exception {
	for (String customerName : customerIdQuarterMap.keySet()) {
		TargetVsActualDetailed targetVsActualDetailed = new TargetVsActualDetailed();
		List<TargetVsActualYearToDate> targetVsActualYtds = getTargetVsActualYtdList(customerIdQuarterMap.get(customerName));
		CustomerMasterT customerMasterT = customerRepository.findByCustomerName(customerName);
		targetVsActualDetailed.setCustomerMasterT(customerMasterT);
		targetVsActualDetailed.setYearToDate(targetVsActualYtds);
		targetVsActualDetails.add(targetVsActualDetailed);
	}
}

/**
 * This method is used to form actual detailed query  based on privileges and the input parameters
 * 
 * @param customerIdQuarterMap
 * @param userId
 * @param iouList
 * @param geographyList
 * @return
 * @throws Exception
 */
private String getActualDetailsQueryString(Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
		String userId, List<String> iouList, List<String> geographyList) throws Exception {
	logger.debug("Inside getTargetVsActualProjectedDetailedQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TARGET_VS_ACTUAL_ACTUAL_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder
			.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(TARVSACT_ACTUAL_GROUP_BY_COND_PREFIX);
	return queryBuffer.toString();

}

/**
 * This method is used to form target detailed query based on privileges and input parameters
 * 
 * @param quarterList
 * @param customerIdQuarterMap
 * @param userId
 * @param iouList
 * @param geographyList
 * @return
 * @throws Exception
 */
private String getTargetDetailsQueryString(Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
		String userId, List<String> iouList, List<String> geographyList) throws Exception {
	
	logger.debug("Inside getTargetVsActualProjectedDetailedQueryString() method");
	
	StringBuffer queryBuffer = new StringBuffer(TARGET_VS_ACTUAL_TARGET_QUERY_PREFIX);
	// Get user access privilege groups
	
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(TARVSACT_GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);

	// Get WHERE clause string
	queryBuffer.append(TARVSACTUAL_BEACON_QUARTER_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(TARVSACT_TARGET_GROUP_BY_COND_PREFIX);
	return queryBuffer.toString();
}


/**
 * This method is used to form the projected detailed query  based on privileges and input parameters
 * 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getProjectedDetailsQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getProjectedDetailsQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TARGET_VS_ACTUAL_PROJECTED_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder
			.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(TARVSACT_PROJECTED_GROUP_BY_COND_PREFIX);
	return queryBuffer.toString();
}


private void setCurrency(
		List<TargetVsActualDetailed> targetVsActualDetails,
		List<String> currencyList) throws DestinationException {
	try {
		for (TargetVsActualDetailed targetVsActualDetailed : targetVsActualDetails) {
			for (String currency : currencyList) {
				for (TargetVsActualYearToDate targetVsActualYearToDate : targetVsActualDetailed
						.getYearToDate()) {
					// Setting Actual Currency conversion
					if (targetVsActualYearToDate.getActual() != null) {
						CurrencyValue currencyValue = new CurrencyValue();
						currencyValue.setCurrency(currency);
						currencyValue.setValue(beaconConverterService
								.convert("INR", currency,
										targetVsActualYearToDate
												.getActual()));
						targetVsActualYearToDate.getActualValues().add(
								currencyValue);
					}
					// Setting Target Currency conversion
					if (targetVsActualYearToDate.getTarget() != null) {
						CurrencyValue currencyValue = new CurrencyValue();
						currencyValue.setCurrency(currency);
						currencyValue.setValue(beaconConverterService
								.convert("INR", currency,
										targetVsActualYearToDate
												.getTarget()));
						targetVsActualYearToDate.getTargetValues().add(
								currencyValue);
					}
					// Setting Projected Currency conversion
					if (targetVsActualYearToDate.getProjected() != null) {
						CurrencyValue currencyValue = new CurrencyValue();
						currencyValue.setCurrency(currency);
						currencyValue.setValue(beaconConverterService
								.convert("INR", currency,
										targetVsActualYearToDate
												.getProjected()));
						targetVsActualYearToDate.getProjectedValues().add(
								currencyValue);
					}
					// Setting Revenue Currency conversion
					if (targetVsActualYearToDate.getRevenue() != null) {
						CurrencyValue currencyValue = new CurrencyValue();
						currencyValue.setCurrency(currency);
						currencyValue.setValue(beaconConverterService
								.convert("INR", currency,
										targetVsActualYearToDate
												.getRevenue()));
						targetVsActualYearToDate.getRevenueValues().add(
								currencyValue);
					}
					// Setting the Target Quarter values
					{
						for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualYearToDate
								.getQuarterList()) {
							// Setting Actual Currency conversion
							if (targetVsActualQuarter.getActual() != null) {
								CurrencyValue currencyValue = new CurrencyValue();
								currencyValue.setCurrency(currency);
								currencyValue
										.setValue(beaconConverterService
												.convert(
														"INR",
														currency,
														targetVsActualQuarter
																.getActual()));
								targetVsActualQuarter.getActualValues()
										.add(currencyValue);
							}
							// Setting Target Currency conversion
							if (targetVsActualQuarter.getTarget() != null) {
								CurrencyValue currencyValue = new CurrencyValue();
								currencyValue.setCurrency(currency);
								currencyValue
										.setValue(beaconConverterService
												.convert(
														"INR",
														currency,
														targetVsActualQuarter
																.getTarget()));
								targetVsActualQuarter.getTargetValues()
										.add(currencyValue);
							}
							// Setting Projected Currency conversion
							if (targetVsActualQuarter.getProjected() != null) {
								CurrencyValue currencyValue = new CurrencyValue();
								currencyValue.setCurrency(currency);
								currencyValue
										.setValue(beaconConverterService
												.convert(
														"INR",
														currency,
														targetVsActualQuarter
																.getProjected()));
								targetVsActualQuarter.getProjectedValues()
										.add(currencyValue);
							}
							// Setting Revenue Currency conversion
							if (targetVsActualQuarter.getRevenue() != null) {
								CurrencyValue currencyValue = new CurrencyValue();
								currencyValue.setCurrency(currency);
								currencyValue
										.setValue(beaconConverterService
												.convert(
														"INR",
														currency,
														targetVsActualQuarter
																.getRevenue()));
								targetVsActualQuarter.getRevenueValues()
										.add(currencyValue);
							}
						}
					}
				}
			}
		}
	} catch (Exception e) {
		e.printStackTrace();
		throw new DestinationException(HttpStatus.INTERNAL_SERVER_ERROR,
				e.getLocalizedMessage());
	}

}


private void addEmptyItemToListIfGeo(List<String> displayGeography, List<String> geographyList) {
	if (displayGeography.contains("All") || displayGeography.isEmpty()) {
		geographyList.add("");
	} else {
		geographyList.addAll(geographyRepository.findByDisplayGeography(displayGeography));
	}
}


private List<TargetVsActualYearToDate> getTargetVsActualYtdList(
		List<TargetVsActualQuarter> targetVsActualQuarterList) {

	List<TargetVsActualYearToDate> targetVsActualYearToDates = new ArrayList<TargetVsActualYearToDate>();
	Map<String, List<TargetVsActualQuarter>> targetVsActualYtdMap = new HashMap<String, List<TargetVsActualQuarter>>();
	for (TargetVsActualQuarter targetVsActualQuarter : targetVsActualQuarterList) {
		String financialYear = DateUtils
				.getFinancialYearForQuarter(targetVsActualQuarter
						.getQuarter());
		targetVsActualYtdMap = createMapforQuarters(targetVsActualYtdMap,
				targetVsActualQuarter, financialYear);
	}
	for (String financialYear : targetVsActualYtdMap.keySet()) {
		TargetVsActualYearToDate targetVsActualYtd = new TargetVsActualYearToDate();
		targetVsActualYtd.setFinancialYear(financialYear);
		targetVsActualYtd.setQuarterList(targetVsActualYtdMap
				.get(financialYear));
		targetVsActualYearToDates.add(targetVsActualYtd);
	}

	return targetVsActualYearToDates;
}

private Map<String, List<TargetVsActualQuarter>> createMapforQuarters(
		Map<String, List<TargetVsActualQuarter>> quarterList,
		TargetVsActualQuarter targetVsActualQuarter, String key) {
	if (quarterList.containsKey(key)) {
		quarterList.get(key).add(targetVsActualQuarter);
	} else {
		List<TargetVsActualQuarter> targetVsActualQuarterMapList = new ArrayList<TargetVsActualQuarter>();
		targetVsActualQuarterMapList.add(targetVsActualQuarter);
		quarterList.put(key, targetVsActualQuarterMapList);
	}
	return quarterList;
}

// Excel Reports

public InputStreamResource getTargetVsActualReports(List<String> geography, List<String> countryList,
		List<String> iou, String fromMonth, String toMonth, List<String> currencyList, List<String> fields, String userId)
		throws Exception {
	SXSSFWorkbook workbook = new SXSSFWorkbook(50);
	String tillDate = DateUtils.getCurrentDate();
	//To Write The Report Title page
	buildExcelTargetVsActualDetailedReportService.getTargetVsActualTitlePage(workbook, geography, iou, userId, tillDate, currencyList, fromMonth, toMonth, "Summary, Detailed");
	//
	List<TargetVsActualDetailed> targetVsActualDetailedList = getTargetVsActual(geography, iou, fromMonth, toMonth, currencyList, userId, countryList);
	
	getTargetVsActualSummaryExcel(geography, iou, fromMonth, toMonth, currencyList, userId, workbook);
	
	buildExcelTargetVsActualDetailedReportService.getTargetVsActualExcel(targetVsActualDetailedList, fields, currencyList, fromMonth,
			workbook);
	
	InputStreamResource inputStream = getInputStreamResource(workbook);
	return inputStream;
}

public InputStreamResource getTargetVsActualDetailedReport(
		List<String> geography, List<String> countryList, List<String> iou, String fromMonth,
		String toMonth, List<String> currency, List<String> fields,
		String userId) throws Exception {
	SXSSFWorkbook workbook = new SXSSFWorkbook(50);
	List<TargetVsActualDetailed> targetVsActualDetailedList = getTargetVsActual(
			geography, iou, fromMonth, toMonth, currency, userId, countryList);
	if(targetVsActualDetailedList!=null){
	String tillDate = DateUtils.getCurrentDate();
	buildExcelTargetVsActualDetailedReportService
			.getTargetVsActualTitlePage(workbook, geography, iou, userId,
					tillDate, currency, fromMonth, toMonth, "Detailed");
	buildExcelTargetVsActualDetailedReportService.getTargetVsActualExcel(
			targetVsActualDetailedList, fields, currency, fromMonth,
			workbook);
	}else{
		logger.error("NOT_FOUND: Report could not be downloaded, as no targetVsActual details are available for user selection and privilege combination");
		throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no targetVsActual details are available for user selection and privilege combination");
	}
	InputStreamResource inputStream = getInputStreamResource(workbook);
	return inputStream;
}

public InputStreamResource getTargetVsActualSummaryReport(
		List<String> geography, List<String> countryList, List<String> iou, String fromMonth,
		String toMonth, List<String> currencyList, String userId)
		throws Exception {
	SXSSFWorkbook workbook = new SXSSFWorkbook(50);
	String tillDate = DateUtils.getCurrentDate();
	buildExcelTargetVsActualDetailedReportService
			.getTargetVsActualTitlePage(workbook, geography, iou, userId, tillDate, currencyList, fromMonth, toMonth, "Summary");
	getTargetVsActualSummaryExcel(geography, iou, fromMonth, toMonth,
			currencyList, userId, workbook);
	InputStreamResource inputStream = getInputStreamResource(workbook);
	return inputStream;
}

public void getTargetVsActualSummaryExcel(List<String> geography,
		List<String> iou, String fromMonth, String toMonth,
		List<String> currencyList, String userId, SXSSFWorkbook workbook)
		throws Exception {
	List<String> geographyList = new ArrayList<String>();
	List<String> iouList = new ArrayList<String>();
	BigDecimal totalTargetINR = new BigDecimal(0);
	BigDecimal totalActualProjectedINR = new BigDecimal(0);
	BigDecimal top30CustomersRevenueINR = new BigDecimal(0);
	List<CustomerRevenueValues> actualProjectedRevenuesList = null;
	Map<String, BigDecimal> targetRevenuesMap = null;
	Map<String, BigDecimal> targetOverAllRevenuesMap = null;
	Map<String, BigDecimal> actualProjectedOverAllRevenuesMap = null;
	List<Object[]> overAllRevenuesByGeoList = null;
	List<GroupCustomerGeoIouResponse> geoIouGroupCustNameList = null;
	int count = 30;
	if (toMonth.isEmpty()) {
		toMonth = DateUtils.getCurrentMonth();
	}
	List<String> formattedMonths = DateUtils.getAllMonthsBetween(fromMonth,
			toMonth);

	if (formattedMonths != null && !formattedMonths.isEmpty()) {
		for (String formattedMonth : formattedMonths) {
			DateUtils.getQuarterForMonth(formattedMonth);
		}
	}
	UserT user = userService.findByUserId(userId);
	if (user == null) {
		logger.error("NOT_FOUND: User not found: {}", userId);
		throw new DestinationException(HttpStatus.NOT_FOUND,
				"User not found: " + userId);
	} else {
		String userGroup = user.getUserGroupMappingT().getUserGroup();
		if (UserGroup.contains(userGroup)) {
			// Validate user group, BDM's & BDM supervisor's are not authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case BDM_SUPERVISOR:
			case PRACTICE_OWNER:
			case PRACTICE_HEAD:
			case REPORTING_TEAM:
				logger.error("User is not authorized to access this service");
				throw new DestinationException(HttpStatus.UNAUTHORIZED, "User is not authorised to access this service");
			default:
				ExcelUtils.addItemToList(iou, iouList);
				addEmptyItemToListIfGeo(geography, geographyList);
				if (formattedMonths == null || formattedMonths.isEmpty()) {
					throw new DestinationException(HttpStatus.BAD_REQUEST, "No Months available for this perticular range");
				}
				// sec 1
				totalTargetINR = getTotalTargetByUserPrivileges(fromMonth, toMonth, userId,geographyList,iouList);
				totalActualProjectedINR = getTotalActualProjectedByUserPrivileges(formattedMonths, userId,geographyList,iouList);
				top30CustomersRevenueINR = getTop30CustomersRevenueSumByUserPrivileges(formattedMonths, userId,geographyList,iouList);
				// sec 2
				targetOverAllRevenuesMap = getOverAllTargetRevenuesByUserPrivileges(fromMonth, toMonth, userId,geographyList,iouList);
				actualProjectedOverAllRevenuesMap = getOverAllActualProjectedRevenuesByUserPrivileges(formattedMonths, userId,geographyList,iouList);
				overAllRevenuesByGeoList = getOverAllRevenuesByGeoAndUserPrivileges(formattedMonths, userId,geographyList,iouList);
				// sec 3
				actualProjectedRevenuesList = getTopCustomersRevenueListByUserPrivileges(formattedMonths, userId, count,geographyList,iouList);
				targetRevenuesMap = getTargetRevenuesByUserPrivileges(fromMonth, toMonth, userId,geographyList,iouList);
				geoIouGroupCustNameList = getGroupCustGeoIouByUserPrivilages(formattedMonths, userId,geographyList,iouList);
			}
		} else {
			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST,
					"Invalid User Group");
		}
		if (totalTargetINR != null && totalActualProjectedINR != null && top30CustomersRevenueINR != null
				&& targetOverAllRevenuesMap != null && actualProjectedOverAllRevenuesMap != null
				&& overAllRevenuesByGeoList != null && actualProjectedRevenuesList != null
				&& targetRevenuesMap != null && geoIouGroupCustNameList != null) {
			buildExcelTargetVsActualSummaryReportService.getTargetVsActualSummaryExcel(fromMonth, totalTargetINR, 
					totalActualProjectedINR, top30CustomersRevenueINR, actualProjectedRevenuesList, targetRevenuesMap,
					currencyList, targetOverAllRevenuesMap, actualProjectedOverAllRevenuesMap, userId, formattedMonths, 
					workbook, overAllRevenuesByGeoList, geoIouGroupCustNameList);
		} else {
			logger.error("NOT_FOUND: Report could not be downloaded, as no targetVsActual details are available for user selection and privilege combination");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no targetVsActual details are available for user selection and privilege combination");
		}
	}
}


/**
 * This method is used to get Total target revenue for the given user
 * 
 * @param fromMonth
 * @param toMonth
 * @param userId
 * @param iouList 
 * @param geographyList 
 * @return
 * @throws Exception
 */
private BigDecimal getTotalTargetByUserPrivileges(String fromMonth, String toMonth, String userId, 
		List<String> geographyList, List<String> iouList) throws Exception {
	// Form the native top revenue query string
	String queryString = getTotalTargetRevenueQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	
	String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
	String toQuarter = DateUtils.getQuarterForMonth(toMonth);
	List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,toQuarter);
	
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("quarters",quarterList);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<BigDecimal> resultList = targetVsActualReportQuery.getResultList();
	return resultList.get(0);
}

/**
 * This method is used to get Total Target Revenue Query
 * 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getTotalTargetRevenueQueryString(String userId,List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalTargetRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TARGET_VS_ACTUAL_TOTAL_REVENUE_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(TARVSACTUAL_BEACON_QUARTER_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	return queryBuffer.toString();
}


/**
 * This Method is used to get total actual and projected revenue 
 * 
 * @param formattedMonths
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private BigDecimal getTotalActualProjectedByUserPrivileges(
		List<String> formattedMonths, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	String queryString = getTotalActualProjectedRevenueQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	List<BigDecimal> resultList = targetVsActualReportQuery.getResultList();
	return resultList.get(0);
}

/**
 * This method is used to get Total Revenue (Actual + Projected) Query
 * 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getTotalActualProjectedRevenueQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TARGET_VS_ACTUAL_TOTAL_ACT_PROJ_REVENUE_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	String whereClause = userAccessPrivilegeQueryBuilder
			.getUserAccessPrivilegeWhereConditionClause(userId,
					queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
			
	queryBuffer.append(TARGET_VS_ACTUAL_TOTAL_UNION_PROJECTED_REVENUE_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryUnionPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	String whereUnionClause = userAccessPrivilegeQueryBuilder
			.getUserAccessPrivilegeWhereConditionClause(userId, queryUnionPrefixMap);
	if (whereUnionClause != null && !whereUnionClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereUnionClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
			
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
			
	queryBuffer.append(TARVSACT_ACTUAL_AS_RVNU_COND_PREFIX);
	return queryBuffer.toString();
}


/**
 * This method is used to overall revenue by geography
 * 
 * @param formattedMonths
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private List<Object[]> getOverAllRevenuesByGeoAndUserPrivileges(
		List<String> formattedMonths, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	String queryString = getOverAllRevenuesByGeoQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);

	targetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = targetVsActualReportQuery.getResultList();
	return resultList;
}

/**
 * This method is used to form over all revenue by geography query
 * 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getOverAllRevenuesByGeoQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(OVERALL_REVENUE_BY_GEO_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(GROUP_BY_OVERALL_ACTUAL_REVENUE_BY_GEO_PREFIX);
	
	queryBuffer.append(OVERALL_REVENUE_BY_GEO_UNION_PROJECTED_QUERY_PREFIX);
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	queryBuffer.append(GROUP_BY_OVERALL_PROJECTED_REVENUE_BY_GEO_PREFIX);
	return queryBuffer.toString();
}

/**
 * This method is used to get Top 30 Customer Revenue 
 * 
 * @param formattedMonths
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private BigDecimal getTop30CustomersRevenueSumByUserPrivileges(List<String> formattedMonths, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	String queryString = getTop30CustomersRevenueSumQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<BigDecimal> resultList = targetVsActualReportQuery.getResultList();
	return resultList.get(0);
}

/**
 * This Method is used to get Top 30 Customer Revenue Query
 * 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getTop30CustomersRevenueSumQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalTargetRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TOP30_CUSTOMERS_REVENUE_SUM_QUERY_PREFIX);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX,null,IOU_COND_PREFIX,null);
	
	String whereClause = userAccessPrivilegeQueryBuilder
			.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(GROUPBY_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
	
	queryBuffer.append(TOP_CUSTOMER_REVENUE_SUM_UNION_QUERY_PREFIX);
	
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	
	// Get WHERE clause string
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
			
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(CUST_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX);
	queryBuffer.append(TOP30_GROUP_BY_COND_PREFIX);
	return queryBuffer.toString();
}


/**
 * This method is used to get overall target revenue details
 * 
 * @param fromMonth
 * @param toMonth
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private Map<String, BigDecimal> getOverAllTargetRevenuesByUserPrivileges(
		String fromMonth, String toMonth, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
	String toQuarter = DateUtils.getQuarterForMonth(toMonth);
	List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter, toQuarter);
	
	Map<String, BigDecimal> targetRevenues = new TreeMap<String, BigDecimal>();
	// Form the native top revenue query string
	String queryString = getOverAllTargetRevenueQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("quarters",quarterList);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = targetVsActualReportQuery.getResultList();
	for (Object[] targetRevenueObj : resultList) {
		String customerName = targetRevenueObj[0].toString();
		BigDecimal revenue = (BigDecimal) (targetRevenueObj[1]);
		targetRevenues.put(customerName, revenue);
	}
	return targetRevenues;
}

/**
 * This Method is used to get target revenue 
 * 
 * @param fromMonth
 * @param toMonth
 * @param userId
 * @param iouList 
 * @param geographyList 
 * @return
 * @throws Exception
 */
private Map<String, BigDecimal> getTargetRevenuesByUserPrivileges(
		String fromMonth, String toMonth, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
	String toQuarter = DateUtils.getQuarterForMonth(toMonth);
	List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter, toQuarter);
	Map<String, BigDecimal> targetRevenues = new TreeMap<String, BigDecimal>();
	// Form the native top revenue query string
	String queryString = getTargetRevenueQueryString(quarterList, userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("quarters",quarterList);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = targetVsActualReportQuery.getResultList();
	for (Object[] targetRevenueObj : resultList) {
		String customerName = targetRevenueObj[0].toString();
		BigDecimal revenue = (BigDecimal) (targetRevenueObj[1]);
		targetRevenues.put(customerName, revenue);
	}
	return targetRevenues;
}

/**
 * This method is used to form target revenue query
 * 
 * @param quarterList
 * @param userId
 * @param iouList 
 * @param geographyList 
 * @return
 * @throws Exception
 */
private String getTargetRevenueQueryString(List<String> quarterList,
		String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalTargetRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(
			TARGET_VS_ACTUAL_TARGET_REVENUE_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(TARVSACTUAL_BEACON_QUARTER_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(TARVSACT_GROUP_BY_ORDER_BY_REV_COND_PREFIX);
	return queryBuffer.toString();
}

/**
 * This Method is used to get customer name, group customer name, display geography and display iou for the given input paramaters
 * 
 * @param formattedMonths
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private List<GroupCustomerGeoIouResponse> getGroupCustGeoIouByUserPrivilages(
		List<String> formattedMonths, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	List<GroupCustomerGeoIouResponse> revenueGeoValuesList = new ArrayList<GroupCustomerGeoIouResponse>();
	String queryString = getGroupCustGeoIouQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = targetVsActualReportQuery.getResultList();
	for (Object[] actualRevenueObj : resultList) {
		GroupCustomerGeoIouResponse revenueGeoValues = new GroupCustomerGeoIouResponse();
		String customerName = actualRevenueObj[0].toString();
		String groupCustomerName = actualRevenueObj[1].toString();
		String geography = actualRevenueObj[3].toString();
		String iou = actualRevenueObj[2].toString();
		revenueGeoValues.setCustomerName(customerName);
		revenueGeoValues.setGroupCustomerName(groupCustomerName);
		revenueGeoValues.setDisplayGeography(geography);
		revenueGeoValues.setDisplayIou(iou);
		revenueGeoValuesList.add(revenueGeoValues);
	}
	return revenueGeoValuesList;
}

/**
 * This Method is used to form to get group customer, geography and display iou
 * 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getGroupCustGeoIouQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(GROUP_CUST_GEO_IOU_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(GROUP_CUST_GEO_IOU_UNION_QUERY_PREFIX);
	// Get user access privilege groups
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(TARVSACT_ACTUAL_AS_RVNU_COND_PREFIX);
	return queryBuffer.toString();
}

/**
 * This Method is used to get overall target revenue query
 * 
 * @param quarterList
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getOverAllTargetRevenueQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalTargetRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TARGET_VS_ACTUAL_OVERALL_TARGET_REVENUE_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(TARVSACTUAL_BEACON_QUARTER_COND_PREFIX);
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(TARVSACT_GROUP_BY_ORDER_BY_REV_COND_PREFIX);
	return queryBuffer.toString();
}

/**
 * This method is used to get Top customers revenue details 
 * 
 * @param formattedMonths
 * @param userId
 * @param count
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private List<CustomerRevenueValues> getTopCustomersRevenueListByUserPrivileges(
		List<String> formattedMonths, String userId, int count, List<String> geographyList, List<String> iouList)
		throws Exception {
	List<CustomerRevenueValues> revenueGeoValuesList = new ArrayList<CustomerRevenueValues>();
	
	String queryString = getActualProjectedTopRevenuesQueryString(
			formattedMonths, userId, count,GEO_COND_PREFIX,null,IOU_COND_PREFIX,null, geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = targetVsActualReportQuery.getResultList();
	for (Object[] actualRevenueObj : resultList) {
		CustomerRevenueValues revenueGeoValues = new CustomerRevenueValues();
		String customerName = actualRevenueObj[0].toString();
		BigDecimal revenue = (BigDecimal) (actualRevenueObj[1]);
		revenueGeoValues.setCustomerName(customerName);
		revenueGeoValues.setValue(revenue);
		revenueGeoValuesList.add(revenueGeoValues);
	}
	return revenueGeoValuesList;
}

/*
 * This method returns query for top customers based on revenue including actuals as well as projected
 */
public String getTopRevenueCustomersForDashboard(String userId,int count) throws Exception {
	return getActualProjectedTopRevenuesQueryString(userId, count, 
			RCMT_GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, TOP_REVENUE_CUSTOMER_COND_PREFIX);
}

private String getActualProjectedTopRevenuesQueryString(
		String userId, int count, String geoPrefix, String subSpPrefix, String iouPrefix, String custPrefix)
		throws Exception {
	logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(TOP_CUSTOMER_REVENUE_QUERY_PREFIX);
	
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(geoPrefix, subSpPrefix, iouPrefix, custPrefix);

	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	queryBuffer.append(GROUPBY_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
	
	queryBuffer.append(TOP_CUSTOMER_REVENUE_UNION_QUERY_PREFIX);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	queryBuffer.append(CUST_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX);
	queryBuffer.append(count);
	return queryBuffer.toString();
}





/**
 * This method is used to form top customers revenue query
 * 
 * @param formattedMonths
 * @param userId
 * @param count
 * @param geoPrefix
 * @param subSpPrefix
 * @param iouPrefix
 * @param custPrefix
 * @param iouList 
 * @param geographyList 
 * @return
 * @throws Exception
 */
private String getActualProjectedTopRevenuesQueryString(
		List<String> formattedMonths, String userId, int count, String geoPrefix, String subSpPrefix, String iouPrefix, String custPrefix, List<String> geographyList, List<String> iouList)
		throws Exception {
	logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
	
	StringBuffer queryBuffer = new StringBuffer(TOP_CUSTOMER_REVENUE_QUERY_PREFIX);
	
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(geoPrefix, subSpPrefix, iouPrefix, custPrefix);

	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(GROUPBY_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
	
	queryBuffer.append(TOP_CUSTOMER_REVENUE_UNION_QUERY_PREFIX);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(CUST_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX);
	queryBuffer.append(count);
	return queryBuffer.toString();
}

/**
 * This method is used to get over all (actual+projected) revenue
 * 
 * @param formattedMonths
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private Map<String, BigDecimal> getOverAllActualProjectedRevenuesByUserPrivileges(
		List<String> formattedMonths, String userId, List<String> geographyList, List<String> iouList) throws Exception {
	Map<String, BigDecimal> actualProjectedOverAllRevenues = new TreeMap<String, BigDecimal>();

	String queryString = getOverAllActualRevenuesQueryString(userId,geographyList,iouList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query targetVsActualReportQuery = entityManager.createNativeQuery(queryString);
	
	targetVsActualReportQuery.setParameter("months",formattedMonths);
	
	if(!geographyList.contains("") && geographyList!=null){
		targetVsActualReportQuery.setParameter("geoList", geographyList);
	}
	
	if(!iouList.contains("") && iouList!=null){
		targetVsActualReportQuery.setParameter("iouList",iouList);
	}
	
	List<Object[]> resultList = targetVsActualReportQuery.getResultList();
	for (Object[] actualRevenueObj : resultList) {
		String customerName = actualRevenueObj[0].toString();
		BigDecimal revenue = (BigDecimal) (actualRevenueObj[1]);
		actualProjectedOverAllRevenues.put(customerName, revenue);
	}
	return actualProjectedOverAllRevenues;
}

/**
 * This method is used to form over all revenue query 
 * @param userId
 * @param geographyList
 * @param iouList
 * @return
 * @throws Exception
 */
private String getOverAllActualRevenuesQueryString(String userId, List<String> geographyList, List<String> iouList) throws Exception {
	logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
	
	StringBuffer queryBuffer = new StringBuffer(OVER_ALL_CUSTOMER_REVENUE_QUERY_PREFIX);
	
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX,null,IOU_COND_PREFIX,null);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_ACTUAL_COND_PREFIX);
	
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(GROUPBY_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
	
	queryBuffer.append(OVER_ALL_CUSTOMER_REVENUE_UNION_QUERY_PREFIX);
	
	// Get WHERE clause string
	queryBuffer.append(TARGETVSACTUAL_MONTHS_PROJECTED_COND_PREFIX);
	
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(GEOGRAPHY_COND_PREFIX);
	}
	
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	
	queryBuffer.append(CUST_GROUP_CUST_ORDER_PROJECTED_REVENUE_COND_PREFIX);
	return queryBuffer.toString();
}


/**
 * This method forms and executes the query to find customers connects based on user access privileges
 * 
 * @param fromDate
 * @param toDate
 * @param userId
 * @return
 * @throws Exception
 */
private List<String> getCustomerConnectDetailsBasedOnUserPrivileges(
		Date fromDate, Date toDate, String userId, String displayGeography, List<String> iouList, 
		List<String> serviceLinesList, List<String> countryList) throws Exception {
	logger.debug("Inside getConnectDetailsBasedOnUserPrivileges() method");
	// Form the native top revenue query string
	String queryString = getCustomerConnectDetailedQueryString(userId, displayGeography,iouList,serviceLinesList,countryList);
	logger.info("Query string: {}", queryString);
	
	// Execute the native revenue query string
	Query connectDetailedReportQuery = entityManager.createNativeQuery(queryString);
	connectDetailedReportQuery.setParameter("startDate",fromDate);
	connectDetailedReportQuery.setParameter("endDate",toDate);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		connectDetailedReportQuery.setParameter("displayGeography", displayGeography);
	}
	if(!iouList.contains("") && iouList!=null){
		connectDetailedReportQuery.setParameter("iouList", iouList);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		connectDetailedReportQuery.setParameter("serviceLinesList", serviceLinesList);
	}
	if(!countryList.contains("") && countryList!=null){
		connectDetailedReportQuery.setParameter("countryList", countryList);
	}
	
	List<String> connectDetailsList = connectDetailedReportQuery.getResultList();
	return connectDetailsList;
}

/**
 * This method forms and executes the query to find partner connects based on user access privileges
 * 
 * @param fromDate
 * @param toDate
 * @param userId
 * @param countryList 
 * @param serviceLinesList 
 * @param iouList 
 * @param displayGeography 
 * @return
 * @throws Exception
 */
private List<String> getPartnerConnectDetails(
		Date fromDate, Date toDate, String userId, String displayGeography, List<String> serviceLinesList, List<String> countryList) throws Exception {
	logger.info("Inside getConnectDetailsBasedOnUserPrivileges() method");
	// Form the native top revenue query string
	String queryString = getPartnerConnectDetailedQueryString(userId, displayGeography,serviceLinesList,countryList);
	logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query connectDetailedReportQuery = entityManager.createNativeQuery(queryString);
	connectDetailedReportQuery.setParameter("startDate",fromDate);
	connectDetailedReportQuery.setParameter("endDate",toDate);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		connectDetailedReportQuery.setParameter("displayGeography", displayGeography);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		connectDetailedReportQuery.setParameter("serviceLinesList", serviceLinesList);
	}
	if(!countryList.contains("") && countryList!=null){
		connectDetailedReportQuery.setParameter("countryList", countryList);
	}
	
	List<String> connectDetailsList = connectDetailedReportQuery.getResultList();
	return connectDetailsList;
}


/**
 * This Method is used to construct the Geo or Iou Heads customer connect summary details by displayGeo 
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param geography
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @param userIds
 * @return
 * @throws Exception
 */
private String getConnectGeoSummaryQueryString(String userId,
		Date fromDate, Date toDate, String geography, List<String> iouList, List<String> serviceLinesList, List<String> countryList, List<String> userIds) throws Exception {
	logger.info("Inside getConnectGeoSummaryQueryString() method");
	
	StringBuffer queryBuffer = new StringBuffer(CONNECT_SUMMARY_GEO_REPORT_QUERY_PREFIX);
	queryBuffer.append(CONNECT_SUMMARY_GEO_REPORT_SUB_QUERY_PREFIX);
	String unionQuery ="UNION " +CONNECT_SUMMARY_GEO_REPORT_SUB_QUERY_PREFIX;
	getCustomerConnectSubSpQueryString(userId, fromDate, toDate, queryBuffer, unionQuery,geography,iouList,serviceLinesList,countryList,userIds,"displayGeography");
	queryBuffer.append(CONNECT_GROUP_BY_GEOGRAPHY_COND_PREFIX);
	
	return queryBuffer.toString();
}

/**
 * This Method is used to construct the Geo or Iou Heads customer connect summary details by displayIou
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param geography
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @param userIds
 * @return
 * @throws Exception
 */
private String getConnectIouSummaryQueryString(String userId,
		Date fromDate, Date toDate, String geography, List<String> iouList, List<String> serviceLinesList, List<String> countryList, List<String> userIds) throws Exception {
	logger.debug("Inside getConnectSummaryQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(CONNECT_SUMMARY_IOU_REPORT_QUERY_PREFIX);
	queryBuffer.append(CONNECT_SUMMARY_IOU_REPORT_SUB_QUERY_PREFIX);
	String unionQuery ="UNION " +CONNECT_SUMMARY_IOU_REPORT_SUB_QUERY_PREFIX;
	getCustomerConnectSubSpQueryString(userId, fromDate, toDate, queryBuffer, unionQuery,geography,iouList,serviceLinesList,countryList,userIds,"displayIou");
	queryBuffer.append(CONNECT_GROUP_BY_IOU_COND_PREFIX);
	return queryBuffer.toString();
}

/**
 * This Method is used to construct the Geo or Iou Heads customer connect summary details by subsp 
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param countryList 
 * @param serviceLinesList 
 * @param iouList 
 * @param geography 
 * @param userIds 
 * @return
 * @throws Exception
 */
private String getConnectSubSpSummaryQueryString(String userId,
		Date fromDate, Date toDate, String geography, List<String> iouList, List<String> serviceLinesList, List<String> countryList, List<String> userIds) throws Exception {
	logger.info("Inside getConnectSummaryQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(CONNECT_SUMMARY_SUBSP_REPORT_QUERY_PREFIX);
	queryBuffer.append(CONNECT_SUMMARY_SUBSP_REPORT_SUB_QUERY_PREFIX);
	String unionQuery = "UNION " +CONNECT_SUMMARY_SUBSP_REPORT_SUB_QUERY_PREFIX;
	getCustomerConnectSubSpQueryString(userId, fromDate, toDate, queryBuffer, unionQuery,geography,iouList,serviceLinesList,countryList,userIds,"displaySubSp");
	queryBuffer.append(CONNECT_GROUP_BY_SUBSP_COND_PREFIX);
	return queryBuffer.toString();
}

/**
 * This method is used to construct the customer connect summary details by subsp based on privileges + subordinate logic
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param queryBuffer
 * @param unionPartner
 * @param countryList 
 * @param serviceLinesList 
 * @param iouList 
 * @param geography 
 * @param userIds 
 * @throws Exception
 */
public void getCustomerConnectSubSpQueryString(String userId, Date fromDate, Date toDate, 
		StringBuffer queryBuffer, String unionPartner, String displayGeography, List<String> iouList, 
		List<String> serviceLinesList, List<String> countryList, List<String> userIds, String groupBy) throws Exception {
	logger.debug("Inside getCustomerConnectSubSpQueryString() method");
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, null);
	// Get WHERE clause string   
	queryBuffer.append("where ");
	queryBuffer.append(CONNECT_START_AND_END_DATE_COND_PREFIX);
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!displayGeography.equals("") && displayGeography!=null){
		queryBuffer.append(DISPLAY_GEOGRAPHY_COND_PREFIX);
	}
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		queryBuffer.append(DISPLAY_SUBSP_PREFIX);
	}
	if(!countryList.contains("") && countryList!=null){
		queryBuffer.append(COUNTRY_PREFIX);
	}
	
	queryBuffer.append(" group by "+groupBy+ ",CON.connect_id ");
	queryBuffer.append(unionPartner);
	queryBuffer.append(CONNECT_SECONDARY_OWNER_LINK_JOIN_PREFIX);
	queryBuffer.append(" where ");
	// Get WHERE clause string
	queryBuffer.append(CONNECT_START_AND_END_DATE_COND_PREFIX);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		queryBuffer.append(DISPLAY_GEOGRAPHY_COND_PREFIX);
	}
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		queryBuffer.append(DISPLAY_SUBSP_PREFIX);
	}
	if(!countryList.contains("") && countryList!=null){
		queryBuffer.append(COUNTRY_PREFIX);
	}
	
	queryBuffer.append(CONNECT_PRIMARY_OR_SECONDARY_OWNER_IN_PREFIX);
	
	queryBuffer.append("group by "+groupBy+ ",CON.connect_id");
}


/**
 * This Method returns the Connect Detailed Query String
 * @param userId
 * @param fromDate
 * @param toDate
 * @return Query string
 * @throws Exception
 */
private String getCustomerConnectDetailedQueryString(String userId,String displayGeography, List<String> iouList, 
		List<String> serviceLinesList, List<String> countryList) throws Exception {
	logger.debug("Inside getRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(CONNECT_REPORT_QUERY_PREFIX);
	queryBuffer.append(CONNECT_JOIN_CUS_GEO_IOU_SUBSP);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(CONNECT_CATEGORY_CUSTOMER_PREFIX);
	queryBuffer.append(CONNECT_START_AND_END_DATE_COND_PREFIX);
	
	String whereClause = 
			userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	
	if(!displayGeography.equals("") && displayGeography!=null){
		queryBuffer.append(DISPLAY_GEOGRAPHY_COND_PREFIX);
	}
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(DISPLAY_IOU_PREFIX);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		queryBuffer.append(DISPLAY_SUBSP_PREFIX);
	}
	if(!countryList.contains("") && countryList!=null){
		queryBuffer.append(COUNTRY_PREFIX);
	}

	return queryBuffer.toString();
}



/**
 * This Method returns the Connect Detailed Query String
 * @param userId
 * @param fromDate
 * @param toDate
 * @param countryList 
 * @param serviceLinesList 
 * @param iouList 
 * @param displayGeography 
 * @return Query string
 * @throws Exception
 */
private String getPartnerConnectDetailedQueryString(String userId, String displayGeography, List<String> serviceLinesList, List<String> countryList) throws Exception {
	logger.debug("Inside getRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(CONNECT_REPORT_QUERY_PREFIX);
	queryBuffer.append(CONNECT_PARTNER_UNION_REPORT_QUERY_PREFIX);
	// Get WHERE clause string
	queryBuffer.append(CONNECT_CATEGORY_PARTNER_PREFIX);
	queryBuffer.append(CONNECT_START_AND_END_DATE_COND_PREFIX);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		queryBuffer.append(DISPLAY_GEOGRAPHY_COND_PREFIX);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		queryBuffer.append(DISPLAY_SUBSP_PREFIX);
	}
	if(!countryList.contains("") && countryList!=null){
		queryBuffer.append(COUNTRY_PREFIX);
	}
	return queryBuffer.toString();
}


/**
 * This method validate the input details based on user access and privileges, and gets the connect detailed report in excel
 * 
 * @param month
 * @param quarter
 * @param year
 * @param iou
 * @param geography
 * @param country
 * @param serviceLines
 * @param userId
 * @param fields
 * @param connectCategory 
 * @return connect detailed report in excel from
 * @throws Exception
 */
public InputStreamResource getConnectDetailedReport(String month,
		String quarter, String year, List<String> iou, String displayGeography, List<String> country,
		List<String> serviceLines, String userId, List<String> fields, String connectCategory)
		throws Exception {
	logger.debug("Inside getConnectDetailedReport Service");
	UserT user = userService.findByUserId(userId);
	
	if (user != null) {
		
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		
		List<String> iouList = new ArrayList<String>();
		
		List<String> countryList = new ArrayList<String>();
		
		List<String> serviceLinesList = new ArrayList<String>();
		
		List<String> connectIdList = new ArrayList<String>();
		
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		
		ExcelUtils.addItemToList(iou, iouList);
		
		ExcelUtils.addItemToList(serviceLines, serviceLinesList);
		
		ExcelUtils.addItemToList(country, countryList);
		
		String userGroup = user.getUserGroupMappingT().getUserGroup();
		
		List<String> userIds = new ArrayList<String>();
		
		String category="";
		
		if (UserGroup.contains(userGroup)) {
		
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			
			case BDM:
			case PRACTICE_OWNER:
			case DELIVERY_MANAGER:
				userIds.add(userId);
				connectIdList = getConnectDetailsByUserIds(fromDate,toDate,userIds,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				break;

			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
			case DELIVERY_CLUSTER_HEAD:
			case DELIVERY_CENTRE_HEAD:
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
				userIds.add(userId);
				connectIdList = getConnectDetailsByUserIds(fromDate,toDate,userIds,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				break;
			
			case GEO_HEADS:
			case IOU_HEADS:
			case PMO:
				connectIdList = getGeoHeadOrIouHeadConnectDetails(fromDate,toDate,userId,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				break;
			
			default:
				connectIdList = getConnectIdsForStratagicInitiative(fromDate,toDate,userId,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				break;
			}
		} else {
			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
		}
		if (!connectIdList.isEmpty()) {
			
			connectDetailedReportService.getConnectTitlePage(workbook, displayGeography, iou, serviceLines, user, country, month, quarter, year, ReportConstants.DETAILED,connectCategory);
			
			connectDetailedReportService.setConnectDetailedReport(connectIdList, fields, workbook,connectCategory);
			
		} else {
			if(!connectCategory.equals("All")){
				category=category+connectCategory+Constants.SPACE;
			}
			logger.error("NOT_FOUND: Report could not be downloaded, as no"+category+"connects are available for user selection and privilege combination");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no"+category+"connects are available for user selection and privilege combination");
		}
		
		InputStreamResource inputStreamResource = getInputStreamResource(workbook);
		
		return inputStreamResource;
	
	} else {
		logger.error("NOT_FOUND: User not found: {}", userId);
		throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
	}
}

/**
 * This method is used to convert workbook to inputStreamResource
 * 
 * @param workbook
 * @return
 * @throws IOException
 */
private InputStreamResource getInputStreamResource(SXSSFWorkbook workbook)
		throws IOException {
	ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
	workbook.write(byteOutPutStream);
	byteOutPutStream.flush();
	byteOutPutStream.close();
	byte[] bytes = byteOutPutStream.toByteArray();
	InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
	return inputStreamResource;
}

/**
 * This Method is used to get customer or partner or both connect id's for SI
 * 
 * @param fromDate
 * @param toDate
 * @param userId
 * @param iouList
 * @param geographyList
 * @param countryList
 * @param serviceLinesList
 * @param connectCategory
 * @return
 * @throws Exception 
 */
private List<String> getConnectIdsForStratagicInitiative(Date fromDate,
		Date toDate, String userId, List<String> iouList, String displayGeography, List<String> countryList,
		List<String> serviceLinesList, String connectCategory) throws Exception {
	
		List<String> connectIdList = new ArrayList<String>();
	
		if(isCustomer(connectCategory)){
			
			List<String> customerConnectIdList = connectRepository.findCustomerConnectIds(new Timestamp(fromDate.getTime()),
					new Timestamp(toDate.getTime()), iouList, displayGeography, countryList, serviceLinesList);
			connectIdList.addAll(customerConnectIdList);
		}
		
		if(isPartner(connectCategory)){
			
			List<String> partnerConnectIdList = connectRepository.findPartnerConnectIds(new Timestamp(fromDate.getTime()),
					new Timestamp(toDate.getTime()), displayGeography, countryList, serviceLinesList);
			connectIdList.addAll(partnerConnectIdList);
	}		
	
	return connectIdList;
}

/**
 * This method is used to get Geo Head or Iou Head Connect Details
 * 
 * @param fromDate
 * @param toDate
 * @param userIds
 * @param iouList
 * @param geographyList
 * @param countryList
 * @param serviceLinesList
 * @param connectCategory
 * @return
 * @throws Exception 
 */
private List<String> getGeoHeadOrIouHeadConnectDetails(Date fromDate, Date toDate, String userId, List<String> iouList,
		String displayGeography, List<String> countryList, List<String> serviceLinesList, String connectCategory) throws Exception {
	
	Set<String> connectIdSet = new HashSet<String>();
	List<String> userIds = new ArrayList<String>();
		
	if(isCustomer(connectCategory)){
	
		List<String> customerConnectIdList = getCustomerConnectDetailsBasedOnUserPrivileges(fromDate, toDate, userId,displayGeography,iouList,serviceLinesList,countryList);
			connectIdSet.addAll(customerConnectIdList);
		} 
	
	if(isPartner(connectCategory)){
	
		List<String> partnerConnectIdList = getPartnerConnectDetails(fromDate, toDate, userId,displayGeography,serviceLinesList,countryList);	
			connectIdSet.addAll(partnerConnectIdList);
		}

	userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
	userIds.add(userId);

	List<String> subOrdinatesConnectList = getConnectDetailsByUserIds(fromDate,toDate,userIds,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
	connectIdSet.addAll(subOrdinatesConnectList);
	List<String> connectList = new ArrayList<String>(connectIdSet);
	return connectList;
}


/**
 * This Method is used to get the Customer Connects or Partner Connects Or Both for the given input parameters
 * @param fromDate
 * @param toDate
 * @param userId
 * @param iouList
 * @param geographyList
 * @param countryList
 * @param serviceLinesList
 * @param connectCategory
 * @return
 */
private List<String> getConnectDetailsByUserIds(Date fromDate, Date toDate, List<String> userIds, List<String> iouList, String displayGeography,
		List<String> countryList, List<String> serviceLinesList, String connectCategory) {
	List<String> connectList = new ArrayList<String>();
	
	if(isCustomer(connectCategory)){
		
		List<String> customerConnectList = connectRepository.findByCustomerConnects(new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), 
				userIds, iouList, displayGeography, countryList, serviceLinesList);
		connectList.addAll(customerConnectList);
	}
	
	if(isPartner(connectCategory)){
		
		List<String> partnerConnectList = connectRepository.findByPartnerConnects(new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), 
				userIds, displayGeography, countryList, serviceLinesList);
		connectList.addAll(partnerConnectList);
	}
	return connectList;
}

/**
 * This Method is used to check whether connect category is partner or All
 * 
 * @param connectCategory
 * @return
 */
private boolean isPartner(String connectCategory) {
	return connectCategory.equals(ReportConstants.PARTNER) || connectCategory.equals(ReportConstants.All);
}

/**
 * This Method is used to check whether connect category is customer or All
 * 
 * @param connectCategory
 * @return
 */
private boolean isCustomer(String connectCategory) {
	return connectCategory.equals(ReportConstants.CUSTOMER) || connectCategory.equals(ReportConstants.All);
}


/**
 * This method validate the input details based on user access and privileges, and gets the connect summary report in excel
 * 
 * @param month
 * @param quarter
 * @param year
 * @param iou
 * @param geography
 * @param country
 * @param serviceLines
 * @param userId
 * @param fields
 * @param connectCategory 
 * @return
 * @throws Exception
 */
public InputStreamResource connectSummaryReport(String month, String quarter, String year, List<String> iou,
		String geography, List<String> country, List<String> serviceLines, String userId, List<String> fields,
		String connectCategory) throws Exception {
	
	logger.debug("Inside connectSummaryReport() method");
	
	UserT user = userService.findByUserId(userId);

	if (user != null) {
	
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		
		List<String> iouList = new ArrayList<String>();
		
		List<String> countryList = new ArrayList<String>();
		
		List<String> serviceLinesList = new ArrayList<String>();
		
		List<String> userIds = new ArrayList<String>();
		
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		
		List<Object[]> subSpCustomerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> subSpPartnerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> geographyCustomerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> geographyPartnerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> iouConnectCountList = new ArrayList<Object[]>();
		
		ExcelUtils.addItemToList(iou, iouList);
		
		ExcelUtils.addItemToList(serviceLines, serviceLinesList);
		
		ExcelUtils.addItemToList(country, countryList);

		String userGroup = user.getUserGroupMappingT().getUserGroup();
		
		String category = "";
		
		if (UserGroup.contains(userGroup)) {
		
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			
			case BDM:
			case PRACTICE_OWNER:
			case DELIVERY_MANAGER:
				userIds.add(userId);
				
				getConnectSummaryDetailsByUserIds(userIds, fromDate, toDate, subSpCustomerConnectCountList, subSpPartnerConnectCountList,geographyCustomerConnectCountList
						, geographyPartnerConnectCountList, iouConnectCountList, iouList, geography, countryList, serviceLinesList,connectCategory);
				break;
			
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
			case DELIVERY_CENTRE_HEAD:
			case DELIVERY_CLUSTER_HEAD:
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
				userIds.add(userId);
				
				getConnectSummaryDetailsByUserIds(userIds, fromDate, toDate, subSpCustomerConnectCountList, subSpPartnerConnectCountList,geographyCustomerConnectCountList, 
						geographyPartnerConnectCountList ,iouConnectCountList, iouList, geography, countryList, serviceLinesList,connectCategory);
				break;
			
			case GEO_HEADS:
			case IOU_HEADS:
			case PMO:
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
				userIds.add(userId);
				if(isCustomer(connectCategory)){
					subSpCustomerConnectCountList = getCustomerConnectSubSpSummaryDetails(userId, fromDate, toDate,geography,iouList,serviceLinesList,countryList,userIds);
					geographyCustomerConnectCountList = getCustomerConnectGeoSummaryDetails(userId, fromDate, toDate,geography,iouList,serviceLinesList,countryList,userIds);
					iouConnectCountList = getConnectIouSummaryDetails(userId, fromDate, toDate,geography,iouList,serviceLinesList,countryList,userIds);
				}
				
				if(isPartner(connectCategory)) {
					subSpPartnerConnectCountList = connectRepository.findSubSpPartnerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
							new Timestamp(toDate.getTime()), geography, countryList, serviceLinesList);
					geographyPartnerConnectCountList = connectRepository.findGeographyPartnerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
							new Timestamp(toDate.getTime()), geography, countryList, serviceLinesList);
				}
				break;

			default:
				
				getConnectSummaryDetailsForSI(fromDate, toDate, subSpCustomerConnectCountList, subSpPartnerConnectCountList,
						geographyCustomerConnectCountList, geographyPartnerConnectCountList,
						iouConnectCountList, iouList, geography, countryList, serviceLinesList,connectCategory);
				break;
			}
			
		} else {

			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
			
		}
		
		if (!subSpCustomerConnectCountList.isEmpty() || !subSpPartnerConnectCountList.isEmpty() || !(geographyCustomerConnectCountList.isEmpty()) || !(geographyPartnerConnectCountList.isEmpty()) || !(iouConnectCountList.isEmpty())) {
				
			connectDetailedReportService.getConnectTitlePage(workbook, geography, iou, serviceLines, user, country, month, quarter, year, ReportConstants.SUMMARY,connectCategory);
			
			connectSummaryReportService.getConnectSummaryExcelReport(subSpCustomerConnectCountList, subSpPartnerConnectCountList,
					geographyCustomerConnectCountList, geographyPartnerConnectCountList, iouConnectCountList, month, quarter, year, workbook,connectCategory);
			
		} else {
			if(!connectCategory.equals("All")){
				category=category+connectCategory+Constants.SPACE;
			}
			logger.error("NOT_FOUND: Report could not be downloaded, as no"+category+"connects are available for user selection and privilege combination");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no"+category+"connects are available for user selection and privilege combination");
		}
		
		InputStreamResource inputStreamResource = getInputStreamResource(workbook);
		
		return inputStreamResource;
	
	} else {
		logger.error("NOT_FOUND: User not found: {}", userId);
		throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
	}
}

/**
 * This method is used to get Geo Or Iou Heads Customer connects summary details by displaySubsp
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param geography
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @param userIds
 * @param connectCategory 
 * @return
 * @throws Exception
 */
private List<Object[]> getCustomerConnectSubSpSummaryDetails(String userId, Date fromDate, Date toDate, String displayGeography, List<String> iouList, List<String> serviceLinesList, List<String> countryList, List<String> userIds) throws Exception {
	
	String subSpQueryString = getConnectSubSpSummaryQueryString(userId, fromDate, toDate,displayGeography,iouList,serviceLinesList,countryList,userIds);
	logger.info("SUBSP Query string: {}", subSpQueryString);
	// Execute the native revenue query string
	Query connectSubSpSummaryReportQuery = entityManager.createNativeQuery(subSpQueryString);
	
	connectSubSpSummaryReportQuery.setParameter("startDate",fromDate);
	connectSubSpSummaryReportQuery.setParameter("endDate",toDate);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		connectSubSpSummaryReportQuery.setParameter("displayGeography", displayGeography);
	}
	if(!iouList.contains("") && iouList!=null){
		connectSubSpSummaryReportQuery.setParameter("iouList", iouList);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		connectSubSpSummaryReportQuery.setParameter("serviceLinesList", serviceLinesList);
	}
	if(!countryList.contains("") && countryList!=null){
		connectSubSpSummaryReportQuery.setParameter("countryList", countryList);
	}
	connectSubSpSummaryReportQuery.setParameter("userIds",userIds);
	
	return connectSubSpSummaryReportQuery.getResultList();
}

/**
 * This method is used to get Geo Or Iou Heads Customer connects summary details by displayGeography
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param geography
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @param userIds
 * @return
 * @throws Exception
 */
private List<Object[]> getCustomerConnectGeoSummaryDetails(String userId, Date fromDate, Date toDate, String displayGeography, List<String> iouList, List<String> serviceLinesList, List<String> countryList, List<String> userIds) throws Exception {
	// Form the native top revenue query string
	String geoQueryString = getConnectGeoSummaryQueryString(userId, fromDate, toDate,displayGeography,iouList,serviceLinesList,countryList,userIds);
	// Execute the native revenue query string
	Query connectGeoSummaryReportQuery = entityManager.createNativeQuery(geoQueryString);
	connectGeoSummaryReportQuery.setParameter("startDate",fromDate);
	connectGeoSummaryReportQuery.setParameter("endDate",toDate);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		connectGeoSummaryReportQuery.setParameter("displayGeography", displayGeography);
	}
	if(!iouList.contains("") && iouList!=null){
		connectGeoSummaryReportQuery.setParameter("iouList", iouList);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		connectGeoSummaryReportQuery.setParameter("serviceLinesList", serviceLinesList);
	}
	if(!countryList.contains("") && countryList!=null){
		connectGeoSummaryReportQuery.setParameter("countryList", countryList);
	}
	connectGeoSummaryReportQuery.setParameter("userIds",userIds);
	
	return connectGeoSummaryReportQuery.getResultList();
}

/**
 * This method is used to get Geo Or Iou Heads Customer connects summary details by displayIou
 * 
 * @param userId
 * @param fromDate
 * @param toDate
 * @param geography
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @param userIds
 * @return
 * @throws Exception
 */
private List<Object[]> getConnectIouSummaryDetails(String userId,
		Date fromDate, Date toDate, String displayGeography, List<String> iouList, List<String> serviceLinesList, List<String> countryList, List<String> userIds) throws Exception {
	String iouQueryString = getConnectIouSummaryQueryString(userId, fromDate, toDate,displayGeography,iouList,serviceLinesList,countryList,userIds);
	// Execute the native revenue query string
	Query connectIouSummaryReportQuery = entityManager.createNativeQuery(iouQueryString);
	connectIouSummaryReportQuery.setParameter("startDate",fromDate);
	connectIouSummaryReportQuery.setParameter("endDate",toDate);
	
	if(!displayGeography.equals("") && displayGeography!=null){
		connectIouSummaryReportQuery.setParameter("displayGeography", displayGeography);
	}
	if(!iouList.contains("") && iouList!=null){
		connectIouSummaryReportQuery.setParameter("iouList", iouList);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		connectIouSummaryReportQuery.setParameter("serviceLinesList", serviceLinesList);
	}
	if(!countryList.contains("") && countryList!=null){
		connectIouSummaryReportQuery.setParameter("countryList", countryList);
	}
	connectIouSummaryReportQuery.setParameter("userIds",userIds);
	
	return connectIouSummaryReportQuery.getResultList();
}


/**
 * This Method is used to get connect summary details for BDM and BDM Supervisor
 * 
 * @param userIds
 * @param fromDate
 * @param toDate
 * @param subSpCustomerConnectCountList
 * @param subSpPartnerConnectCountList
 * @param geographyCustomerConnectCountList
 * @param geographyPartnerConnectCountList
 * @param iouConnectCountList
 * @param iouList
 * @param displayGeography
 * @param countryList
 * @param serviceLinesList
 * @param connectCategory
 */
public void getConnectSummaryDetailsByUserIds(List<String> userIds,
		Date fromDate, Date toDate, List<Object[]> subSpCustomerConnectCountList,List<Object[]> subSpPartnerConnectCountList,
		List<Object[]> geographyCustomerConnectCountList, List<Object[]> geographyPartnerConnectCountList, List<Object[]> iouConnectCountList, 
		  List<String> iouList, String displayGeography, List<String> countryList, List<String> serviceLinesList, String connectCategory) {
	logger.info("Inside getConnectSummaryDetailsByUserIds() method");
	
	if(isCustomer(connectCategory)){
		
		subSpCustomerConnectCountList.addAll(connectRepository.findBySubSpCustomerConnectSummaryDetails(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), userIds, iouList, displayGeography, countryList, serviceLinesList));
		
		geographyCustomerConnectCountList.addAll(connectRepository.findByGeographyCustomerConnectSummaryDetails(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), userIds, iouList, displayGeography, countryList, serviceLinesList));
		
		iouConnectCountList.addAll(connectRepository.findByIouConnectSummaryReport(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), userIds, iouList, displayGeography, countryList, serviceLinesList));
	}
	
	if(isPartner(connectCategory)){

		subSpPartnerConnectCountList.addAll(connectRepository.findBySubSpPartnerConnectSummaryDetails(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), userIds, displayGeography, countryList, serviceLinesList));
	
		geographyPartnerConnectCountList.addAll(connectRepository.findByGeographyPartnerConnectSummaryDetails(new Timestamp(fromDate.getTime()),
			new Timestamp(toDate.getTime()), userIds, displayGeography, countryList, serviceLinesList));
	}
	
}

/**
 *  This Method is used to get connect summary details for SI
 *  
 * @param fromDate
 * @param toDate
 * @param subSpCustomerConnectCountList
 * @param subSpPartnerConnectCountList
 * @param geographyCustomerConnectCountList
 * @param geographyPartnerConnectCountList
 * @param iouConnectCountList
 * @param iouList
 * @param displayGeography
 * @param countryList
 * @param serviceLinesList
 * @param connectCategory
 */
private void getConnectSummaryDetailsForSI(Date fromDate, Date toDate, List<Object[]> subSpCustomerConnectCountList,
		List<Object[]> subSpPartnerConnectCountList, List<Object[]> geographyCustomerConnectCountList,
		List<Object[]> geographyPartnerConnectCountList, List<Object[]> iouConnectCountList, List<String> iouList,
		String displayGeography, List<String> countryList, List<String> serviceLinesList, String connectCategory) {
	logger.info("Inside getConnectSummaryDetailsForSI() method");
	
	if(isCustomer(connectCategory)){

		subSpCustomerConnectCountList.addAll(connectRepository.findSubSpCustomerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), iouList, displayGeography, countryList, serviceLinesList));
		
		geographyCustomerConnectCountList.addAll(connectRepository.findGeographyCustomerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), iouList, displayGeography, countryList, serviceLinesList));
		
		iouConnectCountList.addAll(connectRepository.findIouConnectsSummaryReport(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), iouList, displayGeography, countryList, serviceLinesList));
	}
	
	if(isPartner(connectCategory)){

		subSpPartnerConnectCountList.addAll(connectRepository.findSubSpPartnerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), displayGeography, countryList, serviceLinesList));
	
		geographyPartnerConnectCountList.addAll(connectRepository.findGeographyPartnerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
			new Timestamp(toDate.getTime()), displayGeography, countryList, serviceLinesList));
	}
	
}


/**
 * This Method is used to get both connect detailed and summary report
 * 
 * @param month
 * @param quarter
 * @param year
 * @param iou
 * @param displayGeography
 * @param country
 * @param serviceLines
 * @param userId
 * @param fields
 * @param connectCategory
 * @return
 * @throws Exception
 */
public InputStreamResource getConnectDetailedAndSummaryReports(String month, String quarter, String year, List<String> iou, String displayGeography,
		List<String> country, List<String> serviceLines, String userId, List<String> fields, String connectCategory) throws Exception {
	logger.debug("Inside getConnectReports() method");
	
	UserT user = userService.findByUserId(userId);
	
	if (user != null) {
		
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);

		List<String> iouList = new ArrayList<String>();

		List<String> serviceLinesList = new ArrayList<String>();

		List<String> countryList = new ArrayList<String>();

		List<String> userIds = new ArrayList<String>();
		
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		
		List<Object[]> subSpCustomerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> subSpPartnerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> geographyCustomerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> geographyPartnerConnectCountList = new ArrayList<Object[]>();
		
		List<Object[]> iouConnectCountList = new ArrayList<Object[]>();
		
		List<String> connectIdList = new ArrayList<String>();
	
		ExcelUtils.addItemToList(iou, iouList);
		
		ExcelUtils.addItemToList(serviceLines, serviceLinesList);
		
		ExcelUtils.addItemToList(country, countryList);
		
		String userGroup = user.getUserGroupMappingT().getUserGroup();
		
		String category="";

		if (UserGroup.contains(userGroup)) {

			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case PRACTICE_OWNER:
			case DELIVERY_MANAGER:
				userIds.add(userId);
				
				connectIdList = getConnectDetailsByUserIds(fromDate,toDate,userIds,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				
				getConnectSummaryDetailsByUserIds(userIds, fromDate, toDate, subSpCustomerConnectCountList, subSpPartnerConnectCountList,geographyCustomerConnectCountList
						, geographyPartnerConnectCountList, iouConnectCountList, iouList, displayGeography, countryList, serviceLinesList,connectCategory);
				break;
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
			case DELIVERY_CENTRE_HEAD:
			case DELIVERY_CLUSTER_HEAD:
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
				userIds.add(userId);
				
				connectIdList = getConnectDetailsByUserIds(fromDate,toDate,userIds,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				
				getConnectSummaryDetailsByUserIds(userIds, fromDate, toDate, subSpCustomerConnectCountList, subSpPartnerConnectCountList,geographyCustomerConnectCountList, 
						geographyPartnerConnectCountList ,iouConnectCountList, iouList, displayGeography, countryList, serviceLinesList,connectCategory);
				break;
			case GEO_HEADS:
			case IOU_HEADS:
			case PMO:
				userIds = userRepository.getAllSubordinatesIdBySupervisorId(userId);
				userIds.add(userId);
				
				connectIdList = getGeoHeadOrIouHeadConnectDetails(fromDate,toDate,userId,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				
				if(isCustomer(connectCategory)){
					subSpCustomerConnectCountList = getCustomerConnectSubSpSummaryDetails(userId, fromDate, toDate,displayGeography,iouList,serviceLinesList,countryList,userIds);
					geographyCustomerConnectCountList = getCustomerConnectGeoSummaryDetails(userId, fromDate, toDate,displayGeography,iouList,serviceLinesList,countryList,userIds);
					iouConnectCountList = getConnectIouSummaryDetails(userId, fromDate, toDate,displayGeography,iouList,serviceLinesList,countryList,userIds);
				}
				
				if(isPartner(connectCategory)) {
					subSpPartnerConnectCountList = connectRepository.findSubSpPartnerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
							new Timestamp(toDate.getTime()), displayGeography, countryList, serviceLinesList);
					geographyPartnerConnectCountList = connectRepository.findGeographyPartnerConnectsSummaryDetails(new Timestamp(fromDate.getTime()),
							new Timestamp(toDate.getTime()), displayGeography, countryList, serviceLinesList);
				}
				break;
			default:
				
				connectIdList = getConnectIdsForStratagicInitiative(fromDate,toDate,userId,iouList,displayGeography,countryList,serviceLinesList,connectCategory);
				
				getConnectSummaryDetailsForSI(fromDate, toDate, subSpCustomerConnectCountList, subSpPartnerConnectCountList,
						geographyCustomerConnectCountList, geographyPartnerConnectCountList,
						iouConnectCountList, iouList, displayGeography, countryList, serviceLinesList,connectCategory);
				break;
			}
		} else {
			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
		}
		if (!connectIdList.isEmpty()) {
			
			connectDetailedReportService.getConnectTitlePage(workbook, displayGeography, iou, serviceLines, user, country, month, quarter, year, "Summary, Detailed",connectCategory);
			
			connectSummaryReportService.getConnectSummaryExcelReport(subSpCustomerConnectCountList, subSpPartnerConnectCountList,
					geographyCustomerConnectCountList, geographyPartnerConnectCountList, iouConnectCountList, month,
					quarter, year, workbook, connectCategory);
			connectDetailedReportService.setConnectDetailedReport(connectIdList, fields, workbook,connectCategory);
			
		} else {
			if(!connectCategory.equals("All")){
				category=category+connectCategory+Constants.SPACE;
			}
			logger.error("NOT_FOUND: Report could not be downloaded, as no connects are available for user selection and privilege combination");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no "+category+" connects are available for user selection and privilege combination");
		}
		InputStreamResource inputStreamResource = getInputStreamResource(workbook);
	
		return inputStreamResource;
	
	} else {
		logger.error("NOT_FOUND: User not found: {}", userId);
		throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
	}
}

/**
 * This Method is used to get bid detailed report
 * 
 * @param year
 * @param fromMonth
 * @param toMonth
 * @param bidOwner
 * @param currency
 * @param iou
 * @param geography
 * @param country
 * @param serviceLines
 * @param userId
 * @param fields
 * @return
 * @throws Exception
 */
public InputStreamResource getBidReport(String year, String fromMonth,
		String toMonth, List<String> bidOwner, List<String> currency,
		List<String> iou, List<String> geography, List<String> country,
		List<String> serviceLines, String userId, List<String> fields)
		throws Exception {
	logger.info("Inside getBidDetailedReport Service");
	SXSSFWorkbook workbook = new SXSSFWorkbook(50);
	List<String> geographyList = new ArrayList<String>();
	List<String> iouList = new ArrayList<String>();
	List<String> serviceLinesList = new ArrayList<String>();
	List<String> countryList = new ArrayList<String>();
	Date startDate = null;
	Date endDate = null;
	String tillDate = DateUtils.getCurrentDate();
	List<BidDetailsT> bidDetailsList =new ArrayList<BidDetailsT>();
	List<BidDetailsT> bidDetails = new ArrayList<BidDetailsT>();
	
	startDate = DateUtils.getDateFromGivenAndCurrentFinancialYear(fromMonth, year, true);
	endDate = DateUtils.getDateFromGivenAndCurrentFinancialYear(toMonth, year, false);
	
	UserT user = userService.findByUserId(userId);
	if (user == null) {
		logger.error("NOT_FOUND: User not found: {}", userId);
		throw new DestinationException(HttpStatus.NOT_FOUND, "User not found: " + userId);
	} else {
		String userGroup = user.getUserGroupMappingT().getUserGroup();
		if (UserGroup.contains(userGroup)) {
		// Validate user group, BDM's & BDM supervisor's are not
		// authorized for this service
		switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
		case BDM:
		case BDM_SUPERVISOR:
		case PRACTICE_OWNER:
		case PRACTICE_HEAD:
		case REPORTING_TEAM:
			logger.error("User is not authorized to access this service");
			throw new DestinationException(HttpStatus.UNAUTHORIZED,	"User is not authorised to access this service");
		default:
			addEmptyItemToListIfGeo(geography, geographyList);
			ExcelUtils.addItemToList(iou, iouList);
			ExcelUtils.addItemToList(serviceLines, serviceLinesList);
			ExcelUtils.addItemToList(country, countryList);
			bidDetails = getBidDetailsBasedOnUserPrivileges(startDate, endDate, userId, bidOwner, geographyList, iouList, serviceLinesList, countryList);
			
			bidDetailsList = beaconConverterService.convertBidDetailsCurrency(bidDetails, currency);
			if (bidDetailsList == null || bidDetailsList.isEmpty()) {
				logger.error("NOT_FOUND: Report could not be downloaded, as no bids are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no bids are available for user selection and privilege combination");
			}
		}
		} else {
			logger.error("Invalid User Group: {}", userGroup);
			throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
		}
	}
	buildBidReportService.getBidReportTitlePage(workbook, geography, iou,
			serviceLines, userId, tillDate, country, currency, fromMonth, toMonth, "Detailed", year);
	
	return buildBidReportService.getBidDetailsReport(bidDetailsList, fields,
			currency, workbook);
}

/**
 * This Method is used to get list of BidDetailsT object based on privileges and fileter applied
 * 
 * @param startDate
 * @param endDate
 * @param userId
 * @param bidOwner
 * @param geographyList
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @return
 * @throws Exception
 */
private List<BidDetailsT> getBidDetailsBasedOnUserPrivileges(Date startDate, Date endDate, String userId, List<String> bidOwner,
		List<String> geographyList, List<String> iouList, List<String> serviceLinesList, List<String> countryList) throws Exception {
	logger.debug("Inside getBidDetailsBasedOnUserPrivileges() method");
	// Form the native top revenue query string
	String queryString = getBidDetailedQueryString(userId, startDate, endDate, bidOwner, geographyList, iouList, serviceLinesList, countryList);
//		logger.info("Query string: {}", queryString);
	// Execute the native revenue query string
	Query bidDetailedReportQuery = entityManager.createNativeQuery(queryString);
	List<String> resultList = bidDetailedReportQuery.getResultList();
	// Retrieve connect details
	List<BidDetailsT> bidDetailsList = null;
	if ((resultList != null) && !(resultList.isEmpty())) {
		bidDetailsList = bidDetailsTRepository.findByBidId(resultList);
	}
	if (bidDetailsList == null || bidDetailsList.isEmpty()) {
		logger.error("NOT_FOUND: Report could not be downloaded, as no bids are available for user selection and privilege combination");
		throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no bids are available for user selection and privilege combination");
	}
	return bidDetailsList;
}

/**
 * This Method used to form bid details query based on user access priviledges
 * 
 * @param userId
 * @param startDate
 * @param endDate
 * @param bidOwner
 * @param geographyList
 * @param iouList
 * @param serviceLinesList
 * @param countryList
 * @return
 * @throws Exception
 */
private String getBidDetailedQueryString(String userId, Date startDate, Date endDate, List<String> bidOwner, List<String> geographyList,
		List<String> iouList, List<String> serviceLinesList, List<String> countryList) throws Exception {
	logger.debug("Inside getRevenueQueryString() method");
	StringBuffer queryBuffer = new StringBuffer(BID_REPORT_QUERY_PREFIX);
	// Get user access privilege groups
	HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
			.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, null);
	// Get WHERE clause string
	queryBuffer.append(BID_START_DATE_COND_PREFIX
			+ new Timestamp(startDate.getTime()) + Constants.SINGLE_QUOTE);
	queryBuffer.append(BID_END_DATE_COND_C_PREFIX
			+ new Timestamp(endDate.getTime()) + Constants.SINGLE_QUOTE);
	if (bidOwner.isEmpty() || bidOwner.size() == 0) {
		queryBuffer.append(Constants.AND_CLAUSE	+ BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX + "''" + ")"
				+ Constants.OR_CLAUSE + "('')" + " in" + "(''))");
	} else {
		String bidOwners = ExcelUtils.getStringListWithSingleQuotes(bidOwner);
		queryBuffer.append(Constants.AND_CLAUSE
				+ BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX + bidOwners + ")"+ Constants.OR_CLAUSE + "('') in (" + bidOwners + "))");
	}
	if(!geographyList.contains("") && geographyList!=null){
		queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(geographyList)+ Constants.RIGHT_PARANTHESIS);
	}
	if(!iouList.contains("") && iouList!=null){
		queryBuffer.append(Constants.AND_CLAUSE + IOU_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(iouList)+ Constants.RIGHT_PARANTHESIS);
	}
	if(!serviceLinesList.contains("") && serviceLinesList!=null){
		queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(serviceLinesList)+ Constants.RIGHT_PARANTHESIS);
	}
	if(!countryList.contains("") && countryList!=null){
		queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(countryList)+ Constants.RIGHT_PARANTHESIS);
		queryBuffer.append(Constants.AND_CLAUSE + OPPORTUNITY_COUNTRY_COND_PREFIX +ExcelUtils.getStringListWithSingleQuotes(countryList)+ Constants.RIGHT_PARANTHESIS);
	}
	String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
	if (whereClause != null && !whereClause.isEmpty()) {
		queryBuffer.append(Constants.AND_CLAUSE + whereClause);
	}
	return queryBuffer.toString();
}



// For Detailed Report

	public String getOpportunityDetailedQueryString(String userId, Date fromDate,
			Date toDate, List<Integer> salesStage)throws Exception {
		logger.debug("Inside getOpportunityDetailedQueryString() method" );
		StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_DETAILED_QUERY_PREFIX);
		// Get user access privilege groups 
		HashMap<String, String> queryPrefixMap = 
					userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
							OPPORTUNITY_IOU_COND_PREFIX, null);
		// Get WHERE clause string
		queryBuffer.append(Constants.LEFT_PARANTHESIS+OPPORTUNITY_SALES_STAGE_CODE_COND_PREFIX );
		queryBuffer.append(OPPORTUNITY_START_DATE_COND_PREFIX  + fromDate + Constants.SINGLE_QUOTE);
		queryBuffer.append(OPPORTUNITY_END_DATE_COND_PREFIX  + toDate + Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) { 
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(Constants.SPACE+ Constants.AND_CLAUSE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
		return queryBuffer.toString();
	}
	
	
		/**
		 *  This method is used to form the opportunity win loss query for the service line
		 *  
		 * @param userId
		 * @param fromDate
		 * @param toDate
		 * @param salesStage
		 * @return
		 * @throws Exception
		 */
		public String getOpportunityServiceLineSummaryQueryString(String userId, Date fromDate,
				Date toDate, Integer salesStage)throws Exception {
			logger.debug("Inside getOpportunityServiceLineSummaryQueryString() method" );
			
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_SUMMARY_SUBSP_QUERY_PREFIX);
				// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
								OPPORTUNITY_IOU_COND_PREFIX, null);
			// Get WHERE clause string
			queryBuffer.append(Constants.LEFT_PARANTHESIS+OPPORTUNITY_SALES_STAGE_CODE_COND_PREFIX );
			queryBuffer.append(OPPORTUNITY_START_DATE_COND_PREFIX  + fromDate + Constants.SINGLE_QUOTE);
			queryBuffer.append(OPPORTUNITY_END_DATE_COND_PREFIX  + toDate + Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
			String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
			
			if (whereClause != null && !whereClause.isEmpty()) { 
				queryBuffer.append(Constants.AND_CLAUSE + whereClause);
			}
			queryBuffer.append(Constants.SPACE+ Constants.AND_CLAUSE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
			queryBuffer.append(Constants.SPACE+ OPPORTUNITY_SUBSP_GROUP_BY_COND_PREFIX);
			return queryBuffer.toString();
		}
	
	
		/**
		 * This method is used to form the opportunity win loss query for the geography
		 * 
		 * @param userId
		 * @param fromDate
		 * @param toDate
		 * @param salesStage
		 * @return
		 * @throws Exception
		 */
		public String getOpportunityGeoSummaryQueryString(String userId, Date fromDate,
				Date toDate, Integer salesStage)throws Exception {
			logger.debug("Inside getOpportunityGeoSummaryQueryString() method" );
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_SUMMARY_GEO_QUERY_PREFIX);
				// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = 
						userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
								OPPORTUNITY_IOU_COND_PREFIX, null);
				// Get WHERE clause string
			queryBuffer.append(Constants.LEFT_PARANTHESIS+OPPORTUNITY_SALES_STAGE_CODE_COND_PREFIX );
			queryBuffer.append(OPPORTUNITY_START_DATE_COND_PREFIX  + fromDate + Constants.SINGLE_QUOTE);
			queryBuffer.append(OPPORTUNITY_END_DATE_COND_PREFIX  + toDate + Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
				String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
				if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
				queryBuffer.append(Constants.SPACE+ Constants.AND_CLAUSE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
				queryBuffer.append(Constants.SPACE+ OPPORTUNITY_GEO_GROUP_BY_COND_PREFIX);
				return queryBuffer.toString();
		}
	
		/**
		 * This method is used to form the opportunity win loss query for the iou
		 * 
		 * @param userId
		 * @param fromDate
		 * @param toDate
		 * @param salesStage
		 * @return
		 * @throws Exception
		 */
		public String getOpportunityIouSummaryQueryString(String userId, Date fromDate,
				Date toDate, Integer salesStage)throws Exception {
			logger.debug("Inside getOpportunityIouSummaryQueryString() method" );
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_SUMMARY_IOU_QUERY_PREFIX);
				// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = 
						userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
								OPPORTUNITY_IOU_COND_PREFIX, null);
				// Get WHERE clause string
			queryBuffer.append(Constants.LEFT_PARANTHESIS+OPPORTUNITY_SALES_STAGE_CODE_COND_PREFIX );
			queryBuffer.append(OPPORTUNITY_START_DATE_COND_PREFIX  + fromDate + Constants.SINGLE_QUOTE);
			queryBuffer.append(OPPORTUNITY_END_DATE_COND_PREFIX  + toDate + Constants.SINGLE_QUOTE + Constants.RIGHT_PARANTHESIS);
				String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
				if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
				queryBuffer.append(Constants.SPACE+ Constants.AND_CLAUSE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
				queryBuffer.append(Constants.SPACE+ OPPORTUNITY_IOU_GROUP_BY_COND_PREFIX);
				return queryBuffer.toString();
		}
		
	 
		/**
		 * This method is used to form the opportunity Anticipating or Pipeline query for the Geography
		 * 
		 * @param userId
		 * @param salesStage
		 * @return
		 * @throws Exception
		 */
		public String getPipelineAnticipatingOppGeoSummaryQueryString(String userId, Integer salesStage)throws Exception {
			logger.debug("Inside getPipelineAnticipatingOppGeoSummaryQueryString() method" );
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_PIPELINE_PROSPECTS_GEOGRAPHY_QUERY_PREFIX);
				// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = 
						userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
								OPPORTUNITY_IOU_COND_PREFIX, null);
				// Get WHERE clause string
			queryBuffer.append(Constants.SPACE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
			String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
			if (whereClause != null && !whereClause.isEmpty()) { 
				queryBuffer.append(Constants.AND_CLAUSE + whereClause);
			}
			queryBuffer.append(Constants.SPACE+ OPPORTUNITY_GEO_GROUP_BY_COND_PREFIX + Constants.COMMA + OPPORTUNITY_SALES_STAGE_COND);
			return queryBuffer.toString();
		}
		
		//
		
		// Anticipating or Pipeline Iou
		
		public String getPipelineAnticipatingOppIouSummaryQueryString(String userId, Integer salesStage)throws Exception {
			logger.debug("Inside getPipelineAnticipatingOppIouSummaryQueryString() method" );
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_PIPELINE_PROSPECTS_IOU_QUERY_PREFIX);
				// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = 
						userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
								OPPORTUNITY_IOU_COND_PREFIX, null);
			queryBuffer.append(Constants.SPACE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
				// Get WHERE clause string
				String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
				if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
				queryBuffer.append(Constants.SPACE+ OPPORTUNITY_IOU_GROUP_BY_COND_PREFIX + Constants.COMMA + OPPORTUNITY_SALES_STAGE_COND);
				return queryBuffer.toString();
		}
		
		
		
		// Anticipating or Pipeline Service Lines
		
		public String getPipelineAnticipatingOppServiceLineSummaryQueryString(String userId, List<Integer> salesStage)throws Exception {
			logger.debug("Inside getPipelineAnticipatingOppServiceLineSummaryQueryString() method" );
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_PIPELINE_PROSPECTS_SERVICELINES_QUERY_PREFIX);
				// Get user access privilege groups 
			HashMap<String, String> queryPrefixMap = 
						userAccessPrivilegeQueryBuilder.getQueryPrefixMap(OPPORTUNITY_GEO_COND_PREFIX, OPPORTUNITY_SUBSP_COND_PREFIX, 
								OPPORTUNITY_IOU_COND_PREFIX, null);
			queryBuffer.append(Constants.SPACE+ OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
				// Get WHERE clause string
				String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
				if (whereClause != null && !whereClause.isEmpty()) { 
					queryBuffer.append(Constants.AND_CLAUSE + whereClause);
				}
				queryBuffer.append(Constants.SPACE+ OPPORTUNITY_SUBSP_GROUP_BY_COND_PREFIX);
				return queryBuffer.toString();
		}
		
		

		/**
		 * This method is used to get opportunity summary report
		 * 
		 * @param month
		 * @param year
		 * @param quarter
		 * @param geography
		 * @param country
		 * @param iou
		 * @param currency
		 * @param serviceLines
		 * @param salesStage
		 * @param userId
		 * @return
		 * @throws Exception
		 */
		public InputStreamResource getOpportunitySummaryReport(String month,
				String year, String quarter, List<String> geography,
				List<String> country, List<String> iou, List<String> currency,
				List<String> serviceLines, List<Integer> salesStage, String userId) throws Exception {
			
			if (year.isEmpty() && month.isEmpty() && quarter.isEmpty()) {
				year = DateUtils.getCurrentFinancialYear();
			}
			
			List<Integer> salesStageCodeList=new ArrayList<Integer>();
			removeUnwantedSalesStageCodes(salesStage, salesStageCodeList);
			
			SXSSFWorkbook workbook = new SXSSFWorkbook(50);
			String tillDate=DateUtils.getCurrentDate();
			buildOpportunityReportService.getTitleSheet(workbook,geography,iou,serviceLines,salesStage,userId,tillDate, country, "Summary", month, quarter, year, currency);
			getOpportunitySummaryReportExcel(month, year, quarter, geography, country, iou, currency, serviceLines, salesStageCodeList, userId,workbook);
			ExcelUtils.arrangeSheetOrder(workbook);
			InputStreamResource inputStreamResource = getInputStreamResource(workbook);
			return inputStreamResource;
		}
		

		/**
		 * This method is used to get opportunity detailed report
		 * 
		 * @param month
		 * @param quarter
		 * @param year
		 * @param geography
		 * @param country
		 * @param iou
		 * @param serviceLines
		 * @param salesStage
		 * @param currency
		 * @param userId
		 * @param fields
		 * @param toDate
		 * @return
		 * @throws Exception
		 */
		public InputStreamResource getOpportunitiesWith(String month, String quarter,
				String year, List<String> geography, List<String> country,
				List<String> iou, List<String> serviceLines,
				List<Integer> salesStage, List<String> currency, String userId,
				List<String> fields, String toDate) throws Exception {
			
			if (year.isEmpty() && month.isEmpty() && quarter.isEmpty()) {
				year = DateUtils.getCurrentFinancialYear();
			}
			SXSSFWorkbook workbook = new SXSSFWorkbook(50);
			buildOpportunityReportService.getTitleSheet(workbook,geography,iou,serviceLines,salesStage,userId,toDate, country,"Detailed", month, quarter, year, currency);
			buildOpportunityReportService.getOpportunities(month, quarter,year, geography, country,iou, serviceLines, salesStage, currency, userId,fields,workbook);
			ExcelUtils.arrangeSheetOrder(workbook);
			InputStreamResource inputStreamResource = getInputStreamResource(workbook);
			return inputStreamResource;
		}
		

		/**
		 * This method is used to get both opportunity summary and detailed report
		 * 
		 * @param month
		 * @param year
		 * @param quarter
		 * @param geography
		 * @param country
		 * @param iou
		 * @param currency
		 * @param serviceLines
		 * @param salesStage
		 * @param userId
		 * @param fields
		 * @return
		 * @throws Exception
		 */
		public InputStreamResource getOpportunityBothReport(String month,
				String year, String quarter, List<String> geography,
				List<String> country, List<String> iou, List<String> currency,
				List<String> serviceLines, List<Integer> salesStage, String userId, List<String> fields) throws Exception{
			
			if (year.isEmpty() && month.isEmpty() && quarter.isEmpty()) {
				year = DateUtils.getCurrentFinancialYear();
			}
			List<Integer> salesStageCodeList=new ArrayList<Integer>();
			removeUnwantedSalesStageCodes(salesStage, salesStageCodeList);
			
			String fyear=new String(year);
			String fquarter=new String(quarter);
			String fmonth=new String(month);
			SXSSFWorkbook workbook = new SXSSFWorkbook(50);
			String tillDate = DateUtils.getCurrentDate();
			buildOpportunityReportService.getTitleSheet(workbook,geography,iou,serviceLines,salesStage,userId,tillDate, country, "Summary, Detailed", month, quarter, year, currency);
			getOpportunitySummaryReportExcel(month, year, quarter, geography, country, iou, currency, serviceLines, salesStageCodeList, userId,workbook);
			buildOpportunityReportService.getOpportunities(fmonth, fquarter,fyear, geography, country,iou, serviceLines, salesStage, currency, userId,fields,workbook);
			ExcelUtils.arrangeSheetOrder(workbook);
			InputStreamResource inputStreamResource = getInputStreamResource(workbook);
			return inputStreamResource;
		}
		
		/**
		 * This method is used to remove sales stage code 11, 12 and 13
		 * 
		 */
		public void removeUnwantedSalesStageCodes(List<Integer> salesStageList, List<Integer> salesStageCodeList) {
			for(Integer salesStage:salesStageList){
				if(!salesStage.equals(11) && !salesStage.equals(12) && !salesStage.equals(13)){
					salesStageCodeList.add(salesStage);
				}
			}
		}

		/**
		 * This method is used to set opportunity summary details to spreadSheet
		 * 
		 * @param month
		 * @param year
		 * @param quarter
		 * @param geography
		 * @param country
		 * @param iou
		 * @param currency
		 * @param serviceLines
		 * @param salesStageList
		 * @param userId
		 * @param workbook
		 * @throws DestinationException
		 * @throws Exception
		 */
		public void getOpportunitySummaryReportExcel(
				String month, String year, String quarter, List<String> geography,
				List<String> country, List<String> iou, List<String> currency,
				List<String> serviceLines, List<Integer> salesStageList,
				String userId, SXSSFWorkbook workbook) throws DestinationException, Exception {
			logger.debug("Inside Report Service getReportSummaryOpportunities method");
			
			Boolean isDistinctIou = true;
			List<Object[]> opportunityList = new ArrayList<Object[]>();
			List<Integer> pipilineAntiSalesStageList = new ArrayList<Integer>(salesStageList);
			List<ReportSummaryOpportunity> pipelineAntiIou = new ArrayList<ReportSummaryOpportunity>();
			List<ReportSummaryOpportunity> pipelineAnticipatingGeography = new ArrayList<ReportSummaryOpportunity>();
			List<ReportSummaryOpportunity> reportSummaryOpportunities = new ArrayList<ReportSummaryOpportunity>();
			Map<String, List<ReportSummaryOpportunity>> reportSummaryOppMap = new TreeMap<String, List<ReportSummaryOpportunity>>();
			List<String> userIds = new ArrayList<String>();
			List<String> geoList = new ArrayList<String>();
			List<String> iouList = new ArrayList<String>();
			List<String> countryList = new ArrayList<String>();
			List<String> serviceLinesList = new ArrayList<String>();
			addItemToListGeo(geography,geoList);
			ExcelUtils.addItemToList(iou,iouList);
			ExcelUtils.addItemToList(country,countryList);
			ExcelUtils.addItemToList(serviceLines,serviceLinesList);
			// ADD USERIDS HERE ITSELF
			UserT user = userRepository.findByUserId(userId);
			if(user == null){
				logger.error("User Id Not Found "+ userId );
				throw new DestinationException(HttpStatus.NOT_FOUND,"User Id Not Found");
			}
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
			case PRACTICE_OWNER:
			case DELIVERY_MANAGER:
				userIds.add(userId);
				break;
			case BDM_SUPERVISOR:
			case PRACTICE_HEAD:
			case DELIVERY_CLUSTER_HEAD:
			case DELIVERY_CENTRE_HEAD:
				List<String> subOrdinatesList =userRepository.getAllSubordinatesIdBySupervisorId(userId);
				userIds.addAll(subOrdinatesList);
				userIds.add(userId);
				break;
			}

			for (int i = 0; i <salesStageList.size();) {
				if (salesStageList.get(i) < 9) {
					switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
					case BDM:
					case PRACTICE_OWNER:
					case DELIVERY_MANAGER:
						opportunityList = opportunityRepository.findSummaryGeographyByRole(salesStageList.get(i), userIds, geoList, countryList, iouList, serviceLinesList);
						break;
					case BDM_SUPERVISOR:
					case PRACTICE_HEAD:
					case DELIVERY_CLUSTER_HEAD:
					case DELIVERY_CENTRE_HEAD:
						opportunityList = opportunityRepository.findSummaryGeographyByRole(salesStageList.get(i), userIds, geoList, countryList, iouList, serviceLinesList);
						break;
					default:
						if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
							String queryString = getPipelineAnticipatingOppGeoSummaryQueryString(userId,salesStageList.get(i));
							Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
							opportunityList = opportunitySummaryReportQuery.getResultList();
						} else {
							opportunityList = opportunityRepository.findSummaryGeography(geoList, countryList, iouList, serviceLinesList, 
									salesStageList.get(i));
						}
						break;
					}
					if (opportunityList.size() > 0) {
						pipelineAnticipatingGeography.addAll(buildOpportunityReportService.getPipelineAnticipatingOpportunities(
								opportunityList, salesStageList.get(i),false));
					}
					if (isDistinctIou) {
						switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
						case BDM:
						case PRACTICE_OWNER:
						case DELIVERY_MANAGER:
							opportunityList = opportunityRepository.findSummaryIouByRole(salesStageList.get(i), userIds, geoList, countryList, iouList, serviceLinesList);
							break;
						case BDM_SUPERVISOR:
						case PRACTICE_HEAD:
						case DELIVERY_CLUSTER_HEAD:
						case DELIVERY_CENTRE_HEAD:
							opportunityList = opportunityRepository.findSummaryIouByRole(salesStageList.get(i), userIds, geoList, countryList, iouList, serviceLinesList);
							break;
						default:
							if(geography.contains("All") && (iou.contains("All") && serviceLines.contains("All")) && country.contains("All")){
								String queryString = getPipelineAnticipatingOppIouSummaryQueryString(userId,salesStageList.get(i));
								Query opportunitySummaryReportQuery = entityManager.createNativeQuery(queryString);
								opportunityList = opportunitySummaryReportQuery.getResultList();
							} else {
								opportunityList = opportunityRepository.findSummaryIou(geoList, countryList, iouList, serviceLinesList, 
										salesStageList.get(i));
							}
							break;
						}
						if (opportunityList.size() > 0) {
							pipelineAntiIou.addAll(buildOpportunityReportService.getPipelineAnticipatingOpportunities(
								opportunityList, salesStageList.get(i),isDistinctIou));
						}
					}
					salesStageList.remove(salesStageList.get(i));
				} else {
					i++;
				}
			}
			
			List<ReportSummaryOpportunity> serviceLinesOpp = buildOpportunityReportService.getServiceLineForPipelineAnticipating(currency, 
					geography,country, iou, serviceLines, pipilineAntiSalesStageList,userIds, userId, userGroup);
			if(serviceLinesOpp.size() > 0){
			reportSummaryOppMap.put("pipelineAnticipatingServiceLine",serviceLinesOpp);
			}
			if (pipelineAnticipatingGeography.size() > 0) {
				reportSummaryOppMap.put("pipelineAnticipatingGeography",
						pipelineAnticipatingGeography);
			}
			if (pipelineAntiIou.size() > 0) {
				reportSummaryOppMap.put("pipelineAnticipatingIou", pipelineAntiIou);
			}
			
			reportSummaryOpportunities = buildOpportunityReportService.getWinLossOpportunities(month, year,
					quarter, geography, country, iou, serviceLines,
					salesStageList, userIds, userId, isDistinctIou, userGroup);
			if (reportSummaryOpportunities.size() > 0) {
				if (!month.isEmpty()) {
					reportSummaryOppMap
					.put("month", reportSummaryOpportunities);
				} else if (!quarter.isEmpty()) {
					reportSummaryOppMap
					.put(quarter, reportSummaryOpportunities);
				} else {
					reportSummaryOppMap.put(year, reportSummaryOpportunities);
				}
			}
			if (reportSummaryOppMap.size() > 0) {
				buildOpportunityReportService.buildExcelReport(reportSummaryOppMap,month,year,quarter,currency,geography,iou,workbook);
			}else{
				logger.error("Report could not be downloaded, as no opportunities are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND," Report could not be downloaded, as no opportunities are available for user selection and privilege combination");
			}
		}
		
		/**
		 * This method is used to add ("") to targetList if itemList is empty else geographies for the given display geography
		 * 
		 * @param itemList
		 * @param targetList
		 */
		public void addItemToListGeo(List<String> itemList, List<String> targetList){
			if(itemList.contains("All") || itemList.isEmpty()){
				targetList.add("");
				} else {
					targetList.addAll(geographyRepository.findByDisplayGeography(itemList));
				}
			}

}
