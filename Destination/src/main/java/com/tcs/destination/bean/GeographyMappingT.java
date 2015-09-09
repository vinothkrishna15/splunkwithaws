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
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the geography_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "geography")
@Entity
@Table(name = "geography_mapping_t")
@NamedQuery(name = "GeographyMappingT.findAll", query = "SELECT g FROM GeographyMappingT g")
public class GeographyMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String geography;

	private String active;

	@Column(name = "display_geography")
	private String displayGeography;
	
	
	//bi-directional many-to-one association to GeographyCountryMappingT
	@OneToMany(mappedBy="geographyMappingT")
	private List<GeographyCountryMappingT> geographyCountryMappingTs;


	// bi-directional many-to-one association to BeaconCustomerMappingT
	@JsonIgnore
	@OneToMany(mappedBy = "geographyMappingT")
	private List<BeaconCustomerMappingT> beaconCustomerMappingTs;

	// bi-directional many-to-one association to CustomerMasterT
	@JsonIgnore
	@OneToMany(mappedBy = "geographyMappingT")
	private List<CustomerMasterT> customerMasterTs;

	// bi-directional many-to-one association to PartnerMasterT
	@JsonIgnore
	@OneToMany(mappedBy = "geographyMappingT")
	private List<PartnerMasterT> partnerMasterTs;

	// bi-directional many-to-one association to RevenueCustomerMappingT
	@JsonIgnore
	@OneToMany(mappedBy = "geographyMappingT")
	private List<RevenueCustomerMappingT> revenueCustomerMappingTs;

	public GeographyMappingT() {
	}

	public String getGeography() {
		return this.geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDisplayGeography() {
		return this.displayGeography;
	}

	public void setDisplayGeography(String displayGeography) {
		this.displayGeography = displayGeography;
	}

	public List<BeaconCustomerMappingT> getBeaconCustomerMappingTs() {
		return this.beaconCustomerMappingTs;
	}

	public void setBeaconCustomerMappingTs(
			List<BeaconCustomerMappingT> beaconCustomerMappingTs) {
		this.beaconCustomerMappingTs = beaconCustomerMappingTs;
	}

	public BeaconCustomerMappingT addBeaconCustomerMappingT(
			BeaconCustomerMappingT beaconCustomerMappingT) {
		getBeaconCustomerMappingTs().add(beaconCustomerMappingT);
		beaconCustomerMappingT.setGeographyMappingT(this);

		return beaconCustomerMappingT;
	}

	public BeaconCustomerMappingT removeBeaconCustomerMappingT(
			BeaconCustomerMappingT beaconCustomerMappingT) {
		getBeaconCustomerMappingTs().remove(beaconCustomerMappingT);
		beaconCustomerMappingT.setGeographyMappingT(null);

		return beaconCustomerMappingT;
	}

	public List<CustomerMasterT> getCustomerMasterTs() {
		return this.customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public CustomerMasterT addCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().add(customerMasterT);
		customerMasterT.setGeographyMappingT(this);

		return customerMasterT;
	}

	public CustomerMasterT removeCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().remove(customerMasterT);
		customerMasterT.setGeographyMappingT(null);

		return customerMasterT;
	}

	public List<PartnerMasterT> getPartnerMasterTs() {
		return this.partnerMasterTs;
	}

	public void setPartnerMasterTs(List<PartnerMasterT> partnerMasterTs) {
		this.partnerMasterTs = partnerMasterTs;
	}

	public PartnerMasterT addPartnerMasterT(PartnerMasterT partnerMasterT) {
		getPartnerMasterTs().add(partnerMasterT);
		partnerMasterT.setGeographyMappingT(this);

		return partnerMasterT;
	}

	public PartnerMasterT removePartnerMasterT(PartnerMasterT partnerMasterT) {
		getPartnerMasterTs().remove(partnerMasterT);
		partnerMasterT.setGeographyMappingT(null);

		return partnerMasterT;
	}

	public List<RevenueCustomerMappingT> getRevenueCustomerMappingTs() {
		return this.revenueCustomerMappingTs;
	}

	public void setRevenueCustomerMappingTs(
			List<RevenueCustomerMappingT> revenueCustomerMappingTs) {
		this.revenueCustomerMappingTs = revenueCustomerMappingTs;
	}

	public RevenueCustomerMappingT addRevenueCustomerMappingT(
			RevenueCustomerMappingT revenueCustomerMappingT) {
		getRevenueCustomerMappingTs().add(revenueCustomerMappingT);
		revenueCustomerMappingT.setGeographyMappingT(this);

		return revenueCustomerMappingT;
	}

	public RevenueCustomerMappingT removeRevenueCustomerMappingT(
			RevenueCustomerMappingT revenueCustomerMappingT) {
		getRevenueCustomerMappingTs().remove(revenueCustomerMappingT);
		revenueCustomerMappingT.setGeographyMappingT(null);

		return revenueCustomerMappingT;
	}
	
	public List<GeographyCountryMappingT> getGeographyCountryMappingTs() {
		return this.geographyCountryMappingTs;
	}

	public void setGeographyCountryMappingTs(List<GeographyCountryMappingT> geographyCountryMappingTs) {
		this.geographyCountryMappingTs = geographyCountryMappingTs;
	}

	public GeographyCountryMappingT addGeographyCountryMappingT(GeographyCountryMappingT geographyCountryMappingT) {
		getGeographyCountryMappingTs().add(geographyCountryMappingT);
		geographyCountryMappingT.setGeographyMappingT(this);

		return geographyCountryMappingT;
	}

	public GeographyCountryMappingT removeGeographyCountryMappingT(GeographyCountryMappingT geographyCountryMappingT) {
		getGeographyCountryMappingTs().remove(geographyCountryMappingT);
		geographyCountryMappingT.setGeographyMappingT(null);

		return geographyCountryMappingT;
	}
	
}