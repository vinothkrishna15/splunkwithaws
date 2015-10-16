package com.tcs.destination.utils;


public class QueryConstants {
	
	public static final String BID_DETAILS_TRGT_DT_QUERY = "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.target_bid_submission_date "
			+ "from bid_details_t bd, opportunity_t op, user_general_settings_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and op.opportunity_owner = us.user_id and us.user_id = ut.user_id "
			+ "and CURRENT_DATE = (bd.target_bid_submission_date - CAST(us.event_reminder AS INTEGER)) and bd.actual_bid_submission_date is null "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.target_bid_submission_date "
			+ "from bid_details_t bd, opportunity_t op, opportunity_sales_support_link_t oss, user_general_settings_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id and oss.sales_support_owner = us.user_id "
			+ "and us.user_id = ut.user_id  and CURRENT_DATE = (bd.target_bid_submission_date - CAST(us.event_reminder AS INTEGER)) "
			+ "and bd.actual_bid_submission_date is null "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.target_bid_submission_date "
			+ "from bid_details_t bd, opportunity_t op, bid_office_group_owner_link_t bogo, user_general_settings_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id and bogo.bid_office_group_owner = us.user_id "
			+ "and us.user_id = ut.user_id and CURRENT_DATE = (bd.target_bid_submission_date - CAST(us.event_reminder AS INTEGER)) "
			+ "and bd.actual_bid_submission_date is null "; 
													
	
	public static final String BID_DETAILS_OUTCOME_DT_QUERY = "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.expected_date_of_outcome "
			+ "from bid_details_t bd, opportunity_t op, user_general_settings_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and op.opportunity_owner = us.user_id and us.user_id = ut.user_id "
			+ "and CURRENT_DATE = (bd.expected_date_of_outcome - CAST(us.event_reminder AS INTEGER)) and op.sales_stage_code between 2 and 8 "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.expected_date_of_outcome "
			+ "from bid_details_t bd, opportunity_t op, opportunity_sales_support_link_t oss, user_general_settings_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id and oss.sales_support_owner = us.user_id "
			+ "and us.user_id = ut.user_id and CURRENT_DATE = (bd.expected_date_of_outcome - CAST(us.event_reminder AS INTEGER)) and op.sales_stage_code between 2 and 8 "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, us.user_id, ut.user_name, bd.expected_date_of_outcome "
			+ "from bid_details_t bd, opportunity_t op, bid_office_group_owner_link_t bogo, user_general_settings_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id and bogo.bid_office_group_owner = us.user_id "
			+ "and us.user_id = ut.user_id and CURRENT_DATE = (bd.expected_date_of_outcome - CAST(us.event_reminder AS INTEGER)) "
			+ "and op.sales_stage_code between 2 and 8"; 
	
	public static final String TASK_TRGT_DT_DUE_QUERY = "select t.task_id, t.task_description, us.user_id, ut.user_name, t.target_date_for_completion from task_t t, "
			+ "user_general_settings_t us, user_t ut where t.task_owner  = us.user_id and us.user_id = ut.user_id "
			+ "and CURRENT_DATE = (t.target_date_for_completion - CAST(us.event_reminder AS INTEGER)) and t.task_status != 'CLOSED' "
			+ "UNION "
			+ "select t.task_id, t.task_description, us.user_id, ut.user_name, t.target_date_for_completion "
			+ "from task_t t,user_general_settings_t us, task_bdms_tagged_link_t tb,user_t ut "
			+ "where t.task_id  = tb.task_id and tb.bdms_tagged = us.user_id and us.user_id = ut.user_id and "
			+ "CURRENT_DATE = (t.target_date_for_completion - CAST(us.event_reminder AS INTEGER)) and t.task_status != 'CLOSED' ";
	
	public static final String CONNECT_REMINDER = "select c.connect_id, c.connect_name, c.primary_owner, ut.user_name, c.end_datetime_of_connect from connect_t c, user_t ut "
			+ "where end_datetime_of_connect <= CURRENT_TIMESTAMP and end_datetime_of_connect + interval '1 day' > modified_datetime and c.primary_owner = ut.user_id "
			+ "UNION "
			+ "select c.connect_id, c.connect_name, cs.secondary_owner, ut.user_name, c.end_datetime_of_connect from connect_t c, connect_secondary_owner_link_t cs, "
			+ "user_t ut where c.connect_id = cs.connect_id and cs.secondary_owner = ut.user_id and c.end_datetime_of_connect <= CURRENT_TIMESTAMP "
			+ "and c.end_datetime_of_connect + interval '1 day' > c.modified_datetime";
	
