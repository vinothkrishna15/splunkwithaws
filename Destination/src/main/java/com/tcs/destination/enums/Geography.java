package com.tcs.destination.enums;

/**
 * Enum used for Geography/Display Geography
 * @author TCS
 *
 */
public enum Geography {

	APAC_IND_MEA("APAC Ind MEA"),
	AMERICAS("Americas"),
	EU_UK("EU&UK");
	
	private String displayGeography;

	private Geography(String displayGeography) {
		this.displayGeography = displayGeography;
	}

	public String getDisplayGeography() {
		return displayGeography;
	}

	public void setDisplayGeography(String displayGeography) {
		this.displayGeography = displayGeography;
	}
	
	
}
