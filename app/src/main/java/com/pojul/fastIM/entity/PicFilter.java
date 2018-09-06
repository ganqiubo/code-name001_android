package com.pojul.fastIM.entity;

import java.util.List;

public class PicFilter {

    private boolean nearBy = false;
    private String city;
    private double lan;
    private double lon;
    private List<String> labels;
    private String gallery = "脚步";
    private int sex = -1;

    public boolean isNearBy() {
        return nearBy;
    }

    public void setNearBy(boolean nearBy) {
        this.nearBy = nearBy;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLan() {
        return lan;
    }

    public void setLan(double lan) {
        this.lan = lan;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
