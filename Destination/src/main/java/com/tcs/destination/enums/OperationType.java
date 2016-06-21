package com.tcs.destination.enums;


public enum OperationType {
	//	ADD, EDIT, FOLLOW, COMMENT,
	OPPORTUNITY_CREATE(EntityType.OPPORTUNITY),
	OPPORTUNITY_EDIT(EntityType.OPPORTUNITY),
	OPPORTUNITY_FOLLOW(EntityType.OPPORTUNITY),
	OPPORTUNITY_COMMENT(EntityType.OPPORTUNITY),
	CONNECT_CREATE(EntityType.CONNECT),
	CONNECT_EDIT(EntityType.CONNECT),
	CONNECT_FOLLOW(EntityType.CONNECT),
	CONNECT_COMMENT(EntityType.CONNECT),
	TASK_CREATE(EntityType.TASK),
	TASK_EDIT(EntityType.TASK),
	TASK_COMMENT(EntityType.TASK);
	
	private EntityType entityType;
	
	private OperationType(EntityType entityType) {
		this.entityType = entityType;
	}

	public EntityType getEntityType() {
		return entityType;
	}



	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}



	public static OperationType getByValue(String value) {
		for (OperationType c : OperationType.values()) {
			if (c.name().equals(value)) {
				return c;
			}
		}
		return null;
	}
}
