package com.tcs.destination.enums;


public enum HealthCardComponent {

	WINS_RATIO(1,"Wins Ratio"),
	UTILIZATION(2,"Utilization"),
	UNALLOCATION(3,"Unallocation"),
	BILABILITY(4,"Bilability"),
	ATTRITION(5,"Attrition"),
	SKILL_CATEGORY(6,"Skill Category"),
	SENIOR_RATIO(7,"Senior Ratio"),
	TRAINEE_PERCENTAGE(8,"Trainee Percentage");
	
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
}
