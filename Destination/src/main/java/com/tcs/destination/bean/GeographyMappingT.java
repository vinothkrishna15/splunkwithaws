package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the geography_mapping_t database table.
 * 
 */
@Entity
@Table(name="geography_mapping_t")
@NamedQuery(name="GeographyMappingT.findAll", query="SELECT g FROM GeographyMappingT g")
public class GeographyMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String geography;

	//bi-directional many-to-one association to CustomerMasterT
	@JsonIgnore
	@OneToMany(mappedBy="geographyMappingT")
	private List<CustomerMasterT> customerMasterTs;

	//bi-directional many-to-one association to PartnerMasterT
	@JsonIgnore
	@OneToMany(mappedBy="geographyMappingT")
	private List<PartnerMasterT> partnerMasterTs;

	public GeographyMappingT() {
	}

	public String getGeography() {
		return this.geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
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

}