package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.fastIM.entity.CommunityRoom;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetTopMessMultiResp extends ResponseMessage{

    private List<CommunityMessEntity> communityMessEntities;
    private List<CommunityRoom> communityRooms;

    public List<CommunityMessEntity> getCommunityMessEntities() {
        return communityMessEntities;
    }

    public void setCommunityMessEntities(List<CommunityMessEntity> communityMessEntities) {
        this.communityMessEntities = communityMessEntities;
    }

    public List<CommunityRoom> getCommunityRooms() {
        return communityRooms;
    }

    public void setCommunityRooms(List<CommunityRoom> communityRooms) {
        this.communityRooms = communityRooms;
    }
}
