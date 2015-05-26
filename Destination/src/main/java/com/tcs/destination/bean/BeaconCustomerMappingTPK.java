package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The primary key class for the beacon_customer_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Embeddable
public class BeaconCustomerMappingTPK implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name = "beacon_customer_name")
	private String beaconCustomerName;

	@Column(name="customer_geography", insertable=false, updatable=false)
	private String customerGeography;

	public BeaconCustomerMappingTPK() {
	}

	public String getBeaconCustomerName() {
		return this.beaconCustomerName;
	}

	public void setBeaconCustomerName(String beaconCustomerName) {
		this.beaconCustomerName = beaconCustomerName;
	}

	public String getCustomerGeography() {
		return this.customerGeography;
	}

	public void setCustomerGeography(String customerGeography) {
		this.customerGeography = customerGeography;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeaconCustomerMappingTPK)) {
			return false;
		}
		BeaconCustomerMappingTPK castOther = (BeaconCustomerMappingTPK)other;
		return 
			this.beaconCustomerName.equals(castOther.beaconCustomerName)
			&& this.customerGeography.equals(castOther.customerGeography);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.beaconCustomerName.hashCode();
		hash = hash * prime + this.customerGeography.hashCode();

		return hash;
	}
}