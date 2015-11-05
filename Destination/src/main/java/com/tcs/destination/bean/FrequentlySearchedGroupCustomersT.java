package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;

/**
 * The persistent class for the frequently_searched_group_customers_t database
 * table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name = "frequently_searched_group_customers_t")
@NamedQuery(name = "FrequentlySearchedGroupCustomersT.findAll", query = "SELECT f FROM FrequentlySearchedGroupCustomersT f")
public class FrequentlySearchedGroupCustomersT implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private FrequentlySearchedGroupCustomersTPK freqSearchedGroupCustomer;

	@Column(name = "created_datetime")
	private Timestamp createdDateTime;

	public FrequentlySearchedGroupCustomersT() {
	}

	public FrequentlySearchedGroupCustomersTPK getFreqSearchedGroupCustomer() {
		return freqSearchedGroupCustomer;
	}


	public void setFreqSearchedGroupCustomer(
			FrequentlySearchedGroupCustomersTPK freqSearchedGroupCustomer) {
		this.freqSearchedGroupCustomer = freqSearchedGroupCustomer;
	}


	public Timestamp getCreatedDateTime() {
		return this.createdDateTime;
	}

	public void setCreatedDateTime(Timestamp createdDateTime) {
		this.createdDateTime = createdDateTime;
	}

}