package com.pojul.fastIM.entity;

public class ExtendUploadPic extends UploadPic{

    private String galleryType;
    private int sex;
    private String photo;
    private String userName;
    private String nickName;
    private int age;
    private double distance;
    private String brosePic;
    private String thirdUid;

    public String getGalleryType() {
        return galleryType;
    }

    public void setGalleryType(String galleryType) {
        this.galleryType = galleryType;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getBrosePic() {
        return brosePic;
    }

    public void setBrosePic(String brosePic) {
        this.brosePic = brosePic;
    }

    public String getThirdUid() {
        return thirdUid;
    }

    public void setThirdUid(String thirdUid) {
        this.thirdUid = thirdUid;
    }
}
