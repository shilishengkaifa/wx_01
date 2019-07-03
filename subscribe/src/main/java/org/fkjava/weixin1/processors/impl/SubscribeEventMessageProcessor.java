package org.fkjava.weixin1.processors.impl;

import org.fkjava.commons.domain.User;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.processors.EventMessageProcessor;
import org.fkjava.commons.repository.UserRepository;
import org.fkjava.weixin1.service.WeixinProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("subscribeMessageProcessor")
public class SubscribeEventMessageProcessor  implements EventMessageProcessor {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WeixinProxy weixinProxy;
	
	@Override
	public void onMessage(EventInMessage msg) {
		if(!msg.getEvent().equals("subscribe")) {
			return;
		}
		//发生操作用户是否已经关注
		String openId = msg.getFromUserName();
		//检查用户是否已经关注
		User user = this.userRepository.findByOpenId(openId);
		//如果用户未关注，则调用远程接口获取用户信息
		if(user == null || user.getStatus() != User.Status.IS_SUBSCRIBE) {
	        //调用远程接口
			// TODO 根据ToUserName找到对应的微信号
			String account = "";
			User wx1User = weixinProxy.getUser(account, openId);
			if(wx1User == null) {
				return;
			}
			//存储到数据库
			if(user != null) {
				wx1User.setId(user.getId());
				wx1User.setSubTime(user.getSubTime());
				wx1User.setUnsubTime(null);
			}
			wx1User.setStatus(User.Status.IS_SUBSCRIBE);
			this.userRepository.save(wx1User);
			
			//通过客服接口，发送一条信息给用户
			weixinProxy.sendText(account, openId, "欢迎关注我的公众号，恢复帮助可获得人工智能菜单");
		}
	}
}
