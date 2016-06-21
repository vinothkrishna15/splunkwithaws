package com.tcs.destination.processor;

import static com.tcs.destination.utils.Constants.AUDIT_CONNECT;
import static com.tcs.destination.utils.Constants.AUDIT_CONNECT_SEC_OWNERS;
import static com.tcs.destination.utils.Constants.AUDIT_BID_DETAILS;
import static com.tcs.destination.utils.Constants.AUDIT_BID_OFFICE_GRP_OWNER;
import static com.tcs.destination.utils.Constants.AUDIT_OPP_SALES_SUPPORT;
import static com.tcs.destination.utils.Constants.AUDIT_OPPORTUNITY;
import static com.tcs.destination.utils.Constants.AUDIT_TASK;
import static com.tcs.destination.utils.Constants.AUDIT_TASK_BDM_TAGGED;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

import com.google.common.collect.Lists;
import com.tcs.destination.bean.AuditBidDetailsT;
import com.tcs.destination.bean.AuditBidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.AuditConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.AuditConnectT;
import com.tcs.destination.bean.AuditOpportunitySalesSupportLinkT;
import com.tcs.destination.bean.AuditOpportunityT;
import com.tcs.destination.bean.AuditTaskBdmsTaggedLinkT;
import com.tcs.destination.bean.AuditTaskT;
import com.tcs.destination.bean.Recipient;
import com.tcs.destination.bean.UserNotificationsT;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.helper.NotificationBatchHelper;

/**
 * Processor to process the recipient details and framing notifications for each recipient
 * @author TCS
 *
 */
