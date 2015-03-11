package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * The persistent class for the beacon_customer_mapping_t database table.
 * 
 */
@Entity
@Table(name = "beacon_customer_mapping_t")
@NamedQuery(name = "BeaconCustomerMappingT.findAll", query = "SELECT b FROM BeaconCustomerMappingT b")
public class BeaconCustomerMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "beacon_customer_name")
	private String beaconCustomerName;

	@Column(name = "customer_geography")
	private String customerGeography;

	// bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_name", referencedColumnName = "customer_name")
	private CustomerMasterT customerMasterT;

	// bi-directional many-to-one association to BeaconDataT
	@OneToMany(mappedBy = "beaconCustomerMappingT")
	private List<BeaconDataT> beaconDataTs;

	public BeaconCustomerMappingT() {
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

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public List<BeaconDataT> getBeaconDataTs() {
		return this.beaconDataTs;
	}

	public void setBeaconDataTs(List<BeaconDataT> beaconDataTs) {
		this.beaconDataTs = beaconDataTs;
	}

	public BeaconDataT addBeaconDataT(BeaconDataT beaconDataT) {
		getBeaconDataTs().add(beaconDataT);
		beaconDataT.setBeaconCustomerMappingT(this);

		return beaconDataT;
	}

	public BeaconDataT removeBeaconDataT(BeaconDataT beaconDataT) {
		getBeaconDataTs().remove(beaconDataT);
		beaconDataT.setBeaconCustomerMappingT(null);

		return beaconDataT;
	}

}