package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetTagMessByUidReq extends RequestMessage{

    private String tagMessUid;

    public GetTagMessByUidReq() {
        super();
        setRequestUrl("GetTagMessByUidReq");
    }

    public String getTagMessUid() {
        return tagMessUid;
    }

    public void setTagMessUid(String tagMessUid) {
        this.tagMessUid = tagMessUid;
    }
}
