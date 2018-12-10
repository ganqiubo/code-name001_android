package com.pojul.fastIM.message.other;

import com.pojul.objectsocket.message.BaseMessage;

public class TagMessClosed extends BaseMessage {

    private String closeMessUid;

    public String getCloseMessUid() {
        return closeMessUid;
    }

    public void setCloseMessUid(String closeMessUid) {
        this.closeMessUid = closeMessUid;
    }
}
