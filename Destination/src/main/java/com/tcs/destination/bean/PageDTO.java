package com.tcs.destination.bean;

import java.util.List;

public class PageDTO<T> {

	private List<T> content;
	private int totalCount;
	
	public PageDTO() {
		super();
	}
	
	public PageDTO(List<T> content, int totalCount) {
		super();
		this.content = content;
		this.totalCount = totalCount;
	}

	public List<T> getContent() {
		return content;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
}
