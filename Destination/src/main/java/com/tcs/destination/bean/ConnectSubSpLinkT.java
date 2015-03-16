package com.tcs.destination.bean;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

import java.sql.Timestamp;


/**
 * The persistent class for the connect_sub_sp_link_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="connectSubSpLinkId")
@Entity
@Table(name="connect_sub_sp_link_t")
@NamedQuery(name="ConnectSubSpLinkT.findAll", query="SELECT c FROM ConnectSubSpLinkT c")
public class ConnectSubSpLinkT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="connect_sub_sp_link_id")
	private String connectSubSpLinkId;

	@Column(name="created_modified_by")
	private String createdModifiedBy;

	@Column(name="created_modified_datetime")
	private Timestamp createdModifiedDatetime;

	@Column(name="sub_sp")
	private String subSp;

	//bi-directional many-to-one association to ConnectT
	@ManyToOne
	@JoinColumn(name="connect_id")
	private ConnectT connectT;

	public ConnectSubSpLinkT() {
	}

	public String getConnectSubSpLinkId() {
		return this.connectSubSpLinkId;
	}

	public void setConnectSubSpLinkId(String connectSubSpLinkId) {
		this.connectSubSpLinkId = connectSubSpLinkId;
	}

	public String getCreatedModifiedBy() {
		return this.createdModifiedBy;
	}

	public void setCreatedModifiedBy(String createdModifiedBy) {
		this.createdModifiedBy = createdModifiedBy;
	}

	public Timestamp getCreatedModifiedDatetime() {
		return this.createdModifiedDatetime;
	}

	public void setCreatedModifiedDatetime(Timestamp createdModifiedDatetime) {
		this.createdModifiedDatetime = createdModifiedDatetime;
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public ConnectT getConnectT() {
		return this.connectT;
	}

	public void setConnectT(ConnectT connectT) {
		this.connectT = connectT;
	}

}