package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetTagMessByUidResp extends ResponseMessage{

    private CommunityMessEntity communityMessEntity;

	public CommunityMessEntity getCommunityMessEntity() {
		return communityMessEntity;
	}

	public void setCommunityMessEntity(CommunityMessEntity communityMessEntity) {
		this.communityMessEntity = communityMessEntity;
	}

}
