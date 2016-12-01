package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the mobile_dashboard_component_t database table.
 * 
 */
@Entity
@Table(name="mobile_dashboard_component_t")
@NamedQuery(name="MobileDashboardComponentT.findAll", query="SELECT m FROM MobileDashboardComponentT m")
public class MobileDashboardComponentT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="component_id")
	private Integer componentId;

	@Column(name="component_name")
	private String componentName;

	//bi-directional many-to-one association to MobileDashboardT
	@OneToMany(mappedBy="mobileDashboardComponentT")
	private List<MobileDashboardT> mobileDashboardTs;

	public MobileDashboardComponentT() {
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

	public List<MobileDashboardT> getMobileDashboardTs() {
		return this.mobileDashboardTs;
	}

	public void setMobileDashboardTs(List<MobileDashboardT> mobileDashboardTs) {
		this.mobileDashboardTs = mobileDashboardTs;
	}

	public MobileDashboardT addMobileDashboardT(MobileDashboardT mobileDashboardT) {
		getMobileDashboardTs().add(mobileDashboardT);
		mobileDashboardT.setMobileDashboardComponentT(this);

		return mobileDashboardT;
	}

	public MobileDashboardT removeMobileDashboardT(MobileDashboardT mobileDashboardT) {
		getMobileDashboardTs().remove(mobileDashboardT);
		mobileDashboardT.setMobileDashboardComponentT(null);

		return mobileDashboardT;
	}

}