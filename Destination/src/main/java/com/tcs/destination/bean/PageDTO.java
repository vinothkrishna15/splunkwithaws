package com.tcs.destination.bean;

import java.util.List;

public class PageDTO<T> {

	private List<T> content;
	private long totalCount;
	
	public PageDTO() {
		super();
	}
	
	public PageDTO(List<T> content, long totalCount) {
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
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	
}
