package com.pojul.fastIM.entity;

import com.google.gson.Gson;

import java.util.List;
import java.sql.ResultSet;

public class UserFilter extends BaseEntity{

	private long id;
    private boolean isFilterEnabled;
    private boolean isWhiteListEnabled;
    private List<String> whiteListNames;
    private boolean isBlackListEnabled;
    private List<String> blackListNames;
    private boolean isAgeEnabled;
    private int minAge;
    private int maxAge;
    private boolean isSexEnabled;
    private int sex;//0: 女; 1: 男
    private boolean isCertificatEnabled;
    private boolean isCreditEnabled;
    private int credit;
    private String messageUid;

    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isFilterEnabled() {
        return isFilterEnabled;
    }

    public void setFilterEnabled(boolean filterEnabled) {
        isFilterEnabled = filterEnabled;
    }

    public boolean isWhiteListEnabled() {
        return isWhiteListEnabled;
    }

    public void setWhiteListEnabled(boolean whiteListEnabled) {
        isWhiteListEnabled = whiteListEnabled;
    }

    public List<String> getWhiteListNames() {
        return whiteListNames;
    }

    public void setWhiteListNames(List<String> whiteListNames) {
        this.whiteListNames = whiteListNames;
    }

    public boolean isBlackListEnabled() {
        return isBlackListEnabled;
    }

    public void setBlackListEnabled(boolean blackListEnabled) {
        isBlackListEnabled = blackListEnabled;
    }

    public List<String> getBlackListNames() {
        return blackListNames;
    }

    public void setBlackListNames(List<String> blackListNames) {
        this.blackListNames = blackListNames;
    }

    public boolean isAgeEnabled() {
        return isAgeEnabled;
    }

    public void setAgeEnabled(boolean ageEnabled) {
        isAgeEnabled = ageEnabled;
    }

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

	public boolean isSexEnabled() {
        return isSexEnabled;
    }

    public void setSexEnabled(boolean sexEnabled) {
        isSexEnabled = sexEnabled;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

	public boolean isCertificatEnabled() {
		return isCertificatEnabled;
	}

	public void setCertificatEnabled(boolean isCertificatEnabled) {
		this.isCertificatEnabled = isCertificatEnabled;
	}

	public boolean isCreditEnabled() {
		return isCreditEnabled;
	}

	public void setCreditEnabled(boolean isCreditEnabled) {
		this.isCreditEnabled = isCreditEnabled;
	}

	public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }
  
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

	public String getMessageUid() {
		return messageUid;
	}

	public void setMessageUid(String messageUid) {
		this.messageUid = messageUid;
	}

}
