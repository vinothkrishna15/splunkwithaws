package com.tcs.destination.data.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserNotificationSettingsConditionsT;

@Repository
public interface UserNotificationSettingsConditionRepository extends
		CrudRepository<UserNotificationSettingsConditionsT, String> {

	List<UserNotificationSettingsConditionsT> findByUserIdAndEventId(
			String userId, Integer eventId);

	@Query(value = "select distinct(user_id) from user_notification_settings_conditions_t where condition_id = ?1 and condition_value=?2 ", nativeQuery = true)
	Set<String> findUserIdByConditionIdAndConditionValue(Integer conditionId,
			String conditionValue);

	@Query(value = "select distinct(user_id) from user_notification_settings_conditions_t where condition_id = 6 and (CAST(REGEXP_REPLACE(coalesce(condition_value,'0'),'[^0-9]*','0') AS NUMERIC)) < (:digitalDealValue) ", nativeQuery = true)
	Set<String> findUserIdByDigitalDealValueGreaterThan(
			@Param("digitalDealValue") BigDecimal digitalDealValue);
}
