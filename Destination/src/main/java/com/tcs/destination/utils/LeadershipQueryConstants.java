package com.tcs.destination.utils;

/**
 * This constant file holds the Query Constants for all services related to Leadership Dashboard
 * 
 */
public class LeadershipQueryConstants {

    public static final String TEAM_CONNECTS_GEO_COND_PREFIX = "RCMT.customer_geography in (";
    
    public static final String TEAM_CONNECTS_IOU_COND_PREFIX = "ICMT.display_iou in (";
    
    public static final String TEAM_CONNECTS_SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
    
    public static final String TEAM_CONNECTS_CUSTOMER_COND_PREFIX = "RCMT.customer_name in (";

    public static final String TEAM_CONNECTS_QUERY_PART1 = "SELECT DISTINCT c2.connect_id FROM connect_t c2 JOIN customer_master_t CMT ON  CMT.customer_id=c2.customer_id JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography JOIN connect_sub_sp_link_t CSL on c2.connect_id=CSL.connect_id JOIN sub_sp_mapping_t SSM on CSL.sub_sp=SSM.sub_sp JOIN revenue_customer_mapping_t RCMT ON RCMT.customer_geography=GMT.geography WHERE (((c2.connect_id IN ((SELECT c1.connect_id FROM Connect_T c1 WHERE c1.primary_owner IN (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";

    public static final String TEAM_CONNECTS_QUERY_PART2 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)) UNION (SELECT c.connect_id FROM Connect_T c, connect_secondary_owner_link_T cs WHERE (c.connect_id=cs.connect_id) AND (cs.secondary_owner IN (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";

    public static final String TEAM_CONNECTS_QUERY_PART3 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)))))) AND (c2.start_datetime_of_connect between '";

    public static final String TEAM_CONNECTS_QUERY_PART4 = "' AND '";

    public static final String TEAM_CONNECTS_QUERY_PART5 = "' )) AND (GMT.display_geography='";

    public static final String TEAM_CONNECTS_QUERY_PART6 = "' OR '";

    public static final String TEAM_CONNECTS_QUERY_PART7 = "' = '')";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART1 = "select DISTINCT (OPP.opportunity_id) from opportunity_t OPP JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography JOIN customer_master_t CMT ON CMT.customer_id=OPP.customer_id JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou JOIN revenue_customer_mapping_t RCMT ON GMT.geography=RCMT.customer_geography and (GMT.display_geography = '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART1a = "' or '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART2 = "' = '') where OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART3 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)) union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";

    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART4 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)) union (select opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART5 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)))) and (OPP.digital_deal_value <> 0) and (OPP.sales_stage_code=9) and OPP.deal_closure_date between '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART6 = TEAM_CONNECTS_QUERY_PART4;
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART7 = Constants.SINGLE_QUOTE;
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART1 = "select OPP1.opportunity_id, OPP1.digital_deal_value * ((select conversion_rate from beacon_convertor_mapping_t where currency_name=OPP1.deal_currency) / (select conversion_rate from beacon_convertor_mapping_t where currency_name = 'USD')), OPP1.sales_stage_code from opportunity_t OPP1 where OPP1.opportunity_id in (select DISTINCT (OPP.opportunity_id) from opportunity_t OPP JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography JOIN customer_master_t CMT ON CMT.customer_id=OPP.customer_id JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou  JOIN revenue_customer_mapping_t RCMT ON GMT.geography=RCMT.customer_geography and (GMT.display_geography = '";
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART1a = "' or '";
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART2 = "' = '') where OPP.opportunity_id in ((select opportunity_id from opportunity_t where opportunity_owner in (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART3 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)) union (select opportunity_id from opportunity_sales_support_link_t where sales_support_owner in (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '"; 
  
    public static final String TEAM_OPPORTUNITY_QUERY_PART4 = "' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)) union (select opportunity_id from bid_details_t BDT where BDT.bid_id in (select bid_id from bid_office_group_owner_link_t where bid_office_group_owner in (WITH RECURSIVE U1 AS (SELECT * FROM user_t WHERE supervisor_user_id = '";

    public static final String TEAM_OPPORTUNITY_QUERY_PART5 ="' UNION ALL SELECT U2.* FROM user_t U2 JOIN U1 ON U2.supervisor_user_id = U1.user_id) SELECT U1.user_id FROM U1 ORDER BY U1.user_id asc)))) and (OPP.digital_deal_value <> 0) and OPP.deal_closure_date between '";

    public static final String TEAM_OPPORTUNITY_QUERY_PART6 =  TEAM_CONNECTS_QUERY_PART4;

    public static final String TEAM_OPPORTUNITY_QUERY_PART7 = Constants.SINGLE_QUOTE;
    
    public static final String TEAM_OPPORTUNITY_QUERY_SUFFIX = ") order by OPP1.sales_stage_code";
}
