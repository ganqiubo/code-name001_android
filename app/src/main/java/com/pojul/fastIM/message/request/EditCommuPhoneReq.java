package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class EditCommuPhoneReq extends RequestMessage{

    private String phone;
    private String roomUid;

    public EditCommuPhoneReq() {
        super();
        setRequestUrl("EditCommuPhoneReq");
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }
}
