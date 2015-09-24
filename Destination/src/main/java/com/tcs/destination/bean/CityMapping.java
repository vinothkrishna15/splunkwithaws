package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;


/**
 * The persistent class for the city_mapping database table.
 * 
 */
@Entity
@Table(name="city_mapping")
@NamedQuery(name="CityMapping.findAll", query="SELECT c FROM CityMapping c")
public class CityMapping implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String city;

	private String latitude;

	private String longitude;

	//bi-directional many-to-one association to ConnectT
	@JsonIgnore
	@OneToMany(mappedBy="cityMapping")
	private List<ConnectT> connectTs;

	public CityMapping() {
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setCityMapping(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setCityMapping(null);

		return connectT;
	}

}