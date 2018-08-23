package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class ReplyMessReq extends RequestMessage{

    private String replyMessUid;
    private int reqCode = 1; //1: 请求消息; 2: 取消请求消息

    public ReplyMessReq() {
        super();
        setRequestUrl("ReplyMessReq");
    }

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    public String getReplyMessUid() {
        return replyMessUid;
    }

    public void setReplyMessUid(String replyMessUid) {
        this.replyMessUid = replyMessUid;
    }
}
