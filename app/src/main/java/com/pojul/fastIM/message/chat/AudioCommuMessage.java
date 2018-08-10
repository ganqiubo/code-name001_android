package com.pojul.fastIM.message.chat;

public class AudioCommuMessage extends CommunityMessage{

    private AudioMessage audioMessage;

    public AudioMessage getAudioMessage() {
        return audioMessage;
    }

    public void setAudioMessage(AudioMessage audioMessage) {
        this.audioMessage = audioMessage;
    }

    @Override
    public ChatMessage getContent() {
        return audioMessage;
    }

    @Override
    public void setContent(ChatMessage chatMessage) {
        this.audioMessage = (AudioMessage) chatMessage;
    }
}
