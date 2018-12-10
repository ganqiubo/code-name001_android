package com.pojul.fastIM.message.request;

import com.pojul.objectsocket.message.RequestMessage;

public class RegisterReq extends RequestMessage{

    private String nickName;
    private String imei;
    private String imsi;
    private String birthday;
    private int birthdayType;
    private int sex;
    private String passwd;

    public RegisterReq() {
        super();
        setRequestUrl("RegisterReq");
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getBirthdayType() {
        return birthdayType;
    }

    public void setBirthdayType(int birthdayType) {
        this.birthdayType = birthdayType;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
