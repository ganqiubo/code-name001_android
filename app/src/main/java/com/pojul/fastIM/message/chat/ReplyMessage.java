package com.pojul.fastIM.message.chat;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.StringFile;

import java.util.List;

public class ReplyMessage extends ChatMessage{

    private long id;
    private String replyMessageUid;
    private String userName;
    private String nickName;
    private StringFile photo;
    private int userSex;
    private int certificate;
    private int isSpaceTravel;
    private int thumbUps;
    private int hasThumbUp;
    private List<SubReplyMessage> subReplys;
    private String subReplyStrs;
    private int subReplysNum;
    private String text;
    private long timeMilli;
    private boolean hasMoreSubReply;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getIsSpaceTravel() {
        return isSpaceTravel;
    }

    public void setIsSpaceTravel(int isSpaceTravel) {
        this.isSpaceTravel = isSpaceTravel;
    }

    public int getThumbUps() {
        return thumbUps;
    }

    public void setThumbUps(int thumbUps) {
        this.thumbUps = thumbUps;
    }

    public int getHasThumbUp() {
        return hasThumbUp;
    }

    public void setHasThumbUp(int hasThumbUp) {
        this.hasThumbUp = hasThumbUp;
    }

    public List<SubReplyMessage> getSubReplys() {
        return subReplys;
    }

    public void setSubReplys(List<SubReplyMessage> subReplys) {
        this.subReplys = subReplys;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReplyMessageUid() {
        return replyMessageUid;
    }

    public void setReplyMessageUid(String replyMessageUid) {
        this.replyMessageUid = replyMessageUid;
    }

    public String getSubReplyStrs() {
        return subReplyStrs;
    }

    public void setSubReplyStrs(String subReplyStrs) {
        this.subReplyStrs = subReplyStrs;
    }

    public int getSubReplysNum() {
        return subReplysNum;
    }

    public void setSubReplysNum(int subReplysNum) {
        this.subReplysNum = subReplysNum;
    }

    public boolean isHasMoreSubReply() {
        return hasMoreSubReply;
    }

    public void setHasMoreSubReply(boolean hasMoreSubReply) {
        this.hasMoreSubReply = hasMoreSubReply;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
