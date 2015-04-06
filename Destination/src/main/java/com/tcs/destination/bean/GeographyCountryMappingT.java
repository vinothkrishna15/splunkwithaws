package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

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
 * The persistent class for the geography_country_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="geography_country_mapping_t")
@NamedQuery(name="GeographyCountryMappingT.findAll", query="SELECT g FROM GeographyCountryMappingT g")
public class GeographyCountryMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private String country;

	private String geography;

	//bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy="geographyCountryMappingT")
	private List<ConnectT> connectTs;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="geographyCountryMappingT")
	private List<OpportunityT> opportunityTs;

	public GeographyCountryMappingT() {
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getGeography() {
		return this.geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setGeographyCountryMappingT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setGeographyCountryMappingT(null);

		return connectT;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setGeographyCountryMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setGeographyCountryMappingT(null);

		return opportunityT;
	}

}