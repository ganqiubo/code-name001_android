package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.PicComment;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetPicCommentsResp extends ResponseMessage{

    private List<PicComment> picComments;
    private long totalComments;

    public List<PicComment> getPicComments() {
        return picComments;
    }

    public void setPicComments(List<PicComment> picComments) {
        this.picComments = picComments;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }
}
