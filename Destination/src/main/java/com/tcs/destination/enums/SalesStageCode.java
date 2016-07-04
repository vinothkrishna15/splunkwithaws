package com.tcs.destination.enums;


public enum SalesStageCode {
	
	SUSPECTING(0,"00 - Suspecting"),
	PROSPECTING(1,"01 - Prospecting"),
	RFI_IN_RESPONSE(2,"02 - EOI / RFI In Response"),
	RFI_SUBMITTED(3,"03 - EOI / RFI Submitted"),
	RFP_IN_PROGRESS(4,"04 - RFP in Progress"),
	RFP_SUBMITTED(5,"05 - RFP Submitted"),
	SHORTLISTED(6,"06 - Shortlisted"),
	SELECTED(7,"07 - Selected"),
	CONTRACT_NEGOTIATION(8,"08 - Contract Negotiation"),
	WIN(9,"09 - Closed & Won"),
	LOST(10,"10 - Closed & Lost"),
	CLOSED_AND_SCRAPPED(11,"11 - Closed & Scrapped"),
	CLOSED_AND_SHELVED(12,"12 - Closed & Shelved"),
	CLOSED_AND_DISQUALIFIED(13,"13 - Closed & Disqualified");

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
