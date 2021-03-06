package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The SalesStageMappingDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class SalesStageMappingDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Integer salesStageCode;
	private String salesStageDescription;

	public SalesStageMappingDTO() {
	}

	public Integer getSalesStageCode() {
		return this.salesStageCode;
	}

	public void setSalesStageCode(Integer salesStageCode) {
		this.salesStageCode = salesStageCode;
	}

	public String getSalesStageDescription() {
		return this.salesStageDescription;
	}

	public void setSalesStageDescription(String salesStageDescription) {
		this.salesStageDescription = salesStageDescription;
	}

}