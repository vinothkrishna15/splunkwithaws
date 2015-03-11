package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.util.List;


/**
 * The persistent class for the revenue_customer_mapping_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="financeCustomerName")
@Entity
@Table(name="revenue_customer_mapping_t")
@NamedQuery(name="RevenueCustomerMappingT.findAll", query="SELECT r FROM RevenueCustomerMappingT r")
public class RevenueCustomerMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="finance_customer_name")
	private String financeCustomerName;

	@Column(name="customer_geography")
	private String customerGeography;

	//bi-directional many-to-one association to ActualRevenuesDataT
	@OneToMany(mappedBy="revenueCustomerMappingT")
	private List<ActualRevenuesDataT> actualRevenuesDataTs;

	//bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name="customer_name", referencedColumnName="customer_name")
	private CustomerMasterT customerMasterT;

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

	public List<ActualRevenuesDataT> getActualRevenuesDataTs() {
		return this.actualRevenuesDataTs;
	}

	public void setActualRevenuesDataTs(List<ActualRevenuesDataT> actualRevenuesDataTs) {
		this.actualRevenuesDataTs = actualRevenuesDataTs;
	}

	public ActualRevenuesDataT addActualRevenuesDataT(ActualRevenuesDataT actualRevenuesDataT) {
		getActualRevenuesDataTs().add(actualRevenuesDataT);
		actualRevenuesDataT.setRevenueCustomerMappingT(this);

		return actualRevenuesDataT;
	}

	public ActualRevenuesDataT removeActualRevenuesDataT(ActualRevenuesDataT actualRevenuesDataT) {
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

}