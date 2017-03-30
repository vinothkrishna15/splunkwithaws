package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



/**
 * The persistent class for the mobile_dashboard_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name="mobile_dashboard_t")
@NamedQuery(name="MobileDashboardT.findAll", query="SELECT m FROM MobileDashboardT m")
public class MobileDashboardT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="dashboard_id")
	private Integer dashboardId;

	@Column(name="order_number")
	private Integer orderNumber;

	@Column(name="dashboard_category")
	private Integer dashboardCategory;
	
	@Column(name="component_id")
	private Integer componentId;

	@Column(name="user_id")
	private String userId;
	
	@Transient
	private BigDecimal quarterlyPercentage;

	//bi-directional many-to-one association to MobileDashboardCategory
	@ManyToOne
	@JoinColumn(name="dashboard_category", insertable=false, updatable=false)
	private MobileDashboardCategory mobileDashboardCategory;
	
	//bi-directional many-to-one association to MobileDashboardComponentT
	@ManyToOne
	@JoinColumn(name="component_id", insertable=false, updatable=false)
	private MobileDashboardComponentT mobileDashboardComponentT;
	
	@Transient
	private BigDecimal value;

	public MobileDashboardT() {
		super();
	}

	public Integer getOrderNumber() {
		return this.orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public MobileDashboardCategory getMobileDashboardCategory() {
		return this.mobileDashboardCategory;
	}

	public void setMobileDashboardCategory(MobileDashboardCategory mobileDashboardCategory) {
		this.mobileDashboardCategory = mobileDashboardCategory;
	}

	public Integer getDashboardCategory() {
		return dashboardCategory;
	}

	public void setDashboardCategory(Integer dashboardCategory) {
		this.dashboardCategory = dashboardCategory;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getDashboardId() {
		return dashboardId;
	}

	public void setDashboardId(Integer dashboardId) {
		this.dashboardId = dashboardId;
	}

	public Integer getComponentId() {
		return componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public MobileDashboardComponentT getMobileDashboardComponentT() {
		return mobileDashboardComponentT;
	}

	public void setMobileDashboardComponentT(
			MobileDashboardComponentT mobileDashboardComponentT) {
		this.mobileDashboardComponentT = mobileDashboardComponentT;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getQuarterlyPercentage() {
		return quarterlyPercentage;
	}

	public void setQuarterlyPercentage(BigDecimal quarterlyPercentage) {
		this.quarterlyPercentage = quarterlyPercentage;
	}
}