package com.pojul.fastIM.entity;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.StringFile;
import java.sql.ResultSet;

public class CommunityMessEntity extends BaseEntity{

    private long id;
    private String messageUid;
    private String communityName;
    private int sex;
    private int certificate;
    private String nickName;
    private StringFile photo;
    private String messageClass;
    private String messageContent;
    private long timeMill;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public int getCertificate() {
		return certificate;
	}

	public void setCertificate(int certificate) {
		this.certificate = certificate;
	}

	public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public StringFile getPhoto() {
        return photo;
    }

    public void setPhoto(StringFile photo) {
        this.photo = photo;
    }

    public String getMessageClass() {
        return messageClass;
    }

    public void setMessageClass(String messageClass) {
        this.messageClass = messageClass;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    
    public long getTimeMill() {
		return timeMill;
	}

	public void setTimeMill(long timeMill) {
		this.timeMill = timeMill;
	}

	@Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
