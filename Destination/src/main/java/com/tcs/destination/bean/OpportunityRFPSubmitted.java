package com.tcs.destination.bean;

import com.tcs.destination.config.ReportDataItem;
/**
 * Bean used for getting RFP Submitted Opportunities to be processed in generating Weekly report
 * @author TCS
 *
 */
public class OpportunityRFPSubmitted {
 
	@ReportDataItem(columnName = "Opportunity ID", width = 80)
	private String opportunityId;
	
	@ReportDataItem(columnName = "CRM ID",width = 50)
	private String crmId;
	
	@ReportDataItem(columnName = "Customer Name")
	private String customerName;
	
	@ReportDataItem(columnName = "Opportunity Name")
	private String opportunityName;
	
	@ReportDataItem(columnName = "IOU",width = 40)
	private String iou;
	
	@ReportDataItem(columnName = "Opportunity Description")
	private String opportunityDescription;
	
	@ReportDataItem(columnName = "Sub SP",width = 60)
	private String subSp;
	
	@ReportDataItem(columnName = "Deal Value\n(USD)",width = 60)
	private String dealValue;
	
	@ReportDataItem(columnName = "Outcome Expected on",width = 70)
	private String outcomeExpectedDate;
	
	@ReportDataItem(columnName = "Competitors",width = 75)
	private String competitors;
	
	@ReportDataItem(columnName = "Owner",width = 50)
	private String owner;
	
	private Integer dealValueInt;

	public Integer getDealValueInt() {
		return dealValueInt;
	}
	
	public void setDealValueInt(Integer dealValueInt) {
		this.dealValueInt = dealValueInt;
	}

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

	public String getOutcomeExpectedDate() {
		return outcomeExpectedDate;
	}

	public void setOutcomeExpectedDate(String outcomeExpectedDate) {
		this.outcomeExpectedDate = outcomeExpectedDate;
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

	public String getOpportunityDescription() {
		return opportunityDescription;
	}

	public void setOpportunityDescription(String opportunityDescription) {
		this.opportunityDescription = opportunityDescription;
	}
	
}
