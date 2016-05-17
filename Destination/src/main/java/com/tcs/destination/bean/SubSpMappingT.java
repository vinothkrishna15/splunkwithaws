package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tcs.destination.utils.Constants;

/**
 * The persistent class for the sub_sp_mapping_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "subSp")
@Entity
@Table(name = "sub_sp_mapping_t")
@NamedQuery(name = "SubSpMappingT.findAll", query = "SELECT s FROM SubSpMappingT s")
public class SubSpMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "sub_sp")
	private String subSp;

	private boolean active;

	@Column(name = "display_sub_sp")
	private String displaySubSp;

	@Column(name = "sp_code")
	private Integer spCode;

	@Column(name = "sub_sp_id")
	private Integer subSpId;

	@Column(name = "actual_sub_sp")
	private String actualSubSp;

	// bi-directional many-to-one association to ActualRevenuesDataT
	@JsonIgnore
	@OneToMany(mappedBy = "subSpMappingT")
	private List<ActualRevenuesDataT> actualRevenuesDataTs;

	// bi-directional many-to-one association to ProjectedRevenuesDataT
	@JsonIgnore
	@OneToMany(mappedBy = "subSpMappingT")
	private List<ProjectedRevenuesDataT> projectedRevenuesDataTs;

	// bi-directional many-to-one association to ConnectSubSpLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "subSpMappingT")
	private List<ConnectSubSpLinkT> connectSubSpLinkTs;

	// bi-directional many-to-one association to OpportunitySubSpLinkT
	@JsonIgnore
	@OneToMany(mappedBy = "subSpMappingT")
	private List<OpportunitySubSpLinkT> opportunitySubSpLinkTs;

	public SubSpMappingT() {
	}

	public String getSubSp() {
		return this.subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
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

	public List<ActualRevenuesDataT> getActualRevenuesDataTs() {
		return this.actualRevenuesDataTs;
	}

	public void setActualRevenuesDataTs(
			List<ActualRevenuesDataT> actualRevenuesDataTs) {
		this.actualRevenuesDataTs = actualRevenuesDataTs;
	}

	public ActualRevenuesDataT addActualRevenuesDataT(
			ActualRevenuesDataT actualRevenuesDataT) {
		getActualRevenuesDataTs().add(actualRevenuesDataT);
		actualRevenuesDataT.setSubSpMappingT(this);

		return actualRevenuesDataT;
	}

	public ActualRevenuesDataT removeActualRevenuesDataT(
			ActualRevenuesDataT actualRevenuesDataT) {
		getActualRevenuesDataTs().remove(actualRevenuesDataT);
		actualRevenuesDataT.setSubSpMappingT(null);

		return actualRevenuesDataT;
	}

	public List<ConnectSubSpLinkT> getConnectSubSpLinkTs() {
		return this.connectSubSpLinkTs;
	}

	public void setConnectSubSpLinkTs(List<ConnectSubSpLinkT> connectSubSpLinkTs) {
		this.connectSubSpLinkTs = connectSubSpLinkTs;
	}

	public ConnectSubSpLinkT addConnectSubSpLinkT(
			ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().add(connectSubSpLinkT);
		connectSubSpLinkT.setSubSpMappingT(this);

		return connectSubSpLinkT;
	}

	public ConnectSubSpLinkT removeConnectSubSpLinkT(
			ConnectSubSpLinkT connectSubSpLinkT) {
		getConnectSubSpLinkTs().remove(connectSubSpLinkT);
		connectSubSpLinkT.setSubSpMappingT(null);

		return connectSubSpLinkT;
	}

	public List<OpportunitySubSpLinkT> getOpportunitySubSpLinkTs() {
		return this.opportunitySubSpLinkTs;
	}

	public void setOpportunitySubSpLinkTs(
			List<OpportunitySubSpLinkT> opportunitySubSpLinkTs) {
		this.opportunitySubSpLinkTs = opportunitySubSpLinkTs;
	}

	public OpportunitySubSpLinkT addOpportunitySubSpLinkT(
			OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().add(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setSubSpMappingT(this);

		return opportunitySubSpLinkT;
	}

	public OpportunitySubSpLinkT removeOpportunitySubSpLinkT(
			OpportunitySubSpLinkT opportunitySubSpLinkT) {
		getOpportunitySubSpLinkTs().remove(opportunitySubSpLinkT);
		opportunitySubSpLinkT.setSubSpMappingT(null);

		return opportunitySubSpLinkT;
	}

	public List<ProjectedRevenuesDataT> getProjectedRevenuesDataTs() {
		return this.projectedRevenuesDataTs;
	}

	public void setProjectedRevenuesDataTs(
			List<ProjectedRevenuesDataT> projectedRevenuesDataTs) {
		this.projectedRevenuesDataTs = projectedRevenuesDataTs;
	}

	public ProjectedRevenuesDataT addProjectedRevenuesDataT(ProjectedRevenuesDataT projectedRevenuesDataT) {
		getProjectedRevenuesDataTs().add(projectedRevenuesDataT);
		projectedRevenuesDataT.setSubSpMappingT(this);

		return projectedRevenuesDataT;
	}

	public ProjectedRevenuesDataT removeProjectedRevenuesDataT(ProjectedRevenuesDataT projectedRevenuesDataT) {
		getProjectedRevenuesDataTs().remove(projectedRevenuesDataT);
		projectedRevenuesDataT.setSubSpMappingT(null);

		return projectedRevenuesDataT;
	}

	public String getActualSubSp() {
		return actualSubSp;
	}
	public void setActualSubSp(String actualSubSp) {
		this.actualSubSp = actualSubSp;
	}

}