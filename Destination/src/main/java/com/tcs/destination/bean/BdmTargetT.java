package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


/**
 * The persistent class for the bdm_target_t database table.
 * 
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="bdmUserId")
@Entity
@Table(name="bdm_target_t")
@NamedQuery(name="BdmTargetT.findAll", query="SELECT b FROM BdmTargetT b")
public class BdmTargetT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="bdm_user_id")
	private String bdmUserId;

	private String currency;

	private String quarter;

	private Long target;

	private String year;

	public BdmTargetT() {
	}

	public String getBdmUserId() {
		return this.bdmUserId;
	}

	public void setBdmUserId(String bdmUserId) {
		this.bdmUserId = bdmUserId;
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

}