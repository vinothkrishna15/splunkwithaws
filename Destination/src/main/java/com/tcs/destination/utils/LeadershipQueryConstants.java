package com.tcs.destination.utils;

/**
 * This constant file holds the Query Constants for all services related to Leadership Dashboard
 * 
 */
public class LeadershipQueryConstants {

    public static final String TEAM_CONNECTS_GEO_COND_PREFIX = "CMT.geography in (";
    
    public static final String TEAM_CONNECTS_IOU_COND_PREFIX = "ICMT.display_iou in (";
    
    public static final String TEAM_CONNECTS_SUBSP_COND_PREFIX = "SSMT.display_sub_sp in (";
    
    public static final String TEAM_CONNECTS_CUSTOMER_COND_PREFIX = "CMT.customer_name in (";
    
    public static final String TEAM_CONNECTS_QUERY_PART1 = "select DISTINCT CONNECT.* FROM (SELECT DISTINCT c2.* FROM connect_t c2 JOIN customer_master_t CMT ON  CMT.customer_id=c2.customer_id JOIN iou_customer_mapping_t ICMT ON  CMT.iou=ICMT.iou JOIN geography_mapping_t GMT ON CMT.geography=GMT.geography JOIN geography_country_mapping_t GCM ON GMT.geography=GCM.geography LEFT JOIN connect_sub_sp_link_t CSL on c2.connect_id=CSL.connect_id LEFT JOIN sub_sp_mapping_t SSMT on CSL.sub_sp=SSMT.sub_sp WHERE ((c2.start_datetime_of_connect between (:fromDate) AND (:toDate) )) AND (GMT.display_geography=(:displayGeography) OR (:displayGeography)= '')";
    
   // public static final String TEAM_CONNECTS_QUERY_PART2 = " UNION SELECT c2.* FROM connect_t c2 WHERE c2.connect_category='PARTNER' and c2.start_datetime_of_connect between (:fromDate) AND (:toDate)) AS CONNECT order by location";
    public static final String TEAM_CONNECTS_QUERY_PART2 = ") AS CONNECT";
    
    public static final String TEAM_CONNECTS_QUERY_PART4 = "' AND '";
    
    public static final String TEAM_CONNECTS_QUERY_PARTNER = "select * from connect_t where connect_category  ='PARTNER' and start_datetime_of_connect between (:fromDate) and (:toDate) order by location";
       
    public static final String TEAM_CONNECTS_QUERY_CUSTOMER = "select distinct * from connect_t where primary_owner in(:userIdList) and connect_category='CUSTOMER' and start_datetime_of_connect between (:fromDate) AND (:toDate) UNION select distinct * from connect_t where connect_id in(select connect_id from connect_secondary_owner_link_t  where secondary_owner in(:userIdList))AND connect_category ='CUSTOMER' AND (start_datetime_of_connect between (:fromDate) AND (:toDate))";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART1 = "select DISTINCT (OPP.opportunity_id) from opportunity_t OPP LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country JOIN geography_mapping_t GMT on GCMT.geography = GMT.geography JOIN customer_master_t CMT ON CMT.customer_id=OPP.customer_id and GMT.geography=CMT.geography JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou and (GMT.display_geography = '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART1a = "' or '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART2 = "' = '') where (OPP.digital_deal_value <> 0) and (OPP.sales_stage_code=9) and OPP.deal_closure_date between '";
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART3 =TEAM_CONNECTS_QUERY_PART4;
    
    public static final String TEAM_OPPORTUNITY_WIN_QUERY_PART4 = Constants.SINGLE_QUOTE;
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART1 = "select DISTINCT (OPP.*) from opportunity_t OPP LEFT JOIN opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id LEFT JOIN sub_sp_mapping_t SSMT on OSSL.sub_sp = SSMT.sub_sp JOIN geography_country_mapping_t GCMT on GCMT.country = OPP.country JOIN geography_mapping_t GMT on GMT.geography = GCMT.geography JOIN customer_master_t CMT ON CMT.customer_id=OPP.customer_id and GMT.geography=CMT.geography JOIN iou_customer_mapping_t ICMT ON CMT.iou=ICMT.iou  and (GMT.display_geography = '";
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART1a = "' or '";
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART2 = "' = '') where ((OPP.sales_stage_code >= 9  and OPP.deal_closure_date between '";
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART3 =  TEAM_CONNECTS_QUERY_PART4;
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART4= Constants.SINGLE_QUOTE;
    
    public static final String TEAM_OPPORTUNITY_QUERY_PART5=") or sales_stage_code < 9 )";
    
    public static final String TEAM_OPPORTUNITY_QUERY_SUFFIX = " order by OPP.country";
    
}
