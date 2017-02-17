package com.tcs.destination.enums;

public enum BidRequestType {

	APPROACH_NOTE("Approach Note"),
	POC("PoC"),
	PROACTIVE("Proactive"),
	RFI("RFI"),
	RFQ("RFQ"),
	RFP("RFP");
	
	private final String bidType;

	private BidRequestType(String bidType) {
		this.bidType = bidType;
	}

	public String getBidType() {
		return bidType;
	}
}
