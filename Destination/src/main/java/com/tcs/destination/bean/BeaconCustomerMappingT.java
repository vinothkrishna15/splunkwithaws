package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the beacon_customer_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "beacon_customer_mapping_t")
@NamedQuery(name = "BeaconCustomerMappingT.findAll", query = "SELECT b FROM BeaconCustomerMappingT b")
public class BeaconCustomerMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="beacon_customer_map_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long beaconCustomerMapId;

	//bi-directional many-to-one association to CustomerMasterT
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "customer_id", 
		insertable = false, updatable = false)
	private CustomerMasterT customerMasterT;
	
	@Column(name = "customer_id")
	private String customerId;
	
	@Column(name = "beacon_customer_name")
	private String beaconCustomerName;
	
	@Column(name = "customer_geography")
	private String customerGeography;
	
	@Column(name = "beacon_iou")
	private String beaconIou;
	
	//bi-directional many-to-one association to GeographyMappingT
	@ManyToOne
	@JoinColumn(name="customer_geography", insertable = false, updatable = false)
	private GeographyMappingT geographyMappingT;

	//bi-directional many-to-one association to BeaconDataT
	@JsonIgnore
	@OneToMany(mappedBy="beaconCustomerMappingT")
	private List<BeaconDataT> beaconDataTs;

	//bi-directional many-to-one association to IouBeaconMappingT
	@ManyToOne
	@JoinColumn(name="beacon_iou", insertable = false, updatable = false)
	private IouBeaconMappingT iouBeaconMappingT;

	public Long getBeaconCustomerMapId() {
		return beaconCustomerMapId;
	}

	public void setBeaconCustomerMapId(Long beaconCustomerMapId) {
		this.beaconCustomerMapId = beaconCustomerMapId;
	}

	public BeaconCustomerMappingT() {
	}

	public CustomerMasterT getCustomerMasterT() {
		return this.customerMasterT;
	}

	public void setCustomerMasterT(CustomerMasterT customerMasterT) {
		this.customerMasterT = customerMasterT;
	}

	public GeographyMappingT getGeographyMappingT() {
		return this.geographyMappingT;
	}

	public void setGeographyMappingT(GeographyMappingT geographyMappingT) {
		this.geographyMappingT = geographyMappingT;
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

	public IouBeaconMappingT getIouBeaconMappingT() {
		return this.iouBeaconMappingT;
	}

	public void setIouBeaconMappingT(IouBeaconMappingT iouBeaconMappingT) {
		this.iouBeaconMappingT = iouBeaconMappingT;
	}

	public String getBeaconIou() {
		return beaconIou;
	}

	public void setBeaconIou(String beaconIou) {
		this.beaconIou = beaconIou;
	}

	public String getCustomerGeography() {
		return customerGeography;
	}

	public void setCustomerGeography(String customerGeography) {
		this.customerGeography = customerGeography;
	}

	public String getBeaconCustomerName() {
		return beaconCustomerName;
	}

	public void setBeaconCustomerName(String beaconCustomerName) {
		this.beaconCustomerName = beaconCustomerName;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

}
