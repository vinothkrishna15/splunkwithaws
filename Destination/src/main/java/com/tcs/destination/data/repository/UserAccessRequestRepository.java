package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserAccessRequestT;


/**
 * 
 * Repository for working with {@link UserAccessRequestT} domain objects
 */
@Repository
public interface UserAccessRequestRepository extends JpaRepository<UserAccessRequestT, String> {
}
