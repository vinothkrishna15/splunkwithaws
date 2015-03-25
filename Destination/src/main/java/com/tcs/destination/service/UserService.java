package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;

public class UserService {

	@Autowired
	UserRepository userRepository;  
	
	public List<UserT> findByUserName(String nameWith) {
		return (List<UserT>) userRepository.findByUserName(nameWith);
	}

}
