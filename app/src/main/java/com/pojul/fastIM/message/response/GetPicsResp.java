package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.ExtendUploadPic;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetPicsResp extends ResponseMessage{

    private List<ExtendUploadPic> uploadPics;

    public List<ExtendUploadPic> getUploadPics() {
        return uploadPics;
    }

    public void setUploadPics(List<ExtendUploadPic> uploadPics) {
        this.uploadPics = uploadPics;
    }
}
