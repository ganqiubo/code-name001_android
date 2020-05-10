package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetPicTopCommentsReq extends RequestMessage{

    private String picId;

    public GetPicTopCommentsReq() {
        super();
        setRequestUrl("GetPicTopCommentsReq");
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }
}
