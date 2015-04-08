package com.tcs.destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class DestinationContextListener implements
		ApplicationListener<ContextRefreshedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(DestinationContextListener.class);

	@Value("${logBaseDir}")
	private String logBasePath;
	
	@Value("${fileBaseDir}")
	private String fileBasePath;

	@Value("${multipart.maxFileSize}")
	private String multipartMaxFileSize;

	@Value("${multipart.maxRequestSize}")
	private String multipartMaxRequestSize;

	@Value("${spring.datasource.url}")
	private String springDatasourceUrl;

	@Value("${spring.datasource.username}")
	private String springDatasourceUsername;

	@Value("${spring.datasource.driver-class-name}")
	private String springDatasourceDriver_class_name;

	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		// do what you want - you can use all spring beans (this is the
		// difference between init-method and @PostConstructor where you can't)
		// this class can be annotated as spring service, and you can use
		// @Autowired in it
		logger.info("Datasource Url - " + springDatasourceUrl);
		logger.info("Datasource Username - " + springDatasourceUsername);
		logger.info("Datasource Driver - " + springDatasourceDriver_class_name);
		logger.info("Log files Directory - " + logBasePath);
		logger.info("Uploaded files Directory - " + fileBasePath);
		logger.info("Max Size of file upload - " + multipartMaxFileSize);
		logger.info("Max size of Request - " + multipartMaxRequestSize);
	}

}
