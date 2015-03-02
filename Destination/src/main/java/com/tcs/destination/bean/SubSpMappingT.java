package com.tcs.destination.bean;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the sub_sp_mapping_t database table.
 * 
 */
@Entity
@Table(name="sub_sp_mapping_t")
@NamedQuery(name="SubSpMappingT.findAll", query="SELECT s FROM SubSpMappingT s")
public class SubSpMappingT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="sub_sp")
	private String subSp;

	private String active;

	@Column(name="service_line")
	private String serviceLine;

	@Column(name="sp_code")
	private Integer spCode;

	@Column(name="sub_sp_id")
	private Integer subSpId;

	//bi-directional many-to-one association to ConnectT
	@OneToMany(mappedBy="subSpMappingT")
	private List<ConnectT> connectTs;

	//bi-directional many-to-one association to OpportunityT
	@OneToMany(mappedBy="subSpMappingT")
	private List<OpportunityT> opportunityTs;

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

	public String getServiceLine() {
		return this.serviceLine;
	}

	public void setServiceLine(String serviceLine) {
		this.serviceLine = serviceLine;
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

	public List<ConnectT> getConnectTs() {
		return this.connectTs;
	}

	public void setConnectTs(List<ConnectT> connectTs) {
		this.connectTs = connectTs;
	}

	public ConnectT addConnectT(ConnectT connectT) {
		getConnectTs().add(connectT);
		connectT.setSubSpMappingT(this);

		return connectT;
	}

	public ConnectT removeConnectT(ConnectT connectT) {
		getConnectTs().remove(connectT);
		connectT.setSubSpMappingT(null);

		return connectT;
	}

	public List<OpportunityT> getOpportunityTs() {
		return this.opportunityTs;
	}

	public void setOpportunityTs(List<OpportunityT> opportunityTs) {
		this.opportunityTs = opportunityTs;
	}

	public OpportunityT addOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().add(opportunityT);
		opportunityT.setSubSpMappingT(this);

		return opportunityT;
	}

	public OpportunityT removeOpportunityT(OpportunityT opportunityT) {
		getOpportunityTs().remove(opportunityT);
		opportunityT.setSubSpMappingT(null);

		return opportunityT;
	}

}