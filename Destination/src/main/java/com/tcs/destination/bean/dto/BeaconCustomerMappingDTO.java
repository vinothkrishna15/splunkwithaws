package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The BeaconCustomerMappingDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class BeaconCustomerMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long beaconCustomerMapId;
	private String customerId;
	private String beaconCustomerName;
	private String customerGeography;
	private String beaconIou;
	private boolean active;
	
	private GeographyMappingDTO geographyMappingT;
	private List<BeaconDataDTO> beaconDataTs;
	private IouBeaconMappingDTO iouBeaconMappingT;

	public BeaconCustomerMappingDTO() {
		super();
	}

	public Long getBeaconCustomerMapId() {
		return beaconCustomerMapId;
	}

	public void setBeaconCustomerMapId(Long beaconCustomerMapId) {
		this.beaconCustomerMapId = beaconCustomerMapId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getBeaconCustomerName() {
		return beaconCustomerName;
	}

	public void setBeaconCustomerName(String beaconCustomerName) {
		this.beaconCustomerName = beaconCustomerName;
	}

	public String getCustomerGeography() {
		return customerGeography;
	}

	public void setCustomerGeography(String customerGeography) {
		this.customerGeography = customerGeography;
	}

	public String getBeaconIou() {
		return beaconIou;
	}

	public void setBeaconIou(String beaconIou) {
		this.beaconIou = beaconIou;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public GeographyMappingDTO getGeographyMappingT() {
		return geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingDTO geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
	}

	public List<BeaconDataDTO> getBeaconDataTs() {
		return beaconDataTs;
	}

	public void setBeaconDataTs(List<BeaconDataDTO> beaconDataTs) {
		this.beaconDataTs = beaconDataTs;
	}

	public IouBeaconMappingDTO getIouBeaconMappingT() {
		return iouBeaconMappingT;
	}

	public void setIouBeaconMappingT(IouBeaconMappingDTO iouBeaconMappingT) {
		this.iouBeaconMappingT = iouBeaconMappingT;
	}

}
