package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.constant.StorageType;
import com.pojul.objectsocket.message.RequestMessage;
import com.pojul.objectsocket.message.StringFile;

public class UpdateUserPhotoReq extends RequestMessage{

    private StringFile stringFile;
    private int photoType; //0: 头像; 1: 主页图片
    private String rawPhotoName;

    public UpdateUserPhotoReq() {
        super();
        setRequestUrl("UpdateUserPhotoReq");
    }

    public StringFile getStringFile() {
        return stringFile;
    }

    public void setStringFile(StringFile stringFile) {
        this.stringFile = stringFile;
        this.stringFile.setStorageType(StorageType.LOCAL);
    }

    public int getPhotoType() {
        return photoType;
    }

    public void setPhotoType(int photoType) {
        this.photoType = photoType;
    }

    public String getRawPhotoName() {
        return rawPhotoName;
    }

    public void setRawPhotoName(String rawPhotoName) {
        this.rawPhotoName = rawPhotoName;
    }
}
