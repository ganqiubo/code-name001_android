package com.pojul.fastIM.entity;

import com.google.gson.Gson;

public class BaiduMapStyle extends BaseEntity{

    private String visibility;
    private String color;

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
