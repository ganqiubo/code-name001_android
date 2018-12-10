package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetUsersByNameResp extends ResponseMessage{

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
