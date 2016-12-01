package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The GeographyMappingDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class GeographyMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String geography;
	private boolean active;
	private String displayGeography;
	
	private List<GeographyCountryMappingDTO> geographyCountryMappingTs;

	public GeographyMappingDTO() {
		super();
	}

	public String getGeography() {
		return geography;
	}

	public void setGeography(String geography) {
		this.geography = geography;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDisplayGeography() {
		return displayGeography;
	}

	public void setDisplayGeography(String displayGeography) {
		this.displayGeography = displayGeography;
	}

	public List<GeographyCountryMappingDTO> getGeographyCountryMappingTs() {
		return geographyCountryMappingTs;
	}

	public void setGeographyCountryMappingTs(
			List<GeographyCountryMappingDTO> geographyCountryMappingTs) {
		this.geographyCountryMappingTs = geographyCountryMappingTs;
	}

}