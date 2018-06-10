package com.pojul.fastIM.entity;

import java.sql.ResultSet;

import com.pojul.objectsocket.message.StringFile;

public class Friend extends User {

	private int unreadMessage;

	public Friend() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Friend(int id, String userName, String passwd, String nickName, String registDate, StringFile photo,
			String autograph, int unreadMessage) {
		super(id, userName, passwd, nickName, registDate, photo, autograph);
		// TODO Auto-generated constructor stub
		this.unreadMessage = unreadMessage;
	}

	public int getUnreadMessage() {
		return unreadMessage;
	}

	public void setUnreadMessage(int unreadMessage) {
		this.unreadMessage = unreadMessage;
	}

	@Override
	public String toString() {
		return "Friend [unreadMessage=" + unreadMessage + ", id=" + id + ", userName=" + userName + ", passwd=" + passwd
				+ ", nickName=" + nickName + ", registDate=" + registDate + ", photo=" + photo + ", autograph="
				+ autograph + "]";
	}

	@Override
	public void setBySql(ResultSet rs) {
		// TODO Auto-generated method stub
		super.setBySql(rs);
	}
	
	
}
