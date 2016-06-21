package com.tcs.destination.tasklet;

import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.AuditBidDetailsT;
import com.tcs.destination.bean.AuditBidOfficeGroupOwnerLinkT;
import com.tcs.destination.bean.AuditConnectSecondaryOwnerLinkT;
import com.tcs.destination.bean.AuditConnectT;
import com.tcs.destination.bean.AuditOpportunitySalesSupportLinkT;
import com.tcs.destination.bean.AuditOpportunityT;
import com.tcs.destination.bean.AuditTaskBdmsTaggedLinkT;
import com.tcs.destination.bean.AuditTaskT;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.data.repository.AuditBidDetailsTRepository;
import com.tcs.destination.data.repository.AuditBidOfficeGroupOwnerLinkTRepository;
import com.tcs.destination.data.repository.AuditConnectSecondaryOwnerLinkTRepository;
import com.tcs.destination.data.repository.AuditConnectTRepository;
import com.tcs.destination.data.repository.AuditOpportunityRepository;
import com.tcs.destination.data.repository.AuditOpportunitySalesSupportLinkTRepository;
import com.tcs.destination.data.repository.AuditTaskBdmsTaggedLinkTRepository;
import com.tcs.destination.data.repository.AuditTaskTRepository;
import com.tcs.destination.data.repository.BidDetailsTRepository;
import com.tcs.destination.data.repository.OpportunityRepository;
import com.tcs.destination.enums.OperationType;
import static com.tcs.destination.utils.Constants.AUDIT_CONNECT;
import static com.tcs.destination.utils.Constants.AUDIT_BID_DETAILS;
import static com.tcs.destination.utils.Constants.AUDIT_BID_OFFICE_GRP_OWNER;
import static com.tcs.destination.utils.Constants.AUDIT_CONNECT_SEC_OWNERS;
import static com.tcs.destination.utils.Constants.AUDIT_OPP_SALES_SUPPORT;
import static com.tcs.destination.utils.Constants.AUDIT_OPPORTUNITY;
import static com.tcs.destination.utils.Constants.AUDIT_TASK;
import static com.tcs.destination.utils.Constants.AUDIT_TASK_BDM_TAGGED;;


@Component("notificationPreprocessor")
public class NotificationPreprocessor implements Tasklet {

	 private String entityId;
		
		
		private String operationType;
		
		
		private AuditOpportunityRepository auditOpportunityRepository;
		
		private AuditOpportunitySalesSupportLinkTRepository auditOpportunitySalesSupportLinkTRepository;
		
		private AuditBidDetailsTRepository auditBidDetailsTRepository;
		
		private AuditBidOfficeGroupOwnerLinkTRepository auditBidOfficeGroupOwnerLinkTRepository;
		
		private AuditConnectTRepository auditConnectTRepository;
		
		private AuditConnectSecondaryOwnerLinkTRepository auditConnectSecondaryOwnerLinkTRepository; 
		
		private AuditTaskTRepository auditTaskTRepository;
		
		private AuditTaskBdmsTaggedLinkTRepository auditTaskBdmsTaggedLinkTRepository;
	
		
		private BidDetailsTRepository bidDetailsTRepository;
		
	
	@Override
	public RepeatStatus execute(StepContribution contribution,
			ChunkContext chunkContext) throws Exception {
		ExecutionContext jobContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
		AuditConnectT auditConnectT = null;
		List<AuditConnectSecondaryOwnerLinkT> auditConnectSecondaryOwnerLinkTs = null;
		AuditOpportunityT auditOpportunityT = null;
		List<AuditOpportunitySalesSupportLinkT> auditOpportunitySalesSupportLinkTs = null;
		AuditBidDetailsT auditBidDetailsT  = null;
		List<AuditBidOfficeGroupOwnerLinkT> auditBidOfficeGroupOwnerLinkTs = null;
		AuditTaskT auditTaskT = null;
		List<AuditTaskBdmsTaggedLinkT> auditTaskBdmsTaggedLinkTs = null;
		OperationType operationTypeE = OperationType.getByValue(operationType);
		switch (operationTypeE) {
		case OPPORTUNITY_CREATE:
		case OPPORTUNITY_EDIT:
			auditOpportunityT = auditOpportunityRepository
			.findFirstByOpportunityIdAndNotifiedFalseOrderByNewModifiedDatetimeDesc(entityId);
			
			auditOpportunitySalesSupportLinkTs = auditOpportunitySalesSupportLinkTRepository
					.findByOldOpportunityIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(entityId);
			
			auditBidDetailsT = auditBidDetailsTRepository
					.findFirstByOldOpportunityIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(entityId);
			
			BidDetailsT bidDetailsT = bidDetailsTRepository.findFirstByOpportunityIdOrderByModifiedDatetimeDesc(entityId);
			if(bidDetailsT!=null) {
				auditBidOfficeGroupOwnerLinkTs = auditBidOfficeGroupOwnerLinkTRepository.findByOldBidIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(bidDetailsT.getBidId());
			}
			
		break;	
			
		case CONNECT_CREATE:
		case CONNECT_EDIT:
			auditConnectT = auditConnectTRepository
			.findFirstByConnectIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(entityId);
			
			auditConnectSecondaryOwnerLinkTs = auditConnectSecondaryOwnerLinkTRepository
					.findByOldConnectIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(entityId);
			
		break;	
		case TASK_CREATE:
		case TASK_EDIT:
			
			auditTaskT = auditTaskTRepository.findFirstByTaskIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(entityId);
			
			auditTaskBdmsTaggedLinkTs = auditTaskBdmsTaggedLinkTRepository.findByOldTaskIdAndNotifiedFalseOrderByCreatedModifiedDatetimeDesc(entityId);
		break;
		default :
			break;
		}
	jobContext.put(AUDIT_BID_DETAILS, auditBidDetailsT);	
	jobContext.put(AUDIT_BID_OFFICE_GRP_OWNER, auditBidOfficeGroupOwnerLinkTs);	
	jobContext.put(AUDIT_CONNECT, auditConnectT);	
	jobContext.put(AUDIT_CONNECT_SEC_OWNERS, auditConnectSecondaryOwnerLinkTs);	
	jobContext.put(AUDIT_OPP_SALES_SUPPORT, auditOpportunitySalesSupportLinkTs);	
	jobContext.put(AUDIT_OPPORTUNITY, auditOpportunityT);	
	jobContext.put(AUDIT_TASK, auditTaskT);	
	jobContext.put(AUDIT_TASK_BDM_TAGGED, auditTaskBdmsTaggedLinkTs);	
		
		
		return RepeatStatus.FINISHED;
	}


