package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetUserInfoResp extends ResponseMessage{

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
