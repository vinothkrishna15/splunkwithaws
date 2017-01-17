package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the delivery_centre_utilization_t database table.
 * 
 */
@Entity
@Table(name="delivery_centre_utilization_t")
@NamedQuery(name="DeliveryCentreUtilizationT.findAll", query="SELECT d FROM DeliveryCentreUtilizationT d")
public class DeliveryCentreUtilizationT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="utilization_id")
	private Integer utilizationId;

	@Column(name="created_datetime", updatable = false)
	private Timestamp createdDatetime;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name="utilization_percentage")
	private BigDecimal utilizationPercentage;

	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id", insertable = false, updatable = false)
	private DeliveryCentreT deliveryCentreT;
	
	@Column(name="category_id")
	private Integer categoryId;
	
	@Column(name="cluster_id")
	private Integer clusterId;
	
	@Column(name="overall_percentage_id")
	private Integer overallPercentageId;
	
	@Column(name="junior_percentage")
	private BigDecimal juniorPercentage;

	@Column(name="senior_percentage")
	private BigDecimal seniorPercentage;

	@Column(name="trainee_percentage")
	private BigDecimal traineePercentage;
	
	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="category_id", insertable = false, updatable = false)
	private MobileDashboardComponentT mobileDashboardComponentT;
	
	//bi-directional many-to-one association to DeliveryClusterT
	@ManyToOne
	@JoinColumn(name="cluster_id", insertable = false, updatable = false)
	private DeliveryClusterT deliveryClusterT;

	//bi-directional many-to-one association to HealthCardOverallPercentage
	@ManyToOne
	@JoinColumn(name="overall_percentage_id", insertable = false, updatable = false)
	private HealthCardOverallPercentage healthCardOverallPercentage;

	public DeliveryCentreUtilizationT() {
	}

	public Integer getUtilizationId() {
		return this.utilizationId;
	}

	public void setUtilizationId(Integer utilizationId) {
		this.utilizationId = utilizationId;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getUtilizationPercentage() {
		return this.utilizationPercentage;
	}

	public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
		this.utilizationPercentage = utilizationPercentage;
	}

	public DeliveryCentreT getDeliveryCentreT() {
		return this.deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreT deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public MobileDashboardComponentT getMobileDashboardComponentT() {
		return mobileDashboardComponentT;
	}

	public void setMobileDashboardComponentT(
			MobileDashboardComponentT mobileDashboardComponentT) {
		this.mobileDashboardComponentT = mobileDashboardComponentT;
	}

	public DeliveryClusterT getDeliveryClusterT() {
		return deliveryClusterT;
	}

	public void setDeliveryClusterT(DeliveryClusterT deliveryClusterT) {
		this.deliveryClusterT = deliveryClusterT;
	}

	public HealthCardOverallPercentage getHealthCardOverallPercentage() {
		return healthCardOverallPercentage;
	}

	public void setHealthCardOverallPercentage(
			HealthCardOverallPercentage healthCardOverallPercentage) {
		this.healthCardOverallPercentage = healthCardOverallPercentage;
	}

	public Integer getClusterId() {
		return clusterId;
	}

	public void setClusterId(Integer clusterId) {
		this.clusterId = clusterId;
	}

	public Integer getOverallPercentageId() {
		return overallPercentageId;
	}

	public void setOverallPercentageId(Integer overallPercentageId) {
		this.overallPercentageId = overallPercentageId;
	}

	public BigDecimal getJuniorPercentage() {
		return juniorPercentage;
	}

	public void setJuniorPercentage(BigDecimal juniorPercentage) {
		this.juniorPercentage = juniorPercentage;
	}

	public BigDecimal getSeniorPercentage() {
		return seniorPercentage;
	}

	public void setSeniorPercentage(BigDecimal seniorPercentage) {
		this.seniorPercentage = seniorPercentage;
	}

	public BigDecimal getTraineePercentage() {
		return traineePercentage;
	}

	public void setTraineePercentage(BigDecimal traineePercentage) {
		this.traineePercentage = traineePercentage;
	}
}