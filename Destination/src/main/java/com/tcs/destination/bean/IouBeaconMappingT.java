package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the iou_beacon_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="iou_beacon_mapping_t")
@NamedQuery(name="IouBeaconMappingT.findAll", query="SELECT i FROM IouBeaconMappingT i")
public class IouBeaconMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="beacon_iou")
	private String beaconIou;

	@Column(name="display_iou")
	private String displayIou;

	//bi-directional many-to-one association to BeaconDataT
	@OneToMany(mappedBy="iouBeaconMappingT")
	private List<BeaconDataT> beaconDataTs;

	public IouBeaconMappingT() {
	}

	public String getBeaconIou() {
		return this.beaconIou;
	}

	public void setBeaconIou(String beaconIou) {
		this.beaconIou = beaconIou;
	}

	public String getDisplayIou() {
		return this.displayIou;
	}

	public void setDisplayIou(String displayIou) {
		this.displayIou = displayIou;
	}

	public List<BeaconDataT> getBeaconDataTs() {
		return this.beaconDataTs;
	}

	public void setBeaconDataTs(List<BeaconDataT> beaconDataTs) {
		this.beaconDataTs = beaconDataTs;
	}

	public BeaconDataT addBeaconDataT(BeaconDataT beaconDataT) {
		getBeaconDataTs().add(beaconDataT);
		beaconDataT.setIouBeaconMappingT(this);

		return beaconDataT;
	}

	public BeaconDataT removeBeaconDataT(BeaconDataT beaconDataT) {
		getBeaconDataTs().remove(beaconDataT);
		beaconDataT.setIouBeaconMappingT(null);

		return beaconDataT;
	}

}