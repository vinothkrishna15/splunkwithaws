package com.tcs.destination;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.ExpiringSession;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class EmbeddedRedisConfiguration {

	 @Value("${spring.redis.host}")
	private String redisHost;
	
	@Value("${spring.redis.port}")
	private int redisPort;
	
	@Value("${server.session-timeout}")
	private int maxInactiveIntervalInSeconds;
	
    @Bean
    public JedisConnectionFactory connectionFactory() throws IOException {
   	JedisConnectionFactory connection = new JedisConnectionFactory();
        connection.setPort(redisPort);
        connection.setHostName(redisHost);
        return connection;

    }
    
    @Primary
    @Bean
    public RedisOperationsSessionRepository sessionRepository(RedisTemplate<String, ExpiringSession> sessionRedisTemplate) {
        RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(sessionRedisTemplate);

        sessionRepository.setDefaultMaxInactiveInterval(maxInactiveIntervalInSeconds);
        
        return sessionRepository;
    }
   
    @Bean
    RedisTemplate<Object, Object> redisTemplate() throws IOException {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(connectionFactory());
        return redisTemplate;
    }
    
    @Primary
    @Bean (name="PrimaryCacheManager")
    CacheManager cacheManager() throws IOException {
    	RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate());
    	cacheManager.setDefaultExpiration(60 * 60 * 24);
    	cacheManager.setUsePrefix(true);
    	
    	return cacheManager;
        
    }
    
}