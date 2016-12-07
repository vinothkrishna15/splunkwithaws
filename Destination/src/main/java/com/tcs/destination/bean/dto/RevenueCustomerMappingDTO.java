package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The RevenueCustomerMappingDTO
 * 
 */
@JsonInclude(Include.NON_NULL)
public class RevenueCustomerMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long revenueCustomerMapId;
	private String financeCustomerName;
	private String customerGeography;
	private String financeIou;
	private boolean active = true;
	private String customerId;

	private List<ActualRevenuesDataDTO> actualRevenuesDataTs;
	private List<ProjectedRevenuesDataDTO> projectedRevenuesDataTs;

	private GeographyMappingDTO geographyMappingT;
	private IouCustomerMappingDTO iouCustomerMappingT;

	public RevenueCustomerMappingDTO() {
		super();
	}

	public Long getRevenueCustomerMapId() {
		return revenueCustomerMapId;
	}

	public void setRevenueCustomerMapId(Long revenueCustomerMapId) {
		this.revenueCustomerMapId = revenueCustomerMapId;
	}

	public String getFinanceCustomerName() {
		return financeCustomerName;
	}

	public void setFinanceCustomerName(String financeCustomerName) {
		this.financeCustomerName = financeCustomerName;
	}

	public String getCustomerGeography() {
		return customerGeography;
	}

	public void setCustomerGeography(String customerGeography) {
		this.customerGeography = customerGeography;
	}

	public String getFinanceIou() {
		return financeIou;
	}

	public void setFinanceIou(String financeIou) {
		this.financeIou = financeIou;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<ActualRevenuesDataDTO> getActualRevenuesDataTs() {
		return actualRevenuesDataTs;
	}

	public void setActualRevenuesDataTs(
			List<ActualRevenuesDataDTO> actualRevenuesDataTs) {
		this.actualRevenuesDataTs = actualRevenuesDataTs;
	}

	public List<ProjectedRevenuesDataDTO> getProjectedRevenuesDataTs() {
		return projectedRevenuesDataTs;
	}

	public void setProjectedRevenuesDataTs(
			List<ProjectedRevenuesDataDTO> projectedRevenuesDataTs) {
		this.projectedRevenuesDataTs = projectedRevenuesDataTs;
	}

	public GeographyMappingDTO getGeographyMappingT() {
		return geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingDTO geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public IouCustomerMappingDTO getIouCustomerMappingT() {
		return iouCustomerMappingT;
	}

	public void setIouCustomerMappingT(IouCustomerMappingDTO iouCustomerMappingT) {
		this.iouCustomerMappingT = iouCustomerMappingT;
	}
	
}
