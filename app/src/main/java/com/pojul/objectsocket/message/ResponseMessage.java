package com.pojul.objectsocket.message;

public class ResponseMessage extends BaseMessage{

	protected int code;
	protected String message;

	public ResponseMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseMessage(int code, String message, String messageUid) {
		super();
		this.code = code;
		this.message = message;
		super.MessageUid = messageUid;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Response [code=" + code + ", message=" + message + ", from=" + from + ", to=" + to + ", sendTime="
				+ sendTime + ", receiveTime=" + receiveTime + ", MessageUid=" + MessageUid + "]";
	}
	
}
