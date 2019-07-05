package org.fkjava.weixin.menu;

import org.fkjava.commons.config.EventListenerConfig;
import org.fkjava.commons.domain.event.EventInMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("org.fkjava")
@ComponentScan("org.fkjava")
public class SelfMenuApplication implements EventListenerConfig {

	public static void main(String[] args) {
		SpringApplication.run(SelfMenuApplication.class, args);
	}

	@Override
	public void handle(EventInMessage msg) {
		// 目前此模块不处理任何消息，所以这里留空即可
	}
}
