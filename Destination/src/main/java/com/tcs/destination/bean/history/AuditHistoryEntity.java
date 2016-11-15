package com.tcs.destination.bean.history;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class AuditHistoryEntity {

	private List<AuditHistoryTable> auditHistoryTable;
	private String type;
	
	@XmlElement
	public List<AuditHistoryTable> getAuditHistoryTable() {
		return auditHistoryTable;
	}
	public void setAuditHistoryTable(List<AuditHistoryTable> auditHistoryTable) {
		this.auditHistoryTable = auditHistoryTable;
	}
	
	@XmlAttribute
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	
}
