package com.tcs.destination.bean.history;


import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class AuditHistoryTable {

	private String repository;
	private String repoMethod;
	private String modifiedByField;
	private String modifiedDateField;
	private String operationTypeField;
	private Boolean isChildTable;
	private String idField;
	private Fields fields;
	private List<AuditHistoryTable> auditHistoryTable;
	
	@XmlElement
	public List<AuditHistoryTable> getAuditHistoryTable() {
		return auditHistoryTable;
	}
	public void setAuditHistoryTable(List<AuditHistoryTable> auditHistoryTable) {
		this.auditHistoryTable = auditHistoryTable;
	}

	@XmlAttribute
	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	@XmlAttribute
	public String getRepoMethod() {
		return repoMethod;
	}

	public void setRepoMethod(String repoMethod) {
		this.repoMethod = repoMethod;
	}

	@XmlAttribute
	public String getModifiedByField() {
		return modifiedByField;
	}
	public void setModifiedByField(String modifiedByField) {
		this.modifiedByField = modifiedByField;
	}
	
	@XmlAttribute
	public String getModifiedDateField() {
		return modifiedDateField;
	}
	public void setModifiedDateField(String modifiedDateField) {
		this.modifiedDateField = modifiedDateField;
	}
	
	@XmlAttribute
	public Boolean getIsChildTable() {
		return isChildTable;
	}

	public void setIsChildTable(Boolean isChildTable) {
		this.isChildTable = isChildTable;
	}

	@XmlAttribute
	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	@XmlElement
	public Fields getFields() {
		return fields;
	}

	public void setFields(Fields fields) {
		this.fields = fields;
	}
	
	@XmlAttribute
	public String getOperationTypeField() {
		return operationTypeField;
	}
	public void setOperationTypeField(String operationTypeField) {
		this.operationTypeField = operationTypeField;
	}
	
	
	
	
}
