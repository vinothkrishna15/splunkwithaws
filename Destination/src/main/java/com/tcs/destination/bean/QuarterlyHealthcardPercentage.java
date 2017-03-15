package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the quarterly_healthcard_percentage database table.
 * 
 */
@Entity
@Table(name="quarterly_healthcard_percentage")
@NamedQuery(name="QuarterlyHealthcardPercentage.findAll", query="SELECT q FROM QuarterlyHealthcardPercentage q")
public class QuarterlyHealthcardPercentage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="quarterly_healthcard_percentage_id")
	private Integer quarterlyHealthcardPercentageId;

	private BigDecimal percentage;

	private Integer quarter;

	private Integer year;
	
	@Column(name="component_id")
	private Integer componentId;
	
	@Column(name = "created_datetime", updatable = false)
	private Timestamp createdDatetime;
	
	@Column(name = "modified_datetime")
	private Timestamp modifiedDatetime;

	//bi-directional many-to-one association to MobileDashboardComponentT
	@ManyToOne
	@JoinColumn(name="component_id", updatable = false, insertable = false)
	private MobileDashboardComponentT mobileDashboardComponentT;

	public QuarterlyHealthcardPercentage() {
	}

	public Integer getQuarterlyHealthcardPercentageId() {
		return this.quarterlyHealthcardPercentageId;
	}

	public void setQuarterlyHealthcardPercentageId(Integer quarterlyHealthcardPercentageId) {
		this.quarterlyHealthcardPercentageId = quarterlyHealthcardPercentageId;
	}

	public BigDecimal getPercentage() {
		return this.percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public Integer getQuarter() {
		return this.quarter;
	}

	public void setQuarter(Integer quarter) {
		this.quarter = quarter;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public MobileDashboardComponentT getMobileDashboardComponentT() {
		return this.mobileDashboardComponentT;
	}

	public void setMobileDashboardComponentT(MobileDashboardComponentT mobileDashboardComponentT) {
		this.mobileDashboardComponentT = mobileDashboardComponentT;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Timestamp getModifiedDatetime() {
		return modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}
	
}