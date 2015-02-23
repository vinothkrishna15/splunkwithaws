package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the frequently_searched_customer_partner_t database table.
 * 
 */
@Entity
@Table(name="frequently_searched_customer_partner_t")
@NamedQuery(name="FrequentlySearchedCustomerPartnerT.findAll", query="SELECT f FROM FrequentlySearchedCustomerPartnerT f")
public class FrequentlySearchedCustomerPartnerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="frequently_searched_id")
	private String frequentlySearchedId;

	@Column(name="entity_type")
	private String entityType;

	@Column(name="search_datetime")
	private Timestamp searchDatetime;

	@Column(name="search_record")
	private String searchRecord;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="user_id")
	private UserT userT;

	public FrequentlySearchedCustomerPartnerT() {
	}

	public String getFrequentlySearchedId() {
		return this.frequentlySearchedId;
	}

	public void setFrequentlySearchedId(String frequentlySearchedId) {
		this.frequentlySearchedId = frequentlySearchedId;
	}

	public String getEntityType() {
		return this.entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Timestamp getSearchDatetime() {
		return this.searchDatetime;
	}

	public void setSearchDatetime(Timestamp searchDatetime) {
		this.searchDatetime = searchDatetime;
	}

	public String getSearchRecord() {
		return this.searchRecord;
	}

	public void setSearchRecord(String searchRecord) {
		this.searchRecord = searchRecord;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}