	public static final String BID_DETAILS_TRGT_DT_POST_QUERY = "select op.opportunity_id, op.opportunity_name, op.opportunity_owner, ut.user_name, bd.target_bid_submission_date "
			+ "from bid_details_t bd, opportunity_t op, user_t ut where bd.opportunity_id = op.opportunity_id and op.opportunity_owner = ut.user_id "
			+ "and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < 31 "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, oss.sales_support_owner, ut.user_name,bd.target_bid_submission_date "
			+ "from bid_details_t bd, opportunity_t op, opportunity_sales_support_link_t oss, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id and oss.sales_support_owner = ut.user_id "
			+ "and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < 31 "
			+ "UNION "
			+ " select op.opportunity_id, op.opportunity_name, bogo.bid_office_group_owner, ut.user_name, bd.target_bid_submission_date "
			+ "from bid_details_t bd, opportunity_t op, bid_office_group_owner_link_t bogo, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id  and bogo.bid_office_group_owner = ut.user_id "
			+ "and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < 31 ";
	
	public static final String BID_DETAILS_OUTCOME_DT_POST_QUERY = "select op.opportunity_id, op.opportunity_name, op.opportunity_owner, ut.user_name , bd.expected_date_of_outcome "
			+ "from bid_details_t bd, opportunity_t op, user_t ut where bd.opportunity_id = op.opportunity_id and op.opportunity_owner = ut.user_id "
			+ "and CURRENT_DATE > bd.expected_date_of_outcome and op.sales_stage_code between 2 and 8 and (CURRENT_DATE - bd.expected_date_of_outcome) < 31 "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, oss.sales_support_owner, ut.user_name, bd.expected_date_of_outcome "
			+ "from bid_details_t bd, opportunity_t op, opportunity_sales_support_link_t oss, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id "
			+ "and oss.sales_support_owner = ut.user_id and CURRENT_DATE > bd.expected_date_of_outcome and op.sales_stage_code between 2 and 8 "
			+ "and (CURRENT_DATE - bd.expected_date_of_outcome) < 31 "
			+ "UNION "
			+ "select op.opportunity_id, op.opportunity_name, bogo.bid_office_group_owner, ut.user_name, bd.expected_date_of_outcome "
			+ "from bid_details_t bd, opportunity_t op, bid_office_group_owner_link_t bogo, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id and bogo.bid_office_group_owner = ut.user_id and "
			+ "CURRENT_DATE > bd.expected_date_of_outcome and op.sales_stage_code between 2 and 8 and (CURRENT_DATE - bd.expected_date_of_outcome) < 31"; 
	
	public static final String TASK_TRGT_DT_POST_QUERY = "select t.task_id, t.task_description, t.task_owner, ut.user_name, t.target_date_for_completion from task_t t, user_t ut "
			+ "where CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' and (CURRENT_DATE - t.target_date_for_completion) < 31 and t.task_owner = ut.user_id "
			+ "UNION "
			+ "select t.task_id, t.task_description, tb.bdms_tagged, ut.user_name, t.target_date_for_completion from task_t t, task_bdms_tagged_link_t tb, user_t ut "
			+ "where t.task_id  = tb.task_id and t.task_owner = ut.user_id and CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' "
			+ "and (CURRENT_DATE - t.target_date_for_completion) < 31";
	
