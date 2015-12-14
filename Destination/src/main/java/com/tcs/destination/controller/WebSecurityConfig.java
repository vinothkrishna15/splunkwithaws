package com.tcs.destination.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

@Configuration
@EnableWebSecurity
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
		http.sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry());
		http.authorizeRequests().
		antMatchers(HttpMethod.POST,"/api/useraccess/request").permitAll().
		antMatchers(HttpMethod.POST,"/api/user/forgotpwd").permitAll().
		antMatchers(HttpMethod.OPTIONS, "/**").permitAll().
		anyRequest().authenticated().and().requestCache()
        .requestCache(new NullRequestCache()).and().httpBasic();		
		
		 //http.authorizeRequests().antMatchers("api/newuser/request").permitAll();
		// .authenticated().and().csrf().disable();
		// .antMatchers("/login").permitAll()
		// .anyRequest().authenticated();
		// // .and().formLogin();
		// // .loginPage("/login").permitAll();
		//
	}
	
		
	@Bean
    public  SessionRegistry sessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return  sessionRegistry;
    }
   
    // Register HttpSessionEventPublisher
	 @Bean
    public static HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
	@Bean
    public HttpSessionStrategy httpSessionStrategy() {
        return new HeaderHttpSessionStrategy();
    }
	
}
