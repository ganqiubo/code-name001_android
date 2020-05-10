package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class EditCommuDetailReq extends RequestMessage{

    private String detail;
    private String roomUid;

    public EditCommuDetailReq() {
        super();
        setRequestUrl("EditCommuDetailReq");
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }
}
