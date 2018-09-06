package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.ExtendUser;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class FollowedUsersResp extends ResponseMessage{

    private List<ExtendUser> extendUsers;
    private int beFollowedCount = -1;

    public List<ExtendUser> getExtendUsers() {
        return extendUsers;
    }

    public void setExtendUsers(List<ExtendUser> extendUsers) {
        this.extendUsers = extendUsers;
    }

    public int getBeFollowedCount() {
        return beFollowedCount;
    }

    public void setBeFollowedCount(int beFollowedCount) {
        this.beFollowedCount = beFollowedCount;
    }
}
