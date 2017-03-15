package com.tcs.destination.bean.dto;

import java.math.BigDecimal;
import java.util.List;

import com.tcs.destination.bean.HealthCardValues;

public class ContentModifiedDTO {

	private List<HealthCardValues> content;
	private BigDecimal quarterlyPercentage;
	
	public ContentModifiedDTO() {
		super();
	}
	

	public List<HealthCardValues> getContent() {
		return content;
	}

	public void setContent(List<HealthCardValues> content) {
		this.content = content;
	}



	public BigDecimal getQuarterlyPercentage() {
		return quarterlyPercentage;
	}

	public void setQuarterlyPercentage(BigDecimal quarterlyPercentage) {
		this.quarterlyPercentage = quarterlyPercentage;
	}
}
