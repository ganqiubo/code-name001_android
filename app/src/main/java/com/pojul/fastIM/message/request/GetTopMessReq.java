package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetTopMessReq extends RequestMessage{

    private String communityUid;
    private int num;

    public GetTopMessReq() {
        super();
        setRequestUrl("GetTopMessReq");
    }

    public String getCommunityUid() {
        return communityUid;
    }

    public void setCommunityUid(String communityUid) {
        this.communityUid = communityUid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
