package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;
import com.pojul.objectsocket.message.StringFile;

public class EditCommuPhotoReq extends RequestMessage{

    private StringFile photo;
    private String roomUid;
    private String rawName;

    public EditCommuPhotoReq() {
        super();
        setRequestUrl("EditCommuPhotoReq");
    }

    public StringFile getPhoto() {
        return photo;
    }

    public void setPhoto(StringFile photo) {
        this.photo = photo;
    }

    public String getRoomUid() {
        return roomUid;
    }

    public void setRoomUid(String roomUid) {
        this.roomUid = roomUid;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }
}
