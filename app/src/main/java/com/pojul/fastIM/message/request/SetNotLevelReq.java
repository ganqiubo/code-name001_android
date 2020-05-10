package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class SetNotLevelReq extends RequestMessage{

    private int level;
    private String roomUid;

    public SetNotLevelReq() {
        super();
        setRequestUrl("SetNotLevelReq");
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }
}
