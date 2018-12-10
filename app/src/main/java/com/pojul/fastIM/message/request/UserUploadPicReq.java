package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class UserUploadPicReq extends RequestMessage{

    private long userId;
    private int num;
    //private long lastUploadPicId;
    private long startNum;

    public UserUploadPicReq() {
        super();
        setRequestUrl("UserUploadPicReq");
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }
}
