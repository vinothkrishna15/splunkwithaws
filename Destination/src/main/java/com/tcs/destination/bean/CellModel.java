package com.tcs.destination.bean;

import java.util.List;

public class CellModel {

	private String cellValue;
	
	private List<String> errors;
	
	private List<String> cellValues;

	public String getCellValue() {
		return cellValue;
	}

	public void setCellValue(String cellValue) {
		this.cellValue = cellValue;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public List<String> getCellValues() {
		return cellValues;
	}

	public void setCellValues(List<String> cellValues) {
		this.cellValues = cellValues;
	} 
	
}
