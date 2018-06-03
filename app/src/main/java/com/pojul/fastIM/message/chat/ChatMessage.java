package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.BaseMessage;

public class ChatMessage extends BaseMessage{

	protected int chatType;

	@Override
	public void setFrom(String from) {
		// TODO Auto-generated method stub
		super.setFrom(from);
	}

	public int getChatType() {
		return chatType;
	}

	public void setChatType(int chatType) {
		this.chatType = chatType;
	}
	
}
