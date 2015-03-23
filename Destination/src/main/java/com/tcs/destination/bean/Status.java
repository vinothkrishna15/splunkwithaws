package com.tcs.destination.bean;

public class Status {

	public static final String SUCCESS = "Success";

	public static final String FAILED = "Failed";

	private String status;

	private String description;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status, String description) {
		this.status = status;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
