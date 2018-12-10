package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.LocUser;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetNearByPeopleResp extends ResponseMessage{

    private List<LocUser> locUsers;

    public List<LocUser> getLocUsers() {
        return locUsers;
    }

    public void setLocUsers(List<LocUser> locUsers) {
        this.locUsers = locUsers;
    }
}
