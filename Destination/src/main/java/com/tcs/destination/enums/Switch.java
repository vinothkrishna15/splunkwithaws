package com.tcs.destination.enums;

public enum Switch {
	
	ON, OFF;
	
	public boolean checkSwitch(String value) {
		return this.name().equals(value);
	}
	
}
