package com.tcs.destination.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
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
	
	private static final String CONNECT_COUNT_GEO_SUMMARY_QUERY_PREFIX = "select count(connect_count), display_geography  from (((select distinct(CON.connect_id) as connect_count,display_geography "; 
	private static final String CONNECT_SUMMARY_GEO_UNION_PARTNER_REPORT_QUERY_PREFIX =  " UNION select distinct(CON.connect_id) as connect_count, display_geography "; 
	private static final String CONNECT_SUMMARY_IOU_REPORT_QUERY_PREFIX = "select count(distinct(CON.connect_id)),display_iou ";

	private static final String CONNECT_SUMMARY_SUBSP_REPORT_QUERY_PREFIX =  "select count(connect_count), display_sub_sp  from (((select distinct(CON.connect_id) as connect_count,display_sub_sp "; 
	private static final String CONNECT_SUMMARY_SUBSP_UNION_PARTNER_REPORT_QUERY_PREFIX =  " UNION select distinct(CON.connect_id) as connect_count, display_sub_sp "; 
	private static final String CONNECT_JOIN_CUS_GEO_IOU_SUBSP = "from connect_t CON "
			+ "   JOIN customer_master_t CMT ON  CMT.customer_id=CON.customer_id"
			+ "   JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou  "
			+ "   JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography "
			+ "   JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography"
			+ "   left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id"
			+ "   left outer JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp"
			+ " where ";
	private static final String CONNECT_ID_PARTNER_UNION_REPORT_QUERY_PREFIX = 	" union select distinct CON.connect_id";	
	
	private static final String CONNECT_PARTNER_UNION_REPORT_QUERY_PREFIX = 
			" from connect_t CON   "
			+ "JOIN partner_master_t PAT ON  PAT.partner_id=CON.partner_id " 
			+ "JOIN geography_mapping_t GMT ON PAT.geography=GMT.geography  "  
			+ "JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography "   
			+ "left outer Join connect_sub_sp_link_t CSL ON CON.connect_id=CSL.connect_id "   
			+ "left outer JOIN sub_sp_mapping_t SSM ON CSL.sub_sp=SSM.sub_sp where ";
	
	private static final String BID_REPORT_QUERY_PREFIX = " select distinct BID.bid_id from bid_details_t BID "
			+ "	 JOIN bid_office_group_owner_link_t BIDGO ON BIDGO.bid_id=BID.bid_id"
			+ "  JOIN opportunity_t OPP ON BID.opportunity_id=OPP.opportunity_id"
			+ "  JOIN customer_master_t CMT ON  CMT.customer_id = OPP.customer_id"
			+ "  JOIN iou_customer_mapping_t ICMT on  CMT.iou = ICMT.iou"
			+ "  JOIN geography_mapping_t GMT on CMT.geography = GMT.geography"
			+ "  JOIN geography_country_mapping_t GCM ON GMT.geography = GCM.geography"
			+ "  left outer join opportunity_sub_sp_link_t OPSUBL ON OPP.opportunity_id = OPSUBL.opportunity_id"
			+ "  left outer JOIN sub_sp_mapping_t SSM ON OPSUBL.sub_sp = SSM.sub_sp "
			+ " where ";

	// TargetVsActual Detailed Report
	private static final String TARGET_VS_ACTUAL_PROJECTED_QUERY_PREFIX = "select RCMT.customer_name,PRDT.quarter,sum(PRDT.revenue) from projected_revenues_data_t PRDT "
			+ "JOIN revenue_customer_mapping_t RCMT on RCMT.finance_customer_name=PRDT.finance_customer_name "
			+ "and RCMT.customer_geography = PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou "
			+ "JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and ";

	private static final String TARGET_VS_ACTUAL_ACTUAL_QUERY_PREFIX = "select RCMT.customer_name,ARDT.quarter,sum(ARDT.revenue) from actual_revenues_data_t ARDT "
			+ "JOIN revenue_customer_mapping_t RCMT on RCMT.finance_customer_name=ARDT.finance_customer_name "
			+ "and RCMT.customer_geography = ARDT.finance_geography and RCMT.finance_iou =ARDT.finance_iou "
			+ "JOIN geography_mapping_t GMT on ARDT.finance_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and ";

	private static final String TARGET_VS_ACTUAL_TARGET_QUERY_PREFIX = "select BCMT.customer_name,BDT.quarter,sum(BDT.target) from beacon_data_t BDT "
			+ "JOIN beacon_customer_mapping_t BCMT on BCMT.beacon_customer_name=BDT.beacon_customer_name "
			+ "and BCMT.customer_geography = BDT.beacon_geography "
			+ "and BDT.beacon_iou = BCMT.beacon_iou "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou  "
			+ "where BCMT.customer_name not like 'UNKNOWN%' and ";

	// TargetVsActual Summary
	private static final String TARGET_VS_ACTUAL_TOTAL_REVENUE_QUERY_PREFIX = "select sum(BDT.target) from beacon_data_t BDT "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou  "
			+ "where ";

	private static final String TARGET_VS_ACTUAL_TOTAL_ACT_PROJ_REVENUE_QUERY_PREFIX = 
			"select sum(actual_revenue) as revenue from ( "
			+ "(select sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT "
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name "
			+ "and RCMT.customer_geography=ARDT.finance_geography and RCMT.finance_iou =ARDT.finance_iou) "
			+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp where	";

	private static final String TARGET_VS_ACTUAL_TOTAL_UNION_PROJECTED_REVENUE_QUERY_PREFIX = 
			"UNION (select case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue from projected_revenues_data_t PRDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou ) "
			+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp where ";
	
	private static final String TOP30_CUSTOMERS_REVENUE_SUM_QUERY_PREFIX = "select sum(revenue) as top_revenue from (select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from "
			+ " (((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name "
			+ "and RCMT.customer_geography=ARDT.finance_geography and RCMT.finance_iou = ARDT.finance_iou)"
			+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and ";
	
	private static final String TOP_CUSTOMER_REVENUE_QUERY_PREFIX = " select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from "
			+ " (((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name "
			+ "and RCMT.customer_geography = ARDT.finance_geography and RCMT.finance_iou = ARDT.finance_iou)"
			+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and ";
	
	private static final String OVER_ALL_CUSTOMER_REVENUE_QUERY_PREFIX = " select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue from "
			+ " (((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue from actual_revenues_data_t ARDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name "
			+ "and RCMT.customer_geography = ARDT.finance_geography and RCMT.finance_iou = ARDT.finance_iou)"
			+ "JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ "JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ "where ";
			
	private static final String RCMT_GEO_COND_PREFIX = "RCMT.customer_geography in (";

	private static final String RCMT_GROUP_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX = "group by RCMT.customer_name order by actual_revenue desc)";
	
	private static final String TOP_CUSTOMER_REVENUE_UNION_QUERY_PREFIX = 
		"UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
			+ "from projected_revenues_data_t PRDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou)"
			+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and " ;
	
	private static final String OVER_ALL_CUSTOMER_REVENUE_UNION_QUERY_PREFIX = 
		"UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
			+ "from projected_revenues_data_t PRDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou)"
			+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where " ;
	
	private static final String TOP_CUSTOMER_REVENUE_SUM_UNION_QUERY_PREFIX = 
		"UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue "
			+ "from projected_revenues_data_t PRDT " 
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography = PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou)"
			+ "JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and " ;

	private static final String RCMT_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX = "group by RCMT.customer_name order by projected_revenue desc)))"
			+ " as RVNU group by RVNU.customer_name order by revenue desc LIMIT ";
	
	private static final String RCMT_GROUP_CUST_ORDER_PROJECTED_REVENUE_COND_PREFIX = "group by RCMT.customer_name order by projected_revenue desc)))"
			+ " as RVNU group by RVNU.customer_name order by revenue desc  ";
	
	private static final String TARGET_VS_ACTUAL_TARGET_REVENUE_QUERY_PREFIX = "select BCMT.customer_name,sum(BDT.target) as revenue_sum from beacon_data_t BDT  "
			+ "JOIN beacon_customer_mapping_t BCMT on (BCMT.beacon_customer_name=BDT.beacon_customer_name "
			+ "and BCMT.customer_geography = BDT.beacon_geography and BDT.beacon_iou = BCMT.beacon_iou) "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou "
			+ "where BCMT.customer_name not like 'UNKNOWN%' and ";

	private static final String TARGET_VS_ACTUAL_OVERALL_TARGET_REVENUE_QUERY_PREFIX = "select BCMT.customer_name,sum(BDT.target) as revenue_sum from beacon_data_t BDT  "
			+ "JOIN beacon_customer_mapping_t BCMT on (BCMT.beacon_customer_name=BDT.beacon_customer_name "
			+ "and BCMT.customer_geography = BDT.beacon_geography and BDT.beacon_iou = BCMT.beacon_iou) "
			+ "JOIN geography_mapping_t GMT on BDT.beacon_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou "
			+ "where ";

	private static final String OVERALL_REVENUE_BY_GEO_QUERY_PREFIX = "select RVNU.customer_name, sum(RVNU.actual_revenue) as revenue, RVNU.display_geography from  "
			+ "((select RCMT.customer_name, sum(ARDT.revenue) as actual_revenue, GMT.display_geography from actual_revenues_data_t ARDT " 
			+ " JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = ARDT.finance_customer_name " 
			+ " and RCMT.customer_geography = ARDT.finance_geography and RCMT.finance_iou = ARDT.finance_iou) "
			+ " JOIN geography_mapping_t GMT on ARDT.finance_geography = GMT.geography "
			+ " JOIN iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp  "
			+ "where ";
	private static final String GROUP_BY_OVERALL_ACTUAL_REVENUE_BY_GEO_PREFIX="group by RCMT.customer_name,GMT.display_geography  order by actual_revenue desc) ";
	
	private static final String OVERALL_REVENUE_BY_GEO_UNION_PROJECTED_QUERY_PREFIX = 
			"UNION (select RCMT.customer_name, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0' end as projected_revenue " 
			+ ", GMT.display_geography from projected_revenues_data_t PRDT " 
			+ " JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ " JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou) "
			+ " JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where ";
	
	private static final String GROUP_BY_OVERALL_PROJECTED_REVENUE_BY_GEO_PREFIX = 
			"group by RCMT.customer_name, GMT.display_geography order by projected_revenue desc)) "
			+ " as RVNU group by RVNU.customer_name, RVNU.display_geography order by revenue desc ";
	
	private static final String GROUP_CUST_GEO_IOU_QUERY_PREFIX = "select RVNU.customer_name, RVNU.finance_customer_name, RVNU.display_iou, RVNU.display_geography "
			+ "from ((select RCMT.customer_name, RCMT.finance_customer_name, icmt.display_iou, " 
			+ "gmt.display_geography from actual_revenues_data_t ARDT JOIN revenue_customer_mapping_t RCMT on "
			+ "(RCMT.finance_customer_name = ARDT.finance_customer_name and RCMT.customer_geography=ARDT.finance_geography and RCMT.finance_iou = ARDT.finance_iou) "
			+ "JOIN geography_mapping_t GMT on ARDT.finance_geography = GMT.geography "
			+ "JOIN iou_customer_mapping_t ICMT on "
			+ "ARDT.finance_iou = ICMT.iou JOIN sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp " 
			+ "where RCMT.customer_name not like 'UNKNOWN%' and ";
	
	private static final String GROUP_CUST_GEO_IOU_UNION_QUERY_PREFIX = " UNION (select RCMT.customer_name, RCMT.finance_customer_name, icmt.display_iou, gmt.display_geography "
			+ "from projected_revenues_data_t PRDT JOIN geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ "JOIN revenue_customer_mapping_t RCMT on (RCMT.finance_customer_name = PRDT.finance_customer_name "
			+ "and RCMT.customer_geography=PRDT.finance_geography and RCMT.finance_iou = PRDT.finance_iou) JOIN iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou " 
			+ "JOIN sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ "where RCMT.customer_name not like 'UNKNOWN%' and ";
	
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
			+ " where ";

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
			+ " inner join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
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
			+ " where ";

	public static final String OPPORTUNITY_PIPELINE_PROSPECTS_IOU_QUERY_PREFIX =
			"select distinct SASMT.sales_stage_description,case when count(opp.opportunity_id) is not null then count(opp.opportunity_id) else 0 end as noOfBids,ICM.display_iou,case when sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) is not null then sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = ('INR'))) else 0 end as bidValue"
			+ " from opportunity_t OPP"
			+ " inner join geography_country_mapping_t GCMT on GCMT.country=OPP.country"
			+ " inner join geography_mapping_t GMT on GMT.geography = GCMT.geography"
//			+ " inner join bid_details_t BDT on BDT.opportunity_id = OPP.opportunity_id"
			+ " left outer join opportunity_sub_sp_link_t ssl on opp.opportunity_id = ssl.opportunity_id"
			+ " inner join sub_sp_mapping_t SSMT on ssl.sub_sp = SSMT.sub_sp"
			+ " inner join customer_master_t CMT on opp.customer_id = CMT.customer_id"
			+ " inner join iou_customer_mapping_t ICM on CMT.iou = ICM.iou"
			+ " inner join sales_stage_mapping_t SASMT on opp.sales_stage_code = SASMT.sales_stage_code"
			+ " where ";

	private static final String CONNECT_START_DATE_COND_PREFIX = "CON.start_datetime_of_connect between '";
	private static final String CONNECT_END_DATE_COND_PREFIX = " AND '";
	private static final String GEO_COND_PREFIX = "GMT.geography in (";
	private static final String SUBSP_COND_PREFIX = "SSM.display_sub_sp in (";
	private static final String IOU_COND_PREFIX = "ICMT.display_iou in (";
	private static final String COUNTRY_COND_PREFIX = "GCM.country in (";
	private static final String CONNECT_GEO_GROUP_BY_COND_PREFIX = "group by GMT.display_geography";
	private static final String CONNECT_GROUP_BY_GEOGRAPHY_COND_PREFIX = " ))) as geo group by display_geography ";
	private static final String CONNECT_GROUP_BY_SUBSP_COND_PREFIX = " ))) as geo group by display_sub_sp ";
	private static final String CONNECT_IOU_GROUP_BY_COND_PREFIX = "group by display_iou";
	private static final String CONNECT_SUBSP_GROUP_BY_COND_PREFIX = "group by display_sub_sp";
	private static final String BID_START_DATE_COND_PREFIX = "BID.bid_request_receive_date between '";
	private static final String BID_END_DATE_COND_C_PREFIX = " AND '";
	private static final String BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX = " (BIDGO.bid_office_group_owner in (";
	

	private static final String TARVSACT_GEO_COND_PREFIX = "GMT.geography in  (";
	private static final String TARVSACT_PROJECTED_GROUP_BY_COND_PREFIX = "group by RCMT.customer_name,PRDT.quarter";
	private static final String TARVSACT_ACTUAL_GROUP_BY_COND_PREFIX = "group by RCMT.customer_name,ARDT.quarter";
	private static final String TARVSACT_TARGET_GROUP_BY_COND_PREFIX = "group by BCMT.customer_name,BDT.quarter";
	private static final String TARVSACT_MONTHS_PROJECTED_COND_PREFIX = "upper(PRDT.month) in (";
	private static final String TARVSACT_REVENUE_MONTHS_COND_PREFIX = "upper(ARDT.month) in (";
	private static final String TARVSACT_ACTUAL_QUARTER_COND_PREFIX = "BDT.quarter in (";
	private static final String TARVSACT_ACTUAL_AS_RVNU_COND_PREFIX = "))) as RVNU";
	private static final String TARVSACT_GROUP_BY_ORDER_BY_COND_PREFIX = "30) as top_Revenue";
	private static final String GROUP_BY_ORDER_BY_TOP_LIMIT_COND_PREFIX = "group by RCMT.customer_name order by actual_revenue desc) as RVNU order by revenue desc LIMIT ";
	private static final String TARVSACT_GROUP_BY_ORDER_BY_REV_COND_PREFIX = "group by BCMT.customer_name order by revenue_sum desc";
	private static final String TARVSACT_OVERALL_GROUP_BY_ORDER_BY_COND_PREFIX = "group by RCMT.customer_name order by actual_revenue desc) as RVNU group by RVNU.customer_name order by revenue desc";

	private static final String TARVS_ACT_OVERALL_GROUP_BY_GEO_COND_PREFIX = "group by RCMT.customer_name, GMT.display_geography order by actual_revenue desc) "
			+ "as RVNU group by RVNU.customer_name ,RVNU.display_geography order by revenue desc";

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
		private static final String TOP_REVENUE_CUSTOMER_COND_PREFIX = "RCMT.customer_name in (";
		

	
	
	public List<TargetVsActualDetailed> getTargetVsActual(
			List<String> geography, List<String> iou, String fromMonth,
			String toMonth, List<String> currency, String userId)
			throws Exception {
		Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap = new TreeMap<String, List<TargetVsActualQuarter>>();
		List<TargetVsActualDetailed> targetVsActualDetails = new ArrayList<TargetVsActualDetailed>();
		List<String> geographyList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
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
				// Validate user group, BDM's & BDM supervisor's are not
				// authorized for this service
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
				case BDM_SUPERVISOR:
					logger.error("User is not authorized to access this service");
					throw new DestinationException(HttpStatus.UNAUTHORIZED,
							"User is not authorised to access this service");
				default:
					if (geography.contains("All") && (iou.contains("All"))) {
						if (formattedMonths == null
								|| formattedMonths.isEmpty()) {
							throw new DestinationException(
									HttpStatus.BAD_REQUEST,
									"No Months available for this perticular range");
						}
						createMapForProjectedByUserPrevilages(formattedMonths,
								customerIdQuarterMap, userId);
						mergeMapForActualByUserPrevilages(formattedMonths,
								customerIdQuarterMap, userId);
						mergeMapForTargetByUserPrevilages(fromMonth, toMonth,
								customerIdQuarterMap, userId);
						generateReponseFromMap(customerIdQuarterMap,
								targetVsActualDetails);
						setCurrency(targetVsActualDetails, currency);
						return targetVsActualDetails;
					} else {
						addEmptyItemToListIfAll(iou, iouList);
						addEmptyItemToListIfGeo(geography, geographyList);
						if (formattedMonths == null
								|| formattedMonths.isEmpty()) {
							throw new DestinationException(
									HttpStatus.BAD_REQUEST,
									"No Months available for this perticular range");
						}
						createMapForProjected(iouList, geographyList,
								formattedMonths, customerIdQuarterMap);
						mergeMapForActual(iouList, geographyList,
								formattedMonths, customerIdQuarterMap);
						mergeMapForTarget(iouList, geographyList, fromMonth,
								toMonth, customerIdQuarterMap);
						generateReponseFromMap(customerIdQuarterMap,
								targetVsActualDetails);
						setCurrency(targetVsActualDetails, currency);
						return targetVsActualDetails;
					}
				}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid User Group");
			}
		}
	}

	private void createMapForProjectedByUserPrevilages(
			List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			String userId) throws Exception {

		// Form the native top revenue query string
		String queryString = getProjectedDetailsQueryString(formattedMonths,
				customerIdQuarterMap, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);

		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();

		if (resultList != null) {
			for (Object[] projectedQuarter : resultList) {
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				String customerName = projectedQuarter[0].toString();
				targetVsActualQuarter
						.setQuarter(projectedQuarter[1].toString());
				targetVsActualQuarter
						.setProjected((BigDecimal) projectedQuarter[2]);
				customerIdQuarterMap = createMapforQuarters(
						customerIdQuarterMap, targetVsActualQuarter,
						customerName);
			}
		}
	}

	private String getProjectedDetailsQueryString(List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			String userId) throws Exception {
		logger.debug("Inside getProjectedDetailsQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_PROJECTED_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX + formattedMonthsList
				+ Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append("" + TARVSACT_PROJECTED_GROUP_BY_COND_PREFIX);
		return queryBuffer.toString();
	}

	private void mergeMapForActualByUserPrevilages(
			List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			String userId) throws Exception {

		// Form the native top revenue query string
		String queryString = getActualDetailsQueryString(formattedMonths,
				customerIdQuarterMap, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);

		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
		for (Object[] actualRevenueObj : resultList) {
			String customerName = actualRevenueObj[0].toString();
			String quarter = actualRevenueObj[1].toString();
			BigDecimal actual = (BigDecimal) actualRevenueObj[2];
			if (customerIdQuarterMap.containsKey(customerName)) {
				List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap
						.get(customerName);
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
				customerIdQuarterMap.put(customerName,
						targetVsActualQuarterList);
			}
		}
	}

	private String getActualDetailsQueryString(List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			String userId) throws Exception {
		logger.debug("Inside getTargetVsActualProjectedDetailedQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_ACTUAL_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TARVSACT_ACTUAL_GROUP_BY_COND_PREFIX);
		return queryBuffer.toString();

	}

	private void mergeMapForTargetByUserPrevilages(String fromMonth,
			String toMonth,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			String userId) throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);

		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		// Form the native top revenue query string
		String queryString = getTargetDetailsQueryString(quarterList,
				customerIdQuarterMap, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
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
				List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap
						.get(customerName);
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
				customerIdQuarterMap.put(customerName,
						targetVsActualQuarterList);
			}
		}

	}

	private String getTargetDetailsQueryString(List<String> quarterList,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			String userId) throws Exception {
		logger.debug("Inside getTargetVsActualProjectedDetailedQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_TARGET_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(TARVSACT_GEO_COND_PREFIX, null,
						IOU_COND_PREFIX, null);

		String quarters = getStringListWithSingleQuotes(quarterList);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_ACTUAL_QUARTER_COND_PREFIX + quarters
				+ Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TARVSACT_TARGET_GROUP_BY_COND_PREFIX);
		return queryBuffer.toString();
	}

	private void generateReponseFromMap(
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap,
			List<TargetVsActualDetailed> targetVsActualDetails) throws Exception {
		try{
		for (String customerName : customerIdQuarterMap.keySet()) {
			TargetVsActualDetailed targetVsActualDetailed = new TargetVsActualDetailed();
			List<TargetVsActualYearToDate> targetVsActualYtds = getTargetVsActualYtdList(customerIdQuarterMap
					.get(customerName));
			CustomerMasterT customerMasterT = customerRepository
					.findByCustomerName(customerName);
			targetVsActualDetailed.setCustomerMasterT(customerMasterT);
			targetVsActualDetailed.setYearToDate(targetVsActualYtds);
			targetVsActualDetails.add(targetVsActualDetailed);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void createMapForProjected(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap) {
		List<Object[]> projectedQuarterObjList = projectedRevenuesDataTRepository
				.getProjectedRevenuesByQuarter(iouList, geographyList,
						formattedMonths);
		if (projectedQuarterObjList != null) {
			for (Object[] projectedQuarter : projectedQuarterObjList) {
				TargetVsActualQuarter targetVsActualQuarter = new TargetVsActualQuarter();
				String customerName = projectedQuarter[0].toString();
				targetVsActualQuarter
						.setQuarter(projectedQuarter[1].toString());
				targetVsActualQuarter
						.setProjected((BigDecimal) projectedQuarter[2]);
				customerIdQuarterMap = createMapforQuarters(
						customerIdQuarterMap, targetVsActualQuarter,
						customerName);
			}
		}
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

	private void mergeMapForTarget(List<String> iouList,
			List<String> geographyList, String fromMonth, String toMonth,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap)
			throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);

		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);

		List<Object[]> targetObjList = beaconDataTRepository
				.getTargetByQuarter(iouList, geographyList, quarterList);
		for (Object[] targetObj : targetObjList) {
			String customerName = targetObj[0].toString();
			String quarter = targetObj[1].toString();
			BigDecimal target = (BigDecimal) targetObj[2];

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
				List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap
						.get(customerName);
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
				customerIdQuarterMap.put(customerName,
						targetVsActualQuarterList);
			}
		}
	}

	private void mergeMapForActual(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths,
			Map<String, List<TargetVsActualQuarter>> customerIdQuarterMap) {
		List<Object[]> actualRevenueObjList = actualRevenuesDataTRepository
				.getActualRevenuesByQuarter(iouList, geographyList,
						formattedMonths);
		for (Object[] actualRevenueObj : actualRevenueObjList) {
			String customerName = actualRevenueObj[0].toString();
			String quarter = actualRevenueObj[1].toString();
			BigDecimal actual = (BigDecimal) actualRevenueObj[2];
			if (customerIdQuarterMap.containsKey(customerName)) {
				List<TargetVsActualQuarter> targetVsActualQuarterList = customerIdQuarterMap
						.get(customerName);
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
				customerIdQuarterMap.put(customerName,
						targetVsActualQuarterList);
			}
		}
	}

	private void addEmptyItemToListIfGeo(List<String> itemList,
			List<String> targetList) {
		if (itemList.contains("All") || itemList.isEmpty()) {
			targetList.add("");
		} else {
			targetList.addAll(geographyRepository
					.findByDisplayGeography(itemList));
		}
	}

	private void addEmptyItemToListIfAll(List<String> itemList,
			List<String> targetList) {
		if (itemList.contains("All") || itemList.isEmpty()) {
			targetList.add("");
		} else {
			targetList.addAll(itemList);
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

	public InputStreamResource getTargetVsActualReports(List<String> geography,
			List<String> iou, String fromMonth, String toMonth,
			List<String> currencyList, List<String> fields, String userId)
			throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		String tillDate = DateUtils.getCurrentDate();
		//To Write The Report Title page
		buildExcelTargetVsActualDetailedReportService.getTargetVsActualTitlePage(workbook, geography, iou, userId, tillDate, currencyList, fromMonth, toMonth, "Summary, Detailed");
		//
		List<TargetVsActualDetailed> targetVsActualDetailedList = getTargetVsActual(geography, iou, fromMonth, toMonth, currencyList, userId);
		
		getTargetVsActualSummaryExcel(geography, iou, fromMonth, toMonth, currencyList, userId, workbook);
		
		buildExcelTargetVsActualDetailedReportService.getTargetVsActualExcel(targetVsActualDetailedList, fields, currencyList, fromMonth,
				workbook);
		
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStream = new InputStreamResource(new ByteArrayInputStream(bytes));
		return inputStream;
	}

	public InputStreamResource getTargetVsActualDetailedReport(
			List<String> geography, List<String> iou, String fromMonth,
			String toMonth, List<String> currency, List<String> fields,
			String userId) throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		List<TargetVsActualDetailed> targetVsActualDetailedList = getTargetVsActual(
				geography, iou, fromMonth, toMonth, currency, userId);
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
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStream = new InputStreamResource(
				new ByteArrayInputStream(bytes));
		return inputStream;
	}

	public InputStreamResource getTargetVsActualSummaryReport(
			List<String> geography, List<String> iou, String fromMonth,
			String toMonth, List<String> currencyList, String userId)
			throws Exception {
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		String tillDate = DateUtils.getCurrentDate();
		buildExcelTargetVsActualDetailedReportService
				.getTargetVsActualTitlePage(workbook, geography, iou, userId, tillDate, currencyList, fromMonth, toMonth, "Summary");
		getTargetVsActualSummaryExcel(geography, iou, fromMonth, toMonth,
				currencyList, userId, workbook);
		ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
		workbook.write(byteOutPutStream);
		byteOutPutStream.flush();
		byteOutPutStream.close();
		byte[] bytes = byteOutPutStream.toByteArray();
		InputStreamResource inputStream = new InputStreamResource(
				new ByteArrayInputStream(bytes));
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
				// Validate user group, BDM's & BDM supervisor's are not
				// authorized for this service
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
				case BDM_SUPERVISOR:
					logger.error("User is not authorized to access this service");
					throw new DestinationException(HttpStatus.UNAUTHORIZED,
							"User is not authorised to access this service");
				default:
					if (geography.contains("All") && (iou.contains("All"))) {
						if (formattedMonths == null
								|| formattedMonths.isEmpty()) {
							throw new DestinationException(
									HttpStatus.BAD_REQUEST,
									"No Months available for this perticular range");
						}
						// sec 1
						totalTargetINR = getTotalTargetByUserPrivileges(
								fromMonth, toMonth, userId);
						totalActualProjectedINR = getTotalActualProjectedByUserPrivileges(
								formattedMonths, userId);
						top30CustomersRevenueINR = getTop30CustomersRevenueSumByUserPrivileges(
								formattedMonths, userId);
						// sec 2
						targetOverAllRevenuesMap = getOverAllTargetRevenuesByUserPrivileges(
								fromMonth, toMonth, userId);
						actualProjectedOverAllRevenuesMap = getOverAllActualProjectedRevenuesByUserPrivileges(
								formattedMonths, userId);
						overAllRevenuesByGeoList = getOverAllRevenuesByGeoAndUserPrivileges(
								formattedMonths, userId);
						// sec 3
						actualProjectedRevenuesList = getTopCustomersRevenueListByUserPrivileges(
								formattedMonths, userId, count);
						targetRevenuesMap = getTargetRevenuesByUserPrivileges(
								fromMonth, toMonth, userId);
						geoIouGroupCustNameList = getGroupCustGeoIouByUserPrivilages(
								formattedMonths, userId);
					} else {
						addEmptyItemToListIfAll(iou, iouList);
						addEmptyItemToListIfGeo(geography, geographyList);
						if (formattedMonths == null
								|| formattedMonths.isEmpty()) {
							throw new DestinationException(
									HttpStatus.BAD_REQUEST,
									"No Months available for this perticular range");
						}
						totalTargetINR = getTotalTarget(iouList, geographyList,
								fromMonth, toMonth);
						totalActualProjectedINR = getTotalActualProjected(
								iouList, geographyList, formattedMonths);
						top30CustomersRevenueINR = getTop30CustomersRevenueSum(
								iouList, geographyList, formattedMonths);
						targetOverAllRevenuesMap = getOverAllTargetRevenues(
								iouList, geographyList, fromMonth, toMonth);
						actualProjectedOverAllRevenuesMap = getOverAllActualProjectedRevenues(
								iouList, geographyList, formattedMonths);
						overAllRevenuesByGeoList = getOverAllRevenuesByGeo(
								iouList, geographyList, formattedMonths);
						actualProjectedRevenuesList = getTop30CustomersRevenuesList(
								iouList, geographyList, formattedMonths);
						targetRevenuesMap = getTargetRevenues(iouList,
								geographyList, fromMonth, toMonth);
						geoIouGroupCustNameList = getGroupCustGeoIou(iouList,
								geographyList, formattedMonths);
					}
				}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid User Group");
			}
			if (totalTargetINR != null && totalActualProjectedINR != null
					&& top30CustomersRevenueINR != null
					&& targetOverAllRevenuesMap != null
					&& actualProjectedOverAllRevenuesMap != null
					&& overAllRevenuesByGeoList != null
					&& actualProjectedRevenuesList != null
					&& targetRevenuesMap != null
					&& geoIouGroupCustNameList != null) {
				buildExcelTargetVsActualSummaryReportService
						.getTargetVsActualSummaryExcel(fromMonth,
								totalTargetINR, totalActualProjectedINR,
								top30CustomersRevenueINR,
								actualProjectedRevenuesList, targetRevenuesMap,
								currencyList, targetOverAllRevenuesMap,
								actualProjectedOverAllRevenuesMap, userId,
								formattedMonths, workbook,
								overAllRevenuesByGeoList,
								geoIouGroupCustNameList);
			} else {
				logger.error("NOT_FOUND: Report could not be downloaded, as no targetVsActual details are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no targetVsActual details are available for user selection and privilege combination");
			}
		}
	}

	private List<GroupCustomerGeoIouResponse> getGroupCustGeoIouByUserPrivilages(
			List<String> formattedMonths, String userId) throws Exception {
		List<GroupCustomerGeoIouResponse> revenueGeoValuesList = new ArrayList<GroupCustomerGeoIouResponse>();
		String queryString = getGroupCustGeoIouQueryString(formattedMonths, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);

		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
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

	private String getGroupCustGeoIouQueryString(List<String> formattedMonths,
			String userId) throws Exception {
		logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				GROUP_CUST_GEO_IOU_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		
		queryBuffer.append(GROUP_CUST_GEO_IOU_UNION_QUERY_PREFIX);
		// Get user access privilege groups
		queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TARVSACT_ACTUAL_AS_RVNU_COND_PREFIX);
		return queryBuffer.toString();
	}

	private List<Object[]> getOverAllRevenuesByGeoAndUserPrivileges(
			List<String> formattedMonths, String userId) throws Exception {
		String queryString = getOverAllRevenuesByGeoQueryString(
				formattedMonths, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);

		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
		return resultList;
	}

	private String getOverAllRevenuesByGeoQueryString(
			List<String> formattedMonths, String userId) throws Exception {
		logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				OVERALL_REVENUE_BY_GEO_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(GROUP_BY_OVERALL_ACTUAL_REVENUE_BY_GEO_PREFIX);
		
		queryBuffer.append(OVERALL_REVENUE_BY_GEO_UNION_PROJECTED_QUERY_PREFIX);
		queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(GROUP_BY_OVERALL_PROJECTED_REVENUE_BY_GEO_PREFIX);
		return queryBuffer.toString();
	}

	private BigDecimal getTop30CustomersRevenueSumByUserPrivileges(
			List<String> formattedMonths, String userId) throws Exception {
		String queryString = getTop30CustomersRevenueSumQueryString(
				formattedMonths, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
		List<BigDecimal> resultList = tergetVsActualReportQuery.getResultList();
		return resultList.get(0);
	}

	private String getTop30CustomersRevenueSumQueryString(
			List<String> formattedMonths, String userId) throws Exception {
		logger.debug("Inside getTotalTargetRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(TOP30_CUSTOMERS_REVENUE_SUM_QUERY_PREFIX);
//		queryBuffer.append(TOP_CUSTOMER_REVENUE_QUERY_PREFIX);
		
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(RCMT_GEO_COND_PREFIX,null,IOU_COND_PREFIX,null);
		
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(RCMT_GROUP_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
//		queryBuffer.append(GROUP_BY_ORDER_BY_TOP_LIMIT_COND_PREFIX);
		
		queryBuffer.append(TOP_CUSTOMER_REVENUE_SUM_UNION_QUERY_PREFIX);
		
		queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		
		// Get user access privilege groups
		HashMap<String, String> queryPrefixProjectedMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(RCMT_GEO_COND_PREFIX,null,IOU_COND_PREFIX,null);
		// Get WHERE clause string
		
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(RCMT_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX);
		queryBuffer.append(TARVSACT_GROUP_BY_ORDER_BY_COND_PREFIX);
		
		return queryBuffer.toString();
	}

	private BigDecimal getTotalTargetByUserPrivileges(String fromMonth,
			String toMonth, String userId) throws Exception {
		// Form the native top revenue query string
		String queryString = getTotalTargetRevenueQueryString(fromMonth,
				toMonth, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
		List<BigDecimal> resultList = tergetVsActualReportQuery.getResultList();
		return resultList.get(0);
	}

	private String getTotalTargetRevenueQueryString(String fromMonth,
			String toMonth, String userId) throws Exception {
		logger.debug("Inside getTotalTargetRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_TOTAL_REVENUE_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);
		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		String quarters = getStringListWithSingleQuotes(quarterList);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_ACTUAL_QUARTER_COND_PREFIX + quarters
				+ Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		return queryBuffer.toString();
	}

	public String getStringListWithSingleQuotes(List<String> formattedList) {
		String appendedString = Joiner.on("\',\'").join(formattedList);
		if (!formattedList.isEmpty()) {
			appendedString = "\'" + appendedString + "\'";
		}
		return appendedString;
	}

	private BigDecimal getTotalActualProjectedByUserPrivileges(
			List<String> formattedMonths, String userId) throws Exception {
		String queryString = getTotalActualProjectedRevenueQueryString(
				formattedMonths, userId);
		
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
		List<BigDecimal> resultList = tergetVsActualReportQuery.getResultList();
		return resultList.get(0);
	}

	private String getTotalActualProjectedRevenueQueryString(
			List<String> formattedMonths, String userId) throws Exception {
		logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_TOTAL_ACT_PROJ_REVENUE_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(RCMT_GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		
		queryBuffer.append(TARGET_VS_ACTUAL_TOTAL_UNION_PROJECTED_REVENUE_QUERY_PREFIX);
		// Get user access privilege groups
				HashMap<String, String> queryUnionPrefixMap = userAccessPrivilegeQueryBuilder
						.getQueryPrefixMap(RCMT_GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
				// Get WHERE clause string
				queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX
						+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
				String whereUnionClause = userAccessPrivilegeQueryBuilder
						.getUserAccessPrivilegeWhereConditionClause(userId,
								queryUnionPrefixMap);
				if (whereUnionClause != null && !whereUnionClause.isEmpty()) {
					queryBuffer.append(Constants.AND_CLAUSE + whereUnionClause);
				}
		
		queryBuffer.append(TARVSACT_ACTUAL_AS_RVNU_COND_PREFIX);
		return queryBuffer.toString();
	}

	private Map<String, BigDecimal> getTargetRevenuesByUserPrivileges(
			String fromMonth, String toMonth, String userId) throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);
		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		Map<String, BigDecimal> targetRevenues = new TreeMap<String, BigDecimal>();
		// Form the native top revenue query string
		String queryString = getTargetRevenueQueryString(quarterList, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);

		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
		for (Object[] targetRevenueObj : resultList) {
			String customerName = targetRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (targetRevenueObj[1]);
			targetRevenues.put(customerName, revenue);
		}
		return targetRevenues;
	}

	private String getTargetRevenueQueryString(List<String> quarterList,
			String userId) throws Exception {
		logger.debug("Inside getTotalTargetRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_TARGET_REVENUE_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String quarters = getStringListWithSingleQuotes(quarterList);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_ACTUAL_QUARTER_COND_PREFIX + quarters
				+ Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TARVSACT_GROUP_BY_ORDER_BY_REV_COND_PREFIX);
		return queryBuffer.toString();
	}

	private Map<String, BigDecimal> getOverAllTargetRevenuesByUserPrivileges(
			String fromMonth, String toMonth, String userId) throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);
		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		Map<String, BigDecimal> targetRevenues = new TreeMap<String, BigDecimal>();
		// Form the native top revenue query string
		String queryString = getOverAllTargetRevenueQueryString(quarterList,
				userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
		for (Object[] targetRevenueObj : resultList) {
			String customerName = targetRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (targetRevenueObj[1]);
			targetRevenues.put(customerName, revenue);
		}
		return targetRevenues;
	}

	private String getOverAllTargetRevenueQueryString(List<String> quarterList,
			String userId) throws Exception {
		logger.debug("Inside getTotalTargetRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				TARGET_VS_ACTUAL_OVERALL_TARGET_REVENUE_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		String quarters = getStringListWithSingleQuotes(quarterList);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_ACTUAL_QUARTER_COND_PREFIX + quarters
				+ Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TARVSACT_GROUP_BY_ORDER_BY_REV_COND_PREFIX);
		return queryBuffer.toString();
	}

	private List<CustomerRevenueValues> getTopCustomersRevenueListByUserPrivileges(
			List<String> formattedMonths, String userId, int count)
			throws Exception {
		List<CustomerRevenueValues> revenueGeoValuesList = new ArrayList<CustomerRevenueValues>();
		String queryString = getActualProjectedTopRevenuesQueryString(
				formattedMonths, userId, count,RCMT_GEO_COND_PREFIX,null,IOU_COND_PREFIX,null);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
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
	public String getTopRevenueCustomersForDashboard(String userId,String financialYear,int count) throws Exception {
		List<String> months = DateUtils.getMonthsFromYear(financialYear);
		return getActualProjectedTopRevenuesQueryString(months,userId, count, 
				RCMT_GEO_COND_PREFIX, SUBSP_COND_PREFIX, IOU_COND_PREFIX, TOP_REVENUE_CUSTOMER_COND_PREFIX);
	}

	private String getActualProjectedTopRevenuesQueryString(
			List<String> formattedMonths, String userId, int count, String geoPrefix, String subSpPrefix, String iouPrefix, String custPrefix)
			throws Exception {
		logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
//		StringBuffer queryBuffer = new StringBuffer(
//				TOP_CUS_REVENUES_LIST_QUERY_PREFIX);
		StringBuffer queryBuffer = new StringBuffer(TOP_CUSTOMER_REVENUE_QUERY_PREFIX);
		
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(geoPrefix, subSpPrefix, iouPrefix, custPrefix);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(RCMT_GROUP_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
//		queryBuffer.append(GROUP_BY_ORDER_BY_TOP_LIMIT_COND_PREFIX);
		
		queryBuffer.append(TOP_CUSTOMER_REVENUE_UNION_QUERY_PREFIX);
		
		// Get user access privilege groups
		HashMap<String, String> queryPrefixProjectedMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(geoPrefix, subSpPrefix, iouPrefix, custPrefix);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(RCMT_GROUP_CUST_ORDER_PROJECTED_REVENUE_LIMIT_COND_PREFIX);
		queryBuffer.append(count);
		return queryBuffer.toString();
	}

	private Map<String, BigDecimal> getOverAllActualProjectedRevenuesByUserPrivileges(
			List<String> formattedMonths, String userId) throws Exception {
		Map<String, BigDecimal> actualProjectedOverAllRevenues = new TreeMap<String, BigDecimal>();

		String queryString = getOverAllActualRevenuesQueryString(
				formattedMonths, userId);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query tergetVsActualReportQuery = entityManager
				.createNativeQuery(queryString);
		List<Object[]> resultList = tergetVsActualReportQuery.getResultList();
		for (Object[] actualRevenueObj : resultList) {
			String customerName = actualRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (actualRevenueObj[1]);
			actualProjectedOverAllRevenues.put(customerName, revenue);
		}
		return actualProjectedOverAllRevenues;
	}

	private String getOverAllActualRevenuesQueryString(
			List<String> formattedMonths, String userId) throws Exception {
		logger.debug("Inside getTotalActualProjectedRevenueQueryString() method");
StringBuffer queryBuffer = new StringBuffer(OVER_ALL_CUSTOMER_REVENUE_QUERY_PREFIX);
		
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(RCMT_GEO_COND_PREFIX,null,IOU_COND_PREFIX,null);
		String formattedMonthsList = getStringListWithSingleQuotes(formattedMonths);
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_REVENUE_MONTHS_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(RCMT_GROUP_CUST_ORDER_ACTUAL_REVENUE_COND_PREFIX);
		
		queryBuffer.append(OVER_ALL_CUSTOMER_REVENUE_UNION_QUERY_PREFIX);
		
		// Get WHERE clause string
		queryBuffer.append(TARVSACT_MONTHS_PROJECTED_COND_PREFIX
				+ formattedMonthsList + Constants.RIGHT_PARANTHESIS);
		
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(RCMT_GROUP_CUST_ORDER_PROJECTED_REVENUE_COND_PREFIX);
		return queryBuffer.toString();
	}

	private BigDecimal getTop30CustomersRevenueSum(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths) {
		Object[] top30CustomersRevenueObj = actualRevenuesDataTRepository
				.getTop30CustomersRevenueByQuarter(iouList, geographyList,
						formattedMonths);
		BigDecimal top30CustomersRevenue = (BigDecimal) top30CustomersRevenueObj[0];
		return top30CustomersRevenue;
	}

	private BigDecimal getTotalTarget(List<String> iouList,
			List<String> geographyList, String fromMonth, String toMonth)
			throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);
		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		Object[] targetObj = beaconDataTRepository.getTotalTargetByQuarter(
				iouList, geographyList, quarterList);
		BigDecimal target = (BigDecimal) targetObj[0];
		return target;
	}

	private BigDecimal getTotalActualProjected(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths) {
		Object[] actualRevenueObj = actualRevenuesDataTRepository
				.getTotalRevenue(iouList, geographyList, formattedMonths);
		BigDecimal actual = (BigDecimal) actualRevenueObj[0];
		return actual;
	}

	private Map<String, BigDecimal> getTargetRevenues(List<String> iouList,
			List<String> geographyList, String fromMonth, String toMonth)
			throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);
		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		Map<String, BigDecimal> targetRevenues = new TreeMap<String, BigDecimal>();
		List<Object[]> targetObjList = beaconDataTRepository
				.getTargetRevenueByQuarter(iouList, geographyList, quarterList);
		for (Object[] targetRevenueObj : targetObjList) {
			String customerName = targetRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (targetRevenueObj[1]);
			targetRevenues.put(customerName, revenue);
		}
		return targetRevenues;
	}

	private Map<String, BigDecimal> getOverAllTargetRevenues(
			List<String> iouList, List<String> geographyList, String fromMonth,
			String toMonth) throws Exception {
		String fromQuarter = DateUtils.getQuarterForMonth(fromMonth);
		String toQuarter = DateUtils.getQuarterForMonth(toMonth);
		List<String> quarterList = DateUtils.getAllQuartersBetween(fromQuarter,
				toQuarter);
		Map<String, BigDecimal> targetRevenues = new TreeMap<String, BigDecimal>();
		List<Object[]> targetObjList = beaconDataTRepository
				.getOverAllTargetRevenue(iouList, geographyList, quarterList);
		for (Object[] targetRevenueObj : targetObjList) {
			String customerName = targetRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (targetRevenueObj[1]);
			targetRevenues.put(customerName, revenue);
		}
		return targetRevenues;
	}

	private List<Object[]> getOverAllRevenuesByGeo(List<String> iouList,
			List<String> geographyList, List<String> formattedMonths) {
		List<Object[]> overAllRevenueByGeo = actualRevenuesDataTRepository
				.getOverAllActualRevenuesByGeo(geographyList, iouList,
						formattedMonths);

		return overAllRevenueByGeo;
	}

	private List<CustomerRevenueValues> getTop30CustomersRevenuesList(
			List<String> iouList, List<String> geographyList,
			List<String> formattedMonths) {
		List<CustomerRevenueValues> customerRevenueValuesList = new ArrayList<CustomerRevenueValues>();
		List<Object[]> actualRevenueObjList = actualRevenuesDataTRepository
				.getTop30CustomersRevenues(iouList, geographyList,
						formattedMonths);
		for (Object[] actualRevenueObj : actualRevenueObjList) {
			CustomerRevenueValues revenueGeoValues = new CustomerRevenueValues();
			String customerName = actualRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (actualRevenueObj[1]);
			revenueGeoValues.setCustomerName(customerName);
			revenueGeoValues.setValue(revenue);
			customerRevenueValuesList.add(revenueGeoValues);
		}
		return customerRevenueValuesList;
	}

	private Map<String, BigDecimal> getOverAllActualProjectedRevenues(
			List<String> iouList, List<String> geographyList,
			List<String> formattedMonths) {
		Map<String, BigDecimal> actualProjectedOverAllRevenues = new TreeMap<String, BigDecimal>();
		List<Object[]> actualRevenueObjList = actualRevenuesDataTRepository
				.getOverAllActualRevenues(iouList, geographyList,
						formattedMonths);
		for (Object[] actualRevenueObj : actualRevenueObjList) {
			String customerName = actualRevenueObj[0].toString();
			BigDecimal revenue = (BigDecimal) (actualRevenueObj[1]);
			actualProjectedOverAllRevenues.put(customerName, revenue);
		}
		return actualProjectedOverAllRevenues;
	}

	private List<GroupCustomerGeoIouResponse> getGroupCustGeoIou(
			List<String> iouList, List<String> geographyList,
			List<String> formattedMonths) {
		List<GroupCustomerGeoIouResponse> revenueGeoValuesList = new ArrayList<GroupCustomerGeoIouResponse>();
		List<Object[]> groupCustGeoIouObjList = actualRevenuesDataTRepository
				.getGroupCustGeoIou(geographyList, iouList, formattedMonths);
		for (Object[] actualRevenueObj : groupCustGeoIouObjList) {
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
	 * This method forms and executes the query to find Top revenue customers
	 * based on user access privileges
	 * 
	 * @param userId
	 * @param financialYear
	 * @param count
	 * @return
	 * @return Top revenue customers
	 * @throws Exception
	 */
	private List<ConnectT> getConnectDetailsBasedOnUserPrivileges(
			Date fromDate, Date toDate, String userId) throws Exception {
		logger.debug("Inside getConnectDetailsBasedOnUserPrivileges() method");
		// Form the native top revenue query string
		String queryString = getConnectDetailedQueryString(userId, fromDate,
				toDate);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query connectDetailedReportQuery = entityManager
				.createNativeQuery(queryString);
		List<String> resultList = connectDetailedReportQuery.getResultList();
		// Retrieve connect details
		List<ConnectT> connectDetailsList = null;
		if ((resultList != null) && !(resultList.isEmpty())) {
			connectDetailsList = connectRepository.getConnectsByIds(resultList);
		}
		if (connectDetailsList == null || connectDetailsList.isEmpty()) {
			logger.error("NOT_FOUND: Connects not found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connects not found");
		}
		return connectDetailsList;
	}

	private String getConnectGeoSummaryQueryString(String userId,
			Date fromDate, Date toDate) throws Exception {
		logger.debug("Inside getConnectSummaryQueryString() method");
		
		StringBuffer queryBuffer = new StringBuffer(CONNECT_COUNT_GEO_SUMMARY_QUERY_PREFIX);
		String unionPartner = CONNECT_SUMMARY_GEO_UNION_PARTNER_REPORT_QUERY_PREFIX;
		getConnectCustPartUnionQueryString(userId, fromDate, toDate, queryBuffer, unionPartner);
		queryBuffer.append(CONNECT_GROUP_BY_GEOGRAPHY_COND_PREFIX);
		
		return queryBuffer.toString();
	}
	
	private String getConnectSubSpSummaryQueryString(String userId,
			Date fromDate, Date toDate) throws Exception {
		logger.debug("Inside getConnectSummaryQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(CONNECT_SUMMARY_SUBSP_REPORT_QUERY_PREFIX);
		String unionPartner = CONNECT_SUMMARY_SUBSP_UNION_PARTNER_REPORT_QUERY_PREFIX;
		getConnectCustPartUnionQueryString(userId, fromDate, toDate, queryBuffer, unionPartner);
		
		queryBuffer.append(CONNECT_GROUP_BY_SUBSP_COND_PREFIX);
		return queryBuffer.toString();
	}

	public void getConnectCustPartUnionQueryString(String userId,
			Date fromDate, Date toDate, StringBuffer queryBuffer, String unionPartner)
			throws Exception {
		queryBuffer.append(CONNECT_JOIN_CUS_GEO_IOU_SUBSP);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, null);
		// Get WHERE clause string   
		queryBuffer.append(CONNECT_START_DATE_COND_PREFIX
				+ new Timestamp(fromDate.getTime()) + Constants.SINGLE_QUOTE);
		queryBuffer.append(CONNECT_END_DATE_COND_PREFIX
				+ new Timestamp(toDate.getTime()) + Constants.SINGLE_QUOTE);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(unionPartner);
		queryBuffer.append(CONNECT_PARTNER_UNION_REPORT_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryUnionPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						null, null);
		// Get WHERE clause string
		queryBuffer.append(CONNECT_START_DATE_COND_PREFIX
				+ new Timestamp(fromDate.getTime()) + Constants.SINGLE_QUOTE);
		queryBuffer.append(CONNECT_END_DATE_COND_PREFIX
				+ new Timestamp(toDate.getTime()) + Constants.SINGLE_QUOTE);
		String whereUnionClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryUnionPrefixMap);
		if (whereUnionClause != null && !whereUnionClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereUnionClause);
		}
	}

	private String getConnectIouSummaryQueryString(String userId,
			Date fromDate, Date toDate) throws Exception {
		logger.debug("Inside getConnectSummaryQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(
				CONNECT_SUMMARY_IOU_REPORT_QUERY_PREFIX);
		queryBuffer.append(CONNECT_JOIN_CUS_GEO_IOU_SUBSP);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, COUNTRY_COND_PREFIX);
		// Get WHERE clause string
		queryBuffer.append(CONNECT_START_DATE_COND_PREFIX
				+ new Timestamp(fromDate.getTime()) + Constants.SINGLE_QUOTE);
		queryBuffer.append(CONNECT_END_DATE_COND_PREFIX
				+ new Timestamp(toDate.getTime()) + Constants.SINGLE_QUOTE);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(CONNECT_IOU_GROUP_BY_COND_PREFIX);
		return queryBuffer.toString();
	}


	/**
	 * This Method returns the Connect Detailed Query String
	 * @param userId
	 * @param fromDate
	 * @param toDate
	 * @return Query string
	 * @throws Exception
	 */
	private String getConnectDetailedQueryString(String userId, Date fromDate,
			Date toDate) throws Exception {
		logger.debug("Inside getRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(CONNECT_REPORT_QUERY_PREFIX);
		queryBuffer.append(CONNECT_JOIN_CUS_GEO_IOU_SUBSP);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, null);
		// Get WHERE clause string
		queryBuffer.append(CONNECT_START_DATE_COND_PREFIX
				+ new Timestamp(fromDate.getTime()) + Constants.SINGLE_QUOTE);
		queryBuffer.append(CONNECT_END_DATE_COND_PREFIX
				+ new Timestamp(toDate.getTime()) + Constants.SINGLE_QUOTE);
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
			queryBuffer.append(CONNECT_ID_PARTNER_UNION_REPORT_QUERY_PREFIX);
			queryBuffer.append(CONNECT_PARTNER_UNION_REPORT_QUERY_PREFIX);
			// Get user access privilege groups
			HashMap<String, String> queryUnionPrefixMap = userAccessPrivilegeQueryBuilder
					.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,null, null);
			
			// Get WHERE clause string
			queryBuffer.append(CONNECT_START_DATE_COND_PREFIX
					+ new Timestamp(fromDate.getTime()) + Constants.SINGLE_QUOTE);
			queryBuffer.append(CONNECT_END_DATE_COND_PREFIX
					+ new Timestamp(toDate.getTime()) + Constants.SINGLE_QUOTE);
			String whereClauseUnion = userAccessPrivilegeQueryBuilder
					.getUserAccessPrivilegeWhereConditionClause(userId,
							queryUnionPrefixMap);
			if (whereClauseUnion != null && !whereClauseUnion.isEmpty()) {
				queryBuffer.append(Constants.AND_CLAUSE + whereClauseUnion);	
		
			}
		return queryBuffer.toString();
	}

	/**
	 * This method validate the input details based on user access and
	 * privileges, and gets the connect detailed report in excel
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
	 * @return connect detailed report in excel from
	 * @throws Exception
	 */
	public InputStreamResource getConnectDetailedReport(String month,
			String quarter, String year, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines, String userId, List<String> fields)
			throws Exception {
		logger.debug("Inside getConnectDetailedReport Service");
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		List<String> geographyList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		List<String> serviceLinesList = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		// user access
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		String tillDate = DateUtils.getCurrentDate();
		List<ConnectT> connectList = new ArrayList<ConnectT>();
		UserT user = userService.findByUserId(userId);
		if (user == null) {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found: " + userId);
		} else {
			addEmptyItemToListIfGeo(geography, geographyList);
			addEmptyItemToListIfAll(iou, iouList);
			addEmptyItemToListIfAll(serviceLines, serviceLinesList);
			addEmptyItemToListIfAll(country, countryList);
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (UserGroup.contains(userGroup)) {
				// Validate user group, BDM's & BDM supervisor's are not
				// authorized for this service
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
					userIds.add(userId);
					connectList = connectRepository.findByConnectReport(
							new Timestamp(fromDate.getTime()), new Timestamp(
									toDate.getTime()), userIds, iouList,
									geographyList, countryList, serviceLinesList);
					break;
				case BDM_SUPERVISOR:
					userIds = userRepository
							.getAllSubordinatesIdBySupervisorId(user
									.getSupervisorUserId());
					userIds.add(userId);
					connectList = connectRepository.findByConnectReport(
							new Timestamp(fromDate.getTime()), new Timestamp(
									toDate.getTime()), userIds, iouList,
									geographyList, countryList, serviceLinesList);
					break;
				default:
					if (geography.contains("All")
							&& (iou.contains("All") && serviceLines
									.contains("All"))
							&& country.contains("All")) {
						connectList = getConnectDetailsBasedOnUserPrivileges(
								fromDate, toDate, userId);
					} else {
						connectList = connectRepository.findByConnectReport(
								new Timestamp(fromDate.getTime()),
								new Timestamp(toDate.getTime()), iouList,
								geographyList, countryList, serviceLinesList);
					}
					break;
				}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid User Group");
			}
			if (connectList != null) {
				connectDetailedReportService.getConnectTitlePage(workbook,
						geography, iou, serviceLines, userId, tillDate, country, month, quarter, year, "Detailed");
				getConnectDetailedReportInExcel(connectList, iouList,
						geographyList, countryList, serviceLinesList, fields,
						workbook);
			} else {
				logger.error("NOT_FOUND: Report could not be downloaded, as no connects are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no connects are available for user selection and privilege combination");
			}
			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(bytes));
			return inputStreamResource;
		}
	}

	/**
	 * This method
	 * 
	 * @param month
	 * @param quarter
	 * @param year
	 * @param connectList
	 * @param iouList
	 * @param geographyList
	 * @param country
	 * @param serviceLines
	 * @param userIds
	 * @param fields
	 * @param workbook
	 * @throws Exception
	 */
	private void getConnectDetailedReportInExcel(List<ConnectT> connectList,
			List<String> iouList, List<String> geographyList,
			List<String> country, List<String> serviceLines,
			List<String> fields, SXSSFWorkbook workbook) throws Exception {
		logger.debug("Inside connectDetailedReportInExcel Service");
		if (connectList.isEmpty() || connectList == null) {
			logger.error("NOT_FOUND: Connects Not Found");
			throw new DestinationException(HttpStatus.NOT_FOUND, "Connects Not Found");
		} else {
			connectDetailedReportService.getConnectDetailedReport(connectList,
					fields, workbook);
		}
	}

	/**
	 * This method validate the input details based on user access and
	 * privileges, and gets the connect summary report in excel
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
	 * @return
	 * @throws Exception
	 */
	public InputStreamResource connectSummaryReport(String month,
			String quarter, String year, List<String> iou,
			List<String> geography, List<String> country,
			List<String> serviceLines, String userId, List<String> fields)
			throws Exception {
		logger.debug("Inside connectSummaryReport() method");
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		List<String> geographyList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		List<String> serviceLinesList = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		// user access
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		String tillDate = DateUtils.getCurrentDate();
		List<Object[]> subSpConnectCountList = new ArrayList<Object[]>();
		List<Object[]> geographyConnectCountList = new ArrayList<Object[]>();
		List<Object[]> iouConnectCountList = new ArrayList<Object[]>();
		UserT user = userService.findByUserId(userId);
		if (user == null) {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found: " + userId);
		} else {
			addEmptyItemToListIfGeo(geography, geographyList);
			addEmptyItemToListIfAll(iou, iouList);
			addEmptyItemToListIfAll(serviceLines, serviceLinesList);
			addEmptyItemToListIfAll(country, countryList);
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (UserGroup.contains(userGroup)) {
				// Validate user group, BDM's & BDM supervisor's are not
				// authorized for this service
				switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
				case BDM:
					userIds.add(userId);
					getConnectSummaryDetailsByUserIds(userIds, fromDate,
							toDate, subSpConnectCountList,
							geographyConnectCountList, iouConnectCountList, iouList,
							geographyList, countryList,
							serviceLinesList);
					break;
				case BDM_SUPERVISOR:
					userIds = userRepository
							.getAllSubordinatesIdBySupervisorId(user
									.getSupervisorUserId());
					userIds.add(userId);
					getConnectSummaryDetailsByUserIds(userIds, fromDate,
							toDate, subSpConnectCountList,
							geographyConnectCountList, iouConnectCountList, iouList,
							geographyList, countryList,
							serviceLinesList);
					break;
				default:
					if (geography.contains("All")
							&& (iou.contains("All") && serviceLines
									.contains("All"))
							&& country.contains("All")) {

						geographyConnectCountList = getConnectGeoSummaryDetails(userId, fromDate, toDate);
						iouConnectCountList = getConnectIouSummaryDetails(userId, fromDate, toDate);
						subSpConnectCountList = getConnectSubSpSummaryDetails(userId, fromDate, toDate);
					} else {
						subSpConnectCountList = connectRepository
								.findBySubSpConnectSummaryReport(new Timestamp(
										fromDate.getTime()), new Timestamp(
										toDate.getTime()), iouList,
										geographyList, countryList,
										serviceLinesList);
						geographyConnectCountList = connectRepository
								.findByGeographyConnectSummaryReport(
										new Timestamp(fromDate.getTime()),
										new Timestamp(toDate.getTime()),
										iouList, geographyList, countryList,
										serviceLinesList);
						iouConnectCountList = connectRepository
								.findByIouConnectSummaryReport(new Timestamp(
										fromDate.getTime()), new Timestamp(
										toDate.getTime()), iouList,
										geographyList, countryList,
										serviceLinesList);
					}
					break;
				}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Invalid User Group");
			}
			if (subSpConnectCountList != null
					&& geographyConnectCountList != null
					&& iouConnectCountList != null) {
				connectDetailedReportService.getConnectTitlePage(workbook,
						geography, iou, serviceLines, userId, tillDate, country, month, quarter, year, "Summary");
				getConnectSummaryReportExcel(month, quarter, year,
						subSpConnectCountList, geographyConnectCountList,
						iouConnectCountList, country, fields, workbook);
			} else {
				logger.error("NOT_FOUND: Report could not be downloaded, as no connects are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no connects are available for user selection and privilege combination");
			}
			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			InputStreamResource inputStreamResource = new InputStreamResource(
					new ByteArrayInputStream(bytes));
			return inputStreamResource;
		}
	}

	private List<Object[]> getConnectSubSpSummaryDetails(String userId,
			Date fromDate, Date toDate) throws Exception {
		String subSpQueryString = getConnectSubSpSummaryQueryString(
				userId, fromDate, toDate);
		logger.info("SUBSP Query string: {}", subSpQueryString);
		// Execute the native revenue query string
		Query connectSubSpSummaryReportQuery = entityManager
				.createNativeQuery(subSpQueryString);
		return connectSubSpSummaryReportQuery
				.getResultList();
	}

	private List<Object[]> getConnectIouSummaryDetails(String userId,
			Date fromDate, Date toDate) throws Exception {
		String iouQueryString = getConnectIouSummaryQueryString(
				userId, fromDate, toDate);
		logger.info("IOU Query string: {}", iouQueryString);
		// Execute the native revenue query string
		Query connectIouSummaryReportQuery = entityManager
				.createNativeQuery(iouQueryString);
		return connectIouSummaryReportQuery.getResultList();
	}

	private List<Object[]> getConnectGeoSummaryDetails(String userId, Date fromDate, Date toDate) throws Exception {
		// Form the native top revenue query string
		String geoQueryString = getConnectGeoSummaryQueryString(userId, fromDate, toDate);
		logger.info("GEO Query string: {}", geoQueryString);
		// Execute the native revenue query string
		Query connectGeoSummaryReportQuery = entityManager
				.createNativeQuery(geoQueryString);
		return connectGeoSummaryReportQuery.getResultList();
	}

	public void getConnectSummaryDetailsByUserIds(List<String> userIds,
			Date fromDate, Date toDate, List<Object[]> subSpConnectCountList,
			List<Object[]> geographyConnectCountList,
			List<Object[]> iouConnectCountList, List<String> iouList, List<String> geographyList, List<String> countryList, List<String> serviceLinesList) {
		subSpConnectCountList.addAll(connectRepository
				.findBySubSpConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), userIds, iouList,
						geographyList, countryList,
						serviceLinesList));
		geographyConnectCountList.addAll(connectRepository
				.findByGeographyConnectSummaryReport(
						new Timestamp(fromDate.getTime()),
						new Timestamp(toDate.getTime()), userIds, iouList,
						geographyList, countryList,
						serviceLinesList));
		iouConnectCountList.addAll(connectRepository.findByIouConnectSummaryReport(
				new Timestamp(fromDate.getTime()),
				new Timestamp(toDate.getTime()), userIds, iouList,
				geographyList, countryList,
				serviceLinesList));
	}

	/**
	 * This method
	 * 
	 * @param month
	 * @param quarter
	 * @param year
	 * @param iouList
	 * @param geographyList
	 * @param country
	 * @param serviceLines
	 * @param userId
	 * @param fields
	 * @param workbook
	 * @throws Exception
	 */
	public void getConnectSummaryReportExcel(String month, String quarter,
			String year, List<Object[]> subSpConnectCountList,
			List<Object[]> geographyConnectCountList,
			List<Object[]> iouConnectCountList, List<String> country,
			List<String> fields, SXSSFWorkbook workbook) throws Exception {
		logger.debug("Inside ConnectSummaryReportExcel() method");
		if ((subSpConnectCountList.isEmpty() || subSpConnectCountList == null)
				&& (geographyConnectCountList.isEmpty() || geographyConnectCountList == null)
				&& (iouConnectCountList.isEmpty() || iouConnectCountList == null)) {
			logger.error("Connects Not Found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Connects Not Found");
		} else {
			connectSummaryReportService.getSummaryReport(subSpConnectCountList,
					geographyConnectCountList, iouConnectCountList, month,
					quarter, year, workbook);
		}
	}

	public InputStreamResource getConnectReports(String month, String quarter,
			String year, List<String> iou, List<String> geography,
			List<String> country, List<String> serviceLines, String userId,
			List<String> fields) throws Exception {
		logger.debug("Inside getConnectReports() method");
		SXSSFWorkbook workbook = new SXSSFWorkbook(50);
		List<String> geographyList = new ArrayList<String>();
		List<String> iouList = new ArrayList<String>();
		List<String> serviceLinesList = new ArrayList<String>();
		List<String> countryList = new ArrayList<String>();
		List<String> userIds = new ArrayList<String>();
		// user access
		Date fromDate = DateUtils.getDate(month, quarter, year, true);
		Date toDate = DateUtils.getDate(month, quarter, year, false);
		String tillDate =DateUtils.getCurrentDate();
		List<Object[]> subSpConnectCountList = new ArrayList<Object[]>();
		List<Object[]> geographyConnectCountList = new ArrayList<Object[]>();
		List<Object[]> iouConnectCountList = new ArrayList<Object[]>();
		List<ConnectT> connectList = new ArrayList<ConnectT>();
		UserT user = userService.findByUserId(userId);
		if (user == null) {
			logger.error("NOT_FOUND: User not found: {}", userId);
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"User not found: " + userId);
		} else {
			addEmptyItemToListIfGeo(geography, geographyList);
			addEmptyItemToListIfAll(iou, iouList);
			addEmptyItemToListIfAll(serviceLines, serviceLinesList);
			addEmptyItemToListIfAll(country, countryList);
			String userGroup = user.getUserGroupMappingT().getUserGroup();
			if (UserGroup.contains(userGroup)) {
			// Validate user group, BDM's & BDM supervisor's are not
			// authorized for this service
			switch (UserGroup.valueOf(UserGroup.getName(userGroup))) {
			case BDM:
				userIds.add(userId);
				connectList = connectRepository.findByConnectReport(
						new Timestamp(fromDate.getTime()), new Timestamp(
								toDate.getTime()), userIds,  iouList,
								geographyList, countryList, serviceLinesList);
				getConnectSummaryDetailsByUserIds(userIds, fromDate,
						toDate, subSpConnectCountList,
						geographyConnectCountList, iouConnectCountList, iouList,
						geographyList, countryList, serviceLinesList);
				break;
			case BDM_SUPERVISOR:
				userIds = userRepository
						.getAllSubordinatesIdBySupervisorId(user
								.getSupervisorUserId());
				userIds.add(userId);
				connectList = connectRepository.findByConnectReport(
						new Timestamp(fromDate.getTime()), new Timestamp(
								toDate.getTime()), userIds, iouList,
								geographyList, countryList, serviceLinesList);
				getConnectSummaryDetailsByUserIds(userIds, fromDate,
						toDate, subSpConnectCountList,
						geographyConnectCountList, iouConnectCountList, iouList,
						geographyList, countryList, serviceLinesList);
				break;
			default:
				if (geography.contains("All") && (iou.contains("All") && serviceLines.contains("All"))&& country.contains("All")) {
					connectList = getConnectDetailsBasedOnUserPrivileges(fromDate, toDate, userId);
					geographyConnectCountList = getConnectGeoSummaryDetails(userId, fromDate, toDate);
					iouConnectCountList = getConnectIouSummaryDetails(userId, fromDate, toDate);
					subSpConnectCountList = getConnectSubSpSummaryDetails(userId, fromDate, toDate);

				} else {
					connectList = connectRepository.findByConnectReport(new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), iouList,
							geographyList, countryList, serviceLinesList);
					subSpConnectCountList = connectRepository.findBySubSpConnectSummaryReport(new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), iouList,
									geographyList, countryList, serviceLinesList);
					geographyConnectCountList = connectRepository.findByGeographyConnectSummaryReport(new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), iouList,
									geographyList, countryList, serviceLinesList);
					iouConnectCountList = connectRepository.findByIouConnectSummaryReport(new Timestamp(fromDate.getTime()), new Timestamp(toDate.getTime()), iouList,
									geographyList, countryList, serviceLinesList);
				}
				break;
			}
			} else {
				logger.error("Invalid User Group: {}", userGroup);
				throw new DestinationException(HttpStatus.BAD_REQUEST, "Invalid User Group");
			}
			if (connectList != null && subSpConnectCountList != null
					&& geographyConnectCountList != null
					&& iouConnectCountList != null) {
				connectDetailedReportService.getConnectTitlePage(workbook, geography, iou, serviceLines, userId, tillDate, country, month, quarter, year, "Summary, Detailed");
				getConnectSummaryReportExcel(month, quarter, year, subSpConnectCountList, geographyConnectCountList,
						iouConnectCountList, country, fields, workbook);
				getConnectDetailedReportInExcel(connectList, iouList, geographyList, country, serviceLines, fields, workbook);
			} else {
				logger.error("NOT_FOUND: Report could not be downloaded, as no connects are available for user selection and privilege combination");
				throw new DestinationException(HttpStatus.NOT_FOUND, "Report could not be downloaded, as no connects are available for user selection and privilege combination");
			}
			ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
			workbook.write(byteOutPutStream);
			byteOutPutStream.flush();
			byteOutPutStream.close();
			byte[] bytes = byteOutPutStream.toByteArray();
			InputStreamResource inputStreamResource = new InputStreamResource(new ByteArrayInputStream(bytes));
			return inputStreamResource;
		}
	}

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
				logger.error("User is not authorized to access this service");
				throw new DestinationException(HttpStatus.UNAUTHORIZED,	"User is not authorised to access this service");
			default:
				addEmptyItemToListIfGeo(geography, geographyList);
				addEmptyItemToListIfAll(iou, iouList);
				addEmptyItemToListIfAll(serviceLines, serviceLinesList);
				addEmptyItemToListIfAll(country, countryList);
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

	private List<BidDetailsT> getBidDetailsBasedOnUserPrivileges(Date startDate, Date endDate, String userId, List<String> bidOwner,
			List<String> geographyList, List<String> iouList, List<String> serviceLinesList, List<String> countryList) throws Exception {
		logger.debug("Inside getBidDetailsBasedOnUserPrivileges() method");
		// Form the native top revenue query string
//		String queryString = getBidDetailedQueryString(userId, startDate, endDate, bidOwner);
		String queryString = getBidDetailedQueryString(userId, startDate, endDate, bidOwner, geographyList, iouList, serviceLinesList, countryList);
		logger.info("Query string: {}", queryString);
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
			String bidOwners = getStringListWithSingleQuotes(bidOwner);
			queryBuffer.append(Constants.AND_CLAUSE
					+ BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX + bidOwners + ")"+ Constants.OR_CLAUSE + "('') in (" + bidOwners + "))");
		}
		if(!geographyList.contains("") && geographyList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + GEO_COND_PREFIX +getStringListWithSingleQuotes(geographyList)+ Constants.RIGHT_PARANTHESIS);
		}
		if(!iouList.contains("") && iouList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + IOU_COND_PREFIX +getStringListWithSingleQuotes(iouList)+ Constants.RIGHT_PARANTHESIS);
		}
		if(!serviceLinesList.contains("") && serviceLinesList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + SUBSP_COND_PREFIX +getStringListWithSingleQuotes(serviceLinesList)+ Constants.RIGHT_PARANTHESIS);
		}
		if(!countryList.contains("") && countryList!=null){
			queryBuffer.append(Constants.AND_CLAUSE + COUNTRY_COND_PREFIX +getStringListWithSingleQuotes(countryList)+ Constants.RIGHT_PARANTHESIS);
		}
		String whereClause = userAccessPrivilegeQueryBuilder.getUserAccessPrivilegeWhereConditionClause(userId, queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		return queryBuffer.toString();
	}

	private List<BidDetailsT> getBidDetailsBasedOnUserPrivileges(
			Date startDate, Date endDate, String userId, List<String> bidOwner)
			throws Exception {
		logger.debug("Inside getBidDetailsBasedOnUserPrivileges() method");
		// Form the native top revenue query string
		String queryString = getBidDetailedQueryString(userId, startDate,
				endDate, bidOwner);
		logger.info("Query string: {}", queryString);
		// Execute the native revenue query string
		Query bidDetailedReportQuery = entityManager
				.createNativeQuery(queryString);
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

	private String getBidDetailedQueryString(String userId, Date startDate,
			Date endDate, List<String> bidOwner) throws Exception {
		logger.debug("Inside getRevenueQueryString() method");
		StringBuffer queryBuffer = new StringBuffer(BID_REPORT_QUERY_PREFIX);
		// Get user access privilege groups
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, null, IOU_COND_PREFIX, null);
		// Get WHERE clause string
		queryBuffer.append(BID_START_DATE_COND_PREFIX
				+ new Timestamp(startDate.getTime()) + Constants.SINGLE_QUOTE);
		queryBuffer.append(BID_END_DATE_COND_C_PREFIX
				+ new Timestamp(endDate.getTime()) + Constants.SINGLE_QUOTE);
		if (bidOwner.isEmpty() || bidOwner.size() == 0) {
			queryBuffer.append(Constants.AND_CLAUSE	+ BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX + "''" + ")"
					+ Constants.OR_CLAUSE + "('')" + " in" + "(''))");
		} else {
			String bidOwners = getStringListWithSingleQuotes(bidOwner);
			queryBuffer.append(Constants.AND_CLAUSE
					+ BID_OFFICE_GROUP_OWNEER_COND_B_PREFIX + bidOwners + ")"+ Constants.OR_CLAUSE + "('') in (" + bidOwners
					+ "))");
		}
		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		return queryBuffer.toString();
	}

	public InputStreamResource getBdmDetailedReport(String from, String to, List<String> geography, List<String> country,
			List<String> currency, List<String> serviceLines, List<Integer> salesStage, List<String> opportunityOwnerIds,
			String supervisorId) {
		// TODO Auto-generated method stub
		return null;
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
//				String salesStageCode = Joiner.on("\',\'").join(salesStage);
//				if (!salesStage.isEmpty()) {
//					salesStageCode = "\'" + salesStageCode + "\'";
//				}
				queryBuffer.append(Constants.SPACE+ Constants.AND_CLAUSE +OPPORTUNITY_SALES_STAGE_COND_PREFIX +salesStage.toString().replace("[", "").replace("]", "")+Constants.RIGHT_PARANTHESIS);
				return queryBuffer.toString();
		}
		
		// Detailed report ends here
		
		// Win Loss Service line
		
		public String getOpportunityServiceLineSummaryQueryString(String userId, Date fromDate,
				Date toDate, Integer salesStage)throws Exception {
			logger.debug("Inside getOpportunityServiceLineSummaryQueryString() method" );
			StringBuffer queryBuffer = new StringBuffer(OPPORTUNITY_SUMMARY_SUBSP_QUERY_PREFIX);
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
				queryBuffer.append(Constants.SPACE+ OPPORTUNITY_SUBSP_GROUP_BY_COND_PREFIX);
				return queryBuffer.toString();
		}
		
		//
		
		// Win Loss Geography
		
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
		
		//
		
		// Win Loss Iou
		
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
			
			//
			
			// Anticipating or Pipeline Geography
			
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
						ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
						workbook.write(byteOutPutStream);
						byteOutPutStream.flush();
						byteOutPutStream.close();
						byte[] bytes = byteOutPutStream.toByteArray();
						InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
						return inputStreamResource;
					}
					

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
						ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
						workbook.write(byteOutPutStream);
						byteOutPutStream.flush();
						byteOutPutStream.close();
						byte[] bytes = byteOutPutStream.toByteArray();
						InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
						return inputStreamResource;
					}
					

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
						ByteArrayOutputStream byteOutPutStream = new ByteArrayOutputStream();
						workbook.write(byteOutPutStream);
						byteOutPutStream.flush();
						byteOutPutStream.close();
						byte[] bytes = byteOutPutStream.toByteArray();
						InputStreamResource inputStreamResource= new InputStreamResource(new ByteArrayInputStream(bytes));
						return inputStreamResource;
					}
					
					
					public void removeUnwantedSalesStageCodes(List<Integer> salesStageList, List<Integer> salesStageCodeList) {
						for(Integer salesStage:salesStageList){
							if(!salesStage.equals(11) && !salesStage.equals(12) && !salesStage.equals(13)){
								salesStageCodeList.add(salesStage);
							}
						}
					}

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
						addItemToList(iou,iouList);
						addItemToList(country,countryList);
						addItemToList(serviceLines,serviceLinesList);
						// ADD USERIDS HERE ITSELF
						UserT user = userRepository.findByUserId(userId);
						if(user == null){
							logger.error("User Id Not Found "+ userId );
							throw new DestinationException(HttpStatus.NOT_FOUND,"User Id Not Found");
						}
						String userGroup = user.getUserGroupMappingT().getUserGroup();
						switch (userGroup) {
						case ReportConstants.BDM:
							userIds.add(userId);
							break;
						case ReportConstants.BDMSUPERVISOR:
							List<String> subOrdinatesList =userRepository.getAllSubordinatesIdBySupervisorId(userId);
							userIds.addAll(subOrdinatesList);
							if(!userIds.contains(userId)){
								userIds.add(userId);
							}
							break;
						}

						for (int i = 0; i <salesStageList.size();) {
							if (salesStageList.get(i) < 9) {
								switch (userGroup) {
								case ReportConstants.BDM:
									opportunityList = opportunityRepository.findSummaryGeographyByRole(salesStageList.get(i), userIds, geoList, countryList, iouList, serviceLinesList);
									break;
								case ReportConstants.BDMSUPERVISOR:
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
									switch (userGroup) {
									case ReportConstants.BDM:
										opportunityList = opportunityRepository.findSummaryIouByRole(salesStageList.get(i), userIds, geoList, countryList, iouList, serviceLinesList);
										break;
									case ReportConstants.BDMSUPERVISOR:
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