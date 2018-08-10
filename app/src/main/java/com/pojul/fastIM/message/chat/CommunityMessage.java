package com.pojul.fastIM.message.chat;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.StringFile;

public class CommunityMessage extends ChatMessage{

    private String communityName;
    private int isSpaceTravel; // 0: 穿越; 1: 非穿越;
    private int userSex;
    private int certificate;
    private String nickName;
    private StringFile photo;
    private long timeMill;

    public CommunityMessage() {
        super();
        chatType = 3;
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

    public int getIsSpaceTravel() {
        return isSpaceTravel;
    }

    public void setIsSpaceTravel(int isSpaceTravel) {
        this.isSpaceTravel = isSpaceTravel;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
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

    public ChatMessage getContent() {
        return this;
    }

    public void setContent(ChatMessage chatMessage){
    }

    public long getTimeMill() {
        return timeMill;
    }

    public void setTimeMill(long timeMill) {
        this.timeMill = timeMill;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
