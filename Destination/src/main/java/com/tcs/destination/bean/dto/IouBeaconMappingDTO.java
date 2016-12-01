package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the iou_beacon_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class IouBeaconMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String beaconIou;
	private String displayIou;
	private boolean active;

	public IouBeaconMappingDTO() {
		super();
	}

	public String getBeaconIou() {
		return beaconIou;
	}

	public void setBeaconIou(String beaconIou) {
		this.beaconIou = beaconIou;
	}

	public String getDisplayIou() {
		return displayIou;
	}

	public void setDisplayIou(String displayIou) {
		this.displayIou = displayIou;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}