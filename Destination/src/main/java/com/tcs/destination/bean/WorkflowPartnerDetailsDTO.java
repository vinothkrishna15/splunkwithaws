package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

public class WorkflowPartnerDetailsDTO implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//Stores requested partner details
	private WorkflowPartnerT requestedPartner;
	 //Details of workflow steps
	 private List<WorkflowStepT> workflowSteps;
	 //Status of the request
	 private String status;
	 
	 private PartnerMasterT partnerMasterT;
	 
	public WorkflowPartnerT getRequestedPartner() {
		return requestedPartner;
	}
	public void setRequestedPartner(WorkflowPartnerT requestedPartner) {
		this.requestedPartner = requestedPartner;
	}
	public List<WorkflowStepT> getWorkflowSteps() {
		return workflowSteps;
	}
	public void setWorkflowSteps(List<WorkflowStepT> workflowSteps) {
		this.workflowSteps = workflowSteps;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public PartnerMasterT getPartnerMasterT() {
		return partnerMasterT;
	}
	public void setPartnerMasterT(PartnerMasterT partnerMasterT) {
		this.partnerMasterT = partnerMasterT;
	}
}
