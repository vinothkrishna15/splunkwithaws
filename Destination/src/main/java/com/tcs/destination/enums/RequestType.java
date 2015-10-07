package com.tcs.destination.enums;

public enum RequestType {
	
	CUSTOMER_UPLOAD(1),
	CUSTOMER_DOWNLOAD(2),
	CONNECT_UPLOAD(3),
	CONNECT_DOWNLOAD(4),
	OPPORTUNITY_UPLOAD(5),
	OPPORTUNITY_DOWNLOAD(6);
	
	private final int type;

	private RequestType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return this.type;
	}

}
