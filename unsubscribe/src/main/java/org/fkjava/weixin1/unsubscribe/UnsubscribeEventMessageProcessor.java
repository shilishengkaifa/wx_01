package org.fkjava.weixin1.unsubscribe;

import java.util.Date;

import javax.transaction.Transactional;

import org.fkjava.commons.domain.User;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.processors.EventMessageProcessor;
import org.fkjava.commons.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("unsubscribeMessageProcessor")
public class UnsubscribeEventMessageProcessor  implements EventMessageProcessor {
	
	@Autowired
	private UserRepository userRepository;
	
	
	@Override
	@Transactional
	public void onMessage(EventInMessage msg) {
		if(!msg.getEvent().equals("unsubscribe")) {
			return;
		}
		User user = this.userRepository.findByOpenId(msg.getFromUserName());
		if( user != null) {
		user.setStatus(User.Status.IS_UNSUBSCRIBE);
		user.setUnsubTime(new Date());
		}
	}
}
