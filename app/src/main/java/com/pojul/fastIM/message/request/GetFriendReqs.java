package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetFriendReqs extends RequestMessage{

    private int num;
    private long startNum;

    public GetFriendReqs() {
        super();
        setRequestUrl("GetFriendReqs");
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }
}
