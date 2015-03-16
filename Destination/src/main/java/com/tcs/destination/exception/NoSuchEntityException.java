package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Entity type exists. Please ensure your entity type.")
public class NoSuchEntityException extends RuntimeException {

}
