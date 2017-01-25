package com.tcs.destination.bean.dto;

import java.util.Date;
import java.util.List;


public class CustomerListDTO {

	List<String> groupCustomerNames;
	String nameWith;
	String mapId;
	String type;
	Date fromDate;
	Date toDate;
	int page;
	int count;
	public List<String> getGroupCustomerNames() {
		return groupCustomerNames;
	}
	public void setGroupCustomerNames(List<String> groupCustomerNames) {
		this.groupCustomerNames = groupCustomerNames;
	}
	public String getMapId() {
		return mapId;
	}
	public void setMapId(String mapId) {
		this.mapId = mapId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getNameWith() {
		return nameWith;
	}
	public void setNameWith(String nameWith) {
		this.nameWith = nameWith;
	}
}
