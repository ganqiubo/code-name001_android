package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetPicCommentsReq extends RequestMessage{

    private String picId;
    private int page;
    private int num;

    public GetPicCommentsReq() {
        super();
        setRequestUrl("GetPicCommentsReq");
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
