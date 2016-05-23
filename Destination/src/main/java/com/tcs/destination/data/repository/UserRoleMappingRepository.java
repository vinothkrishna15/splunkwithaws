package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserRoleMappingT;

/**
 * 
 * Repository for working with {@link UserGroupMappingT} domain objects
 */
@Repository
public interface UserRoleMappingRepository extends JpaRepository<UserRoleMappingT, String> {

	
}
