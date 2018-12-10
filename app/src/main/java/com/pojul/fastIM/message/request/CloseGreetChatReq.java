package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class CloseGreetChatReq extends RequestMessage{

    private String fromUserName;
    private String toUserName;

    public CloseGreetChatReq() {
        super();
        setRequestUrl("CloseGreetChatReq");
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}