	public static final String BID_DETAILS_TRGT_DT_POST_SUPERVISOR = "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.target_bid_submission_date, "
			+ "us.user_id, us.user_name as subordinate_name from bid_details_t bd, opportunity_t op, user_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and op.opportunity_owner = us.user_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id "
			+ "and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < 31 "
			+ "and ((CURRENT_DATE - bd.target_bid_submission_date) % 7) = 0 "
			+ "UNION ALL "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.target_bid_submission_date, us.user_id, us.user_name as subordinate_name "
			+ "from bid_details_t bd, opportunity_t op, opportunity_sales_support_link_t oss,user_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id and oss.sales_support_owner = us.user_id and us.supervisor_user_id is not null "
			+ "and us.supervisor_user_id = ut.user_id and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null "
			+ "and (CURRENT_DATE - bd.target_bid_submission_date) < 31 and ((CURRENT_DATE - bd.target_bid_submission_date) % 7) = 0 "
			+ "UNION ALL "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.target_bid_submission_date, us.user_id, us.user_name as subordinate_name "
			+ "from bid_details_t bd, opportunity_t op, bid_office_group_owner_link_t bogo, user_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bogo.bid_office_group_owner = us.user_id and bd.bid_id = bogo.bid_id and us.supervisor_user_id is not null "
			+ "and us.supervisor_user_id = ut.user_id and CURRENT_DATE > bd.target_bid_submission_date and bd.actual_bid_submission_date is null and (CURRENT_DATE - bd.target_bid_submission_date) < 31 "
			+ "and ((CURRENT_DATE - bd.target_bid_submission_date) % 7) = 0";

	public static final String BID_DETAILS_OUTCOME_DT_POST_SUPERVISOR = "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, "
			+ "bd.expected_date_of_outcome, us.user_id, us.user_name as subordinate_name "
			+ "from bid_details_t bd, opportunity_t op, user_t us, user_t ut where bd.opportunity_id = op.opportunity_id "
			+ "and op.opportunity_owner = us.user_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and (CURRENT_DATE - bd.expected_date_of_outcome) < 31 and op.sales_stage_code between 2 and 8 and ((CURRENT_DATE - bd.expected_date_of_outcome) % 7) = 0 "
			+ "UNION ALL "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.expected_date_of_outcome, us.user_id, us.user_name as subordinate_name "
			+ "from bid_details_t bd, opportunity_t op, opportunity_sales_support_link_t oss, user_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.opportunity_id = oss.opportunity_id and oss.sales_support_owner = us.user_id "
			+ "and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and op.sales_stage_code between 2 and 8 and (CURRENT_DATE - bd.expected_date_of_outcome) < 31 and ((CURRENT_DATE - bd.expected_date_of_outcome) % 7) = 0 "
			+ "UNION ALL "
			+ "select distinct op.opportunity_id, op.opportunity_name, us.supervisor_user_id, ut.user_name, bd.expected_date_of_outcome, us.user_id, us.user_name as subordinate_name "
			+ "from bid_details_t bd, opportunity_t op, bid_office_group_owner_link_t bogo, user_t us, user_t ut "
			+ "where bd.opportunity_id = op.opportunity_id and bd.bid_id = bogo.bid_id and bogo.bid_office_group_owner = us.user_id "
			+ "and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id and CURRENT_DATE > bd.expected_date_of_outcome "
			+ "and op.sales_stage_code between 2 and 8 and (CURRENT_DATE - bd.expected_date_of_outcome) < 31 and ((CURRENT_DATE - bd.expected_date_of_outcome) % 7) = 0"; 

public static final String TASK_TRGT_DT_POST_SUPERVISOR = "select distinct t.task_id, t.task_description, us.supervisor_user_id, ut.user_name, t.target_date_for_completion, "
		+ "us.user_id, us.user_name as subordinate_name "
		+ "from task_t t, user_t us, user_t ut where t.task_owner = us.user_id and us.supervisor_user_id is not null and us.supervisor_user_id = ut.user_id "
		+ "and CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' "
		+ "and (CURRENT_DATE - t.target_date_for_completion) < 31 and ((CURRENT_DATE - t.target_date_for_completion) % 7) = 0 "
		+ "UNION ALL "
		+ "select distinct t.task_id, t.task_description, us.supervisor_user_id, ut.user_name, t.target_date_for_completion, us.user_id, us.user_name as subordinate_name "
		+ "from task_t t, task_bdms_tagged_link_t tb, user_t us, user_t ut where t.task_id  = tb.task_id and tb.bdms_tagged = us.user_id and us.supervisor_user_id is not null "
		+ "and us.supervisor_user_id = ut.user_id and CURRENT_DATE > t.target_date_for_completion and t.task_status != 'CLOSED' "
		+ "and (CURRENT_DATE - t.target_date_for_completion) < 31 and ((CURRENT_DATE - t.target_date_for_completion) % 7) = 0";
	
}
