package org.fkjava.weixin1;

import org.fkjava.commons.domain.InMessage;
import org.fkjava.commons.service.JsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
public class Weixin1Application {
	//相当于spring的XML配置方式中的<bean>元素
	@Bean
    public RedisTemplate<String,InMessage> inMessageTemplate(@Autowired RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String,InMessage> template =new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		
		template.setValueSerializer(new JsonRedisSerializer());
		
		return template;
    }
	public static void main(String[] args) {
		SpringApplication.run(Weixin1Application.class, args);
	}

}
