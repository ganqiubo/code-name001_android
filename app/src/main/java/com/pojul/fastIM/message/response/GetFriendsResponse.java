package com.pojul.fastIM.message.response;

import java.util.List;

import com.pojul.fastIM.entity.Friend;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetFriendsResponse extends ResponseMessage{

	private List<Friend> friends;

	public GetFriendsResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetFriendsResponse(int code, String message, String messageUid) {
		super(code, message, messageUid);
		// TODO Auto-generated constructor stub
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}
	
}
