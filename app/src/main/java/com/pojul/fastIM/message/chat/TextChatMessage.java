package com.pojul.fastIM.message.chat;

public class TextChatMessage extends ChatMessage{
	
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "TextChatMessage [text=" + text + ", chatType=" + chatType + ", chatUid=" + chatUid + ", from=" + from
				+ ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + "]";
	}
	
}
