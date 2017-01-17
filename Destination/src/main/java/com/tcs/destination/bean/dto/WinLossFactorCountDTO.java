package com.tcs.destination.bean.dto;

import java.io.Serializable;
import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The WinLossFactorCountDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class WinLossFactorCountDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String winLossFactor;
	private BigInteger count;


	public WinLossFactorCountDTO() {
		super();
	}

	public WinLossFactorCountDTO(String winLossFactor, BigInteger count) {
		super();
		this.winLossFactor = winLossFactor;
		this.count = count;
	}

	public String getWinLossFactor() {
		return winLossFactor;
	}


	public void setWinLossFactor(String winLossFactor) {
		this.winLossFactor = winLossFactor;
	}


	public BigInteger getCount() {
		return count;
	}


	public void setCount(BigInteger count) {
		this.count = count;
	}

}
