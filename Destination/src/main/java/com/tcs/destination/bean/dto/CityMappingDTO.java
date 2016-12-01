package com.tcs.destination.bean.dto;

import java.io.Serializable;


/**
 * The CityMappingDTO.
 * 
 */
public class CityMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String city;
	private String latitude;
	private String longitude;

	public CityMappingDTO() {
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}