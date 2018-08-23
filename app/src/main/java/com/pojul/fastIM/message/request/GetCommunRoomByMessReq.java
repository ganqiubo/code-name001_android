package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetCommunRoomByMessReq extends RequestMessage{

    private String communMessUid;

    public GetCommunRoomByMessReq() {
        super();
        setRequestUrl("GetCommunRoomByMessReq");
    }

    public String getCommunMessUid() {
        return communMessUid;
    }

    public void setCommunMessUid(String communMessUid) {
        this.communMessUid = communMessUid;
    }
}
