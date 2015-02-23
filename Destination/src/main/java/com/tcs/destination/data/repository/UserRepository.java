package com.tcs.destination.data.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.tcs.destination.bean.UserT;
import java.lang.String;

@Repository
public interface UserRepository extends CrudRepository<UserT, Long> {
	List<UserT> findByUserName(String username);
}
