package org.fkjava.weixin1.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;

import org.fkjava.commons.domain.ResponseError;
import org.fkjava.commons.domain.ResponseMessage;
import org.fkjava.commons.domain.ResponseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TokenManagerImpl implements TokenManager  {
    
	private static final Logger LOG = LoggerFactory.getLogger(WeixinProxy.class);
	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public String getToken(String account) {
		String appid ="wx79264174843e1d49";
		String appsecret = "dbc97f8b9dcd289db39047582cfbf51b";
		
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" //
				+ "&appid="+ appid //
				+ "&secret="+ appsecret ;
		
		HttpClient hc=HttpClient.newBuilder()
				.version(Version.HTTP_1_1)
				.build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url))
				.GET()
				.build();
		
		ResponseMessage rm;
		try {
			HttpResponse<String> response = hc.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));
			
			String body = response.body();
			
			LOG.trace("调用远程接口返回的内容 ：  \n{}",body);
			
			if(body.contains("errcode")) {
				//错误
				rm = objectMapper.readValue(body,ResponseError.class);
				rm.setStatus(2);
			}else {
				//成功
				rm = objectMapper.readValue(body,ResponseToken.class);
				rm.setStatus(1);
			}
			if(rm.getStatus()==1) {
				return ((ResponseToken)rm).getAccessToken();
			}
		} catch (Exception e) {
			throw new RuntimeException("无法获取令牌,因为:"+ e.getLocalizedMessage());
		}
		
		throw new RuntimeException("无法获取令牌,因为:错误代码=" //
		+((ResponseError)rm).getErrorCode()//
		 +"错误描述="+((ResponseError)rm).getErrorMessage());
	}

}
