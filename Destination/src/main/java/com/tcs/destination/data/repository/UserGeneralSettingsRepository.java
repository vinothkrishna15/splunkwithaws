package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserGeneralSettingsT;
import java.lang.String;

@Repository
public interface UserGeneralSettingsRepository extends CrudRepository<UserGeneralSettingsT, Long> {

	UserGeneralSettingsT findByUserId(String userId);
	
	
}
