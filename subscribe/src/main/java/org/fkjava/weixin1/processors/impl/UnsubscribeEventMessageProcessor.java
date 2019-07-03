package org.fkjava.weixin1.processors.impl;

import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.processors.EventMessageProcessor;
import org.springframework.stereotype.Service;

@Service("unsubscribeMessageProcessor")
public class UnsubscribeEventMessageProcessor  implements EventMessageProcessor {

	@Override
	public void onMessage(EventInMessage msg) {
		
	}

}
