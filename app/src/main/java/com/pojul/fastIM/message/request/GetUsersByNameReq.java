package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

import java.util.List;

public class GetUsersByNameReq extends RequestMessage{

    private List<String> userNames;

    public GetUsersByNameReq() {
        super();
        setRequestUrl("GetUsersByNameReq");
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }
}
