package com.tcs.destination.enums;

public enum Operation {
	
	ADD(1), UPDATE(2), DELETE(0);
	
	private int code;

	private Operation(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public static Operation getByCode(int code) {
		for (Operation operation : Operation.values()) {
			if(operation.getCode() == code) {
				return operation;
			}
		}
		return null;
	}
}
