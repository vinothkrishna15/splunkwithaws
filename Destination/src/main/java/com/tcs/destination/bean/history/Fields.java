package com.tcs.destination.bean.history;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Fields {
	private List<Field> field;

	@XmlElement
	public List<Field> getField() {
		return field;
	}

	public void setField(List<Field> field) {
		this.field = field;
	}
	
	
}
