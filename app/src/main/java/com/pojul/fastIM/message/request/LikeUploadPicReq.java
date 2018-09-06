package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class LikeUploadPicReq extends RequestMessage{

    private long uploadPicId;
    private long likeUserId;
    private int type; //0: 喜欢;1: 取消喜欢
    private String gallery;
    private String uid;
    private String url;

    public LikeUploadPicReq() {
        super();
        setRequestUrl("LikeUploadPicReq");
    }

    public long getUploadPicId() {
        return uploadPicId;
    }

    public void setUploadPicId(long uploadPicId) {
        this.uploadPicId = uploadPicId;
    }

    public long getLikeUserId() {
        return likeUserId;
    }

    public void setLikeUserId(long likeUserId) {
        this.likeUserId = likeUserId;
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
