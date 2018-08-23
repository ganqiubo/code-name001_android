package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.MoreSubReply;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class MoreSubReplyResp extends ResponseMessage{

    private List<MoreSubReply> moreSubReplies;

    public List<MoreSubReply> getMoreSubReplies() {
        return moreSubReplies;
    }

    public void setMoreSubReplies(List<MoreSubReply> moreSubReplies) {
        this.moreSubReplies = moreSubReplies;
    }
}
