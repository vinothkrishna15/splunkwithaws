package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;

/**
 * Controller class to handle application exceptions.
 * 
 */
@ControllerAdvice
public class GlobalExceptionController {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);

	/**
	 * Exception handler method for DestinationException
	 * 
	 * @param DestinationException
	 * @return ResponseEntity
	 */
	@ExceptionHandler(DestinationException.class)
	public ResponseEntity<String> handleDestinationException(DestinationException de) {
		logger.error("DestinationException: " + de.getMessage());
		Status status = new Status();
		status.setStatus(Status.FAILED, de.getMessage());
		return new ResponseEntity<String> (status.toString(), de.getHttpStatus());
	}

	/**
	 * Exception handler method for all Exceptions
	 * 
	 * @param Exception
	 * @return ResponseEntity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleAllException(Exception e) {
		logger.error("Exception: " + e.getMessage());
		Status status = new Status();
		status.setStatus(Status.FAILED, e.getMessage());
		return new ResponseEntity<String>(status.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
