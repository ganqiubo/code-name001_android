package com.pojul.fastIM.message.response;

import com.pojul.fastIM.message.chat.ReplyMessage;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetReplysResp extends ResponseMessage{

    private List<ReplyMessage> replyMessages;

    public List<ReplyMessage> getReplyMessages() {
        return replyMessages;
    }

    public void setReplyMessages(List<ReplyMessage> replyMessages) {
        this.replyMessages = replyMessages;
    }
}
