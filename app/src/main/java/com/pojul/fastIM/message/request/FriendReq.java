package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.AddFriend;
import com.pojul.objectsocket.message.RequestMessage;

public class FriendReq extends RequestMessage{

    private AddFriend addFriend;

    public FriendReq() {
        super();
        setRequestUrl("FriendReq");
    }

    public AddFriend getAddFriend() {
        return addFriend;
    }

    public void setAddFriend(AddFriend addFriend) {
        this.addFriend = addFriend;
    }
}
