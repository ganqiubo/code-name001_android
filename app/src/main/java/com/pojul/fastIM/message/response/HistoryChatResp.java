package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.Message;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.ArrayList;

public class HistoryChatResp extends ResponseMessage {

    private ArrayList<Message> messages;

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
