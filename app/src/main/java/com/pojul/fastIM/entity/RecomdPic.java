package com.pojul.fastIM.entity;

import java.sql.ResultSet;

public class RecomdPic extends BaseEntity{

    private String gallery;
    private String url;
    private String userName;
    private String nickName;
    private String userPhoto;
    private String location;
    private String date;
    public String getGallery() {
        return gallery;
    }
    public void setGallery(String gallery) {
        this.gallery = gallery;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public String getUserPhoto() {
        return userPhoto;
    }
    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public void setBySql(ResultSet rs) {
        // TODO Auto-generated method stub
        super.setBySql(rs);
        if(rs == null) {
            return;
        }
        gallery = getString(rs, "gallery");
        url = getString(rs, "url");
        userName = getString(rs, "user_name");
        nickName = getString(rs, "nick_name");
        userPhoto = getString(rs, "user_photo");
        location = getString(rs, "location");
        date = getString(rs, "date");
    }
}
