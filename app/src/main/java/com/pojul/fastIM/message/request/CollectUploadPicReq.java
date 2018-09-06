package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class CollectUploadPicReq extends RequestMessage{

    private long uploadPicId;
    private long collectUserId;
    private int type; //0: 收藏;1: 取消收藏
    private String gallery;
    private String uid;
    private String url;

    public CollectUploadPicReq() {
        super();
        setRequestUrl("CollectUploadPicReq");
    }

    public long getUploadPicId() {
        return uploadPicId;
    }

    public void setUploadPicId(long uploadPicId) {
        this.uploadPicId = uploadPicId;
    }

    public long getCollectUserId() {
        return collectUserId;
    }

    public void setCollectUserId(long collectUserId) {
        this.collectUserId = collectUserId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
