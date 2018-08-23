package com.pojul.fastIM.entity;

import com.google.gson.Gson;

import java.util.List;

public class MessageFilter {

    private List<String> labels;
    private int certificat = -1; // 1: 实名; -1: 不限
    private int sex = -1; //0: 女; 1: 男; -1: 不限
    private int efficative = -1; //0: 有效; -1: 不限

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public int getCertificat() {
        return certificat;
    }

    public void setCertificat(int certificat) {
        this.certificat = certificat;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getEfficative() {
        return efficative;
    }

    public void setEfficative(int efficative) {
        this.efficative = efficative;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
