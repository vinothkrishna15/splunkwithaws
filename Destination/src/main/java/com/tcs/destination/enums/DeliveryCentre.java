package com.tcs.destination.enums;

public enum DeliveryCentre {
	OPEN(-1,"Open"), 
	MUMBAI(1,"Mumbai"), 
	PUNE(2,"Pune"), 
	KOLKATA(3,"Kolkata"), 
	DELHI(4, "Delhi"),
	HYDERABAD(5,"Hyderabad"),
	CHENNAI(6,"Chennai"),
	KOCHI(7,"Kochi"),
	BANGALORE(8,"Bangalore");
	
	private Integer deliveryCentreId;
	
	private String deliveryCentre;

	private DeliveryCentre(Integer deliveryCentreId, String deliveryCentre) {
		this.deliveryCentreId = deliveryCentreId;
		this.deliveryCentre = deliveryCentre;
	}
	
	public static DeliveryCentre byCentreId(Integer deliveryCentreId) {
		for (DeliveryCentre centre : DeliveryCentre.values()) {
			if(centre.getDeliveryCentreId().equals(deliveryCentreId)) {
				return centre;
			}
		}
		return null;
	}
	
	public static String getCentreNameFromCentreId(Integer deliveryCentreId) {
		for (DeliveryCentre centre : DeliveryCentre.values()) {
			if(centre.getDeliveryCentreId().equals(deliveryCentreId)) {
				return centre.getDeliveryCentre();
			}
		}
		return null;
	}

	public Integer getDeliveryCentreId() {
		return deliveryCentreId;
	}

	public void setDeliveryCentreId(Integer deliveryCentreId) {
		this.deliveryCentreId = deliveryCentreId;
	}

	public String getDeliveryCentre() {
		return deliveryCentre;
	}

	public void setDeliveryCentre(String deliveryCentre) {
		this.deliveryCentre = deliveryCentre;
	}
	
	
	
}
