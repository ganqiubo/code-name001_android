package com.pojul.fastIM.message.other;

import com.pojul.objectsocket.message.BaseMessage;

public class NotifyManagerNotify extends BaseMessage{

	private long id;
	private String manager;
	private String managerNickname;
	private String commuRoomUid;
	private String messUid;
	private String messageTitle;
	private long timeMill;
	private String userName;
	private String managerPhoto;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getManagerNickname() {
		return managerNickname;
	}
	public void setManagerNickname(String managerNickname) {
		this.managerNickname = managerNickname;
	}
	public String getCommuRoomUid() {
		return commuRoomUid;
	}
	public void setCommuRoomUid(String commuRoomUid) {
		this.commuRoomUid = commuRoomUid;
	}
	public String getMessUid() {
		return messUid;
	}
	public void setMessUid(String messUid) {
		this.messUid = messUid;
	}
	public String getMessageTitle() {
		return messageTitle;
	}
	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}
	public long getTimeMill() {
		return timeMill;
	}
	public void setTimeMill(long timeMill) {
		this.timeMill = timeMill;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getManagerPhoto() {
		return managerPhoto;
	}

	public void setManagerPhoto(String managerPhoto) {
		this.managerPhoto = managerPhoto;
	}
}
