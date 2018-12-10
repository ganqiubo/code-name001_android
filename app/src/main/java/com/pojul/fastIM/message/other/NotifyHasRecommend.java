package com.pojul.fastIM.message.other;

import com.pojul.objectsocket.message.BaseMessage;

public class NotifyHasRecommend extends BaseMessage{

	private String toUserName;
	private int recommendtype; //1: 推荐消息;2: 推荐附近的人
	
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public int getRecommendtype() {
		return recommendtype;
	}
	public void setRecommendtype(int recommendtype) {
		this.recommendtype = recommendtype;
	}
	
}
