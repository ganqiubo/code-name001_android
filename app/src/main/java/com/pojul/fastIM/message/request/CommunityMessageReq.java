package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.objectsocket.message.RequestMessage;

public class CommunityMessageReq extends RequestMessage{

    private CommunityRoom communityRoom;
    private int reqCode = 1; //1: 请求消息; 2: 取消请求消息
    private String uid;

    public CommunityMessageReq() {
        super();
        setRequestUrl("CommunityMessageReq");
    }

    public CommunityRoom getCommunityRoom() {
        return communityRoom;
    }

    public void setCommunityRoom(CommunityRoom communityRoom) {
        this.communityRoom = communityRoom;
    }

    public int getReqCode() {
        return reqCode;
    }

    public void setReqCode(int reqCode) {
        this.reqCode = reqCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
