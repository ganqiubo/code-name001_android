package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetUserInfoReq extends RequestMessage{

    private long userId = -1;
    private String userName;

    public GetUserInfoReq() {
        super();
        setRequestUrl("GetUserInfoReq");
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
