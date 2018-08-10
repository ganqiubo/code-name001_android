package com.pojul.fastIM.message.chat;

public class PicCommuMessage extends CommunityMessage{

    private PicMessage picMessage;

    public PicMessage getPicMessage() {
        return picMessage;
    }

    public void setPicMessage(PicMessage picMessage) {
        this.picMessage = picMessage;
    }

    @Override
    public ChatMessage getContent() {
        return picMessage;
    }

    @Override
    public void setContent(ChatMessage chatMessage) {
        this.picMessage = (PicMessage) chatMessage;
    }
}
