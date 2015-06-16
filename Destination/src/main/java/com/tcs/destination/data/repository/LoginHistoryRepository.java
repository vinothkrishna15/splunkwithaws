package com.tcs.destination.data.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.tcs.destination.bean.LoginHistoryT;

public interface LoginHistoryRepository extends CrudRepository<LoginHistoryT, Integer> {
	
	@Query(value="select * from login_history_t where user_id = ?1 order by login_datetime desc limit 1", nativeQuery=true)
	LoginHistoryT findLastLoginByUserId(String userId);
	
	LoginHistoryT findByUserIdAndSessionId(String userId, String sessionId);
}
