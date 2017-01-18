package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The WinRatioDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class GeoWinRatioDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String geoName;
	private List<MoneyBucketDTO> buckets;

	public GeoWinRatioDTO() {
		super();
	}

	public String getGeoName() {
		return geoName;
	}

	public void setGeoName(String geoName) {
		this.geoName = geoName;
	}

	public List<MoneyBucketDTO> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<MoneyBucketDTO> buckets) {
		this.buckets = buckets;
	}
	
}
