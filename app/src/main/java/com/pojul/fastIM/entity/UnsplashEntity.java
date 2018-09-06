package com.pojul.fastIM.entity;

public class UnsplashEntity {

    private String id;
    private String created_at;
    private String updated_at;
    private int width;
    private int height;
    private String color;
    private String description;
    private UnsplashPicUrls urls;
    private UnsplashUser user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UnsplashPicUrls getUrls() {
        return urls;
    }

    public void setUrls(UnsplashPicUrls urls) {
        this.urls = urls;
    }

    public UnsplashUser getUser() {
        return user;
    }

    public void setUser(UnsplashUser user) {
        this.user = user;
    }
}
