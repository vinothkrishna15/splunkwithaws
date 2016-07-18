package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserSubscriptions;

@Repository
public interface UserSubscriptionRepository extends CrudRepository<UserSubscriptions, Long> {

}
