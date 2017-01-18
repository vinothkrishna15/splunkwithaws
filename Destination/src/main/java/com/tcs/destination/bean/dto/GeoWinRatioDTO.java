package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The WinRatioDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class WinRatioDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String geoName;
	private Map<String, BigDecimal> ratio;

	public WinRatioDTO() {
		super();
	}

	public String getGeoName() {
		return geoName;
	}

	public void setGeoName(String geoName) {
		this.geoName = geoName;
	}

	public Map<String, BigDecimal> getRatio() {
		return ratio;
	}

	public void setRatio(Map<String, BigDecimal> ratio) {
		this.ratio = ratio;
	}
	
	

}
