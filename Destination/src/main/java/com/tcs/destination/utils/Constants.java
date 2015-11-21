/**
 * 
 * Constants.java 
 *
 * @author TCS
 * @Version 1.0 - 2015
 * 
 * @Copyright 2015 Tata Consultancy 
 */
package com.tcs.destination.utils;


/**
 * This Constants class holds all the constants
 * 
 */
public class Constants {

	public static final String FILTER = "DestinationFilter";
	public static final String TIME_OUT = "TIME_OUT";
	public static final String NO = "NO";
	public static final String YES = "YES";
	public static final String Y = "Y";
	public static final String N = "N";
	public static final String FIELD = "F";
	public static final String COLLECTION = "C";
	public static final String ID_FIELD = "I";
	public static final String SYSTEM_USER = "System";
	public static final String SYSTEM_ADMIN = "System Admin";
	public static final String MIME = "MIME";
	public static final String UTF8 = "UTF-8";
	public static final String LOGIN_APP_VERSION = "APP_VERSION";
	public static final String GLOBAL = "GLOBAL_CUSTOMERS";
	public static final String SINGLE_QUOTE = "'";
	public static final String DOUBLE_SINGLE_QUOTE = "''";
	public static final String COMMA = ",";
	public static final String RIGHT_PARANTHESIS = ")";
	public static final String LEFT_PARANTHESIS = "(";
	public static final String AND_CLAUSE = " and ";
	public static final String OR_CLAUSE = " or ";
	public static final int ONE_DAY_IN_MILLIS = 86400000;
	public static final Double FIVE_MILLION = 5000000.0;
	public static final Double ONE_MILLION = 1000000.0;
	public static final Double TEN_MILLION = 10000000.0;
	public static final Double TWENTY_MILLION = 20000000.0;
	public static final String USD = "USD";
	public static final String SPACE = "";
	public static final String VALIDATOR_SHEET_NAME="Validate";
	public static final int OPPORTUNITY_DESC_MAX_SIZE = 1000;
	public static final int ENGAGEMENT_DURATION_MAX_SIZE = 15;
	public static final int NOTEST_MAX_SIZE = 1000;
	public static final int CORE_ATTRIBUTES_MAX_VALUE = 300;
	public static final String ROWNUMBER = "Row Number";
	public static final String SHEETNAME = "Sheet Name";
	public static final String ACTION_ADD = "Add"; 
	public static final String ERROR_MESSAGE = "Error Message";
	public static final String UPLOAD_ERRORS = "upload errors";
	public static final String UNKNOWN_CUSTOMER = "UNKNOWN%";
	public static final String EMPTY_PARANTHESIS = "()";
	public static final String GEOGRAPHY = "GEOGRAPHY";
	public static final String IOU = "IOU";
	
