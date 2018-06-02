package com.pojul.fastIM.message.login;

import com.pojul.objectsocket.message.BaseMessage;

public class LoginMessage extends BaseMessage{

	private String userName;
	private String PassWd;
	private String deviceType;

	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWd() {
		return PassWd;
	}
	public void setPassWd(String passWd) {
		PassWd = passWd;
	}
	
}
