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
public class WinRatioWrapperDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<GeoWinRatioDTO> geoWinRatios;
	private List<WinLossCountDTO> winLossCounts;
	private List<CustomerWinRatioDTO> customerWinRatios;

	public WinRatioWrapperDTO() {
		super();
	}

	public List<GeoWinRatioDTO> getGeoWinRatios() {
		return geoWinRatios;
	}

	public void setGeoWinRatios(List<GeoWinRatioDTO> geoWinRatios) {
		this.geoWinRatios = geoWinRatios;
	}

	public List<WinLossCountDTO> getWinLossCounts() {
		return winLossCounts;
	}

	public void setWinLossCounts(List<WinLossCountDTO> winLossCounts) {
		this.winLossCounts = winLossCounts;
	}

	public List<CustomerWinRatioDTO> getCustomerWinRatios() {
		return customerWinRatios;
	}

	public void setCustomerWinRatios(List<CustomerWinRatioDTO> customerWinRatios) {
		this.customerWinRatios = customerWinRatios;
	}

}
