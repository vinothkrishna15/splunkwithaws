package com.tcs.destination;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class EmbeddedRedisConfiguration {

	@Value("${spring.redis.host}")
	private String redisHost;
	
	@Value("${spring.redis.port}")
	private int redisPort;
	
    @Bean
    public JedisConnectionFactory connectionFactory() throws IOException {
    	JedisConnectionFactory connection = new JedisConnectionFactory();
        connection.setPort(redisPort);
        connection.setHostName(redisHost);
        return connection;

    }

}