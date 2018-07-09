package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.ResponseMessage;

public class LoginResponse extends ResponseMessage{

	private String chatId;
	private User user;
	private String tokenId;
	
	public LoginResponse(int code, String message, String chatId, String messageUid, User user, String tokenId) {
		super(code, message, messageUid);
		// TODO Auto-generated constructor stub
		this.chatId = chatId;
		this.user = user;
		this.tokenId = tokenId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	@Override
	public String toString() {
		return "LoginResponse [chatId=" + chatId + ", user=" + user + ", code=" + code + ", message=" + message
				+ ", from=" + from + ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime
				+ ", MessageUid=" + MessageUid + "]";
	}

	

}
