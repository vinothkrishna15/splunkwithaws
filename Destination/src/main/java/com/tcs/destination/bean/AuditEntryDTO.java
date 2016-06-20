package com.tcs.destination.bean;

import java.util.Date;

public class AuditEntryDTO {

	private String fieldName;
	private String fromVal;
	private String toVal;
	private int operation;
	private String user;
	private Date date;
	private boolean isNewEntry;
	
	/**
	 * Updation entry
	 * @param fieldName
	 * @param fromVal
	 * @param toVal
	 * @param user
	 * @param date
	 * @param isNewEntry
	 */
	public AuditEntryDTO(String fieldName, String fromVal, String toVal,
			String user, Date date) {
		this(fieldName, fromVal, toVal, 2, user, date, false);
	}

	/**
	 * Audit for a new Entry
	 * @param user
	 * @param date
	 */
	public AuditEntryDTO(String user, Date date) {
		this(null, null, null, 1, user, date, true);
	}
	
	public AuditEntryDTO(String fieldName, String fromVal, String toVal,
			int operation, String user, Date date, boolean isNewEntry) {
		super();
		this.fieldName = fieldName;
		this.fromVal = fromVal;
		this.toVal = toVal;
		this.operation = operation;
		this.user = user;
		this.date = date;
		this.isNewEntry = isNewEntry;
	}


	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFromVal() {
		return fromVal;
	}
	public void setFromVal(String fromVal) {
		this.fromVal = fromVal;
	}
	public String getToVal() {
		return toVal;
	}
	public void setToVal(String toVal) {
		this.toVal = toVal;
	}
	public int getOperation() {
		return operation;
	}
	public void setOperation(int operation) {
		this.operation = operation;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public boolean isNewEntry() {
		return isNewEntry;
	}
	public void setNewEntry(boolean isNewEntry) {
		this.isNewEntry = isNewEntry;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + operation;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuditEntryDTO other = (AuditEntryDTO) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (operation != other.operation)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
