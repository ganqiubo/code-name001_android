package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.LatLonRange;
import com.pojul.fastIM.entity.MessageFilter;
import com.pojul.objectsocket.message.RequestMessage;

public class GetTagMessNearByReq extends RequestMessage{

    private int num;
    private long startNum;
    private MessageFilter filter;
    private LatLonRange latLonRange;

    public GetTagMessNearByReq() {
        super();
        setRequestUrl("GetTagMessNearByReq");
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public MessageFilter getFilter() {
        return filter;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }

    public void setFilter(MessageFilter filter) {
        this.filter = filter;
    }

    public LatLonRange getLatLonRange() {
        return latLonRange;
    }

    public void setLatLonRange(LatLonRange latLonRange) {
        this.latLonRange = latLonRange;
    }
}
