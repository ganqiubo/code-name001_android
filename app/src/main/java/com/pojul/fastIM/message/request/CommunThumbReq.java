package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class CommunThumbReq extends RequestMessage{

    private String CommunMessageUid;

    public CommunThumbReq() {
        super();
        setRequestUrl("CommunThumbReq");
    }

    public String getCommunMessageUid() {
        return CommunMessageUid;
    }

    public void setCommunMessageUid(String communMessageUid) {
        CommunMessageUid = communMessageUid;
    }
}
