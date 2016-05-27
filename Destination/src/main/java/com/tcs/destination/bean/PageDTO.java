package com.tcs.destination.bean;

import java.util.List;

public class PageDTO<T> {

	private List<T> content;
	private int totalCount;
	
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
