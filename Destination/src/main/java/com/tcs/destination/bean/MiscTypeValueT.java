package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the misc_type_value_t database table.
 * 
 */
@Entity
@Table(name="misc_type_value_t")
@NamedQuery(name="MiscTypeValueT.findAll", query="SELECT m FROM MiscTypeValueT m")
public class MiscTypeValueT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "type", insertable = false, updatable = false)
	private String type;

	@Column(name = "value", insertable = false, updatable = false)
	private String value;
	
	@EmbeddedId
	private MiscTypeValueTPK id;

	public MiscTypeValueT() {
	}

	public MiscTypeValueTPK getId() {
		return this.id;
	}

	public void setId(MiscTypeValueTPK id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}