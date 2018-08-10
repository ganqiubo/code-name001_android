package com.pojul.fastIM.entity;

import java.sql.ResultSet;

import com.google.gson.Gson;
import com.pojul.fastIM.utils.ServerConstant;
import com.pojul.objectsocket.message.StringFile;
import com.pojul.objectsocket.utils.Constant;

public class User extends BaseEntity{

	protected int id;
	protected String userName;
	protected String passwd;
	protected String nickName;
	protected String registDate;
	protected StringFile photo;
	protected String autograph;
	protected int sex;
	protected int certificate;
	protected int credit;
	protected int age;
	protected int ban;
	private int showCommunityLoc; //0: 显示; 1: 不显示
	
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
	
	public StringFile getPhoto() {
		return photo;
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

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getBan() {
		return ban;
	}

	public void setBan(int ban) {
		this.ban = ban;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public int getShowCommunityLoc() {
		return showCommunityLoc;
	}

	public void setShowCommunityLoc(int showCommunityLoc) {
		this.showCommunityLoc = showCommunityLoc;
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
		photo.setFilePath(Constant.BASE_URL + photo.getFilePath());
		autograph = getString(rs, "autograph");
		sex = getInt(rs, "sex");
		certificate = getInt(rs, "certificate");
		credit = getInt(rs, "credit");
		age = getInt(rs, "age");
		ban = getInt(rs, "ban");
		showCommunityLoc = getInt(rs, "show_community_loc");
	}

	
}
