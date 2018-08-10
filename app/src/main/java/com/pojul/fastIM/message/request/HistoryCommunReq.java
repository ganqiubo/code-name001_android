package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

/**
 * 社区历史消息
 * */
public class HistoryCommunReq  extends RequestMessage {

    private String roomUid;
    private String lastMessageUid;
    private int num;

    public HistoryCommunReq() {
        super();
        setRequestUrl("HistoryCommunMessReq");
    }

    public HistoryCommunReq(String roomUid, String lastMessageUid, int num) {
        this.roomUid = roomUid;
        this.lastMessageUid = lastMessageUid;
        this.num = num;
        setRequestUrl("HistoryCommunMessReq");
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }

    public String getLastMessageUid() {
        return lastMessageUid;
    }

    public void setLastMessageUid(String lastMessageUid) {
        this.lastMessageUid = lastMessageUid;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
