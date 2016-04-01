package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the iou_customer_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="iou")
@Entity
@Table(name = "iou_customer_mapping_t")
@NamedQuery(name = "IouCustomerMappingT.findAll", query = "SELECT i FROM IouCustomerMappingT i")
public class IouCustomerMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String iou;

	@Column(name = "display_iou")
	private String displayIou;


	//bi-directional many-to-one association to CustomerMasterT
	@JsonIgnore
	@OneToMany(mappedBy="iouCustomerMappingT")
	private List<CustomerMasterT> customerMasterTs;


	//bi-directional many-to-one association to RevenueCustomerMappingT
	@JsonIgnore
	@OneToMany(mappedBy="iouCustomerMappingT")
	private List<RevenueCustomerMappingT> revenueCustomerMappingTs;
		

	public IouCustomerMappingT() {
	}

	public String getIou() {
		return this.iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public String getDisplayIou() {
		return this.displayIou;
	}

	public void setDisplayIou(String displayIou) {
		this.displayIou = displayIou;
	}



	public List<CustomerMasterT> getCustomerMasterTs() {
		return this.customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public CustomerMasterT addCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().add(customerMasterT);
		customerMasterT.setIouCustomerMappingT(this);

		return customerMasterT;
	}

	public CustomerMasterT removeCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().remove(customerMasterT);
		customerMasterT.setIouCustomerMappingT(null);

		return customerMasterT;
	}

	public List<RevenueCustomerMappingT> getRevenueCustomerMappingTs() {
		return this.revenueCustomerMappingTs;
	}

	public void setRevenueCustomerMappingTs(List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		this.revenueCustomerMappingTs = revenueCustomerMappingTs;
	}

	public RevenueCustomerMappingT addRevenueCustomerMappingT(RevenueCustomerMappingT revenueCustomerMappingT) {
		getRevenueCustomerMappingTs().add(revenueCustomerMappingT);
		revenueCustomerMappingT.setIouCustomerMappingT(this);

		return revenueCustomerMappingT;
	}

	public RevenueCustomerMappingT removeRevenueCustomerMappingT(RevenueCustomerMappingT revenueCustomerMappingT) {
		getRevenueCustomerMappingTs().remove(revenueCustomerMappingT);
		revenueCustomerMappingT.setIouCustomerMappingT(null);

		return revenueCustomerMappingT;
	}

}