package org.fkjava.commons.domain.text;

import org.fkjava.commons.domain.OutMessage;

public class TextOutMessage extends OutMessage {

	private static class TextContent{
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
	
	private TextContent text;
	
	public TextOutMessage(String toUser,String content) {
		super.setMessageType("text");
		super.setToUser(toUser);
		this.text = new TextContent();
		this.text.content = content;
	}

	public TextContent getText() {
		return text;
	}

	public void setText(TextContent text) {
		this.text = text;
	}
}
