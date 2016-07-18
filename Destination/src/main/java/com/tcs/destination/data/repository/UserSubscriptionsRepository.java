package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserSubscriptions;

@Repository
public interface UserSubscriptionsRepository extends CrudRepository<UserSubscriptions, Long> {

	List<UserSubscriptions> findByUserId(String userId);

}
