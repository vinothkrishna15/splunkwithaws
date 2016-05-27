package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;
import java.util.Date;

/**
 * The persistent class for the deal_closure_reporting_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reportingId")
@Entity
@Table(name="deal_closure_reporting_t")
@NamedQuery(name="DealClosureReportingT.findAll", query="SELECT d FROM DealClosureReportingT d")
public class DealClosureReportingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="reporting_id")
	private Integer reportingId;

	@Column(name="created_by")
	private String createdBy;

	@Column(name="created_datetime")
	private Timestamp createdDateTime;

	@Temporal(TemporalType.DATE)
	@Column(name="deal_reporting_end_date")
	private Date dealReportingEndDate;

	@Temporal(TemporalType.DATE)
	@Column(name="deal_reporting_start_date")
	private Date dealReportingStartDate;

	@Column(name="modified_by")
	private String modifiedBy;

	@Column(name="modified_datetime")
	private Timestamp modifiedDateTime;
	
	@Column(name="active")
	private boolean active;

	public DealClosureReportingT() {
	}

	public String getCreatedBy() {
		return this.createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreatedDateTime() {
		return this.createdDateTime;
	}

	public void setCreatedDateTime(Timestamp createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

	public Date getDealReportingEndDate() {
		return this.dealReportingEndDate;
	}

	public void setDealReportingEndDate(Date dealReportingEndDate) {
		this.dealReportingEndDate = dealReportingEndDate;
	}

	public Date getDealReportingStartDate() {
		return this.dealReportingStartDate;
	}

	public void setDealReportingStartDate(Date dealReportingStartDate) {
		this.dealReportingStartDate = dealReportingStartDate;
	}

	public String getModifiedBy() {
		return this.modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Timestamp getModifiedDateTime() {
		return this.modifiedDateTime;
	}

	public void setModifiedDateTime(Timestamp modifiedDateTime) {
		this.modifiedDateTime = modifiedDateTime;
	}

	public Integer getReportingId() {
		return this.reportingId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}