package com.tcs.lambda.splunkexample;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaFunctionHandler implements RequestHandler<String, String> {

	final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    
    public String handleRequest(String input, Context context) {
    	 Logger logger = LogManager.getLogger("splunk.logger");
         
    	    logger.info(input);

    	    try {
    	        Thread.sleep(100);                 
    	    } catch(InterruptedException ex) {
    	        Thread.currentThread().interrupt();
    	    }

        String output = "Hello, " + input + "!";
        return output;
    }

}
