package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetReplysReq extends RequestMessage{

    private String lastMessUid;
    private String replyTagMessUid;
    private int num;

    public GetReplysReq() {
        super();
        setRequestUrl("GetReplysReq");
    }

    public String getLastMessUid() {
        return lastMessUid;
    }

    public void setLastMessUid(String lastMessUid) {
        this.lastMessUid = lastMessUid;
    }

    public String getReplyTagMessUid() {
        return replyTagMessUid;
    }

    public void setReplyTagMessUid(String replyTagMessUid) {
        this.replyTagMessUid = replyTagMessUid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
