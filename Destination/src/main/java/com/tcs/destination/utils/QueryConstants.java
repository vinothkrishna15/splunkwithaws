package com.tcs.destination.utils;


public class QueryConstants {
	
	public static final String BID_DETAILS_TRGT_DT_QUERY = "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, "
			+ "bd.target_bid_submission_date, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, user_general_settings_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and op.opportunity_id = bd.opportunity_id and op.opportunity_owner = us.user_id "
			+ "and us.user_id = ut.user_id and op.customer_id = cs.customer_id and CURRENT_DATE = (bd.target_bid_submission_date - CAST(us.event_reminder AS INTEGER)) "
			+ "and bd.actual_bid_submission_date is null "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.target_bid_submission_date, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, opportunity_sales_support_link_t oss, user_general_settings_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id "
			+ "and oss.sales_support_owner = us.user_id and us.user_id = ut.user_id  and op.customer_id = cs.customer_id "
			+ "and CURRENT_DATE = (bd.target_bid_submission_date - CAST(us.event_reminder AS INTEGER)) and bd.actual_bid_submission_date is null "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.target_bid_submission_date, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, bid_office_group_owner_link_t bogo, user_general_settings_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id "
			+ "and bogo.bid_office_group_owner = us.user_id and us.user_id = ut.user_id and op.customer_id = cs.customer_id "
			+ "and CURRENT_DATE = (bd.target_bid_submission_date - CAST(us.event_reminder AS INTEGER)) and bd.actual_bid_submission_date is null"; 
													
	
	public static final String BID_DETAILS_OUTCOME_DT_QUERY = "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.expected_date_of_outcome, "
			+ "'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, user_general_settings_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and op.opportunity_owner = us.user_id and us.user_id = ut.user_id "
			+ "and op.customer_id = cs.customer_id and CURRENT_DATE = (bd.expected_date_of_outcome - CAST(us.event_reminder AS INTEGER)) "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.expected_date_of_outcome, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, opportunity_sales_support_link_t oss, user_general_settings_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id "
			+ "and oss.sales_support_owner = us.user_id and us.user_id = ut.user_id and op.customer_id = cs.customer_id "
			+ "and CURRENT_DATE = (bd.expected_date_of_outcome - CAST(us.event_reminder AS INTEGER)) "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.expected_date_of_outcome, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, bid_office_group_owner_link_t bogo, user_general_settings_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id "
			+ "and bogo.bid_office_group_owner = us.user_id and us.user_id = ut.user_id and op.customer_id = cs.customer_id "
			+ "and CURRENT_DATE = (bd.expected_date_of_outcome - CAST(us.event_reminder AS INTEGER)) ";
	
	public static final String TASK_TRGT_DT_DUE_QUERY = "select t.task_id, t.task_description, us.user_id, ut.user_name, t.target_date_for_completion, "
			+ "t.entity_reference, COALESCE(t.connect_id, t.opportunity_id) from task_t t, user_general_settings_t us, user_t ut "
			+ "where t.task_owner  = us.user_id and us.user_id = ut.user_id "
			+ "and CURRENT_DATE = (t.target_date_for_completion - CAST(us.event_reminder AS INTEGER)) and t.task_status != 'CLOSED' "
			+ "UNION "
			+ "select t.task_id, t.task_description, us.user_id, ut.user_name, t.target_date_for_completion, "
			+ "t.entity_reference, COALESCE(t.connect_id, t.opportunity_id) from task_t t,user_general_settings_t us, task_bdms_tagged_link_t tb,user_t ut "
			+ "where t.task_id  = tb.task_id and tb.bdms_tagged = us.user_id and us.user_id = ut.user_id "
			+ "and  CURRENT_DATE = (t.target_date_for_completion - CAST(us.event_reminder AS INTEGER)) and t.task_status != 'CLOSED' ";
	
	public static final String CONNECT_REMINDER = "select c.connect_id, c.connect_name, c.primary_owner, ut.user_name, c.end_datetime_of_connect from connect_t c, user_t ut "
			+ "where end_datetime_of_connect <= CURRENT_TIMESTAMP and end_datetime_of_connect + interval '1 day' > modified_datetime and c.primary_owner = ut.user_id "
			+ "UNION "
			+ "select c.connect_id, c.connect_name, cs.secondary_owner, ut.user_name, c.end_datetime_of_connect from connect_t c, connect_secondary_owner_link_t cs, "
			+ "user_t ut where c.connect_id = cs.connect_id and cs.secondary_owner = ut.user_id and c.end_datetime_of_connect <= CURRENT_TIMESTAMP "
			+ "and c.end_datetime_of_connect + interval '1 day' > c.modified_datetime";
	
