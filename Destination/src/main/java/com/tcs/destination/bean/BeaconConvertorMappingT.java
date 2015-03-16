package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the beacon_convertor_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="currencyName")
@Entity
@Table(name="beacon_convertor_mapping_t")
@NamedQuery(name="BeaconConvertorMappingT.findAll", query="SELECT b FROM BeaconConvertorMappingT b")
public class BeaconConvertorMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="currency_name")
	private String currencyName;

	@Column(name="conversion_rate")
	private BigDecimal conversionRate;

	@Temporal(TemporalType.DATE)
	@Column(name="date_updated")
	private Date dateUpdated;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="beaconConvertorMappingT")
	private List<OpportunityT> opportunityTs;

	public BeaconConvertorMappingT() {
	}

	public String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public BigDecimal getConversionRate() {
		return this.conversionRate;
	}

	public void setConversionRate(BigDecimal conversionRate) {
		this.conversionRate = conversionRate;
	}

	public Date getDateUpdated() {
		return this.dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setBeaconConvertorMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setBeaconConvertorMappingT(null);

		return opportunityT;
	}

}