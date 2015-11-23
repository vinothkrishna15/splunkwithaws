package com.tcs.destination.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.FrequentlySearchedGroupCustomersT;
import com.tcs.destination.bean.ActualRevenuesDataT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.FrequentlySearchedGroupCustomersTPK;
import com.tcs.destination.bean.GeographyReport;
import com.tcs.destination.bean.IOUReport;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.ReportsOpportunity;
import com.tcs.destination.bean.ReportsSalesStage;
import com.tcs.destination.bean.SalesStageMappingT;
import com.tcs.destination.bean.SubSpReport;
import com.tcs.destination.bean.TargetVsActualResponse;
import com.tcs.destination.bean.UserTaggedFollowedT;
import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.ActualRevenuesDataTRepository;
import com.tcs.destination.data.repository.BeaconDataTRepository;
import com.tcs.destination.data.repository.CustomerRepository;
import com.tcs.destination.data.repository.FrequentlySearchedGroupCustomerTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.data.repository.PerformanceReportRepository;
import com.tcs.destination.data.repository.ProjectedRevenuesDataTRepository;
import com.tcs.destination.data.repository.SalesStageMappingRepository;
import com.tcs.destination.enums.UserGroup;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.helper.UserAccessPrivilegeQueryBuilder;
import com.tcs.destination.utils.Constants;
import com.tcs.destination.utils.DateUtils;
import com.tcs.destination.utils.DestinationUtils;

@Service
public class PerformanceReportService {

	private static final Logger logger = LoggerFactory
			.getLogger(PerformanceReportService.class);

	private static final BigDecimal ZERO_REVENUE = new BigDecimal("0.0");

	@Autowired
	private PerformanceReportRepository perfRepo;

	@Autowired
	private OpportunityRepository opportunityRepository;

	@Autowired
	BeaconConverterService beaconService;

	@Autowired
	ActualRevenuesDataTRepository actualsRepository;

	@Autowired
	ProjectedRevenuesDataTRepository projectedRepository;

	@Autowired
	BeaconDataTRepository beaconDataTRepository;

	@Autowired
	SalesStageMappingRepository salesStageMappingRepository;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	FrequentlySearchedGroupCustomerTRepository frequentlySearchedGroupCustomerTRepository;

	@Autowired
	UserAccessPrivilegeQueryBuilder userAccessPrivilegeQueryBuilder;

	@Autowired
	UserService userService;

	@PersistenceContext
	private EntityManager entityManager;

	private static final String ACTUAL_REVENUE_QUERY_PREFIX = "select ARDT.quarter, case when sum(ARDT.revenue) is not null then sum(ARDT.revenue)"
			+ " else '0.0' end as actual_revenue from actual_revenues_data_t ARDT"
			+ " join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography"
			+ " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou"
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp"
			+ " join revenue_customer_mapping_t RCMT on (ARDT.finance_customer_name = RCMT.finance_customer_name"
			+ " and ARDT.finance_geography = RCMT.customer_geography and RCMT.finance_iou =ARDT.finance_iou)"
			+ " where ";

