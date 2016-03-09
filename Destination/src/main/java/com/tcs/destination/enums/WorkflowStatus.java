package com.tcs.destination.enums;

public enum WorkflowStatus {
		SUBMITTED("SUBMITTED"), PENDING("PENDING"), NOT_APPLICABLE("NOT APPLICABLE"), APPROVED("APPROVED"), REJECTED("REJECTED");

	private final String status;

	private WorkflowStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
