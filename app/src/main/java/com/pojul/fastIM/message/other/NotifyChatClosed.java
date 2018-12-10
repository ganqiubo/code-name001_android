package com.pojul.fastIM.message.other;

import com.pojul.objectsocket.message.BaseMessage;

public class NotifyChatClosed extends BaseMessage{

    private String chatUid;

    public String getChatUid() {
        return chatUid;
    }

    public void setChatUid(String chatUid) {
        this.chatUid = chatUid;
    }
    
}
