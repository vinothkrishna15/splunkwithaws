package com.tcs.destination.enums;

public enum RequestStatus {
	
	SUBMITTED(1), NOTIFIED(2), VERIFIED(3), INPROGRESS(4), PROCESSED(5), EMAILED(6);
	
	private final int status;

	private RequestStatus(int status) {
		this.status = status;
	}
	
	public int getStatus() {
		return this.status;
	}

}
