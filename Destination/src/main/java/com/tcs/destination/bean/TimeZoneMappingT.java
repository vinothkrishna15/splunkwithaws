package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the time_zone_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "timeZoneCode")
@Entity
@Table(name = "time_zone_mapping_t")
@NamedQuery(name = "TimeZoneMappingT.findAll", query = "SELECT t FROM TimeZoneMappingT t")
public class TimeZoneMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "description")
	private String description;
	
	@Column(name = "time_zone_code")
	private String timeZoneCode;

	@Column(name="time_zone_offset")
	private String timeZoneOffset;
	
	// bi-directional many-to-one association to UserGeneralSettingsT
	@JsonIgnore
	@OneToMany(mappedBy = "timeZoneMappingT")
	private List<UserGeneralSettingsT> userGeneralSettingsTs;

	// bi-directional many-to-one association to ConnectT
	@JsonIgnore
	@OneToMany(mappedBy = "timeZoneMappingT")
	private List<ConnectT> connectTs;

	public TimeZoneMappingT() {
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
	public List<UserGeneralSettingsT> getUserGeneralSettingsTs() {
		return this.userGeneralSettingsTs;
	}

	public void setUserGeneralSettingsTs(
			List<UserGeneralSettingsT> userGeneralSettingsTs) {
		this.userGeneralSettingsTs = userGeneralSettingsTs;
	}

	public UserGeneralSettingsT addUserGeneralSettingsT(
			UserGeneralSettingsT userGeneralSettingsT) {
		getUserGeneralSettingsTs().add(userGeneralSettingsT);
		userGeneralSettingsT.setTimeZoneMappingT(this);

		return userGeneralSettingsT;
	}

	public UserGeneralSettingsT removeUserGeneralSettingsT(
			UserGeneralSettingsT userGeneralSettingsT) {
		getUserGeneralSettingsTs().remove(userGeneralSettingsT);
		userGeneralSettingsT.setTimeZoneMappingT(null);

		return userGeneralSettingsT;
	}

	public List<ConnectT> getConnectTs() {
		return connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

}