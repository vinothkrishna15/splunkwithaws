package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The persistent class for the delivery_centre_utilization_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class DeliveryCentreUtilizationDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer utilizationId;
	private Timestamp createdDatetime;
	private Date date;
	private BigDecimal utilizationPercentage;
	private Integer deliveryCentreId;
	private DeliveryCentreDTO deliveryCentreT;

	public DeliveryCentreUtilizationDTO() {
		super();
	}

	public Integer getUtilizationId() {
		return utilizationId;
	}

	public void setUtilizationId(Integer utilizationId) {
		this.utilizationId = utilizationId;
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

	public BigDecimal getUtilizationPercentage() {
		return utilizationPercentage;
	}

	public void setUtilizationPercentage(BigDecimal utilizationPercentage) {
		this.utilizationPercentage = utilizationPercentage;
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