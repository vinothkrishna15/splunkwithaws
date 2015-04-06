package com.tcs.destination.bean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class DestinationBeanObject {

	@JsonIgnore
	@Transient
	protected List<Class> traversedClasses = new ArrayList<Class>();

	public DestinationBeanObject() {
		traversedClasses.add(getClass());
	}

	public DestinationBeanObject traverse(List<Class> traversedClasses) {
//		this.traversedClasses.removeAll(traversedClasses);
//		this.traversedClasses.addAll(traversedClasses);
		System.out.println("Current Class " + getClass());
		for (Class className : traversedClasses)
			System.out.println(">> Classes traversed : "
					+ className.getCanonicalName());
		if (traversedClasses.contains(getClass())) {
			return null;
		} else {
			this.traversedClasses.addAll(traversedClasses);
			return this;
		}
	}

	public List<DestinationBeanObject> getCyclicCheckedList(
			List<? extends DestinationBeanObject> destinationObjects) {
		List<DestinationBeanObject> checkedList = new ArrayList<DestinationBeanObject>();
		for (DestinationBeanObject object : destinationObjects) {
			checkedList.add(object.traverse(traversedClasses));
		}
		return checkedList;
	}

	protected void printCurrentTraversalList(String id) {
		for (Class className : traversedClasses)
			System.out.println("Traversal Classes for " + id + " : "
					+ className.getCanonicalName());
	}
}
