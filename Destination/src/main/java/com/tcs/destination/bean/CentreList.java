package com.tcs.destination.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CentreList {
	private Integer deliveryCentreId;
	private String deliveryCentre;
	private BigDecimal deliveryCentrePercentage;
	private UnallocationAssociate unallocationAssociate;
	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}
	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}
	public String getDeliveryCentre() {
		return deliveryCentre;
	}
	public void setDeliveryCentre(String deliveryCentre) {
		this.deliveryCentre = deliveryCentre;
	}
	public BigDecimal getDeliveryCentrePercentage() {
		return deliveryCentrePercentage;
	}
	public void setDeliveryCentrePercentage(BigDecimal deliveryCentrePercentage) {
		this.deliveryCentrePercentage = deliveryCentrePercentage;
	}
	public UnallocationAssociate getUnallocationAssociate() {
		return unallocationAssociate;
	}
	public void setUnallocationAssociate(UnallocationAssociate unallocationAssociate) {
		this.unallocationAssociate = unallocationAssociate;
	}
}
