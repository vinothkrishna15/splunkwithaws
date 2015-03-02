package com.tcs.destination.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No such Customer")
public class CustomerNotFoundException extends RuntimeException {

}
