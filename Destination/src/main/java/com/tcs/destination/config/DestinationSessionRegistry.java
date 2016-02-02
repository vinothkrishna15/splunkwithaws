/**
 * 
 * DestinationSessionRegistry.java 
 *
 * @author TCS
 * @Version 1.0 - 2016
 * 
 * @Copyright 2016 Tata Consultancy 
 */
package com.tcs.destination.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.session.events.SessionDestroyedEvent;

/**
 * This DestinationSessionRegistry class holds the implementation for session
 * registry for destination
 * 
 */
public class DestinationSessionRegistry implements SessionRegistry,
		ApplicationListener<SessionDestroyedEvent> {

	private static final Logger logger = LoggerFactory
			.getLogger(DestinationSessionRegistry.class);

	private SessionRegistry sessionRegistry;

	public DestinationSessionRegistry() {
		sessionRegistry = new SessionRegistryImpl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.session.SessionRegistry#getAllPrincipals
	 * ()
	 */
	@Override
	public List<Object> getAllPrincipals() {
		return sessionRegistry.getAllPrincipals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.session.SessionRegistry#getAllSessions
	 * (java.lang.Object, boolean)
	 */
	@Override
	public List<SessionInformation> getAllSessions(Object principal,
			boolean includeExpiredSessions) {
		return sessionRegistry
				.getAllSessions(principal, includeExpiredSessions);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.session.SessionRegistry#
	 * getSessionInformation(java.lang.String)
	 */
	@Override
	public SessionInformation getSessionInformation(String sessionId) {
		return sessionRegistry.getSessionInformation(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.session.SessionRegistry#refreshLastRequest
	 * (java.lang.String)
	 */
	@Override
	public void refreshLastRequest(String sessionId) {
		sessionRegistry.refreshLastRequest(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.core.session.SessionRegistry#registerNewSession
	 * (java.lang.String, java.lang.Object)
	 */
	@Override
	public void registerNewSession(String sessionId, Object principal) {
		sessionRegistry.registerNewSession(sessionId, principal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.session.SessionRegistry#
	 * removeSessionInformation(java.lang.String)
	 */
	@Override
	public void removeSessionInformation(String sessionId) {
		sessionRegistry.removeSessionInformation(sessionId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationListener#onApplicationEvent(org
	 * .springframework.context.ApplicationEvent)
	 */
	@Override
	public void onApplicationEvent(SessionDestroyedEvent event) {

		String sessionId = event.getSessionId();

		logger.info("Removing session, id: {}, event: {} ", sessionId,
				event.toString());

		removeSessionInformation(sessionId);

	}

}
