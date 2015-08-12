package com.tcs.destination;

import java.io.IOException;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import redis.clients.jedis.Protocol;
import redis.embedded.RedisServer;

@Configuration
@EnableRedisHttpSession
public class EmbeddedRedisConfiguration {
//    private static RedisServer redisServer;

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

//        redisServer = new RedisServer(Protocol.DEFAULT_PORT);
//        redisServer.start();
//        connection.setPassword("secret");
    }

//    @PreDestroy
 //   public void destroy() {
   //     redisServer.stop();
    //}
}