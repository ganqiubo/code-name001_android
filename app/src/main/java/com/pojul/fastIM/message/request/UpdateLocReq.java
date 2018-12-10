package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.NearByPeople;
import com.pojul.objectsocket.message.RequestMessage;

public class UpdateLocReq extends RequestMessage{

    private NearByPeople nearByPeople;

    public UpdateLocReq() {
        super();
        setRequestUrl("UpdateLocReq");
    }

    public NearByPeople getNearByPeople() {
        return nearByPeople;
    }

    public void setNearByPeople(NearByPeople nearByPeople) {
        this.nearByPeople = nearByPeople;
    }
}
