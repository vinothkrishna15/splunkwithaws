package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;


/**
 * The persistent class for the customer_comments_t database table.
 * 
 */
@Entity
@Table(name="customer_comments_t")
@NamedQuery(name="CustomerCommentsT.findAll", query="SELECT c FROM CustomerCommentsT c")
public class CustomerCommentsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="customer_comments_id")
	private String customerCommentsId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="customer_comments")
	private String customerComments;

	//bi-directional many-to-one association to CustomerMasterT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="customer_id")
	private CustomerMasterT customerMasterT;

	public CustomerCommentsT() {
	}

	public String getCustomerCommentsId() {
		return this.customerCommentsId;
	}

	public void setCustomerCommentsId(String customerCommentsId) {
		this.customerCommentsId = customerCommentsId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getCustomerComments() {
		return this.customerComments;
	}

	public void setCustomerComments(String customerComments) {
		this.customerComments = customerComments;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

}