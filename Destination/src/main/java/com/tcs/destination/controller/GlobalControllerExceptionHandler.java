package com.tcs.destination.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class GlobalControllerExceptionHandler {

	
	@ResponseStatus(HttpStatus.BAD_REQUEST)  // 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleRequest() {
        // Nothing to do
    }
}
