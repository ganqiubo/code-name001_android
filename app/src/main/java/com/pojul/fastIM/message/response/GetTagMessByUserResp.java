package com.pojul.fastIM.message.response;

import java.util.List;

import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetTagMessByUserResp extends ResponseMessage{
	
    private List<CommunityMessEntity> communityMessEntities;

	public List<CommunityMessEntity> getCommunityMessEntities() {
		return communityMessEntities;
	}

	public void setCommunityMessEntities(List<CommunityMessEntity> communityMessEntities) {
		this.communityMessEntities = communityMessEntities;
	}
}
