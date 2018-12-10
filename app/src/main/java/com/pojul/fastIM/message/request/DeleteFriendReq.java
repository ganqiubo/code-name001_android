package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class DeleteFriendReq extends RequestMessage{

    private String owner;
    private String friend;

    public DeleteFriendReq() {
        super();
        setRequestUrl("DeleteFriendReq");
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }
}
