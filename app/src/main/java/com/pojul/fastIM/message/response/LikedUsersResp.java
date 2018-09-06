package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.ExtendUser;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class LikedUsersResp extends ResponseMessage{

    private List<ExtendUser> extendUsers;
    private int beLikedCount = -1;

    public List<ExtendUser> getExtendUsers() {
        return extendUsers;
    }

    public void setExtendUsers(List<ExtendUser> extendUsers) {
        this.extendUsers = extendUsers;
    }

    public int getBeLikedCount() {
        return beLikedCount;
    }

    public void setBeLikedCount(int beLikedCount) {
        this.beLikedCount = beLikedCount;
    }
}
