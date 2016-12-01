package com.tcs.destination.bean;

import java.util.List;

public class ContentDTO<T> {

	private List<T> content;
	
	public ContentDTO() {
		super();
	}
	
	public ContentDTO(List<T> content, int totalCount) {
		super();
		this.content = content;
	}

	public List<T> getContent() {
		return content;
	}
	public void setContent(List<T> content) {
		this.content = content;
	}
}
