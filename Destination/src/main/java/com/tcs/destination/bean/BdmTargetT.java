package com.tcs.destination.bean;

import java.io.Serializable;

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
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the bdm_target_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@Entity
@Table(name="bdm_target_t")
@NamedQuery(name="BdmTargetT.findAll", query="SELECT b FROM BdmTargetT b")
public class BdmTargetT implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="bdm_target_id")
	private String bdmTargetId;

	private String currency;

	private String quarter;

	private Long target;

	private String year;

	//bi-directional many-to-one association to UserT
	@ManyToOne
	@JoinColumn(name="bdm_user_id")
	private UserT userT;

	public BdmTargetT() {
	}

	public String getBdmTargetId() {
		return this.bdmTargetId;
	}

	public void setBdmTargetId(String bdmTargetId) {
		this.bdmTargetId = bdmTargetId;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getQuarter() {
		return this.quarter;
	}

	public void setQuarter(String quarter) {
		this.quarter = quarter;
	}

	public Long getTarget() {
		return this.target;
	}

	public void setTarget(Long target) {
		this.target = target;
	}

	public String getYear() {
		return this.year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public UserT getUserT() {
		return this.userT;
	}

	public void setUserT(UserT userT) {
		this.userT = userT;
	}

}