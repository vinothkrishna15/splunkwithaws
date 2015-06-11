package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.ApplicationSettingsT;

public interface ApplicationSettingsRepository extends
		CrudRepository<ApplicationSettingsT, String> {

}
