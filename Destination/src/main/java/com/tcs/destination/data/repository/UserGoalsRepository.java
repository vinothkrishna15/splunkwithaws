package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserGoalsT;

/**
 * 
 * Repository for working with {@link UserGoalsT} domain objects
 */
@Repository
public interface UserGoalsRepository extends JpaRepository<UserGoalsT, String> {
	
}
