package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No such Owner Type exists. Please ensure your Owner Type.")
public class NoSuchOwnerTypeException extends RuntimeException {

}
