package com.tcs.destination.bean;

import com.tcs.destination.config.ReportDataItem;

/**
 * Bean used for getting Opportunity Loss to be processed in generating Weekly report
 * @author TCS
 *
 */
public class OpportunityLoss {

	@ReportDataItem(columnName = "Opportunity ID", width = 70)
	private String opportunityId;
	
	@ReportDataItem(columnName = "CRM ID",width = 50)
	private String crmId;
	
	@ReportDataItem(columnName = "Customer Name")
	private String customerName;
	
	@ReportDataItem(columnName = "Opportunity Name")
	private String opportunityName;
	
	@ReportDataItem(columnName = "Sub SP",width = 60)
	private String subSp;
	
	@ReportDataItem(columnName = "IOU",width = 40)
	private String iou;
	
	@ReportDataItem(columnName = "Deal Value\n(USD)",width = 60)
	private String dealValue;
	
	@ReportDataItem(columnName = "Loss Factors")
	private String lossFactors;
	
	@ReportDataItem(columnName = "Competitors")
	private String competitors;
	
	@ReportDataItem(columnName = "Owner")
	private String owner;

	public String getOpportunityId() {
		return opportunityId;
	}

	public void setOpportunityId(String opportunityId) {
		this.opportunityId = opportunityId;
	}

	public String getCrmId() {
		return crmId;
	}

	public void setCrmId(String crmId) {
		this.crmId = crmId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getOpportunityName() {
		return opportunityName;
	}

	public void setOpportunityName(String opportunityName) {
		this.opportunityName = opportunityName;
	}

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public String getIou() {
		return iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public String getDealValue() {
		return dealValue;
	}

	public void setDealValue(String dealValue) {
		this.dealValue = dealValue;
	}

	public String getLossFactors() {
		return lossFactors;
	}

	public void setLossFactors(String lossFactors) {
		this.lossFactors = lossFactors;
	}

	public String getCompetitors() {
		return competitors;
	}

	public void setCompetitors(String competitors) {
		this.competitors = competitors;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
	
}
