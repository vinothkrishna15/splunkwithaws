package com.tcs.destination.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.tcs.destination.utils.Constants;


/**
 * The persistent class for the user_module_access_t database table.
 * 
 */
@JsonFilter(Constants.FILTER)
public class UserModuleAccess implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Set<UserModule> module;

	public Set<UserModule> getModule() {
		return module;
	}

	public void setModule(Set<UserModule> module) {
		this.module = module;
	}
	
	public UserModule getModule(String moduleName) {
		UserModule rtnModule = null;
		if (CollectionUtils.isNotEmpty(this.module)) {
			for (UserModule module: this.module) {
				rtnModule = moduleName.equals(module.getModuleName()) ? module: null;
			}
		}
		return rtnModule;
	}
	
	public void addModule(UserModule module) {
		Set<UserModule> modules = CollectionUtils.isNotEmpty(this.module) ? this.module: new HashSet<UserModule>();
		modules.add(module);
		setModule(modules);
	}
}