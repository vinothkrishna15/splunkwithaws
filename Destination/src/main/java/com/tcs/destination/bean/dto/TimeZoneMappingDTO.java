package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

/**
 * The TimeZoneMappingDTO.
 * 
 */
@JsonFilter(Constants.FILTER)
public class TimeZoneMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String description;
	private String timeZoneCode;
	private String timeZoneOffset;

	public TimeZoneMappingDTO() {
		super();
	}

	public String getTimeZoneCode() {
		return this.timeZoneCode;
	}

	public void setTimeZoneCode(String timeZoneCode) {
		this.timeZoneCode = timeZoneCode;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTimeZoneOffset() {
		return this.timeZoneOffset;
	}

	public void setTimeZoneOffset(String timeZoneOffset) {
		this.timeZoneOffset = timeZoneOffset;
	}

}