	public String getEntityId() {
		return entityId;
	}


	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}


	public String getOperationType() {
		return operationType;
	}


	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}


	public AuditOpportunityRepository getAuditOpportunityRepository() {
		return auditOpportunityRepository;
	}


	public void setAuditOpportunityRepository(
			AuditOpportunityRepository auditOpportunityRepository) {
		this.auditOpportunityRepository = auditOpportunityRepository;
	}


	public AuditOpportunitySalesSupportLinkTRepository getAuditOpportunitySalesSupportLinkTRepository() {
		return auditOpportunitySalesSupportLinkTRepository;
	}


	public void setAuditOpportunitySalesSupportLinkTRepository(
			AuditOpportunitySalesSupportLinkTRepository auditOpportunitySalesSupportLinkTRepository) {
		this.auditOpportunitySalesSupportLinkTRepository = auditOpportunitySalesSupportLinkTRepository;
	}


	public AuditBidDetailsTRepository getAuditBidDetailsTRepository() {
		return auditBidDetailsTRepository;
	}


	public void setAuditBidDetailsTRepository(
			AuditBidDetailsTRepository auditBidDetailsTRepository) {
		this.auditBidDetailsTRepository = auditBidDetailsTRepository;
	}


	public AuditBidOfficeGroupOwnerLinkTRepository getAuditBidOfficeGroupOwnerLinkTRepository() {
		return auditBidOfficeGroupOwnerLinkTRepository;
	}


	public void setAuditBidOfficeGroupOwnerLinkTRepository(
			AuditBidOfficeGroupOwnerLinkTRepository auditBidOfficeGroupOwnerLinkTRepository) {
		this.auditBidOfficeGroupOwnerLinkTRepository = auditBidOfficeGroupOwnerLinkTRepository;
	}


	public AuditConnectTRepository getAuditConnectTRepository() {
		return auditConnectTRepository;
	}


	public void setAuditConnectTRepository(
			AuditConnectTRepository auditConnectTRepository) {
		this.auditConnectTRepository = auditConnectTRepository;
	}


	public AuditConnectSecondaryOwnerLinkTRepository getAuditConnectSecondaryOwnerLinkTRepository() {
		return auditConnectSecondaryOwnerLinkTRepository;
	}


	public void setAuditConnectSecondaryOwnerLinkTRepository(
			AuditConnectSecondaryOwnerLinkTRepository auditConnectSecondaryOwnerLinkTRepository) {
		this.auditConnectSecondaryOwnerLinkTRepository = auditConnectSecondaryOwnerLinkTRepository;
	}


	public AuditTaskTRepository getAuditTaskTRepository() {
		return auditTaskTRepository;
	}


	public void setAuditTaskTRepository(AuditTaskTRepository auditTaskTRepository) {
		this.auditTaskTRepository = auditTaskTRepository;
	}


	public AuditTaskBdmsTaggedLinkTRepository getAuditTaskBdmsTaggedLinkTRepository() {
		return auditTaskBdmsTaggedLinkTRepository;
	}


	public void setAuditTaskBdmsTaggedLinkTRepository(
			AuditTaskBdmsTaggedLinkTRepository auditTaskBdmsTaggedLinkTRepository) {
		this.auditTaskBdmsTaggedLinkTRepository = auditTaskBdmsTaggedLinkTRepository;
	}


	public BidDetailsTRepository getBidDetailsTRepository() {
		return bidDetailsTRepository;
	}


	public void setBidDetailsTRepository(BidDetailsTRepository bidDetailsTRepository) {
		this.bidDetailsTRepository = bidDetailsTRepository;
	}
    
	
}
