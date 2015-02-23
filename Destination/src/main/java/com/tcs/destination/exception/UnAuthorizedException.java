package com.tcs.destination.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Invalid Login")
public class UnAuthorizedException extends RuntimeException {
}
