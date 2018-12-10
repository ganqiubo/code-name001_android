package com.pojul.fastIM.entity;

public class AddFriend extends BaseEntity{

    private long id;
    private String reqUserName;
    private String reqedUserName;
    private int reqType;
    private String reqText;
    private int reqNotified;
    private int reqedNotified;
    private int reqStatus;
    private User reqUserInfo;
    private String reqTime;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getReqUserName() {
        return reqUserName;
    }

    public void setReqUserName(String reqUserName) {
        this.reqUserName = reqUserName;
    }

    public String getReqedUserName() {
        return reqedUserName;
    }

    public void setReqedUserName(String reqedUserName) {
        this.reqedUserName = reqedUserName;
    }

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public String getReqText() {
        return reqText;
    }

    public void setReqText(String reqText) {
        this.reqText = reqText;
    }

    public int getReqNotified() {
        return reqNotified;
    }

    public void setReqNotified(int reqNotified) {
        this.reqNotified = reqNotified;
    }

    public int getReqedNotified() {
        return reqedNotified;
    }

    public void setReqedNotified(int reqedNotified) {
        this.reqedNotified = reqedNotified;
    }

    public int getReqStatus() {
        return reqStatus;
    }

    public void setReqStatus(int reqStatus) {
        this.reqStatus = reqStatus;
    }

    public User getReqUserInfo() {
        return reqUserInfo;
    }

    public void setReqUserInfo(User reqUserInfo) {
        this.reqUserInfo = reqUserInfo;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }
}
