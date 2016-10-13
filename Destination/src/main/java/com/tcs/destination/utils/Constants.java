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
	public static final String UNKNOWN_PARTNER = "UNKNOWN%";
	public static final String EMPTY_PARANTHESIS = "()";
	public static final String GEOGRAPHY = "GEOGRAPHY";
	public static final String IOU = "IOU";
	public static final String CUSTOMER = "CUSTOMER";
	public static final String PARTNER ="PARTNER";
	
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
	public static final String OPPORTUNITY_TEMPLATE_TCS_AND_CUSTOMER_SHEET_NAME = "TCS & Customer Contact(Ref)";
	
	public static final String BFM_DEAL_FINANCIAL_TEMPLATE_LOCATION_PROPERTY_NAME = "bfmDealFinancialSheetTemplate";

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
	public static final String TOKEN_CST_OR_PARTNER = "customerOrPartner";
	public static final String TOKEN_CST_OR_PARTNER_VALUE = "custOrPartValue";
	public static final String TOKEN_PRIMARY_OWNER = "primaryOwner";
	public static final String TOKEN_SECONDARY_OWNERS = "secondaryOwners";
	public static final String TOKEN_OWNERSHIP = "ownership";
	
	public static final String CUSTOMER_TEMPLATE_LOCATION_PROPERTY_NAME = "customerSheetTemplate";
	public static final String CUSTOMER_CONTACT_TEMPLATE_LOCATION_PROPERTY_NAME = "customerContactSheetTemplate";
	public static final String USER_TEMPLATE_LOCATION_PROPERTY_NAME = "userSheetTemplate";
	public static final String CUSTOMER_MASTER_SHEET_NAME = "Customer Master";
	public static final String CUSTOMER_CONTACT_SHEET_NAME = "Customer Contact";
	public static final String FINANCE_MAPPING_SHEET_NAME = "Finance Mappping";
	public static final String BEACON_MAPPING_SHEET_NAME = "Beacon Mapping";
	public static final String IOU_CUSTOMER_MAPPING_REF= "IOU Customer Map(Ref)";
	public static final String CUSTOMER_MASTER_REF="Customer Master(Ref)";
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
	public static final String PRODUCT_UPLOAD_NOTIFY_SUBJECT = "Product upload request submitted";
	public static final String PRODUCT_CONTACT_UPLOAD_NOTIFY_SUBJECT = "Product contact upload request submitted";
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
	public static final String OPPORTUNITY_DAILY_DOWNLOAD_SUBJECT = "Opportunity daily download";
	
	//added as part of partner changes 
	public static final String PRODUCT_UPLOAD_SUBJECT = "Product upload request processed.";
	public static final String PRODUCT_DOWNLOAD_SUBJECT = "Product download request processed.";
	public static final String PRODUCT_CONTACT_UPLOAD_SUBJECT = "Product contact upload request processed.";
	public static final String PRODUCT_CONTACT_DOWNLOAD_SUBJECT = "Product contact download request processed.";
	public static final String PRODUCT_MASTER_SHEET = "Product Master";
	public static final String PRODUCT_MASTER_REF_SHEET = "Product Master(Ref)";
	public static final String XLS = ".xls";
	public static final String PRODUCT_TEMPLATE_LOCATION_PROPERTY_NAME = "productSheetTemplate";
	public static final String RGS_TEMPLATE_LOCATION_PROPERTY_NAME = "rgsDetailsSheetTemplate";
	
	public static final String PARTNER_MASTER_UPLOAD_SUBJECT = "Partner upload request processed.";
	public static final String PARTNER_MASTER_DOWNLOAD_SUBJECT = "Partner download request processed.";
	public static final String PRODUCT_CONTACT_SHEET = "Product Contacts";
    public static final String PRODUCT_CONTACT_TEMPLATE_LOCATION_PROPERTY_NAME = "productContactSheetTemplate";
	
    public static final String RGS_UPLOAD_SUBJECT  = "RGS upload request processed.";
	public static final String RGS_DETAILS_SHEET_NAME = "RGS_Details";

	public static final String PARTNER_TEMPLATE_LOCATION_PROPERTY_NAME = "partnerSheetTemplate";
	public static final String PARTNER_CONTACT_TEMPLATE_LOCATION_PROPERTY_NAME = "partnerContactSheetTemplate" ;
	public static final String PARTNER_TEMPLATE_PARTNER_SHEET_NAME = "Partner Master";
	public static final String PARTNER_SUBSP_TEMPLATE_SHEET_NAME = "Partner SubSp";
	public static final String PARTNER_SUBSP_PRODUCT_TEMPLATE_SHEET_NAME = "Partner subsp Product";
	public static final String PARTNER_MASTER_REF_PARTNER_SHEET_NAME = "Partner Master(Ref)";
	public static final String PARTNER_TEMPLATE_PARTNER_CONTACT_SHEET_NAME = "Partner Contacts";
	
	// Beacon Data template
	public static final String BEACON_TEMPLATE_LOCATION_PROPERTY_NAME = "beaconSheetTemplate";
	public static final String BEACON_TEMPLATE_BEACON_SHEET_NAME = "BEACON - DATA";
	public static final String BEACON_MAPPING_TEMPLATE_BEACON_SHEET_NAME = "Beacon Mapping(Ref)";
	public static final String BEACON_IOU_MAPPING_TEMPLATE_BEACON_SHEET_NAME="IOU Map(Ref)";
	
	public static final String DOWNLOAD = "download";
	public static final String UPLOAD = "upload";
	public static final String CUSTOMER_MAP = "CUSTOMER_MAP";
	
	//User data template
	public static final String USER_TEMPLATE_USER_MASTER = "BDM Users";
	public static final String USER_TEMPLATE_TIMEZONE= "TimeZone(Ref)";
	public static final String USER_TEMPLATE_USERGOALS = "BDM Target";
	public static final String USER_TEMPLATE_USERGOALREF = "Targets Values(Ref)";
	public static final String USER_TEMPLATE_CUSTOMER = "Customer(Ref)";
	public static final String USER_TEMPLATE_PRIVILEGE = "User Privilege";
	public static final String USER_TEMPLATE_OTHER_REFERENCES = "Geo Country  IOU SubSp(Ref)";
	
	//Connect data template
	public static final String CONNECT_TEMPLATE_CONNECT_SHEET_NAME = "Connect";
	public static final String CONNECT_TEMPLATE_CUSTOMER_MASTER_SHEET_NAME = "Customer Master(Ref)";
	public static final String CONNECT_TEMPLATE_PARTNER_MASTER_SHEET_NAME = "Partner Master(Ref)";
	public static final String CONNECT_TEMPLATE_GEOGRAPHY_COUNTRY_SHEET_NAME = "Geography Country(Ref)";
	public static final String CONNECT_TEMPLATE_SUBSP_SHEET_NAME = "SubSp(Ref)";
	public static final String CONNECT_TEMPLATE_OFFERING_SHEET_NAME = "Offering(Ref)";
	public static final String CONNECT_TEMPLATE_CUSTOMER_CONTACT_SHEET_NAME = "Customer Contact(Ref)";
	public static final String CONNECT_TEMPLATE_PARTNER_CONTACT_SHEET_NAME = "Partner Contact(Ref)";
	public static final String CONNECT_TEMPLATE_USER_SHEET_NAME = "User(Ref)";
	public static final String CONNECT_TEMPLATE_CONNECT_TYPE_SHEET_NAME = "Connect Type(Ref)";
	public static final String CONNECT_TEMPLATE_TIME_ZONE_SHEET_NAME = "TimeZone(Ref)";
	
	//Partner Data sheet names
	public static final String PARTNER_TEMPLATE_GEOGRAPHY_REF_SHEET_NAME = "Geography(Ref)";
	public static final String PARTNER_TEMPLATE_SUBSP_REF_SHEET_NAME = "SubSp(ref)";
	public static final String PARTNER_TEMPLATE_PRODUCT_REF_SHEET_NAME = "Product(ref)";
	
	public static final String DOWNLOADCONSTANT = "Download_";
	public static final String XLSM = ".xlsm";
	public static final String PARTNER_MASTER_SHEET_NAME = "Partner Master";
	public static final String NOTIFICATION_CUSTOMER = "CUSTOMER";
	public static final String NOTIFICATION_PRIMARY_OWNER = "(Primary)";
	public static final String NOTIFICATION_SECONDARY_OWNER = "(Secondary)";
	public static final String NOTIFICATION_SALES_SUPPORT = "(Sales support)";
	public static final String NOTIFICATION_BID_OFFICE_GRP_OWNER = "(Bid Office Group Owner)";
	public static final String PMO_KEYWORD = "pmo";
	public static final String WORKFLOW_GEO_HEADS_PMO = "GEO Heads,PMO";
	public static final String WORKFLOW_GEO_HEADS = "GEO Heads";
	public static final String WORKFLOW_IOU_HEADS = "IOU Heads";
	public static final String WORKFLOW_STRATEGIC_ADMIN = "Strategic Group Admin";
	public static final String WORKFLOW_CUSTOMER = "Customer";
	public static final String WORKFLOW_PARTNER = "Partner";
	public static final String WORKFLOW_COMPETITOR = "Competitor";
	public static final String WORKFLOW_OPPORTUNITY_REOPEN = "Opportunity";
	public static final String WORKFLOW_COMMENTS = "Please find the comment :";
	public static final String WORKFLOW_OPERATION_CREATION_TEMPLATE = "for creation of";
	public static final String WORKFLOW_OPERATION_REOPEN_TEMPLATE = "to reopen the";
	public static final String WORKFLOW_OPERATION_CREATE = "create";
	public static final String WORKFLOW_OPERATION_REOPEN = "reopen";
	public static final String WORKFLOW_REOPEN_PREFIX = "Please find the reason for reopen :";
	public static final String WORKFLOW_PMO = "PMO";
	public static final String FROM = "from";
	public static final int CONSTANT_ZERO = 0;
	public static final int CONSTANT_ONE = 1;
	public static final int CONSTANT_TWO = 2;
	public static final int CONSTANT_NINE = 9;
	public static final int CONSTANT_FOUR = 4;
	public static final int CONSTANT_FIVE = 5;
	public static final int CONSTANT_SIX = 6;
	public static final int CONSTANT_SEVEN = 7;
	public static final int CONSTANT_EIGHT = 8;
	
	
	//For Opportunity win/loss mail
	public static final String 	WITH_SUPPORT_FROM = "with support from";
	public static final String NOT_AVAILABLE = "Not Available";
	public static final String LOSS_OTHER = "Loss - Other";
	public static final String WIN_OTHER = "Win - Other";
	public static final String OTHER = "Other";
	public static final String NONE = "None";
	
	public static final String WORKFLOW_CUSTOMER_PENDING_SUBJECT = "Pending New Customer Request";
	public static final String WORKFLOW_PARTNER_PENDING_SUBJECT = "Pending New Partner Request";
	public static final String WORKFLOW_OPPORTUNITY_REOPEN_PENDING_SUBJECT = "Pending Opportunity reopen request";
	public static final String WORKFLOW_COMPETITOR_PENDING_SUBJECT = "Pending New Competitor Request";
	
	public static final String ENVIRONMENT_NAME = "environment.name";
	public static final String TCS_UAT = "tcs_uat";
	public static final String TCS_PROD = "tcs_prod";
	public static final String TCS_SIT = "tcs_sit";
	public static final String TCS_DEV = "tcs_dev";
	public static final String UAT = "UAT";
	public static final String SIT = "SIT";
	public static final String PROD = "PROD";
	public static final String USD_PATTERN = "###,###.## USD";
	
	//Notification Related
	public static final String PRIMARY_OWNER_FIELD = "Primary Owner";
	public static final String SECONDARY_OWNER_FIELD = "Secondary Owner";
	public static final String START_DATE_TIME_OF_CONNECT_FIELD = "Start Date time of connect";
	public static final String END_DATE_TIME_OF_CONNECT_FIELD = "End Date time of connect";
	public static final String DIGITAL_DEAL_VALUE_FIELD = "Digital Deal Value";
	public static final String SALES_STAGE_FIELD = "Sales Stage";
	public static final String SALES_SUPPORT_OWNER_FIELD = "Sales Support Owners";
	public static final String BID_OFFICE_GROUP_OWNER_FIELD = "Bid Office Group Owner";
	public static final String BID_FIELD = "Bid";
	public static final String ACTUAL_BID_SUBMISSION_DATE_FIELD = "Actual Bid Submission Date";
	public static final String TARGET_BID_SUBMISSION_DATE_FIELD = "Target Bid Submission Date";
	public static final String EXPECTED_DATE_OF_OUTCOME_FIELD = "Expected Date of Outcome";
	public static final String TASK_STATUS_FIELD = "Task Status";
	public static final String TARGET_DATE_OF_COMPLETION_FIELD = "Target date for completion";
	public static final String TASK_OWNER_FIELD = "Task Owner";
	public static final String FROM_TO_STRING = " of <entityType> : <entityName> from <from> to <to>";
	public static final String ENTITY_NAME_TO_STRING = " : <to> for <entityType> : <entityName>";
	public static final String ENTITY_NAME_STRING = " of <entityType> : <entityName>";
	public static final String WON = "Won";
	public static final String LOST = "Lost";
	public static final String UPDATED = "updated";
	public static final String ADDED = "added";
	public static final String REMOVED = "removed";
	public static final String SYSTEM = "SYSTEM";
	
	public static final String AUDIT_CONNECT = "Audit Connect";
	public static final String AUDIT_CONNECT_SEC_OWNERS = "Audit Connect Secondary Owners";
	public static final String AUDIT_OPPORTUNITY = "Audit Opportunity";
	public static final String AUDIT_OPP_SALES_SUPPORT = "Audit Opportunity Sales Support Owners";
	public static final String AUDIT_BID_DETAILS = "Audit Bid Details";
	public static final String AUDIT_BID_OFFICE_GRP_OWNER = "Audit Bid Office Group Owner";
	public static final String AUDIT_TASK = "Audit Task";
	public static final String AUDIT_TASK_BDM_TAGGED = "Audit Task BDM Tagged";
	
	//added for share link
	public static final String shareConnectPortal="<User> has shared Connect: <ConnectName> with you";
	public static final String shareOpportunityPortal="<User> has shared Opportunity: <OpportunityName> with you";
	
	//Workflow BFM
	public static final String E1 = "E1";
	public static final String E2 = "E2";
	public static final String E3 = "E3";
	public static final String E4 = "E4";
	public static final String E5 = "E5";
	
	public static final String WORKFLOW_BFM_STEP1_PENDING_SUBJECT = "Deal Financial Submitted - ";
	public static final String WORKFLOW_BFM_STEP1_APPROVED_SUBJECT = "Deal Financial Approved - ";
	public static final String WORKFLOW_BFM_STEP1_REJECTED_SUBJECT = "Deal Financial Rejected - ";
	public static final String WORKFLOW_BFM_ESCALATE_PENDING_SUBJECT = "Deal Financial Exception Initiated for ";
	public static final String WORKFLOW_BFM_ESCALATE_PATH_A_APPROVED_SUBJECT = "Deal Financial Exception Approved - ";
	public static final String WORKFLOW_BFM_ESCALATE_PATH_A_REJECTED_SUBJECT = "Deal Financial Exception Rejected - ";
	public static final String WORKFLOW_BFM_ESCALATE_PATH_B_APPROVED_SUBJECT = "Deal Financial Exception Approved - ";
	public static final String WORKFLOW_BFM_ESCALATE_PATH_B_REJECTED_SUBJECT = "Deal Financial Exception Rejected - ";
	public static final String CONSULTING = "CONSULTING";
	public static final String ABIM_CONSULTING = "ABIM – Consulting";
	
	
	public static final String defaultUser_CC_BFM = "139339";
	public static final String notifyUserRolesForBFM = "System Admin,Strategic Group Admin";
	public static final String DELIVERY_MANAGER = "Delivery Manager";
	public static final Integer DELIVERY_CENTRE_OPEN = -1;

	
	//RGS Sheet column index
	public static final int RGS_ID_COL_INDEX = 1;
	public static final int REQ_ID_COL_INDEX = 2;
	public static final int CUSTOMER_COL_INDEX = 9;
	public static final int BRANCH_COL_INDEX = 10;
	public static final int LOCATION_COL_INDEX = 7;
	public static final int COMPETENCY_COL_INDEX = 4;
	public static final int SUB_COMP_COL_INDEX = 5;
	public static final int EXPERIENCE_COL_INDEX = 6;
	public static final int ROLE_COL_INDEX = 3;
	public static final int STATUS_COL_INDEX = 8;
	public static final int IOU_COL_INDEX = 11;
	public static final int EMP_ID_COL_INDEX = 13;
	public static final int EMP_NAME_COL_INDEX = 12;
	public static final int FULFILL_DATE_COL_INDEX = 14;
	public static final int REQ_START_DATE_COL_INDEX = 15;
	public static final int REQ_END_DATE_COL_INDEX = 16;
	
}
