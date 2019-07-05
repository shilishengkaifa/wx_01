package org.fkjava.commons.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import org.fkjava.commons.domain.ResponseError;
import org.fkjava.commons.domain.ResponseMessage;
import org.fkjava.commons.domain.ResponseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class TokenManagerImpl implements TokenManager {

	private static final Logger LOG = LoggerFactory.getLogger(TokenManagerImpl.class);
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	@Qualifier("tokenRedisTemplate")
	private RedisTemplate<String, ResponseToken> tokenRedisTemplate;

	@Override
	public String getToken(String account) {
		BoundValueOperations<String, ResponseToken> ops = tokenRedisTemplate.boundValueOps("weixin_access_token");
		ResponseToken token = ops.get();
		LOG.trace("获取令牌，结果：{}",token);
		if (token == null) {
			// 增加事物锁
			for (int i = 0; i < 10; i++) {
				Boolean locked = tokenRedisTemplate.opsForValue().setIfAbsent("weixin_access_token_lock",
						new ResponseToken());
				LOG.trace("没有令牌，增加事物锁，结果：{}",locked);
				if (locked == true) {
					// 增加事物鎖成
					try {
                         //再次检查token是否在数据库里面
						token = ops.get();
						if (token == null) {
							LOG.trace("再次检车令牌，还是没有，调用远程接口");
							token = getRemoteToken(account);

							// 把token存儲到redis裡面
							ops.set(token);
							// 設置令牌過期時間
							ops.expire(token.getExpiresIn() - 60, TimeUnit.SECONDS);
						}else {
							LOG.trace("再次检车令牌，已经有令牌在Redis里面，直接使用");
						}
						//不需要继续循环
						break;
					} finally {
						LOG.trace("删除令牌事物锁");
						// 刪除事物鎖
						tokenRedisTemplate.delete("weixin_access_token_lock");
						synchronized (this) {
							//通知其他线程继续执行!
							this.notifyAll();
						}
					}
				} else {
					// 增加事物鎖不成功，要等待一分鐘再重試
					synchronized (this) {
						try {
							LOG.trace("其他线程锁定了令牌，无法获得锁，等待...");
							this.wait(1000 * 60);
						} catch (InterruptedException e) {
							LOG.error("等待獲取分佈式的事物鎖出現問題：" + e.getLocalizedMessage(), e);
							break;
						}
					}
				}
			}
		}
		if(token != null) {
			return token.getAccessToken();
		}
		return null;
	}

	public ResponseToken getRemoteToken(String account) {
		String appid = "wx79264174843e1d49";
		String appsecret = "dbc97f8b9dcd289db39047582cfbf51b";

		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" //
				+ "&appid=" + appid //
				+ "&secret=" + appsecret;

		HttpClient hc = HttpClient.newBuilder().version(Version.HTTP_1_1).build();
		HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();

		ResponseMessage rm;
		try {
			HttpResponse<String> response = hc.send(request, BodyHandlers.ofString(Charset.forName("UTF-8")));

			String body = response.body();

			LOG.trace("调用远程接口返回的内容 ：  \n{}", body);

			if (body.contains("errcode")) {
				// 错误
				rm = objectMapper.readValue(body, ResponseError.class);
				rm.setStatus(2);
			} else {
				// 成功
				rm = objectMapper.readValue(body, ResponseToken.class);
				rm.setStatus(1);
			}
			if (rm.getStatus() == 1) {
				// return ((ResponseToken) rm).getAccessToken();
				return ((ResponseToken) rm);
			}
		} catch (Exception e) {
			throw new RuntimeException("无法获取令牌,因为:" + e.getLocalizedMessage());
		}

		throw new RuntimeException("无法获取令牌,因为:错误代码=" //
				+ ((ResponseError) rm).getErrorCode()//
				+ "错误描述=" + ((ResponseError) rm).getErrorMessage());
	}

}
