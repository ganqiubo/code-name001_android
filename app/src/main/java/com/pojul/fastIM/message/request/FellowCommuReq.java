package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class FellowCommuReq extends RequestMessage{

    private String roomUid;
    private int mode; //0: follow; -1: cancel follow

    public FellowCommuReq() {
        super();
        setRequestUrl("FellowCommuReq");
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
