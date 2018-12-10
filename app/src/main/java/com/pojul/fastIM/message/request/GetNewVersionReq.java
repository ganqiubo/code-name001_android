package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class GetNewVersionReq extends RequestMessage{

    public GetNewVersionReq() {
        super();
        setRequestUrl("GetNewVersionReq");
    }
}
