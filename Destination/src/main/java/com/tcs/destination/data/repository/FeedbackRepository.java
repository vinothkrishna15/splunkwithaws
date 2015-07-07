package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.FeedbackT;

/**
 * 
 * Repository for working with {@link FeedbackT} domain objects
 */
@Repository
public interface FeedbackRepository extends JpaRepository<FeedbackT, String> {
}
