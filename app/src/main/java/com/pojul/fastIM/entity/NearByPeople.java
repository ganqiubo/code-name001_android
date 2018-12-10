package com.pojul.fastIM.entity;

import com.google.gson.Gson;

public class NearByPeople extends BaseEntity{

    private long id;
    private String userName;
    private String country;
    private String province;
    private String city;
    private String district;
    private String addr;
    private double longitude;
    private double latitude;
    private double altitude;
    private String updateTime;
    private long updateMilli;
    private int enable;
    private String filter;
    private UserFilter userFilter;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public long getUpdateMilli() {
        return updateMilli;
    }

    public void setUpdateMilli(long updateMilli) {
        this.updateMilli = updateMilli;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public UserFilter getUserFilter() {
        return userFilter;
    }

    public void setUserFilter(UserFilter userFilter) {
        this.userFilter = userFilter;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
