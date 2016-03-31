package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

	
	@Id
	@Column(name="revenue_customer_map_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long revenueCustomerMapId;
	
	// @Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "finance_customer_name")
	private String financeCustomerName;

	@Column(name = "customer_geography")
	private String customerGeography;
	
	@Column(name = "finance_iou")
	private String financeIou;

	// bi-directional many-to-one association to ActualRevenuesDataT
	@JsonIgnore
	@OneToMany(mappedBy = "revenueCustomerMappingT")
	private List<ActualRevenuesDataT> actualRevenuesDataTs;

	//bi-directional many-to-one association to ProjectedRevenuesDataT
	@JsonIgnore
	@OneToMany(mappedBy="revenueCustomerMappingT")
	private List<ProjectedRevenuesDataT> projectedRevenuesDataTs;
	
	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_id", insertable = false, updatable = false)
	private CustomerMasterT customerMasterT;
	
	@Column(name = "customer_id")
	private String customerId;

	//bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name="customer_geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;

	//bi-directional many-to-one association to IouCustomerMappingT
	@ManyToOne
	@JoinColumn(name="finance_iou", insertable = false, updatable = false)
	private IouCustomerMappingT iouCustomerMappingT;

	public RevenueCustomerMappingT() {
	}
	
	public Long getRevenueCustomerMapId() {
		return this.revenueCustomerMapId;
	}

	public void setRevenueCustomerMapId(Long revenueCustomerMapId) {
		this.revenueCustomerMapId = revenueCustomerMapId;
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

	public List<ProjectedRevenuesDataT> getProjectedRevenuesDataTs() {
		return this.projectedRevenuesDataTs;
	}

	public void setProjectedRevenuesDataTs(List<ProjectedRevenuesDataT> projectedRevenuesDataTs) {
		this.projectedRevenuesDataTs = projectedRevenuesDataTs;
	}

	public ProjectedRevenuesDataT addProjectedRevenuesDataT(ProjectedRevenuesDataT projectedRevenuesDataT) {
		getProjectedRevenuesDataTs().add(projectedRevenuesDataT);
		projectedRevenuesDataT.setRevenueCustomerMappingT(this);

		return projectedRevenuesDataT;
	}

	public ProjectedRevenuesDataT removeProjectedRevenuesDataT(ProjectedRevenuesDataT projectedRevenuesDataT) {
		getProjectedRevenuesDataTs().remove(projectedRevenuesDataT);
		projectedRevenuesDataT.setRevenueCustomerMappingT(null);

		return projectedRevenuesDataT;
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

	public IouCustomerMappingT getIouCustomerMappingT() {
		return this.iouCustomerMappingT;
	}

	public void setIouCustomerMappingT(IouCustomerMappingT iouCustomerMappingT) {
		this.iouCustomerMappingT = iouCustomerMappingT;
	}

	public String getFinanceIou() {
		return financeIou;
	}

	public void setFinanceIou(String financeIou) {
		this.financeIou = financeIou;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

}
