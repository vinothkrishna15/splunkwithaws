package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the service_practice_t database table.
 * 
 */
@Entity
@Table(name="service_practice_t")
@NamedQuery(name="ServicePracticeT.findAll", query="SELECT s FROM ServicePracticeT s")
public class ServicePracticeT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sp_id")
	private Integer spId;

	private String sp;

	@Column(name="sp_desc")
	private String spDesc;

	public ServicePracticeT() {
	}

	public Integer getSpId() {
		return this.spId;
	}

	public void setSpId(Integer spId) {
		this.spId = spId;
	}

	public String getSp() {
		return this.sp;
	}

	public void setSp(String sp) {
		this.sp = sp;
	}

	public String getSpDesc() {
		return this.spDesc;
	}

	public void setSpDesc(String spDesc) {
		this.spDesc = spDesc;
	}

}