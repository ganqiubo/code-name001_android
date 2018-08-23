package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

import java.util.List;

public class GetTopMessMultiReq extends RequestMessage{

    private List<String> communityUids;
    private int num;

    public GetTopMessMultiReq() {
        super();
        setRequestUrl("GetTopMessMultiReq");
    }

    public List<String> getCommunityUids() {
        return communityUids;
    }

    public void setCommunityUids(List<String> communityUids) {
        this.communityUids = communityUids;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
