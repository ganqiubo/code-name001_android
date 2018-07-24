package com.pojul.fastIM.message.response;

import com.google.gson.Gson;
import com.pojul.fastIM.entity.UploadPic;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class UploadPicRecordResp extends ResponseMessage {

    private List<UploadPic> uploadPics;

    public List<UploadPic> getUploadPics() {
        return uploadPics;
    }

    public void setUploadPics(List<UploadPic> uploadPics) {
        this.uploadPics = uploadPics;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
