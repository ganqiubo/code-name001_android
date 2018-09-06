package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class FollowUserReq extends RequestMessage{

    private String followedUserName;
    private int type; //0: 关注; 1: 取消关注

    public FollowUserReq() {
        super();
        setRequestUrl("FollowUserReq");
    }

    public String getFollowedUserName() {
        return followedUserName;
    }

    public void setFollowedUserName(String followedUserName) {
        this.followedUserName = followedUserName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
