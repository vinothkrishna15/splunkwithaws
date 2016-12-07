package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The persistent class for the delivery_centre_unallocation_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class DeliveryCentreUnallocationDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer unallocationId;
	private Timestamp createdDatetime;
	private Date date;
	private BigDecimal juniorPercentage;
	private BigDecimal seniorPercentage;
	private BigDecimal traineePercentage;
	private Integer deliveryCentreId;
	private DeliveryCentreDTO deliveryCentreT;

	public DeliveryCentreUnallocationDTO() {
		super();
	}

	public Integer getUnallocationId() {
		return unallocationId;
	}

	public void setUnallocationId(Integer unallocationId) {
		this.unallocationId = unallocationId;
	}

	public Timestamp getCreatedDatetime() {
		return createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public DeliveryCentreDTO getDeliveryCentreT() {
		return deliveryCentreT;
	}

	public void setDeliveryCentreT(DeliveryCentreDTO deliveryCentreT) {
		this.deliveryCentreT = deliveryCentreT;
	}
}