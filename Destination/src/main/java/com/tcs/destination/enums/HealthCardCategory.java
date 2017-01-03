package com.tcs.destination.enums;


public enum HealthCardCategory {

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


	private HealthCardCategory(int categoryId, String category) {
		this.categoryId = categoryId;
		this.category = category;
	}

	public String getCategory() {
		return category;
	}
	
	public int getCategoryId() {
		return categoryId;
	}

	public static HealthCardCategory valueOfCategory(int categoryId) {
		for(HealthCardCategory h : HealthCardCategory.values()) {
			if(h.getCategoryId()==categoryId) {
				return h;
			}
		}
		return null;
	}
}