	public static final String BID_DETAILS_TRGT_DT_POST_QUERY = "select op.opportunity_id, op.opportunity_name, op.opportunity_owner, ut.user_name, bd.target_bid_submission_date, "
			+ "'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, user_t ut, customer_master_t cs  "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and op.opportunity_owner = ut.user_id "
			+ "and op.customer_id = cs.customer_id and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null "
			+ "and (CURRENT_DATE - bd.target_bid_submission_date) < :remindForDays "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, oss.sales_support_owner, ut.user_name,bd.target_bid_submission_date, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, opportunity_sales_support_link_t oss, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id "
			+ "and oss.sales_support_owner = ut.user_id and op.customer_id = cs.customer_id and CURRENT_DATE > bd.target_bid_submission_date "
			+ "and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < :remindForDays "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, bogo.bid_office_group_owner, ut.user_name, bd.target_bid_submission_date, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, bid_office_group_owner_link_t bogo, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id  "
			+ "and bogo.bid_office_group_owner = ut.user_id and op.customer_id = cs.customer_id  and CURRENT_DATE > bd.target_bid_submission_date "
			+ "and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < :remindForDays";
	
	public static final String BID_DETAILS_OUTCOME_DT_POST_QUERY = "select op.opportunity_id, op.opportunity_name, op.opportunity_owner, ut.user_name, bd.expected_date_of_outcome, "
			+ "'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and op.opportunity_owner = ut.user_id "
			+ "and op.customer_id = cs.customer_id and CURRENT_DATE > bd.expected_date_of_outcome and (CURRENT_DATE - bd.expected_date_of_outcome) < :remindForDays "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, oss.sales_support_owner, ut.user_name, bd.expected_date_of_outcome, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, opportunity_sales_support_link_t oss, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id "
			+ "and oss.sales_support_owner = ut.user_id and op.customer_id = cs.customer_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and (CURRENT_DATE - bd.expected_date_of_outcome) < :remindForDays "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, bogo.bid_office_group_owner, ut.user_name, bd.expected_date_of_outcome, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, bid_office_group_owner_link_t bogo, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id "
			+ "and bogo.bid_office_group_owner = ut.user_id and op.customer_id = cs.customer_id and "
			+ "CURRENT_DATE > bd.expected_date_of_outcome and (CURRENT_DATE - bd.expected_date_of_outcome) < :remindForDays"; 
	
	public static final String TASK_TRGT_DT_POST_QUERY = "select t.task_id, t.task_description, t.task_owner, ut.user_name, t.target_date_for_completion"
			+ ",t.entity_reference, COALESCE(t.connect_id, t.opportunity_id) from task_t t, user_t ut where CURRENT_DATE > t.target_date_for_completion "
			+ "and t.task_status != 'CLOSED' and (CURRENT_DATE - t.target_date_for_completion) < :remindForDays and t.task_owner = ut.user_id "
			+ "UNION "
			+ "select t.task_id, t.task_description, tb.bdms_tagged, ut.user_name, t.target_date_for_completion, t.entity_reference, COALESCE(t.connect_id, t.opportunity_id)"
			+ " from task_t t, task_bdms_tagged_link_t tb, user_t ut where t.task_id  = tb.task_id and t.task_owner = ut.user_id "
			+ "and CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' and (CURRENT_DATE - t.target_date_for_completion) < :remindForDays ";
	
