package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetTagMessLabelsResp extends ResponseMessage{

    private String tagMessLabels;

    public String getTagMessLabels() {
        return tagMessLabels;
    }

    public void setTagMessLabels(String tagMessLabels) {
        this.tagMessLabels = tagMessLabels;
    }

}
