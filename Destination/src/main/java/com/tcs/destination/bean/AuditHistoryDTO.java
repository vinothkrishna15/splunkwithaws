package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Response DTO for the audit history service
 * @author TCS
 *
 */
public class AuditHistoryDTO implements Serializable, Comparable<AuditHistoryDTO> {

	private static final long serialVersionUID = 1L;

	private String userName;
	private Date date;
	private String operation;
	private List<String> messages;
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
	@Override
	public int compareTo(AuditHistoryDTO o) {
		return this.date.after(o.getDate()) ? 1 : (this.date.before(o.getDate()) ? -1 : 0);
	}
}
