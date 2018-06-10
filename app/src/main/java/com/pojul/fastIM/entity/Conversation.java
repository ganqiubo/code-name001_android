package com.pojul.fastIM.entity;

import android.database.Cursor;

import java.sql.ResultSet;

//local table
public class Conversation extends BaseEntity {

    private String conversationName;
    private String conversationFrom;
    private String conversationPhoto;
    private String conversationLastChat;
    private String conversationLastChattime;
    private String conversationOwner;
	private int unreadMessage;
	
	public Conversation() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Conversation(String conversationName, String conversationFrom, String conversationPhoto,
			String conversationLastChat, String conversationLastChattime, String conversationOwner, int unreadMessage) {
		super();
		this.conversationName = conversationName;
		this.conversationFrom = conversationFrom;
		this.conversationPhoto = conversationPhoto;
		this.conversationLastChat = conversationLastChat;
		this.conversationLastChattime = conversationLastChattime;
		this.conversationOwner = conversationOwner;
		this.unreadMessage = unreadMessage;
	}
	public String getConversationName() {
		return conversationName;
	}
	public void setConversationName(String conversationName) {
		this.conversationName = conversationName;
	}
	public String getConversationFrom() {
		return conversationFrom;
	}
	public void setConversationFrom(String conversationFrom) {
		this.conversationFrom = conversationFrom;
	}
	public String getConversationPhoto() {
		return conversationPhoto;
	}
	public void setConversationPhoto(String conversationPhoto) {
		this.conversationPhoto = conversationPhoto;
	}
	public String getConversationLastChat() {
		return conversationLastChat;
	}
	public void setConversationLastChat(String conversationLastChat) {
		this.conversationLastChat = conversationLastChat;
	}
	public String getConversationLastChattime() {
		return conversationLastChattime;
	}
	public void setConversationLastChattime(String conversationLastChattime) {
		this.conversationLastChattime = conversationLastChattime;
	}
	public String getConversationOwner() {
		return conversationOwner;
	}
	public void setConversationOwner(String conversationOwner) {
		this.conversationOwner = conversationOwner;
	}

	public int getUnreadMessage() {
		return unreadMessage;
	}

	public void setUnreadMessage(int unreadMessage) {
		this.unreadMessage = unreadMessage;
	}

	@Override
	public String toString() {
		return "Conversation{" +
				"conversationName='" + conversationName + '\'' +
				", conversationFrom='" + conversationFrom + '\'' +
				", conversationPhoto='" + conversationPhoto + '\'' +
				", conversationLastChat='" + conversationLastChat + '\'' +
				", conversationLastChattime='" + conversationLastChattime + '\'' +
				", conversationOwner='" + conversationOwner + '\'' +
				", unreadMessage=" + unreadMessage +
				'}';
	}

	@Override
	public void setBySql(Cursor cursor) {
		// TODO Auto-generated method stub
		super.setBySql(cursor);
		if(cursor == null) {
			return;
		}
		conversationName = getString(cursor, "conversation_name");
		conversationFrom = getString(cursor, "conversation_from");
		conversationPhoto = getString(cursor, "conversation_photo");
		conversationLastChat = getString(cursor, "conversation_last_chat");
		conversationLastChattime = getString(cursor, "conversation_last_chattime");
		conversationOwner = getString(cursor, "conversation_owner");
		unreadMessage = getInt(cursor, "unread_message");
	}
	
}
