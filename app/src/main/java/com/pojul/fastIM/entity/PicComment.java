package com.pojul.fastIM.entity;

import java.sql.ResultSet;
import java.util.List;

public class PicComment extends BaseEntity{

	private long id;
	private String uploadPicId;
	private String commentUserName;
	private String commentText;
	private int commentLevel;
	private long oneLevelId;
	private String gallery;
	private long thumpups;
	private String nickName;
	private String photo;
	private long timeMilli;
	private int hasThumbUp;
	private int sex;
	private List<PicComment> subComments;
	private int subCommentsNum;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUploadPicId() {
		return uploadPicId;
	}
	public void setUploadPicId(String uploadPicId) {
		this.uploadPicId = uploadPicId;
	}
	public String getCommentUserName() {
		return commentUserName;
	}
	public void setCommentUserName(String commentUserName) {
		this.commentUserName = commentUserName;
	}
	public String getCommentText() {
		return commentText;
	}
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	public int getCommentLevel() {
		return commentLevel;
	}
	public void setCommentLevel(int commentLevel) {
		this.commentLevel = commentLevel;
	}
	public long getOneLevelId() {
		return oneLevelId;
	}
	public void setOneLevelId(long oneLevelId) {
		this.oneLevelId = oneLevelId;
	}
	public String getGallery() {
		return gallery;
	}
	public void setGallery(String gallery) {
		this.gallery = gallery;
	}
	public long getThumpups() {
		return thumpups;
	}
	public void setThumpups(long thumpups) {
		this.thumpups = thumpups;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public long getTimeMilli() {
		return timeMilli;
	}
	public void setTimeMilli(long timeMilli) {
		this.timeMilli = timeMilli;
	}

	public int getHasThumbUp() {
		return hasThumbUp;
	}

	public void setHasThumbUp(int hasThumbUp) {
		this.hasThumbUp = hasThumbUp;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public List<PicComment> getSubComments() {
		return subComments;
	}

	public void setSubComments(List<PicComment> subComments) {
		this.subComments = subComments;
	}

	public int getSubCommentsNum() {
		return subCommentsNum;
	}

	public void setSubCommentsNum(int subCommentsNum) {
		this.subCommentsNum = subCommentsNum;
	}
}