	private static final String ACTUAL_REVENUE_QUERY_COND_SUFFIX = "(ARDT.finance_geography = (:geography) or (:geography) = '') "
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography)='')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '')"
			+ " and (RCMT.customer_name in (:customerName) or ('') in (:customerName))"
			+ " and ARDT.financial_year = (:financialYear) and (ARDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography) = '')";

	private static final String ACTUAL_REVENUE_QUERY_GROUP_BY_ORDER_BY = " group by ARDT.quarter order by ARDT.quarter asc ";

	private static final String ACTUAL_REVENUE_BY_QUARTER_QUERY_PREFIX = "select upper(ARDT.month), case when sum(ARDT.revenue) is not null then sum(ARDT.revenue)"
			+ " else '0.0' end as actual_revenue from actual_revenues_data_t ARDT"
			+ " join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography"
			+ " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ " join revenue_customer_mapping_t RCMT on (ARDT.finance_customer_name = RCMT.finance_customer_name"
			+ " and ARDT.finance_geography = RCMT.customer_geography and RCMT.finance_iou =ARDT.finance_iou) "
			+ " where ";

	private static final String ACTUAL_REVENUE_QUERY_BY_QUARTER_COND_SUFFIX = "(GMT.geography = (:geography) or (:geography) = '') "
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography)='')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '')"
			+ " and (RCMT.customer_name in (:customerName) or ('') in (:customerName))"
			+ " and ARDT.financial_year = (:financialYear) and (ARDT.quarter = (:quarter) or (:quarter) = '')";

	private static final String ACTUAL_REVENUE_BY_QUARTER_QUERY_GROUP_BY_ORDER_BY = " group by ARDT.month order by ARDT.month asc ";

	private static final String PROJECTED_REVENUE_QUERY_PREFIX = "select PRDT.quarter, case when sum(PRDT.revenue) is not null then sum(PRDT.revenue)"
			+ " else '0.0' end as projected_revenue from projected_revenues_data_t PRDT"
			+ " join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography"
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou"
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp"
			+ " join revenue_customer_mapping_t RCMT on (PRDT.finance_customer_name = RCMT.finance_customer_name"
			+ " and PRDT.finance_geography=RCMT.customer_geography)"
			+ " and RCMT.finance_iou =PRDT.finance_iou" + " where ";

	private static final String PROJECTED_REVENUE_QUERY_COND_SUFFIX = " (PRDT.finance_geography = (:geography) or (:geography) = '')"
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography)='')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '')"
			+ " and (RCMT.customer_name in (:customerName) or ('') in (:customerName))"
			+ " and PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '')";

	private static final String PROJECTED_REVENUE_QUERY_GROUP_BY_ORDER_BY = " group by PRDT.quarter order by PRDT.quarter asc";

	private static final String PROJECTED_REVENUE_BY_QUARTER_QUERY_PREFIX = "select upper(PRDT.month), case when sum(PRDT.revenue) is not null then sum(PRDT.revenue) else '0.0' end as projected_revenue from projected_revenues_data_t PRDT "
			+ "join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ "join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou "
			+ "join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp  "
			+ "join revenue_customer_mapping_t RCMT on "
			+ "(PRDT.finance_customer_name = RCMT.finance_customer_name and PRDT.finance_geography=RCMT.customer_geography and RCMT.finance_iou =PRDT.finance_iou) "
			+ " where ";

	private static final String PROJECTED_REVENUE_BY_QUARTER_QUERY_COND_SUFFIX = " (GMT.geography=(:geography) or (:geography)='')"
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography)='') "
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '')"
			+ " and (RCMT.customer_name  in (:customerName) or ('') in (:customerName))"
			+ " and PRDT.financial_year = (:financialYear) and (PRDT.quarter = (:quarter) or (:quarter) = '') ";

	private static final String PROJECTED_REVENUE_BY_QUARTER_QUERY_GROUP_BY_ORDER_BY = " group by PRDT.month order by PRDT.month asc ";

	private static final String TARGET_REVENUE_QUERY_PREFIX = "select BDT.quarter, case when sum(BDT.target) is not null then sum(BDT.target) else '0.0' end as target from beacon_data_t BDT "
			+ "join iou_customer_mapping_t ICMT on BDT.beacon_iou = ICMT.iou "
			+ "join beacon_customer_mapping_t BCMT on (BDT.beacon_customer_name = BCMT.beacon_customer_name and BDT.beacon_geography = BCMT.customer_geography and BDT.beacon_iou = BCMT.beacon_iou) "
			+ "join geography_mapping_t GMT on BDT.beacon_geography = GMT.geography  "
			+ "where ";
	private static final String TARGET_REVENUE_QUERY_COND_SUFFIX = " (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (BCMT.customer_name in (:customerName) or ('') in (:customerName))"
			+ " and (BCMT.customer_geography = (:geography) or (:geography)= '')"
			+ " and (GMT.display_geography=(:displayGeography) or (:displayGeography)='')"
			+ " and BDT.financial_year = (:financialYear) and (BDT.quarter = (:quarter) or (:quarter) = '')";

	private static final String TARGET_REVENUE_QUERY_GROUP_BY_ORDER_BY = " group by BDT.quarter order by BDT.quarter asc";

	private static final String DIGITAL_DEAL_VALUE_QUERY_PREFIX = "select OPP.deal_closure_date, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currencyName)))  as digitalDealCalue from opportunity_t OPP "
			+ "join geography_country_mapping_t GCMT on GCMT.country=OPP.country "
			+ "join geography_mapping_t GMT on GMT.geography=GCMT.geography "
			+ "left join opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id=OPP.opportunity_id "
			+ "left JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN customer_master_t CMT on OPP.customer_id = CMT.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where ";

	private static final String DIGITAL_DEAL_VALUE_QUERY_COND_SUFFIX = " (GCMT.geography=(:geography) or (:geography) = '')"
			+ " and (GMT.display_geography=(:displayGeography) or (:displayGeography)='')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '')"
			+ " and (CMT.customer_name in (:customerName) or ('') in (:customerName) )"
			+ " and (ICMT.display_iou = (:iou) OR (:iou) = '')"
			+ " and OPP.deal_closure_date between (:fromDate) and (:toDate) and OPP.sales_stage_code=9 ";

	private static final String DIGITAL_DEAL_VALUE_QUERY_GROUP_BY = " group by OPP.deal_closure_date";

	private static final String PIPELINE_PERFORMANCE_BY_IOU_PREFIX = "select ICMT.display_iou, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where ";

	private static final String PIPELINE_PERFORMANCE_BY_IOU_COND_SUFFIX = " (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ " and (GMT.display_geography = (:displayGeography) OR (:displayGeography) = '')"
			+ " and (GMT.geography=(:geography) OR (:geography) = '')"
			+ " and OPP.digital_deal_value <> 0 "
			+ " AND ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) "
			+ " AND (OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo))";

	private static final String PIPELINE_PERFORMANCE_BY_IOU_GROUP_BY_ORDER_BY = " group by ICMT.display_iou order by ICMT.display_iou";

	private static final String ACTUAL_REVENUES_BY_IOU_QUERY_PREFIX = "select distinct ICMT.display_iou as displayIOU, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from iou_customer_mapping_t ICMT left outer join";

	private static final String ACTUAL_REVENUES_BY_IOU_INNER_QUERY_PREFIX = " (select ICMT.display_iou as displayIOU, sum(ARDT.revenue) as actualRevenue"
			+ " from iou_customer_mapping_t ICMT join actual_revenues_data_t ARDT on ICMT.iou = ARDT.finance_iou"
			+ " join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography"
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp"
			+ " where ";

	private static final String ACTUAL_REVENUES_BY_IOU_INNER_QUERY_COND_SUFFIX = " (ARDT.financial_year = (:financialYear) or (:financialYear) ='')"
			+ " and (ARDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography) = '')"
			+ " and (GMT.geography = (:geography) or (:geography) = '')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '')";

	private static final String ACTUAL_REVENUES_BY_IOU_INNER_QUERY_GROUP_BY_ORDER_BY = " group by ICMT.display_iou order by actualRevenue desc)";

	private static final String ACTUAL_REVENUES_BY_IOU_ORDER_BY = " Result on ICMT.display_iou = Result.displayIOU order by revenue desc";

	private static final String PROJECTED_REVENUES_BY_IOU = "select distinct ICMT.display_iou as displayIOU, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from iou_customer_mapping_t ICMT left outer join";

	private static final String PROJECTED_REVENUES_BY_IOU_INNER_QUERY_PREFIX = " (select ICMT.display_iou as displayIOU, sum(PRDT.revenue) as actualRevenue"
			+ " from iou_customer_mapping_t ICMT join projected_revenues_data_t PRDT on ICMT.iou = PRDT.finance_iou"
			+ " join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ " where ";

	private static final String PROJECTED_REVENUES_BY_IOU_INNER_QUERY_COND_SUFFIX = " (PRDT.financial_year = (:financialYear) or (:financialYear) = '')"
			+ " and (PRDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography) = '')"
			+ " and (GMT.geography = (:geography) or (:geography) = '')"
			+ " and (SSMT.display_sub_sp = (:serviceLine) or (:serviceLine) = '')";

	private static final String PROJECTED_REVENUES_BY_IOU_INNER_QUERY_GROUP_BY_ORDER_BY = " group by ICMT.display_iou order by actualRevenue desc)";

	private static final String PROJECTED_REVENUES_BY_IOU_ORDER_BY = " Result on ICMT.display_iou = Result.displayIOU order by revenue desc";

	private static final String PIPELINE_PERFORMANCE_BY_SERVICE_LINE_QUERY_PREFIX = "select COALESCE(SSMT.display_sub_sp, 'SubSp Not Defined') ,sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as OBV from opportunity_t OPP "
			+ "LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography  "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou  "
			+ "where ";
	private static final String PIPELINE_PERFORMANCE_BY_SERVICE_LINE_COND_SUFFIX = "(GMT.display_geography = (:displayGeography) OR (:displayGeography) = '') "
			+ "and (GMT.geography = (:geography) OR (:geography) = '') "
			+ "and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ "and OPP.digital_deal_value <> 0 and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo) "
			+ "and ((OPP.deal_closure_date between (:fromDate)  and (:toDate) "
			+ "and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9)";

	private static final String PIPELINE_PERFORMANCE_BY_SERVICE_LINE_GROUP_BY_ORDER_BY = " group by SSMT.display_sub_sp order by SSMT.display_sub_sp";

	private static final String ACTUAL_REVENUES_BY_SUBSP_QUERY_PREFIX = "select distinct SSMT.display_sub_sp as displaySubSp, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from sub_sp_mapping_t SSMT left outer join";

	private static final String ACTUAL_REVENUES_BY_SUBSP_INNER_QUERY_PREFIX = " (select SSMT.display_sub_sp as displaySubSp, sum(ARDT.revenue) as actualRevenue"
			+ " from sub_sp_mapping_t SSMT join actual_revenues_data_t ARDT on SSMT.actual_sub_sp = ARDT.sub_sp"
			+ " join geography_mapping_t GMT on ARDT.finance_geography = GMT.geography "
			+ " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and ARDT.finance_geography = RCMT.customer_geography and RCMT.finance_iou =ARDT.finance_iou"
			+ " where";

	private static final String ACTUAL_REVENUES_BY_SUBSP_INNER_QUERY_COND_SUFFIX = " (ARDT.financial_year = (:financialYear) or (:financialYear) = '')"
			+ " and (ARDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (GMT.display_geography = (:displayGeography) or (:displayGeography) = '')"
			+ " and (GMT.geography=(:geography) or (:geography)='')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (RCMT.customer_name in (:customerName) or ('') in (:customerName))";

	private static final String ACTUAL_REVENUES_BY_SUBSP_INNER_QUERY_GROUP_BY_ORDER_BY = " group by SSMT.display_sub_sp order by actualRevenue desc)";

	private static final String ACTUAL_REVENUES_BY_SUBSP_ORDER_BY = " Result on SSMT.display_sub_sp = Result.displaySubSp order by revenue desc";

	private static final String PROJECTED_REVENUES_BY_SUBSP_QUERY_PREFIX = "select distinct SSMT.display_sub_sp as displaySubSp, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from sub_sp_mapping_t SSMT left outer join";

	private static final String PROJECTED_REVENUES_BY_SUBSP_INNER_QUERY_PREFIX = " (select SSMT.display_sub_sp as displaySubSp, sum(PRDT.revenue) as actualRevenue"
			+ " from sub_sp_mapping_t SSMT join projected_revenues_data_t PRDT on SSMT.actual_sub_sp = PRDT.sub_sp"
			+ " join geography_mapping_t GMT on PRDT.finance_geography = GMT.geography "
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou "
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography"
			+ " where";

	private static final String PROJECTED_REVENUES_BY_SUBSP_INNER_QUERY_COND_SUFFIX = " (PRDT.financial_year = (:financialYear) or (:financialYear) = '')"
			+ " and (PRDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (PRDT.finance_geography = (:geography) or (:geography) = '')"
			+ " and (GMT.display_geography=(:displayGeography) or (:displayGeography) ='')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (RCMT.customer_name in (:customerName) or ('') in (:customerName))";

	private static final String PROJECTED_REVENUES_BY_SUBSP_INNER_QUERY_GROUP_BY_ORDER_BY = " group by SSMT.display_sub_sp order by actualRevenue desc)";

	private static final String PROJECTED_REVENUES_BY_SUBSP_ORDER_BY = " Result on SSMT.display_sub_sp = Result.displaySubSp order by revenue desc";

	private static final String PIPELINE_PERFORMANCE_BY_GEO_QUERY_PREFIX = "select GMT.display_geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where ";

	private static final String PIPELINE_PERFORMANCE_BY_GEO_COND_SUFFIX = " (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '')"
			+ " and (ICMT.display_iou = (:iou) OR (:iou) = '') "
			+ " and OPP.digital_deal_value <> 0 and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)"
			+ " and ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) ";

	private static final String PIPELINE_PERFORMANCE_BY_GEO_GROUP_BY_ORDER_BY = "group by GMT.display_geography order by GMT.display_geography";

	private static final String ACTUAL_REVENUES_BY_GEO_QUERY_PREFIX = "select distinct GMT.display_geography as displayGeography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from geography_mapping_t GMT left outer join";

	private static final String ACTUAL_REVENUES_BY_GEO_INNER_QUERY_PREFIX = " (select GMT.display_geography as displayGeography, sum(ARDT.revenue) as actualRevenue"
			+ " from geography_mapping_t GMT join actual_revenues_data_t ARDT on GMT.geography = ARDT.finance_geography"
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name"
			+ " and ARDT.finance_geography = RCMT.customer_geography "
			+ " and RCMT.finance_iou =ARDT.finance_iou" + " where";

	private static final String ACTUAL_REVENUES_BY_GEO_INNER_QUERY_COND_SUFFIX = " (ARDT.financial_year = (:financialYear) or (:financialYear) = '')"
			+ " and (ARDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (RCMT.customer_name  in (:customer) or ('') in (:customer))";

	private static final String ACTUAL_REVENUES_BY_GEO_INNER_QUERY_GROUP_BY_ORDER_BY = " group by GMT.display_geography"
			+ " order by actualRevenue desc) ";

	private static final String ACTUAL_REVENUES_BY_GEO_ORDER_BY = " Result on GMT.display_geography = Result.displayGeography order by revenue desc";

	private static final String PROJECTED_REVENUES_BY_GEO_QUERY_PREFIX = "select distinct GMT.display_geography as displayGeography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from geography_mapping_t GMT left outer join";

	private static final String PROJECTED_REVENUES_BY_GEO_INNER_QUERY_PREFIX = " (select GMT.display_geography as displayGeography, sum(PRDT.revenue) as actualRevenue"
			+ " from geography_mapping_t GMT join projected_revenues_data_t PRDT on GMT.geography = PRDT.finance_geography"
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou "
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography"
			+ " where";

	private static final String PROJECTED_REVENUES_BY_GEO_INNER_QUERY_COND_SUFFIX = " (PRDT.financial_year = (:financialYear) or (:financialYear) = '')"
			+ " and (PRDT.quarter = (:quarter) or (:quarter) = '')"
			+ " and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (RCMT.customer_name in (:customer) or ('') in (:customer))";

	private static final String PROJECTED_REVENUES_BY_GEO_INNER_QUERY_GROUP_BY_ORDER_BY = " group by GMT.display_geography"
			+ " order by actualRevenue desc)";

	private static final String PROJECTED_REVENUES_BY_GEO_ORDER_BY = " Result on GMT.display_geography = Result.displayGeography order by revenue desc";

	private static final String PIPELINE_PERFORMANCE_BY_SUB_GEO_QUERY_PREFIX = "select GMT.geography, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp  "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where ";

	private static final String PIPELINE_PERFORMANCE_BY_SUB_GEO_COND_SUFFIX = " (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ " and (GMT.display_geography = (:geography) OR (:geography) = '')"
			+ " and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ " and (ICMT.display_iou = (:iou) OR (:iou) = '')"
			+ " and OPP.digital_deal_value <> 0 "
			+ " AND ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) "
			+ " AND (OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)) ";

	private static final String PIPELINE_PERFORMANCE_BY_SUB_GEO_GROUP_BY_ORDER_BY = " group by GMT.geography order by GMT.geography";

	private static final String PIPELINE_PERFORMANCE_BY_COUNTRY_QUERY_PREFIX = "select OPP.country, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV from opportunity_t OPP "
			+ "LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where ";

	private static final String PIPELINE_PERFORMANCE_BY_COUNTRY_COND_SUFFIX = " (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '') "
			+ " and (GCMT.geography =(:geography) OR (:geography) = '')"
			+ " and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ " and (ICMT.display_iou = (:iou) OR (:iou) = '')"
			+ " and OPP.digital_deal_value <> 0 and ((OPP.sales_stage_code >= 9 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) "
			+ " and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo) ";

	private static final String PIPELINE_PERFORMANCE_BY_COUNTRY_GROUP_BY_ORDER_BY = " group by OPP.country order by OPP.country";

	private static final String ACTUAL_REVENUES_BY_SUB_GEO_QUERY_PREFIX = "select GMT.geography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from geography_mapping_t GMT left outer join";

	private static final String ACTUAL_REVENUES_BY_SUB_GEO_INNER_QUERY_PREFIX = " (select GMT.geography as displayGeography, sum(ARDT.revenue) as actualRevenue"
			+ " from geography_mapping_t GMT left outer join actual_revenues_data_t ARDT on GMT.geography = ARDT.finance_geography"
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and ARDT.finance_geography = RCMT.customer_geography and RCMT.finance_iou =ARDT.finance_iou "
			+ " where";

	private static final String ACTUAL_REVENUES_BY_SUB_GEO_INNER_QUERY_COND_SUFFIX = " (GMT.display_geography = (:geography) or (:geography) = '')"
			+ " and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (RCMT.customer_name in (:customer) or ('') in (:customer))"
			+ " and (ARDT.financial_year = (:financialYear) or (:financialYear) = '') "
			+ " and (ARDT.quarter = (:quarter) or (:quarter) = '')";

	private static final String ACTUAL_REVENUES_BY_SUB_GEO_INNER_QUERY_GROUP_BY_ORDER_BY = " group by GMT.geography"
			+ " order by actualRevenue desc)";

	private static final String ACTUAL_REVENUES_BY_SUB_GEO_ORDER_BY = " Result on GMT.geography = Result.displayGeography"
			+ " where GMT.display_geography = (:geography) order by revenue desc";

	private static final String PROJECTED_REVENUES_BY_SUB_GEO_QUERY_PREFIX = "select GMT.geography, case when Result.actualRevenue is not null then Result.actualRevenue else '0.0' end as revenue"
			+ " from geography_mapping_t GMT left outer join";

	private static final String PROJECTED_REVENUES_BY_SUB_GEO_INNER_QUERY_PREFIX = " (select GMT.geography as displayGeography, sum(PRDT.revenue) as actualRevenue"
			+ " from geography_mapping_t GMT left outer join projected_revenues_data_t PRDT on GMT.geography = PRDT.finance_geography"
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp "
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou "
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and RCMT.finance_iou =PRDT.finance_iou and PRDT.finance_geography=RCMT.customer_geography"
			+ " where";

	private static final String PROJECTED_REVENUES_BY_SUB_GEO_INNER_QUERY_COND_SUFFIX = " (PRDT.finance_geography = (:geography) or (:geography) = '')"
			+ " and (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (RCMT.customer_name in (:customer) or ('') in (:customer))"
			+ " and (PRDT.financial_year = (:financialYear) or (:financialYear)='') "
			+ " and (PRDT.quarter = (:quarter) or (:quarter) = '')";

	private static final String PROJECTED_REVENUES_BY_SUB_GEO_INNER_QUERY_GROUP_BY_ORDER_BY = " group by GMT.geography"
			+ " order by actualRevenue desc) ";

	private static final String PROJECTED_REVENUES_BY_SUB_GEO_ORDER_BY = "Result on GMT.geography = Result.displayGeography"
			+ " where GMT.display_geography = (:geography) order by revenue desc";

	private static final String ACTUAL_REVENUES_BY_COUNTRY_QUERY_PREFIX = "select ARDT.client_country,sum(ARDT.revenue) from actual_revenues_data_t  ARDT "
			+ " join sub_sp_mapping_t SSMT on ARDT.sub_sp = SSMT.actual_sub_sp "
			+ " join revenue_customer_mapping_t RCMT on ARDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and ARDT.finance_geography = RCMT.customer_geography and RCMT.finance_iou =ARDT.finance_iou"
			+ " join iou_customer_mapping_t ICMT on ARDT.finance_iou = ICMT.iou "
			+ " where ";

	private static final String ACTUAL_REVENUES_BY_COUNTRY_COND_SUFFIX = " (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " and (RCMT.customer_name in (:customer) or ('') in (:customer))"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (ARDT.financial_year=(:financialYear) or (:financialYear)= '') "
			+ " and (ARDT.quarter=(:quarter) or (:quarter)= '') "
			+ " AND (RCMT.customer_geography=(:geography) or (:geography)='') ";

	private static final String ACTUAL_REVENUES_BY_COUNTRY_GROUP_BY = " group by ARDT.client_country";

	private static final String PROJECTED_REVENUES_BY_COUNTRY_QUERY_PREFIX = "select PRDT.client_country,sum(PRDT.revenue) from projected_revenues_data_t PRDT "
			+ " join sub_sp_mapping_t SSMT on PRDT.sub_sp = SSMT.actual_sub_sp  "
			+ " join revenue_customer_mapping_t RCMT on PRDT.finance_customer_name = RCMT.finance_customer_name "
			+ " and PRDT.finance_geography = RCMT.customer_geography and RCMT.finance_iou =PRDT.finance_iou "
			+ " join iou_customer_mapping_t ICMT on PRDT.finance_iou = ICMT.iou  "
			+ " where";

	private static final String PROJECTED_REVENUES_BY_COUNTRY_COND_SUFFIX = " (SSMT.display_sub_sp = (:subSp) or (:subSp) = '')"
			+ " and (RCMT.customer_name in (:customer) or ('') in (:customer))"
			+ " and (ICMT.display_iou = (:iou) or (:iou) = '')"
			+ " and (PRDT.financial_year = (:financialYear) or (:financialYear)='') "
			+ " and (PRDT.quarter=(:quarter) or (:quarter)= '') "
			+ " and (RCMT.customer_geography=(:geography) or (:geography)='') ";

	private static final String PROJECTED_REVENUES_BY_COUNTRY_GROUP_BY = " group by PRDT.client_country";

	private static final String PIPELINE_PERFORMANCE_BY_SALES_STAGE = "select OPP.sales_stage_code as SalesStage, count(*) as oppCount, sum((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency)))  as OBV,"
			+ "median((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Median,"
			+ "avg((digital_deal_value * (select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP.deal_currency)) /  (select conversion_rate from beacon_convertor_mapping_t where currency_name = (:currency))) as Mean  from opportunity_t OPP "
			+ "LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
			+ "LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp  "
			+ "JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country "
			+ "JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography  "
			+ "JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ "JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ "where ";

	private static final String PIPELINE_PERFORMANCE_BY_SALES_STAGE_COND_SUFFIX = " (SSMT.display_sub_sp = (:serviceLine) OR (:serviceLine) = '')"
			+ " and (GMT.display_geography = (:displayGeography) OR (:displayGeography) = '')"
			+ " and (GMT.geography = (:geography) OR (:geography) = '')"
			+ " and (CMT.customer_name in (:customer) OR ('') in (:customer))"
			+ " and (ICMT.display_iou = (:iou) OR (:iou) = '')"
			+ " and OPP.digital_deal_value <> 0 "
			+ " and ((OPP.sales_stage_code >= 9 and deal_closure_date between (:fromDate) and (:toDate)) or OPP.sales_stage_code < 9) "
			+ "and OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo) ";

	private static final String PIPELINE_PERFORMANCE_BY_SALES_STAGE_GROUP_BY_ORDER_BY = " group by SalesStage order by SalesStage";

	private static final String TOP_OPPORTUNITIES_QUERY_PREFIX = "select distinct OPP.* from opportunity_t OPP"
			+ " LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id"
			+ " LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp "
			+ " JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country"
			+ " JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography "
			+ " JOIN customer_master_t CMT on CMT.customer_id = OPP.customer_id "
			+ " JOIN iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
			+ " where ";

	private static final String TOP_OPPORTUNITIES_COND_SUFFIX = " (SSMT.display_sub_sp = (:subSp) OR (:subSp) = '')"
			+ " and (GMT.display_geography = (:displayGeography) OR (:displayGeography) = '')"
			+ " and (GMT.geography = (:geography) OR (:geography) = '')"
			+ " and (CMT.customer_name in (:customerName) OR ('') in (:customerName))"
			+ " and (ICMT.display_iou = (:iou) OR (:iou) = '')"
			+ " and OPP.digital_deal_value <> 0 "
			+ "AND ((OPP.deal_closure_date between (:fromDate)  and (:toDate) and OPP.sales_stage_code >=9) or OPP.sales_stage_code < 9) "
			+ "AND (OPP.sales_stage_code between (:salesStageFrom) and (:salesStageTo)) ";

	private static final String TOP_OPPORTUNITIES_ORDER_BY = " order by OPP.digital_deal_value DESC limit (:count)";

	private static final String GEO_COND_PREFIX = "GMT.geography in (";
	private static final String RCMT_GEO_COND_PREFIX = "RCMT.customer_geography in (";
	private static final String GCMT_GEO_COND_PREFIX = "GCMT.geography in (";
	private static final String SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
	private static final String IOU_COND_PREFIX = "ICMT.display_iou in (";
	private static final String CUSTOMER_COND_SUFFIX = "RCMT.customer_name in (";

	public List<TargetVsActualResponse> getTargetVsActualRevenueSummary(
			String financialYear, String quarter, String displayGeography,
			String geography, String serviceLine, String iou,
			String customerName, String currency, String groupCustomer,
			boolean wins, String userId, boolean canValidate) throws Exception {
		logger.info("Inside getRevenueSummary Service");
		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}

		logger.debug("Financial Year: " + financialYear);
		if (!wins) {
			if (financialYear.equals("")) {
				logger.debug("Financial Year is Empty");
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			// Get get data of actuals
			List<Object[]> actualObjList = null;
			if (quarter.isEmpty()) {
				actualObjList = findActualRevenue(financialYear, quarter,
						displayGeography, geography, iou, custName,
						serviceLine, userId, canValidate);
			} else {
				actualObjList = findActualRevenueByQuarter(financialYear,
						quarter, displayGeography, geography, iou, custName,
						serviceLine, userId, canValidate);
			}
			logger.info("Actual Revenue has " + actualObjList.size()
					+ " values");

			Map<String, BigDecimal> quarterMap = getMapFromObjList(actualObjList);

			List<Object[]> projectedObjList = null;

			if (quarter.isEmpty()) {
				projectedObjList = findProjectedRevenue(financialYear, quarter,
						displayGeography, geography, iou, custName,
						serviceLine, userId, canValidate);
			} else {
				projectedObjList = findProjectedRevenueByQuarter(financialYear,
						quarter, displayGeography, geography, iou, custName,
						serviceLine, userId, canValidate);
			}
			logger.info("Projected Revenue has " + projectedObjList.size()
					+ " values");

			mergeProjectedRevenue(quarterMap, projectedObjList);

			List<TargetVsActualResponse> actualProjectedList = convertMaptoTargetvsActualResponse(
					quarterMap, currency);

			// service line does not have target revenue
			if (serviceLine.equals("")) {

				List<Object[]> targetRevenueList = null;
				targetRevenueList = findTargetRevenue(financialYear, quarter,
						displayGeography, geography, iou, custName, userId, canValidate);

				logger.debug("Target Revenue has " + targetRevenueList.size()
						+ " values");

				List<TargetVsActualResponse> targetList = new ArrayList<TargetVsActualResponse>();

				populateResponseList(targetRevenueList, targetList, true,
						currency);

				List<TargetVsActualResponse> tarActResponseList = mergeLists(
						targetList, actualProjectedList);

				if (!quarter.isEmpty()) {
					Collections.sort(tarActResponseList, new MonthComparator());
				}
				if (tarActResponseList.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				return tarActResponseList;
			} else {
				if (actualProjectedList.isEmpty()) {
					logger.error("NOT_FOUND: No Relevent Data Found in the database");
					throw new DestinationException(HttpStatus.NOT_FOUND,
							"No Relevent Data Found in the database");
				}
				return actualProjectedList;
			}
		} else {
			if (financialYear.equals("") && quarter.isEmpty()) {
				logger.debug("Financial Year is Empty");
				financialYear = DateUtils.getCurrentFinancialYear();
			}
			Date fromDate = DateUtils.getDate("", quarter, financialYear, true);
			Date toDate = DateUtils.getDate("", quarter, financialYear, false);
			List<Object[]> digitalDealValueList = getDigitalDealValueByClosureDate(
					fromDate, toDate, displayGeography, geography, serviceLine,
					iou, custName, currency, userId);
			List<Object[]> digitalDealValueByTimeLineList = new ArrayList<Object[]>();
			logger.debug("Digital Deal Value has "
					+ digitalDealValueList.size() + " values");
			if (!quarter.isEmpty()) {
				logger.debug("financial year is empty");
				for (Object[] quarterDigitalDealValue : digitalDealValueList) {
					if (quarterDigitalDealValue[0] != null) {
						Object[] quarterArray = new Object[2];
						quarterArray[0] = DateUtils
								.getFormattedMonth((Date) quarterDigitalDealValue[0]);
						quarterArray[1] = quarterDigitalDealValue[1];
						digitalDealValueByTimeLineList.add(quarterArray);
					}
				}
			} else {
				logger.debug("financial year is empty");
				for (Object[] quarterDigitalDealValue : digitalDealValueList) {
					if (quarterDigitalDealValue[0] != null) {
						Object[] quarterArray = new Object[2];
						quarterArray[0] = DateUtils
								.getQuarterForMonth(DateUtils
										.getFormattedMonth((Date) quarterDigitalDealValue[0]));
						quarterArray[1] = quarterDigitalDealValue[1];
						digitalDealValueByTimeLineList.add(quarterArray);
					}
				}
			}
			Map<String, BigDecimal> quarterMap = getSumUpInMap(digitalDealValueByTimeLineList);

			List<TargetVsActualResponse> targetList = new ArrayList<TargetVsActualResponse>();

			populateResponseList(quarterMap, targetList);

			if (!quarter.isEmpty()) {
				Collections.sort(targetList, new MonthComparator());
			}
			return targetList;
		}
	}

	private List<Object[]> getDigitalDealValueByClosureDate(Date fromDate,
			Date toDate, String displayGeography, String geography,
			String serviceLine, String iou, List<String> custName,
			String currency, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getDigitalDealValueQueryString(userId);
			Query digitalDealValueQuery = entityManager
					.createNativeQuery(queryString);
			digitalDealValueQuery.setParameter("geography", geography);
			digitalDealValueQuery.setParameter("displayGeography",
					displayGeography);
			digitalDealValueQuery.setParameter("iou", iou);
			digitalDealValueQuery.setParameter("serviceLine", serviceLine);
			digitalDealValueQuery.setParameter("fromDate", fromDate);
			digitalDealValueQuery.setParameter("toDate", toDate);
			digitalDealValueQuery.setParameter("customerName", custName);
			digitalDealValueQuery.setParameter("currencyName", currency);
			resultList = digitalDealValueQuery.getResultList();
			logger.info("Query string: Digital Deal Value {}", queryString);
		}
		return resultList;
	}

	private String getDigitalDealValueQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				DIGITAL_DEAL_VALUE_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(DIGITAL_DEAL_VALUE_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(DIGITAL_DEAL_VALUE_QUERY_GROUP_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> findTargetRevenue(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, List<String> custName, String userId, boolean canValidate) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (canValidate){
			validateUserAndUserGroup(userId);
		}
			String queryString = getTargetRevenueQueryString(userId);
			Query targetRevenueQuery = entityManager
					.createNativeQuery(queryString);
			targetRevenueQuery.setParameter("geography", geography);
			targetRevenueQuery.setParameter("displayGeography",
					displayGeography);
			targetRevenueQuery.setParameter("iou", iou);
			targetRevenueQuery.setParameter("financialYear", financialYear);
			targetRevenueQuery.setParameter("customerName", custName);
			targetRevenueQuery.setParameter("quarter", quarter);
			resultList = targetRevenueQuery.getResultList();
			logger.info("Query string: Target Revenue {}", queryString);
		return resultList;
	}

	private String getTargetRevenueQueryString(String userId) throws Exception {
		StringBuffer queryBuffer = new StringBuffer(TARGET_REVENUE_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(TARGET_REVENUE_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TARGET_REVENUE_QUERY_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> findProjectedRevenueByQuarter(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, List<String> custName, String serviceLine, String userId, boolean canValidate)
			throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (canValidate){
			validateUserAndUserGroup(userId);
		}
			String queryString = getProjectedRevenueByQuarterQueryString(userId);
			Query projectedRevenueByQuarterQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenueByQuarterQuery.setParameter("geography", geography);
			projectedRevenueByQuarterQuery.setParameter("displayGeography",
					displayGeography);
			projectedRevenueByQuarterQuery.setParameter("iou", iou);
			projectedRevenueByQuarterQuery.setParameter("serviceLine",
					serviceLine);
			projectedRevenueByQuarterQuery.setParameter("financialYear",
					financialYear);
			projectedRevenueByQuarterQuery.setParameter("customerName",
					custName);
			projectedRevenueByQuarterQuery.setParameter("quarter", quarter);
			resultList = projectedRevenueByQuarterQuery.getResultList();
			logger.info("Query string: Projected Revenue by Quarter {}",
					queryString);
		return resultList;
	}

	private String getProjectedRevenueByQuarterQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PROJECTED_REVENUE_BY_QUARTER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PROJECTED_REVENUE_BY_QUARTER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PROJECTED_REVENUE_BY_QUARTER_QUERY_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> findProjectedRevenue(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, List<String> custName, String serviceLine, String userId, boolean canValidate)
			throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (canValidate){
			validateUserAndUserGroup(userId);
		}
			String queryString = getProjectedRevenueQueryString(userId);
			Query projectedRevenueQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenueQuery.setParameter("geography", geography);
			projectedRevenueQuery.setParameter("displayGeography",
					displayGeography);
			projectedRevenueQuery.setParameter("iou", iou);
			projectedRevenueQuery.setParameter("serviceLine", serviceLine);
			projectedRevenueQuery.setParameter("financialYear", financialYear);
			projectedRevenueQuery.setParameter("customerName", custName);
			projectedRevenueQuery.setParameter("quarter", quarter);
			resultList = projectedRevenueQuery.getResultList();
			logger.info("Query string: Projected Revenue {}", queryString);
		return resultList;
	}

	private String getProjectedRevenueQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PROJECTED_REVENUE_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PROJECTED_REVENUE_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(PROJECTED_REVENUE_QUERY_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> findActualRevenueByQuarter(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, List<String> custName, String serviceLine, String userId, boolean canValidate)
			throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (canValidate){
			validateUserAndUserGroup(userId);
		}
			String queryString = getActualRevenueByQuarterQueryString(userId);
			Query actualRevenueByQuarterQuery = entityManager
					.createNativeQuery(queryString);
			actualRevenueByQuarterQuery.setParameter("geography", geography);
			actualRevenueByQuarterQuery.setParameter("displayGeography",
					displayGeography);
			actualRevenueByQuarterQuery.setParameter("iou", iou);
			actualRevenueByQuarterQuery
					.setParameter("serviceLine", serviceLine);
			actualRevenueByQuarterQuery.setParameter("financialYear",
					financialYear);
			actualRevenueByQuarterQuery.setParameter("customerName", custName);
			actualRevenueByQuarterQuery.setParameter("quarter", quarter);
			resultList = actualRevenueByQuarterQuery.getResultList();
			logger.info("Query string: Actual Revenue by Quarter{}",
					queryString);
		return resultList;
	}

	private String getActualRevenueByQuarterQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				ACTUAL_REVENUE_BY_QUARTER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUE_QUERY_BY_QUARTER_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(ACTUAL_REVENUE_BY_QUARTER_QUERY_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> findActualRevenue(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, List<String> custName, String serviceLine,
			String userId, boolean canValidate) throws Exception {
		// TODO Auto-generated method stub
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (canValidate){
			validateUserAndUserGroup(userId);
		}
		String queryString = getActualRevenueQueryString(userId);
		Query actualRevenueQuery = entityManager.createNativeQuery(queryString);
		actualRevenueQuery.setParameter("geography", geography);
		actualRevenueQuery.setParameter("displayGeography", displayGeography);
		actualRevenueQuery.setParameter("iou", iou);
		actualRevenueQuery.setParameter("serviceLine", serviceLine);
		actualRevenueQuery.setParameter("financialYear", financialYear);
		actualRevenueQuery.setParameter("customerName", custName);
		actualRevenueQuery.setParameter("quarter", quarter);
		resultList = actualRevenueQuery.getResultList();
		logger.info("Query string: Actual Revenue{}", queryString);
		return resultList;
	}

	private boolean validateUserAndUserGroup(String userId) throws Exception {

		// if (!DestinationUtils.getCurrentUserDetails().getUserId()
		// .equalsIgnoreCase(userId)) {
		// logger.error("User Id mismatch");
		// throw new DestinationException(HttpStatus.NOT_FOUND, userId
		// + " does not matches with the logged in user id ");
		// } else {
		UserT user = userService.findByUserId(userId);
		// if (user == null) {
		// logger.error("NOT_FOUND: User not found: {}", userId);
		// throw new DestinationException(HttpStatus.NOT_FOUND,
		// "User not found: " + userId);
		// } else {
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
				return true;
			}
		} else {
			throw new DestinationException(HttpStatus.UNAUTHORIZED,
					"User doesnot belong to any user group");
		}
		// }
		// }

	}

	private String getActualRevenueQueryString(String userId) throws Exception {
		// TODO Auto-generated method stub

		StringBuffer queryBuffer = new StringBuffer(ACTUAL_REVENUE_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUE_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(ACTUAL_REVENUE_QUERY_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();

	}

	private Map<String, BigDecimal> getSumUpInMap(
			List<Object[]> digitalDealValueByTimeLineList) {

		Map<String, BigDecimal> map = new TreeMap<String, BigDecimal>();
		for (int i = 0; i < digitalDealValueByTimeLineList.size(); i++) {
			Object[] obj = digitalDealValueByTimeLineList.get(i);
			logger.debug("In item " + i + "  Obj [0] : " + obj[0]
					+ " Obj [1] : " + obj[1]);
			if (obj[0] != null) {
				String dispName = (String) obj[0];
				if (obj[1] != null) {
					BigDecimal rev = new BigDecimal(obj[1].toString());
					if (dispName != null) {
						if (map.containsKey(dispName)) {
							rev = rev.add(map.get(dispName));
						}
						map.put(dispName, rev);
					}
				}
			}

		}
		return map;

	}

	private void populateResponseList(Map<String, BigDecimal> quarterMap,
			List<TargetVsActualResponse> targetList) {
		for (String quarterKeyset : quarterMap.keySet()) {
			TargetVsActualResponse targetVsActual = new TargetVsActualResponse();
			targetVsActual.setSubTimeLine(quarterKeyset);
			targetVsActual.setDigitalDealValue(quarterMap.get(quarterKeyset));
			targetList.add(targetVsActual);
		}

	}

	private List<TargetVsActualResponse> convertMaptoTargetvsActualResponse(
			Map<String, BigDecimal> quarterMap, String targetCurrency)
			throws Exception {
		List<TargetVsActualResponse> revenueList = new ArrayList<TargetVsActualResponse>();
		for (Map.Entry<String, BigDecimal> entry : quarterMap.entrySet()) {
			String dispName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			TargetVsActualResponse resp = new TargetVsActualResponse();
			resp.setSubTimeLine(dispName);
			resp.setActual(revenueCurrency);
			revenueList.add(resp);
		}
		return revenueList;
	}

	private void populateResponseList(List<Object[]> objList,
			List<TargetVsActualResponse> respList, boolean isTarget,
			String targetCurrency) throws Exception {
		if (objList != null && !objList.isEmpty()) {
			for (Object[] objArr : objList) {
				TargetVsActualResponse resp = new TargetVsActualResponse();
				resp.setSubTimeLine((String) objArr[0]);
				if (isTarget) {
					BigDecimal target = (BigDecimal) objArr[1];
					BigDecimal revenueCurrency = beaconService.convert(
							BeaconConverterService.TARGET_REVENUE_CURRENCY,
							targetCurrency, target);
					resp.setTarget(revenueCurrency);
				} else {
					BigDecimal actual = (BigDecimal) objArr[1];
					BigDecimal revenueCurrency = beaconService.convert(
							BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
							targetCurrency, actual);
					resp.setActual(revenueCurrency);
				}
				respList.add(resp);
			}
		}
	}

	private static List<TargetVsActualResponse> mergeLists(
			List<TargetVsActualResponse> targetList,
			List<TargetVsActualResponse> actualList) throws Exception {
		Map<String, BigDecimal[]> map = getMapFromLists(targetList, actualList);
		List<TargetVsActualResponse> respList = getMergedListFromMap(map);
		return respList;
	}

	private static List<TargetVsActualResponse> getMergedListFromMap(
			Map<String, BigDecimal[]> map) throws Exception {
		List<TargetVsActualResponse> respList = new ArrayList<TargetVsActualResponse>();
		for (Map.Entry<String, BigDecimal[]> entry : map.entrySet()) {
			String quarter = entry.getKey();
			BigDecimal[] valuesList = entry.getValue();
			BigDecimal target = valuesList[0];
			BigDecimal actual = valuesList[1];
			TargetVsActualResponse resp = new TargetVsActualResponse();
			resp.setSubTimeLine(quarter);
			resp.setActual(actual);
			resp.setTarget(target);
			respList.add(resp);
		}
		return respList;
	}

	private static Map<String, BigDecimal[]> getMapFromLists(
			List<TargetVsActualResponse> targetList,
			List<TargetVsActualResponse> actualList) throws Exception {
		Map<String, BigDecimal[]> map = new TreeMap<String, BigDecimal[]>();

		// Populate Target Revenue
		for (TargetVsActualResponse obj : targetList) {
			BigDecimal[] values = new BigDecimal[2];
			String quarter = obj.getSubTimeLine();
			BigDecimal target = obj.getTarget();
			values[0] = target;
			values[1] = ZERO_REVENUE;
			map.put(quarter, values);
		}

		// Populate Actual Revenue
		for (TargetVsActualResponse obj : actualList) {
			BigDecimal[] values1 = new BigDecimal[2];
			String quarter = obj.getSubTimeLine();
			BigDecimal actual = obj.getActual();
			if (map.containsKey(quarter)) {
				BigDecimal[] valList = map.get(quarter);
				valList[1] = actual;
				map.put(quarter, valList);
			} else {
				values1[0] = ZERO_REVENUE;
				values1[1] = actual;
				map.put(quarter, values1);
			}
		}

		return map;
	}

	public List<IOUReport> getRevenuesByIOU(String financialYear,
			String quarter, String displayGeography, String geography,
			String serviceLine, String currency, String userId)
			throws Exception {

		List<Object[]> iouObjList = getActualRevenuesByIOU(financialYear,
				quarter, displayGeography, geography, serviceLine, userId);

		// initializing the map with actuals data
		Map<String, BigDecimal> iouMap = getMapFromObjList(iouObjList);

		List<Object[]> iouProjObjList = getProjectedRevenuesByIOU(
				financialYear, quarter, displayGeography, geography,
				serviceLine, userId);

		// adding projected revenue
		mergeProjectedRevenue(iouMap, iouProjObjList);

		List<IOUReport> iouRevenuesList = convertMaptoIOUList(iouMap, currency);

		if (iouRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		Collections.sort(iouRevenuesList, new IOUActualComparator());

		return iouRevenuesList;
	}

	private List<Object[]> getProjectedRevenuesByIOU(String financialYear,
			String quarter, String displayGeography, String geography,
			String serviceLine, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getProjectedRevenuesByIOUQueryString(userId);
			Query projectedRevenuesByIOUQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenuesByIOUQuery.setParameter("displayGeography",
					displayGeography);
			projectedRevenuesByIOUQuery.setParameter("geography", geography);
			projectedRevenuesByIOUQuery
					.setParameter("serviceLine", serviceLine);
			projectedRevenuesByIOUQuery.setParameter("financialYear",
					financialYear);
			projectedRevenuesByIOUQuery.setParameter("quarter", quarter);
			resultList = projectedRevenuesByIOUQuery.getResultList();
			logger.info("Query string: Actual Revenues by IOU {}", queryString);
		}
		return resultList;
	}

	private String getProjectedRevenuesByIOUQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(PROJECTED_REVENUES_BY_IOU);
		queryBuffer.append(PROJECTED_REVENUES_BY_IOU_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PROJECTED_REVENUES_BY_IOU_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PROJECTED_REVENUES_BY_IOU_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(PROJECTED_REVENUES_BY_IOU_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> getActualRevenuesByIOU(String financialYear,
			String quarter, String displayGeography, String geography,
			String serviceLine, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getActualRevenuesByIOUQueryString(userId);
			Query actualRevenuesByIOUQuery = entityManager
					.createNativeQuery(queryString);
			actualRevenuesByIOUQuery.setParameter("geography", geography);
			actualRevenuesByIOUQuery.setParameter("displayGeography",
					displayGeography);
			actualRevenuesByIOUQuery.setParameter("serviceLine", serviceLine);
			actualRevenuesByIOUQuery.setParameter("financialYear",
					financialYear);
			actualRevenuesByIOUQuery.setParameter("quarter", quarter);
			resultList = actualRevenuesByIOUQuery.getResultList();
			logger.info("Query string: Actual Revenues by IOU {}", queryString);
		}
		return resultList;
	}

	private String getActualRevenuesByIOUQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				ACTUAL_REVENUES_BY_IOU_QUERY_PREFIX);
		queryBuffer.append(ACTUAL_REVENUES_BY_IOU_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUES_BY_IOU_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(ACTUAL_REVENUES_BY_IOU_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(ACTUAL_REVENUES_BY_IOU_ORDER_BY);
		return queryBuffer.toString();

	}

	private List<IOUReport> convertMaptoIOUList(Map<String, BigDecimal> iouMap,
			String targetCurrency) throws Exception {
		List<IOUReport> iouList = new ArrayList<IOUReport>();

		for (Map.Entry<String, BigDecimal> entry : iouMap.entrySet()) {
			String iouName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			IOUReport iou = new IOUReport();
			iou.setDisplayIOU(iouName);
			iou.setActualRevenue(revenueCurrency);
			iouList.add(iou);
		}
		return iouList;
	}

	private void mergeProjectedRevenue(Map<String, BigDecimal> map,
			List<Object[]> projObjList) throws Exception {
		if (map == null)
			map = new TreeMap<String, BigDecimal>();
		for (Object[] obj : projObjList) {
			String dispName = (String) obj[0];
			if (obj[1] != null && dispName != null) {
				BigDecimal projRev = new BigDecimal(obj[1].toString());
				if (map.containsKey(dispName)) {
					BigDecimal actual = map.get(dispName);
					map.put(dispName, actual.add(projRev));
				} else {
					// if subsp/iou/geography/quarter does not have actuals data
					map.put(dispName, projRev);
				}
			}
		}
	}

	private Map<String, BigDecimal> getMapFromObjList(List<Object[]> objList)
			throws Exception {
		Map<String, BigDecimal> map = new TreeMap<String, BigDecimal>();
		for (Object[] obj : objList) {
			String dispName = (String) obj[0];
			BigDecimal rev = null;
			if (obj[1] != null)
				rev = new BigDecimal(obj[1].toString());
			if (dispName != null)
				map.put(dispName, rev);
		}
		return map;
	}

	public List<SubSpReport> getRevenuesBySubSp(String financialYear,
			String quarter, String displayGeography, String geography,
			String customerName, String iou, String currency,
			String groupCustomer, String userId) throws Exception {

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		List<Object[]> subObjList = getActualRevenuesBySubSp(financialYear,
				quarter, displayGeography, geography, custName, iou, userId);

		// initializing the map with actuals data
		Map<String, BigDecimal> subSpMap = getMapFromObjList(subObjList);

		List<Object[]> subProjObjList = getProjectedRevenuesBySubSp(
				financialYear, quarter, displayGeography, geography, custName,
				iou, userId);

		// adding projected revenue
		mergeProjectedRevenue(subSpMap, subProjObjList);

		List<SubSpReport> subSpRevenuesList = convertMaptoSubSpList(subSpMap,
				currency);
		if (subSpRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return subSpRevenuesList;
	}

	private List<Object[]> getProjectedRevenuesBySubSp(String financialYear,
			String quarter, String displayGeography, String geography,
			List<String> custName, String iou, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getProjectedRevenuesBySubSpQueryString(userId);
			Query projectedRevenuesBySubSpQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenuesBySubSpQuery.setParameter("geography", geography);
			projectedRevenuesBySubSpQuery.setParameter("displayGeography",
					displayGeography);
			projectedRevenuesBySubSpQuery.setParameter("financialYear",
					financialYear);
			projectedRevenuesBySubSpQuery.setParameter("quarter", quarter);
			projectedRevenuesBySubSpQuery.setParameter("iou", iou);
			projectedRevenuesBySubSpQuery
					.setParameter("customerName", custName);
			resultList = projectedRevenuesBySubSpQuery.getResultList();
			logger.info("Query string: Projected Revenues by Sub Sp {}",
					queryString);
		}
		return resultList;
	}

	private String getProjectedRevenuesBySubSpQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PROJECTED_REVENUES_BY_SUBSP_QUERY_PREFIX);
		queryBuffer.append(PROJECTED_REVENUES_BY_SUBSP_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PROJECTED_REVENUES_BY_SUBSP_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PROJECTED_REVENUES_BY_SUBSP_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(PROJECTED_REVENUES_BY_SUBSP_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> getActualRevenuesBySubSp(String financialYear,
			String quarter, String displayGeography, String geography,
			List<String> custName, String iou, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getActualRevenuesBySubSpQueryString(userId);
			Query actualRevenuesBySubSPQuery = entityManager
					.createNativeQuery(queryString);
			actualRevenuesBySubSPQuery.setParameter("geography", geography);
			actualRevenuesBySubSPQuery.setParameter("displayGeography",
					displayGeography);
			actualRevenuesBySubSPQuery.setParameter("financialYear",
					financialYear);
			actualRevenuesBySubSPQuery.setParameter("quarter", quarter);
			actualRevenuesBySubSPQuery.setParameter("iou", iou);
			actualRevenuesBySubSPQuery.setParameter("customerName", custName);
			resultList = actualRevenuesBySubSPQuery.getResultList();
			logger.info("Query string: Actual Revenues by SubSp {}",
					queryString);
		}
		return resultList;
	}

	private String getActualRevenuesBySubSpQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				ACTUAL_REVENUES_BY_SUBSP_QUERY_PREFIX);
		queryBuffer.append(ACTUAL_REVENUES_BY_SUBSP_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUES_BY_SUBSP_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(ACTUAL_REVENUES_BY_SUBSP_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(ACTUAL_REVENUES_BY_SUBSP_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<SubSpReport> convertMaptoSubSpList(
			Map<String, BigDecimal> subSpMap, String targetCurrency)
			throws Exception {
		List<SubSpReport> subSpList = new ArrayList<SubSpReport>();

		for (Map.Entry<String, BigDecimal> entry : subSpMap.entrySet()) {
			String dispName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueTargetCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			SubSpReport subSp = new SubSpReport();
			subSp.setDisplaySubSp(dispName);
			subSp.setActualRevenue(revenueTargetCurrency);
			subSpList.add(subSp);
		}
		return subSpList;
	}

	public List<GeographyReport> getRevenuesByDispGeography(
			String financialYear, String quarter, String customerName,
			String subSp, String iou, String currency, String groupCustomer,
			String userId) throws Exception {

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		List<Object[]> geoObjList = getActualRevenuesByDispGeo(financialYear,
				quarter, custName, subSp, iou, userId);

		// initializing the map with actuals data
		Map<String, BigDecimal> dispGeoMap = getMapFromObjList(geoObjList);

		List<Object[]> geoProjObjList = getProjectedRevenuesByDispGeo(
				financialYear, quarter, custName, subSp, iou, userId);

		// adding projected revenue
		mergeProjectedRevenue(dispGeoMap, geoProjObjList);

		List<GeographyReport> geoRevenuesList = convertMaptoGeographyList(
				dispGeoMap, currency);

		if (geoRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return geoRevenuesList;
	}

	private List<Object[]> getProjectedRevenuesByDispGeo(String financialYear,
			String quarter, List<String> custName, String subSp, String iou,
			String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getProjectedRevenuesByDispGeoQueryString(userId);
			Query projectedRevenuesByDispGeoQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenuesByDispGeoQuery.setParameter("financialYear",
					financialYear);
			projectedRevenuesByDispGeoQuery.setParameter("subSp", subSp);
			projectedRevenuesByDispGeoQuery.setParameter("quarter", quarter);
			projectedRevenuesByDispGeoQuery.setParameter("iou", iou);
			projectedRevenuesByDispGeoQuery.setParameter("customer", custName);
			resultList = projectedRevenuesByDispGeoQuery.getResultList();
			logger.info(
					"Query string: Projected Revenues by display Geography {}",
					queryString);
		}
		return resultList;
	}

	private String getProjectedRevenuesByDispGeoQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PROJECTED_REVENUES_BY_GEO_QUERY_PREFIX);
		queryBuffer.append(PROJECTED_REVENUES_BY_GEO_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PROJECTED_REVENUES_BY_GEO_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PROJECTED_REVENUES_BY_GEO_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(PROJECTED_REVENUES_BY_GEO_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> getActualRevenuesByDispGeo(String financialYear,
			String quarter, List<String> custName, String subSp, String iou,
			String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getActualRevenuesByDispGeoQueryString(userId);
			Query actualRevenuesByDispGeoQuery = entityManager
					.createNativeQuery(queryString);
			actualRevenuesByDispGeoQuery.setParameter("financialYear",
					financialYear);
			actualRevenuesByDispGeoQuery.setParameter("subSp", subSp);
			actualRevenuesByDispGeoQuery.setParameter("quarter", quarter);
			actualRevenuesByDispGeoQuery.setParameter("iou", iou);
			actualRevenuesByDispGeoQuery.setParameter("customer", custName);
			resultList = actualRevenuesByDispGeoQuery.getResultList();
			logger.info(
					"Query string: Actual Revenues by display Geography {}",
					queryString);
		}
		return resultList;
	}

	private String getActualRevenuesByDispGeoQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				ACTUAL_REVENUES_BY_GEO_QUERY_PREFIX);
		queryBuffer.append(ACTUAL_REVENUES_BY_GEO_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUES_BY_GEO_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(ACTUAL_REVENUES_BY_GEO_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(ACTUAL_REVENUES_BY_GEO_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<GeographyReport> convertMaptoGeographyList(
			Map<String, BigDecimal> dispGeoMap, String targetCurrency)
			throws Exception {
		List<GeographyReport> geoList = new ArrayList<GeographyReport>();

		for (Map.Entry<String, BigDecimal> entry : dispGeoMap.entrySet()) {
			String dispName = entry.getKey();
			BigDecimal revenue = entry.getValue();
			BigDecimal revenueTargetCurrency = beaconService.convert(
					BeaconConverterService.ACTUALS_REVENUE_CURRENCY,
					targetCurrency, revenue);
			GeographyReport geo = new GeographyReport();
			geo.setGeography(dispName);
			geo.setActualRevenue(revenueTargetCurrency);
			geoList.add(geo);
		}
		return geoList;
	}

	public List<GeographyReport> getRevenuesBySubGeography(
			String financialYear, String quarter, String customerName,
			String subSp, String iou, String displayGeography,
			String geography, String currency, String groupCustomer,
			String userId) throws Exception {

		List<Object[]> geoObjList = null;
		List<Object[]> geoProjObjList = null;

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		if (geography.isEmpty()) {
			geoObjList = getActualRevenuesBySubGeo(financialYear, quarter,
					custName, subSp, iou, displayGeography, userId);
			geoProjObjList = getProjectedRevenuesBySubGeo(financialYear,
					quarter, custName, subSp, iou, displayGeography, userId);
		} else {
			geoObjList = getActualRevenuesByCountry(financialYear, quarter,
					custName, subSp, iou, geography, userId);
			geoProjObjList = getProjectedRevenuesByCountry(financialYear,
					quarter, custName, subSp, iou, displayGeography, userId);
		}

		// initializing the map with actuals data
		Map<String, BigDecimal> dispGeoMap = getMapFromObjList(geoObjList);

		// adding projected revenue
		mergeProjectedRevenue(dispGeoMap, geoProjObjList);

		List<GeographyReport> geoRevenuesList = convertMaptoGeographyList(
				dispGeoMap, currency);

		if (geoRevenuesList.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return geoRevenuesList;
	}

	private List<Object[]> getProjectedRevenuesByCountry(String financialYear,
			String quarter, List<String> custName, String subSp, String iou,
			String displayGeography, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getProjectedRevenuesByCountryQueryString(userId);
			Query projectedRevenuesByCountryQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenuesByCountryQuery.setParameter("financialYear",
					financialYear);
			projectedRevenuesByCountryQuery.setParameter("subSp", subSp);
			projectedRevenuesByCountryQuery.setParameter("geography",
					displayGeography);
			projectedRevenuesByCountryQuery.setParameter("quarter", quarter);
			projectedRevenuesByCountryQuery.setParameter("iou", iou);
			projectedRevenuesByCountryQuery.setParameter("customer", custName);
			resultList = projectedRevenuesByCountryQuery.getResultList();
			logger.info("Query string: Projected Revenues by Country {}",
					queryString);
		}
		return resultList;
	}

	private String getProjectedRevenuesByCountryQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PROJECTED_REVENUES_BY_COUNTRY_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(RCMT_GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PROJECTED_REVENUES_BY_COUNTRY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(PROJECTED_REVENUES_BY_COUNTRY_GROUP_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> getActualRevenuesByCountry(String financialYear,
			String quarter, List<String> custName, String subSp, String iou,
			String geography, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getActualRevenuesByCountryQueryString(userId);
			Query actualRevenuesByCountryQuery = entityManager
					.createNativeQuery(queryString);
			actualRevenuesByCountryQuery.setParameter("financialYear",
					financialYear);
			actualRevenuesByCountryQuery.setParameter("subSp", subSp);
			actualRevenuesByCountryQuery.setParameter("geography", geography);
			actualRevenuesByCountryQuery.setParameter("quarter", quarter);
			actualRevenuesByCountryQuery.setParameter("iou", iou);
			actualRevenuesByCountryQuery.setParameter("customer", custName);
			resultList = actualRevenuesByCountryQuery.getResultList();
			logger.info("Query string: Actual Revenues by Country {}",
					queryString);
		}
		return resultList;
	}

	private String getActualRevenuesByCountryQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				ACTUAL_REVENUES_BY_COUNTRY_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(RCMT_GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUES_BY_COUNTRY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(ACTUAL_REVENUES_BY_COUNTRY_GROUP_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> getProjectedRevenuesBySubGeo(String financialYear,
			String quarter, List<String> custName, String subSp, String iou,
			String displayGeography, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getProjectedRevenuesBySubGeoQueryString(userId);
			Query projectedRevenuesBySubGeoQuery = entityManager
					.createNativeQuery(queryString);
			projectedRevenuesBySubGeoQuery.setParameter("financialYear",
					financialYear);
			projectedRevenuesBySubGeoQuery.setParameter("subSp", subSp);
			projectedRevenuesBySubGeoQuery.setParameter("geography",
					displayGeography);
			projectedRevenuesBySubGeoQuery.setParameter("quarter", quarter);
			projectedRevenuesBySubGeoQuery.setParameter("iou", iou);
			projectedRevenuesBySubGeoQuery.setParameter("customer", custName);
			resultList = projectedRevenuesBySubGeoQuery.getResultList();
			logger.info("Query string: Projected Revenues by Sub Geography {}",
					queryString);
		}
		return resultList;
	}

	private String getProjectedRevenuesBySubGeoQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PROJECTED_REVENUES_BY_SUB_GEO_QUERY_PREFIX);
		queryBuffer.append(PROJECTED_REVENUES_BY_SUB_GEO_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer
				.append(PROJECTED_REVENUES_BY_SUB_GEO_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PROJECTED_REVENUES_BY_SUB_GEO_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(PROJECTED_REVENUES_BY_SUB_GEO_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> getActualRevenuesBySubGeo(String financialYear,
			String quarter, List<String> custName, String subSp, String iou,
			String displayGeography, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getActualRevenuesBySubGeoQueryString(userId);
			Query actualRevenuesBySubGeoQuery = entityManager
					.createNativeQuery(queryString);
			actualRevenuesBySubGeoQuery.setParameter("financialYear",
					financialYear);
			actualRevenuesBySubGeoQuery.setParameter("subSp", subSp);
			actualRevenuesBySubGeoQuery.setParameter("geography",
					displayGeography);
			actualRevenuesBySubGeoQuery.setParameter("quarter", quarter);
			actualRevenuesBySubGeoQuery.setParameter("iou", iou);
			actualRevenuesBySubGeoQuery.setParameter("customer", custName);
			resultList = actualRevenuesBySubGeoQuery.getResultList();
			logger.info("Query string: Actual Revenues by Sub Geography {}",
					queryString);
		}
		return resultList;
	}

	private String getActualRevenuesBySubGeoQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				ACTUAL_REVENUES_BY_SUB_GEO_QUERY_PREFIX);
		queryBuffer.append(ACTUAL_REVENUES_BY_SUB_GEO_INNER_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(ACTUAL_REVENUES_BY_SUB_GEO_INNER_QUERY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(ACTUAL_REVENUES_BY_SUB_GEO_INNER_QUERY_GROUP_BY_ORDER_BY);
		queryBuffer.append(ACTUAL_REVENUES_BY_SUB_GEO_ORDER_BY);
		return queryBuffer.toString();
	}

	public List<OpportunityT> getTopOpportunities(String currency,
			String displayGeography, String geography, int stageFrom,
			int stageTo, String subSp, String iou, Date dateFrom, Date dateTo,
			int count, String customerName, String groupCustomer, String userId)
			throws Exception {
		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		List<OpportunityT> topOppList = new ArrayList<OpportunityT>();
		topOppList = findTopOpportunities(displayGeography, geography, subSp,
				iou, dateFrom, dateTo, stageFrom, stageTo, count, custName,
				userId);
		return topOppList;

	}

	private List<OpportunityT> findTopOpportunities(String displayGeography,
			String geography, String subSp, String iou, Date dateFrom,
			Date dateTo, int stageFrom, int stageTo, int count,
			List<String> custName, String userId) throws Exception {
		List<OpportunityT> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getTopOpportunitiesQueryString(userId);
			Query TopOpportunitiesQuery = entityManager.createNativeQuery(
					queryString, OpportunityT.class);
			TopOpportunitiesQuery.setParameter("displayGeography",
					displayGeography);
			TopOpportunitiesQuery.setParameter("geography", geography);
			TopOpportunitiesQuery.setParameter("subSp", subSp);
			TopOpportunitiesQuery.setParameter("iou", iou);
			TopOpportunitiesQuery.setParameter("customerName", custName);
			TopOpportunitiesQuery.setParameter("salesStageFrom", stageFrom);
			TopOpportunitiesQuery.setParameter("salesStageTo", stageTo);
			TopOpportunitiesQuery.setParameter("fromDate", dateFrom);
			TopOpportunitiesQuery.setParameter("toDate", dateTo);
			TopOpportunitiesQuery.setParameter("count", count);
			resultList = TopOpportunitiesQuery.getResultList();
			logger.info("Query string:PipelinePerformanceBy Sales Stage {}",
					queryString);
		}
		return resultList;
	}

	private String getTopOpportunitiesQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				TOP_OPPORTUNITIES_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(TOP_OPPORTUNITIES_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(TOP_OPPORTUNITIES_ORDER_BY);
		return queryBuffer.toString();
	}

	public ReportsOpportunity getOpportunity(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, String serviceLine, String currency,
			int salesStageFrom, int salesStageTo, String customerName,
			String groupCustomer, String userId) throws Exception {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		ReportsOpportunity reportsOpportunity = new ReportsOpportunity();
		List<ReportsSalesStage> salesStageList = new ArrayList<ReportsSalesStage>();
		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		// List<Object[]> pipelineData = opportunityRepository
		// .findPipelinePerformance(geography, iou, serviceLine,
		// currency, custName, fromDate, toDate,
		// salesStageFrom, salesStageTo);
		// if (pipelineData != null) {
		// Object[] pipeline = pipelineData.get(0);
		// if (pipeline[1] != null) {
		// reportsOpportunity.setOverallBidValue(pipeline[1]
		// .toString());
		// }

		List<Object[]> pipeLinesBySalesStage = findPipelinePerformanceBySalesStage(
				displayGeography, geography, iou, serviceLine, currency,
				custName, fromDate, toDate, salesStageFrom, salesStageTo,
				userId);
		if (pipeLinesBySalesStage != null) {
			for (Object[] pipeLineBySalesStage : pipeLinesBySalesStage) {
				ReportsSalesStage reportsSalesStage = new ReportsSalesStage();
				if (pipeLineBySalesStage[0] != null) {
					int salesStageCode = Integer
							.parseInt(pipeLineBySalesStage[0].toString());
					reportsSalesStage.setSalesStageCode(salesStageCode + "");
					SalesStageMappingT salesStageMapping = salesStageMappingRepository
							.findBySalesStageCode(salesStageCode);
					if (salesStageMapping != null) {
						reportsSalesStage
								.setSalesStageCodeDescription(salesStageMapping
										.getSalesStageDescription());
					}
					if (pipeLineBySalesStage[1] != null) {
						reportsSalesStage.setCount(pipeLineBySalesStage[1]
								.toString());
					}
					if (pipeLineBySalesStage[2] != null) {
						reportsSalesStage
								.setDigitalDealValue(pipeLineBySalesStage[2]
										.toString());
					}
					if (pipeLineBySalesStage[3] != null) {
						reportsSalesStage.setMedian(pipeLineBySalesStage[3]
								.toString());
					}
					if (pipeLineBySalesStage[4] != null) {
						reportsSalesStage.setMean(pipeLineBySalesStage[4]
								.toString());
					}

				}
				salesStageList.add(reportsSalesStage);
			}
		}

		reportsOpportunity.setSalesStageList(salesStageList);
		return reportsOpportunity;
	}

	private List<Object[]> findPipelinePerformanceBySalesStage(
			String displayGeography, String geography, String iou,
			String serviceLine, String currency, List<String> custName,
			Date fromDate, Date toDate, int salesStageFrom, int salesStageTo,
			String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getPipelinePerformanceBySalesStageQueryString(userId);
			Query pipelinePerformanceBySalesStageQuery = entityManager
					.createNativeQuery(queryString);
			pipelinePerformanceBySalesStageQuery.setParameter("geography",
					geography);
			pipelinePerformanceBySalesStageQuery.setParameter(
					"displayGeography", displayGeography);
			pipelinePerformanceBySalesStageQuery.setParameter("serviceLine",
					serviceLine);
			pipelinePerformanceBySalesStageQuery.setParameter("iou", iou);
			pipelinePerformanceBySalesStageQuery.setParameter("currency",
					currency);
			pipelinePerformanceBySalesStageQuery.setParameter("customer",
					custName);
			pipelinePerformanceBySalesStageQuery.setParameter("salesStageFrom",
					salesStageFrom);
			pipelinePerformanceBySalesStageQuery.setParameter("salesStageTo",
					salesStageTo);
			pipelinePerformanceBySalesStageQuery.setParameter("fromDate",
					fromDate);
			pipelinePerformanceBySalesStageQuery.setParameter("toDate", toDate);
			resultList = pipelinePerformanceBySalesStageQuery.getResultList();
			logger.info("Query string:PipelinePerformanceBy Sales Stage {}",
					queryString);
		}
		return resultList;
	}

	private String getPipelinePerformanceBySalesStageQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PIPELINE_PERFORMANCE_BY_SALES_STAGE);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_SALES_STAGE_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PIPELINE_PERFORMANCE_BY_SALES_STAGE_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	private Date getDate(String financialYear, String quarter,
			boolean isFromDate) throws Exception {
		Date date = new Date();
		if (financialYear.equals("")) {
			if (quarter.equals("")) {
				financialYear = DateUtils.getCurrentFinancialYear();
				date = DateUtils.getDateFromFinancialYear(financialYear,
						isFromDate);
			} else {
				date = DateUtils.getDateFromQuarter(quarter, isFromDate);
			}
		} else {
			if (quarter.equals("")) {
				date = DateUtils.getDateFromFinancialYear(financialYear,
						isFromDate);
			} else {
				throw new DestinationException(HttpStatus.BAD_REQUEST,
						"Cannot have both Financial year and Quarter as input parameters.");
			}
		}
		return date;
	}

	public List<IOUReport> getOpportunitiesByIOU(String financialYear,
			String quarter, String displayGeography, String geography,
			String serviceLine, String currency, int salesStageFrom,
			int salesStageTo, String userId) throws Exception {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<IOUReport> iouReports = new ArrayList<IOUReport>();
		List<Object[]> opportunitiesByIOU = null;
		opportunitiesByIOU = findPipelinePerformanceByIOU(displayGeography,
				geography, serviceLine, currency, fromDate, toDate,
				salesStageFrom, salesStageTo, userId);
		if (opportunitiesByIOU != null) {
			for (Object[] opportunityByIOU : opportunitiesByIOU) {
				IOUReport iouReport = new IOUReport();
				if (opportunityByIOU[0] != null) {
					iouReport.setDisplayIOU(opportunityByIOU[0].toString());
				}
				if (opportunityByIOU[1] != null) {
					iouReport.setDigitalDealValue(new BigDecimal(
							opportunityByIOU[1].toString()));
				}

				iouReports.add(iouReport);
			}

		}
		if (iouReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");
		Collections.sort(iouReports, new IOUPipelineComparator());
		return iouReports;
	}

	private List<Object[]> findPipelinePerformanceByIOU(
			String displayGeography, String geography, String serviceLine,
			String currency, Date fromDate, Date toDate, int salesStageFrom,
			int salesStageTo, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getPipelinePerformanceByIOUQueryString(userId);
			Query pipelinePerformanceByIOUQuery = entityManager
					.createNativeQuery(queryString);
			pipelinePerformanceByIOUQuery.setParameter("displayGeography",
					displayGeography);
			pipelinePerformanceByIOUQuery.setParameter("geography", geography);
			pipelinePerformanceByIOUQuery.setParameter("serviceLine",
					serviceLine);
			pipelinePerformanceByIOUQuery.setParameter("currency", currency);
			pipelinePerformanceByIOUQuery.setParameter("salesStageFrom",
					salesStageFrom);
			pipelinePerformanceByIOUQuery.setParameter("salesStageTo",
					salesStageTo);
			pipelinePerformanceByIOUQuery.setParameter("fromDate", fromDate);
			pipelinePerformanceByIOUQuery.setParameter("toDate", toDate);
			resultList = pipelinePerformanceByIOUQuery.getResultList();
			logger.info("Query string:PipelinePerformanceByIOU {}", queryString);
		}
		return resultList;
	}

	private String getPipelinePerformanceByIOUQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PIPELINE_PERFORMANCE_BY_IOU_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_IOU_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_IOU_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	public List<SubSpReport> getOpportunitiesBySubSp(String financialYear,
			String quarter, String displayGeography, String geography,
			String iou, String currency, int salesStageFrom, int salesStageTo,
			String userId) throws Exception {
		if (!quarter.isEmpty())
			financialYear = "";
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<SubSpReport> subSpReports = new ArrayList<SubSpReport>();
		List<Object[]> opportunitiesBySubSpReports = null;

		opportunitiesBySubSpReports = findPipelinePerformanceByServiceLine(
				displayGeography, geography, iou, currency, salesStageFrom,
				salesStageTo, fromDate, toDate, userId);

		if (opportunitiesBySubSpReports != null) {
			for (Object[] opportunityBySubSp : opportunitiesBySubSpReports) {
				SubSpReport subSpReport = new SubSpReport();
				if (opportunityBySubSp[0] != null) {
					subSpReport.setDisplaySubSp(opportunityBySubSp[0]
							.toString());
				}
				if (opportunityBySubSp[1] != null) {
					subSpReport.setDigitalDealValue(new BigDecimal(
							opportunityBySubSp[1].toString()));
				}

				subSpReports.add(subSpReport);
			}
		}
		if (subSpReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return subSpReports;
	}

	private List<Object[]> findPipelinePerformanceByServiceLine(
			String displayGeography, String geography, String iou,
			String currency, int salesStageFrom, int salesStageTo,
			Date fromDate, Date toDate, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getPipelinePerformanceByServiceLineQueryString(userId);
			Query pipelinePerformanceByServiceLineQuery = entityManager
					.createNativeQuery(queryString);
			pipelinePerformanceByServiceLineQuery.setParameter("geography",
					geography);
			pipelinePerformanceByServiceLineQuery.setParameter(
					"displayGeography", displayGeography);
			pipelinePerformanceByServiceLineQuery.setParameter("iou", iou);
			pipelinePerformanceByServiceLineQuery.setParameter("currency",
					currency);
			pipelinePerformanceByServiceLineQuery.setParameter(
					"salesStageFrom", salesStageFrom);
			pipelinePerformanceByServiceLineQuery.setParameter("salesStageTo",
					salesStageTo);
			pipelinePerformanceByServiceLineQuery.setParameter("fromDate",
					fromDate);
			pipelinePerformanceByServiceLineQuery
					.setParameter("toDate", toDate);
			resultList = pipelinePerformanceByServiceLineQuery.getResultList();
			logger.info("Query string:PipelinePerformanceByServiceLine {}",
					queryString);
		}
		return resultList;
	}

	private String getPipelinePerformanceByServiceLineQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PIPELINE_PERFORMANCE_BY_SERVICE_LINE_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_SERVICE_LINE_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer
				.append(PIPELINE_PERFORMANCE_BY_SERVICE_LINE_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	public List<GeographyReport> getOpportunitiesByDispGeography(
			String financialYear, String quarter, String subSp, String iou,
			String currency, int salesStageFrom, int salesStageTo, String userId)
			throws Exception {
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<GeographyReport> geographyReports = new ArrayList<GeographyReport>();
		List<Object[]> opportunitiesByGeographyReports = null;
		opportunitiesByGeographyReports = findPipelinePerformanceByGeography(
				subSp, iou, currency, salesStageFrom, salesStageTo, fromDate,
				toDate, userId);
		if (opportunitiesByGeographyReports != null) {
			for (Object[] opportunityByGeography : opportunitiesByGeographyReports) {
				GeographyReport geographyReport = new GeographyReport();
				if (opportunityByGeography[0] != null) {
					geographyReport.setGeography(opportunityByGeography[0]
							.toString());
				}
				if (opportunityByGeography[1] != null) {
					geographyReport.setDigitalDealValue(new BigDecimal(
							opportunityByGeography[1].toString()));
				}
				geographyReports.add(geographyReport);
			}
		}

		if (geographyReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");
		return geographyReports;
	}

	private List<Object[]> findPipelinePerformanceByGeography(String subSp,
			String iou, String currency, int salesStageFrom, int salesStageTo,
			Date fromDate, Date toDate, String userId) throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getPipelinePerformanceByGeographyQueryString(userId);
			Query pipelinePerformanceByGeographyQuery = entityManager
					.createNativeQuery(queryString);
			pipelinePerformanceByGeographyQuery.setParameter("serviceLine",
					subSp);
			pipelinePerformanceByGeographyQuery.setParameter("iou", iou);
			pipelinePerformanceByGeographyQuery.setParameter("currency",
					currency);
			pipelinePerformanceByGeographyQuery.setParameter("salesStageFrom",
					salesStageFrom);
			pipelinePerformanceByGeographyQuery.setParameter("salesStageTo",
					salesStageTo);
			pipelinePerformanceByGeographyQuery.setParameter("fromDate",
					fromDate);
			pipelinePerformanceByGeographyQuery.setParameter("toDate", toDate);
			resultList = pipelinePerformanceByGeographyQuery.getResultList();
			logger.info("Query string:PipelinePerformanceByGeography {}",
					queryString);
		}
		return resultList;
	}

	private String getPipelinePerformanceByGeographyQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PIPELINE_PERFORMANCE_BY_GEO_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_GEO_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_GEO_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	public List<GeographyReport> getOpportunitiesBySubGeography(
			String financialYear, String quarter, String customerName,
			String serviceLine, String iou, String displayGeography,
			String geography, String currency, int salesStageFrom,
			int salesStageTo, String groupCustomer, String userId)
			throws Exception {

		List<String> custName = new ArrayList<String>();
		if (customerName.length() == 0 && groupCustomer.length() > 0) {
			custName = customerRepository
					.findByGroupCustomerName(groupCustomer);
			if (custName.isEmpty()) {
				logger.error("NOT_FOUND: Invalid Group Customer");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer");
			}
		} else {
			custName.add(customerName);
		}
		Date fromDate = getDate(financialYear, quarter, true);
		Date toDate = getDate(financialYear, quarter, false);
		List<GeographyReport> geographyReports = new ArrayList<GeographyReport>();
		List<Object[]> opportunitiesByGeographyReports = null;
		if (geography.isEmpty()) {
			opportunitiesByGeographyReports = findPipelinePerformanceBySubGeography(
					custName, serviceLine, iou, displayGeography, currency,
					fromDate, toDate, salesStageFrom, salesStageTo, userId);
		} else {
			opportunitiesByGeographyReports = findPipelinePerformanceByCountry(
					custName, serviceLine, iou, geography, currency, fromDate,
					toDate, salesStageFrom, salesStageTo, userId);
		}
		if (opportunitiesByGeographyReports != null) {
			for (Object[] opportunityByGeography : opportunitiesByGeographyReports) {
				GeographyReport geographyReport = new GeographyReport();
				if (opportunityByGeography[0] != null) {
					geographyReport.setGeography(opportunityByGeography[0]
							.toString());
				}
				if (opportunityByGeography[1] != null) {
					geographyReport.setDigitalDealValue(new BigDecimal(
							opportunityByGeography[1].toString()));
				}
				geographyReports.add(geographyReport);
			}
		}

		if (geographyReports.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"No Data Found");

		return geographyReports;
	}

	private List<Object[]> findPipelinePerformanceByCountry(
			List<String> custName, String serviceLine, String iou,
			String geography, String currency, Date fromDate, Date toDate,
			int salesStageFrom, int salesStageTo, String userId)
			throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getPipelinePerformanceByCountryQueryString(userId);
			Query pipelinePerformanceByCountryQuery = entityManager
					.createNativeQuery(queryString);
			pipelinePerformanceByCountryQuery.setParameter("serviceLine",
					serviceLine);
			pipelinePerformanceByCountryQuery.setParameter("iou", iou);
			pipelinePerformanceByCountryQuery.setParameter("geography",
					geography);
			pipelinePerformanceByCountryQuery.setParameter("customerName",
					custName);
			pipelinePerformanceByCountryQuery
					.setParameter("currency", currency);
			pipelinePerformanceByCountryQuery.setParameter("salesStageFrom",
					salesStageFrom);
			pipelinePerformanceByCountryQuery.setParameter("salesStageTo",
					salesStageTo);
			pipelinePerformanceByCountryQuery
					.setParameter("fromDate", fromDate);
			pipelinePerformanceByCountryQuery.setParameter("toDate", toDate);
			resultList = pipelinePerformanceByCountryQuery.getResultList();
			logger.info("Query string:PipelinePerformanceByCountry {}",
					queryString);
		}
		return resultList;
	}

	private String getPipelinePerformanceByCountryQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PIPELINE_PERFORMANCE_BY_COUNTRY_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GCMT_GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_COUNTRY_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_COUNTRY_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	private List<Object[]> findPipelinePerformanceBySubGeography(
			List<String> custName, String serviceLine, String iou,
			String displayGeography, String currency, Date fromDate,
			Date toDate, int salesStageFrom, int salesStageTo, String userId)
			throws Exception {
		List<Object[]> resultList = null;
		userId = DestinationUtils.getCurrentUserDetails().getUserId();
		if (validateUserAndUserGroup(userId)) {
			String queryString = getPipelinePerformanceBySubGeographyQueryString(userId);
			Query pipelinePerformanceBySubGeographyQuery = entityManager
					.createNativeQuery(queryString);
			pipelinePerformanceBySubGeographyQuery.setParameter("serviceLine",
					serviceLine);
			pipelinePerformanceBySubGeographyQuery.setParameter("iou", iou);
			pipelinePerformanceBySubGeographyQuery.setParameter("geography",
					displayGeography);
			pipelinePerformanceBySubGeographyQuery.setParameter("customerName",
					custName);
			pipelinePerformanceBySubGeographyQuery.setParameter("currency",
					currency);
			pipelinePerformanceBySubGeographyQuery.setParameter(
					"salesStageFrom", salesStageFrom);
			pipelinePerformanceBySubGeographyQuery.setParameter("salesStageTo",
					salesStageTo);
			pipelinePerformanceBySubGeographyQuery.setParameter("fromDate",
					fromDate);
			pipelinePerformanceBySubGeographyQuery.setParameter("toDate",
					toDate);
			resultList = pipelinePerformanceBySubGeographyQuery.getResultList();
			logger.info("Query string:PipelinePerformanceBySubGeography {}",
					queryString);
		}
		return resultList;
	}

	private String getPipelinePerformanceBySubGeographyQueryString(String userId)
			throws Exception {
		StringBuffer queryBuffer = new StringBuffer(
				PIPELINE_PERFORMANCE_BY_SUB_GEO_QUERY_PREFIX);
		HashMap<String, String> queryPrefixMap = userAccessPrivilegeQueryBuilder
				.getQueryPrefixMap(GEO_COND_PREFIX, SUBSP_COND_PREFIX,
						IOU_COND_PREFIX, CUSTOMER_COND_SUFFIX);

		String whereClause = userAccessPrivilegeQueryBuilder
				.getUserAccessPrivilegeWhereConditionClause(userId,
						queryPrefixMap);
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_SUB_GEO_COND_SUFFIX);
		if (whereClause != null && !whereClause.isEmpty()) {
			queryBuffer.append(Constants.AND_CLAUSE + whereClause);
		}
		queryBuffer.append(PIPELINE_PERFORMANCE_BY_SUB_GEO_GROUP_BY_ORDER_BY);
		return queryBuffer.toString();
	}

	public class MonthComparator implements Comparator<TargetVsActualResponse> {
		public int compare(TargetVsActualResponse a, TargetVsActualResponse b) {
			int firstIndex = 0;
			int secondIndex = 0;
			try {
				firstIndex = DateUtils.getMonthIndexOnQuarter(a
						.getSubTimeLine());

				secondIndex = DateUtils.getMonthIndexOnQuarter(b
						.getSubTimeLine());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (firstIndex < secondIndex)
				return -1;

			return 1;

		}
	}

	public class IOUActualComparator implements Comparator<IOUReport> {
		public int compare(IOUReport a, IOUReport b) {

			if (a != null && b != null)
				if (a.getActualRevenue() != null
						&& b.getActualRevenue() != null) {
					if (a.getActualRevenue().compareTo(b.getActualRevenue()) < 0)
						return 1;
				}
			return -1;

		}
	}

	public class IOUPipelineComparator implements Comparator<IOUReport> {
		public int compare(IOUReport a, IOUReport b) {
			if (a != null && b != null)
				if (a.getDigitalDealValue() != null
						&& b.getDigitalDealValue() != null) {
					if (a.getDigitalDealValue().compareTo(
							b.getDigitalDealValue()) < 0)
						return 1;
				}
			return -1;

		}
	}

	/**
	 * This Method is used to insert frequently searched group customer details
	 * 
	 * @param frequentlySearchedGroupCustomersT
	 * @return
	 */
	public boolean insertFrequentlySearchedGroupCustomer(
			FrequentlySearchedGroupCustomersT frequentlySearchedGroupCustomersT) {
		logger.info("Inside insertFrequentlySearchedGroupCustomer() Method");
		FrequentlySearchedGroupCustomersTPK frequentlySearchedGroupCustomersTPK = new FrequentlySearchedGroupCustomersTPK();
		frequentlySearchedGroupCustomersTPK
				.setGroupCustomerName(frequentlySearchedGroupCustomersT
						.getFreqSearchedGroupCustomer().getGroupCustomerName());
		frequentlySearchedGroupCustomersTPK.setUserId(DestinationUtils
				.getCurrentUserDetails().getUserId());
		frequentlySearchedGroupCustomersT
				.setFreqSearchedGroupCustomer(frequentlySearchedGroupCustomersTPK);
		boolean isInserted = false;
		String groupCustomerName = null;
		if (frequentlySearchedGroupCustomersT != null) {
			List<FrequentlySearchedGroupCustomersT> frequentlySearchedGroupCustomersTs = null;
			frequentlySearchedGroupCustomersTs = frequentlySearchedGroupCustomerTRepository
					.findByUserId(frequentlySearchedGroupCustomersT
							.getFreqSearchedGroupCustomer().getUserId());
			groupCustomerName = frequentlySearchedGroupCustomerTRepository
					.findByGroupCustomerName(frequentlySearchedGroupCustomersT
							.getFreqSearchedGroupCustomer()
							.getGroupCustomerName());

			if (groupCustomerName == null) {
				if (frequentlySearchedGroupCustomersTs.size() > 4) {
					frequentlySearchedGroupCustomerTRepository
							.delete(frequentlySearchedGroupCustomersTs.get(4));
				}
			}
			List<String> customerName = null;
			customerName = customerRepository
					.findByGroupCustomerName(frequentlySearchedGroupCustomersT
							.getFreqSearchedGroupCustomer()
							.getGroupCustomerName());

			if (customerName != null && !customerName.isEmpty()) {
				if (frequentlySearchedGroupCustomerTRepository
						.save(frequentlySearchedGroupCustomersT) != null) {
					isInserted = true;
				}
			} else {
				logger.error("Invalid Group Customer Name");
				throw new DestinationException(HttpStatus.NOT_FOUND,
						"Invalid Group Customer Name");
			}
		}
		return isInserted;
	}

	/**
	 * This Method used to retrieve the recently searched group customer name
	 * 
	 * @param userId
	 * @return
	 */
	public List<FrequentlySearchedGroupCustomersT> findGroupCustomerName(
			String userId) {
		List<FrequentlySearchedGroupCustomersT> frequentlySearchedGroupCustomersTs = null;
		frequentlySearchedGroupCustomersTs = frequentlySearchedGroupCustomerTRepository
				.findByUserId(userId);
		if (frequentlySearchedGroupCustomersTs == null
				|| frequentlySearchedGroupCustomersTs.isEmpty()) {
			logger.error("Recently Searched Group Customers Not Found");
			throw new DestinationException(HttpStatus.NOT_FOUND,
					"Recently Searched Group Customers Not Found");
		}
		return frequentlySearchedGroupCustomersTs;
	}
}