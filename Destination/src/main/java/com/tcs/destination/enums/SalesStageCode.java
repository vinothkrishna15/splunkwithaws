package com.tcs.destination.enums;


public enum SalesStageCode {
	
	WIN(9),
	LOST(10);

	private final int code;

	private SalesStageCode(int code) {
		this.code = code;
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
	
}
