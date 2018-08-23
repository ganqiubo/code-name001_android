package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class CloseTagMessReq extends RequestMessage{

    private String tagMessUid;

    public CloseTagMessReq() {
        super();
        setRequestUrl("CloseTagMessReq");
    }

    public String getTagMessUid() {
        return tagMessUid;
    }

    public void setTagMessUid(String tagMessUid) {
        this.tagMessUid = tagMessUid;
    }
}
