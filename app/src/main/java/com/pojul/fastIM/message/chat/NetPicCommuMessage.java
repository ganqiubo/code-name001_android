package com.pojul.fastIM.message.chat;

public class NetPicCommuMessage extends CommunityMessage{

    private NetPicMessage netPicMessage;

    public NetPicMessage getNetPicMessage() {
        return netPicMessage;
    }

    public void setNetPicMessage(NetPicMessage netPicMessage) {
        this.netPicMessage = netPicMessage;
    }

    @Override
    public ChatMessage getContent() {
        return netPicMessage;
    }

    @Override
    public void setContent(ChatMessage chatMessage) {
        this.netPicMessage = (NetPicMessage) chatMessage;
    }
}
