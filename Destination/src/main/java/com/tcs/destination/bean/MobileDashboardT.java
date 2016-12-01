package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


/**
 * The persistent class for the mobile_dashboard_t database table.
 * 
 */
@Entity
@Table(name="mobile_dashboard_t")
@NamedQuery(name="MobileDashboardT.findAll", query="SELECT m FROM MobileDashboardT m")
public class MobileDashboardT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="component_id")
	private Integer componentId;

	@Column(name="component_name")
	private String componentName;

	@Column(name="order_number")
	private Integer orderNumber;

	@Column(name="dashboard_category")
	private Integer dashboardCategory;

	@Column(name="user_id")
	private String userId;

	//bi-directional many-to-one association to MobileDashboardCategory
	@ManyToOne
	@JoinColumn(name="dashboard_category", insertable=false, updatable=false)
	private MobileDashboardCategory mobileDashboardCategory;

	public MobileDashboardT() {
		super();
	}

	public Integer getComponentId() {
		return this.componentId;
	}

	public void setComponentId(Integer componentId) {
		this.componentId = componentId;
	}

	public String getComponentName() {
		return this.componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
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

}