package com.pojul.fastIM.entity;

import java.sql.ResultSet;

public class Message extends BaseEntity{

	private String messageUid;
	private String messageClass;
	private String messageContent;
	
	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Message(String messageUid, String messageClass, String messageContent) {
		super();
		this.messageUid = messageUid;
		this.messageClass = messageClass;
		this.messageContent = messageContent;
	}

	public String getMessageUid() {
		return messageUid;
	}

	public void setMessageUid(String messageUid) {
		this.messageUid = messageUid;
	}

	public String getMessageClass() {
		return messageClass;
	}

	public void setMessageClass(String messageClass) {
		this.messageClass = messageClass;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	@Override
	public void setBySql(ResultSet rs) {
		// TODO Auto-generated method stub
		super.setBySql(rs);
		if(rs == null) {
			return;
		}
		messageUid = getString(rs, "message_uid");
		messageClass = getString(rs, "message_class");
		messageContent = getString(rs, "message_content");
	}
	
}
