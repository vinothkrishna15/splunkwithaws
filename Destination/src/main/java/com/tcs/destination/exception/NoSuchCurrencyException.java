package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "No relevent currency found")
public class NoSuchCurrencyException extends RuntimeException {

}
