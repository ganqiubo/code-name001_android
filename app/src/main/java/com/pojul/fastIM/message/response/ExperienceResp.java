package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

public class ExperienceResp extends ResponseMessage{

    private String validTime;

    public String getValidTime() {
        return validTime;
    }

    public void setValidTime(String validTime) {
        this.validTime = validTime;
    }
}
