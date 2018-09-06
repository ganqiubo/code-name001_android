package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class LikeUserReq extends RequestMessage{

    private String likedUserName;
    private int type; //0: 喜欢; 1: 取消喜欢

    public LikeUserReq() {
        super();
        setRequestUrl("LikeUserReq");
    }

    public String getLikedUserName() {
        return likedUserName;
    }

    public void setLikedUserName(String likedUserName) {
        this.likedUserName = likedUserName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
