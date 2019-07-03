package org.fkjava.weixin1.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.fkjava.commons.domain.InMessage;
import org.fkjava.commons.domain.event.EventInMessage;
import org.fkjava.commons.domain.image.ImageInMessage;
import org.fkjava.commons.domain.link.LinkInMessage;
import org.fkjava.commons.domain.location.LocationInMessage;
import org.fkjava.commons.domain.shortvideo.ShortvideoInMessage;
import org.fkjava.commons.domain.text.TextInMessage;
import org.fkjava.commons.domain.video.VideoInMessage;
import org.fkjava.commons.domain.voice.VoiceInMessage;

public class MessageTypeMapper {
   
	private static Map<String,Class<? extends InMessage>> typeMap =new ConcurrentHashMap<>();
	static {
		typeMap.put("text",TextInMessage.class);
		typeMap.put("image",ImageInMessage.class);
		typeMap.put("link",LinkInMessage.class);
		typeMap.put("location",LocationInMessage.class);
		typeMap.put("shortvideo",ShortvideoInMessage.class);
		typeMap.put("video",VideoInMessage.class);
		typeMap.put("voice",VoiceInMessage.class);
		
		typeMap.put("event",EventInMessage.class);
	}
	@SuppressWarnings("unchecked")
	public static <T extends InMessage>Class<T> getClass(String type){
		return (Class<T>) typeMap.get(type);
	}
}
