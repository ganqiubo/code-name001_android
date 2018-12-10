package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class GetRecomdMessResp extends ResponseMessage{

    private List<CommunityMessEntity> communityMessEntities;

    public List<CommunityMessEntity> getCommunityMessEntities() {
        return communityMessEntities;
    }

    public void setCommunityMessEntities(List<CommunityMessEntity> communityMessEntities) {
        this.communityMessEntities = communityMessEntities;
    }
}
