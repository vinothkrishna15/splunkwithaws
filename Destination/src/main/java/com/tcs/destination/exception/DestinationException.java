package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;

public class DestinationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message = null;
	private HttpStatus httpStatus = null;

	public DestinationException() {
		super();
	}

	public DestinationException(HttpStatus httpStatus, String message) {
		super(message);
		this.message = message;
		this.httpStatus = httpStatus;
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}