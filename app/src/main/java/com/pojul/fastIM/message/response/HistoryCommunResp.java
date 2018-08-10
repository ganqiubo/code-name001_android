package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.CommunityMessEntity;
import com.pojul.objectsocket.message.ResponseMessage;

import java.util.List;

public class HistoryCommunResp extends ResponseMessage {

    private List<CommunityMessEntity> historyCommuMessList;

    public HistoryCommunResp() {
        super();
    }

    public List<CommunityMessEntity> getHistoryCommuMessList() {
        return historyCommuMessList;
    }

    public void setHistoryCommuMessList(List<CommunityMessEntity> historyCommuMessList) {
        this.historyCommuMessList = historyCommuMessList;
    }
}
