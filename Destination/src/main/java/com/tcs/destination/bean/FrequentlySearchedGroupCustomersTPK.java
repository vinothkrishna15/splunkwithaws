package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the frequently_searched_group_customers_t database table.
 * 
 */
@Embeddable
public class FrequentlySearchedGroupCustomersTPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="user_id", insertable=false, updatable=false)
	private String userId;

	@Column(name="group_customer_name")
	private String groupCustomerName;

	public FrequentlySearchedGroupCustomersTPK() {
	}
	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGroupCustomerName() {
		return this.groupCustomerName;
	}
	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof FrequentlySearchedGroupCustomersTPK)) {
			return false;
		}
		FrequentlySearchedGroupCustomersTPK castOther = (FrequentlySearchedGroupCustomersTPK)other;
		return 
			this.userId.equals(castOther.userId)
			&& this.groupCustomerName.equals(castOther.groupCustomerName);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.userId.hashCode();
		hash = hash * prime + this.groupCustomerName.hashCode();
		
		return hash;
	}
}