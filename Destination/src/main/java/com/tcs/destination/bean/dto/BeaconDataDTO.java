package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The BeaconDataDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class BeaconDataDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String beaconDataId;
	private String beaconGroupClient;
	private String financialYear;
	private String quarter;
	private BigDecimal target;
	private Long beaconCustomerMapId;
	
	public BeaconDataDTO() {
		super();
	}
	public String getBeaconDataId() {
		return beaconDataId;
	}
	public void setBeaconDataId(String beaconDataId) {
		this.beaconDataId = beaconDataId;
	}
	public String getBeaconGroupClient() {
		return beaconGroupClient;
	}
	public void setBeaconGroupClient(String beaconGroupClient) {
		this.beaconGroupClient = beaconGroupClient;
	}
	public String getFinancialYear() {
		return financialYear;
	}
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	public String getQuarter() {
		return quarter;
	}
	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}
	public BigDecimal getTarget() {
		return target;
	}
	public void setTarget(BigDecimal target) {
		this.target = target;
	}
	public Long getBeaconCustomerMapId() {
		return beaconCustomerMapId;
	}
	public void setBeaconCustomerMapId(Long beaconCustomerMapId) {
		this.beaconCustomerMapId = beaconCustomerMapId;
	}

}
