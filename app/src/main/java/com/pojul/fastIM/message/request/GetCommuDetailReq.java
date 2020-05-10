package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetCommuDetailReq extends RequestMessage{

    private String commuUid;

    public GetCommuDetailReq() {
        super();
        setRequestUrl("GetCommuDetailReq");
    }

    public String getCommuUid() {
        return commuUid;
    }

    public void setCommuUid(String commuUid) {
        this.commuUid = commuUid;
    }
}
