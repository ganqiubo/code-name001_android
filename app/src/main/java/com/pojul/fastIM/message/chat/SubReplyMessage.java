package com.pojul.fastIM.message.chat;

import com.google.gson.Gson;

public class SubReplyMessage extends ChatMessage{

    private long id;
    private String replyTagMessUid;
    private String replyMessageUid;
    private int isSpaceTravel;
    private String subReplyUid;
    private String userName;
    private String nickName;
    private String text;
    private long timeMilli;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getReplyMessageUid() {
        return replyMessageUid;
    }

    public void setReplyMessageUid(String replyMessageUid) {
        this.replyMessageUid = replyMessageUid;
    }

    public int getIsSpaceTravel() {
        return isSpaceTravel;
    }

    public void setIsSpaceTravel(int isSpaceTravel) {
        this.isSpaceTravel = isSpaceTravel;
    }

    public String getSubReplyUid() {
        return subReplyUid;
    }

    public void setSubReplyUid(String subReplyUid) {
        this.subReplyUid = subReplyUid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeMilli() {
        return timeMilli;
    }

    public void setTimeMilli(long timeMilli) {
        this.timeMilli = timeMilli;
    }

    public String getReplyTagMessUid() {
        return replyTagMessUid;
    }

    public void setReplyTagMessUid(String replyTagMessUid) {
        this.replyTagMessUid = replyTagMessUid;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
