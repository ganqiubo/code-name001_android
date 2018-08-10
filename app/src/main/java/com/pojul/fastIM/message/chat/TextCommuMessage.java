package com.pojul.fastIM.message.chat;

public class TextCommuMessage extends CommunityMessage{

    private TextChatMessage textChatMessage;

    public TextChatMessage getTextChatMessage() {
        return textChatMessage;
    }

    public void setTextChatMessage(TextChatMessage textChatMessage) {
        this.textChatMessage = textChatMessage;
    }

    @Override
    public ChatMessage getContent() {
        return textChatMessage;
    }

    @Override
    public void setContent(ChatMessage chatMessage) {
        this.textChatMessage = (TextChatMessage) chatMessage;
    }

}
