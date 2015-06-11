package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the application_settings_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="application_settings_t")
@NamedQuery(name="ApplicationSettingsT.findAll", query="SELECT a FROM ApplicationSettingsT a")
public class ApplicationSettingsT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String key;

	private String value;

	public ApplicationSettingsT() {
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}