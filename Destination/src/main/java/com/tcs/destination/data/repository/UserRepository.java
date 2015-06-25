package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserT;

import java.lang.String;

@Repository
public interface UserRepository extends CrudRepository<UserT, String> {
	List<UserT> findByUserNameIgnoreCaseLike(String nameWith);
	UserT findByUserName(String userName);
	
	UserT findByUserIdAndTempPassword(String userName, String tempPassword);
}
