package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

public class CheckTagEffictiveResp extends ResponseMessage{

    private int effictive;

    public int getEffictive() {
        return effictive;
    }

    public void setEffictive(int effictive) {
        this.effictive = effictive;
    }
}
