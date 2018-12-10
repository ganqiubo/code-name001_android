package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.BaseMessage;

public class ChatMessage extends BaseMessage{

	protected int chatType;
	protected int isRead;
	protected int sendProgress;
	protected String note;

	public ChatMessage() {
		super();
	}

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

	public int getIsRead() {
		return isRead;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}

	public int getSendProgress() {
		return sendProgress;
	}

	public void setSendProgress(int sendProgress) {
		this.sendProgress = sendProgress;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
