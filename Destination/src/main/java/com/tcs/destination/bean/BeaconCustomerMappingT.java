package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the beacon_customer_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@Entity
@Table(name = "beacon_customer_mapping_t")
@NamedQuery(name = "BeaconCustomerMappingT.findAll", query = "SELECT b FROM BeaconCustomerMappingT b")
public class BeaconCustomerMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private BeaconCustomerMappingTPK id;

	//bi-directional many-to-one association to CustomerMasterT
	@ManyToOne
	@JoinColumn(name = "customer_name", referencedColumnName = "customer_name")
	private CustomerMasterT customerMasterT;

	// bi-directional many-to-one association to BeaconDataT
	@OneToMany(mappedBy = "beaconCustomerMappingT")
	private List<BeaconDataT> beaconDataTs;

	public BeaconCustomerMappingT() {
	}

	public BeaconCustomerMappingTPK getId() {
		return this.id;
	}

	public void setId(BeaconCustomerMappingTPK id) {
		this.id = id;
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