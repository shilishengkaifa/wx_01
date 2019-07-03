package org.fkjava.commons.domain.text;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.fkjava.commons.domain.InMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name ="xml")
public class TextInMessage extends InMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(name="Content")
	@JsonProperty("Content")
	private String content;
	
	 public TextInMessage() {
		 super.setMsqType("text"); 
	 }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
   //如果要打印一些有含义的信息出来，必须重写此方法，否则只能打印一个内存地址
	@Override
	public String toString() {
		return "TextInMessage [content=" + content + ", getToUserName()=" + getToUserName() + ", getFromUserName()="
				+ getFromUserName() + ", getCreateTime()=" + getCreateTime() + ", getMsqType()=" + getMsqType()
				+ ", getMsqId()=" + getMsqId() + "]";
	}
}
