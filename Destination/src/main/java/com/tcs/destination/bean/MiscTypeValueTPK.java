package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the misc_type_value_t database table.
 * 
 */
@Embeddable
public class MiscTypeValueTPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	private String type;

	private String value;

	public MiscTypeValueTPK() {
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return this.value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MiscTypeValueTPK)) {
			return false;
		}
		MiscTypeValueTPK castOther = (MiscTypeValueTPK)other;
		return 
			this.type.equals(castOther.type)
			&& this.value.equals(castOther.value);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.type.hashCode();
		hash = hash * prime + this.value.hashCode();
		
		return hash;
	}
}