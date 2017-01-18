package com.tcs.destination.enums;


public enum HealthCardComponent {

	WIN_RATIO(1,"Win Ratio"),
	UTILIZATION(2,"Utilization"),
	UNALLOCATION(3,"Unallocation"),
	BILLABILITY(4,"Billability"),
	ATTRITION(5,"Attrition"),
	SENIOR_RATIO(6,"Senior Ratio"),
	TRAINEE(7,"Trainee");
	
	private final int categoryId;
	private final String category;


	private HealthCardComponent(int categoryId, String category) {
		this.categoryId = categoryId;
		this.category = category;
	}

	public String getCategory() {
		return category;
	}
	
	public int getCategoryId() {
		return categoryId;
	}

	public static HealthCardComponent valueOfCategory(int categoryId) {
		for(HealthCardComponent h : HealthCardComponent.values()) {
			if(h.getCategoryId()==categoryId) {
				return h;
			}
		}
		return null;
	}
	
	public static HealthCardComponent valueOfCategory(String category) {
		for(HealthCardComponent h : HealthCardComponent.values()) {
			if(h.getCategory().equals(category)) {
				return h;
			}
		}
		return null;
	}
	
	public static String getCategoryName(int categoryId) {
		for(HealthCardComponent h : HealthCardComponent.values()) {
			if(h.getCategoryId()==categoryId) {
				return h.getCategory();
			}
		}
		return null;
	}
}
