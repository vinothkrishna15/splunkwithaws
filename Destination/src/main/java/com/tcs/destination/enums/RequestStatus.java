package com.tcs.destination.enums;

public enum RequestStatus {
	
	SUBMITTED(0), VERIFIED(1), INPROGRESS(2), PROCESSED(3), EMAILED(4);
	
	private final int status;

	private RequestStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}

}
