package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * The persistent class for the win_loss_factor_mapping_t database table.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class WinLossFactorMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String winLossFactor;
	private String type;
	private boolean active;
	
	public WinLossFactorMappingDTO() {
		super();
	}
	
	public String getWinLossFactor() {
		return winLossFactor;
	}
	public void setWinLossFactor(String winLossFactor) {
		this.winLossFactor = winLossFactor;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	

	
}