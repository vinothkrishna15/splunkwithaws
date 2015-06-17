package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the revenue_customer_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "revenue_customer_mapping_t")
@NamedQuery(name = "RevenueCustomerMappingT.findAll", query = "SELECT r FROM RevenueCustomerMappingT r")
public class RevenueCustomerMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	// @Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "finance_customer_name", insertable = false, updatable = false)
	private String financeCustomerName;

	@Column(name = "customer_geography", insertable = false, updatable = false)
	private String customerGeography;

	@EmbeddedId
	private RevenueCustomerMappingTPK id;

	// bi-directional many-to-one association to ActualRevenuesDataT
	@JsonIgnore
	@OneToMany(mappedBy = "revenueCustomerMappingT")
	private List<ActualRevenuesDataT> actualRevenuesDataTs;

	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_name", referencedColumnName = "customer_name")
	private CustomerMasterT customerMasterT;

	//bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name="customer_geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;
	
	public RevenueCustomerMappingT() {
	}

	public String getFinanceCustomerName() {
		return this.financeCustomerName;
	}

	public void setFinanceCustomerName(String financeCustomerName) {
		this.financeCustomerName = financeCustomerName;
	}

	public String getCustomerGeography() {
		return this.customerGeography;
	}

	public void setCustomerGeography(String customerGeography) {
		this.customerGeography = customerGeography;
	}

	public RevenueCustomerMappingTPK getId() {
		return this.id;
	}

	public void setId(RevenueCustomerMappingTPK id) {
		this.id = id;
	}

	public List<ActualRevenuesDataT> getActualRevenuesDataTs() {
		return this.actualRevenuesDataTs;
	}

	public void setActualRevenuesDataTs(
			List<ActualRevenuesDataT> actualRevenuesDataTs) {
		this.actualRevenuesDataTs = actualRevenuesDataTs;
	}

	public ActualRevenuesDataT addActualRevenuesDataT(
			ActualRevenuesDataT actualRevenuesDataT) {
		getActualRevenuesDataTs().add(actualRevenuesDataT);
		actualRevenuesDataT.setRevenueCustomerMappingT(this);

		return actualRevenuesDataT;
	}

	public ActualRevenuesDataT removeActualRevenuesDataT(
			ActualRevenuesDataT actualRevenuesDataT) {
		getActualRevenuesDataTs().remove(actualRevenuesDataT);
		actualRevenuesDataT.setRevenueCustomerMappingT(null);

		return actualRevenuesDataT;
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public GeographyMappingT getGeographyMappingT() {
		return this.geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

}