	public static final String BID_DETAILS_TRGT_DT_POST_SUPERVISOR = "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.target_bid_submission_date, "
			+ " us.user_name as subordinate_name, 'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, user_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and op.opportunity_owner = us.user_id and op.customer_id = cs.customer_id "
			+ "and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null "
			+ "and (CURRENT_DATE - bd.target_bid_submission_date) < :remindForDays and ((CURRENT_DATE - bd.target_bid_submission_date) % 7) = 0 "
			+ "UNION "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.target_bid_submission_date, "
			+ "us.user_name as subordinate_name, 'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, opportunity_sales_support_link_t oss,user_t us, "
			+ "user_t ut, customer_master_t cs where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id "
			+ "and oss.sales_support_owner = us.user_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and op.customer_id = cs.customer_id "
			+ "and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < :remindForDays "
			+ "and ((CURRENT_DATE - bd.target_bid_submission_date) % 7) = 0 "
			+ "UNION "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.target_bid_submission_date, "
			+ "us.user_name as subordinate_name, 'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, bid_office_group_owner_link_t bogo, user_t us, user_t ut, "
			+ "customer_master_t cs where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bogo.bid_office_group_owner = us.user_id "
			+ "and bd.bid_id = bogo.bid_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and op.customer_id = cs.customer_id "
			+ "and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < :remindForDays "
			+ "and ((CURRENT_DATE - bd.target_bid_submission_date) % 7) = 0";

	public static final String BID_DETAILS_OUTCOME_DT_POST_SUPERVISOR = "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, "
			+ "bd.expected_date_of_outcome, us.user_name as subordinate_name, 'CUSTOMER', cs.customer_name "
			+ "from opportunity_t op, bid_details_t bd, user_t us, user_t ut, customer_master_t cs where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id "
			+ "and op.opportunity_owner = us.user_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and op.customer_id = cs.customer_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and (CURRENT_DATE - bd.expected_date_of_outcome) < :remindForDays and op.sales_stage_code between 2 and 8 and ((CURRENT_DATE - bd.expected_date_of_outcome) % 7) = 0 "
			+ "UNION "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.expected_date_of_outcome, us.user_name as subordinate_name, "
			+ " 'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, opportunity_sales_support_link_t oss, user_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id and oss.sales_support_owner = us.user_id "
			+ "and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and op.customer_id = cs.customer_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and op.sales_stage_code between 2 and 8 and (CURRENT_DATE - bd.expected_date_of_outcome) < :remindForDays and ((CURRENT_DATE - bd.expected_date_of_outcome) % 7) = 0 "
			+ "UNION "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.expected_date_of_outcome, us.user_name as subordinate_name, "
			+ "'CUSTOMER', cs.customer_name from opportunity_t op, bid_details_t bd, bid_office_group_owner_link_t bogo, user_t us, user_t ut, customer_master_t cs "
			+ "where op.sales_stage_code between 2 and 8 and bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id and bogo.bid_office_group_owner = us.user_id "
			+ "and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and op.customer_id = cs.customer_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and (CURRENT_DATE - bd.expected_date_of_outcome) < :remindForDays and ((CURRENT_DATE - bd.expected_date_of_outcome) % 7) = 0"; 

public static final String TASK_TRGT_DT_POST_SUPERVISOR = "select distinct t.task_id, t.task_description, us.supervisor_user_id, ut.user_name, t.target_date_for_completion, "
		+ "t.entity_reference, COALESCE(t.connect_id, t.opportunity_id), us.user_name as subordinate_name from task_t t, user_t us, user_t ut where t.task_owner = us.user_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id "
		+ "and CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' "
		+ "and (CURRENT_DATE - t.target_date_for_completion) < :remindForDays and ((CURRENT_DATE - t.target_date_for_completion) % 7) = 0 "
		+ "UNION ALL "
		+ "select distinct t.task_id, t.task_description, us.supervisor_user_id, ut.user_name, t.target_date_for_completion, t.entity_reference, COALESCE(t.connect_id, t.opportunity_id), us.user_name as subordinate_name "
		+ "from task_t t, task_bdms_tagged_link_t tb, user_t us, user_t ut where t.task_id  = tb.task_id and tb.bdms_tagged = us.user_id and us.supervisor_user_id is not null "
		+ "and us.supervisor_user_id = ut.user_id and CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' "
		+ "and (CURRENT_DATE - t.target_date_for_completion) < :remindForDays and ((CURRENT_DATE - t.target_date_for_completion) % 7) = 0";

	public static final String OPPORTUNITY_SHELVE_BASED_ON_OUTCOME = "UPDATE opportunity_t SET sales_stage_code = 12 ,modified_by='System' where opportunity_id in "
			+ "(select OPP.opportunity_id from opportunity_t OPP "
			+ "JOIN (select opportunity_id,(max(expected_date_of_outcome) < date(now())- '1 month'::interval) "
			+ "as can_update from bid_details_t group by opportunity_id ) "
			+ "as SUB_LIST on OPP.opportunity_id=SUB_LIST.opportunity_id where "
			+ "SUB_LIST.can_update='t' and OPP.sales_stage_code in (3,5,6,7,8))";

