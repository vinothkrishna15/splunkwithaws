package com.tcs.destination.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;

@Component
public class UserService {

	@Autowired
	UserRepository userRepository;  
	
	public List<UserT> findByUserName(String nameWith) throws Exception{
		List<UserT> users = (List<UserT>) userRepository.findByUserName(nameWith);
		
		if (users.isEmpty())
			throw new DestinationException(HttpStatus.NOT_FOUND,"No matching user found");
		
		return users;
	}

}
