package com.tcs.destination.enums;

public enum DeliveryStage {

	INTIMATED(0,"Intimated"),ACCEPTED(1,"Accepted"),ASSIGNED(2,"Assigned"),PLANNED(3,"Planned"),LIVE(4,"Live");
	
	private final Integer stage;
	private final String stageName;
	private DeliveryStage(Integer stage, String stageName) {
		this.stage = stage;
		this.stageName = stageName;
	}
	public Integer getStage() {
		return stage;
	}
	public String getStageName() {
		return stageName;
	}
	
	
}
