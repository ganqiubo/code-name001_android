package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.User;
import com.pojul.objectsocket.message.RequestMessage;

public class AcceptFriendReq extends RequestMessage{

    private String reqedUserName;
    private String reqUserName;
    private int type;
    private User reqedUserInfo;
    private String reqText;

    public AcceptFriendReq() {
        super();
        setRequestUrl("AcceptFriendReq");
    }

    public String getReqedUserName() {
        return reqedUserName;
    }

    public void setReqedUserName(String reqedUserName) {
        this.reqedUserName = reqedUserName;
    }

    public String getReqUserName() {
        return reqUserName;
    }

    public void setReqUserName(String reqUserName) {
        this.reqUserName = reqUserName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public User getReqedUserInfo() {
        return reqedUserInfo;
    }

    public void setReqedUserInfo(User reqedUserInfo) {
        this.reqedUserInfo = reqedUserInfo;
    }

    public String getReqText() {
        return reqText;
    }

    public void setReqText(String reqText) {
        this.reqText = reqText;
    }
}
