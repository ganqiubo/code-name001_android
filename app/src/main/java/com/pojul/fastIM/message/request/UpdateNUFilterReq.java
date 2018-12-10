package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.NearbyUserFilter;
import com.pojul.objectsocket.message.RequestMessage;

public class UpdateNUFilterReq extends RequestMessage{

    private NearbyUserFilter filter;

    public UpdateNUFilterReq() {
        super();
        setRequestUrl("UpdateNUFilterReq");
    }

    public NearbyUserFilter getFilter() {
        return filter;
    }

    public void setFilter(NearbyUserFilter filter) {
        this.filter = filter;
    }
}
