package org.fkjava.weixin1;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.fkjava.commons.domain.InMessage;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.processors.EventMessageProcessor;
import org.fkjava.commons.service.JsonRedisSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@ComponentScan("org.fkjava")
@EnableJpaRepositories("org.fkjava")
@EntityScan("org.fkjava")
public class SubscribeApplication  implements CommandLineRunner,DisposableBean,ApplicationContextAware {

	private ApplicationContext ctx;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)throws BeansException{
		ctx=applicationContext;
	}
	
	private final Object stopMonitor =new Object();
	@Override
	public void run(String... args) throws Exception {	
		new Thread(() -> { 
			synchronized(stopMonitor) {
				try {
					stopMonitor.wait();
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}) .start();
	}
	
	
	@Override
	public void destroy() throws Exception {
		synchronized (stopMonitor) {
			stopMonitor.notify();
		}
	}
	
	
	
	//相当于spring的XML配置方式中的<bean>元素
	@Bean
    public RedisTemplate<String,InMessage> inMessageTemplate(
    		@Autowired RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String,InMessage> template =new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		template.setValueSerializer(new JsonRedisSerializer());
		
		return template;
    }
	 
	 @Bean
	 public MessageListenerAdapter messageListener(@Autowired RedisTemplate<String,InMessage> inMessageTemplate) {
		 MessageListenerAdapter adapter =new MessageListenerAdapter();
		 adapter.setSerializer(inMessageTemplate.getValueSerializer());
		 
		 adapter.setDelegate(this);
		 adapter.setDefaultListenerMethod("handle");
		 
		 return adapter;
	 }
	
	 @Bean
	 public RedisMessageListenerContainer messageListenerContainer(
			 @Autowired RedisConnectionFactory redisConnectionFactory,
			 @Autowired MessageListener l) {
		 
		 RedisMessageListenerContainer container =new RedisMessageListenerContainer();
		 container.setConnectionFactory(redisConnectionFactory);
		 List<Topic> topics =  new ArrayList<>();
		 topics.add(new ChannelTopic("weixin1_event"));
		 container.addMessageListener(l,topics);
		 
		 return container;
	 }
	
	 
	 private static final Logger LOG = LoggerFactory.getLogger(SubscribeApplication.class);
	 public void handle(EventInMessage msg) {
		 
		 String id =msg.getEvent().toLowerCase()+"MessageProcessor";
		try { 
		      EventMessageProcessor mp=(EventMessageProcessor)ctx.getBean(id);
		      if(mp !=null) {
			      mp.onMessage(msg);
		        }else {
		        	LOG.warn("Bean的ID{}无法调用对应的消息处理器：{}对应的Bean不存在",id,id);
		        }
	         }catch(Exception e) {
		         LOG.warn("Bean的ID{}无法调用对应的消息处理器：{}",id,e.getMessage());
		         LOG.trace(e.getMessage(),e);
	         }
	      }
	 
	 
	 @Bean
	 public ObjectMapper objectMapper() {
		 return new ObjectMapper();
	 }
	 
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(SubscribeApplication.class, args);
		
	}

}
