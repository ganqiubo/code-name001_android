package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class HistoryChatReq extends RequestMessage{

	private String roomUid;
    private String lastSendTime;
    private int num;

    public HistoryChatReq() {
        super();
        setRequestUrl("HistoryChatReq");
    }

    public HistoryChatReq(String lastSendTime, String roomUid, int num) {
        super();
        this.lastSendTime = lastSendTime;
        this.num = num;
        this.roomUid = roomUid;
        setRequestUrl("HistoryChatReq");
    }

    public String getLastSendTime() {
		return lastSendTime;
	}

	public void setLastSendTime(String lastSendTime) {
		this.lastSendTime = lastSendTime;
	}

	public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

	public String getRoomUid() {
		return roomUid;
	}

	public void setRoomUid(String roomUid) {
		this.roomUid = roomUid;
	}
    
}
