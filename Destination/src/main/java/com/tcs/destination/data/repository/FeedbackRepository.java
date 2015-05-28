package com.tcs.destination.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.FeedbackT;

/**
 * 
 * Repository for working with {@link FeedbackT} domain objects
 */
@Repository
public interface FeedbackRepository extends CrudRepository<FeedbackT, String> {
}
