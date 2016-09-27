package com.tcs.destination.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tcs.destination.bean.Status;
import com.tcs.destination.exception.DestinationException;
import com.tcs.destination.utils.DestinationUtils;

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
	public ResponseEntity<Status> handleDestinationException(DestinationException de) {
		if (DestinationUtils.getCurrentUserDetails() != null) 
			MDC.put("userId", DestinationUtils.getCurrentUserDetails().getUserId());
		logger.error("DestinationException: Status-" + de.getHttpStatus()
				+ " Message-" + de.getMessage());
		Status status = new Status();
		status.setStatus(Status.FAILED, de.getMessage());
		return new ResponseEntity<Status> (status, de.getHttpStatus());
	}

	/**
	 * Exception handler method for all Exceptions
	 * 
	 * @param Exception
	 * @return ResponseEntity
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Status> handleAllException(Exception e) {
		if (DestinationUtils.getCurrentUserDetails() != null) 
			MDC.put("userId", DestinationUtils.getCurrentUserDetails().getUserId());
		logger.error("Exception: ", e);
		Status status = new Status();
		status.setStatus(Status.FAILED, "INTERNAL SERVER ERROR");
		return new ResponseEntity<Status>(status, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
