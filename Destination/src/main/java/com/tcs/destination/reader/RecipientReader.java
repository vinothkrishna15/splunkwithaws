package com.tcs.destination.reader;


import static com.tcs.destination.utils.Constants.AUDIT_BID_OFFICE_GRP_OWNER;
import static com.tcs.destination.utils.Constants.AUDIT_CONNECT;
import static com.tcs.destination.utils.Constants.AUDIT_CONNECT_SEC_OWNERS;
import static com.tcs.destination.utils.Constants.AUDIT_OPPORTUNITY;
import static com.tcs.destination.utils.Constants.AUDIT_OPP_SALES_SUPPORT;
import static com.tcs.destination.utils.Constants.AUDIT_TASK;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.tcs.destination.bean.AuditBidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.AuditConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.AuditConnectT;
import com.tcs.destination.bean.AuditOpportunitySalesSupportLinkT;
import com.tcs.destination.bean.AuditOpportunityT;
import com.tcs.destination.bean.AuditTaskT;
import com.tcs.destination.bean.OperationEventRecipientMappingT;
import com.tcs.destination.bean.Recipient;
import com.tcs.destination.enums.OperationType;
import com.tcs.destination.helper.NotificationBatchHelper;

/**
 * Reader to Get the recipient details for notification
 * @author TCS
 * 
 */
public class RecipientReader implements ItemReader<List<Recipient>>, StepExecutionListener{

	private static final Logger logger = LoggerFactory
			.getLogger(RecipientReader.class);
	
	private String entityId;
	
	private String entityType;
	
	private String operationType;
	
	private NotificationBatchHelper notificationBatchHelper;
	
	private String currentUser;
	
	private int readCount = 0;
	
    private AuditConnectT auditConnectT;
	
	private List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs;
	
	private AuditOpportunityT auditOpportunityT;
	
	private List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs;
	
	
	private List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs;
	
	private AuditTaskT auditTaskT;
	
	
	@Override
	public List<Recipient> read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		
		List<Recipient> filteredRecipients = null;
		if(readCount==0) {
			logger.info("Inside Read Method");
			OperationType operationTypeE = OperationType.getByValue(operationType);
			
			List<OperationEventRecipientMappingT> eventRecipientMapping = notificationBatchHelper.pullEventsByOperation(operationTypeE);
			//Getting recipients
			List<Recipient> recipients = notificationBatchHelper.getRecipients(entityId,operationTypeE,eventRecipientMapping,currentUser,auditConnectT,
					auditConnectSecondaryOwnerLinkTs,auditOpportunityT,auditOpportunitySalesSupportLinkTs,auditTaskT,auditBidOfficeGroupOwnerLinkTs
					);
			logger.info("Recipients retrieved");
			//Remove Modified user from the recipients
			filteredRecipients = notificationBatchHelper.removeModifiedByUserFromRecipients(currentUser, recipients);
			logger.info("Removed Modified user from the recipients");
			readCount++;
		}
		
		
		return filteredRecipients;
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

	public NotificationBatchHelper getNotificationBatchHelper() {
		return notificationBatchHelper;
	}

	public void setNotificationBatchHelper(
			NotificationBatchHelper notificationBatchHelper) {
		this.notificationBatchHelper = notificationBatchHelper;
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void beforeStep(StepExecution stepExecution) {

		ExecutionContext jobContext = stepExecution.getJobExecution().getExecutionContext();
		auditConnectT = (AuditConnectT) jobContext.get(AUDIT_CONNECT);
		auditConnectSecondaryOwnerLinkTs = (List<AuditConnectSecondaryOwnerLinkT>) jobContext.get(AUDIT_CONNECT_SEC_OWNERS);
		auditOpportunityT = (AuditOpportunityT) jobContext.get(AUDIT_OPPORTUNITY);
		auditOpportunitySalesSupportLinkTs = (List<AuditOpportunitySalesSupportLinkT>) jobContext.get(AUDIT_OPP_SALES_SUPPORT);
//		auditBidDetailsT = (AuditBidDetailsT) jobContext.get(AUDIT_BID_DETAILS);
		auditBidOfficeGroupOwnerLinkTs = (List<AuditBidOfficeGroupOwnerLinkT>) jobContext.get(AUDIT_BID_OFFICE_GRP_OWNER);
		auditTaskT = (AuditTaskT) jobContext.get(AUDIT_TASK);
//		auditTaskBdmsTaggedLinkTs = (List<AuditTaskBdmsTaggedLinkT>) jobContext.get(AUDIT_TASK_BDM_TAGGED);
				
	
		
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
