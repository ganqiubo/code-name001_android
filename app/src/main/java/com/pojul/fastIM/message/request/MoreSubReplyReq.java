package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class MoreSubReplyReq extends RequestMessage{

    private String replyMessUid;
    private long lastMilli;
    private int num;

    public MoreSubReplyReq() {
        super();
        setRequestUrl("MoreSubReplyReq");
    }

    public String getReplyMessUid() {
        return replyMessUid;
    }

    public void setReplyMessUid(String replyMessUid) {
        this.replyMessUid = replyMessUid;
    }

    public long getLastMilli() {
        return lastMilli;
    }

    public void setLastMilli(long lastMilli) {
        this.lastMilli = lastMilli;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
