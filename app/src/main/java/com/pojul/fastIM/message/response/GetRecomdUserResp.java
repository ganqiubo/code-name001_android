package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.LocUser;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetRecomdUserResp extends ResponseMessage{

    private List<LocUser> recomdUsers;

    public List<LocUser> getRecomdUsers() {
        return recomdUsers;
    }

    public void setRecomdUsers(List<LocUser> recomdUsers) {
        this.recomdUsers = recomdUsers;
    }
}
