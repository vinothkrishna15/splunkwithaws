package com.tcs.destination.enums;

public enum WorkflowStepStatus {
	OPEN("OPEN"), PENDING("PENDING"), APPROVED("APPROVED"), REJECTED("REJECTED");

	private final String status;

	private WorkflowStepStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
