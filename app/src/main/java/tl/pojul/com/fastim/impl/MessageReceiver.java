package tl.pojul.com.fastim.impl;

import com.pojul.objectsocket.message.BaseMessage;

import tl.pojul.com.fastim.MyApplication;

public class MessageReceiver implements MyApplication.IReceiveMessage{

    private String messageUid;

    public MessageReceiver(String messageUid) {
        this.messageUid = messageUid;
    }

    @Override
    public void receiveMessage(BaseMessage message) {

    }
}
