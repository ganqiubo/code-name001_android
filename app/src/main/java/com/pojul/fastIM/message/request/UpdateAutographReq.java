package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class UpdateAutographReq extends RequestMessage{

    private String autograph;

    public UpdateAutographReq() {
        super();
        setRequestUrl("UpdateAutographReq");
    }

    public String getAutograph() {
        return autograph;
    }

    public void setAutograph(String autograph) {
        this.autograph = autograph;
    }
}
