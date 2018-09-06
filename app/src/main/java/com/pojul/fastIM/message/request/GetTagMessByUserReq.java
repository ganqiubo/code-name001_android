package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetTagMessByUserReq extends RequestMessage{

    private String userName;
    private int num;
    private long lastTagMessid = -1;

    public GetTagMessByUserReq() {
        super();
        setRequestUrl("GetTagMessByUserReq");
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getLastTagMessid() {
        return lastTagMessid;
    }

    public void setLastTagMessid(long lastTagMessid) {
        this.lastTagMessid = lastTagMessid;
    }
}
