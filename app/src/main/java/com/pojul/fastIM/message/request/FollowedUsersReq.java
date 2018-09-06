package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class FollowedUsersReq extends RequestMessage{

    private String userName;
    private int num;
    private long lastFollowedId;

    public FollowedUsersReq() {
        super();
        setRequestUrl("FollowedUsersReq");
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

    public long getLastFollowedId() {
        return lastFollowedId;
    }

    public void setLastFollowedId(long lastFollowedId) {
        this.lastFollowedId = lastFollowedId;
    }
}
