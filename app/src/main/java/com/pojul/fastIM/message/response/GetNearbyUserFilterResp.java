package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.NearbyUserFilter;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetNearbyUserFilterResp extends ResponseMessage{

	private NearbyUserFilter filter;

	public NearbyUserFilter getFilter() {
		return filter;
	}

	public void setFilter(NearbyUserFilter filter) {
		this.filter = filter;
	}
	
}
