package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.ThirdPicLikes;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class ThirdPicLikesCountResp extends ResponseMessage{

    private List<ThirdPicLikes> thirdPicLikes;

    public List<ThirdPicLikes> getThirdPicLikes() {
        return thirdPicLikes;
    }

    public void setThirdPicLikes(List<ThirdPicLikes> thirdPicLikes) {
        this.thirdPicLikes = thirdPicLikes;
    }
}
