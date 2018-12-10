package com.pojul.fastIM.message.request;

import com.pojul.fastIM.entity.LatLonRange;
import com.pojul.fastIM.entity.UserFilter;
import com.pojul.fastIM.entity.UserSelectFilter;
import com.pojul.objectsocket.message.RequestMessage;

public class GetNearByPeopleReq extends RequestMessage{

    private UserSelectFilter userSelectFilter;
    private int num;
    private long startNum;
    private LatLonRange latLonRange;

    public GetNearByPeopleReq() {
        super();
        setRequestUrl("GetNearByPeopleReq");
    }

    public UserSelectFilter getUserSelectFilter() {
        return userSelectFilter;
    }

    public void setUserSelectFilter(UserSelectFilter userSelectFilter) {
        this.userSelectFilter = userSelectFilter;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public long getStartNum() {
        return startNum;
    }

    public void setStartNum(long startNum) {
        this.startNum = startNum;
    }

    public LatLonRange getLatLonRange() {
        return latLonRange;
    }

    public void setLatLonRange(LatLonRange latLonRange) {
        this.latLonRange = latLonRange;
    }
}