	public static final String APPLICATION_PROPERTIES_FILENAME = "application";
	public static final String OPPORTUNITY_TEMPLATE_LOCATION_PROPERTY_NAME = "opportunitySheetTemplate";
	public static final String CONNECT_TEMPLATE_LOCATION_PROPERTY_NAME = "connectSheetTemplate";
	public static final String OPPORTUNITY_TEMPLATE_COMPETITOR_SHEET_NAME = "Competitor(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_GEOGRAPHY_COUNTRY_SHEET_NAME = "Geography Country(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_OFFERING_SHEET_NAME = "Offering(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_USER_SHEET_NAME = "User(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_CUSTOMER_MASTER_SHEET_NAME = "Customer Master(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_SUBSP_SHEET_NAME = "SubSp(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_PARTNER_SHEET_NAME = "Partner(Ref)";  
	public static final String OPPORTUNITY_TEMPLATE_CUSTOMER_CONTACT_SHEET_NAME = "Customer Contact(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_PARTNER_CONTACT_SHEET_NAME = "Partner Contact(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_CURRENCY_SHEET_NAME = "Currency(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_WIN_LOSS_SHEET_NAME = "Win Loss Factor(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_BID_REQUEST_DEAL_TYPE_SHEET_NAME = "Bid Request  & Deal Type(Ref)";
	public static final String OPPORTUNITY_TEMPLATE_OPPORTUNITY_SHEET_NAME = "Opportunity";
	
	
	public static final String CONNECT = "Connect";
	public static final String OPPORTUNITY = "Opportunity";
	public static final String TASK = "Task";
	
	public static final String PATTERN = "\\<(.+?)\\>";
	public static final String TOKEN_USER = "user";
	public static final String TOKEN_ENTITY_NAME = "entityName";
	public static final String TOKEN_ENTITY_TYPE = "entityType";
	public static final String TOKEN_FROM = "from";
	public static final String TOKEN_TO = "to";
	public static final String TOKEN_SUBORDINATE = "subordinate";
	public static final String DATE_TYPE = "dateType";
	public static final String DATE = "date";
	public static final String SALES_STAGE = "sales_stage";
	public static final String FILE_DIR_SEPERATOR = "/";
	public static final String FILE_PATH = "FILE_PATH";
	public static final String USER_ID = "USER_ID";
	public static final String NEXT_STEP = "NEXT_STEP";
	public static final String REQUEST_ID = "REQUEST_ID";
	public static final String REQUEST = "REQUEST";
	
	public static final String CUSTOMER_TEMPLATE_LOCATION_PROPERTY_NAME = "customerSheetTemplate";
	public static final String CUSTOMER_MASTER_SHEET_NAME = "Customer Master";
	public static final String FINANCE_MAPPING_SHEET_NAME = "Finance Mappping";
	public static final String BEACON_MAPPING_SHEET_NAME = "Beacon Mapping";
	public static final String IOU_CUSTOMER_MAPPING_REF= "IOU Customer Map(Ref)";
	public static final String IOU_BEACON_MAP_REF= "IOU Beacon Map(Ref)";
	
	// Actual Revenues Data template
	public static final String ACTUAL_REVENUE_DATA_TEMPLATE_LOCATION_PROPERTY_NAME = "actualRevenueDataTemplate";
	public static final String ACTUAL_REVENUE_DATA = "Actual Revenue - DATA";
	public static final String FINANCE_MAP_REF = "Finance Map(Ref)";
	public static final String CUSTOMER_IOU_MAPPING_REF= "Customer IOU Map(Ref)";
	public static final String SUB_SP_MAP_REF = "Sub Sp Map(Ref)";
	// Email subjects
	public static final String USER_UPLOAD_NOTIFY_SUBJECT = "User upload request submitted.";
	public static final String CUSTOMER_UPLOAD_NOTIFY_SUBJECT = "Customer upload request submitted.";
	public static final String CONNECT_UPLOAD_NOTIFY_SUBJECT = "Connect upload request submitted.";
	public static final String OPPORTUNITY_UPLOAD_NOTIFY_SUBJECT = "Opportunity upload request submitted.";
	public static final String ACTUAL_REVENUE_UPLOAD_NOTIFY_SUBJECT = "Actual revenue upload request submitted.";
	public static final String CUSTOMER_CONTACT_UPLOAD_NOTIFY_SUBJECT = "Customer contact upload request submitted.";
	public static final String PARTNER_UPLOAD_NOTIFY_SUBJECT = "Partner upload request submitted.";
	public static final String PARTNER_CONTACT_UPLOAD_NOTIFY_SUBJECT = "Partner contact upload request submitted.";
	public static final String BEACON_UPLOAD_NOTIFY_SUBJECT = "Beacon upload request submitted.";
	public static final String USER_UPLOAD_SUBJECT = "User upload request processed.";
	public static final String CUSTOMER_UPLOAD_SUBJECT = "Customer upload request processed.";
	public static final String CONNECT_UPLOAD_SUBJECT = "Connect upload request processed.";
	public static final String OPPORTUNITY_UPLOAD_SUBJECT = "Opportunity upload request processed.";
	public static final String ACTUAL_REVENUE_UPLOAD_SUBJECT = "Actual revenue upload request processed.";
	public static final String CUSTOMER_CONTACT_UPLOAD_SUBJECT = "Customer contact upload request processed.";
	public static final String PARTNER_UPLOAD_SUBJECT = "Partner upload request processed.";
	public static final String PARTNER_CONTACT_UPLOAD_SUBJECT = "Partner contact upload request processed.";
	public static final String BEACON_UPLOAD_SUBJECT = "Beacon upload request processed.";
	public static final String USER_DOWNLOAD_SUBJECT = "User download request processed.";
	public static final String CUSTOMER_DOWNLOAD_SUBJECT = "Customer download request processed.";
	public static final String CONNECT_DOWNLOAD_SUBJECT = "Connect download request processed.";
	public static final String OPPORTUNITY_DOWNLOAD_SUBJECT = "Opportunity download request processed.";
	public static final String ACTUAL_REVENUE_DOWNLOAD_SUBJECT = "Actual revenue download request processed.";
	public static final String CUSTOMER_CONTACT_DOWNLOAD_SUBJECT = "Customer contact download request processed.";
	public static final String PARTNER_DOWNLOAD_SUBJECT = "Partner download request processed.";
	public static final String PARTNER_CONTACT_DOWNLOAD_SUBJECT = "Partner contact download request processed.";
	public static final String BEACON_DOWNLOAD_SUBJECT = "Beacon download request processed.";
	
	public static final String PARTNER_TEMPLATE_LOCATION_PROPERTY_NAME = "partnerSheetTemplate";
	public static final String PARTNER_TEMPLATE_PARTNER_SHEET_NAME = "Partner Master";
	public static final String PARTNER_TEMPLATE_PARTNER_CONTACT_SHEET_NAME = "Partner Contacts";
	
	// Beacon Data template
	public static final String BEACON_TEMPLATE_LOCATION_PROPERTY_NAME = "beaconSheetTemplate";
	public static final String BEACON_TEMPLATE_BEACON_SHEET_NAME = "BEACON - DATA";
	public static final String BEACON_MAPPING_TEMPLATE_BEACON_SHEET_NAME = "Beacon Mapping(Ref)";
	public static final String BEACON_IOU_MAPPING_TEMPLATE_BEACON_SHEET_NAME="IOU Map(Ref)";
	
	public static final String DOWNLOAD = "download";
	public static final String CUSTOMER_MAP = "CUSTOMER_MAP";

}