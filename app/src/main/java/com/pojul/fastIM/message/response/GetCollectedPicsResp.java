package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.SimpleUploadPic;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetCollectedPicsResp extends ResponseMessage{

    private List<SimpleUploadPic> simpleUploadPics;

    public List<SimpleUploadPic> getSimpleUploadPics() {
        return simpleUploadPics;
    }

    public void setSimpleUploadPics(List<SimpleUploadPic> simpleUploadPics) {
        this.simpleUploadPics = simpleUploadPics;
    }

}
