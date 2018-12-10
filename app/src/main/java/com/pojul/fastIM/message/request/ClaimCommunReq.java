package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class ClaimCommunReq extends RequestMessage{

    private String roomUid;
    private long milli;

    public ClaimCommunReq() {
        super();
        setRequestUrl("ClaimCommunReq");
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }

    public long getMilli() {
        return milli;
    }

    public void setMilli(long milli) {
        this.milli = milli;
    }
}
