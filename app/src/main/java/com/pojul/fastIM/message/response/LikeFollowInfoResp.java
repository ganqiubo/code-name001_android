package com.pojul.fastIM.message.response;

import com.pojul.objectsocket.message.ResponseMessage;

public class LikeFollowInfoResp extends ResponseMessage{

    private int likeCount;
    private int followCount;

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getFollowCount() {
        return followCount;
    }

    public void setFollowCount(int followCount) {
        this.followCount = followCount;
    }
}
