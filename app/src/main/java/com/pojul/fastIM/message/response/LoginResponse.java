package com.pojul.fastIM.message.response;

public class LoginResponse extends Response{

	private String chatId;
	
	public LoginResponse(int code, String message, String chatId) {
		super(code, message);
		// TODO Auto-generated constructor stub
		this.chatId = chatId;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	@Override
	public String toString() {
		return "LoginResponse [chatId=" + chatId + ", code=" + code + ", message=" + message + ", from=" + from
				+ ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + "]";
	}

}
