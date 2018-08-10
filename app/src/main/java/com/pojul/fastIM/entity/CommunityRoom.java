package com.pojul.fastIM.entity;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

public class CommunityRoom extends BaseEntity implements Comparable{

    private long id;
    private String communityUid;
    private String name;
    private String createDate;
    private String communityType;
    private String communitySubtype;
    private String country;
    private String province;
    private String city;
    private String district;
    private String addr;
    private double longitude;
    private double latitude;
    private double altitude;
    private double distance;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCommunityUid() {
        return communityUid;
    }

    public void setCommunityUid(String communityUid) {
        this.communityUid = communityUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCommunityType() {
        return communityType;
    }

    public void setCommunityType(String communityType) {
        this.communityType = communityType;
    }

    public String getCommunitySubtype() {
        return communitySubtype;
    }

    public void setCommunitySubtype(String communitySubtype) {
        this.communitySubtype = communitySubtype;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        CommunityRoom communityRoom = (CommunityRoom) o;
        if (this.distance > communityRoom.distance) {
            return 1;
        }else {
            return -1;
        }
    }
}
