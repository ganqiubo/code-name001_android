package com.pojul.fastIM.entity;

import com.google.gson.Gson;

public class BaiduMapConfig extends BaseEntity{

    private String featureType;
    private String elementType;
    private BaiduMapStyle stylers;

    public String getFeatureType() {
        return featureType;
    }

    public void setFeatureType(String featureType) {
        this.featureType = featureType;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public BaiduMapStyle getStylers() {
        return stylers;
    }

    public void setStylers(BaiduMapStyle stylers) {
        this.stylers = stylers;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