	public static final String OPPORTUNITY_SHELVE_BASED_ON_STATUS_CHANGE = "UPDATE opportunity_t SET sales_stage_code = 12, modified_by='System' where opportunity_id in"
			+ " (select OTH.opportunity_id from opportunity_timeline_history_t OTH"
			+ " JOIN (select opportunity_id,(max(updated_datetime) < (date(now())- '6 months'::interval))"
			+ " as can_update from opportunity_timeline_history_t group by opportunity_id )"
			+ " as SUB_LIST on OTH.opportunity_id=SUB_LIST.opportunity_id where"
			+ " SUB_LIST.can_update='t' and OTH.sales_stage_code in (0,1))";

	public static final String OPPORTUNITY_SHELVE_BASED_ON_TARGET_SUBMISSION_DATE = "UPDATE opportunity_t SET sales_stage_code = 12 ,modified_by='System' where opportunity_id in"
			+ " (select OPP.opportunity_id from opportunity_t OPP JOIN"
			+ " (select opportunity_id,(max(target_bid_submission_date) < date(now())- '1 month'::interval)"
			+ " as can_update from bid_details_t group by opportunity_id )"
			+ " as SUB_LIST on OPP.opportunity_id=SUB_LIST.opportunity_id where SUB_LIST.can_update='t'"
			+ " and OPP.sales_stage_code in (2,4))";

	//Start of Query for Workflow Customer an Workflow Partner
	public static final String QUERY_FOR_CUSTOMER_REQUESTS_PREFIX = "select WCT.customer_name,WRT.status,WST.* from workflow_customer_t WCT join workflow_request_t WRT on WCT.workflow_customer_id = WRT.entity_id and WRT.entity_type_id = 0 join workflow_step_t WST on WST.request_id = WRT.request_id";
    
	public static final String QUERY_CUSTOMER_FINAL_APPROVED = "select WCT.customer_name,WRT.status,WST1.* from workflow_customer_t WCT join workflow_request_t WRT on WCT.workflow_customer_id = WRT.entity_id and WRT.entity_type_id = 0 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status = (:stepStatus) and WST.user_id = (:userId) AND WCT.created_by <> (:userId) join workflow_step_t WST1 on WST1.request_id = WST.request_id and  WST1.step = (select max(step) from workflow_step_t where request_id = WST1.request_id )";
	
	public static final String QUERY_PARTNER_FINAL_APPROVED = "select WPT.partner_name,WRT.status,WST1.* from workflow_partner_t WPT join workflow_request_t WRT on WPT.workflow_partner_id = WRT.entity_id and WRT.entity_type_id = 1 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status = (:stepStatus) and WST.user_id = (:userId) AND WPT.created_by <> (:userId) join workflow_step_t WST1 on WST1.request_id = WST.request_id and WST1.step = (select max(step) from workflow_step_t where request_id = WST1.request_id )";
	
	public static final String MY_CUSTOMER_REQUESTS_SUFFIX1 = " and WCT.created_by = (:userId)";

	public static final String MY_REQUESTS_SUFFIX2 = " WHERE ((WRT.status='PENDING' AND WST.STEP_STATUS='PENDING') OR (WRT.status='REJECTED' AND WST.STEP_STATUS='REJECTED') OR";

	public static final String MY_REQUESTS_PENDING_REJECTED_SUFFIX = " WHERE WRT.status=WST.STEP_STATUS and WRT.status=(:stepStatus)";

	public static final String MY_REQUESTS_APPROVED_SUFFIX = " ((WRT.status='APPROVED' AND WST.STEP_STATUS='APPROVED') AND WST.STEP=(select max(step) from workflow_step_t where request_id=WRT.request_id))";

	public static final String MY_REQUESTS_SUFFIX3 = ")";

	public static final String MY_REQUESTS_WHERE = " WHERE";

	public static final String QUERY_FOR_PARTNER_REQUESTS_PREFIX = "select WPT.partner_name,WRT.status,WST.* from workflow_partner_t WPT join workflow_request_t WRT on WPT.workflow_partner_id = WRT.entity_id and WRT.entity_type_id = 1 join workflow_step_t WST on WST.request_id = WRT.request_id";

	public static final String MY_PARTNER_REQUESTS_SUFFIX = " and WPT.created_by = (:userId)";
	
