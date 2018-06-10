package com.pojul.objectsocket.message;

public class RequestMessage extends BaseMessage{

	private String requestUrl;

	public RequestMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RequestMessage(String requestUrl) {
		super();
		this.requestUrl = requestUrl;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
}
