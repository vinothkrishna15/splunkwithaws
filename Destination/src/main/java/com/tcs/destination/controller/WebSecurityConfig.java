package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
// @ComponentScan(basePackageClasses = UserRepositoryUserDetailsService.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(userDetailsService);
		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Instruct Spring Security not to create Session and use session exists already if any
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER);
		http.csrf().disable();
		//http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
		
		http.authorizeRequests().
		antMatchers("/api/useraccess/request").permitAll().
		antMatchers(HttpMethod.OPTIONS, "/**").permitAll().
		anyRequest().authenticated().and().httpBasic();
		
		
		 //http.authorizeRequests().antMatchers("api/newuser/request").permitAll();
		// .authenticated().and().csrf().disable();
		// .antMatchers("/login").permitAll()
		// .anyRequest().authenticated();
		// // .and().formLogin();
		// // .loginPage("/login").permitAll();
		//
	}
}
