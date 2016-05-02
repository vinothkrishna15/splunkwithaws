package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserGroupMappingT;

/**
 * 
 * Repository for working with {@link UserGroupMappingT} domain objects
 */
@Repository
public interface UserGroupMappingRepository extends JpaRepository<UserGroupMappingT, String> {

	
}
