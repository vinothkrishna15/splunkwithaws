package com.tcs.destination.enums;

public enum DeliveryStage {

	INTIMATED(0,"Intimated"), ACCEPTED(1,"Accepted"), ASSIGNED(2,"Assigned"), PLANNED(3,"Planned"), LIVE(4,"Live");
	
	private final Integer stageCode;
	private final String stageName;
	
	private DeliveryStage(Integer stageCode, String stageName) {
		this.stageCode = stageCode;
		this.stageName = stageName;
	}
	
	
	public Integer getStageCode() {
		return stageCode;
	}
	public String getStageName() {
		return stageName;
	}
	
	public static DeliveryStage byStageCode(Integer code) {
		for (DeliveryStage stage : DeliveryStage.values()) {
			if(stage.getStageCode().equals(code)) {
				return stage;
			}
		}
		return null;
	}
	
}
