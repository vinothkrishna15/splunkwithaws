package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the delivery_cluster_t database table.
 * 
 */
@Entity
@Table(name="delivery_cluster_t")
@NamedQuery(name="DeliveryClusterT.findAll", query="SELECT d FROM DeliveryClusterT d")
public class DeliveryClusterT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="delivery_cluster_id")
	private Integer deliveryClusterId;

	private String active;

	@Column(name="created_datetime")
	private Timestamp createdDatetime;

	@Column(name="delivery_cluster")
	private String deliveryCluster;

	@Column(name="modified_datetime")
	private Timestamp modifiedDatetime;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Column(name="delivery_cluster_head")
	private String deliveryClusterHead;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="created_by", insertable = false, updatable = false)
	private UserT createdByUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="delivery_cluster_head", insertable = false, updatable = false)
	private UserT deliveryClusterHeadUser;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="modified_by", insertable = false, updatable = false)
	private UserT modifiedByUser;

	public DeliveryClusterT() {
	}

	public Integer getDeliveryClusterId() {
		return this.deliveryClusterId;
	}

	public void setDeliveryClusterId(Integer deliveryClusterId) {
		this.deliveryClusterId = deliveryClusterId;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public Timestamp getCreatedDatetime() {
		return this.createdDatetime;
	}

	public void setCreatedDatetime(Timestamp createdDatetime) {
		this.createdDatetime = createdDatetime;
	}

	public String getDeliveryCluster() {
		return this.deliveryCluster;
	}

	public void setDeliveryCluster(String deliveryCluster) {
		this.deliveryCluster = deliveryCluster;
	}

	public Timestamp getModifiedDatetime() {
		return this.modifiedDatetime;
	}

	public void setModifiedDatetime(Timestamp modifiedDatetime) {
		this.modifiedDatetime = modifiedDatetime;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getDeliveryClusterHead() {
		return deliveryClusterHead;
	}

	public void setDeliveryClusterHead(String deliveryClusterHead) {
		this.deliveryClusterHead = deliveryClusterHead;
	}

	public UserT getCreatedByUser() {
		return createdByUser;
	}

	public void setCreatedByUser(UserT createdByUser) {
		this.createdByUser = createdByUser;
	}

	public UserT getDeliveryClusterHeadUser() {
		return deliveryClusterHeadUser;
	}

	public void setDeliveryClusterHeadUser(UserT deliveryClusterHeadUser) {
		this.deliveryClusterHeadUser = deliveryClusterHeadUser;
	}

	public UserT getModifiedByUser() {
		return modifiedByUser;
	}

	public void setModifiedByUser(UserT modifiedByUser) {
		this.modifiedByUser = modifiedByUser;
	}
	
	

}