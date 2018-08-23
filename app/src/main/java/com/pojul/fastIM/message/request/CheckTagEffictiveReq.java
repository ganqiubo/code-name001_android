package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class CheckTagEffictiveReq extends RequestMessage{

    private String tagMessUid;

    public CheckTagEffictiveReq() {
        super();
        setRequestUrl("CheckTagEffictiveReq");
    }

    public String getTagMessUid() {
        return tagMessUid;
    }

    public void setTagMessUid(String tagMessUid) {
        this.tagMessUid = tagMessUid;
    }
}
