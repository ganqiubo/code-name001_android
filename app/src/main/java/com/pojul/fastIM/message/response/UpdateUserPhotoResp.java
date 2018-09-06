package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

public class UpdateUserPhotoResp extends ResponseMessage{

    private String photoPath;

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
