package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Response DTO for the audit history service
 * @author TCS
 *
 */
public class AuditHistoryResponseDTO<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<T> histories;

	
	public AuditHistoryResponseDTO(List<T> auditHistories) {
		super();
		this.histories = auditHistories;
	}
	
	public AuditHistoryResponseDTO() {
		super();
	}

	public List<T> getHistories() {
		return histories;
	}

	public void setHistories(List<T> histories) {
		this.histories = histories;
	}
	
	
}
