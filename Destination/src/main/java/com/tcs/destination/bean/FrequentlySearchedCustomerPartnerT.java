package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.sql.Timestamp;


/**
 * The persistent class for the frequently_searched_customer_partner_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="frequentlySearchedId")
@Entity
@Table(name="frequently_searched_customer_partner_t")
@NamedQuery(name="FrequentlySearchedCustomerPartnerT.findAll", query="SELECT f FROM FrequentlySearchedCustomerPartnerT f")
public class FrequentlySearchedCustomerPartnerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="frequently_searched_id")
	private String frequentlySearchedId;

	@Column(name="entity_id")
	private String entityId;

	@Column(name="entity_type")
	private String entityType;

	@Column(name="search_datetime")
	private Timestamp searchDatetime;

	@Column(name="user_id")
	private String userId;

	public FrequentlySearchedCustomerPartnerT() {
	}

	public String getFrequentlySearchedId() {
		return this.frequentlySearchedId;
	}

	public void setFrequentlySearchedId(String frequentlySearchedId) {
		this.frequentlySearchedId = frequentlySearchedId;
	}

	public String getEntityId() {
		return this.entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
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

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}