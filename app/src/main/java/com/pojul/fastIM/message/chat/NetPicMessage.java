package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.StringFile;

/**
 * 网络搜索图片(如：百度，搜狗图片)，包含静态图片、动态图片
 * */
public class NetPicMessage extends ChatMessage{

    private String searchEngine;
    private StringFile thumbURL;
    private StringFile fullURL;
    private String picType;
    private int width;
    private int height;

    public NetPicMessage(String searchEngine) {
        this.searchEngine = searchEngine;
    }

	public String getSearchEngine() {
		return searchEngine;
	}

	public void setSearchEngine(String searchEngine) {
		this.searchEngine = searchEngine;
	}

	public StringFile getThumbURL() {
		return thumbURL;
	}

	public void setThumbURL(StringFile thumbURL) {
		this.thumbURL = thumbURL;
	}

	public StringFile getFullURL() {
		return fullURL;
	}

	public void setFullURL(StringFile fullURL) {
		this.fullURL = fullURL;
	}

	public String getPicType() {
		return picType;
	}

	public void setPicType(String picType) {
		this.picType = picType;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	@Override
	public String toString() {
		return "NetPicMessage [searchEngine=" + searchEngine + ", thumbURL=" + thumbURL + ", fullURL=" + fullURL
				+ ", picType=" + picType + ", width=" + width + ", height=" + height + ", chatType=" + chatType
				+ ", isRead=" + isRead + ", from=" + from + ", to=" + to + ", sendTime=" + sendTime + ", receiveTime="
				+ receiveTime + ", MessageUid=" + MessageUid + ", isSend=" + isSend + "]";
	}
	
}
