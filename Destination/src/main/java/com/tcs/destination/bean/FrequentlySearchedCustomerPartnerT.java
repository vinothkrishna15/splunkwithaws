package com.tcs.destination.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the frequently_searched_customer_partner_t database
 * table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "frequentlySearchedId")
@Entity
@Table(name = "frequently_searched_customer_partner_t")
@NamedQuery(name = "FrequentlySearchedCustomerPartnerT.findAll", query = "SELECT f FROM FrequentlySearchedCustomerPartnerT f")
public class FrequentlySearchedCustomerPartnerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "frequently_searched_id")
	private String frequentlySearchedId;

	@Column(name = "entity_id")
	private String entityId;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "search_datetime")
	private Timestamp searchDatetime;

	@Column(name = "user_id")
	private String userId;

	// bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private UserT userT;

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

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}