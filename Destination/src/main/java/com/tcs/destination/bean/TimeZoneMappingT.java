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
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the time_zone_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="timeZoneCode")
@Entity
@Table(name="time_zone_mapping_t")
@NamedQuery(name="TimeZoneMappingT.findAll", query="SELECT t FROM TimeZoneMappingT t")
public class TimeZoneMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="time_zone_code")
	private String timeZoneCode;

	private String description;

	//bi-directional many-to-one association to UserSettingsT
	@OneToMany(mappedBy="timeZoneMappingT")
	private List<UserSettingsT> userSettingsTs;

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

	public List<UserSettingsT> getUserSettingsTs() {
		return this.userSettingsTs;
	}

	public void setUserSettingsTs(List<UserSettingsT> userSettingsTs) {
		this.userSettingsTs = userSettingsTs;
	}

	public UserSettingsT addUserSettingsT(UserSettingsT userSettingsT) {
		getUserSettingsTs().add(userSettingsT);
		userSettingsT.setTimeZoneMappingT(this);

		return userSettingsT;
	}

	public UserSettingsT removeUserSettingsT(UserSettingsT userSettingsT) {
		getUserSettingsTs().remove(userSettingsT);
		userSettingsT.setTimeZoneMappingT(null);

		return userSettingsT;
	}

}