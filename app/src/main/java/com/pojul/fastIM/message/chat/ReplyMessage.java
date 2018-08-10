package com.pojul.fastIM.message.chat;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.StringFile;

public class ReplyMessage extends ChatMessage{

    private String replyUid;
    private ChatMessage chatMessage;
    private String nickName;
    private StringFile photo;
    private int userSex;
    private int certificate;
    private int isSpaceTtravel;
    private int likes;

    public String getReplyUid() {
        return replyUid;
    }

    public void setReplyUid(String replyUid) {
        this.replyUid = replyUid;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public StringFile getPhoto() {
        return photo;
    }

    public void setPhoto(StringFile photo) {
        this.photo = photo;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public int getCertificate() {
        return certificate;
    }

    public void setCertificate(int certificate) {
        this.certificate = certificate;
    }

    public int getIsSpaceTtravel() {
        return isSpaceTtravel;
    }

    public void setIsSpaceTtravel(int isSpaceTtravel) {
        this.isSpaceTtravel = isSpaceTtravel;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
