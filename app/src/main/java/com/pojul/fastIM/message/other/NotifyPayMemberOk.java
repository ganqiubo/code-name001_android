package com.pojul.fastIM.message.other;

import com.pojul.objectsocket.message.BaseMessage;

public class NotifyPayMemberOk extends BaseMessage{

	private String validDate;

	public String getValidDate() {
		return validDate;
	}

	public void setValidDate(String validDate) {
		this.validDate = validDate;
	}
	
}
