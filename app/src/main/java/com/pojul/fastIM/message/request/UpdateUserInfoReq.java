package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.RequestMessage;

public class UpdateUserInfoReq extends RequestMessage{

    private User user;

    public UpdateUserInfoReq() {
        super();
        setRequestUrl("UpdateUserInfoReq");
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
