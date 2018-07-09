package com.pojul.fastIM.message.chat;

import com.google.gson.Gson;
import com.pojul.objectsocket.message.StringFile;

public class PicMessage extends ChatMessage{

    private StringFile pic;
    private int width;
    private int height;

    public StringFile getPic() {
        return pic;
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

    public void setPic(StringFile pic) {
        this.pic = pic;
    }

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

}
