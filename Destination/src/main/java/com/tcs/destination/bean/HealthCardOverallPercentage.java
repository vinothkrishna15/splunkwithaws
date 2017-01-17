package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the health_card_overall_percentage database table.
 * 
 */
@Entity
@Table(name="health_card_overall_percentage")
@NamedQuery(name="HealthCardOverallPercentage.findAll", query="SELECT h FROM HealthCardOverallPercentage h")
public class HealthCardOverallPercentage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="overall_percentage_id")
	private Integer overallPercentageId;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name="overall_percentage")
	private BigDecimal overallPercentage;
	
	@Column(name="component_id")
	private Integer componentId;

	//bi-directional many-to-one association to DeliveryCentreUtilizationT
	@OneToMany(mappedBy="healthCardOverallPercentage")
	private List<DeliveryCentreUtilizationT> deliveryCentreUtilizationTs;
	
	//bi-directional many-to-one association to DeliveryCentreUtilizationT
	@OneToMany(mappedBy="healthCardOverallPercentage")
	private List<DeliveryCentreUnallocationT> deliveryCentreUnallocationTs;

	//bi-directional many-to-one association to MobileDashboardComponentT
	@ManyToOne
	@JoinColumn(name="component_id", insertable = false, updatable = false)
	private MobileDashboardComponentT mobileDashboardComponentT;

	public HealthCardOverallPercentage() {
	}

	public Integer getOverallPercentageId() {
		return this.overallPercentageId;
	}

	public void setOverallPercentageId(Integer overallPercentageId) {
		this.overallPercentageId = overallPercentageId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getOverallPercentage() {
		return this.overallPercentage;
	}

	public void setOverallPercentage(BigDecimal overallPercentage) {
		this.overallPercentage = overallPercentage;
	}

	public List<DeliveryCentreUtilizationT> getDeliveryCentreUtilizationTs() {
		return this.deliveryCentreUtilizationTs;
	}

	public void setDeliveryCentreUtilizationTs(List<DeliveryCentreUtilizationT> deliveryCentreUtilizationTs) {
		this.deliveryCentreUtilizationTs = deliveryCentreUtilizationTs;
	}

	public DeliveryCentreUtilizationT addDeliveryCentreUtilizationT(DeliveryCentreUtilizationT deliveryCentreUtilizationT) {
		getDeliveryCentreUtilizationTs().add(deliveryCentreUtilizationT);
		deliveryCentreUtilizationT.setHealthCardOverallPercentage(this);

		return deliveryCentreUtilizationT;
	}

	public DeliveryCentreUtilizationT removeDeliveryCentreUtilizationT(DeliveryCentreUtilizationT deliveryCentreUtilizationT) {
		getDeliveryCentreUtilizationTs().remove(deliveryCentreUtilizationT);
		deliveryCentreUtilizationT.setHealthCardOverallPercentage(null);

		return deliveryCentreUtilizationT;
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

	public List<DeliveryCentreUnallocationT> getDeliveryCentreUnallocationTs() {
		return deliveryCentreUnallocationTs;
	}

	public void setDeliveryCentreUnallocationTs(
			List<DeliveryCentreUnallocationT> deliveryCentreUnallocationTs) {
		this.deliveryCentreUnallocationTs = deliveryCentreUnallocationTs;
	}
	
}