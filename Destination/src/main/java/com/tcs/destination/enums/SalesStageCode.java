package com.tcs.destination.enums;


public enum SalesStageCode {
	
	WIN(9,"09 - Closed & Won"),
	LOST(10,"10 - Closed & Lost");

	private final int code;
	
	private final String description;

	private SalesStageCode(int code, String description) {
		this.code = code;
		this.description = 	description;
	}

	public static boolean contains(String val) {

		for (SalesStageCode c : SalesStageCode.values()) {
			if (c.name().equals(val)) {
				return true;
			}
		}

		return false;
	}
	
	public static SalesStageCode valueOf(Integer val) {

		for (SalesStageCode c : SalesStageCode.values()) {
			if (c.getCode().equals(val)) {
				return c;
			}
		}

		return null;
	}

	public int getCodeValue() {
		return code;
	}
	
	public Integer getCode() {
		return new Integer(code);
	}

	public String getDescription() {
		return description;
	}
	
}
