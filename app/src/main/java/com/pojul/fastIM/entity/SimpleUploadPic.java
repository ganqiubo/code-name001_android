package com.pojul.fastIM.entity;

public class SimpleUploadPic extends BaseEntity{

    private long id;
    private String uid;
    private String picsStr;
    private int hasThubmUp;
    private int hasLiked;
    private int hasCollected;
    private String gallery;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPicsStr() {
        return picsStr;
    }

    public void setPicsStr(String picsStr) {
        this.picsStr = picsStr;
    }

    public int getHasThubmUp() {
        return hasThubmUp;
    }

    public void setHasThubmUp(int hasThubmUp) {
        this.hasThubmUp = hasThubmUp;
    }

    public int getHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(int hasLiked) {
        this.hasLiked = hasLiked;
    }

    public int getHasCollected() {
        return hasCollected;
    }

    public void setHasCollected(int hasCollected) {
        this.hasCollected = hasCollected;
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
}
