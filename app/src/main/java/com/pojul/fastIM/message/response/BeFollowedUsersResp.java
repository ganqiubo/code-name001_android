package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.ExtendUser;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class BeFollowedUsersResp extends ResponseMessage{

    private List<ExtendUser> extendUsers;

    public List<ExtendUser> getExtendUsers() {
        return extendUsers;
    }

    public void setExtendUsers(List<ExtendUser> extendUsers) {
        this.extendUsers = extendUsers;
    }
}
