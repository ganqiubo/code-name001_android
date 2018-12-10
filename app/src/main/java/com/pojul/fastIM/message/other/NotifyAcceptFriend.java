package com.pojul.fastIM.message.other;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.BaseMessage;

public class NotifyAcceptFriend extends BaseMessage{

	private String reqUserName;
	private String reqedUserName;
	private User reqedUserInfo;
	private int type;
	private String reqText;
	
	public String getReqUserName() {
		return reqUserName;
	}
	public void setReqUserName(String reqUserName) {
		this.reqUserName = reqUserName;
	}
	public String getReqedUserName() {
		return reqedUserName;
	}
	public void setReqedUserName(String reqedUserName) {
		this.reqedUserName = reqedUserName;
	}
	public User getReqedUserInfo() {
		return reqedUserInfo;
	}
	public void setReqedUserInfo(User reqedUserInfo) {
		this.reqedUserInfo = reqedUserInfo;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}

	public String getReqText() {
		return reqText;
	}

	public void setReqText(String reqText) {
		this.reqText = reqText;
	}
}
