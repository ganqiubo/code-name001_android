package com.pojul.fastIM.message.chat;

import com.pojul.objectsocket.message.StringFile;

public class TextPicMessage extends ChatMessage{

	private String text;
	private StringFile pic;
	public TextPicMessage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TextPicMessage(String text, StringFile pic) {
		super();
		this.text = text;
		this.pic = pic;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public StringFile getPic() {
		return pic;
	}
	public void setPic(StringFile pic) {
		this.pic = pic;
	}
	@Override
	public String toString() {
		return "TextPicMessage [text=" + text + ", pic=" + pic + ", chatType=" + chatType + ", chatUid=" + chatUid
				+ ", from=" + from + ", to=" + to + ", sendTime=" + sendTime + ", receiveTime=" + receiveTime + "]";
	}
	
}
