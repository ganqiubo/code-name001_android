package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class ThumbupUploadPicReq extends RequestMessage{

    private long uploadPicId;
    private long thumbupUpUserId;
    private String gallery;
    private String uid;
    private String url;

    public ThumbupUploadPicReq() {
        super();
        setRequestUrl("ThumbupUploadPicReq");
    }

    public long getUploadPicId() {
        return uploadPicId;
    }

    public void setUploadPicId(long uploadPicId) {
        this.uploadPicId = uploadPicId;
    }

    public long getThumbupUpUserId() {
        return thumbupUpUserId;
    }

    public void setThumbupUpUserId(long thumbupUpUserId) {
        this.thumbupUpUserId = thumbupUpUserId;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
