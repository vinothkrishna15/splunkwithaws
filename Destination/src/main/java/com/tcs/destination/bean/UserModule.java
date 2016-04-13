package com.tcs.destination.bean;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;

@JsonFilter(Constants.FILTER)
public class UserModule {
		
	private String moduleName;
		
	private Set<String> subModuleName;
	
	public UserModule() {
		super();
	}
	
	public UserModule(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Set<String> getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(Set<String> subModuleName) {
		this.subModuleName = subModuleName;
	}
	
	public void addSubModuleName(String subModuleName) {
		if (CollectionUtils.isEmpty(this.subModuleName)) {
			this.subModuleName = new HashSet<String>();
		}
		this.subModuleName.add(subModuleName);
	}

}