	public static final String APPROVED_REJECTED_REQUESTS_SUFFIX1 = " and WST.step_status = (:stepStatus) and WST.user_id = (:userId)";

	public static final String APPROVED_REJECTED_REQUESTS_SUFFIX2 = " AND WCT.created_by <> (:userId)";
	
	public static final String APPROVED_REJECTED_REQUESTS_SUFFIX3 = " AND WPT.created_by <> (:userId)";
	
	public static final String PARTNER_PENDING_WITH_GROUP_QUERY = "select WPT.partner_name,WRT.status,WRT.entity_Id,WST.* from workflow_partner_t WPT join workflow_request_t WRT on WPT.workflow_partner_id = WRT.entity_id and WRT.entity_type_id = 1 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole) or WST.user_group like (:userGroup))";

	public static final String PARTNER_PENDING_WITH_USER_QUERY = "select WPT.partner_name,WRT.status,WRT.entity_Id,WST.* from workflow_partner_t WPT join workflow_request_t WRT on WPT.workflow_partner_id = WRT.entity_id and WRT.entity_type_id = 1 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status ='PENDING' and WST.user_id = (:userId)";

	public static final String CUSTOMER_PENDING_WITH_IOU_GROUP_QUERY = "select WCT.customer_name,WRT.status,WRT.entity_Id,WST.* from workflow_customer_t WCT join (select * from user_access_privileges_t where (user_id = (:userId) and isactive='Y' and privilege_type = 'IOU')) as UAP on WCT.iou = UAP.privilege_value join workflow_request_t WRT on WCT.workflow_customer_id = WRT.entity_id and WRT.entity_type_id = 0  join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole) or WST.user_group like (:userGroup))";

	public static final String CUSTOMER_PENDING_WITH_USER_QUERY = "select WCT.customer_name,WRT.status,WRT.entity_Id,WST.* from workflow_customer_t WCT join workflow_request_t WRT on WCT.workflow_customer_id = WRT.entity_id and WRT.entity_type_id = 0 join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id = (:userId)";

	public static final String CUSTOMER_PENDING_WITH_GEO_GROUP_QUERY = "select WCT.customer_name,WRT.status,WRT.entity_Id,WST.* from workflow_customer_t WCT join (select * from user_access_privileges_t where (user_id=(:userId) and isactive='Y' and privilege_type = 'GEOGRAPHY')) as UAP on WCT.geography = UAP.privilege_value join workflow_request_t WRT on WCT.workflow_customer_id = WRT.entity_id and WRT.entity_type_id = 0 join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole) or WST.user_group like (:userGroup))";

	public static final String CUSTOMER_PENDING_WITH_SI_QUERY = "select WCT.customer_name,WRT.status,WRT.entity_Id,WST.* from workflow_customer_t WCT join workflow_request_t WRT on WCT.workflow_customer_id = WRT.entity_id and WRT.entity_type_id = 0 join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole) or WST.user_group like (:userGroup))";

	public static final String COMPETITOR_PENDING_WITH_GROUP_QUERY = "select WCMT.workflow_competitor_name,WRT.status,WRT.entity_Id,WST.* from workflow_competitor_t WCMT join workflow_request_t WRT on WCMT.workflow_competitor_id = WRT.entity_id and WRT.entity_type_id = 2 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole) or WST.user_group like (:userGroup))";

   //public static final String OPPORTUNTIY_REOPEN_PENDING_WITH_USER_QUERY = "select OT.opportunity_name,WRT.status,WST.* from opportunity_t OT join workflow_request_t WRT on OT.opportunity_id = WRT.entity_id and WRT.entity_type_id = 1 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status ='PENDING' and WST.user_id = (:userId)";

	public static final String OPPORTUNTIY_REOPEN_PENDING_WITH_GROUP_QUERY = "select OT.opportunity_name,WRT.status,WRT.entity_Id, WST.* from opportunity_t OT join workflow_request_t WRT on OT.opportunity_id = WRT.entity_id and WRT.entity_type_id = 3 join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole))";
	