public class NotificationProcessor implements ItemProcessor<List<Recipient>,List<UserNotificationsT>>, StepExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(NotificationProcessor.class);
	
    private String entityId;
	
	private String entityType;
	
	private String operationType;
	
	private String currentUser;
	
	private NotificationBatchHelper notificationBatchHelper;
	
	private AuditConnectT auditConnectT;
	
	private List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs;
	
	private AuditOpportunityT auditOpportunityT;
	
	private List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs;
	
	private AuditBidDetailsT auditBidDetailsT;
	
	private List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs;
	
	private AuditTaskT auditTaskT;
	
	private List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs;
	
	
	
	@Override
	public List<UserNotificationsT> process(List<Recipient> items) throws Exception {
		logger.info("Inside Notification Process Method");
		List<UserNotificationsT> userNotificationsTs = Lists.newArrayList();
		if(CollectionUtils.isNotEmpty(items)) {
			OperationType operationTypeE = OperationType.getByValue(operationType);
			String currentUserName = notificationBatchHelper.getUserNameForUserId(currentUser);
			//getting user notifications
			userNotificationsTs = notificationBatchHelper.
					getUserNotifications(items,entityId,entityType,operationTypeE,currentUserName,auditConnectT,auditConnectSecondaryOwnerLinkTs,
							auditOpportunityT,auditOpportunitySalesSupportLinkTs,auditBidDetailsT,auditBidOfficeGroupOwnerLinkTs,
							auditTaskT,auditTaskBdmsTaggedLinkTs);
		}
		
		
		return userNotificationsTs;
	}



	@SuppressWarnings("unchecked")
	@Override
	public void beforeStep(StepExecution stepExecution) {
		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		auditConnectT = (AuditConnectT) jobContext.get(AUDIT_CONNECT);
		auditConnectSecondaryOwnerLinkTs = (List<AuditConnectSecondaryOwnerLinkT>) jobContext.get(AUDIT_CONNECT_SEC_OWNERS);
		auditOpportunityT = (AuditOpportunityT) jobContext.get(AUDIT_OPPORTUNITY);
		auditOpportunitySalesSupportLinkTs = (List<AuditOpportunitySalesSupportLinkT>) jobContext.get(AUDIT_OPP_SALES_SUPPORT);
		auditBidDetailsT = (AuditBidDetailsT) jobContext.get(AUDIT_BID_DETAILS);
		auditBidOfficeGroupOwnerLinkTs = (List<AuditBidOfficeGroupOwnerLinkT>) jobContext.get(AUDIT_BID_OFFICE_GRP_OWNER);
		auditTaskT = (AuditTaskT) jobContext.get(AUDIT_TASK);
		auditTaskBdmsTaggedLinkTs = (List<AuditTaskBdmsTaggedLinkT>) jobContext.get(AUDIT_TASK_BDM_TAGGED);
				
	}



	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		notificationBatchHelper.setNotifiedTrueForAuditEntities(auditConnectT,auditConnectSecondaryOwnerLinkTs,
						auditOpportunityT,auditOpportunitySalesSupportLinkTs,auditBidDetailsT,auditBidOfficeGroupOwnerLinkTs,
						auditTaskT,auditTaskBdmsTaggedLinkTs);
		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		jobContext.remove(AUDIT_TASK_BDM_TAGGED);
		jobContext.remove(AUDIT_TASK);
		jobContext.remove(AUDIT_OPPORTUNITY);
		jobContext.remove(AUDIT_OPP_SALES_SUPPORT);
		jobContext.remove(AUDIT_BID_OFFICE_GRP_OWNER);
		jobContext.remove(AUDIT_BID_DETAILS);
		jobContext.remove(AUDIT_CONNECT_SEC_OWNERS);
		jobContext.remove(AUDIT_CONNECT);
		return ExitStatus.COMPLETED;
	}



	public String getEntityId() {
		return entityId;
	}



	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}



	public String getEntityType() {
		return entityType;
	}



	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}



	public String getOperationType() {
		return operationType;
	}



	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}



	public String getCurrentUser() {
		return currentUser;
	}



	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}



	public NotificationBatchHelper getNotificationBatchHelper() {
		return notificationBatchHelper;
	}



	public void setNotificationBatchHelper(
			NotificationBatchHelper notificationBatchHelper) {
		this.notificationBatchHelper = notificationBatchHelper;
	}



	public AuditConnectT getAuditConnectT() {
		return auditConnectT;
	}



	public void setAuditConnectT(AuditConnectT auditConnectT) {
		this.auditConnectT = auditConnectT;
	}



	public List<AuditConnectSecondaryOwnerLinkT> getAuditConnectSecondaryOwnerLinkTs() {
		return auditConnectSecondaryOwnerLinkTs;
	}



	public void setAuditConnectSecondaryOwnerLinkTs(
			List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs) {
		this.auditConnectSecondaryOwnerLinkTs = auditConnectSecondaryOwnerLinkTs;
	}



	public AuditOpportunityT getAuditOpportunityT() {
		return auditOpportunityT;
	}



	public void setAuditOpportunityT(AuditOpportunityT auditOpportunityT) {
		this.auditOpportunityT = auditOpportunityT;
	}



	public List<AuditOpportunitySalesSupportLinkT> getAuditOpportunitySalesSupportLinkTs() {
		return auditOpportunitySalesSupportLinkTs;
	}



	public void setAuditOpportunitySalesSupportLinkTs(
			List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs) {
		this.auditOpportunitySalesSupportLinkTs = auditOpportunitySalesSupportLinkTs;
	}



	public AuditBidDetailsT getAuditBidDetailsT() {
		return auditBidDetailsT;
	}



	public void setAuditBidDetailsT(AuditBidDetailsT auditBidDetailsT) {
		this.auditBidDetailsT = auditBidDetailsT;
	}



	public List<AuditBidOfficeGroupOwnerLinkT> getAuditBidOfficeGroupOwnerLinkTs() {
		return auditBidOfficeGroupOwnerLinkTs;
	}



	public void setAuditBidOfficeGroupOwnerLinkTs(
			List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs) {
		this.auditBidOfficeGroupOwnerLinkTs = auditBidOfficeGroupOwnerLinkTs;
	}



	public AuditTaskT getAuditTaskT() {
		return auditTaskT;
	}



	public void setAuditTaskT(AuditTaskT auditTaskT) {
		this.auditTaskT = auditTaskT;
	}



	public List<AuditTaskBdmsTaggedLinkT> getAuditTaskBdmsTaggedLinkTs() {
		return auditTaskBdmsTaggedLinkTs;
	}



	public void setAuditTaskBdmsTaggedLinkTs(
			List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs) {
		this.auditTaskBdmsTaggedLinkTs = auditTaskBdmsTaggedLinkTs;
	}
	
	

}
