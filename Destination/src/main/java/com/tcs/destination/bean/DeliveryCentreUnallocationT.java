package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.sql.Timestamp;


/**
 * The persistent class for the delivery_centre_unallocation_t database table.
 * 
 */
@Entity
@Table(name="delivery_centre_unallocation_t")
@NamedQuery(name="DeliveryCentreUnallocationT.findAll", query="SELECT d FROM DeliveryCentreUnallocationT d")
public class DeliveryCentreUnallocationT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="unallocation_id")
	private Integer unallocationId;

	@Column(name="created_datetime", updatable=false)
	private Timestamp createdDatetime;

	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name="junior_percentage")
	private BigDecimal juniorPercentage;

	@Column(name="senior_percentage")
	private BigDecimal seniorPercentage;

	@Column(name="trainee_percentage")
	private BigDecimal traineePercentage;

	@Column(name="delivery_centre_id")
	private Integer deliveryCentreId;

	//bi-directional many-to-one association to DeliveryCentreT
	@ManyToOne
	@JoinColumn(name="delivery_centre_id", insertable=false, updatable=false)
	private DeliveryCentreT deliveryCentreT;

	public DeliveryCentreUnallocationT() {
	}

	public Integer getUnallocationId() {
		return this.unallocationId;
	}

	public void setUnallocationId(Integer unallocationId) {
		this.unallocationId = unallocationId;
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

	public BigDecimal getJuniorPercentage() {
		return this.juniorPercentage;
	}

	public void setJuniorPercentage(BigDecimal juniorPercentage) {
		this.juniorPercentage = juniorPercentage;
	}

	public BigDecimal getSeniorPercentage() {
		return this.seniorPercentage;
	}

	public void setSeniorPercentage(BigDecimal seniorPercentage) {
		this.seniorPercentage = seniorPercentage;
	}

	public BigDecimal getTraineePercentage() {
		return this.traineePercentage;
	}

	public void setTraineePercentage(BigDecimal traineePercentage) {
		this.traineePercentage = traineePercentage;
	}

	public DeliveryCentreT getDeliveryCentreT() {
		return this.deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreT deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}

}