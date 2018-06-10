package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetFriendsRequest extends RequestMessage{

	private String userName;

	public GetFriendsRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetFriendsRequest(String userName) {
		super();
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
