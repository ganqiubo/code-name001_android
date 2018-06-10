package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.Conversation;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetConversionInfoResponse extends ResponseMessage{

	private Conversation conversation;

	public GetConversionInfoResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GetConversionInfoResponse(int code, String message, String messageUid, Conversation conversation) {
		super(code, message, messageUid);
		// TODO Auto-generated constructor stub
		this.conversation = conversation;
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	@Override
	public String toString() {
		return "GetConversionInfoResponse [conversation=" + conversation + ", code=" + code + ", message=" + message
				+ ", from=" + from + ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime
				+ ", MessageUid=" + MessageUid + "]";
	} 
	
}
