package com.tcs.destination.bean;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

import java.util.List;


/**
 * The persistent class for the group_customer_t database table.
 * 
 */
@Entity
@Table(name="group_customer_t")
@NamedQuery(name="GroupCustomerT.findAll", query="SELECT g FROM GroupCustomerT g")
public class GroupCustomerT implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="group_customer_name")
	private String groupCustomerName;

	private byte[] logo;

	//bi-directional many-to-one association to CustomerMasterT
	@OneToMany(mappedBy="groupCustomerT")
	private List<CustomerMasterT> customerMasterTs;
	
	@Transient
	private int totalConnects;
	
	@Transient
	private int cxoConnects;
	
	@Transient
	private int otherConnects;
	
	@Transient
	private int associates;
	
	@Transient
	private int associatesDE;
	
	@Transient
	private int associatesNonDE;
	
	@Transient
	private BigDecimal totalRevenue;
	
	@Transient
	private BigDecimal consultingRevenue;
	
	@Transient
	private BigDecimal grossMargin;
	
	@Transient
	private BigDecimal cost;
	
	@Transient
	private int opportunities;
	
	@Transient
	private int prospectingOpportunities;
	
	@Transient
	private int pipelineOpportunities;
	
	@Transient
	private BigDecimal winRatio;
	
	@Transient
	private int totalWins;
	
	@Transient
	private int totalLoss;
	
	@Transient
	private BigDecimal winValue;
	
	@Transient
	private BigDecimal lossValue;
	
	public GroupCustomerT() {
	}

	public String getGroupCustomerName() {
		return this.groupCustomerName;
	}

	public void setGroupCustomerName(String groupCustomerName) {
		this.groupCustomerName = groupCustomerName;
	}

	public byte[] getLogo() {
		return this.logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public List<CustomerMasterT> getCustomerMasterTs() {
		return this.customerMasterTs;
	}

	public void setCustomerMasterTs(List<CustomerMasterT> customerMasterTs) {
		this.customerMasterTs = customerMasterTs;
	}

	public CustomerMasterT addCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().add(customerMasterT);
		customerMasterT.setGroupCustomerT(this);

		return customerMasterT;
	}

	public CustomerMasterT removeCustomerMasterT(CustomerMasterT customerMasterT) {
		getCustomerMasterTs().remove(customerMasterT);
		customerMasterT.setGroupCustomerT(null);

		return customerMasterT;
	}

	public int getTotalConnects() {
		return totalConnects;
	}

	public void setTotalConnects(int totalConnects) {
		this.totalConnects = totalConnects;
	}

	public int getCxoConnects() {
		return cxoConnects;
	}

	public void setCxoConnects(int cxoConnects) {
		this.cxoConnects = cxoConnects;
	}

	public int getOtherConnects() {
		return otherConnects;
	}

	public void setOtherConnects(int otherConnects) {
		this.otherConnects = otherConnects;
	}

	public int getAssociates() {
		return associates;
	}

	public void setAssociates(int associates) {
		this.associates = associates;
	}

	public int getAssociatesDE() {
		return associatesDE;
	}

	public void setAssociatesDE(int associatesDE) {
		this.associatesDE = associatesDE;
	}

	public int getAssociatesNonDE() {
		return associatesNonDE;
	}

	public void setAssociatesNonDE(int associatesNonDE) {
		this.associatesNonDE = associatesNonDE;
	}

	public BigDecimal getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(BigDecimal totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	public BigDecimal getConsultingRevenue() {
		return consultingRevenue;
	}

	public void setConsultingRevenue(BigDecimal consultingRevenue) {
		this.consultingRevenue = consultingRevenue;
	}

	public BigDecimal getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(BigDecimal grossMargin) {
		this.grossMargin = grossMargin;
	}

	public int getOpportunities() {
		return opportunities;
	}

	public void setOpportunities(int opportunities) {
		this.opportunities = opportunities;
	}

	public int getProspectingOpportunities() {
		return prospectingOpportunities;
	}

	public void setProspectingOpportunities(int prospectingOpportunities) {
		this.prospectingOpportunities = prospectingOpportunities;
	}

	public int getPipelineOpportunities() {
		return pipelineOpportunities;
	}

	public void setPipelineOpportunities(int pipelineOpportunities) {
		this.pipelineOpportunities = pipelineOpportunities;
	}

	public BigDecimal getWinRatio() {
		return winRatio;
	}

	public void setWinRatio(BigDecimal winRatio) {
		this.winRatio = winRatio;
	}

	public int getTotalWins() {
		return totalWins;
	}

	public void setTotalWins(int totalWins) {
		this.totalWins = totalWins;
	}

	public int getTotalLoss() {
		return totalLoss;
	}

	public void setTotalLoss(int totalLoss) {
		this.totalLoss = totalLoss;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getWinValue() {
		return winValue;
	}

	public void setWinValue(BigDecimal winValue) {
		this.winValue = winValue;
	}

	public BigDecimal getLossValue() {
		return lossValue;
	}

	public void setLossValue(BigDecimal lossValue) {
		this.lossValue = lossValue;
	}
	
}