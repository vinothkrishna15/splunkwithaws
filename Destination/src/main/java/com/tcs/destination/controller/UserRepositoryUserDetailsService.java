package com.tcs.destination.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.UnAuthorizedException;

@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public UserRepositoryUserDetailsService(UserRepository userRepository) {
		System.out.println("Inside Constructor...");
		this.userRepository = userRepository;
	}
	
	

	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserT user = null;

		try {
			user = userRepository.findByUserName(userName).get(0);
			System.out.println("Getting " + userName + " bean : "
					+ userRepository.findByUserName(userName).size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("No Such User");
			throw new UnAuthorizedException();
		}

		if (user == null) {
			System.out.println("user is null...");
			throw new UnAuthorizedException();
//			throw new UsernameNotFoundException("Could not find user "
//					+ userName);
		}
		System.out.println("Returning value...");
		return new UserRepositoryUserDetails(user);
	}

	private final static class UserRepositoryUserDetails extends UserT
			implements UserDetails {
		//private UserT tempUserT = null;

		private UserRepositoryUserDetails(UserT user) {
			super(user);
			//tempUserT = user;
		}
		
		

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			// TODO Auto-generated method stub
		    return null;
			//authlist.add(new GrantedAuthorityImpl("ROLE_AUTHORIZED_USER"));
		}

		
		
		@Override
		public String getPassword() {
			// TODO Auto-generated method stub
			return getTempPassword();
		}
		
		@Override
		public String getUsername() {
			// TODO Auto-generated method stub
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
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isEnabled() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}
}
