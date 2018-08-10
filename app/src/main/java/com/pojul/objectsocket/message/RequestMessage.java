package com.pojul.objectsocket.message;

import tl.pojul.com.fastim.util.SPUtil;

public class RequestMessage extends BaseMessage{

	private String requestUrl;

	public RequestMessage() {
		super();
		// TODO Auto-generated constructor stub
		/**
		 * for android method
		 * */
		if(SPUtil.getInstance().getUser() != null){
			setFrom(SPUtil.getInstance().getUser().getUserName());
		}
	}

	public RequestMessage(String requestUrl) {
		super();
		this.requestUrl = requestUrl;
		if(SPUtil.getInstance().getUser() != null){
			setFrom(SPUtil.getInstance().getUser().getUserName());
		}
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	
}
