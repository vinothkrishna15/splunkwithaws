package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the beacon_data_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="beaconDataId")
@Entity
@Table(name="beacon_data_t")
@NamedQuery(name="BeaconDataT.findAll", query="SELECT b FROM BeaconDataT b")
public class BeaconDataT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="beacon_data_id")
	private String beaconDataId;

	@Column(name="beacon_group_client")
	private String beaconGroupClient;

	@Column(name="financial_year")
	private String financialYear;

	private String quarter;

	private BigDecimal target;

	//bi-directional many-to-one association to BeaconCustomerMappingT
	@ManyToOne
	@JoinColumn(name="beacon_customer_map_id")
	private BeaconCustomerMappingT beaconCustomerMappingT;
	
	 @Column(name="beacon_customer_map_id")
	 private Long beaconCustomerMapId;

	public Long getBeaconCustomerMapId() {
		return beaconCustomerMapId;
	}

	public void setBeaconCustomerMapId(Long beaconCustomerMapId) {
		this.beaconCustomerMapId = beaconCustomerMapId;
	}

	public BeaconDataT() {
	}

	public String getBeaconDataId() {
		return this.beaconDataId;
	}

	public void setBeaconDataId(String beaconDataId) {
		this.beaconDataId = beaconDataId;
	}

	public String getBeaconGroupClient() {
		return this.beaconGroupClient;
	}

	public void setBeaconGroupClient(String beaconGroupClient) {
		this.beaconGroupClient = beaconGroupClient;
	}

	public String getFinancialYear() {
		return this.financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getQuarter() {
		return this.quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public BigDecimal getTarget() {
		return this.target;
	}

	public void setTarget(BigDecimal target) {
		this.target = target;
	}

	public BeaconCustomerMappingT getBeaconCustomerMappingT() {
		return this.beaconCustomerMappingT;
	}

	public void setBeaconCustomerMappingT(BeaconCustomerMappingT beaconCustomerMappingT) {
		this.beaconCustomerMappingT = beaconCustomerMappingT;
	}

}
