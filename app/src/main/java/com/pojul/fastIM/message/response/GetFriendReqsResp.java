package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.AddFriend;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetFriendReqsResp extends ResponseMessage{

    private List<AddFriend> addFriends;

    public List<AddFriend> getAddFriends() {
        return addFriends;
    }

    public void setAddFriends(List<AddFriend> addFriends) {
        this.addFriends = addFriends;
    }
}
