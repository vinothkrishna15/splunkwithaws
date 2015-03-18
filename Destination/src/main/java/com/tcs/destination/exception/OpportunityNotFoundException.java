package com.tcs.destination.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such Opportunity Found. Please ensure your Opportunity name.")
public class OpportunityNotFoundException extends RuntimeException {

}
