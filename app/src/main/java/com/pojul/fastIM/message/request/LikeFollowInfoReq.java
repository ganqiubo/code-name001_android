package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class LikeFollowInfoReq extends RequestMessage{

    private String beUserName;

    public LikeFollowInfoReq() {
        super();
        setRequestUrl("LikeFollowInfoReq");
    }

    public String getBeUserName() {
        return beUserName;
    }

    public void setBeUserName(String beUserName) {
        this.beUserName = beUserName;
    }
}
