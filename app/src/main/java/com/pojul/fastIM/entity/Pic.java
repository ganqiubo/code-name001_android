package com.pojul.fastIM.entity;

import android.database.Cursor;

import com.pojul.objectsocket.message.StringFile;

public class Pic extends BaseEntity{

	private Long id;
	private Long uploadPicId;
	private StringFile uploadPicUrl;
	private int isDelete= 1;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUploadPicId() {
		return uploadPicId;
	}
	public void setUploadPicId(Long uploadPicId) {
		this.uploadPicId = uploadPicId;
	}
	public StringFile getUploadPicUrl() {
		return uploadPicUrl;
	}
	public void setUploadPicUrl(StringFile uploadPicUrl) {
		this.uploadPicUrl = uploadPicUrl;
	}
	public int getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(int isDelete) {
		this.isDelete = isDelete;
	}

	@Override
	public void setBySql(Cursor cursor) {
		// TODO Auto-generated method stub
		super.setBySql(cursor);
		if(cursor == null) {
			return;
		}
		id = getLong(cursor, "id");
		uploadPicId = getLong(cursor, "upload_pic_id");
		uploadPicUrl = getStringFile(cursor, "upload_pic_url");
		isDelete = getInt(cursor, "is_delete");
	}
}
