package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.BaseMessage;

public class Response extends BaseMessage{

	protected int code;
	protected String message;
	
	public Response() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Response(int code, String message) {
		super();
		this.code = code;
		this.message = message;
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
				+ sendTime + "]";
	}
	
}
