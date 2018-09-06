package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.UploadPic;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class UserUploadPicResp extends ResponseMessage{

    private List<UploadPic> uploadPics;

    public List<UploadPic> getUploadPics() {
        return uploadPics;
    }

    public void setUploadPics(List<UploadPic> uploadPics) {
        this.uploadPics = uploadPics;
    }
}
