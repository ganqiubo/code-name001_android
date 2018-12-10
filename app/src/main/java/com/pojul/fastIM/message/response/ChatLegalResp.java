package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.ResponseMessage;

public class ChatLegalResp extends ResponseMessage{

    private User user;
    private int legal; //1:好友; 2: 临时会话; 3: 不合法

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLegal() {
        return legal;
    }

    public void setLegal(int legal) {
        this.legal = legal;
    }
}
