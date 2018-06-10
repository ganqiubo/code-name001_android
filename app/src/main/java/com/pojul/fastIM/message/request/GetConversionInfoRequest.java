package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetConversionInfoRequest extends RequestMessage{

    private int chatRoomType;
    private String conversionFrom;
    private String owner;
	public GetConversionInfoRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int getChatRoomType() {
		return chatRoomType;
	}
	public void setChatRoomType(int chatRoomType) {
		this.chatRoomType = chatRoomType;
	}
	public String getConversionFrom() {
		return conversionFrom;
	}
	public void setConversionFrom(String conversionFrom) {
		this.conversionFrom = conversionFrom;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	@Override
	public String toString() {
		return "GetConversionInfoRequest [chatRoomType=" + chatRoomType + ", conversionFrom=" + conversionFrom
				+ ", owner=" + owner + ", from=" + from + ", to=" + to + ", sendTime=" + sendTime + ", receiveTime="
				+ receiveTime + ", MessageUid=" + MessageUid + "]";
	}


}
