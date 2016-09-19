package com.tcs.destination.bean;

import com.tcs.destination.config.ReportDataItem;

/**
 * Bean used for getting RFP Partner Connects to be processed in generating Weekly report
 * @author TCS
 *
 */
public class ConnectPartner {

	@ReportDataItem(columnName = "BD Contact")
	private String bdContact;
	
	@ReportDataItem(columnName = "Connect Date")
	private String connectDate;
	
	@ReportDataItem(columnName = "Partner Name")
	private String partnerName;
	
	@ReportDataItem(columnName = "Connect Name")
	private String connectName;
	
	@ReportDataItem(columnName = "Sub Sp",width = 70)
	private String subSp;
	
	@ReportDataItem(columnName = "Partner Contact")
	private String partnerContact;
	
	@ReportDataItem(columnName = "Partner Contact Role")
	private String partnerContactRole;
	
	@ReportDataItem(columnName = "Connect Category")
	private String connectCategory;

	public String getBdContact() {
		return bdContact;
	}

	public void setBdContact(String bdContact) {
		this.bdContact = bdContact;
	}

	public String getConnectDate() {
		return connectDate;
	}

	public void setConnectDate(String connectDate) {
		this.connectDate = connectDate;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getConnectName() {
		return connectName;
	}

	public void setConnectName(String connectName) {
		this.connectName = connectName;
	}

	public String getSubSp() {
		return subSp;
	}

	public void setSubSp(String subSp) {
		this.subSp = subSp;
	}

	public String getPartnerContact() {
		return partnerContact;
	}

	public void setPartnerContact(String partnerContact) {
		this.partnerContact = partnerContact;
	}

	public String getPartnerContactRole() {
		return partnerContactRole;
	}

	public void setPartnerContactRole(String partnerContactRole) {
		this.partnerContactRole = partnerContactRole;
	}

	public String getConnectCategory() {
		return connectCategory;
	}

	public void setConnectCategory(String connectCategory) {
		this.connectCategory = connectCategory;
	}
	
	
}
