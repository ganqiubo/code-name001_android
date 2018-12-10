package com.pojul.fastIM.entity;

public class UserSelectFilter {

    private int minAge = 0;
    private int maxAge = 100;
    private int minCredit = 90;
    private int sex = -1;

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getMinCredit() {
        return minCredit;
    }

    public void setMinCredit(int minCredit) {
        this.minCredit = minCredit;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
