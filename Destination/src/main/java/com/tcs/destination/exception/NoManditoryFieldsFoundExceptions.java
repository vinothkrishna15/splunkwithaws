package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Manditory Fields are unavailable or empty.")
public class NoManditoryFieldsFoundExceptions extends RuntimeException {

}
