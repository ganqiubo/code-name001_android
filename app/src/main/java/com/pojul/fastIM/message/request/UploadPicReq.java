package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.UploadPic;
import com.pojul.objectsocket.message.RequestMessage;

public class UploadPicReq extends RequestMessage {

    private UploadPic uploadPic;
    private String userName;

    public UploadPicReq() {
        super();
        setRequestUrl("UploadPicReq");
    }

    public UploadPic getUploadPic() {
        return uploadPic;
    }

    public void setUploadPic(UploadPic uploadPic) {
        this.uploadPic = uploadPic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
