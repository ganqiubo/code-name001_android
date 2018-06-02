package com.pojul.objectsocket.message;

public class TextMessage extends BaseMessage{

	protected String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "TextMessage [text=" + text + ", from=" + from + ", to=" + to + ", sendTime=" + sendTime
				+ ", receiveTime=" + receiveTime + "]";
	}
}
