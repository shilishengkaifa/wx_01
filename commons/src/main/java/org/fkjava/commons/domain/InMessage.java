package org.fkjava.commons.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlAccessorType(XmlAccessType.FIELD)//JAXB从字段获取配置信息
@XmlRootElement(name ="xml")//JAXB读取XML时根元素名称
public abstract class InMessage  implements Serializable {
	
	
	private static final long serialVersionUID =1L;
	 //由于微信发送给我们的信息字段但是首字母大写，而java字段首字母大写不符合javaBean的规范
	@XmlElement(name= "ToUserName")
	@JsonProperty("ToUserName")
      private String toUserName;
	
	@XmlElement(name= "FromUserName")
	@JsonProperty("FromUserName")
      private String fromUserName;
	
	@XmlElement(name= "CreateTime")
	@JsonProperty("CreateTime")
      private long createTime;
	
	@XmlElement(name= "MsqType")
	@JsonProperty("MsqType")
      private String msqType;
	
	@XmlElement(name= "MsqId")
	@JsonProperty("MsqId")
      private Long msqId;

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getMsqType() {
		return msqType;
	}

	public void setMsqType(String msqType) {
		this.msqType = msqType;
	}

	public Long getMsqId() {
		return msqId;
	}

	public void setMsqId(Long msqId) {
		this.msqId = msqId;
	}
}
