package com.pojul.fastIM.entity;

public class UnsplashUser {

    private String username;
    private String location;
    private UnsplashUserPhoto profile_image;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public UnsplashUserPhoto getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(UnsplashUserPhoto profile_image) {
        this.profile_image = profile_image;
    }
}
