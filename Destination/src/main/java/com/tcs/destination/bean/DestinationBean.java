package com.tcs.destination.bean;

public class DestinationBean {

	private boolean root = false;

	public boolean isRoot() {
		return root;
	}

	public void setRoot(boolean root) {
		this.root = root;
	}

	public DestinationBean asRoot() {
		root = true;
		return this;
	}

	public Object getForRoot(Object bean) {
		if (root) {
			return bean;
		} else {
			return null;
		}
	}
	
	public DestinationBean asLeaf() {
		root = false;
		return this;
	}
}
