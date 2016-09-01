package com.tcs.destination.bean;

import com.tcs.destination.config.ReportDataItem;

/**
 * Bean used for getting Customer Connects to be processed in generating Weekly report
 * @author TCS
 *
 */
public class ConnectCustomer {

	@ReportDataItem(columnName = "BD Contact")
	private String bdContact;
	
	@ReportDataItem(columnName = "Connect Date")
	private String connectDate;
	
	@ReportDataItem(columnName = "Customer Name")
	private String customerName;
	
	@ReportDataItem(columnName = "Connect Name")
	private String connectName;
	
	@ReportDataItem(columnName = "Sub Sp",width = 70)
	private String subSp;
	
	@ReportDataItem(columnName = "IOU",width = 40)
	private String iou;
	
	@ReportDataItem(columnName = "Customer Contact")
	private String customerContact;
	
	@ReportDataItem(columnName = "Customer Contact Role")
	private String customerContactRole;
	
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

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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

	public String getIou() {
		return iou;
	}

	public void setIou(String iou) {
		this.iou = iou;
	}

	public String getCustomerContact() {
		return customerContact;
	}

	public void setCustomerContact(String customerContact) {
		this.customerContact = customerContact;
	}

	public String getCustomerContactRole() {
		return customerContactRole;
	}

	public void setCustomerContactRole(String customerContactRole) {
		this.customerContactRole = customerContactRole;
	}

	public String getConnectCategory() {
		return connectCategory;
	}

	public void setConnectCategory(String connectCategory) {
		this.connectCategory = connectCategory;
	}
	
	
	
	
}
