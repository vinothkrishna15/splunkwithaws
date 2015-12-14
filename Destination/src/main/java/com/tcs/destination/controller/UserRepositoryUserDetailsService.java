package com.tcs.destination.controller;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tcs.destination.bean.UserT;
import com.tcs.destination.data.repository.UserRepository;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.exception.UnAuthorizedException;

/**
 *This service queries the userRepository and have its appropriate getters and setters
 */
@Service
public class UserRepositoryUserDetailsService implements UserDetailsService {

	private static final Logger logger = LoggerFactory.getLogger(UserRepositoryUserDetailsService.class);

	@Autowired
	private UserRepository userRepository;

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException {
		UserT user = null;

		try {
			logger.info("Inside user detail service");
			user = userRepository.findByUserName(userName);
		} catch (EmptyResultDataAccessException e) {
			logger.error("No Such User: " + userName);
			throw new UnAuthorizedException();
		} catch (IncorrectResultSizeDataAccessException e) {
			logger.error("More than one user found for the user name: " + userName);
			throw new DestinationException("More than one user found for the user name: " + userName);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new DestinationException(e.getMessage());
		}

		if (user == null) {
			logger.error("User not authorized: " + userName);
			throw new UnAuthorizedException();
		}
		return new UserRepositoryUserDetails(user);
	}

	/**
	 * UserRepositoryUserDetails and its getters and setters
	 *
	 */
	public final static class UserRepositoryUserDetails extends UserT
			implements UserDetails {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

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