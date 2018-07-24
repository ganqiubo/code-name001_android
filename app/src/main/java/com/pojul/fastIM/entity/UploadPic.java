package com.pojul.fastIM.entity;

import android.database.Cursor;

import java.util.List;
import java.sql.ResultSet;

public class UploadPic extends BaseEntity{

	private long id;
	private int userId;
	private int uploadPicType;
	private int isDelete= 1;
	private String uplodPicTheme;
	private String uplodPicLabel;
	private String uploadPicCountry;
	private String uploadPicCity;
	private String uploadPicDistrict;
	private String uploadPicAddr;
	private double uploadPicLongitude;
	private double uploadPicLatitude;
	private double uploadPicAltitude;
	private String uploadPicLocnote;
	private int uploadPicLocshow;
	private String uploadPicTime;
	private String uploadPicProvince;
	private String picTime;
	private int picLocType;
	
	private List<Pic> pics;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUploadPicType() {
		return uploadPicType;
	}

	public void setUploadPicType(int uploadPicType) {
		this.uploadPicType = uploadPicType;
	}

	public int getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	public String getUplodPicTheme() {
		return uplodPicTheme;
	}

	public void setUplodPicTheme(String uplodPicTheme) {
		this.uplodPicTheme = uplodPicTheme;
	}

	public String getUplodPicLabel() {
		return uplodPicLabel;
	}

	public void setUplodPicLabel(String uplodPicLabel) {
		this.uplodPicLabel = uplodPicLabel;
	}

	public String getUploadPicCountry() {
		return uploadPicCountry;
	}

	public void setUploadPicCountry(String uploadPicCountry) {
		this.uploadPicCountry = uploadPicCountry;
	}

	public String getUploadPicCity() {
		return uploadPicCity;
	}

	public void setUploadPicCity(String uploadPicCity) {
		this.uploadPicCity = uploadPicCity;
	}

	public String getUploadPicDistrict() {
		return uploadPicDistrict;
	}

	public void setUploadPicDistrict(String uploadPicDistrict) {
		this.uploadPicDistrict = uploadPicDistrict;
	}

	public String getUploadPicAddr() {
		return uploadPicAddr;
	}

	public void setUploadPicAddr(String uploadPicAddr) {
		this.uploadPicAddr = uploadPicAddr;
	}

	public double getUploadPicLongitude() {
		return uploadPicLongitude;
	}

	public void setUploadPicLongitude(double uploadPicLongitude) {
		this.uploadPicLongitude = uploadPicLongitude;
	}

	public double getUploadPicLatitude() {
		return uploadPicLatitude;
	}

	public void setUploadPicLatitude(double uploadPicLatitude) {
		this.uploadPicLatitude = uploadPicLatitude;
	}

	public double getUploadPicAltitude() {
		return uploadPicAltitude;
	}

	public void setUploadPicAltitude(double uploadPicAltitude) {
		this.uploadPicAltitude = uploadPicAltitude;
	}

	public String getUploadPicLocnote() {
		return uploadPicLocnote;
	}

	public void setUploadPicLocnote(String uploadPicLocnote) {
		this.uploadPicLocnote = uploadPicLocnote;
	}

	public int getUploadPicLocshow() {
		return uploadPicLocshow;
	}

	public void setUploadPicLocshow(int uploadPicLocshow) {
		this.uploadPicLocshow = uploadPicLocshow;
	}

	public List<Pic> getPics() {
		return pics;
	}

	public void setPics(List<Pic> pics) {
		this.pics = pics;
	}

	public String getUploadPicTime() {
		return uploadPicTime;
	}

	public void setUploadPicTime(String uploadPicTime) {
		this.uploadPicTime = uploadPicTime;
	}

	public String getUploadPicProvince() {
		return uploadPicProvince;
	}

	public void setUploadPicProvince(String uploadPicProvince) {
		this.uploadPicProvince = uploadPicProvince;
	}

	public String getPicTime() {
		return picTime;
	}

	public void setPicTime(String picTime) {
		this.picTime = picTime;
	}

	public int getPicLocType() {
		return picLocType;
	}

	public void setPicLocType(int picLocType) {
		this.picLocType = picLocType;
	}

	@Override
	public void setBySql(Cursor cursor) {
		// TODO Auto-generated method stub
		super.setBySql(cursor);
		if(cursor == null) {
			return;
		}
		id = getLong(cursor, "id");
		userId = getInt(cursor, "user_id");
		uploadPicType = getInt(cursor, "upload_pic_type");
		isDelete = getInt(cursor, "is_delete");
		uplodPicTheme = getString(cursor, "uplod_pic_theme");
		uplodPicLabel = getString(cursor, "uplod_pic_label");
		uploadPicCountry = getString(cursor, "upload_pic_country");
		uploadPicCity = getString(cursor, "upload_pic_city");
		uploadPicDistrict = getString(cursor, "upload_pic_district");
		uploadPicAddr = getString(cursor, "upload_pic_addr");
		uploadPicLocnote = getString(cursor, "upload_pic_locnote");
		uploadPicLocshow = getInt(cursor, "upload_pic_locshow");
		uploadPicLongitude = getDouble(cursor, "upload_pic_longitude");
		uploadPicLatitude = getDouble(cursor, "upload_pic_latitude");
		uploadPicAltitude = getDouble(cursor, "upload_pic_altitude");
		uploadPicTime = getString(cursor, "upload_pic_time");
		uploadPicProvince = getString(cursor, "upload_pic_province");
		picTime = getString(cursor, "pic_time");
		picLocType = getInt(cursor, "pic_loc_type");
	}
	
}
