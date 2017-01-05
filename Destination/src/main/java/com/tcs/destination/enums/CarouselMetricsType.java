package com.tcs.destination.enums;

public enum CarouselMetricsType {
	ALL("ALL"),
	HEALTH_CARD("HEALTH CARD"),
	WIN_RATIO("WIN RATIO"),
	CUSTOMER("CUSTOMER"),
	OPPORTUNITY("OPPORTUNITY");
	
	private final String type;

	private CarouselMetricsType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
