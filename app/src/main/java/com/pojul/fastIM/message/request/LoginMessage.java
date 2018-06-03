package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class LoginMessage extends RequestMessage{

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
		setFrom(userName);
	}
	public String getPassWd() {
		return PassWd;
	}
	public void setPassWd(String passWd) {
		PassWd = passWd;
	}
	
}
