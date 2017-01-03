package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The SubSpMappingDTO.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class SubSpMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subSp;
	private Boolean active;
	private String displaySubSp;
	private Integer spCode;
	private Integer subSpId;
	private String actualSubSp;

	public SubSpMappingDTO() {
		super();
	}

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getDisplaySubSp() {
		return displaySubSp;
	}

	public void setDisplaySubSp(String displaySubSp) {
		this.displaySubSp = displaySubSp;
	}

	public Integer getSpCode() {
		return spCode;
	}

	public void setSpCode(Integer spCode) {
		this.spCode = spCode;
	}

	public Integer getSubSpId() {
		return subSpId;
	}

	public void setSubSpId(Integer subSpId) {
		this.subSpId = subSpId;
	}

	public String getActualSubSp() {
		return actualSubSp;
	}

	public void setActualSubSp(String actualSubSp) {
		this.actualSubSp = actualSubSp;
	}



}