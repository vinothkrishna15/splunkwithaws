package com.tcs.lambda.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;

public class HelloWorld implements RequestHandler<String, String> {

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
