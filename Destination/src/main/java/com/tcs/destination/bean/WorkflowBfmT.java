package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.sql.Timestamp;
import java.math.BigDecimal;


/**
 * The persistent class for the workflow_bfm_t database table.
 * 
 */
@Entity
@Table(name="workflow_bfm_t")
@NamedQuery(name="WorkflowBfmT.findAll", query="SELECT w FROM WorkflowBfmT w")
public class WorkflowBfmT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="workflow_bfm_id")
	private String workflowBfmId;
	
	@Column(name="deal_financial_file")
	private byte[] dealFinancialFile;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="opportunity_id")
	private String opportunityId;

	private String exceptions;

	@Column(name="gross_margin")
	private BigDecimal grossMargin;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="created_by")
	private String createdBy;

	@Column(name="modified_by")
	private String modifiedBy;

	//bi-directional many-to-one association to OpportunityT
	@ManyToOne
	@JoinColumn(name="opportunity_id", insertable =false, updatable = false)
	private OpportunityT opportunityT;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable =false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable =false, updatable = false)
	private UserT modifiedByUser;

	public WorkflowBfmT() {
	}

	public String getWorkflowBfmId() {
		return this.workflowBfmId;
	}

	public void setWorkflowBfmId(String workflowBfmId) {
		this.workflowBfmId = workflowBfmId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public byte[] getDealFinancialFile() {
		return this.dealFinancialFile;
	}

	public void setDealFinancialFile(byte[] dealFinancialFile) {
		this.dealFinancialFile = dealFinancialFile;
	}

	public String getExceptions() {
		return this.exceptions;
	}

	public void setExceptions(String exceptions) {
		this.exceptions = exceptions;
	}

	public BigDecimal getGrossMargin() {
		return this.grossMargin;
	}

	public void setGrossMargin(BigDecimal grossMargin) {
		this.grossMargin = grossMargin;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public OpportunityT getOpportunityT() {
		return this.opportunityT;
	}

	public void setOpportunityT(OpportunityT opportunityT) {
		this.opportunityT = opportunityT;
	}

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
}