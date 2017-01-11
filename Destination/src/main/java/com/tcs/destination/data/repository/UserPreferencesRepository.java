/**
 * 
 */
package com.tcs.destination.data.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.tcs.destination.bean.UserPreferencesT;

/**
 * @author tcs2
 *
 */
public interface UserPreferencesRepository extends
		CrudRepository<UserPreferencesT, Serializable> {

	@Query(value = "select competitor_name from user_preferences_t where user_id=(:userId) ", nativeQuery = true)
	List<String> getCompetitorList(@Param("userId") String userId);
	
	@Query(value = "select group_customer_name from user_preferences_t where user_id=?1", nativeQuery = true)
	List<String> getCustomerList(String userId);


}
