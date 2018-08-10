package com.pojul.fastIM.message.chat;

public class VideoCommuMessage extends CommunityMessage{

    private VideoMessage videoMessage;

    public VideoMessage getVideoMessage() {
        return videoMessage;
    }

    public void setVideoMessage(VideoMessage videoMessage) {
        this.videoMessage = videoMessage;
    }

    @Override
    public ChatMessage getContent() {
        return videoMessage;
    }

    @Override
    public void setContent(ChatMessage chatMessage) {
        this.videoMessage = (VideoMessage) chatMessage;
    }
}
