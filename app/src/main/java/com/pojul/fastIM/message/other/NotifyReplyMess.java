package com.pojul.fastIM.message.other;

import com.pojul.objectsocket.message.BaseMessage;

public class NotifyReplyMess extends BaseMessage{

    private String replyTagMessUid;
    private String replyText;
    private String tagMessTitle;
    private String replyNickName;
    private int replyType; //0: 公开; 1: 密私
    private String photo;
    private int unSendCount;

    public String getReplyTagMessUid() {
        return replyTagMessUid;
    }

    public void setReplyTagMessUid(String replyTagMessUid) {
        this.replyTagMessUid = replyTagMessUid;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public String getTagMessTitle() {
        return tagMessTitle;
    }

    public void setTagMessTitle(String tagMessTitle) {
        this.tagMessTitle = tagMessTitle;
    }

    public String getReplyNickName() {
        return replyNickName;
    }

    public void setReplyNickName(String replyNickName) {
        this.replyNickName = replyNickName;
    }

    public int getReplyType() {
        return replyType;
    }

    public void setReplyType(int replyType) {
        this.replyType = replyType;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getUnSendCount() {
        return unSendCount;
    }

    public void setUnSendCount(int unSendCount) {
        this.unSendCount = unSendCount;
    }
}
