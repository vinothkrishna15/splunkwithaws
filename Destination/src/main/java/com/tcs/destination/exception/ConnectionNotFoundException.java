package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="Connection information not available")
public class ConnectionNotFoundException extends RuntimeException {

}
