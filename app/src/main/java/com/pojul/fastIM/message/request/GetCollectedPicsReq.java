package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetCollectedPicsReq extends RequestMessage{

    private int num;
    private long startNum;
    private String gallery;
    private long userId;

    public GetCollectedPicsReq() {
        super();
        setRequestUrl("GetCollectedPicsReq");
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

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
