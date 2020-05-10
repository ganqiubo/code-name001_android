package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class PicCommentReq extends RequestMessage{

    private String uploadPicId;
    private String text;
    private int level;
    private long oneLevelId = -1;
    private String gallery;

    public PicCommentReq() {
        super();
        setRequestUrl("PicCommentReq");
    }

    public String getUploadPicId() {
        return uploadPicId;
    }

    public void setUploadPicId(String uploadPicId) {
        this.uploadPicId = uploadPicId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getOneLevelId() {
        return oneLevelId;
    }

    public void setOneLevelId(long oneLevelId) {
        this.oneLevelId = oneLevelId;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }
}
