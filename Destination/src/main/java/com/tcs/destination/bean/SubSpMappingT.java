package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the sub_sp_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subSp")
@Entity
@Table(name = "sub_sp_mapping_t")
@NamedQuery(name = "SubSpMappingT.findAll", query = "SELECT s FROM SubSpMappingT s")
public class SubSpMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="sub_sp")
	private String subSp;

	private String active;

	@Column(name="display_sub_sp")
	private String displaySubSp;

	@Column(name="sp_code")
	private Integer spCode;

	@Column(name="sub_sp_id")
	private Integer subSpId;

	//bi-directional many-to-one association to ConnectSubSpLinkT
	@OneToMany(mappedBy="subSpMappingT")
	private List<ConnectSubSpLinkT> connectSubSpLinkTs;

	//bi-directional many-to-one association to OpportunitySubSpLinkT
	@OneToMany(mappedBy="subSpMappingT")
	private List<OpportunitySubSpLinkT> opportunitySubSpLinkTs;

	public SubSpMappingT() {
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public String getActive() {
		return this.active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getDisplaySubSp() {
		return this.displaySubSp;
	}

	public void setDisplaySubSp(String displaySubSp) {
		this.displaySubSp = displaySubSp;
	}

	public Integer getSpCode() {
		return this.spCode;
	}

	public void setSpCode(Integer spCode) {
		this.spCode = spCode;
	}

	public Integer getSubSpId() {
		return this.subSpId;
	}

	public void setSubSpId(Integer subSpId) {
		this.subSpId = subSpId;
	}

	public List<ConnectSubSpLinkT> getConnectSubSpLinkTs() {
		return this.connectSubSpLinkTs;
	}

	public void setConnectSubSpLinkTs(List<ConnectSubSpLinkT> connectSubSpLinkTs) {
		this.connectSubSpLinkTs = connectSubSpLinkTs;
	}

	public ConnectSubSpLinkT addConnectSubSpLinkT(ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().add(connectSubSpLinkT);
		connectSubSpLinkT.setSubSpMappingT(this);

		return connectSubSpLinkT;
	}

	public ConnectSubSpLinkT removeConnectSubSpLinkT(ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().remove(connectSubSpLinkT);
		connectSubSpLinkT.setSubSpMappingT(null);

		return connectSubSpLinkT;
	}

	public List<OpportunitySubSpLinkT> getOpportunitySubSpLinkTs() {
		return this.opportunitySubSpLinkTs;
	}

	public void setOpportunitySubSpLinkTs(List<OpportunitySubSpLinkT> opportunitySubSpLinkTs) {
		this.opportunitySubSpLinkTs = opportunitySubSpLinkTs;
	}

	public OpportunitySubSpLinkT addOpportunitySubSpLinkT(OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().add(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setSubSpMappingT(this);

		return opportunitySubSpLinkT;
	}

	public OpportunitySubSpLinkT removeOpportunitySubSpLinkT(OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().remove(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setSubSpMappingT(null);

		return opportunitySubSpLinkT;
	}

}