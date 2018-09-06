package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class BeLikedUsersReq extends RequestMessage{

    private String userName;
    private int num;
    private long lastLikedId;

    public BeLikedUsersReq() {
        super();
        setRequestUrl("BeLikedUsersReq");
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

    public long getLastLikedId() {
        return lastLikedId;
    }

    public void setLastLikedId(long lastLikedId) {
        this.lastLikedId = lastLikedId;
    }
}
