package com.pojul.fastIM.message.other;

import com.pojul.fastIM.entity.AddFriend;
import com.pojul.objectsocket.message.BaseMessage;

public class NotifyFriendReq extends BaseMessage{

    private AddFriend addFriend;

    public AddFriend getAddFriend() {
        return addFriend;
    }

    public void setAddFriend(AddFriend addFriend) {
        this.addFriend = addFriend;
    }
}
