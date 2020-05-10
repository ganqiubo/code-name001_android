package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetCommuDetailResp extends ResponseMessage{

    private CommunityRoom room;

    public CommunityRoom getRoom() {
        return room;
    }

    public void setRoom(CommunityRoom room) {
        this.room = room;
    }
}
