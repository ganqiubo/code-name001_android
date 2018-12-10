package com.pojul.fastIM.message.response;

import com.pojul.fastIM.entity.NewVersion;
import com.pojul.objectsocket.message.ResponseMessage;

public class GetNewVersionResp extends ResponseMessage{

    private NewVersion newVersion;

    public NewVersion getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(NewVersion newVersion) {
        this.newVersion = newVersion;
    }
}
