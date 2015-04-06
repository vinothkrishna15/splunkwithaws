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
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the geography_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
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

	//bi-directional many-to-one association to CustomerMasterT
	@OneToMany(mappedBy="geographyMappingT")
	private List<CustomerMasterT> customerMasterTs;

	//bi-directional many-to-one association to PartnerMasterT
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