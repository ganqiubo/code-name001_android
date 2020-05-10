package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.PicComment;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetPicTopCommentsResp extends ResponseMessage{

    private List<PicComment> picComments;

    public List<PicComment> getPicComments() {
        return picComments;
    }

    public void setPicComments(List<PicComment> picComments) {
        this.picComments = picComments;
    }
}
