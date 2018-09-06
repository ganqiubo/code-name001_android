package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class PicFilterLabelResp extends ResponseMessage{

    private List<String> labels;

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
