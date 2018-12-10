package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class MakeQRCodeReq extends RequestMessage{

    private String communRoomUid;
    private long milli;

    public MakeQRCodeReq() {
        super();
        setRequestUrl("MakeQRCodeReq");
    }

    public String getCommunRoomUid() {
        return communRoomUid;
    }

    public void setCommunRoomUid(String communRoomUid) {
        this.communRoomUid = communRoomUid;
    }

    public long getMilli() {
        return milli;
    }

    public void setMilli(long milli) {
        this.milli = milli;
    }
}