	public static final String OPPORTUNTIY_REOPEN_PENDING_WITH_PMO_QUERY =  "select distinct OT.opportunity_name,WRT.status,WRT.entity_Id,WST.* from opportunity_t OT join (select * from user_access_privileges_t where (user_id = (:userId) and isactive='Y' and privilege_type = 'GEOGRAPHY' )) as UAP on UAP.privilege_value = (select geography from geography_country_mapping_t GCMT where GCMT.country = OT.country ) join workflow_request_t WRT on OT.opportunity_id  = WRT.entity_id and WRT.entity_type_id = 3 join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_group like (:userGroup))";	

	public static final String BFM_PENDING_WITH_GROUP_QUERY="select WBT.opportunity_id,WRT.status,WRT.entity_Id, WST.* from workflow_bfm_t WBT join workflow_request_t WRT on WBT.workflow_bfm_id = WRT.entity_id and WRT.entity_type_id IN (4,5,6,7,8) join workflow_step_t WST on WST.request_id = WRT.request_id and WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole))or WST.user_group like (:userGroup))";

	public static final String BFM_PENDING_WITH_GEO_GROUP_QUERY ="select WBT.opportunity_id,WRT.status,WRT.entity_Id,WST.* from workflow_bfm_t WBT join (select * from user_access_privileges_t where user_id = (:userId) and isactive='Y' and privilege_type = 'GEOGRAPHY' ) as UAP on UAP.privilege_value = (select geography from geography_country_mapping_t GCMT where GCMT.country = (select country from opportunity_t where opportunity_id=WBT.opportunity_id)) join workflow_request_t WRT on WBT.workflow_bfm_id  = WRT.entity_id and WRT.entity_type_id IN (4,5,6,7,8) join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id IS NULL and WST.user_group like (:userGroup)";
	
	public static final String BFM_PENDING_WITH_IOU_GROUP_QUERY="select WBT.opportunity_id,WRT.status,WRT.entity_Id,WST.* from workflow_bfm_t WBT join (select * from user_access_privileges_t where user_id =(:userId) and isactive='Y' and privilege_type ='IOU') as UAP on UAP.privilege_value = (select iou from customer_master_t CMT where CMT.customer_id = (select customer_id from opportunity_t where opportunity_id=WBT.opportunity_id)) join workflow_request_t WRT on WBT.workflow_bfm_id  = WRT.entity_id and WRT.entity_type_id IN (4,5,6,7,8) join workflow_step_t WST on WRT.request_id = WST.request_id where WST.step_status ='PENDING' and WST.user_id IS NULL and WST.user_group like (:userGroup)";

	public static final String BFM_PENDING_WITH_SI_QUERY ="select WBT.opportunity_id,WRT.status,WRT.entity_Id,WST.* from workflow_bfm_t  WBT join workflow_request_t WRT on WBT.workflow_bfm_id = WRT.entity_id and WRT.entity_type_id IN (4,5,6,7,8) join workflow_step_t WST on WRT.request_id = WST.request_id and WST.step_status ='PENDING' and WST.user_id IS NULL and (WST.user_role like (:userRole) or WST.user_group like (:userGroup))";

    public static final String BFM_PENDING_WITH_USER_QUERY ="select WBT.opportunity_id,WRT.status,WRT.entity_Id,WST.* from workflow_bfm_t WBT join workflow_request_t WRT on WBT.workflow_bfm_id = WRT.entity_id and WRT.entity_type_id IN (4,5,6,7,8) join workflow_step_t WST on WRT.request_id = WST.request_id and WST.step_status ='PENDING' and WST.user_id = (:userId)";
    
    public static final String OPPORTUNITY_QUERY_PREFIX = "select distinct(OPP.*) from opportunity_t OPP join "
    		+ "customer_master_t CMT on CMT.customer_id = OPP.customer_id "
    		+ "join geography_mapping_t GMT on CMT.geography = GMT.geography "
    		+ "join iou_customer_mapping_t ICMT on ICMT.iou = CMT.iou "
    		+ "join opportunity_sub_sp_link_t OSSL on OSSL.opportunity_id = OPP.opportunity_id "
    		+ "join sub_sp_mapping_t SSMT on SSMT.sub_sp = OSSL.sub_sp "
    		+ "join opportunity_sales_support_link_t OSLT on OSLT.opportunity_id = OPP.opportunity_id "
    		+ "where OPP.deal_closure_date between (:fromDate) and (:toDate) "
    		+ "and OPP.sales_stage_code in (9,10)";
    
    public static final String OPPORTUNITY_DEAL_CLOSURE_DATE_ORDER_BY = " order by OPP.deal_closure_date ASC";

}
