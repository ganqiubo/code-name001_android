package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetUsersByNickReq extends RequestMessage{

    private String nickName;
    private long startNum;
    private int num;

    public GetUsersByNickReq() {
        super();
        setRequestUrl("GetUsersByNickReq");
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
