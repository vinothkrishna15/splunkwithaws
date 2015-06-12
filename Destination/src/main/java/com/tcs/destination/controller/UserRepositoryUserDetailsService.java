package com.tcs.destination.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.UnAuthorizedException;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryUserDetailsService.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		UserT user = null;

		try {
			logger.info("Inside user detail service");
			user = userRepository.findByUserName(userName);
		} catch (Exception e) {
			logger.error("No Such User: " + userName);
			throw new UnAuthorizedException();
		}

		if (user == null) {
			logger.error("User not authorized: " + userName);
			throw new UnAuthorizedException();
		}
		return new UserRepositoryUserDetails(user);
	}

	public final static class UserRepositoryUserDetails extends UserT
			implements UserDetails {

		private UserRepositoryUserDetails(UserT user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
		    return null;
		}

		@Override
		public String getPassword() {
			return getTempPassword();
		}
		
		@Override
		public String getUsername() {
			return getUserName();
		}

//		@Override
//		public String getUserId() {
//			return tempUserT.getUserId();
//		}
//
//		
//
//		@Override
//		public String getSupervisorUserId() {
//			// TODO Auto-generated method stub
//			return tempUserT.getSupervisorUserId();
//		}
//
//		@Override
//		public String getSupervisorUserName() {
//			// TODO Auto-generated method stub
//			return tempUserT.getSupervisorUserName();
//		}
//
//		@Override
//		public String getTempPassword() {
//			// TODO Auto-generated method stub
//			return tempUserT.getTempPassword();
//		}
//
//		@Override
//		public String getUserEmailId() {
//			// TODO Auto-generated method stub
//			return tempUserT.getUserEmailId();
//		}
//
//		@Override
//		public String getUserGeography() {
//			// TODO Auto-generated method stub
//			return tempUserT.getUserGeography();
//		}
//
//		@Override
//		public String getUserTelephone() {
//			// TODO Auto-generated method stub
//			return tempUserT.getUserTelephone();
//		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
	}
}