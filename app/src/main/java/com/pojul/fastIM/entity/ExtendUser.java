package com.pojul.fastIM.entity;

public class ExtendUser extends User{

    private long extendId;
    private int isFriend;

    public long getExtendId() {
        return extendId;
    }

    public void setExtendId(long extendId) {
        this.extendId = extendId;
    }

    public int getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(int isFriend) {
        this.isFriend = isFriend;
    }
}
