package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Contact information not available")
public class ContactNotFoundException extends RuntimeException {

}
