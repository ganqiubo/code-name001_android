package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetCommunRoomByMessResp extends ResponseMessage{

    private CommunityRoom communityRoom;

    public CommunityRoom getCommunityRoom() {
        return communityRoom;
    }

    public void setCommunityRoom(CommunityRoom communityRoom) {
        this.communityRoom = communityRoom;
    }
}
