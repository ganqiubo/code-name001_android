package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class SuggestReq extends RequestMessage{

    private String content;

    public SuggestReq() {
        super();
        setRequestUrl("SuggestReq");
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
