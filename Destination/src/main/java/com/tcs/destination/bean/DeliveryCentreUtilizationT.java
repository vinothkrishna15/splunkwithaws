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

}