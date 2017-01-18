package com.tcs.destination.bean;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ClusterList {
	private String deliveryCluster;
	private BigDecimal clusterPercentage;
	private String deliveryClusterHead;
	private List<CentreList> centreList;
	private UnallocationAssociate unallocationAssociate;
	
	public String getDeliveryCluster() {
		return deliveryCluster;
	}
	public void setDeliveryCluster(String deliveryCluster) {
		this.deliveryCluster = deliveryCluster;
	}
	public BigDecimal getClusterPercentage() {
		return clusterPercentage;
	}
	public void setClusterPercentage(BigDecimal clusterPercentage) {
		this.clusterPercentage = clusterPercentage;
	}
	public List<CentreList> getCentreList() {
		return centreList;
	}
	public void setCentreList(List<CentreList> centreList) {
		this.centreList = centreList;
	}
	public UnallocationAssociate getUnallocationAssociate() {
		return unallocationAssociate;
	}
	public void setUnallocationAssociate(UnallocationAssociate unallocationAssociate) {
		this.unallocationAssociate = unallocationAssociate;
	}
	public String getDeliveryClusterHead() {
		return deliveryClusterHead;
	}
	public void setDeliveryClusterHead(String deliveryClusterHead) {
		this.deliveryClusterHead = deliveryClusterHead;
	}
}
