package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class LoginByTokenReq extends RequestMessage{

    private String tokenId;
    private String deviceType;

    public LoginByTokenReq() {
        super();
        setRequestUrl("LoginByToken");
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
}
