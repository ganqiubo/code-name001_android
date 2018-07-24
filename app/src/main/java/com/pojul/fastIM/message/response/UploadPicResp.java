package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

public class UploadPicResp extends ResponseMessage {
	private String uploadTime;

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
}
