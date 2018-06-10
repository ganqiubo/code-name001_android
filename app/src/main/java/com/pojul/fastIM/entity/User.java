package com.pojul.fastIM.entity;

import java.sql.ResultSet;

import com.pojul.objectsocket.message.StringFile;

public class User extends BaseEntity{

	protected int id;
	protected String userName;
	protected String passwd;
	protected String nickName;
	protected String registDate;
	protected StringFile photo;
	protected String autograph;
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public User(int id, String userName, String passwd, String nickName, String registDate, StringFile photo,
			String autograph) {
		super();
		this.id = id;
		this.userName = userName;
		this.passwd = passwd;
		this.nickName = nickName;
		this.registDate = registDate;
		this.photo = photo;
		this.autograph = autograph;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getRegistDate() {
		return registDate;
	}
	public void setRegistDate(String registDate) {
		this.registDate = registDate;
	}
	
	public String getPhoto() {
		return photo.getFilePath();
	}

	public void setPhoto(StringFile photo) {
		this.photo = photo;
	}

	public String getAutograph() {
		return autograph;
	}

	public void setAutograph(String autograph) {
		this.autograph = autograph;
	}

	

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", passwd=" + passwd + ", nickName=" + nickName
				+ ", registDate=" + registDate + ", photo=" + photo + ", autograph=" + autograph + "]";
	}

	@Override
	public void setBySql(ResultSet rs) {
		// TODO Auto-generated method stub
		super.setBySql(rs);
		if(rs == null) {
			return;
		}
		id = getInt(rs, "id");
		userName = getString(rs, "user_name");
		nickName = getString(rs, "nick_name");
		registDate = getString(rs, "regist_date");
		photo = getStringFile(rs, "photo");
		autograph = getString(rs, "autograph");
	}

	
}
