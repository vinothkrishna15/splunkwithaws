package com.tcs.destination.bean.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The IouCustomerMappingT.
 * 
 */
@JsonInclude(Include.NON_NULL)
public class IouCustomerMappingDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String iou;
	private String displayIou;
	private boolean active;

	public IouCustomerMappingDTO() {
		super();
	}

	public String getIou() {
		return iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public String getDisplayIou() {
		return displayIou;
	}

	public void setDisplayIou(String displayIou) {
		this.displayIou = displayIou;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}