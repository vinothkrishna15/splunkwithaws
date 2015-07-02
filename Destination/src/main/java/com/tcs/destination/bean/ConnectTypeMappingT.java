package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.util.List;

/**
 * The persistent class for the connect_type_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "connect_type_mapping_t")
@NamedQuery(name = "ConnectTypeMappingT.findAll", query = "SELECT c FROM ConnectTypeMappingT c")
public class ConnectTypeMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String type;
	
	@Column(name="connect_with")
	private String connectWith;

	private String description;

	// bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy = "connectTypeMappingT")
	private List<ConnectT> connectTs;

	public ConnectTypeMappingT() {
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setConnectTypeMappingT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setConnectTypeMappingT(null);

		return connectT;
	}

	public String getConnectWith() {
		return connectWith;
	}

	public void setConnectWith(String connectWith) {
		this.connectWith = connectWith;
	}
	
	

}