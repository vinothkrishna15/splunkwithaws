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

	@Query(value = "select distinct upt.competitorName from UserPreferencesT upt where upt.userId=(:userId) and moduleType='COMPETITOR'")
	List<String> getCompetitorList(@Param("userId") String userId);

	@Query(value = "select group_customer_name from user_preferences_t where user_id=?1 AND module_type='CUSTOMER'", nativeQuery = true)
	List<String> getCustomerList(String userId);

	UserPreferencesT findByUserIdAndCompetitorName(String userId,
			String competitorName);

	UserPreferencesT findByGroupCustomerNameAndUserId(String groupCustomerName,
			String userId);


}
