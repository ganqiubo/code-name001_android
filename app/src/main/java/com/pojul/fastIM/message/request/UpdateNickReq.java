package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class UpdateNickReq extends RequestMessage{

    private String nickName;

    public UpdateNickReq() {
        super();
        setRequestUrl("UpdateNickReq");
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
