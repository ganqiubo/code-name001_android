package com.pojul.fastIM.message.request;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.RequestMessage;

public class UploadPicRecordReq extends RequestMessage {

    private String userName;
    private String lastSendTime;
    private int num = 5;

    public UploadPicRecordReq() {
        super();
        setRequestUrl("UploadPicRecordReq");
    }

    public UploadPicRecordReq(String userName, String lastSendTime, int num) {
        super("UploadPicRecordReq");
        this.userName = userName;
        this.lastSendTime = lastSendTime;
        this.num = num;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(String lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
