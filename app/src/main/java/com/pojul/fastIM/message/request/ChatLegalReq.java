package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class ChatLegalReq extends RequestMessage{

    private String userNameOwn;
    private String userNameFriend;

    public ChatLegalReq() {
        super();
        setRequestUrl("ChatLegalReq");
    }

    public String getUserNameOwn() {
        return userNameOwn;
    }

    public void setUserNameOwn(String userNameOwn) {
        this.userNameOwn = userNameOwn;
    }

    public String getUserNameFriend() {
        return userNameFriend;
    }

    public void setUserNameFriend(String userNameFriend) {
        this.userNameFriend = userNameFriend;
    }
}
